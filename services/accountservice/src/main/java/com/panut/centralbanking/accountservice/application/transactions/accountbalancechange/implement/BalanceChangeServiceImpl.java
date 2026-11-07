package com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.implement;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.accounts.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.AccountBalanceConnectionFactory;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.AccountBalanceRepositoryManager;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.query.AccountBalanceQuery;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeRecordService;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
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
              List<AccountBalanceChangedData> optional) {}

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class BalanceChangeServiceImpl implements BalanceChangeService {
    private final AccountQuery accountQuery;
    private final AccountBalanceQuery accountBalanceQuery;
    private final BalanceChangeRecordService recordService;
    private final AccountBalanceRepositoryManager manager;
    private final AccountBalanceConnectionFactory connectionFactory;

    public BalanceChangeServiceImpl(AccountQuery accountQuery,
                                    AccountBalanceQuery accountBalanceQuery,
                                    BalanceChangeRecordService recordService,
                                    AccountBalanceRepositoryManager manager,
                                    AccountBalanceConnectionFactory connectionFactory) {
        this.accountQuery = accountQuery;
        this.accountBalanceQuery = accountBalanceQuery;
        this.recordService = recordService;
        this.manager = manager;
        this.connectionFactory = connectionFactory;
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

                        BigDecimal totalBalanceDeduct = getTotalDeduct(filtered,
                                BalanceChangeType.BALANCE);
                        BigDecimal totalReserveDeduct = getTotalDeduct(filtered,
                                BalanceChangeType.RESERVE);



                        log.info("before orchest accountBalance {}",
                                accountBalance.getBalance());
                        log.info("before orchest filter size {}",
                                filtered.size());

                        boolean canSafeApply = accountBalance.canSafeApply(totalBalanceDeduct,
                                        totalReserveDeduct);
                        if (canSafeApply)
                            accountBalance.orchest(filtered);

                        log.info("Account {} can safely apply balance change: {}",
                                accountId, canSafeApply);


                        if (requiredResult) {
                            changeResult.after(accountBalance);
                            optionalResult.add(changeResult);
                            log.debug("Added balance change result.");
                        }

                        log.info("after orchest accountBalance {}",
                                accountBalance.getBalance());
                    });



                    return new Result(accountBalances.values().
                            stream().toList(), optionalResult);

                })
                .doOnNext(a -> log.info("Applied (unsaved) balance changes."))
                .flatMap(result ->
                        connectionFactory.batchUpdate(result.accountBalances())
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
