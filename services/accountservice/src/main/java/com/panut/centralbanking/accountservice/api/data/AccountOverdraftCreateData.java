package com.panut.centralbanking.accountservice.api.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOverdraftCreateData {
    @NotBlank
    private String accountId;

    @Positive
    @NotNull
    private BigDecimal amount;

    private LocalDateTime overdraftedAt = LocalDateTime.now();

    private String transactionId;
}
