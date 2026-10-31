package com.panut.centralbanking.accountservice.api.request.useronly;

import com.panut.centralbanking.accountservice.core.InternalAccountType;
import com.panut.centralbanking.accountservice.core.InternalBranch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalAccountAddRequest {
    @NotBlank
    private String accountId;

    @NotNull
    private InternalBranch branch;

    @NotNull
    private InternalAccountType internalAccountType;
}
