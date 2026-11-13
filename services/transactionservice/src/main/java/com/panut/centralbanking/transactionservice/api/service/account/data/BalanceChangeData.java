package com.panut.centralbanking.transactionservice.api.service.account.data;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceChangeData {
    private String eventId;
    private String accountId;
    private BigDecimal amount;
    private BalanceChangeType balanceChangeType;
    private BalanceImpact balanceImpact;
    private LocalDateTime createdAt;
    private String transactionId;
    private LocalDateTime blacklistExpiry;


    @JsonIgnore
    public static BalanceChangeData eventToRequest(final BalanceChangeEvent event) {
        return new BalanceChangeData(event.getId(),
                event.getAccountId(),
                event.getAmount(),
                event.getBalanceChangeType(),
                event.getBalanceImpact(),
                event.getCreatedAt(),
                event.getTransactionId(),
                event.getBlacklistExpiresAt());
    }


}
