package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.TransferPipeline.v1;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.ExpectedLedgerTypes;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.PipelineTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class TransferPipelineModuleV1 {
    private final LedgerAccountQuery ledgerAccountQuery;

    public TransferPipelineModuleV1(LedgerAccountQuery ledgerAccountQuery) {
        this.ledgerAccountQuery = ledgerAccountQuery;
    }



    private Set<LedgerType> getExpectedLedgerTypes(final TransactionType transactionType) {
        return switch (transactionType) {
            case TRANSFER -> ExpectedLedgerTypes.NORMAL_TRANSFER();
            case ASSET_EXPENSE -> ExpectedLedgerTypes.ASSET_AND_EXPENSE();
            default -> throw new SystemMisused("TransferPipeline only allowed transaction type " +
                    "of TRANSFER, ASSET_EXPENSE");
        };
    }

    private List<AccountData> normalTransfer(PreListAccountDatasInfo info) {
        return List.of(
                AccountData.V1.linearDebit(info.fromAccountInfo(), info.basedAmount(),
                        info.fromAccountLedgerId()),
                AccountData.V1.linearCredit(info.toAccountInfo(), info.basedAmount(),
                        info.toAccountLedgerId()));
    }

    private List<AccountData> assetExpenseTransfer(PreListAccountDatasInfo info) {
        return List.of(
                AccountData.V1.linearCredit(info.fromAccountInfo(), info.basedAmount(),
                        info.fromAccountLedgerId()),
                AccountData.V1.linearDebit(info.toAccountInfo(), info.basedAmount(),
                        info.toAccountLedgerId()));
    }



    public Mono<PreTransactionRegisterData> defaultTransferChain(final GetAccountsInfoForTransactionResponse response,
                                                                    final PipelineTransferData data,
                                                                    final TransactionType transactionType) {
        return ledgerAccountQuery.getLedgerAccountMapByCodes(data.getLedgerCodeSet(),
                        getExpectedLedgerTypes(transactionType))
                .doOnNext(a -> log.info(
                        "getLedgerAccountMapByCodes"))
                .map(ledgerAccounts -> {
                    PreListAccountDatasInfo info = new PreListAccountDatasInfo(
                            response.getAccount(data.getFromAccountId()),
                            response.getAccount(data.getToAccountId()),
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


}
