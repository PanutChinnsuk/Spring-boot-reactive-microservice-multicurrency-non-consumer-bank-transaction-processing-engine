package com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.implement;

import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.accounts.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.AccountBalanceConnectionFactory;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.AccountBalanceRepositoryManager;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.query.AccountBalanceQuery;
import com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.AccountOverdraftConnectionFactory;
import com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.service.AccountOverdraftService;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeRecordService;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


record Result(List<AccountBalance> accountBalances,
              List<AccountBalanceChangedData> optional,
              List<AccountOverdraftCreateData> possibleOverdrafts) {}

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class BalanceChangeServiceImpl implements BalanceChangeService {

    private final AccountBalanceQuery accountBalanceQuery;
    private final BalanceChangeRecordService recordService;
    private final AccountBalanceConnectionFactory connectionFactory;
    private final AccountOverdraftConnectionFactory overdraftConnectionFactory;

    public BalanceChangeServiceImpl(AccountBalanceQuery accountBalanceQuery,
                                    BalanceChangeRecordService recordService,
                                    AccountBalanceConnectionFactory connectionFactory,
                                    AccountOverdraftConnectionFactory overdraftConnectionFactory) {
        this.accountBalanceQuery = accountBalanceQuery;
        this.recordService = recordService;
        this.connectionFactory = connectionFactory;
        this.overdraftConnectionFactory = overdraftConnectionFactory;
    }


    private BigDecimal getTotalDeduct(final List<AccountBalanceChange> records,
                                      final BalanceChangeType balanceChangeType) {
        return records
                .stream()
                .filter(a -> a.getBalanceImpact()
                        .equals(BalanceImpact.DEDUCT) &&
                        a.getBalanceChangeType().equals(balanceChangeType))
                .map(AccountBalanceChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private Mono<CommonResult> map(final Map<String, List<AccountBalanceChange>> records,
                                   boolean requiredResult) {
        return accountBalanceQuery.findTransactionAccountBalances(
                records.keySet())
                .doOnNext(a -> log.info("Created account balances " +
                        "mono chain."))
                .map(accountBalances -> {
                    List<AccountBalanceChangedData> optionalResult = new ArrayList<>();
                    List<AccountOverdraftCreateData> pendingOverdrafts = new ArrayList<>();

                    records.forEach((accountId, list) -> {
                        AccountBalanceChangedData changeResult = null;
                        AccountBalance accountBalance = accountBalances
                                .get(accountId);
                        if (requiredResult) {
                            log.debug("Method return required balance change " +
                                    "result.");
                            changeResult = new AccountBalanceChangedData();
                            changeResult.before(accountBalance);
                        }

                        List<AccountBalanceChange> filtered = list
                                .stream()
                                .sorted(Comparator.comparingInt(record ->
                                        record.getBalanceImpact().getAscendingPriority()))
                                .toList();





                        log.info("before orchest accountBalance {}",
                                accountBalance.getBalance());
                        log.info("before orchest filter size {}",
                                filtered.size());

                        var possibleOverdrafts = accountBalance
                                .orchest(filtered, accountId);

                        if (!possibleOverdrafts.isEmpty())
                            pendingOverdrafts.addAll(possibleOverdrafts);


                        if (requiredResult) {
                            changeResult.after(accountBalance);
                            optionalResult.add(changeResult);
                            log.debug("Added balance change result.");
                        }

                        log.info("after orchest accountBalance {}",
                                accountBalance.getBalance());
                    });



                    return new Result(accountBalances.values().
                            stream().toList(),
                            optionalResult,
                            pendingOverdrafts);

                })
                .doOnNext(a -> log.info("Applied (unsaved) balance changes."))
                .flatMap(result ->
                        connectionFactory.batchUpdate(result.accountBalances())
                                .then(overdraftConnectionFactory.batchInsert(
                                        result.possibleOverdrafts()))
                                .thenReturn(result))
                .map(result -> new CommonResult(
                        "Successfully applied balance changes",
                        result.optional()
                ))
                .doOnNext(a -> log.info(
                        "Saved all transaction's account balances."
                ));

    }



    @Override
    public Mono<CommonResult> bulk(final List<BalanceChangeData> data,
                                   final boolean requiredResult) {
        return recordService.create(data)
                .flatMap(addRecord ->
                        map(addRecord, requiredResult))
                .doOnSuccess(a -> log.info("Successfully " +
                        "applied balance change."))
                .doOnError(error -> log.error("Failed to apply change to account balance, {}"
                        , error.getLocalizedMessage()));
    }






}
