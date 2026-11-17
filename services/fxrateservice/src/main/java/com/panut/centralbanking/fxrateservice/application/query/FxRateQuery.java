package com.panut.centralbanking.fxrateservice.application.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import com.panut.centralbanking.fxrateservice.infrastructure.repository.FxRateRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FxRateQuery {
    private final FxRateRepository fxRateRepository;

    public FxRateQuery(FxRateRepository fxRateRepository) {
        this.fxRateRepository = fxRateRepository;
    }


    public Mono<FxRate> findLatest(final CurrencyCode base,
                                   final CurrencyCode quote) {
        return fxRateRepository.findLatest(base, quote)
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("Fx rate table does not contain a rate" +
                                " which has base currency of " + base + " and" +
                                " quote currency of " + quote)
                ));
    }

    public Mono<String> findExistLatest(final CurrencyCode base,
                                   final CurrencyCode quote) {
        return fxRateRepository.findExistLatest(base, quote);
    }

    public Mono<FxRate> findById(final String fxRateId) {
        return fxRateRepository.findById(fxRateId)
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("Fx rate table does not contain a rate " +
                                " which id was " + fxRateId)
                ));
    }
}
