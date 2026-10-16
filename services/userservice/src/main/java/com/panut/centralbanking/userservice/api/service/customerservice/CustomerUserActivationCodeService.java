package com.panut.centralbanking.userservice.api.service.customerservice;

import com.panut.centralbanking.appcore.client.Retrieves;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.servicemaps.map.CustomerService;
import com.panut.centralbanking.userservice.api.service.customerservice.request.ActivateUserRequest;
import com.panut.centralbanking.userservice.api.service.customerservice.request.GetUserActivationCodeRequest;
import com.panut.centralbanking.userservice.api.service.customerservice.response.UserDefaultPasswordData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerUserActivationCodeService {
    private final WebClient customerServiceWebClient;

    public CustomerUserActivationCodeService(WebClient customerServiceWebClient) {
        this.customerServiceWebClient = customerServiceWebClient;
    }


    private final String LOG_NAME = "CustomerUserActivationCodeService ";

    public Mono<UserDefaultPasswordData> getCode(final GetUserActivationCodeRequest body) {
        return Retrieves.doRequest(customerServiceWebClient,
                        CustomerService.GET_ACTIVATION_CODE_FULL_PATH,
                        body,
                        BusinessException.class,
                        LOG_NAME + "getCode")
                .flatMap(result ->
                     Mono.fromCallable(() -> (UserDefaultPasswordData) result.getOptionalBody()));
    }


    public Mono<CommonResult> consume(final ActivateUserRequest body) {
        return Retrieves.doRequest(customerServiceWebClient,
                CustomerService.ACTIVATE_USER_FULL_PATH,
                body,
                BusinessException.class,
                LOG_NAME + " consume");
    }

}
