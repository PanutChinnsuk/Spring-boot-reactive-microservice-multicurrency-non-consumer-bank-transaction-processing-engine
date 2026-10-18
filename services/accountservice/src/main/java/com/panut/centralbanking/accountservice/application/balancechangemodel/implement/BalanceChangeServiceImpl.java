package com.panut.centralbanking.accountservice.application.balancechangemodel.implement;

import com.panut.centralbanking.accountservice.api.data.AccountBalanceCreateData;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.accountbalance.AccountBalanceRepositoryManager;
import com.panut.centralbanking.accountservice.application.accountbalance.query.AccountBalanceQuery;
import com.panut.centralbanking.accountservice.application.balancechangemodel.BalanceAddRepositoryManager;
import com.panut.centralbanking.accountservice.application.balancechangemodel.BalanceDeductRepositoryManager;
import com.panut.centralbanking.accountservice.application.balancechangemodel.service.BalanceAddCreateService;
import com.panut.centralbanking.accountservice.application.balancechangemodel.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.application.balancechangemodel.service.BalanceDeductCreateService;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceAdd;
import com.panut.centralbanking.accountservice.infrastructure.model.impact.BalanceChangeModel;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

record BalanceChain(AccountBalance accountBalance, AccountBalanceChangedData resultData) {}

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class BalanceChangeServiceImpl implements BalanceChangeService {
    private final AccountQuery accountQuery;
    private final AccountBalanceQuery accountBalanceQuery;
    private final BalanceAddCreateService addService;
    private final BalanceDeductCreateService deductService;
    private final AccountBalanceRepositoryManager manager;

    public BalanceChangeServiceImpl(AccountQuery accountQuery,
                                    AccountBalanceQuery accountBalanceQuery,
                                    BalanceAddCreateService addService,
                                    BalanceDeductCreateService deductService,
                                    AccountBalanceRepositoryManager manager) {
        this.accountQuery = accountQuery;
        this.accountBalanceQuery = accountBalanceQuery;
        this.addService = addService;
        this.deductService = deductService;
        this.manager = manager;
    }


    private Mono<Account> preChange(final List<BalanceChangeData> datas) {
        return Mono.defer(() -> {
            BalanceChangeData perAccount = datas.getFirst();
            if (datas.stream().anyMatch(any ->
                    !any.getAccountId().equals(perAccount.getAccountId())))
                return Mono.error(() -> new SystemMisused(
                        "List<BalanceChangeData> must have the" +
                                " same account id."
                ));


            return accountQuery.findGeneric(datas.getFirst().getAccountId());
        });
    }

    private Mono<CommonResult<AccountBalanceChangedData>> apply(final Account account,
                                                                final List<? extends BalanceChangeModel> perAccountRecords) {
        return accountBalanceQuery.findForBalanceChange(account.getId())
                .doOnNext(a -> log.info("Created account balance " +
                        "mono chain."))
                .flatMap(accountBalance ->
                        new AccountBalanceChangedData().before(accountBalance)
                                .map(balanceChangedData -> new BalanceChain(
                                        accountBalance, balanceChangedData))
                )
                .doOnNext(a -> log.info("Created Balance chain."))
                .flatMap(chain -> chain.accountBalance()
                        .orchest(perAccountRecords)
                        .flatMap(accountBalance -> chain
                                .resultData().after(accountBalance)).thenReturn(chain)
                )
                .doOnNext(a -> log.info("Applied (unsaved) balance change."))
                .flatMap(chain -> manager.save(chain.accountBalance())
                        .thenReturn(new CommonResult<>("Successfully applied change " +
                                "to account balance.", chain.resultData())))
                .doOnSuccess(a -> log.info("Successfully" +
                        "applied balance change."))
                .onErrorResume(error -> {
                    String errMsg = error.getLocalizedMessage();
                    log.error("Failed to apply change to account balance, {}", errMsg);
                    return Mono.just(new CommonResult<>(false, errMsg));
                });
    }


    @Override
    public Mono<CommonResult<AccountBalanceChangedData>> add(final List<BalanceChangeData> data) {
        return preChange(data)
                .flatMap(account -> addService.create(account, data)
                        .flatMap(addRecord -> apply(account,
                                addRecord)));
    }

    @Override
    public Mono<CommonResult<AccountBalanceChangedData>> deduct(final List<BalanceChangeData>  data) {
        return preChange(data)
                .flatMap(account -> deductService.create(account, data)
                        .flatMap(deductRecord -> apply(account,
                                deductRecord)));
    }

}
