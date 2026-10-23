package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business;

import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountsInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.PipelineTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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


    public Mono<TransactionCreationPipelineResult> transfer(final PipelineTransferData data,
                                                            final TransactionType transactionType) {
        return data.validate()
                .thenReturn(data.asAccountIds())
                .flatMap(accountIds -> accountServiceController
                        .getAccountsInfoForTransaction(new GetAccountsInfoForTransactionRequest(
                                accountIds))
                )
                .map(response ->
                        response.compact()
                                .expectedSize(data.getFixedSize())
                                .expectedAllAccountStatusActive()
                                .expectedSameCurrency())
                .flatMap(response -> {
                    return ledgerAccountQuery.getLedgerAccountMapByCodes(data.getLedgerCodeSet())
                            .map(ledgerAccounts -> {
                                var fromAccountInfo = response.getMapValue(data.getFromAccountId());
                                var toAccountInfo = response.getMapValue(data.getToAccountId());

                                BigDecimal basedAmount = data.getAmount();
                                BigDecimal convertedAmount = basedAmount;
                                String fromAccountLedgerId =
                                        ledgerAccounts.get(data.getFromAccountLedgerCode().getValue());
                                String toAccountLedgerId =
                                        ledgerAccounts.get(data.getToAccountLedgerCode().getValue());

                                return new PreTransactionRegisterData(
                                        transactionType,
                                        List.of(
                                                new AccountData(fromAccountInfo.getId(),
                                                        basedAmount,
                                                        convertedAmount,
                                                        fromAccountLedgerId,
                                                        TransactionLineDirection.DEBIT),
                                                new AccountData(toAccountInfo.getId(),
                                                        basedAmount,
                                                        convertedAmount,
                                                        toAccountLedgerId,
                                                        TransactionLineDirection.CREDIT)
                                        ),
                                        FxRateData.empty());
                            });
                })
                .flatMap(transactionRegisterPipeline::register);
    }
}
