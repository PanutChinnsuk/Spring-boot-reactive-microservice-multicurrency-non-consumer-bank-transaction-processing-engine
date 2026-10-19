package com.panut.centralbanking.fxrateservice.application;

import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import com.panut.centralbanking.fxrateservice.infrastructure.repository.FxRateRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FxRateRepositoryManager {
    private final FxRateRepository fxRateRepository;

    public FxRateRepositoryManager(FxRateRepository fxRateRepository) {
        this.fxRateRepository = fxRateRepository;
    }


    public Mono<FxRate> save(final FxRate fxRate) {
        return this.fxRateRepository.save(fxRate);
    }
}
