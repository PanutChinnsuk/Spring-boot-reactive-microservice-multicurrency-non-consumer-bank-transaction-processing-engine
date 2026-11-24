package com.panut.centralbanking.accountservice.application.events.processedbalancechange;

import com.panut.centralbanking.appR2dbccore.query.SqlAppender;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class ProcessedBalanceChangeEventConnectionFactory {
    private final DatabaseClient databaseClient;
    private final ConnectionFactory connectionFactory;

    public ProcessedBalanceChangeEventConnectionFactory(DatabaseClient databaseClient,
                                                        ConnectionFactory connectionFactory) {
        this.databaseClient = databaseClient;
        this.connectionFactory = connectionFactory;
    }

    public Mono<Void> batchInsertMultirow(final List<String> processedEventIds) {
        return databaseClient.inConnection(conn -> {
            if (processedEventIds.isEmpty())
                return Mono.empty();

            StringBuilder sql = new StringBuilder("""
                    INSERT INTO account_processed_balance_change_events (
                        event_id
                    )
                    VALUES 
                    """);
            String appendingValue = "(?)";

            DatabaseClient.GenericExecuteSpec statement = databaseClient.sql(
                    SqlAppender
                            .multiRowInsert(processedEventIds.size(),
                                    sql,
                                    appendingValue)
            );

            int bindingValue = 0;
            for (int i = 0; i < processedEventIds.size(); i++) {
                var eventId = processedEventIds.get(i);

                log.info("binding statement processed balance change event eventId, {}",
                        eventId);

                statement = statement.bind(bindingValue++, eventId);
            }

            return statement.fetch().rowsUpdated()
                    .doOnNext(a -> log.info("ProcessedBalanceChangeEventConnectionFactory batchInsertMultirow, row: {}",
                            a));
        }).then();
    }
}
