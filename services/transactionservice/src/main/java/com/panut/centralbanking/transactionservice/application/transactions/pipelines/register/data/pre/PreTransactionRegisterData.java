package com.panut.centralbanking.transactionservice.application.transactions.pipelines.register.data.pre;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
public class PreTransactionRegisterData {
    private final List<AccountData> accountDatas;
    private final Set<String> usingLedgerCodes;
    private final FxRateData fxRateData;

    public PreTransactionRegisterData(List<AccountData> accountDatas,
                                      FxRateData fxRateData) {
        this.accountDatas = accountDatas;
        this.usingLedgerCodes = this.accountDatas.stream()
                .map(AccountData::getLedgerCode).collect(Collectors.toSet());
        this.fxRateData = fxRateData;
    }






}
