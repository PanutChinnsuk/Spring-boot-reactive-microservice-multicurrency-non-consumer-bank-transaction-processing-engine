package com.panut.centralbanking.customerservice.api.request;

import com.panut.centralbanking.customerservice.api.data.CustomerEmailCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerPhoneCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerProfileCreateData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreateRequest {
    @Valid
    @NotNull
    private CustomerProfileCreateData customerProfileCreateData;

    @Valid
    @NotNull
    private CustomerEmailCreateData customerEmailCreateData;

    @Valid
    @NotNull
    private CustomerPhoneCreateData customerPhoneCreateData;
}
