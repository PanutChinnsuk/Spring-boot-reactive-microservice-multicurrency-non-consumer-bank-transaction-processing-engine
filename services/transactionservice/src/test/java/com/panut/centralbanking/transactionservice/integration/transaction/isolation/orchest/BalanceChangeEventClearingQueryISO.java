package com.panut.centralbanking.transactionservice.integration.transaction.isolation.orchest;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.BalanceChangeEventISO;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.OverdraftContentISO;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.TransactionISO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class BalanceChangeEventClearingQueryISO {
    @Autowired
    private BalanceChangeEventISO eventISO;

    @Autowired
    private TransactionISO transactionISO;

    @Autowired
    private OverdraftContentISO contentISO;

    private String account1 = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    private String accountOverdraftId1 = SeededDatabase.ID_EMPTY_ACCOUNT_OVERDRAFT_1;
    private String accountOverdraftId2 = SeededDatabase.ID_EMPTY_ACCOUNT_OVERDRAFT_2;

    public Transaction commonCase() {
        var tx = transactionISO.transaction();

        eventISO.balanceChangeEvent(tx);
        eventISO.balanceChangeEvent(tx);

        log.info("commonCase");

        return tx;
    }

    public Transaction overdraftCase() {
        var tx = transactionISO.transaction();

        eventISO.balanceChangeEvent(tx,
                account1,
                BigDecimal.ONE,
                BalanceImpact.ADD,
                BalanceChangeType.OVERDRAFTED);

        contentISO.transactionOverdraftContent(tx, accountOverdraftId1);
        contentISO.transactionOverdraftContent(tx, accountOverdraftId2);

        log.info("overdraftCase");



        return tx;
    }

}
