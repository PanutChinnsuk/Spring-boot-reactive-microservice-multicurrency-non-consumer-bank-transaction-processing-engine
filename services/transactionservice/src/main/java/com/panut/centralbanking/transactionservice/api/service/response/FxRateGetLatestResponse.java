package com.panut.centralbanking.transactionservice.api.service.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateGetLatestResponse {
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private BigDecimal treasuryRate;




}
