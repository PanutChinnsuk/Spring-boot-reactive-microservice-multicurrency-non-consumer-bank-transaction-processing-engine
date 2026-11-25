package com.panut.centralbanking.transactionservice.infrastructure.model.datacontent;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
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

    @Column("account_id")
    private String accountId;


    @Builder
    public TransactionMultiFxDirectionContent(String transactionId,
                                              String accountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
    }
}
