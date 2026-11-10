package com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.implement;

import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.accounts.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.AccountBalanceConnectionFactory;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.AccountBalanceRepositoryManager;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.query.AccountBalanceQuery;
import com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.AccountOverdraftConnectionFactory;
import com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.query.AccountOverdraftQuery;
import com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.service.AccountOverdraftService;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeRecordService;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.appR2dbccore.data.MapListSorter;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


record Result(List<AccountBalance> accountBalances,
              List<AccountOverdraft> accountOverdrafts,
              List<AccountBalanceChangedData> optional,
              List<AccountOverdraftCreateData> possibleOverdrafts) {}

@Slf4j
@Getter
@NoArgsConstructor
class Chain {
    private Map<String, AccountOverdraft> overdraftMap;
    private Map<String, List<AccountBalanceChange>> withOverdraftContext = new HashMap<>();
    private Map<String, List<AccountBalanceChange>> plain = new HashMap<>();
    private List<String> accountOverdraftIdsFromContexts = new ArrayList<>();
    private Set<String> accountIds = new HashSet<>();


    public Mono<Chain> overdraftMap
            (final List<AccountOverdraft> accountOverdrafts) {
        return Mono.defer(() -> {
            log.info("mapping unsorted account overdrafts records " +
                    "into map<AccountOverdraft>");

            overdraftMap = accountOverdrafts
                    .stream()
                            .collect(Collectors.toMap(
                                    AccountOverdraft::getId,
                                    Function.identity()
                            ));

            log.info("after mapped unsorted, overdraftMap size: {}",
                    overdraftMap.size());

            return Mono.just(this);
        });
    }



    public Mono<Chain> put
            (final List<AccountBalanceChange> all) {
        return Mono.defer(() -> {
            log.info("mapping unsorted account balance change records " +
                    "into map<accountId, List<AccountBalanceChange>> maps");

            all.forEach(record -> {
                String key = record.getAccountId();

                accountIds.add(key);

                if (record.hasOverdraftChangeContext()) {
                    MapListSorter.sortMapList(withOverdraftContext,
                            key,
                            record);
                    accountOverdraftIdsFromContexts.addAll(
                            record.getOverdraftChangeContext().getAccountOverdraftIds()
                    );
                    log.info("to withOverdraftContext and included account overdraft ids " +
                            "for future query");
                }else {
                    MapListSorter.sortMapList(plain,
                            key,
                            record);
                    log.info("to plain");
                }
            });

            log.info("after mapped unsorted, withOverdraftContext size: {}",
                    withOverdraftContext.size());
            log.info("after mapped unsorted, plain size: {}",
                    plain.size());

            return Mono.just(this);
        });
    }

    private List<AccountBalanceChange> getFromMap(final Map<String, List<AccountBalanceChange>> map,
                                                  String key) {
        var value = map.get(key);
        if (value == null)
            return Collections.emptyList();

        return value
                .stream()
                .sorted(Comparator.comparingInt(record ->
                        record.getBalanceImpact().getAscendingPriority()))
                .toList();
    }


    public List<AccountBalanceChange> getFromPlain(final String key) {
        return getFromMap(plain, key);
    }

    public List<AccountBalanceChange> getFromWithOverdraftContext(final String key) {
        return getFromMap(withOverdraftContext, key);
    }
}

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class BalanceChangeServiceImpl implements BalanceChangeService {

    private final AccountBalanceQuery accountBalanceQuery;
    private final BalanceChangeRecordService recordService;
    private final AccountBalanceConnectionFactory connectionFactory;
    private final AccountOverdraftConnectionFactory overdraftConnectionFactory;
    private final AccountOverdraftQuery accountOverdraftQuery;

    public BalanceChangeServiceImpl(AccountBalanceQuery accountBalanceQuery,
                                    BalanceChangeRecordService recordService,
                                    AccountBalanceConnectionFactory connectionFactory,
                                    AccountOverdraftConnectionFactory overdraftConnectionFactory,
                                    AccountOverdraftQuery accountOverdraftQuery) {
        this.accountBalanceQuery = accountBalanceQuery;
        this.recordService = recordService;
        this.connectionFactory = connectionFactory;
        this.overdraftConnectionFactory = overdraftConnectionFactory;
        this.accountOverdraftQuery = accountOverdraftQuery;
    }



    private Mono<CommonResult> map(final Chain chain,
                                   boolean requiredResult) {
        return accountBalanceQuery.findTransactionAccountBalances(
                chain.getAccountIds())
                .doOnNext(a -> log.info("Created account balances " +
                        "mono chain."))
                .map(accountBalances -> {
                    List<AccountBalanceChangedData> optionalResult = new ArrayList<>();
                    List<AccountOverdraftCreateData> pendingOverdrafts = new ArrayList<>();

                    accountBalances.keySet().forEach(accountId -> {
                        AccountBalanceChangedData changeResult = null;
                        AccountBalance accountBalance = accountBalances
                                .get(accountId);
                        if (requiredResult) {
                            log.debug("Method return required balance change " +
                                    "result.");
                            changeResult = new AccountBalanceChangedData();
                            changeResult.before(accountBalance);
                        }



                        var filteredPlain = chain.getFromPlain(accountId);
                        var filteredWithOverdraftContext = chain
                                .getFromWithOverdraftContext(accountId);





                        log.info("before orchest accountBalance {}",
                                accountBalance.getBalance());
                        log.info("before orchest filtered plain size {}",
                                filteredPlain.size());
                        log.info("before orchest filtered with overdraft context size {}",
                                filteredWithOverdraftContext.size());

                        var possibleOverdrafts = accountBalance
                                .orchest(filteredPlain, accountId);
                        var possibleRollbackOverdrafts = accountBalance
                                .orchestWithOverdraftContext(filteredWithOverdraftContext,
                                        chain.getOverdraftMap(),
                                        accountId);

                        if (!possibleOverdrafts.isEmpty())
                            pendingOverdrafts.addAll(possibleOverdrafts);
                        if (!possibleRollbackOverdrafts.isEmpty())
                            pendingOverdrafts.addAll(possibleRollbackOverdrafts);


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
                            chain.getOverdraftMap().values()
                                    .stream().toList(),
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
    public Mono<CommonResult> bulk(final List<BalanceChangeData> datas,
                                   final boolean requiredResult) {
        return recordService.create(datasToRecords(datas))
                .flatMap(addedRecords ->
                        new Chain().put(addedRecords)
                )
                .flatMap(chain -> accountOverdraftQuery
                        .findAccountOverdrafts(chain.getAccountOverdraftIdsFromContexts())
                        .flatMap(chain::overdraftMap)
                )
                .flatMap(chain -> map(chain, requiredResult))
                .doOnSuccess(a -> log.info("Successfully " +
                        "applied balance change."))
                .doOnError(error -> log.error("Failed to apply change to account balance, {}"
                        , error.getLocalizedMessage()));
    }

    private List<AccountBalanceChange> datasToRecords(final List<BalanceChangeData> datas) {
        return datas.stream()
                .map(data ->
                        AccountBalanceChange.builder()
                                .accountId(data.getAccountId())
                                .changedAt(data.getCreatedAt())
                                .amount(data.getAmount())
                                .balanceImpact(data.getBalanceImpact())
                                .balanceChangeType(data.getBalanceChangeType())
                                .transactionId(data.getTransactionId())
                                .build()
                                .withOverdraftChangeContext(data.getOverdraftChangeContext())
                ).toList();
    }




}
