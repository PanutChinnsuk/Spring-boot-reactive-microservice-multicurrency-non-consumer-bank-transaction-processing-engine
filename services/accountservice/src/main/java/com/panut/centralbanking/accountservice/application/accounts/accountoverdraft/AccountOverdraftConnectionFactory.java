package com.panut.centralbanking.accountservice.application.accounts.accountoverdraft;

import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.appR2dbccore.query.SqlAppender;
import com.panut.centralbanking.appcore.util.ListIndex;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AccountOverdraftConnectionFactory {
    private final DatabaseClient databaseClient;
    private final ConnectionFactory connectionFactory;

    public AccountOverdraftConnectionFactory(DatabaseClient databaseClient,
                                             ConnectionFactory connectionFactory) {
        this.databaseClient = databaseClient;
        this.connectionFactory = connectionFactory;
    }


    public Mono<Void> batchInsert(final List<AccountOverdraftCreateData> datas) {
        return Flux.defer(() -> {
           if (datas.isEmpty())
               return Flux.empty();


           return databaseClient.inConnectionMany(conn -> {
               Statement statement = conn.createStatement("""
                       INSERT INTO account_overdrafts (
                            id,
                            account_id,
                            amount,
                            paid,
                            status,
                            transaction_id,
                            version
                       )    
                       VALUES (
                            ?id,
                            ?accountId,
                            ?amount,
                            ?paid,
                            ?status,
                            ?transactionId,
                            ?version
                       )
                       
                       """);
               log.info("batch insert balance overdraft records");


               List<AccountOverdraft> overdrafts =
                       datas.stream()
                               .map(data -> AccountOverdraft
                                       .builder()
                                       .transactionId(data.getTransactionId())
                                       .accountId(data.getAccountId())
                                       .amount(data.getAmount())
                                       .overdraftedAt(data.getOverdraftedAt())
                                       .build())
                               .toList();

               log.info("turn create datas into balance overdraft records");


               for (int i = 0; i < overdrafts.size(); i++) {
                   AccountOverdraft overdraft = overdrafts.get(i);
                   log.info("overdraft account id {} added to insert statement",
                           overdraft.getAccountId());


                       statement.bind("id", overdraft.getId())
                               .bind("accountId", overdraft.getAccountId())
                               .bind("paid", overdraft.getPaid())
                               .bind("amount", overdraft.getAmount())
                               .bind("status", overdraft.getAccountOverdraftStatus())
                               .bind("version", overdraft.getDefaultVersionValue());

                       String transactionId = overdraft.getTransactionId();
                       if (transactionId == null) {
                           statement.bindNull("transactionId", String.class);
                       }else {
                           statement.bind("transactionId", overdraft.getTransactionId());
                       }



                   if (ListIndex.isNotLast(i, overdrafts.size()))
                       statement.add();
               }

               return Flux.from(statement.execute())
                       .flatMap(result
                               -> Mono.from(result.getRowsUpdated()))
                       .doOnNext(a ->
                               log.info("AccountOverdraftConnectionFactory batch insert statement execute, {}", a));
           });
        }).then();
    }

    public Mono<Void> batchInsertMultirow(final List<AccountOverdraftCreateData> datas) {
        return Flux.defer(() -> {
            if (datas.isEmpty())
                return Flux.empty();


            return databaseClient.inConnection(conn -> {
                List<AccountOverdraft> overdrafts =
                        datas.stream()
                                .map(data -> AccountOverdraft
                                        .builder()
                                        .transactionId(data.getTransactionId())
                                        .accountId(data.getAccountId())
                                        .amount(data.getAmount())
                                        .overdraftedAt(data.getOverdraftedAt())
                                        .build())
                                .toList();
                log.info("turn create datas into balance overdraft records");



                StringBuilder sql = new StringBuilder("""
                       INSERT INTO account_overdrafts (
                            id,
                            account_id,
                            amount,
                            paid,
                            status,
                            version,
                            transaction_id
                       )    
                       VALUES 
                       """);
                String toAppend = "(?,?,?,?,?,?,?)";

                log.info("batch insert balance overdraft records");

                DatabaseClient.GenericExecuteSpec statement =
                        databaseClient.sql(
                                SqlAppender.multiRowInsert(overdrafts.size(),
                                        sql,
                                        toAppend)
                        );


                int bindIndex = 0;
                for (int i = 0; i < overdrafts.size(); i++) {
                    var overdraft = overdrafts.get(i);

                    log.info("current statement overdraft id, {}", overdraft.getId());

                    statement = statement.bind(bindIndex++, overdraft.getId())
                            .bind(bindIndex++, overdraft.getAccountId())
                            .bind(bindIndex++, overdraft.getAmount())
                            .bind(bindIndex++, overdraft.getPaid())
                            .bind(bindIndex++, overdraft.getAccountOverdraftStatus())
                            .bind(bindIndex++, overdraft.getDefaultVersionValue());

                    String transactionId = overdraft.getTransactionId();

                    if (transactionId == null) {
                        statement = statement.bindNull(bindIndex++, String.class);
                    }else {
                        statement = statement.bind(bindIndex++, toAppend);
                    }


                }






                return statement.fetch().rowsUpdated()
                        .doOnNext(a ->
                                log.info("AccountOverdraftConnectionFactory batchInsertMultiRow, row: {}", a));
            });
        }).then();
    }


    public Mono<Void> batchUpdate(final List<AccountOverdraft> accountOverdrafts) {
        return Flux.defer(() -> {
            if (accountOverdrafts.isEmpty())
                return Flux.empty();

            return databaseClient.inConnectionMany(conn -> {
                Statement statement = conn.createStatement("""
                        UPDATE account_overdrafts SET 
                            paid = ?paid,
                            status = ?status,
                            version = ?newVersion
                        WHERE id = ?id
                        """);

                log.info("batch update balance overdraft records");

                for (int i = 0; i < accountOverdrafts.size(); i++) {
                    AccountOverdraft overdraft = accountOverdrafts.get(i);

                    statement.bind("paid", overdraft.getPaid())
                            .bind("status", overdraft.getAccountOverdraftStatus())
                            .bind("newVersion", overdraft.getNextVersion())
                            .bind("id", overdraft.getId());

                    log.info("overdraft account id {}, id {} added to update statement",
                            overdraft.getAccountId(), overdraft.getId());

                    if (ListIndex.isNotLast(i, accountOverdrafts.size()))
                        statement.add();
                }

                return Flux.from(statement.execute())
                        .flatMap(result
                                -> Mono.from(result.getRowsUpdated()))
                        .doOnNext(a ->
                        log.info("AccountOverdraftConnectionFactory batch update statement execute, {}",
                                a));
            });
        }).then();

    }
}


