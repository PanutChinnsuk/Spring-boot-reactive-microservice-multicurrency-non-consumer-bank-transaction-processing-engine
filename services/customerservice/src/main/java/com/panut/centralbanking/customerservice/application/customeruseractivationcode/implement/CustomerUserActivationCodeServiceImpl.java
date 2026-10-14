package com.panut.centralbanking.customerservice.application.customeruseractivationcode.implement;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.customerservice.application.CodeGenerator;
import com.panut.centralbanking.customerservice.application.customer.CustomerRepositoryManager;
import com.panut.centralbanking.customerservice.application.customer.query.CustomerQuery;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.CustomerUserActivationCodeRepositoryManager;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.query.CustomerUserActivationCodeQuery;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.service.CustomerUserActivationCodeService;
import com.panut.centralbanking.customerservice.application.property.CustomerUserActivationCodeProperty;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerUserActivationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class CustomerUserActivationCodeServiceImpl
implements CustomerUserActivationCodeService {
    private final CustomerUserActivationCodeProperty property;
    private final CodeGenerator codeGenerator;
    private final CustomerUserActivationCodeRepositoryManager repoManager;
    private final CustomerQuery customerQuery;
    private final CustomerUserActivationCodeQuery userActivationCodeQuery;
    private final CustomerRepositoryManager customerRepositoryManager;

    public CustomerUserActivationCodeServiceImpl(CustomerUserActivationCodeProperty property,
                                                 CodeGenerator codeGenerator,
                                                 CustomerUserActivationCodeRepositoryManager repoManager,
                                                 CustomerQuery customerQuery,
                                                 CustomerUserActivationCodeQuery userActivationCodeQuery,
                                                 CustomerRepositoryManager customerRepositoryManager) {
        this.property = property;
        this.codeGenerator = codeGenerator;
        this.repoManager = repoManager;
        this.customerQuery = customerQuery;
        this.userActivationCodeQuery = userActivationCodeQuery;
        this.customerRepositoryManager = customerRepositoryManager;
    }

    @Override
    public Mono<CommonResult> revoke(String customerId) {
        return repoManager.revokeAllOfCustomer(customerId)
                .thenReturn(CommonResult.defaultSuccess())
                .onErrorResume(error ->
                        Mono.just(CommonResult.defaultFailed(error.getLocalizedMessage())));
    }

    @Override
    public Mono<CustomerUserActivationCode> createCode(String customerId) {
        return repoManager.revokeAllOfCustomer(customerId)
                .doOnNext(a -> log.info("Revoked all previous customer's " +
                        "user activation codes."))
                .then(repoManager.save(CustomerUserActivationCode.builder()
                        .code(codeGenerator.generateSimple())
                        .customerId(customerId)
                        .expiresAt(property.getExpiry())
                        .build()))
                .doOnSuccess(a -> log.info(
                        "Created customer user activation code."))
                .doOnError(error -> log.error("Failed to create customer" +
                        " user activation code: {}", error.getLocalizedMessage()));

    }

    @Override
    public Mono<CommonResult> renewCode(String customerId) {
        return customerQuery.findGeneric(customerId)
                .then(createCode(customerId))
                .map(code -> CommonResult
                        .defaultSuccess("User activation code: " + code.getCode()))
                .doOnSuccess(a -> log.info(
                        "Renewed customer user activation code."))
                .onErrorResume(error ->
                        Mono.just(CommonResult.defaultFailed(error.getLocalizedMessage())));
    }


    @Override
    public Mono<CommonResult> consumeValidCode(String customerId,
                                               String code,
                                               String userId) {
        return userActivationCodeQuery.findValid(customerId, code)
                .flatMap(activationCode -> activationCode
                        .revoke()
                        .then(repoManager.save(activationCode)))
                .then(customerQuery.findGeneric(customerId))
                .flatMap(customer -> customer.activateWithUser(userId)
                        .then(customerRepositoryManager.save(customer)))
                .thenReturn(CommonResult.defaultSuccess(
                        "Successfully applied user data to" +
                                " customer."
                ))
                .onErrorResume(error -> {
                    String errMsg = error.getLocalizedMessage();
                    log.error("Failed to consume customer activation code: {}", errMsg);

                    return Mono.just(CommonResult.defaultFailed(errMsg));
                });
    }
}
