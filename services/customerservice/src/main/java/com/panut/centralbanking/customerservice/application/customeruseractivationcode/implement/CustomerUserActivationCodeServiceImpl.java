package com.panut.centralbanking.customerservice.application.customeruseractivationcode.implement;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.customerservice.api.response.UserDefaultPasswordData;
import com.panut.centralbanking.customerservice.application.CodeGenerator;
import com.panut.centralbanking.customerservice.application.CustomerDefaultPasswordGenerator;
import com.panut.centralbanking.customerservice.application.customer.CustomerRepositoryManager;
import com.panut.centralbanking.customerservice.application.customer.query.CustomerQuery;
import com.panut.centralbanking.customerservice.application.customerprofile.query.CustomerProfileQuery;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.CustomerUserActivationCodeRepositoryManager;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.query.CustomerUserActivationCodeQuery;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.service.CustomerUserActivationCodeService;
import com.panut.centralbanking.customerservice.application.property.CustomerUserActivationCodeProperty;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerProfile;
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
    private final CustomerProfileQuery customerProfileQuery;
    private final CustomerDefaultPasswordGenerator defaultPassword;

    public CustomerUserActivationCodeServiceImpl(CustomerUserActivationCodeProperty property,
                                                 CodeGenerator codeGenerator,
                                                 CustomerUserActivationCodeRepositoryManager repoManager,
                                                 CustomerQuery customerQuery,
                                                 CustomerUserActivationCodeQuery userActivationCodeQuery,
                                                 CustomerRepositoryManager customerRepositoryManager,
                                                 CustomerProfileQuery customerProfileQuery,
                                                 CustomerDefaultPasswordGenerator defaultPassword) {
        this.property = property;
        this.codeGenerator = codeGenerator;
        this.repoManager = repoManager;
        this.customerQuery = customerQuery;
        this.userActivationCodeQuery = userActivationCodeQuery;
        this.customerRepositoryManager = customerRepositoryManager;
        this.customerProfileQuery = customerProfileQuery;
        this.defaultPassword = defaultPassword;
    }

    @Override
    public Mono<CommonResult> revoke(final String customerId) {
        return repoManager.revokeAllOfCustomer(customerId)
                .thenReturn(CommonResult.defaultSuccess())
                .doOnError(error -> log.error("Failed to revoke user activation code {}",
                        error.getLocalizedMessage()));
    }

    @Override
    public Mono<CustomerUserActivationCode> createCode(final String customerId) {
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
    public Mono<CommonResult> renewCode(final String customerId) {
        return customerQuery.findIdGeneric(customerId)
                .then(createCode(customerId))
                .map(code -> CommonResult
                        .defaultSuccess("User activation code: " + code.getCode()))
                .doOnSuccess(a -> log.info(
                        "Renewed customer user activation code."))
                .doOnError(error -> log.error("Failed to renew user activation code {}",
                        error.getLocalizedMessage()));
    }


    @Override
    public Mono<CommonResult<UserDefaultPasswordData>> getCode(final String customerId,
                                                               final String code) {
        return userActivationCodeQuery.findValid(customerId, code)
                .then(customerQuery.findIdGeneric(customerId))
                .flatMap(queryCustomerId ->
                        customerProfileQuery.findGeneric(queryCustomerId))
                .map(profile ->
                    new CommonResult<>("Get default data for user.",
                            new UserDefaultPasswordData(
                                    profile.getFirstname(),
                                    profile.getLastname(),
                                    defaultPassword.simple(profile.getCitizenId())
                            )))
                .onErrorResume(error -> {
                    String errMsg = error.getLocalizedMessage();
                    log.error("Failed to get user activation code {}", errMsg);

                    return Mono.just(new CommonResult<>(false, errMsg));
                });
    }

    @Override
    public Mono<CommonResult> consume(final String customerId,
                                      final String code,
                                      final String userId) {
        return userActivationCodeQuery.findValid(customerId, code)
                .flatMap(activationCode -> activationCode
                        .revoke()
                        .then(repoManager.save(activationCode)))
                .then(customerQuery.findGeneric(customerId))
                .flatMap(customer -> customer.activateWithUser(userId)
                        .then(customerRepositoryManager.save(customer)))
                .thenReturn(CommonResult.defaultSuccess("Successfully consumed" +
                        " user activation code."))
                .doOnError(error -> log.error("Failed to consume customer activation code: {}",
                        error.getLocalizedMessage()));
    }
}
