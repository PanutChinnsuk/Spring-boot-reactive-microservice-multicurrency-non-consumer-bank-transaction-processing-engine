package com.panut.centralbanking.accountservice.api.service.transaction;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.appcore.client.Retrieves;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.servicemaps.map.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionClearingServiceController {
    private final WebClient transactionServiceWebClient;

    public TransactionClearingServiceController(WebClient transactionServiceWebClient) {
        this.transactionServiceWebClient = transactionServiceWebClient;
    }

    private String LOG_NAME = "TransactionClearingServiceController ";

    public Flux<BalanceChangeData> getBulkPending() {
        return Retrieves.doRequestFlux(transactionServiceWebClient,
                TransactionService.TRANSACTION_CLEAR_PENDING_BALANCE_FULL_PATH,
                BusinessException.class,
                LOG_NAME + "getBulkPending",
                BalanceChangeData.class);
    }
}


