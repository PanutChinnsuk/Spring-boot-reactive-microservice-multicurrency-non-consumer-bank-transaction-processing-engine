package com.panut.centralbanking.accountservice.integration.internalaccounts;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.request.useronly.InternalAccountBranchAddRequest;
import com.panut.centralbanking.accountservice.application.accounts.internalaccounts.accounts.service.InternalAccountService;
import com.panut.centralbanking.accountservice.application.accounts.internalaccounts.branch.query.BranchQuery;
import com.panut.centralbanking.accountservice.application.accounts.internalaccounts.branch.service.BranchService;
import com.panut.centralbanking.accountservice.core.InternalAccountType;
import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.accountservice.integration.testrepository.InternalAccountBranchTestRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.InternalAccountTestRepository;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InternalAccountCycleTest implements IntegrationDevTest {
    @Autowired
    private InternalAccountService internalAccountService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private InternalAccountBranchTestRepository internalAccountBranchTestRepository;

    @Autowired
    private InternalAccountTestRepository internalAccountTestRepository;

    @Autowired
    private BranchQuery branchQuery;

    private InternalBranch testBranch = InternalBranch.TEST;
    private String nonInternalAccountId = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    private String nonInternalAccountId2 = SeededDatabase.ID_NORMAL_ACCOUNT_2;

    @AfterAll
    public void cleanUp() {
        internalAccountTestRepository.deleteByAccountId(nonInternalAccountId).block();
        internalAccountTestRepository.deleteByAccountId(nonInternalAccountId2).block();
        internalAccountBranchTestRepository.deleteByInternalBranch(testBranch).block();
    }

    @Test
    @Order(1)
    public void shouldCreateBranch() {
        cleanUp();
        CommonResult result = branchService
                .addBranch(new InternalAccountBranchAddRequest(
                        InternalBranch.TEST)).block();

        Assertions.assertTrue(result.isSuccess());Assertions.assertDoesNotThrow(() -> branchQuery.getBranchId(testBranch).block());
    }

    @Test
    @Order(2)
    public void shouldAddInternalAccount() {
        CommonResult result = internalAccountService.add(nonInternalAccountId,
                testBranch, InternalAccountType.FX_CLEARING).block();
        Assertions.assertTrue(result.isSuccess());



    }

    @Test
    @Order(3)
    public void shouldErrorForDuplicatedType() {
        Assertions.assertThrows(
                Conflicted.class,
                () -> internalAccountService.add(nonInternalAccountId2,
                        testBranch, InternalAccountType.FX_CLEARING).block()
        );
    }

    @Test
    @Order(4)
    public void shouldErrorForDuplicatedAccount() {
        Assertions.assertThrows(
                Conflicted.class,
                () -> internalAccountService.add(nonInternalAccountId,
                        testBranch, InternalAccountType.FX_PROFIT).block()
        );
    }


}
