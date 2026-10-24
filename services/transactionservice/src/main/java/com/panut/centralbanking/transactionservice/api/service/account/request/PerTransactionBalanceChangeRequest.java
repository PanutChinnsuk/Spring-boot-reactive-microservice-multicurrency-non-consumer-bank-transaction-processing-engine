package com.panut.centralbanking.transactionservice.api.service.account.request;



import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerTransactionBalanceChangeRequest {
    private List<BalanceChangeData> balanceChangeDatas;
}
