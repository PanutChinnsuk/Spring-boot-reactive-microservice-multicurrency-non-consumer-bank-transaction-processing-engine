package com.panut.centralbanking.transactionservice.integration.transaction.isolation.orchest;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.BalanceChangeEventISO;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.OverdraftContentISO;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.TransactionISO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionCompletionCycleISO {
    @Autowired
    private TransactionISO transactionISO;

    @Autowired
    private OverdraftContentISO overdraftContentISO;

    @Autowired
    private BalanceChangeEventISO eventISO;


    private String nonAssetExpenseLedger = SeededDatabase.ID_LEDGER_ACCOUNT_NO_ASSET_EXPENSE;
    private String assetLedger = SeededDatabase.ID_LEDGER_ACCOUNT_ASSET;
    private String expenseLedger = SeededDatabase.ID_LEDGER_ACCOUNT_EXPENSE;


    public Transaction noAssetExpenseCase(String addingAcocunt,
                                          String deductingAccount,
                                          TransactionStatus status,
                                          BigDecimal amount) {
        var tx = transactionISO.transaction(TransactionType.TRANSFER, status);
        var tp = transactionISO.transactionProfile(tx);



        transactionISO.transactionLine(tx, deductingAccount, amount,
                amount, TransactionLineDirection.DEBIT,
                nonAssetExpenseLedger);

        transactionISO.transactionLine(tx, deductingAccount, amount,
                amount, TransactionLineDirection.DEBIT,
                nonAssetExpenseLedger);

        transactionISO.transactionLine(tx, addingAcocunt, amount.add(amount),
                amount.add(amount), TransactionLineDirection.CREDIT,
                nonAssetExpenseLedger);


        return tx;
    }

    public Transaction assetExpenseCase(String addingAcocunt,
                                          String deductingAccount,
                                          TransactionStatus status,
                                          BigDecimal amount) {
        var tx = transactionISO.transaction(TransactionType.ASSET_EXPENSE, status);
        var tp = transactionISO.transactionProfile(tx);



        transactionISO.transactionLine(tx, deductingAccount, amount,
                amount, TransactionLineDirection.CREDIT,
                assetLedger);

        transactionISO.transactionLine(tx, deductingAccount, amount,
                amount, TransactionLineDirection.CREDIT,
                assetLedger);

        transactionISO.transactionLine(tx, addingAcocunt, amount.add(amount),
                amount.add(amount), TransactionLineDirection.DEBIT,
                expenseLedger);


        return tx;
    }

    public Transaction overdraftCase(String addingAcocunt,
                                          String deductingAccount,
                                     TransactionStatus status,
                                     BigDecimal amount) {
        var tx = transactionISO.transaction(TransactionType.OVERDRAFT_PAYMENT, status);
        var tp = transactionISO.transactionProfile(tx);


        transactionISO.transactionLine(tx, deductingAccount, amount,
                amount, TransactionLineDirection.DEBIT_OVERDRAFT,
                nonAssetExpenseLedger);

        transactionISO.transactionLine(tx, addingAcocunt, amount,
               amount, TransactionLineDirection.CREDIT,
                nonAssetExpenseLedger);


        return tx;
    }


    public TransactionProfile reverseWithUnsettledChildCase() {
        var tx = transactionISO.transaction();
        var tp = transactionISO.transactionProfile(tx);


        var reversedTx = transactionISO.transaction();
        var reversedTp = transactionISO.transactionProfile(reversedTx,
                tx);


        return reversedTp;
    }
}
