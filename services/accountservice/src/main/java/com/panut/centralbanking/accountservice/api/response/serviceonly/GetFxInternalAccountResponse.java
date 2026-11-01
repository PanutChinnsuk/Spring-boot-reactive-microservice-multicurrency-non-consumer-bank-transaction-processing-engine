package com.panut.centralbanking.accountservice.api.response.serviceonly;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.accountservice.core.InternalAccountType;
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


    @JsonIgnore
    private static InternalAccountType profit = InternalAccountType.FX_PROFIT;

    @JsonIgnore
    private static InternalAccountType clearing = InternalAccountType.FX_CLEARING;


    @JsonIgnore
    private static Set<InternalAccountType> fxResponsePolicy = Set.of(
            profit,
            clearing
    );


    @JsonIgnore
    public static GetFxInternalAccountResponse withMap(final
                                                       Map<InternalAccountType,String> map) {
        return new GetFxInternalAccountResponse(
                map.get(profit),
                map.get(clearing)
        );
    }
}
