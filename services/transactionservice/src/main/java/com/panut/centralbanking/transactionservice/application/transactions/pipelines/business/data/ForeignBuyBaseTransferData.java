package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.LedgerCode;
import lombok.Data;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;


@Getter
public class ForeignBuyBaseTransferData implements AbstractPipelineTransferData{
    private String fromAccountId;
    private BigDecimal foreignAmount;

    private String toAccountId;
    private String internalFxClearingAccountId;
    private String internalFxProfitAccountId;

    private final int fixedInternalSize = 2;
    private final int fixedNonInternalSize = 2;
    private final int fixedSize = fixedInternalSize + fixedNonInternalSize;
    private final LedgerCode fxLedgerCode = LedgerCode.FX_TRANSFER;

    public ForeignBuyBaseTransferData(String fromAccountId,
                                      String toAccountId,
                                      BigDecimal foreignAmount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.foreignAmount = foreignAmount;
    }


    public ForeignBuyBaseTransferData withInternalFxClearingAccountId(final String accountId) {
        this.internalFxClearingAccountId = accountId;

        return this;
    }

    public ForeignBuyBaseTransferData withInternalFxProfitAccountId(final String accountId) {
        this.internalFxProfitAccountId = accountId;

        return this;
    }

    @Override
    public Set<String> getLedgerCodeSet() {
        return Set.of(fxLedgerCode.getValue());
    }


    @Override
    public Mono<Void> validate() {
        return Mono.defer(() -> {
            Objects.requireNonNull(internalFxClearingAccountId);
            Objects.requireNonNull(internalFxProfitAccountId);
            Objects.requireNonNull(fromAccountId);
            Objects.requireNonNull(toAccountId);

            if (internalFxClearingAccountId.equals(internalFxProfitAccountId))
                throw new SystemMisused("internalFxClearingAccountId should not be the same as " +
                        "internalFxProfitAccountId.");

            if (fromAccountId.equals(toAccountId))
                throw new SystemMisused("fromAccountId should not be the same as " +
                        "toAccountId.");

            return Mono.empty();
        });
    }

    @Override
    public Set<String> asAccountIds() {
        return Set.of(fromAccountId, toAccountId, internalFxClearingAccountId,
                internalFxProfitAccountId);
    }
}
