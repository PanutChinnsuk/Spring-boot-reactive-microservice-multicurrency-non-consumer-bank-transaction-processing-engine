package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre;

import com.panut.centralbanking.transactionservice.core.TransactionType;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
public class PreTransactionRegisterData {
    private final TransactionType transactionType;
    private final List<AccountData> accountDatas;
    private final Set<String> usingAccountIds;
    private final FxRateData fxRateData;




    public PreTransactionRegisterData(TransactionType transactionType,
                                      List<AccountData> accountDatas,
                                      FxRateData fxRateData) {
        this.transactionType = transactionType;
        this.accountDatas = accountDatas;
        this.usingAccountIds = accountDatas.stream()
                .map(AccountData::getAccountId).collect(Collectors.toSet());

        this.fxRateData = fxRateData;
    }


    public FxRateData getFxRateData() {
        return fxRateData;
    }
}
