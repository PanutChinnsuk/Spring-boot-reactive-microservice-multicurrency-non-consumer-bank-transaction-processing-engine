package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.TransferPipeline;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateMultiGetResponse;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Getter
public class TransferMultiGetPipelineChain {
    private FxRateMultiGetResponse fxRateMultiGetResponse;
    private Map<String, String> ledgerCodeMap;
    private GetAccountsInfoForTransactionResponse getAccountsInfoForTransactionResponse;

    private FxRateGetResponse possibleSingleFxRate;


    public FxRateGetResponse getTargetFxRate(final CustomAccountTransferData data) {
        String ofAccountId = null;

        if (data.hasFxContext()) {
            ofAccountId = data.getFxContext().getTargetFxAccountCurrencyId();
            log.info("data hasFxContext, ofAccountId value: {}", ofAccountId);
        }


        if (ofAccountId == null && possibleSingleFxRate == null) {
            log.info("ofAccountId and possibleSingleFxRate are null");
            return null;
        }


        if (possibleSingleFxRate != null) {
            log.info("possibleSingleFxRate found due to single size fx rate map");
            return possibleSingleFxRate;
        }





        var account = getAccountsInfoForTransactionResponse
                .getAccount(ofAccountId);
        var accountCurrency = account.getAccountCurrency();



        return getMyFxRate(accountCurrency);
    }

    public FxRateGetResponse getMyFxRate(final CurrencyCode accountCurrency) {
        var fxRate = fxRateMultiGetResponse.getFxRateMap()
                .get(accountCurrency);

        log.info("gets fx rate base of quote currency: {}", accountCurrency);

        return fxRate;
    }

    public boolean hasFxDirection() {
        return fxRateMultiGetResponse != null
                && fxRateMultiGetResponse.getFxRateMap() != null
                && !fxRateMultiGetResponse.getFxRateMap().isEmpty();
    }


    public Mono<TransferMultiGetPipelineChain> fxRateMultiGetResponse(final FxRateMultiGetResponse fxRateMultiGetResponse) {
        return Mono.defer(() -> {
            Objects.requireNonNull(fxRateMultiGetResponse.getFxRateMap());

            if (getAccountsInfoForTransactionResponse == null)
                return Mono.error(
                        () -> new SystemMisused(
                                "getAccountsInfoForTransactionResponse required before fxRateMultiGetResponse"
                        )
                );

            this.fxRateMultiGetResponse = fxRateMultiGetResponse;

            var map = fxRateMultiGetResponse.getFxRateMap();
            log.info("fxRateMulti map size: {}, quotes: {}", map.size(),
                    map.keySet());

            if (map.size() == 1) {
                possibleSingleFxRate = map
                        .values().stream().findFirst().get();

                log.info("mutilGet fx rate response size is 1, mapping possibleSingleFxRate, null? {}",
                        possibleSingleFxRate == null);
            }

            return Mono.just(this);
        });
    }

    public Mono<TransferMultiGetPipelineChain> ledgerCodeMap(final Map<String, String> ledgerCodeMap) {
        return Mono.defer(() -> {
            this.ledgerCodeMap = ledgerCodeMap;

            return Mono.just(this);
        });
    }



    public Mono<TransferMultiGetPipelineChain> accountsInfoForTransactionResponse
            (final GetAccountsInfoForTransactionResponse getAccountsInfoForTransactionResponse) {
        return Mono.defer(() -> {
            this.getAccountsInfoForTransactionResponse = getAccountsInfoForTransactionResponse;

            return Mono.just(this);
        });
    }
}
