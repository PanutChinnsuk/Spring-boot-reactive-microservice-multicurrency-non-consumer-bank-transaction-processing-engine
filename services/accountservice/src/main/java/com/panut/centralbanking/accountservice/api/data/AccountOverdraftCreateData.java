package com.panut.centralbanking.accountservice.api.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
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

    @JsonIgnore
    public AccountOverdraftCreateData addOverdraftAmount(final BigDecimal amount) {
        this.amount = this.amount.add(amount);

        return this;
    }

    @JsonIgnore
    public static AccountOverdraftCreateData createDefault(final String accountId,
                                                           final BigDecimal amount,
                                                           final @Nullable String transactionId) {
        return new AccountOverdraftCreateData(accountId, amount,
                LocalDateTime.now(), transactionId);
    }
}
