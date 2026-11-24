package com.panut.centralbanking.transactionservice.infrastructure.model.datacontent;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("transaction_multi_fx_contents")
@NoArgsConstructor
@Getter
public class TransactionMultiFxContent extends UUIDBasedStringEntity {
    @Column("transaction_id")
    private String transactionId;

    @Column("fx_rate_id")
    private String fxRateId;



    @Builder
    public TransactionMultiFxContent(String fxRateId) {
        this.fxRateId = fxRateId;

        setInsert();
    }

    public TransactionMultiFxContent withTransactionId(final String transactionId) {
        this.transactionId = transactionId;

        return this;
    }





}
