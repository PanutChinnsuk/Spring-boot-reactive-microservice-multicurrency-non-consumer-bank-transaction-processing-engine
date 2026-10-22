package com.panut.centralbanking.transactionservice.api.service.fxRate.request;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateGetLatestRequest {
    private CurrencyCode baseCurrency;
    private CurrencyCode quoteCurrency;



    public static FxRateGetLatestRequest withFxRateData(final FxRateData fxRateData) {
        return new FxRateGetLatestRequest(
                fxRateData.getBaseCurrency(),
                fxRateData.getQuoteCurrency()
        );
    }

}
