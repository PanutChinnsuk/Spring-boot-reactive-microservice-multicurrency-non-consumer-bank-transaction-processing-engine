package com.panut.centralbanking.accountservice.application.accounts.internalaccounts.branch.service;

import com.panut.centralbanking.accountservice.api.request.useronly.InternalAccountBranchAddRequest;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


public interface BranchService {
    Mono<CommonResult> addBranch(final InternalAccountBranchAddRequest r);
}
