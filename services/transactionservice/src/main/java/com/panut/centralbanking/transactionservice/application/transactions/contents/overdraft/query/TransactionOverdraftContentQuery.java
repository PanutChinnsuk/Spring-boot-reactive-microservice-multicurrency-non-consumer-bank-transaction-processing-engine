package com.panut.centralbanking.transactionservice.application.transactions.contents.overdraft.query;

import com.panut.centralbanking.appR2dbccore.data.MapListSorter;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionOverdraftContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TransactionOverdraftContentQuery {
    private final TransactionOverdraftContentRepository repository;

    public TransactionOverdraftContentQuery(TransactionOverdraftContentRepository repository) {
        this.repository = repository;
    }



    public Mono<Map<String, List<String>>> getOverdraftIdsMap(final List<String> eventIds) {
        return repository.getOverdraftContext(eventIds)
                .collectList()
                .doOnNext(a ->
                        log.info("get all overdraft content from event ids"))
                .map(list -> {
                    Map<String, List<String>> overdraftIds = new HashMap<>();

                    log.info("mapping overdraft content into <transactionId, content>");
                    list.forEach(content -> {
                        String key = content.getTransactionId();
                        String value = content.getOverdraftId();

                        MapListSorter.sortMapList(overdraftIds, key, value);
                    });
                    log.info("after mapping, map size: {}", overdraftIds.size());


                    return overdraftIds;
                });
    }
}
