package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data;

import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxContent;
import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxDirectionContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionMultiFxContext {
    private List<TransactionMultiFxContent> contents;
    private List<TransactionMultiFxDirectionContent> directionContents;


    public boolean hasContents() {
        return contents != null &
                !contents.isEmpty();
    }

    public boolean hasDirectionContents() {
        return directionContents != null &
                !directionContents.isEmpty();
    }
}
