package com.panut.centralbanking.fxrateservice.application.implement;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.fxrateservice.api.request.FxRateCreateRequest;
import com.panut.centralbanking.fxrateservice.api.request.FxRateGetLatestRequest;
import com.panut.centralbanking.fxrateservice.api.response.FxRateCreatedResponse;
import com.panut.centralbanking.fxrateservice.api.response.FxRateGetLatestResponse;
import com.panut.centralbanking.fxrateservice.application.FxRateRepositoryManager;
import com.panut.centralbanking.fxrateservice.application.query.FxRateQuery;
import com.panut.centralbanking.fxrateservice.application.service.FxRateService;
import com.panut.centralbanking.fxrateservice.application.validate.FxRatePersistValidator;
import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class FxRateServiceImpl implements FxRateService {
    private final FxRateRepositoryManager manager;
    private final FxRateQuery fxRateQuery;
    private final FxRatePersistValidator fxRatePersistValidator;

    public FxRateServiceImpl(FxRateRepositoryManager manager,
                             FxRateQuery fxRateQuery,
                             FxRatePersistValidator fxRatePersistValidator) {
        this.manager = manager;
        this.fxRateQuery = fxRateQuery;
        this.fxRatePersistValidator = fxRatePersistValidator;
    }

    @Override
    public Mono<CommonResult<FxRateCreatedResponse>> createFxRate(final FxRateCreateRequest r) {
        return fxRateQuery.findExistLatest(r.getBaseCurrency(), r.getQuoteCurrency())
                .doOnNext(a -> log.info("Find existing latest with lock to " +
                        "avoid concurrent issue."))
                .thenReturn(FxRate.builder()
                                .bidRate(r.getBidRate())
                                .askRate(r.getAskRate())
                                .treasuryRate(r.getTreasuryRate())
                                .baseCurrency(r.getBaseCurrency())
                                .quoteCurrency(r.getQuoteCurrency())
                                .build())
                .doOnNext(a -> log.info("Created unsaved FxRate."))
                .flatMap(fxRate -> fxRatePersistValidator
                        .safeAdd(fxRate)
                        .thenReturn(fxRate))
                .doOnNext(a -> log.info("Validate safe add."))
                .flatMap(fxRate -> manager.save(fxRate)
                        .thenReturn(FxRateCreatedResponse.toResponse(fxRate)))
                .doOnNext(a -> log.info("Saved and map to response."))
                .map(response -> new CommonResult<>(
                        "Successfully created fx rate.",
                        response
                ))
                .doOnSuccess(a ->
                        log.info("Successfully created fx rate."))
                .doOnError(error -> log.error(
                        "Failed to create fx rate, {}", error.getLocalizedMessage()));
    }

    @Override
    public Mono<CommonResult<FxRateGetLatestResponse>> getLatest(final FxRateGetLatestRequest r) {
        return fxRateQuery
                .findLatest(r.getBaseCurrency(),
                        r.getQuoteCurrency())
                .map(FxRateGetLatestResponse::toResponse)
                .map(response -> new CommonResult<FxRateGetLatestResponse>(response))
                .doOnSuccess(a ->
                        log.info("Successfully get latest fx rate."))
                .doOnError(error ->
                        log.error("Failed to get latest fx rate, {}",
                                error.getLocalizedMessage()));
    }


}
