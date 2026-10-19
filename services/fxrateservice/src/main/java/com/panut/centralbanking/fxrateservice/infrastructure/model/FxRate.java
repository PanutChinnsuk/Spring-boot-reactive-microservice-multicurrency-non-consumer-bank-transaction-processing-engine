package com.panut.centralbanking.fxrateservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.fxrateservice.api.response.FxRateGetLatestResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Table("fx_rates")
@Getter
@NoArgsConstructor
public class FxRate extends UUIDBasedStringEntity {
    @Column("base_currency")
    private CurrencyCode baseCurrency;

    @Column("quote_currency")
    private CurrencyCode quoteCurrency;

    @Column("bid_rate")
    private BigDecimal bidRate;

    @Column("ask_rate")
    private BigDecimal askRate;

    @Column("mid_rate")
    private BigDecimal midRate;

    @Column("treasury_rate")
    private BigDecimal treasuryRate;

    @Column("timestamp")
    private LocalDateTime timestamp;


    @Builder
    public FxRate(CurrencyCode baseCurrency,
                  CurrencyCode quoteCurrency,
                  BigDecimal bidRate,
                  BigDecimal askRate,
                  BigDecimal treasuryRate) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.bidRate = bidRate;
        this.askRate = askRate;
        this.midRate = (bidRate.add(askRate)).divide(BigDecimal.TWO,
                baseCurrency.getCurrencyDigits(),
                RoundingMode.HALF_EVEN);
        this.treasuryRate = treasuryRate;
        this.timestamp = LocalDateTime.now();
        setInsert();
    }


}
