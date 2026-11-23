package com.panut.centralbanking.transactionservice.integration.transaction.isolation.orchest;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.BalanceChangeEventISO;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.OverdraftContentISO;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.TransactionISO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class TransactionCancellationISO {
    @Autowired
    private TransactionISO transactionISO;

    @Autowired
    private OverdraftContentISO overdraftContentISO;

    @Autowired
    private BalanceChangeEventISO balanceChangeEventISO;


    private String test = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    private String test2 = SeededDatabase.ID_NORMAL_ACCOUNT_2;
    private String internal = SeededDatabase.ID_INTERNAL_ACCOUNT_1;
    private String internal2 = SeededDatabase.ID_INTERNAL_ACCOUNT_2;
    private String empty = SeededDatabase.ID_EMPTY_ACCOUNT_1;

    public Transaction caseNoProc() {
        var tx = transactionISO.transaction();
        var prof = transactionISO.transactionProfile(tx);

        balanceChangeEventISO.balanceChangeEvent(tx,
                SeededDatabase.ID_NORMAL_ACCOUNT_1,
                BigDecimal.TEN,
                BalanceImpact.DEDUCT,
                BalanceChangeType.BALANCE);


        return tx;
    }

    public List<String> nestedReversingCase(Transaction original) {
        var r1 = transactionISO.transaction();
        var r1p = transactionISO.transactionProfile(r1, original);

        var r2 = transactionISO.transaction();
        var r2p = transactionISO.transactionProfile(r2, r1);

        return List.of(r2.getId(),
                r1.getId());
    }

    public Transaction worstCaseWithEverything() {
        var tx = transactionISO.transaction();
        var prof = transactionISO.transactionProfile(tx);
        log.info("worstCaseWithEverything tx {} {}",
                tx.getId(), prof.getId());

        var completed = BalanceChangeEventStatus.COMPLETED;
        var pending = BalanceChangeEventStatus.PENDING;


        balanceChangeEventISO.balanceChangeEvent(tx,
                internal,
                BigDecimal.TEN,
                BalanceImpact.ADD,
                BalanceChangeType.RESERVE,
                completed);


        balanceChangeEventISO.balanceChangeEvent(tx,
                internal2,
                BigDecimal.TEN,
                BalanceImpact.DEDUCT,
                BalanceChangeType.RESERVE,
                completed);


        balanceChangeEventISO.balanceChangeEvent(tx,
                test,
                BigDecimal.valueOf(50),
                BalanceImpact.DEDUCT,
                BalanceChangeType.BALANCE,
                completed);


        balanceChangeEventISO.balanceChangeEvent(tx,
                test,
                BigDecimal.valueOf(50),
                BalanceImpact.ADD,
                BalanceChangeType.RESERVE,
                completed);


        balanceChangeEventISO.balanceChangeEvent(tx,
                test,
                BigDecimal.valueOf(50),
                BalanceImpact.DEDUCT,
                BalanceChangeType.OVERDRAFTED,
                completed);


        ///
        balanceChangeEventISO.balanceChangeEvent(tx,
                test,
                BigDecimal.valueOf(7),
                BalanceImpact.ADD,
                BalanceChangeType.RESERVE,
                pending);

        ///
        balanceChangeEventISO.balanceChangeEvent(tx,
                test,
                BigDecimal.valueOf(5),
                BalanceImpact.DEDUCT,
                BalanceChangeType.BALANCE,
                pending);


        balanceChangeEventISO.balanceChangeEvent(tx,
                test2,
                BigDecimal.valueOf(100),
                BalanceImpact.ADD,
                BalanceChangeType.BALANCE,
                completed);


        balanceChangeEventISO.balanceChangeEvent(tx,
                test2,
                BigDecimal.valueOf(5),
                BalanceImpact.ADD,
                BalanceChangeType.OVERDRAFTED,
                completed);


        balanceChangeEventISO.balanceChangeEvent(tx,
                test2,
                BigDecimal.valueOf(5),
                BalanceImpact.ADD,
                BalanceChangeType.RESERVE,
                completed);


        balanceChangeEventISO.balanceChangeEvent(tx,
                test2,
                BigDecimal.TEN,
                BalanceImpact.DEDUCT,
                BalanceChangeType.RESERVE,
                completed);

        ///
        balanceChangeEventISO.balanceChangeEvent(tx,
                test2,
                BigDecimal.valueOf(12),
                BalanceImpact.ADD,
                BalanceChangeType.OVERDRAFTED,
                pending);


        balanceChangeEventISO.balanceChangeEvent(tx,
                empty,
                BigDecimal.TEN,
                BalanceImpact.ADD,
                BalanceChangeType.RESERVE,
                completed);


        balanceChangeEventISO.balanceChangeEvent(tx,
                empty,
                BigDecimal.valueOf(5),
                BalanceImpact.DEDUCT,
                BalanceChangeType.RESERVE,
                completed);


        balanceChangeEventISO.balanceChangeEvent(tx,
                empty,
                BigDecimal.valueOf(500),
                BalanceImpact.DEDUCT,
                BalanceChangeType.BALANCE,
                completed);


        return tx;
    }





}
