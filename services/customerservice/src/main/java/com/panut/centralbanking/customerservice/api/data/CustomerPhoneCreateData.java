package com.panut.centralbanking.customerservice.api.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPhoneCreateData {
    @NotBlank
    @Length(min = 3, max = 40)
    private String phone;

}
