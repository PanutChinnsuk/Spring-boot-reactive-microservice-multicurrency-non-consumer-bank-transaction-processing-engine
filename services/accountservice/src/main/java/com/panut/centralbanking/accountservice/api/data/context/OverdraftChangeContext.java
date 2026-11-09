package com.panut.centralbanking.accountservice.api.data.context;

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
    @NotNull
    @NotEmpty
    private List<String> accountOverdraftIds;
}
