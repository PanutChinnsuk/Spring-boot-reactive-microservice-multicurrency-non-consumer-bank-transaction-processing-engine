package com.panut.centralbanking.transactionservice.application.ledgeraccount.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.transactionservice.infrastructure.repository.LedgerAccountRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.querydto.LedgerAccountMapQueryResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LedgerAccountQuery {
    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerAccountQuery(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }




    public Mono<String> findIdGeneric(final String code) {
        return ledgerAccountRepository.getIdByCode(code)
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("Ledger account table does not contain ledger account " +
                                "with code " + code)
                ));
    }

    public Mono<Map<String,String>> getLedgerAccountMapByCodes(final Set<String> codes) {
        return ledgerAccountRepository.getLedgerAccountMapByCodes(codes)
                .collectList()
                .flatMap(list -> {
                    if (list.size() != codes.size())
                        return Mono.<Map<String, String>>error(() -> new NotFound("One or more ledger accounts is missing."));


                    return Mono.just(list.stream().collect(Collectors.toMap(
                            LedgerAccountMapQueryResult::getCode,
                            LedgerAccountMapQueryResult::getId)));
                });
    }

    public Mono<String> findExistNotThrow() {
        return ledgerAccountRepository.getIdExists();
    }
}
