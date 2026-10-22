package com.panut.centralbanking.transactionservice.application.conversion;

import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.core.FxDirection;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FxRateCurrencyConversion {


    public static BigDecimal convert(BigDecimal uncoverted, FxRateGetLatestResponse fxRate,
                                     FxDirection fxDirection) {
        return uncoverted.multiply(fxRate.getRate(fxDirection))
                .setScale(fxRate.getBaseCurrency().getCurrencyDigits(),
                        RoundingMode.HALF_EVEN);

    }
}
