package com.panut.centralbanking.transactionservice.infrastructure.model.datacontent;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("transaction_overdraft_content")
@NoArgsConstructor
@Getter
public class TransactionOverdraftContent extends UUIDBasedStringEntity {
    @Column("transaction_id")
    private String transactionId;

    @Column("overdraft_id")
    private String overdraftId;

    @Builder
    public TransactionOverdraftContent(String transactionId,
                                       String overdraftId) {
        this.transactionId = transactionId;
        this.overdraftId = overdraftId;
        setInsert();
    }
}
