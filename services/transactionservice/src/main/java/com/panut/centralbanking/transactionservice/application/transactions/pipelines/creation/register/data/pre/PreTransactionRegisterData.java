package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre;

import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
public class PreTransactionRegisterData {
    private final List<AccountData> accountDatas;
    private final Set<String> usingLedgerCodes;
    private final Set<String> usingAccountIds;

    private final FxRateData fxRateData;




    public PreTransactionRegisterData(List<AccountData> accountDatas,
                                      FxRateData fxRateData) {
        this.accountDatas = accountDatas;
        this.usingLedgerCodes = this.accountDatas.stream()
                .map(AccountData::getLedgerCode).collect(Collectors.toSet());
        this.usingAccountIds = accountDatas.stream()
                .map(AccountData::getAccountId).collect(Collectors.toSet());

        this.fxRateData = fxRateData;
    }







}
