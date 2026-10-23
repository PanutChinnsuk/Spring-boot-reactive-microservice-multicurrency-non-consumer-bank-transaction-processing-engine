package com.panut.centralbanking.transactionservice.application.conversion;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
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


    public BigDecimal getConvertedAmount(final PreTransactionRegisterData registerData,
                                          FxRateGetLatestResponse fxRate,
                                          final AccountData accountData) {
        boolean isUsingFxRate = !registerData.getFxRateData().isEmpty();
        if (isUsingFxRate) {
            throw new SystemMisused("Transaction is required fx rate but register chain does not" +
                    " contain fx rate.");
        }

        return isUsingFxRate
                ? FxRateCurrencyConversion.convert(accountData.getAmount(),
                fxRate, registerData.getFxRateData().getFxDirection())
                : accountData.getAmount();
    }
}
