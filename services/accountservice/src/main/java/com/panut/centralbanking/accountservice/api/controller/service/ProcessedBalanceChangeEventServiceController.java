package com.panut.centralbanking.accountservice.api.controller.service;

import com.panut.centralbanking.accountservice.api.request.serviceonly.transactioneventclearing.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.accountservice.application.events.balancechange.service.ProcessedBalanceChangeEventService;
import com.panut.centralbanking.appcore.servicemaps.InternalServiceMapping;
import com.panut.centralbanking.appcore.servicemaps.map.AccountService;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsMono;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(InternalServiceMapping.API)
public class ProcessedBalanceChangeEventServiceController {
    private final ProcessedBalanceChangeEventService processedBalanceChangeEventService;

    public ProcessedBalanceChangeEventServiceController(ProcessedBalanceChangeEventService processedBalanceChangeEventService) {
        this.processedBalanceChangeEventService = processedBalanceChangeEventService;
    }



    @ServiceHasAuthorityExpectsMono(AccountService.GET_PROCESSED_BALANCE_CHANGE_EVENT_METHOD)
    @PostMapping(AccountService.GET_PROCESSED_BALANCE_CHANGE_EVENT_METHOD)
    public Mono<Set<String>> getAppliable(@RequestBody @Valid
                                           GetProcessedBalanceEventIdsRequest r) {
        return processedBalanceChangeEventService.getAppliable(r.getPendingEventIds());
    }
}
