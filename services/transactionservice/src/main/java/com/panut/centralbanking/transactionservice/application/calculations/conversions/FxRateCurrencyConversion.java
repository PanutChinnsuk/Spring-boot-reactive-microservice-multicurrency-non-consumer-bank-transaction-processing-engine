package com.panut.centralbanking.transactionservice.application.calculations.conversions;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.core.FxDirection;

import java.math.BigDecimal;

public class FxRateCurrencyConversion {



    public static class V1 {
        public static BigDecimal multiply(final BigDecimal any,
                                          final FxRateGetLatestResponse latestConversion,
                                          final FxDirection fxDirection,
                                          final CurrencyCode quoteCurrency) {
            return any.multiply(latestConversion.getRate(fxDirection))
                    .setScale(quoteCurrency.getCurrencyDigits(),
                            quoteCurrency.getRoundingMode());

        }

        public static BigDecimal divide(final BigDecimal any,
                                        final FxRateGetLatestResponse latestConversion,
                                        final FxDirection fxDirection) {
            CurrencyCode baseCurrency = latestConversion.getBaseCurrency();
            return any.divide(latestConversion.getRate(fxDirection), baseCurrency.getCurrencyDigits(),
                    baseCurrency.getRoundingMode());


        }


        public static BigDecimal convertMultiply(final BigDecimal any,
                                                 final FxRateGetLatestResponse latestConversion,
                                                 final FxRateData fxRateData) {

            return multiply(any, latestConversion, fxRateData.getFxDirection(),
                    fxRateData.getQuoteCurrency());
        }

        public static BigDecimal convertDivide(final BigDecimal any,
                                               final FxRateGetLatestResponse latestConversion,
                                               final FxRateData fxRateData) {
            return divide(any, latestConversion, fxRateData.getFxDirection());
        }
    }



}
