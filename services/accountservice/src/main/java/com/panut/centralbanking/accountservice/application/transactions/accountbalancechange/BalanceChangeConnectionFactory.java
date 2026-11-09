package com.panut.centralbanking.accountservice.application.transactions.accountbalancechange;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.application.ListIndex;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

record Chain(List<AccountBalanceChange> records) {}

@Component
@Slf4j
public class BalanceChangeConnectionFactory {
    private final DatabaseClient databaseClient;
    private final ConnectionFactory connectionFactory;

    public BalanceChangeConnectionFactory(DatabaseClient databaseClient,
                                          ConnectionFactory connectionFactory) {
        this.databaseClient = databaseClient;
        this.connectionFactory = connectionFactory;
    }

    public Mono<List<AccountBalanceChange>> batchInsert(final List<BalanceChangeData> datas) {
        return Mono.just(datas
                .stream().map(change ->
                        AccountBalanceChange.builder()
                                .accountId(change.getAccountId())
                                .changedAt(change.getCreatedAt())
                                .amount(change.getAmount())
                                .balanceImpact(change.getBalanceImpact())
                                .balanceChangeType(change.getBalanceChangeType())
                                .transactionId(change.getTransactionId())
                                .build()
                                .withOverdraftChangeContext(change.getOverdraftChangeContext())).toList())
                        .map(records ->
                                new Chain(records))
                        .flatMap(chain ->  databaseClient.inConnectionMany(conn -> {
                            Statement statement = conn.createStatement("""
                                        INSERT INTO account_balance_changes (
                                            id, account_id,
                                            amount,
                                            change_type,
                                            balance_impact,
                                            transaction_id
                                        ) 
                                        VALUES (
                                            ?id, 
                                            ?accountId, 
                                            ?amount, 
                                            ?changeType,
                                            ?balanceImpact, 
                                            ?transactionId
                                        )
                                        """);
                            log.info("batch insert balance change data records");

                            List<AccountBalanceChange> records = datas
                                    .stream().map(change ->
                                            AccountBalanceChange.builder()
                                                    .accountId(change.getAccountId())
                                                    .changedAt(change.getCreatedAt())
                                                    .amount(change.getAmount())
                                                    .balanceImpact(change.getBalanceImpact())
                                                    .balanceChangeType(change.getBalanceChangeType())
                                                    .transactionId(change.getTransactionId())
                                                    .build()).toList();
                            log.info("turn plain balance change data into records");

                            for (int i = 0; i < records.size(); i++) {
                                AccountBalanceChange data = records.get(i);
                                log.info("record account id {} added to statement",
                                        data.getAccountId());

                                statement.bind("id", data.getId())
                                        .bind("accountId", data.getAccountId())
                                        .bind("amount", data.getAmount())
                                        .bind("changeType", data.getBalanceChangeType())
                                        .bind("balanceImpact", data.getBalanceImpact());

                                String transactionId = data.getTransactionId();
                                if (transactionId == null) {
                                    statement.bindNull("transactionId", String.class);
                                }else {
                                    statement.bind("transactionId", transactionId);
                                }

                                if (ListIndex.isNotLast(i, records.size()))
                                    statement.add();
                            }


                            return Flux.from(statement.execute())
                                    .doOnNext(a ->
                                            log.info("BalanceChangeConnectionFactory statement execute"));
                        }).then().thenReturn(chain.records())
                    );

    }
}
