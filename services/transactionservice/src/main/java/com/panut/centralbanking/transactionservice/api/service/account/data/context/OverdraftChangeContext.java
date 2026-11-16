package com.panut.centralbanking.transactionservice.api.service.account.data.context;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OverdraftChangeContext {
    private List<String> accountOverdraftIds;
}
