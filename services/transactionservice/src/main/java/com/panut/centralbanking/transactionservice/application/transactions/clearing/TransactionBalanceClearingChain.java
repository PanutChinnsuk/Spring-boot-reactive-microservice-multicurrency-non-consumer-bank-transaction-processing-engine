package com.panut.centralbanking.transactionservice.application.transactions.clearing;

import com.panut.centralbanking.transactionservice.application.transactions.clearing.query.data.TransactionBalanceClearingData;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Data
@Slf4j
public class TransactionBalanceClearingChain {
    private Map<String, Sinks.Many<BalanceChangeEvent>> perTransactionSink = new HashMap<>();
    private final Flux<BalanceChangeEvent> unsorted;


    public Mono<BalanceChangeEvent> put(final String transactionId,
                                         final BalanceChangeEvent event) {
      return Mono.defer(() -> {
          Sinks.Many sink = perTransactionSink.computeIfAbsent(transactionId,
                  id -> {
              log.info("perTransactionSink.computeIfAbsent key {}, is absent, created default value",
                      transactionId);
              return Sinks.many().unicast().onBackpressureBuffer();
                  });


          Sinks.EmitResult result = sink.tryEmitNext(event);
          log.info("Append event, sink emit result success: {}", result.isSuccess());
          if (result.isFailure())
              throw new IllegalStateException("Failed to append event account id " +
                      event.getAccountId());

          perTransactionSink.put(transactionId, sink);

          return Mono.just(event);
      });
    }

    public Mono<Void> closeSinks() {
        return Mono.defer(() -> {
            for (Map.Entry<String, Sinks.Many<BalanceChangeEvent>> entry :
                    perTransactionSink.entrySet()) {
                Sinks.EmitResult result = entry.getValue().tryEmitComplete();

                if (result.isFailure()) {
                    throw new IllegalStateException("Failed to complete sink result of key " +
                            entry.getKey());
                }

                log.info("Successfully completed sink of key " + entry.getKey());

            }


            return Mono.empty();
        });
    }

    public Flux<BalanceChangeEvent> getMyTransaction(final String transactionId) {
        Sinks.Many sink = perTransactionSink.get(transactionId);

        if (sink == null)
        {
            log.info("transaction {}, sink is null, returned an empty event flux", transactionId);
            return Flux.empty();
        }

        log.info("transaction {}, sink is not null, returned the valued event flux.", transactionId);
        return sink.asFlux();
    }


    public static Mono<TransactionBalanceClearingChain> newChain(final Flux<BalanceChangeEvent> unsorted) {

        return Mono.just(new TransactionBalanceClearingChain(unsorted));
    }



}
