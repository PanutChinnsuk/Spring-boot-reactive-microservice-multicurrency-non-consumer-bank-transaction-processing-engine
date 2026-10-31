package com.panut.centralbanking.accountservice.application.property;

import com.panut.centralbanking.accountservice.core.InternalBranch;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.internal.accounts")
@Getter
@Setter
public class InternalAccountProperty {
    private InternalBranch currentBankBranch;
}
