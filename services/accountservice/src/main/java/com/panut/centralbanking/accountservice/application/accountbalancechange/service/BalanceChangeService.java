package com.panut.centralbanking.accountservice.application.accountbalancechange.service;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BalanceChangeService {
    Mono<CommonResult> perTransaction(List<BalanceChangeData> datas,
                             boolean requiredResult);

}
