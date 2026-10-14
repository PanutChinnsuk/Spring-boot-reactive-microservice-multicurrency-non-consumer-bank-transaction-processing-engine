package com.panut.centralbanking.customerservice.application.customeruseractivationcode.query;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerUserActivationCode;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerUserActivationCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Slf4j
public class CustomerUserActivationCodeQuery {
    private final CustomerUserActivationCodeRepository customerUserActivationCodeRepository;


    public CustomerUserActivationCodeQuery(CustomerUserActivationCodeRepository customerUserActivationCodeRepository) {
        this.customerUserActivationCodeRepository = customerUserActivationCodeRepository;
    }



    public Mono<CustomerUserActivationCode> findValid(String customerId,
                                                      String code) {
        return customerUserActivationCodeRepository.findValid(customerId, code,
                        LocalDateTime.now())
                .switchIfEmpty(Mono.error(
                        () -> new BusinessException("No valid user activation code" +
                                " found. Either expired or revoked.")
                ));
    }
}
