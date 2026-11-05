package com.panut.centralbanking.transactionservice.application.ledgeraccount.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.infrastructure.repository.LedgerAccountRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.querydto.LedgerAccountMapQueryResult;
import com.panut.centralbanking.transactionservice.infrastructure.repository.querydto.LedgerTypeMapQueryResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LedgerAccountQuery {
    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerAccountQuery(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }

    private void enforceListSize(List<?> anyList,
                                 Set<?> anySet) {
        if (anyList.size() != anySet.size())
            throw new NotFound("One or more ledger accounts is missing.");

    }


    public Mono<String> findIdGeneric(final String code) {
        return ledgerAccountRepository.getIdByCode(code)
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("Ledger account table does not contain ledger account " +
                                "with code " + code)
                ));
    }

    public Mono<Map<String,String>> getLedgerAccountMapByCodes(final Set<String> codes,
                                                               final Set<LedgerType> expectedLedgerType) {
        return ledgerAccountRepository.getLedgerAccountMapByCodes(codes,
                        expectedLedgerType)
                .collectList()
                .flatMap(list -> {
                    enforceListSize(list, codes);

                    return Mono.just(list.stream().collect(Collectors.toMap(
                            LedgerAccountMapQueryResult::getCode,
                            LedgerAccountMapQueryResult::getId)));
                });
    }

    public Mono<Map<String,LedgerType>> getLedgerTypeMapByTransactionId(final String transactionId) {
        return ledgerAccountRepository.getLedgerTypeMapByTransactionId(transactionId)
                .collectList()
                .flatMap(list -> {
                    return Mono.just(list.stream().distinct().collect(Collectors.toMap(
                            LedgerTypeMapQueryResult::getId,
                            LedgerTypeMapQueryResult::getLedgerType)));
                });
    }

    public Mono<String> findExistNotThrow() {
        return ledgerAccountRepository.getIdExists();
    }
}
