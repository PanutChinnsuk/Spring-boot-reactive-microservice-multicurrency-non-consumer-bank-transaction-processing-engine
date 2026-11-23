package com.panut.centralbanking.transactionservice.integration.transaction.isolation.orchest;

import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.BalanceChangeEventISO;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.TransactionISO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BalanceChangeEventClearingModuleISO {
    @Autowired
    private TransactionISO transactionISO;

    @Autowired
    private BalanceChangeEventISO eventISO;



}
