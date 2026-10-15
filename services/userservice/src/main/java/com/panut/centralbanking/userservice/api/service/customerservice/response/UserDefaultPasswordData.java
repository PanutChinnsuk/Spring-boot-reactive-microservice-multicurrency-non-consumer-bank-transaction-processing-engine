package com.panut.centralbanking.userservice.api.service.customerservice.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDefaultPasswordData {
    private String firstname;
    private String lastname;
    private String defaultPassword;




}
