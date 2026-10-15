package com.panut.centralbanking.userservice.api.service.customerservice;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.servicemaps.map.CustomerService;
import com.panut.centralbanking.userservice.api.service.customerservice.request.ActivateUserRequest;
import com.panut.centralbanking.userservice.api.service.customerservice.request.GetUserActivationCodeRequest;
import com.panut.centralbanking.userservice.api.service.customerservice.response.UserDefaultPasswordData;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerUserActivationCodeService {
    private final WebClient webClient;

    public CustomerUserActivationCodeService(WebClient webClient) {
        this.webClient = webClient;
    }

    private <T> Mono<T> error(final CommonResult result) {
        return Mono.error(
                () -> new BusinessException(result.getOptionalMessage()));
    }


    private Mono<CommonResult> doRequest(final String fullPath,
                                         final Object body) {
        return webClient
                .post()
                .uri(fullPath)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CommonResult.class)
                .flatMap(result -> {
                    if (!result.isSuccess()) error(result);
                    return Mono.just(result);
                });
    }

    public Mono<UserDefaultPasswordData> getCode(final GetUserActivationCodeRequest body) {
        return doRequest(CustomerService.GET_ACTIVATION_CODE_FULL_PATH, body)
                .flatMap(result ->
                     Mono.just((UserDefaultPasswordData) result.getOptionalBody()));
    }


    public Mono<CommonResult> consume(final ActivateUserRequest body) {
        return doRequest(CustomerService.ACTIVATE_USER_FULL_PATH, body);
    }

}
