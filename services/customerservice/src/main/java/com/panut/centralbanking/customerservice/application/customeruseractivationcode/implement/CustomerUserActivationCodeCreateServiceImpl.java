package com.panut.centralbanking.customerservice.application.customeruseractivationcode.implement;

import com.panut.centralbanking.customerservice.application.CodeGenerator;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.CustomerUserActivationCodeRepositoryManager;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.service.CustomerUserActivationCodeCreateService;
import com.panut.centralbanking.customerservice.application.property.CustomerUserActivationCodeProperty;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerUserActivationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerUserActivationCodeCreateServiceImpl
implements CustomerUserActivationCodeCreateService {
    private final CustomerUserActivationCodeProperty property;
    private final CodeGenerator codeGenerator;
    private final CustomerUserActivationCodeRepositoryManager repoManager;

    public CustomerUserActivationCodeCreateServiceImpl(CustomerUserActivationCodeProperty property,
                                                       CodeGenerator codeGenerator,
                                                       CustomerUserActivationCodeRepositoryManager repoManager) {
        this.property = property;
        this.codeGenerator = codeGenerator;
        this.repoManager = repoManager;
    }



    @Override
    public Mono<CustomerUserActivationCode> createCode(String customerId) {
        return repoManager.save(CustomerUserActivationCode.builder()
                .code(codeGenerator.generateSimple())
                .customerId(customerId)
                .expiresAt(property.getExpiry())
                .build())
                .doOnSuccess(a -> log.info(
                        "Create customer user activation code."))
                .doOnError(error -> log.error("Failed to create customer" +
                        " user activation code: {}", error.getLocalizedMessage()));

    }
}
