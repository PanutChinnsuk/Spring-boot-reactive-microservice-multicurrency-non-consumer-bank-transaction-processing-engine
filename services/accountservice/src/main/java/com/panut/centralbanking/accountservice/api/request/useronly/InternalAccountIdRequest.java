package com.panut.centralbanking.accountservice.api.request.useronly;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalAccountIdRequest {
    @NotBlank
    private String accountId;
}
