package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.reverse;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetByIdRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetResponse;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.TransactionReversalContext;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManageChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManagePipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.reverse.validator.TransactionReversePipelineValidator;
import com.panut.centralbanking.transactionservice.application.transactions.profile.TransactionProfileRepositoryManager;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.TransactionRepositoryManager;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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

@Slf4j
@Component
public class TransactionReversePipeline {
    private final TransactionManagePipelineRepository repository;
    private final TransactionRepositoryManager transactionManager;
    private final TransactionRegisterPipeline registerPipeline;
    private final TransactionProfileRepositoryManager profileManager;
    private final AccountServiceController accountServiceController;
    private final WorkerService workerService;
    private final TransactionReversePipelineValidator validator;

    public TransactionReversePipeline(TransactionManagePipelineRepository repository,
                                      TransactionRepositoryManager transactionManager,
                                      TransactionRegisterPipeline registerPipeline,
                                      TransactionProfileRepositoryManager profileManager,
                                      AccountServiceController accountServiceController,
                                      WorkerService workerService,
                                      TransactionReversePipelineValidator validator) {
        this.repository = repository;
        this.transactionManager = transactionManager;
        this.registerPipeline = registerPipeline;
        this.profileManager = profileManager;
        this.accountServiceController = accountServiceController;
        this.workerService = workerService;
        this.validator = validator;
    }

    private TransactionLineDirection getReverseDirection(final TransactionLineDirection direction) {
        return switch (direction) {
            case CREDIT -> TransactionLineDirection.DEBIT;
            case DEBIT -> TransactionLineDirection.CREDIT;
            case CREDIT_OVERDRAFT -> TransactionLineDirection.DEBIT_OVERDRAFT;
            case DEBIT_OVERDRAFT -> TransactionLineDirection.CREDIT_OVERDRAFT;
            case CLEARING_DEBIT -> TransactionLineDirection.CLEARING_CREDIT;
            case CLEARING_CREDIT -> TransactionLineDirection.CLEARING_DEBIT;
            case NONE -> TransactionLineDirection.NONE;
        };
    }

    private Mono<TransactionCreationPipelineResult> clear(final TransactionManageChain chain,
                                                          final String reversingOfId) {
        return chain.getTransactionLines()
                .collectList()
                .flatMap(lines -> new ReversingChain().transactionLines(lines))
                .doOnNext(a -> log.info("start reversing chain with transaction lines"))
                .flatMap(reverseChain -> {
                    var profile = chain.getTransactionProfile();
                    var fxRateId = profile.getFxRateId();
                    var fxDirection = profile.getFxDirection();

                    FxRateGetResponse fxRateConversion = null;
                    FxRateData fxRateData = FxRateData.empty(fxDirection);

                    log.info("profile has fxRate? {}", profile.hasFxRate());
                    if (profile.hasFxRate()) {
                        fxRateData.withId(profile.getFxRateId());
                        log.info("profile has fxRate, used the same fx rate id of {}",
                                profile.getFxRateId());
                    }

                    return reverseChain.fxRateData(fxRateData);
                })
                .doOnNext(a -> log.info("reversing lines"))
                .flatMap(reversingChain -> {
                    List<AccountData> reversingAccountDatas = new ArrayList<>();

                    reversingChain.getTransactionLines().forEach(line -> {
                        var direction = line.getDirection();
                        var reversedDirection = getReverseDirection(direction);
                        String accountId = line.getAccountId();


                        reversingAccountDatas.add(AccountData.reverseLine(line,
                                reversedDirection));
                    });


                    return registerPipeline.register(new PreTransactionRegisterData(chain
                            .getTransaction().getTransactionType(),
                            reversingAccountDatas,
                            reversingChain.getFxRateData())
                            .withTransactionReversalContext(
                                    new TransactionReversalContext(reversingOfId)
                            )

                    );
                });

    }


    public Mono<TransactionManagePipelineResult> reverse(final String transactionId) {
        return workerService.initWorker(WorkerName.TRANSACTION)
                .then(validator.isAlreadyReversed(transactionId))
                .doOnNext(a -> log.info("check if transaction is already reversed"))
                .then(repository.fullTransaction(transactionId,
                        TransactionStatus.values()))
                .doOnNext(a ->
                        log.info("get full transaction info for reversal"))
                .flatMap(chain -> clear(chain, transactionId))
                .doOnNext(a -> log.info("pre register"))
                .map(creationResult ->
                        new TransactionManagePipelineResult(
                                creationResult.getTransactionId(),
                                creationResult.getTransactionStatus()
                        ))
                .doOnSuccess(a ->
                        log.info("Successfully reversed transaction id of {}",
                                transactionId))
                .doOnError(error ->
                        log.error("Failed to reverse transaction, {}",
                                error.getLocalizedMessage()));
    }
}


