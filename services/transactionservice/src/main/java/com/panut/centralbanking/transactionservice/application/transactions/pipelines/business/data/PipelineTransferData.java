package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.LedgerCode;
import lombok.Data;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;



@Getter
public class PipelineTransferData implements AbstractPipelineTransferData{
    private final String fromAccountId;
    private final String toAccountId;
    private final BigDecimal amount;
    private final int fixedSize = 2;


    private LedgerCode fromAccountLedgerCode;
    private LedgerCode toAccountLedgerCode;

    private Set<String> accountIdSet = new HashSet<>();

    public PipelineTransferData(String fromAccountId,
                                String toAccountId,
                                BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public PipelineTransferData withFromAccountLedgerCode(final LedgerCode ledgerCode) {
        this.fromAccountLedgerCode = ledgerCode;
        return this;
    }

    public PipelineTransferData withToAccountLedgerCode(final LedgerCode ledgerCode) {
        this.toAccountLedgerCode = ledgerCode;
        return this;
    }

    public PipelineTransferData withSingleLedgerCode(final LedgerCode ledgerCode) {
        this.fromAccountLedgerCode = ledgerCode;
        this.toAccountLedgerCode = ledgerCode;

        return this;
    }

    @Override
    public Mono<Void> validate() {
        return Mono.defer(() -> {
            Objects.requireNonNull(fromAccountLedgerCode);
            Objects.requireNonNull(toAccountLedgerCode);
            if (fromAccountId.equals(toAccountId))
                return Mono.error(() -> new BusinessException("Two account entries for PipelineTransferData " +
                        "can't be the same."));


            return Mono.empty();
        });
    }

    public Set<String> getLedgerCodeSet() {
        return fromAccountLedgerCode.equals(toAccountLedgerCode)
                ? Set.of(fromAccountLedgerCode.getValue())
                : Set.of(fromAccountLedgerCode.getValue(), toAccountLedgerCode.getValue());
    }

    @Override
    public Set<String> asAccountIds() {
        this.accountIdSet.add(this.fromAccountId);
        this.accountIdSet.add(this.toAccountId);


        return this.accountIdSet;
    }

    public boolean includeAccountId(final String accountId) {
        return accountIdSet.add(accountId);
    }
}
