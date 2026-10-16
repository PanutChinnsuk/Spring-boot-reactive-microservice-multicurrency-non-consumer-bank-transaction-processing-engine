package com.panut.centralbanking.appsecuritycore.autoconfig;

import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.handler.ServiceHasAuthorityExpectsFluxAspect;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.handler.ServiceHasAuthorityExpectsMonoAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ServiceAuthorityAspectClassAutoConfiguration {

    @Bean
    public ServiceHasAuthorityExpectsMonoAspect serviceHasAuthorityExpectsMonoAspect() {
        return new ServiceHasAuthorityExpectsMonoAspect();
    }

    @Bean
    public ServiceHasAuthorityExpectsFluxAspect serviceHasAuthorityExpectsFluxAspect() {
        return new ServiceHasAuthorityExpectsFluxAspect();
    }
}
