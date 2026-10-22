package com.panut.centralbanking.fxrateservice.integration;


import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.fxrateservice.IntegrationDev;
import com.panut.centralbanking.fxrateservice.api.request.FxRateCreateRequest;
import com.panut.centralbanking.fxrateservice.api.request.FxRateGetLatestRequest;
import com.panut.centralbanking.fxrateservice.api.response.FxRateCreatedResponse;
import com.panut.centralbanking.fxrateservice.api.response.FxRateGetLatestResponse;
import com.panut.centralbanking.fxrateservice.application.service.FxRateService;
import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import com.panut.centralbanking.fxrateservice.infrastructure.repository.FxRateRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FxRateServiceTest implements IntegrationDev {
    @Autowired
    private FxRateService fxRateService;

    @Autowired
    private FxRateRepository fxRateRepository;


    private List<String> fxRateIds = new ArrayList<>();

    @AfterAll
    public void cleanUp() {
        fxRateIds.forEach(id ->
                fxRateRepository.deleteById(id).block());
    }

    public FxRateCreateRequest fxRateCreateRequest() {
        FxRateCreateRequest r = new FxRateCreateRequest();
        r.setBaseCurrency(CurrencyCode.getBaseCurrency());
        r.setQuoteCurrency(CurrencyCode.THB);
        r.setBidRate(BigDecimal.ONE);
        r.setAskRate(BigDecimal.TWO);
        r.setTreasuryRate(BigDecimal.TWO);



        return r;
    }

    private void addId(CommonResult<FxRateCreatedResponse> r) {
        fxRateIds.add(r.getOptionalBody().getFxRateId());
    }


    @Test
    @Order(1)
    public void shouldGetLatest() {
        FxRateCreateRequest r = fxRateCreateRequest();

        if (!fxRateRepository.existsByBaseCurrencyAndQuoteCurrency(r.getBaseCurrency(),
                r.getQuoteCurrency()).block()) {
            addId(fxRateService.createFxRate(r).block());
        }

        FxRateGetLatestResponse response = fxRateService
                .getLatest(new FxRateGetLatestRequest(r.getBaseCurrency(),
                        r.getQuoteCurrency())).block().getOptionalBody();
        Assertions.assertNotNull(response.getBidRate(), "bidrate");
        Assertions.assertNotNull(response.getAskRate(), "askrate");
        Assertions.assertNotNull(response.getMidRate(), "midrate");
        Assertions.assertNotNull(response.getTreasuryRate(), "treasuryrate");
    }

    @Test
    @Order(2)
    public void shouldConflict() {
        FxRateCreateRequest r = fxRateCreateRequest();

        Assertions.assertThrows(Conflicted.class, () -> {
            addId(fxRateService.createFxRate(r).block());
            addId(fxRateService.createFxRate(r).block());
        });
    }
}
