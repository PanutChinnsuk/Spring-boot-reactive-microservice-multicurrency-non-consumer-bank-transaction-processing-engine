package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.LedgerCode;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.CustomPerAccountTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomInternalAccountProfitContext;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomAccountTransferData {
    @NotBlank
    private String accountId;

    @NotNull
    @Positive
    private BigDecimal baseAmount;

    @Nullable
    @Positive
    private BigDecimal customConvertedAmount;

    @NotNull
    private LedgerCode ledgerCode;

    @NotNull
    private TransactionLineDirection direction;

    @Nullable
    private String optionalUniqueIdentityKey;

    private CustomAccountTransferDataFxContext fxContext;
    private CustomInternalAccountProfitContext profitContext;

    private Set<CustomPerAccountTransferDataPolicy> perAccountPolicies;

    public CustomAccountTransferData(String accountId, BigDecimal baseAmount,
                                     BigDecimal customConvertedAmount,
                                     LedgerCode ledgerCode,
                                     TransactionLineDirection direction,
                                     CustomAccountTransferDataFxContext fxContext,
                                     CustomInternalAccountProfitContext profitContext) {
        this.accountId = accountId;
        this.baseAmount = baseAmount;
        this.customConvertedAmount = customConvertedAmount;
        this.ledgerCode = ledgerCode;
        this.direction = direction;
        this.fxContext = fxContext;
        this.profitContext = profitContext;
    }

    @JsonIgnore
    public CustomAccountTransferData copyFrom(BigDecimal baseAmount) {
        return new CustomAccountTransferData(accountId, baseAmount,
                null, ledgerCode,
                direction, fxContext, profitContext);
    }

    @Nullable
    @JsonIgnore
    public BigDecimal getCustomConvertedAmount() {
        if (customConvertedAmount == null)
            throw new SystemMisused("customConvertedAmount is not set from request " +
                    "before doing internal transfer.");

        return customConvertedAmount;
    }

    @JsonIgnore
    public boolean isCustomizedCovertedAmount() {
        return customConvertedAmount != null;
    }

    @JsonIgnore
    public CustomAccountTransferData expectedNotNoneFxDirection() {
        if (fxContext.getDirectionForFx().equals(FxDirection.NONE))
            throw new BusinessException("CustomAccountTransferData direction for fx expected " +
                    "not to be NONE but current is NONE.");

        return this;
    }

    @JsonIgnore
    public CustomAccountTransferData withPerAccountPolicies(Set<CustomPerAccountTransferDataPolicy> perAccountPolicies) {
        this.perAccountPolicies = perAccountPolicies;

        return this;
    }

    @JsonIgnore
    public CustomAccountTransferData withFxContext(CustomAccountTransferDataFxContext fxContext) {
        this.fxContext = fxContext;

        return this;
    }

    @JsonIgnore
    public CustomAccountTransferData withProfitContext(CustomInternalAccountProfitContext
                                                       profitContext) {
        this.profitContext = profitContext;

        return this;
    }

    @JsonIgnore
    public boolean hasFxContext() {
        return this.fxContext != null;
    }

    @JsonIgnore
    public boolean hasProfitContext() {
        return this.profitContext != null;
    }


    @Nullable
    @JsonIgnore
    public String uniqueIdentityKey() {
        return optionalUniqueIdentityKey;
    }
}
