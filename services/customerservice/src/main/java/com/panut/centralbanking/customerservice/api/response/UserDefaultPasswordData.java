package com.panut.centralbanking.customerservice.api.response;

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
