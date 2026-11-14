package com.panut.centralbanking.accountservice.api.request.serviceonly.transactioneventclearing;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetProcessedBalanceEventIdsRequest {
    @NotNull
    @NotEmpty
    private List<String> pendingEventIds;
}
