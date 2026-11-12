package com.panut.centralbanking.transactionservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("transaction_ledger_accounts")
@Getter
@NoArgsConstructor
public class LedgerAccount extends UUIDBasedStringEntity {
    @Column("ledger_type")
    private LedgerType ledgerType;

    @Column("code")
    private String code;

    @Column("name")
    private String name;

    @Builder
    public LedgerAccount(LedgerType ledgerType,
                         String code,
                         String name) {
        this.ledgerType = ledgerType;
        this.code = code;
        this.name = name;
        setInsert();
    }
}
