package com.panut.centralbanking.transactionservice.api.service.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateGetLatestResponse {
    private String fxRateId;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private BigDecimal treasuryRate;
    private CurrencyCode baseCurrency;


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
