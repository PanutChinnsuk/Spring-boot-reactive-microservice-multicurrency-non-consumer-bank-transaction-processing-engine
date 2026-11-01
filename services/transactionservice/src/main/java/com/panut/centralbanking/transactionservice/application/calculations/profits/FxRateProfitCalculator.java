package com.panut.centralbanking.transactionservice.application.calculations.profits;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.calculations.conversions.FxRateCurrencyConversion;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FxRateProfitCalculator {

    public static class V1 {
        public static BigDecimal foreignBuyBaseProfit(final BigDecimal unconvertedForiegnAmount,
                                                      final FxRateGetLatestResponse latestConversion,
                                                      final FxRateData fxRateData) {
            CurrencyCode baseCurrency = latestConversion.getBaseCurrency();
            RoundingMode roundingMode = baseCurrency.getRoundingMode();
            int digits = baseCurrency.getCurrencyDigits();

            BigDecimal ask = latestConversion.getAskRate();
            BigDecimal bid = latestConversion.getBidRate();

            BigDecimal right = ask.subtract(bid);
            BigDecimal left = unconvertedForiegnAmount.multiply(right)
                    .setScale(digits, roundingMode);

            BigDecimal foreignLeftOver = left.divide(ask, digits, roundingMode);
            BigDecimal profit = FxRateCurrencyConversion
                    .V1.convertDivide(foreignLeftOver,
                            latestConversion, fxRateData);

            if (profit.signum() <= 0)
                throw new BusinessException("Transfering foreign amount is too low.");

            return profit;
        }
    }
}
