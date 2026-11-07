package com.panut.centralbanking.accountservice.application.accounts.internalaccounts.accounts.implement;

import com.panut.centralbanking.accountservice.api.response.serviceonly.GetFxInternalAccountResponse;
import com.panut.centralbanking.accountservice.application.accounts.internalaccounts.accounts.service.InternalAccountQueryService;
import com.panut.centralbanking.accountservice.application.property.InternalAccountProperty;
import com.panut.centralbanking.accountservice.core.InternalAccountType;
import com.panut.centralbanking.accountservice.infrastructure.repository.InternalAccountRepository;
import com.panut.centralbanking.accountservice.infrastructure.repository.queryresult.InternalAccountQueryResult;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class InternalAccountQueryServiceImpl implements InternalAccountQueryService {
    private final InternalAccountProperty internalAccountProperty;
    private final InternalAccountRepository internalAccountRepository;

    public InternalAccountQueryServiceImpl(InternalAccountProperty internalAccountProperty,
                                           InternalAccountRepository internalAccountRepository) {
        this.internalAccountProperty = internalAccountProperty;
        this.internalAccountRepository = internalAccountRepository;
    }

    private Set<InternalAccountType> FX_POLICY = Set.of(
            InternalAccountType.FX_CLEARING,
            InternalAccountType.FX_PROFIT
    );

    private Mono<Map<InternalAccountType, String>> toMap(final int size,
                                          final Flux<InternalAccountQueryResult> any) {
        return any.collectList()
                .map(list -> {
                    if (list.size() != size)
                        throw new NotFound("One or more internal accounts " +
                                        "not found for curreny request. " +
                                        "Found: " + list.size() + ", needed: " +
                                        size);


                    return list.stream().collect(Collectors.toMap(
                            InternalAccountQueryResult::getInternalAccountType,
                            InternalAccountQueryResult::getAccountId
                    ));
                });
    }



    @Override
    public Mono<GetFxInternalAccountResponse> getFxInternalAccount() {
        return internalAccountRepository
                .getInternalAccounts(internalAccountProperty.getCurrentBankBranch(),
                        FX_POLICY)
                .as(flux -> toMap(2, flux))
                .map(GetFxInternalAccountResponse::withMap);
    }
}
