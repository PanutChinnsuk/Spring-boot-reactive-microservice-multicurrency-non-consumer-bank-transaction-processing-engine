package com.panut.centralbanking.transactionservice.api.controller.service;

import com.panut.centralbanking.appcore.servicemaps.InternalServiceMapping;
import com.panut.centralbanking.appcore.servicemaps.map.TransactionService;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsFlux;
import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.service.TransactionClearingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(InternalServiceMapping.API)
public class TransactionClearingServiceController {
    private final TransactionClearingService transactionClearingService;

    public TransactionClearingServiceController(TransactionClearingService transactionClearingService) {
        this.transactionClearingService = transactionClearingService;
    }


    @ServiceHasAuthorityExpectsFlux(TransactionService.TRANSACTION_CLEAR_PENDING_BALANCE_METHOD)
    @PostMapping(TransactionService.TRANSACTION_CLEAR_PENDING_BALANCE_METHOD)
    public Flux<BalanceChangeData> getBulkPending() {
        return transactionClearingService.getBulkPending();
    }
}
