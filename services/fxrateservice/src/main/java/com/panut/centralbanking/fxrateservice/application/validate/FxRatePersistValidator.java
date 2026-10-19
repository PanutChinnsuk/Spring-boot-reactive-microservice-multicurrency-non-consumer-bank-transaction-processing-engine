package com.panut.centralbanking.fxrateservice.application.validate;

import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.fxrateservice.application.property.FxRateProperty;
import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import com.panut.centralbanking.fxrateservice.infrastructure.repository.FxRateRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class FxRatePersistValidator {
    private final FxRateRepository fxRateRepository;
    private final FxRateProperty fxRateProperty;

    public FxRatePersistValidator(FxRateRepository fxRateRepository,
                                  FxRateProperty fxRateProperty) {
        this.fxRateRepository = fxRateRepository;
        this.fxRateProperty = fxRateProperty;
    }


    public Mono<FxRate> safeAdd(final FxRate fxRate) {
        return fxRateRepository.findConflict(fxRate.getBaseCurrency(),
                fxRate.getQuoteCurrency(),
                fxRate.getTimestamp()
                        .minus(Duration.ofSeconds(fxRateProperty
                                .getAddingSafeGuardRangeSeconds())),
                fxRate.getTimestamp()
                        .plus(Duration.ofSeconds(fxRateProperty.
                                getAddingSafeGuardRangeSeconds())),
                fxRate.getId())
                .flatMap(exist -> Mono.error(
                        () -> new Conflicted("Same base and quote fx rates required seconds" +
                                " of timestamp gap to insert.")))
                .thenReturn(fxRate);
    }
}
