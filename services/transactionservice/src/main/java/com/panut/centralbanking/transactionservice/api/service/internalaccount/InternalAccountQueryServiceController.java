package com.panut.centralbanking.transactionservice.api.service.internalaccount;

import com.panut.centralbanking.appcore.client.Retrieves;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.servicemaps.map.InternalAccountsService;
import com.panut.centralbanking.transactionservice.api.service.internalaccount.response.GetFxInternalAccountResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class InternalAccountQueryServiceController {
    private final WebClient internalAccountServiceWebClient;

    public InternalAccountQueryServiceController(WebClient internalAccountServiceWebClient) {
        this.internalAccountServiceWebClient = internalAccountServiceWebClient;
    }

    private String LOG_NAME = "InteralAccountQueryService ";

    public Mono<GetFxInternalAccountResponse> getFxInternalAccounts() {
        return Retrieves.doRequest(internalAccountServiceWebClient,
                InternalAccountsService.GET_FX_INTERNAL_ACCOUNTS_FULL_PATH,
                BusinessException.class,
                LOG_NAME + "getFxInternalAccounts",
                GetFxInternalAccountResponse.class);
    }
}
