package com.panut.centralbanking.accountservice.infrastructure.model;

import com.panut.centralbanking.accountservice.core.InternalAccountType;
import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("internal_accounts")
@Getter
@NoArgsConstructor
public class InternalAccount extends UUIDBasedStringEntity {
    @Column("account_id")
    private String accountId;

    @Column("internal_account_branch_id")
    private String branchId;

    @Column("internal_account_type")
    private InternalAccountType internalAccountType;


    @Builder
    public InternalAccount(String accountId,
                           String branchId,
                           InternalAccountType internalAccountType) {
        this.accountId = accountId;
        this.branchId = branchId;
        this.internalAccountType = internalAccountType;
        setInsert();
    }
}
