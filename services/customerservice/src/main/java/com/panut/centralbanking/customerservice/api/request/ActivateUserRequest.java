package com.panut.centralbanking.customerservice.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActivateUserRequest {
    @NotBlank
    private String customerId;

    @NotBlank
    private String userId;

    @NotBlank
    private String code;

}
