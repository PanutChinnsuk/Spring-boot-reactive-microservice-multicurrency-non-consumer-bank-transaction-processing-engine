package com.panut.centralbanking.accountservice.api.request.serviceonly;


import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerTransactionBalanceChangeRequest {
    @NotEmpty
    private List<@Valid BalanceChangeData> balanceChangeDatas;
}
