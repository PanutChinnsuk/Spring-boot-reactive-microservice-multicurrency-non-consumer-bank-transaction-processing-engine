package com.panut.centralbanking.transactionservice.application.transactions.clearing.implement;

import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.query.TransactionClearingQuery;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.service.TransactionClearingService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TransactionClearingServiceImpl implements TransactionClearingService {
    private final TransactionClearingQuery query;
    private final TransactionClearingProperty property;

    public TransactionClearingServiceImpl(TransactionClearingQuery query,
                                          TransactionClearingProperty property) {
        this.query = query;
        this.property = property;
    }


    public Flux<BalanceChangeData> getBulkPending() {
        return query.getBulk(BalanceChangeEventStatus.PENDING,
                property.getSizeLimit());
    }
}
