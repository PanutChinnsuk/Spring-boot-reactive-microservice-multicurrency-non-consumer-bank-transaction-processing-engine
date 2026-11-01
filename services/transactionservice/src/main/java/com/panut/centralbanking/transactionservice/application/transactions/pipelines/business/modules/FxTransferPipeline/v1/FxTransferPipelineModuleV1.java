package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.FxTransferPipeline.v1;

import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.calculations.conversions.FxRateCurrencyConversion;
import com.panut.centralbanking.transactionservice.application.calculations.profits.FxRateProfitCalculator;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.ForeignBuyBaseTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class FxTransferPipelineModuleV1 {
    private final BusinessPipelineRepository repository;
    private final TransactionRegisterPipeline transactionRegisterPipeline;
    private final AccountServiceController accountServiceController;
    private final FxRateServiceController fxRateServiceController;
    private final LedgerAccountQuery ledgerAccountQuery;


    public FxTransferPipelineModuleV1(BusinessPipelineRepository repository,
                              TransactionRegisterPipeline transactionRegisterPipeline,
                              AccountServiceController accountServiceController,
                              FxRateServiceController fxRateServiceController,
                              LedgerAccountQuery ledgerAccountQuery) {
        this.repository = repository;
        this.transactionRegisterPipeline = transactionRegisterPipeline;
        this.accountServiceController = accountServiceController;
        this.fxRateServiceController = fxRateServiceController;
        this.ledgerAccountQuery = ledgerAccountQuery;
    }

    //SELF REMINDER >>> CHANGE TO ACCEPT TRANSACTION TYPE AND GET EXPECTED LEDGER TYPE
    //ACCORDINGLY IF NEEDED MORE THAN JUST LIABILITY AND NORMAL FX_TRANSFER
    public final TransactionType FIXED_TRANSACTION_TYPE = TransactionType.FX_TRANSFER;
    public final Set<LedgerType> FIXED_EXPECTED_LEDGER_TYPES =
            Set.of(LedgerType.LIABILITY);


    public Mono<TransactionCreationPipelineResult>
    defaultForeignBuyBase(final FxTransferPipelineChain fxTransferPipelineChain,
                          final ForeignBuyBaseTransferData data) {
        return Mono.just(fxTransferPipelineChain)
                .doOnNext(a -> log.info("After passed to defaultForeignBuyBase"))
                .map(chain -> {
                    BigDecimal uncovertedForeignAmount = data.getForeignAmount();
                    FxRateGetLatestResponse latestConversion = chain.getFxRateGetLatestResponse();
                    FxRateData fxRateData = chain.getFxRateData();
                    GetAccountsInfoForTransactionResponse response = chain
                            .getGetAccountsInfoForTransactionResponse();
                    var fromAccount = response.getAccount(data.getFromAccountId());
                    var toAccount = response.getAccount(data.getToAccountId());
                    var internalProfitAccount = response.getAccount(data.getInternalFxProfitAccountId());
                    var internalFxClearing = response.getAccount(data.getInternalFxClearingAccountId());
                    String ledgerCode = data.getFxLedgerCode().getValue();
                    String ledgerAccountId = chain.getLedgerCodeMap().get(ledgerCode);

                    BigDecimal convertedForeignAmount = FxRateCurrencyConversion.V1
                            .convertDivide(uncovertedForeignAmount, latestConversion, fxRateData);
                    BigDecimal convertedProfitAmount = FxRateProfitCalculator.V1
                            .foreignBuyBaseProfit(uncovertedForeignAmount, latestConversion,
                                    fxRateData);

                    log.info("defaultForeignBuyBase, uncovertedForeignAmount {}",
                            uncovertedForeignAmount);
                    log.info("defaultForeignBuyBase, covertedForeignAmount {}",
                            convertedForeignAmount);
                    log.info("defaultForeignBuyBase, convertedProfitAmount {}",
                            convertedProfitAmount);

                    List<AccountData> accountDatas = List.of(
                            AccountData.V1.fxDebit(fromAccount,
                                    uncovertedForeignAmount,
                                    convertedForeignAmount,
                                    ledgerAccountId),
                            AccountData.V1.linearCredit(toAccount,
                                    convertedForeignAmount,
                                    ledgerAccountId),
                            AccountData.V1.linearCredit(internalProfitAccount,
                                    convertedProfitAmount,
                                    ledgerAccountId),
                            AccountData.V1.linearClearingDebit(internalFxClearing,
                                    convertedProfitAmount,
                                    ledgerAccountId)
                    );


                    return new PreTransactionRegisterData(FIXED_TRANSACTION_TYPE,
                            accountDatas, chain.getFxRateData());
                })
                .doOnNext(a -> log.info("pre register fx transfer pipeline"))
                .flatMap(transactionRegisterPipeline::register);
    }
}
