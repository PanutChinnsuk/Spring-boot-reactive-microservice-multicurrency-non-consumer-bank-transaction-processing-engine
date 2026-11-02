package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.LedgerCode;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Set;

@Getter
public class BaseBuyForeignTransferData implements AbstractPipelineTransferData{
    private String fromAccountId;
    private BigDecimal baseAmount;

    private String toAccountId;

    private String internalCollectorAccountId;
    private String internalClearingAccountId;


    private final int nonInternalSize = 2;
    private final int internalSize = 2;
    private final int fixedSize = nonInternalSize + internalSize;
    private final LedgerCode ledgerCode = LedgerCode.FX_TRANSFER;

    public BaseBuyForeignTransferData(String fromAccountId,
                                      String toAccountId,
                                      BigDecimal baseAmount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.baseAmount = baseAmount;
    }


    public BaseBuyForeignTransferData withInternalCollectorAccountId(
            final String internalCollectorAccountId
    ) {
        this.internalCollectorAccountId = internalCollectorAccountId;

        return this;
    }

    public BaseBuyForeignTransferData withInternalClearingAccountId(
            final String internalClearingAccountId
    ) {
        this.internalClearingAccountId = internalClearingAccountId;

        return this;
    }

    @Override
    public Mono<Void> validate() {
        return Mono.defer(() -> {
            if (fromAccountId.equals(toAccountId))
                return Mono.error(
                        () -> new BusinessException("fromAccountId can't be the same as toAccountId."));

            if (internalClearingAccountId.equals(internalCollectorAccountId))
                return Mono.error(
                        () -> new BusinessException("internalClearingAccountId can't be the same as " +
                                "internalCollectorAccountId."));

            return Mono.empty();
        });
    }



    @Override
    public Set<String> asAccountIds() {
        return Set.of(fromAccountId, toAccountId, internalCollectorAccountId);
    }

    @Override
    public Set<String> getLedgerCodeSet() {
        return Set.of(ledgerCode.getValue());
    }
}
