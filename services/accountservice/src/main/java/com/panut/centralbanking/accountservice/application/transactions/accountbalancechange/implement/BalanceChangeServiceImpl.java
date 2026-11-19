package com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.implement;

import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.AccountBalanceConnectionFactory;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.query.AccountBalanceQuery;
import com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.AccountOverdraftConnectionFactory;
import com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.query.AccountOverdraftQuery;
import com.panut.centralbanking.accountservice.application.events.balancechange.ProcessedBalanceChangeEventConnectionFactory;
import com.panut.centralbanking.accountservice.application.events.balancechange.query.ProcessedBalanceChangeEventQuery;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeRecordService;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.application.worker.WorkerService;
import com.panut.centralbanking.accountservice.core.WorkerName;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.appR2dbccore.data.MapListSorter;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.CriticalFailureException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


record Result(Map<String, AccountBalance> accountBalances,
              Chain chain,
              List<AccountBalanceChangedData> optional,
              List<AccountOverdraftCreateData> possibleOverdrafts) {}

@Slf4j
@Getter
@NoArgsConstructor
class Chain {
    private Map<String, AccountOverdraft> overdraftMap;
    private Map<String, List<AccountBalanceChange>> withOverdraftContext = new HashMap<>();
    private Map<String, List<AccountBalanceChange>> plain = new HashMap<>();
    private Map<String, List<String>> eventIdsPerAccountId = new HashMap<>();
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



