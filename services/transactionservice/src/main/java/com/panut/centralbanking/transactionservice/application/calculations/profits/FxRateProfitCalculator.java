package com.panut.centralbanking.transactionservice.application.calculations.profits;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FxRateProfitCalculator {

    public static class V1 {
        public static BigDecimal foreignBuyBaseProfit(final BigDecimal unconvertedForiegnAmount,
                                                      final FxRateGetLatestResponse latestConversion) {
            CurrencyCode baseCurrency = latestConversion.getBaseCurrency();
            RoundingMode roundingMode = baseCurrency.getRoundingMode();
            int digits = baseCurrency.getCurrencyDigits();

            BigDecimal ask = latestConversion.getAskRate();
            BigDecimal bid = latestConversion.getBidRate();

            BigDecimal right = ask.subtract(bid);
            BigDecimal left = unconvertedForiegnAmount.multiply(right)
                    .setScale(digits, roundingMode);

            BigDecimal profit = left.divide(ask, digits, roundingMode);

            return profit;
        }
    }
}
