package com.panut.centralbanking.accountservice.api.data;


import com.panut.centralbanking.accountservice.core.AccountType;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Data
public class AccountProfileCreateData {
    @NotNull
    private CurrencyCode currencyCode;

    @NotNull
    private AccountType accountType;




}
