package com.panut.centralbanking.customerservice.api.data;

import com.panut.centralbanking.customerservice.core.enums.Gender;
import com.panut.centralbanking.customerservice.infrastructure.fields.NameFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEmailCreateData {
    @NotBlank
    @Email
    @Length(max = 200)
    private String email;

}
