package com.panut.centralbanking.accountservice.application.accounts.accountoverdraft;

import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.application.ListIndex;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
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
                            status,
                            transaction_id,
                            version
                       )    
                       VALUES (
                            ?id,
                            ?accountId,
                            ?amount,
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
                   log.info("overdraft account id {} added to statement",
                           overdraft.getAccountId());


                       statement.bind("id", overdraft.getId())
                               .bind("accountId", overdraft.getAccountId())
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
                       .doOnNext(a ->
                               log.info("AccountOverdraftConnectionFactory statement execute"));
           });
        }).then();
    }
}


