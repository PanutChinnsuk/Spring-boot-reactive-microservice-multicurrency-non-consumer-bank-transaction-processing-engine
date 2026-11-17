package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.reverse;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetByIdRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetResponse;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManageChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManagePipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.profile.TransactionProfileRepositoryManager;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.TransactionRepositoryManager;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
class ReversingChain {
    private List<TransactionLine> transactionLines;
    private FxRateGetResponse fxRateConversion;
    private FxRateData fxRateData;
    private GetAccountsInfoForTransactionResponse accountsInfo;

    private Set<String> usingAccountIds;
    private Set<CurrencyCode> usingQuoteCurrencies;

    public Mono<ReversingChain> FxRateGetResponse(final FxRateGetResponse fxRateConversion) {
        return Mono.defer(() -> {
            this.fxRateConversion = fxRateConversion;

            return Mono.just(this);
        });
    }

    public Mono<ReversingChain> fxRateData(final FxRateData fxRateData) {
        return Mono.defer(() -> {
            this.fxRateData = fxRateData;

            return Mono.just(this);
        });
    }


    public Mono<ReversingChain> accountsInfo(final GetAccountsInfoForTransactionResponse accountsInfo) {
        return Mono.defer(() -> {
            this.accountsInfo = accountsInfo;
            this.usingQuoteCurrencies = accountsInfo.getQuoteCurrencies();

            return Mono.just(this);
        });
    }

    public Mono<ReversingChain> transactionLines(final List<TransactionLine> transactionLines) {
        return Mono.defer(() -> {
            this.transactionLines = transactionLines;
            this.usingAccountIds = transactionLines
                    .stream()
                    .map(TransactionLine::getAccountId)
                    .collect(Collectors.toSet());

            return Mono.just(this);
        });
    }
}

@Component
public class TransactionReversePipeline {
    private final TransactionManagePipelineRepository repository;
    private final TransactionRepositoryManager transactionManager;
    private final TransactionRegisterPipeline registerPipeline;
    private final TransactionProfileRepositoryManager profileManager;
    private final FxRateServiceController fxRateServiceController;
    private final AccountServiceController accountServiceController;

    public TransactionReversePipeline(TransactionManagePipelineRepository repository,
                                      TransactionRepositoryManager transactionManager,
                                      TransactionRegisterPipeline registerPipeline,
                                      TransactionProfileRepositoryManager profileManager,
                                      FxRateServiceController fxRateServiceController,
                                      AccountServiceController accountServiceController) {
        this.repository = repository;
        this.transactionManager = transactionManager;
        this.registerPipeline = registerPipeline;
        this.profileManager = profileManager;
        this.fxRateServiceController = fxRateServiceController;
        this.accountServiceController = accountServiceController;
    }

    private Mono<TransactionManageChain> clear(final TransactionManageChain chain) {
        return chain.getTransactionLines()
                .collectList()
                .flatMap(lines -> new ReversingChain().transactionLines(lines))
                .flatMap(reverseChain ->  accountServiceController
                        .getAccountsInfoForTransaction(
                                new GetAccountsInfoForTransactionRequest(reverseChain
                                        .getUsingAccountIds())
                        )
                        .flatMap(reverseChain::accountsInfo)
                )
                .flatMap(reverseChain -> {
                    var transaction = chain.getTransaction();
                    var profile = chain.getTransactionProfile();
                    var fxRateId = profile.getFxRateId();
                    var fxDirection = profile.getFxDirection();

                    FxRateGetResponse fxRateConversion = null;
                    FxRateData fxRateData = null;
                    if (profile.hasFxRate()) {
                        return fxRateServiceController
                                .getById(new FxRateGetByIdRequest(fxRateId))
                                .flatMap(fxRate ->
                                        reverseChain.FxRateGetResponse(fxRate)
                                )
                                .flatMap(revC -> revC
                                        .fxRateData(FxRateData.empty(fxDirection))
                                );
                    }

                    return Mono.just(reverseChain);
                })
                .thenReturn(chain);
    }


    public Mono<TransactionManagePipelineResult> reverse(final String transactionId) {
        return repository.fullTransaction(transactionId,
                TransactionStatus.values())
                .flatMap(this::clear)
                .thenReturn(new TransactionManagePipelineResult());
    }
}


