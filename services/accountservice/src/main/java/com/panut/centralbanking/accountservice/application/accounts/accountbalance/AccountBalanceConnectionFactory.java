package com.panut.centralbanking.accountservice.application.accounts.accountbalance;

import com.panut.centralbanking.accountservice.application.ListIndex;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.connection.ConnectionFactoryUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
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
                           version = ?newVersion
                       WHERE id = ?id
                       AND version = ?currentVersion
                       """);


                   for (int i = 0; i < accountBalances.size(); i++) {
                       AccountBalance accountBalance = accountBalances.get(i);

                       statement.bind("balance", accountBalance.getBalance())
                               .bind("reserve", accountBalance.getReserve())
                               .bind("ledger", accountBalance.getLedger())
                               .bind("newVersion", accountBalance.getVersion() + 1)
                               .bind("id", accountBalance.getId())
                               .bind("currentVersion", accountBalance.getVersion());

                       if (ListIndex.isLast(i, accountBalances.size()))
                           statement.add();

                   }



                   log.info("AccountBalanceConnectionFactory statement execute");
                   return Flux.from(statement.execute());
               })
               .then()
               .doOnNext(a -> log.info("AccountBalanceConnectionFactory" +
                       " after statement executed"));


    }

}
