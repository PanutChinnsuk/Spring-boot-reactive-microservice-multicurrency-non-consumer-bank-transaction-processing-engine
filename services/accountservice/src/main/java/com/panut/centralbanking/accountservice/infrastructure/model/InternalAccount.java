package com.panut.centralbanking.accountservice.infrastructure.model;

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



    @Builder
    public InternalAccount(String accountId) {
        setInsert(accountId);
    }
}
