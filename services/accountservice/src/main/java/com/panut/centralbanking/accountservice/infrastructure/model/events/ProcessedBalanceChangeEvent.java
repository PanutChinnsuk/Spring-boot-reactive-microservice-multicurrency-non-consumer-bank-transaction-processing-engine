package com.panut.centralbanking.accountservice.infrastructure.model.events;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("account_processed_balance_change_events")
@Getter
@NoArgsConstructor
public class ProcessedBalanceChangeEvent {
    @Id
    @Column("id")
    private Long id;

    @Column("event_id")
    private String eventId;

    @Builder
    public ProcessedBalanceChangeEvent(String eventId) {
        this.eventId = eventId;
    }
}
