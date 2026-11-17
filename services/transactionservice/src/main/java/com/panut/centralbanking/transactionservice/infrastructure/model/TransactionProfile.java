package com.panut.centralbanking.transactionservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Table("transaction_profiles")
@NoArgsConstructor
@Getter
public class TransactionProfile extends VersionedUUIDStringEntity {
    @Column("fx_direction")
    private FxDirection fxDirection;

    @Column("fx_rate_id")
    private String fxRateId;

    @Column("posted_at")
    private LocalDateTime postedAt;

    @Column("reversed_of_transaction_id")
    private String reversedOfTransactionId;

    @Builder
    public TransactionProfile(String transactionId,
                              FxDirection fxDirection,
                              String fxRateId) {
        this.fxDirection = fxDirection;
        this.fxRateId = fxRateId;
        setInsert(transactionId);

    }

    public boolean isPostedState() {
        return this.postedAt != null;
    }

    public boolean isReversedState() {
        return this.reversedOfTransactionId != null;
    }

    public boolean hasFxRate() {
        return this.fxRateId != null &&
                !fxDirection.equals(FxDirection.NONE);
    }


    public Mono<TransactionProfile> posted() {
        return Mono.defer(() -> {
            if (isPostedState())
                return Mono.error(
                        () -> new SystemMisused("Same transaction can't be posted twice."));

            this.postedAt = LocalDateTime.now();

            return Mono.just(this);
        });
    }

    public Mono<TransactionProfile> reversed(String ofTransactionId) {
        return Mono.defer(() -> {
            if (isReversedState() || !isPostedState())
                return Mono.error(
                        () -> new SystemMisused("Same transaction can't be reversed at this state."));

            this.reversedOfTransactionId = ofTransactionId;

            return Mono.just(this);
        });
    }

    public Mono<Boolean> isReversed() {
        return Mono.fromCallable(() -> isReversedState());
    }
}
