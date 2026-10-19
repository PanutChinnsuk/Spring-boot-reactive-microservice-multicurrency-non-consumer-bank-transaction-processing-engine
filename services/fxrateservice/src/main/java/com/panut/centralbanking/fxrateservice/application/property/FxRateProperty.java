package com.panut.centralbanking.fxrateservice.application.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "model.fxrate")
@Getter
@Setter
public class FxRateProperty {
    private int addingSafeGuardRangeSeconds;



}
