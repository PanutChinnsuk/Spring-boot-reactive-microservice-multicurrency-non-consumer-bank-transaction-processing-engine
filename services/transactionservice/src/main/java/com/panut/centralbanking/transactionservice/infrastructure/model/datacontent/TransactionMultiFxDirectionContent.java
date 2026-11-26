package com.panut.centralbanking.transactionservice.infrastructure.model.datacontent;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("transaction_multi_fx_direction_contents")
@NoArgsConstructor
@Getter
public class TransactionMultiFxDirectionContent extends UUIDBasedStringEntity {
    @Column("transaction_id")
    private String transactionId;

    @Column("transaction_line_id")
    private String transactionLineId;

    @Column("fx_direction")
    private FxDirection fxDirection;


    @Builder
    public TransactionMultiFxDirectionContent(String transactionLineId,
                                              FxDirection fxDirection) {
        this.transactionLineId = transactionLineId;
        this.fxDirection = fxDirection;
        setInsert();
    }


    public TransactionMultiFxDirectionContent withTransactionId(final String transactionId) {
        this.transactionId = transactionId;

        return this;
    }
}
