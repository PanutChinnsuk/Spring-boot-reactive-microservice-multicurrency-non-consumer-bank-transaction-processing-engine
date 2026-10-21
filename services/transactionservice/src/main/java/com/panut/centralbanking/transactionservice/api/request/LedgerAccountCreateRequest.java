package com.panut.centralbanking.transactionservice.api.request;

import com.panut.centralbanking.transactionservice.core.LedgerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LedgerAccountCreateRequest {
    @NotNull
    private LedgerType ledgerType;

    @NotBlank
    private String code;

    @NotBlank
    private String name;
}
