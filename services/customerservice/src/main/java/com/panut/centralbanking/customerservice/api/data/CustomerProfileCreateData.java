package com.panut.centralbanking.customerservice.api.data;

import com.panut.centralbanking.customerservice.core.enums.Gender;
import com.panut.centralbanking.customerservice.infrastructure.fields.NameFormat;
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
public class CustomerProfileCreateData {
    @NotBlank
    @Length(min = 3, max = 90)
    @NameFormat
    private String firstname;

    @NotBlank
    @Length(min = 3, max = 90)
    @NameFormat
    private String lastname;

    @NotBlank
    @Length(min = 200)
    private String address;

    @Positive
    @NotNull
    private Integer age;

    @NotNull
    private Gender gender;

    @NotBlank
    private String citizenId;

}
