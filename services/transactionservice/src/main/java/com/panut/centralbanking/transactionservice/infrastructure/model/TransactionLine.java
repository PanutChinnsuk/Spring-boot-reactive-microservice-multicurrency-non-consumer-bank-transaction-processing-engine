package com.panut.centralbanking.transactionservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("transaction_lines")
@NoArgsConstructor
@Getter
public class TransactionLine extends UUIDBasedStringEntity {
    @Column("transaction_id")
    private String transactionId;

    @Column("account_id")
    private String accountId;

    @Column("direction")
    private TransactionLineDirection direction;

    @Column("based_amount")
    private BigDecimal bigDecimal;

    @Column("converted_amount")
    private BigDecimal convertedAmount;

    @Column("ledger_account_id")
    private String ledgerAccountId;

    @Builder
    public TransactionLine(String transactionId,
                           String accountId,
                           TransactionLineDirection direction,
                           BigDecimal bigDecimal,
                           BigDecimal convertedAmount,
                           String ledgerAccountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.direction = direction;
        this.bigDecimal = bigDecimal;
        this.convertedAmount = convertedAmount;
        this.ledgerAccountId = ledgerAccountId;
        setInsert();
    }


}
