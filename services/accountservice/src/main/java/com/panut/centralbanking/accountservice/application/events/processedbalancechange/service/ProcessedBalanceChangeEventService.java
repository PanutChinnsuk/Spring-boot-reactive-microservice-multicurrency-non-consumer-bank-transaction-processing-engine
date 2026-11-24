package com.panut.centralbanking.accountservice.application.events.processedbalancechange.service;


import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface ProcessedBalanceChangeEventService {
    Mono<Set<String>> getAppliable(List<String> eventIds);
}
