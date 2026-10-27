package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.ExpectedLedgerTypes;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.PipelineTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


record PreListAccountDatasInfo(AccountInfoForTransactionQueryResult fromAccountInfo,
                               AccountInfoForTransactionQueryResult toAccountInfo,
                               BigDecimal basedAmount,
                               String fromAccountLedgerId,
                               String toAccountLedgerId) {}

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class TransferPipeline {
    private final BusinessPipelineRepository repository;
    private final TransactionRegisterPipeline transactionRegisterPipeline;
    private final AccountServiceController accountServiceController;
    private final LedgerAccountQuery ledgerAccountQuery;

    public TransferPipeline(BusinessPipelineRepository repository,
                            TransactionRegisterPipeline transactionRegisterPipeline,
                            AccountServiceController accountServiceController,
                            LedgerAccountQuery ledgerAccountQuery) {
        this.repository = repository;
        this.transactionRegisterPipeline = transactionRegisterPipeline;
        this.accountServiceController = accountServiceController;
        this.ledgerAccountQuery = ledgerAccountQuery;
    }

    private Set<LedgerType> getMatchedLedgerTypes(final TransactionType transactionType) {
        return switch (transactionType) {
            case TRANSFER -> ExpectedLedgerTypes.NORMAL_TRANSFER();
            case ASSET_EXPENSE -> ExpectedLedgerTypes.ASSET_AND_EXPENSE();
            default -> throw new SystemMisused("TransferPipeline only allowed transaction type " +
                    "of TRANSFER, ASSET_EXPENSE");
        };
    }

    private List<AccountData> normalTransfer(PreListAccountDatasInfo info) {
        return List.of(
                AccountData.linearDebit(info.fromAccountInfo(), info.basedAmount(),
                        info.fromAccountLedgerId()),
                AccountData.linearCredit(info.toAccountInfo(), info.basedAmount(),
                        info.toAccountLedgerId()));
    }

    private List<AccountData> assetExpenseTransfer(PreListAccountDatasInfo info) {
        return List.of(
                AccountData.linearCredit(info.fromAccountInfo(), info.basedAmount(),
                        info.fromAccountLedgerId()),
                AccountData.linearDebit(info.toAccountInfo(), info.basedAmount(),
                        info.toAccountLedgerId()));
    }



    protected Mono<PreTransactionRegisterData> defaultTransferChain(final GetAccountsInfoForTransactionResponse response,
                                                                 final PipelineTransferData data,
                                                                 final TransactionType transactionType) {
        return ledgerAccountQuery.getLedgerAccountMapByCodes(data.getLedgerCodeSet(),
                        getMatchedLedgerTypes(transactionType))
                .map(ledgerAccounts -> {
                    var fromAccountInfo = response.getMapValue(data.getFromAccountId());
                    var toAccountInfo = response.getMapValue(data.getToAccountId());

                    PreListAccountDatasInfo info = new PreListAccountDatasInfo(
                            response.getMapValue(data.getFromAccountId()),
                            response.getMapValue(data.getToAccountId()),
                            data.getAmount(),
                            ledgerAccounts.get(data.getFromAccountLedgerCode().getValue()),
                            ledgerAccounts.get(data.getToAccountLedgerCode().getValue())
                    );

                    return new PreTransactionRegisterData(
                            transactionType,
                            transactionType.isAssetExpense() ? assetExpenseTransfer(info)
                                                             : normalTransfer(info),
                            FxRateData.empty());
                });
    }


    public Mono<TransactionCreationPipelineResult> transfer(final PipelineTransferData data,
                                                            final TransactionType transactionType) {
        return repository.getAccountsInfoForTransaction(data)
                .flatMap(response ->
                        defaultTransferChain(response, data, transactionType))
                .flatMap(transactionRegisterPipeline::register);
    }
}
