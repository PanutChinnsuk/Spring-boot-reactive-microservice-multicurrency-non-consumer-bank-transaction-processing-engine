package com.panut.centralbanking.accountservice.api.data;


import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Getter
public class AccountBalanceCreateData {
    @PositiveOrZero
    private BigDecimal defaultSeededBalance = BigDecimal.ZERO;
}
