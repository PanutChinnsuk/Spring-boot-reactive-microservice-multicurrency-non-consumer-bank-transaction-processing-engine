package com.panut.centralbanking.fxrateservice.infrastructure.repository;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface FxRateRepository extends ReactiveCrudRepository<FxRate, String> {

    @Query("""
            SELECT f.id FROM fx_rates f
            WHERE f.base_currency = :baseCurrency AND
            f.quote_currency = :quoteCurrency AND
            f.timestamp BETWEEN :beforeAdding AND :afterAdding
            AND f.id != :currentId
            LIMIT 1 FOR UPDATE
            """)
    Mono<String> findConflict(@Param("baseCurrency")CurrencyCode baseCurrency,
                              @Param("quoteCurrency")CurrencyCode quoteCurrency,
                              @Param("beforeAdding")LocalDateTime beforeAdding,
                              @Param("afterAdding") LocalDateTime afterAdding,
                              @Param("currentId") String currentFxRateId);

    @Query("""
            SELECT f.id, f.bid_rate, f.ask_rate, f.mid_rate, f.treasury_rate
            FROM fx_rates f
            WHERE f.base_currency = :baseCurrency AND
            f.quote_currency = :quoteCurrency
            ORDER BY f.timestamp DESC LIMIT 1
            """)
    Mono<FxRate> findLatest(@Param("baseCurrency")CurrencyCode baseCurrency,
                              @Param("quoteCurrency")CurrencyCode quoteCurrency);


    @Query("""
            SELECT f.id
            FROM fx_rates f
            WHERE f.base_currency = :baseCurrency AND
            f.quote_currency = :quoteCurrency
            ORDER BY f.timestamp DESC LIMIT 1 FOR UPDATE
            """)
    Mono<String> findExistLatest(@Param("baseCurrency")CurrencyCode baseCurrency,
                            @Param("quoteCurrency")CurrencyCode quoteCurrency);

    Mono<Boolean> existsByBaseCurrencyAndQuoteCurrency(CurrencyCode baseCurrency,
                                                       CurrencyCode quoteCurrency);

}
