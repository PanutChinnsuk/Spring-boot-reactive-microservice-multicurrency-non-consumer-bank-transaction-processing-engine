package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.ledger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomTransferLedgerPolicy {
    @NotNull
    private TransferLedgerPolicyEnum policyEnum;

    @Nullable
    private String storedAccountId;

    @Nullable
    private String storedId;

    @JsonIgnore
    public CustomTransferLedgerPolicy(TransferLedgerPolicyEnum policyEnum) {
        this.policyEnum = policyEnum;
    }

    @JsonIgnore
    public CustomTransferLedgerPolicy withAccountId(final String  accountId) {
        this.storedAccountId = accountId;

        return this;
    }
}
