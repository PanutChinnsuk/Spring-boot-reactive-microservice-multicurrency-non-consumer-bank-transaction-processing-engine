package com.panut.centralbanking.transactionservice.api.service.internalaccount.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFxInternalAccountResponse {
    private String fxProfitInternalAccountId;
    private String fxClearingInternalAccountId;



}
