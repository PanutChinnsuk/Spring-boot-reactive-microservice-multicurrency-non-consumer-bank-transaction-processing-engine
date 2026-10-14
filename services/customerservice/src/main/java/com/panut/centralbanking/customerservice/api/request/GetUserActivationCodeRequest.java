package com.panut.centralbanking.customerservice.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetUserActivationCodeRequest {
    @NotBlank
    private String customerId;

    @NotBlank
    private String code;

}
