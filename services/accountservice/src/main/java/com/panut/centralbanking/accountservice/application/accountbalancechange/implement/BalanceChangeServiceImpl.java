package com.panut.centralbanking.accountservice.application.accountbalancechange.implement;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.account.query.result.TransactionAccounts;
import com.panut.centralbanking.accountservice.application.accountbalance.AccountBalanceRepositoryManager;
import com.panut.centralbanking.accountservice.application.accountbalance.query.AccountBalanceQuery;
import com.panut.centralbanking.accountservice.application.accountbalancechange.service.BalanceChangeRecordService;
import com.panut.centralbanking.accountservice.application.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;


record Result(Collection<AccountBalance> accountBalances,
              List<AccountBalanceChangedData> optional) {}

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class BalanceChangeServiceImpl implements BalanceChangeService {
    private final AccountQuery accountQuery;
    private final AccountBalanceQuery accountBalanceQuery;
    private final BalanceChangeRecordService recordService;
    private final AccountBalanceRepositoryManager manager;

    public BalanceChangeServiceImpl(AccountQuery accountQuery,
                                    AccountBalanceQuery accountBalanceQuery,
                                    BalanceChangeRecordService recordService,
                                    AccountBalanceRepositoryManager manager) {
        this.accountQuery = accountQuery;
        this.accountBalanceQuery = accountBalanceQuery;
        this.recordService = recordService;
        this.manager = manager;
    }




    private Mono<CommonResult> map(final TransactionAccounts transactionAccounts,
                                           final List<AccountBalanceChange> records,
                                           boolean requiredResult) {
        return accountBalanceQuery.findTransactionAccountBalances(
                transactionAccounts.accountIds())
                .doOnNext(a -> log.info("Created account balances " +
                        "mono chain."))
                .map(accountBalances -> {
                    List<AccountBalanceChangedData> optionalResult = new ArrayList<>();

                    transactionAccounts.accountIds()
                            .forEach(accountId -> {
                                AccountBalanceChangedData changeResult = null;
                                AccountBalance accountBalance = accountBalances
                                        .get(accountId);
                                if (requiredResult) {
                                    log.debug("Method return required balance change " +
                                            "result.");
                                    changeResult = new AccountBalanceChangedData();
                                    changeResult.before(accountBalance);
                                }

                                List<AccountBalanceChange> filter = records
                                        .stream().filter(record ->
                                                record.getAccountId().equals(accountId)).toList();

                                log.debug("before orchest accountBalance {}",
                                        accountBalance.getBalance());
                                log.debug("before orchest filter size {}",
                                        filter.size());
                                accountBalance.orchest(filter);
                                if (requiredResult) {
                                    changeResult.after(accountBalance);
                                    optionalResult.add(changeResult);
                                    log.debug("Added balance change result.");
                                }

                                log.debug("after orchest accountBalance {}",
                                        accountBalance.getBalance());
                            });

                    return new Result(accountBalances.values(), optionalResult);

                })
                .doOnNext(a -> log.info("Applied (unsaved) balance changes."))
                .flatMap(result ->
                        manager.saveAll(result.accountBalances())
                                .thenReturn(result))
                .map(result -> new CommonResult(
                        "Successfully applied balance changes",
                        result.optional()
                ))
                .doOnNext(a -> log.info(
                        "Saved all transaction's account balances."
                ));

    }

    private Mono<TransactionAccounts> preChange(final List<BalanceChangeData> datas) {
        return Mono.defer(() -> {
            Set<String> transactionAccountIds = datas.stream()
                    .map(BalanceChangeData::getAccountId)
                    .collect(Collectors.toSet());

            return accountQuery.findTransactionAccounts(transactionAccountIds);
        });
    }


    @Override
    public Mono<CommonResult> perTransaction(final List<BalanceChangeData> data,
                                    final boolean requiredResult) {
        return preChange(data)
                .flatMap(transaction -> recordService
                        .create(transaction.accountMap(), data)
                        .flatMap(addRecord ->
                                map(transaction, addRecord, requiredResult)))
                .doOnSuccess(a -> log.info("Successfully " +
                        "applied balance change."))
                .doOnError(error -> log.error("Failed to apply change to account balance, {}"
                        , error.getLocalizedMessage()));
    }



}
