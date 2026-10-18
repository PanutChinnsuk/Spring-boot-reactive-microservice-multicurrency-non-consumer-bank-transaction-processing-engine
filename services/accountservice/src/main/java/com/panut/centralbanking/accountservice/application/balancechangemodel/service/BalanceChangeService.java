package com.panut.centralbanking.accountservice.application.balancechangemodel.service;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BalanceChangeService {
    Mono<CommonResult<AccountBalanceChangedData>> add(List<BalanceChangeData> datas);
    Mono<CommonResult<AccountBalanceChangedData>> deduct(List<BalanceChangeData> data);
}
