package com.panut.centralbanking.accountservice.application.accounts.accountbalance;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
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
public class AccountBalanceConnectionFactory {
    private final ConnectionFactory connectionFactory;
    private final DatabaseClient databaseClient;

    public AccountBalanceConnectionFactory(ConnectionFactory connectionFactory,
                                           DatabaseClient databaseClient) {
        this.connectionFactory = connectionFactory;
        this.databaseClient = databaseClient;
    }




    public Mono<Void> batchUpdate(final List<AccountBalance> accountBalances) {
       return databaseClient.inConnectionMany(conn -> {
                   Statement statement = conn.createStatement("""
                       UPDATE account_balances 
                       SET balance = ?balance,
                           reserve = ?reserve,
                           ledger = ?ledger,
                           overdrafted = ?overdrafted,
                           version = ?newVersion
                       WHERE id = ?id AND
                       version = ?currentVersion
                       """);


                   for (int i = 0; i < accountBalances.size(); i++) {
                       AccountBalance accountBalance = accountBalances.get(i);

                       log.info("Account balance account id {} added to statement",
                               accountBalance.getAccountId());

                       statement.bind("balance", accountBalance.getBalance())
                               .bind("reserve", accountBalance.getReserve())
                               .bind("ledger", accountBalance.getLedger())
                               .bind("overdrafted", accountBalance.getOverdrafted())
                               .bind("newVersion", accountBalance.getNextVersion())
                               .bind("id", accountBalance.getId())
                               .bind("currentVersion", accountBalance.getVersion());

                       if (ListIndex.isNotLast(i, accountBalances.size()))
                           statement.add();

                   }




                   return Flux.from(statement.execute())
                           .flatMap(result
                                   -> Mono.from(result.getRowsUpdated()))
                           .doOnNext(a ->
                                   log.info("AccountBalanceConnectionFactory statement execute {}",
                                           a));
               })
               .then();


    }


    public Mono<Void> batchUpdateMultirow(final List<AccountBalance> accountBalances) {
        return databaseClient.inConnection(conn -> {
            int size = accountBalances.size();
                    var head = new StringBuilder("""
                       UPDATE account_balances 
                       SET 
                       """);

                    var balance = new StringBuilder("""
                            balance = CASE id 
                            """);
                    var reserve = new StringBuilder("""
                            reserve = CASE id 
                            """);
                    var ledger = new StringBuilder("""
                            ledger = CASE id 
                            """);
                    var overdraft = new StringBuilder("""
                            overdrafted = CASE id 
                            """);
                    var version = new StringBuilder("""
                            version = CASE id 
                            """);
                    var versionEndConditon = new StringBuilder("""
                            AND version = CASE id
                       """);

                    var afterBalance = SqlAppender.Update
                            .appendCondition(size, head,
                                    balance,
                                    "balance",
                                    true);

                    var afterReserve = SqlAppender.Update
                            .appendCondition(size, afterBalance,
                                    reserve,
                                    "reserve",
                                    true);

                    var afterLedger = SqlAppender.Update
                            .appendCondition(size, afterReserve,
                                    ledger,
                                    "ledger",
                                    true);

                    var afterOverdraft = SqlAppender.Update
                            .appendCondition(size, afterLedger,
                                    overdraft,
                                    "overdrafted",
                                    true);

                    var afterVersion = SqlAppender.Update
                            .appendCondition(size, afterOverdraft,
                                    version,
                                    "version",
                                    false);


                    var afterEnded = SqlAppender.Update
                            .endWithWHERE_FIELD_IN(size,
                                    afterVersion, "id");

                    var afterCurrentVersion = SqlAppender.Update
                            .appendCondition(size, afterEnded,
                                    versionEndConditon,
                                    "version",
                                    false);



                    DatabaseClient.GenericExecuteSpec statement =
                            databaseClient.sql(afterCurrentVersion.toString());

                    var statementResult = bindMultirowUpdateValue(statement, accountBalances);
                    statement = statementResult.modifiedExecuteSpec();



                    return statement.fetch().rowsUpdated()
                            .doOnNext(a ->
                                    log.info("AccountBalanceConnectionFactory batchUpdateMultirow, row: {}",
                                            a));
                })
                .then();


    }

    private SqlAppenderResult bindMultirowUpdateValue(final DatabaseClient.GenericExecuteSpec any,
                                                          final List<AccountBalance> accountBalances) {
        int bindingIndex = 0;
        var thisStatement = any;

        var balance = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountBalances,
                        AccountBalance::getId,
                        AccountBalance::getBalance);
        bindingIndex = balance.incrementedBindingIndex();
        thisStatement = balance.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW CONDITION FOR BALANCE");

        var reserve = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountBalances,
                        AccountBalance::getId,
                        AccountBalance::getReserve);
        bindingIndex = reserve.incrementedBindingIndex();
        thisStatement = reserve.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW CONDITION FOR RESERVE");

        var ledger = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountBalances,
                        AccountBalance::getId,
                        AccountBalance::getLedger);
        bindingIndex = ledger.incrementedBindingIndex();
        thisStatement = ledger.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW CONDITION FOR LEDGER");

        var overdrafted = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountBalances,
                        AccountBalance::getId,
                        AccountBalance::getOverdrafted);
        bindingIndex = overdrafted.incrementedBindingIndex();
        thisStatement = overdrafted.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW CONDITION FOR OVERDRAFTED");

        var version = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountBalances,
                        AccountBalance::getId,
                        AccountBalance::getNextVersion);
        bindingIndex = version.incrementedBindingIndex();
        thisStatement = version.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW CONDITION FOR VERSION");

        var endedResult = SqlAppender
                .Update
                .endBinding(bindingIndex,
                        thisStatement,
                        accountBalances,
                        AccountBalance::getId);
        bindingIndex = endedResult.incrementedBindingIndex();
        thisStatement = endedResult.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW UPDATE (WHERE) CONDITION FOR IN (ids)");

        var versionEndCondition = SqlAppender
                .Update
                .bindStatement(bindingIndex,
                        thisStatement,
                        accountBalances,
                        AccountBalance::getId,
                        AccountBalance::getVersion);
        bindingIndex = versionEndCondition.incrementedBindingIndex();
        thisStatement = versionEndCondition.modifiedExecuteSpec();

        log.info("AFTER ADD MULTIROW UPDATE (WHERE) CONDITION VERSION IS CURRENT VERSION");


        return new SqlAppenderResult(bindingIndex, thisStatement);
    }

}
