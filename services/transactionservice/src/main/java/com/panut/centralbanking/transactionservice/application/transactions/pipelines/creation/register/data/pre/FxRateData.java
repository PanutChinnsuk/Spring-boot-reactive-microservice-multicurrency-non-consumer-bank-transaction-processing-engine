package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class FxRateData {
    private String fxRateId;
    private final FxDirection fxDirection;
    private final CurrencyCode baseCurrency;
    private final CurrencyCode quoteCurrency;

    public FxRateData(FxDirection fxDirection,
                      CurrencyCode baseCurrency,
                      CurrencyCode quoteCurrency) {
        this.fxDirection = fxDirection;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }

    private static CurrencyCode getMainCurrency() {
        return CurrencyCode.getBaseCurrency();
    }


    public static FxRateData empty() {
        return new FxRateData(FxDirection.NONE,
                null,
                null)
                .withId(null);
    }


    public static FxRateData quote(final CurrencyCode quoteCurrency,
                            final FxDirection fxDirection) {
        return new FxRateData(fxDirection,
                getMainCurrency(),
                quoteCurrency);
    }

    public static FxRateData quoteBid(final CurrencyCode quoteCurrency) {
        return quote(quoteCurrency, FxDirection.BID);
    }

    public static FxRateData quoteAsk(final CurrencyCode quoteCurrency) {
        return quote(quoteCurrency, FxDirection.ASK);
    }

    public static FxRateData quoteMid(final CurrencyCode quoteCurrency) {
        return quote(quoteCurrency, FxDirection.MID);
    }

    public static FxRateData quoteTreasury(final CurrencyCode quoteCurrency) {
        return quote(quoteCurrency, FxDirection.TREASURY);
    }

    public boolean isEmpty() {
        return this.fxDirection.equals(FxDirection.NONE);
    }


    public FxRateData withId(final String fxRateId) {
        if (this.fxRateId != null)
            throw new SystemMisused("FxRateData fxRateId can only be set once.");

        this.fxRateId = fxRateId;
        return this;
    }

    public CurrencyCode getQuoteCurrency() {
        return quoteCurrency;
    }
}
