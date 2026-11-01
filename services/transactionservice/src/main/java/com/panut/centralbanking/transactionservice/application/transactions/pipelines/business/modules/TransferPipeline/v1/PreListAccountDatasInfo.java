package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.TransferPipeline.v1;

import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;

import java.math.BigDecimal;

public record PreListAccountDatasInfo(AccountInfoForTransactionQueryResult fromAccountInfo,
                               AccountInfoForTransactionQueryResult toAccountInfo,
                               BigDecimal basedAmount,
                               String fromAccountLedgerId,
                               String toAccountLedgerId) {
}
