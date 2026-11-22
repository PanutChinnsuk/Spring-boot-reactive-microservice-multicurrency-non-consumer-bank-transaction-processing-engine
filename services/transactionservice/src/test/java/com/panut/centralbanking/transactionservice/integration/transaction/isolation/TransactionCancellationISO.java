package com.panut.centralbanking.transactionservice.integration.transaction.isolation;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionCancellationISO {
    @Autowired
    private TransactionISO transactionISO;

    @Autowired
    private OverdraftContentISO overdraftContentISO;

    @Autowired
    private BalanceChangeEventISO balanceChangeEventISO;



    public Transaction caseNoProc() {
        var tx = transactionISO.transaction();
        var prof = transactionISO.transactionProfile(tx);

        balanceChangeEventISO.balanceChangeEvent(tx,
                SeededDatabase.ID_NORMAL_ACCOUNT_1,
                BigDecimal.TEN,
                BalanceImpact.DEDUCT,
                BalanceChangeType.BALANCE);
        balanceChangeEventISO.balanceChangeEvent(tx,
                SeededDatabase.ID_NORMAL_ACCOUNT_2,
                BigDecimal.TEN,
                BalanceImpact.ADD,
                BalanceChangeType.BALANCE);

        return tx;
    }


}
