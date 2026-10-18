package com.panut.centralbanking.accountservice.api.data;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Getter
public class AccountBalanceCreateData {
    private BigDecimal defaultSeededBalance = BigDecimal.ZERO;
}