    public Mono<Chain> put(final List<AccountBalanceChange> all) {
        return Mono.defer(() -> {
            log.info("mapping unsorted account balance change records " +
                    "into map<accountId, List<AccountBalanceChange>> maps");

            all.forEach(record -> {
                var eventId = record.getEventId();
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
                MapListSorter.sortMapList(eventIdsPerAccountId,
                        key,
                        eventId);
            });

            log.info("after mapped unsorted, withOverdraftContext size: {}",
                    withOverdraftContext.size());
            log.info("after mapped unsorted, plain size: {}",
                    plain.size());
            log.info("after mapped unsorted, eventIdsPerAccountId size: {}",
                    eventIdsPerAccountId.size());

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
    private final ProcessedBalanceChangeEventConnectionFactory
    processedBalanceChangeEventConnectionFactory;
    private final ProcessedBalanceChangeEventQuery processedBalanceChangeEventQuery;
    private final WorkerService workerService;


    public BalanceChangeServiceImpl(AccountBalanceQuery accountBalanceQuery,
                                    BalanceChangeRecordService recordService,
                                    AccountBalanceConnectionFactory connectionFactory,
                                    AccountOverdraftConnectionFactory overdraftConnectionFactory,
                                    AccountOverdraftQuery accountOverdraftQuery,
                                    ProcessedBalanceChangeEventConnectionFactory processedBalanceChangeEventConnectionFactory,
                                    ProcessedBalanceChangeEventQuery processedBalanceChangeEventQuery,
                                    WorkerService workerService) {
        this.accountBalanceQuery = accountBalanceQuery;
        this.recordService = recordService;
        this.connectionFactory = connectionFactory;
        this.overdraftConnectionFactory = overdraftConnectionFactory;
        this.accountOverdraftQuery = accountOverdraftQuery;
        this.processedBalanceChangeEventConnectionFactory = processedBalanceChangeEventConnectionFactory;
        this.processedBalanceChangeEventQuery = processedBalanceChangeEventQuery;
        this.workerService = workerService;
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
                            changeResult.after(accountBalance, pendingOverdrafts);
                            optionalResult.add(changeResult);
                            log.debug("Added balance change result.");
                        }

                        log.info("after orchest accountBalance {}",
                                accountBalance.getBalance());
                    });



                    return new Result(accountBalances,
                            chain,
                            optionalResult,
                            pendingOverdrafts);

                })
                .doOnNext(a -> log.info("Applying balance changes."))
                .flatMap(result ->
                        connectionFactory.batchUpdateMultirow(result.accountBalances()
                                        .values().stream().toList())
                                .doOnNext(a -> log.info("batch update account balance"))
                                .then(overdraftConnectionFactory.batchInsertMultirow(
                                        result.possibleOverdrafts()))
                                .doOnNext(a -> log.info("batch insert possible overdrafts"))
                                .then(overdraftConnectionFactory.batchUpdate(
                                        result.chain().getOverdraftMap().values()
                                                .stream().toList()
                                ))
                                .doOnNext(a -> log.info("batch update possible overdraft changes"))
                                .thenReturn(result))
                .flatMap(result -> accountBalanceQuery
                        .getTransactionAccountBalances(result.chain()
                                .getAccountIds())
                        .map(postUpdated -> {
                            log.info("making sure account balance is updated");
                            var fromChain = result.chain();
                            List<String> clearedEventIds = new ArrayList<>();
                            Map<String, List<String>> myEvents =
                                    fromChain.getEventIdsPerAccountId();
                            Map<String, AccountBalance> changedButPreUpdate =
                                    result.accountBalances();

                            changedButPreUpdate.forEach((key, old) -> {
                                log.info("Verifying entity correction");
                                AccountBalance updated = postUpdated.get(key);
                                List<String> events = myEvents.get(key);

                                if (updated == null)
                                    throw new IllegalStateException(
                                            "Cannot get updated account balance to check with old, " +
                                                    "due to not found."
                                    );

                                if (events == null || events.isEmpty())
                                    throw new IllegalStateException(
                                            "Cannot get events that affected account balance to check correction"
                                    );

                                if (old.comparedNew(updated)) {
                                    clearedEventIds
                                            .addAll(events);
                                }else {
                                    throw new CriticalFailureException(
                                            "Updated account balance id, " + updated.getId() +
                                                    " has mismatched balances values, aborting operation."
                                    );
                                }
                            });

                            log.info("Total events cleared: {}",
                                    clearedEventIds.size());


                            return clearedEventIds;
                        })
                        .doOnNext(a -> log.info("Adding processed balance change events."))
                        .flatMap(clearedEventIds ->
                                processedBalanceChangeEventConnectionFactory
                                        .batchInsertMultirow(clearedEventIds)
                                        .thenReturn(clearedEventIds))
                        .flatMap(clearedEventIds -> processedBalanceChangeEventQuery
                                .getAlreadyProcessedEventIds(clearedEventIds)
                                .map(processed -> {
                                    log.info("Verifying processed events");
                                    if (!processed.containsAll(clearedEventIds))
                                        throw new CriticalFailureException("Processed event ids " +
                                                "from database does not contain previous event ids " +
                                                "that has affected account balances.");

                                    return processed;
                                })
                        )

                        .thenReturn(result)

                )

                .map(result -> new CommonResult(
                        "Successfully applied balance changes",
                        result.optional()
                ))
                .doOnNext(a -> log.info(
                        "Saved all transaction's account balances."
                ));

    }


    @Override
    public Mono<CommonResult> bulkDirect(final List<BalanceChangeData> datas,
                                   final boolean requiredResult) {
        return workerService.initWorker(WorkerName.TRANSACTION)
                .then(bulk(datas, requiredResult));
    }

    @Override
    public Mono<CommonResult> bulk(final List<BalanceChangeData> datas,
                                   final boolean requiredResult) {
        return excludeAlreadyProcessedEventIds(datas)
                .flatMap(safeDatas -> {
                    if (safeDatas.isEmpty()) {
                        log.info("safeDatas is empty, skipping the entire bulk method");
                        return Mono.empty();
                    }


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
                });


    }

    private Mono<List<BalanceChangeData>> excludeAlreadyProcessedEventIds(
            final List<BalanceChangeData> datas) {
        return Mono.defer(() -> {
            List<String> eventIds = datas
                    .stream()
                    .map(BalanceChangeData::getEventId).toList();

            return Mono.just(eventIds);
        })
                .flatMap(eventIds -> processedBalanceChangeEventQuery
                        .getAlreadyProcessedEventIds(eventIds)
                        .doOnNext(a ->
                                log.info("after get already processed event ids"))
                )
                .map(processed -> {
                    List<BalanceChangeData> checkedList = new ArrayList<>(datas);

                    log.info("checkedList size: {}, before excluding already processed event ids",
                            checkedList.size());
                    for (int i = 0; i < checkedList.size(); i++) {
                        var event = checkedList.get(i);
                        var eventId = event.getEventId();

                        if (eventId == null ||
                                processed.contains(eventId)) {
                            log.info("event id, {} is already processed ? {} and event id exist ? {}, removing from the list.",
                                    event, eventId != null, event != null);
                            checkedList.remove(i);
                            --i;
                        }

                    }

                    log.info("checkedList size: {}, after excluding already processed event ids",
                            checkedList.size());

                    return checkedList;
                });

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
                                .withEventId(data.getEventId())
                ).toList();
    }




}
