package com.panut.centralbanking.accountservice.application.accounts.accountoverdraft;

import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.appR2dbccore.query.SqlAppender;
import com.panut.centralbanking.appR2dbccore.query.SqlAppenderResult;
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
                        WHERE id = ?id AND
                        version = ?currentVersion
                        """);

                log.info("batch update balance overdraft records");

                for (int i = 0; i < accountOverdrafts.size(); i++) {
                    AccountOverdraft overdraft = accountOverdrafts.get(i);

                    statement.bind("paid", overdraft.getPaid())
                            .bind("status", overdraft.getAccountOverdraftStatus())
                            .bind("newVersion", overdraft.getNextVersion())
                            .bind("id", overdraft.getId())
                            .bind("currentVersion", overdraft.getVersion());

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

    public Mono<Void> batchUpdateMultirow(final List<AccountOverdraft> accountOverdrafts) {
        return databaseClient.inConnection(conn -> {
            if (accountOverdrafts.isEmpty())
                return Mono.empty();

            int size = accountOverdrafts.size();
            var head = new StringBuilder("""
                        UPDATE account_overdrafts 
                        SET 
                        """);

            var paid = new StringBuilder("""
                        paid = CASE id
                        """);

            var status = new StringBuilder("""
                        status = CASE id
                        """);

            var version = new StringBuilder("""
                        version = CASE id
                        """);

            var endVersionCondition = new StringBuilder("""
                        AND version = CASE id
                        """);

            var afterPaid = SqlAppender
                    .Update
                    .appendCondition(size, head,
                            paid, "paid", true);

            var afterStatus = SqlAppender
                    .Update
                    .appendCondition(size, afterPaid,
                            status, "paid", true);

            var afterVersion = SqlAppender
                    .Update
                    .appendCondition(size, afterStatus,
                            version, "version", false);

            var afterEnded = SqlAppender
                    .Update
                    .endWithWHERE_FIELD_IN(size, afterVersion, "id");

            var afterEndVersionCondition = SqlAppender
                    .Update
                    .appendCondition(size, afterEnded, endVersionCondition,
                            "version", false);

            DatabaseClient.GenericExecuteSpec statement =
                    databaseClient.sql(afterEndVersionCondition.toString());

            var result = bindMultirowUpdateValue(statement, accountOverdrafts);
            statement = result.modifiedExecuteSpec();

            return statement.fetch().rowsUpdated()
                    .doOnNext(a ->
                            log.info("AccountOverdraftConnectionFactory batch update multirow, row: {}",
                                    a));
        }).then();

    }

    public SqlAppenderResult bindMultirowUpdateValue(final DatabaseClient.GenericExecuteSpec any,
                                                     final List<AccountOverdraft> accountOverdrafts) {
        int bindingIndex = 0;
        var thisStatement = any;

        var balance = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountOverdrafts,
                        AccountOverdraft::getId,
                        AccountOverdraft::getPaid);
        bindingIndex = balance.incrementedBindingIndex();
        thisStatement = balance.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW CONDITION FOR PAID");

        var status = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountOverdrafts,
                        AccountOverdraft::getId,
                        AccountOverdraft::getAccountOverdraftStatus);
        bindingIndex = balance.incrementedBindingIndex();
        thisStatement = balance.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW CONDITION FOR STATUS");

        var version = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountOverdrafts,
                        AccountOverdraft::getId,
                        AccountOverdraft::getNextVersion);
        bindingIndex = balance.incrementedBindingIndex();
        thisStatement = balance.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW CONDITION FOR VERSION");

        var endedResukt = SqlAppender
                .Update
                .endBinding(bindingIndex,
                        thisStatement,
                        accountOverdrafts,
                        AccountOverdraft::getId);
        bindingIndex = balance.incrementedBindingIndex();
        thisStatement = balance.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW UPDATE (WHERE) CONDITION FOR IN (ids)");

        var versionEndCondition = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountOverdrafts,
                        AccountOverdraft::getId,
                        AccountOverdraft::getVersion);
        bindingIndex = versionEndCondition.incrementedBindingIndex();
        thisStatement = versionEndCondition.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW UPDATE (WHERE) CONDITION VERSION IS CURRENT VERSION");



        return new SqlAppenderResult(bindingIndex, thisStatement);
    }
}


