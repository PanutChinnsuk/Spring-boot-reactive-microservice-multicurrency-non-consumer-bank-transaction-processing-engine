package com.panut.centralbanking.accountservice.infrastructure.model;

import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("internal_account_branches")
@Getter
@NoArgsConstructor
public class InternalAccountBranch extends UUIDBasedStringEntity {
    @Column("branch")
    private InternalBranch internalBranch;

    @Builder
    public InternalAccountBranch(InternalBranch internalBranch) {
        this.internalBranch = internalBranch;
        setInsert();
    }
}
