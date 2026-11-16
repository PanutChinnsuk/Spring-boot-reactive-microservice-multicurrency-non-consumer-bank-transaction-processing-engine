package com.panut.centralbanking.transactionservice.application.events.balancechange;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
@Getter
@AllArgsConstructor
public class ChangingBalance {
    private final BalanceImpact balanceImpact;
    private final BalanceChangeType balanceChangeType;







    public static ChangingBalance none() {
        return new ChangingBalance(BalanceImpact.NOTHING,
                BalanceChangeType.BALANCE);
    }

    public static ChangingBalance balanceDeduct() {
        return new ChangingBalance(BalanceImpact.DEDUCT,
                BalanceChangeType.BALANCE);
    }

    public static ChangingBalance reserveDeduct() {
        return new ChangingBalance(BalanceImpact.DEDUCT,
                BalanceChangeType.RESERVE);
    }

    public static ChangingBalance overdraftedDeduct() {
        return new ChangingBalance(BalanceImpact.DEDUCT,
                BalanceChangeType.OVERDRAFTED);
    }


    public static ChangingBalance balanceAdd() {
        return new ChangingBalance(BalanceImpact.ADD,
                BalanceChangeType.BALANCE);
    }

    public static ChangingBalance reserveAdd() {
        return new ChangingBalance(BalanceImpact.ADD,
                BalanceChangeType.RESERVE);
    }

    public static ChangingBalance overdraftedAdd() {
        return new ChangingBalance(BalanceImpact.ADD,
                BalanceChangeType.OVERDRAFTED);
    }

}
