package com.panut.centralbanking.userservice.api.service.customerservice.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActivateUserRequest {
    private String customerId;
    private String userId;
    private String code;

}
