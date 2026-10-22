package com.panut.centralbanking.accountservice.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyAccountsStateRequest {
    @NotNull
    @NotEmpty
    private Set<String> accountIds;
}
