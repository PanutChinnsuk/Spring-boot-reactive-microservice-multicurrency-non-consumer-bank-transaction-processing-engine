package com.panut.centralbanking.accountservice.integration.isolation;

import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountOverdraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class AccountOverdraftISO {
    @Autowired
    private AccountISO accountISO;

    @Autowired
    private AccountOverdraftRepository repository;

    public static BigDecimal twiceOverdraftedSize = BigDecimal
            .valueOf(2);

    public AccountOverdraft accountOverdraft(Account account,
                                             BigDecimal amount,
                                             BigDecimal paid) {
        return repository.save(AccountOverdraft.builder()
                .accountId(account.getId())
                .overdraftedAt(LocalDateTime.now())
                .amount(amount)
                .paid(paid)
                .build()
        ).block();
    }

    public Account twiceOverdrafted(BigDecimal block1,
                                    BigDecimal block2,
                                    BigDecimal availableBalance) {
        var acc = accountISO.accountWithBalance(availableBalance);
        accountOverdraft(acc, block1, null);
        accountOverdraft(acc, block2, null);


        return acc;
    }

    public Account twiceOverdrafted(BigDecimal block1,
                                    BigDecimal block2,
                                    BigDecimal block1Paid,
                                    BigDecimal block2Paid,
                                    BigDecimal availableBalance) {
        var acc = accountISO.accountWithBalance(availableBalance);
        accountOverdraft(acc, block1, block1Paid);
        accountOverdraft(acc, block2, block2Paid);


        return acc;
    }
}
