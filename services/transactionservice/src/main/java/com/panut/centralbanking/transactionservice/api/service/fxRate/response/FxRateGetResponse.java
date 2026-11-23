package com.panut.centralbanking.transactionservice.api.service.fxRate.response;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateGetResponse {
    private String fxRateId;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private BigDecimal treasuryRate;
    private CurrencyCode baseCurrency;
    private CurrencyCode quoteCurrency;


    public BigDecimal getRate(final FxDirection fxDirection) {
        return switch (fxDirection) {
            case ASK -> this.askRate;
            case BID -> this.bidRate;
            case MID -> this.midRate;
            case TREASURY -> this.treasuryRate;
            case NONE -> BigDecimal.ONE;
        };
    }



}
