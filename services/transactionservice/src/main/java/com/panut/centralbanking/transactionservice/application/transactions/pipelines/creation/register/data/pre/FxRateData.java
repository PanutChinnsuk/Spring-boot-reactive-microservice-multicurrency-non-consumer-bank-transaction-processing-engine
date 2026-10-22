package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import lombok.Getter;

@Getter
public class FxRateData {
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

    private CurrencyCode getMainCurrency() {
        return CurrencyCode.getBaseCurrency();
    }


    public FxRateData empty() {
        return new FxRateData(FxDirection.NONE,
                null,
                null);
    }


    public FxRateData quote(final CurrencyCode quoteCurrency,
                            final FxDirection fxDirection) {
        return new FxRateData(fxDirection,
                getMainCurrency(),
                quoteCurrency);
    }

    public FxRateData quoteBid(final CurrencyCode quoteCurrency) {
        return quote(quoteCurrency, FxDirection.BID);
    }

    public FxRateData quoteAsk(final CurrencyCode quoteCurrency) {
        return quote(quoteCurrency, FxDirection.ASK);
    }

    public FxRateData quoteMid(final CurrencyCode quoteCurrency) {
        return quote(quoteCurrency, FxDirection.MID);
    }

    public FxRateData quoteTreasury(final CurrencyCode quoteCurrency) {
        return quote(quoteCurrency, FxDirection.TREASURY);
    }

    public boolean isEmpty() {
        return this.fxDirection.equals(FxDirection.NONE);
    }
}
