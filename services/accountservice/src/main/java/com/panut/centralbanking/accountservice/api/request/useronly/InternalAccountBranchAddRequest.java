package com.panut.centralbanking.accountservice.api.request.useronly;

import com.panut.centralbanking.accountservice.core.InternalBranch;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalAccountBranchAddRequest {
    @NotNull
    private InternalBranch branch;
}
