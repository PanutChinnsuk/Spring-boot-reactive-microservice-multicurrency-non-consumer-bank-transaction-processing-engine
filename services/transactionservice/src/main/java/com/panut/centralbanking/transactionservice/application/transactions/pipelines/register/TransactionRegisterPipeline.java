package com.panut.centralbanking.transactionservice.application.transactions.pipelines.register;

import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.validate.TransactionValidationPipeline;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionRegisterPipeline {
    private final TransactionValidationPipeline transactionValidationPipeline;
    private final LedgerAccountQuery ledgerAccountQuery;

    public TransactionRegisterPipeline(TransactionValidationPipeline transactionValidationPipeline,
                                       LedgerAccountQuery ledgerAccountQuery) {
        this.transactionValidationPipeline = transactionValidationPipeline;
        this.ledgerAccountQuery = ledgerAccountQuery;
    }




    public Mono<Void> register(final PreTransactionRegisterData registerData) {
        return ledgerAccountQuery.getLedgerAccountMapByCodes(registerData.getUsingLedgerCodes())
                .flatMap(ledgerAccounts -> {
                    return null;
                })
                .then();

    }
}
