package com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.handler;

import com.panut.centralbanking.appcore.exceptions.Unauthorized;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsFlux;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsMono;
import com.panut.centralbanking.appsecuritycore.servicesecurity.context.ServiceSecurityContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Aspect
public class ServiceHasAuthorityExpectsFluxAspect {



    @Around("@annotation(anno)")
    public Object hasAuthority(ProceedingJoinPoint jp,
                               ServiceHasAuthorityExpectsFlux anno) {
        return Flux.deferContextual(ctx -> {
            String requiredAuthority = anno.value();
            ServiceSecurityContext securityContext = ctx
                    .getOrDefault(ServiceSecurityContext.class,
                            ServiceSecurityContext.defaultContext(requiredAuthority));

            if (!securityContext.contain(requiredAuthority)) {
                return Flux.error(() ->
                        new Unauthorized("Unauthorized internal service call."));
            }


            try {
                return (Flux<?>) jp.proceed();
            } catch (Throwable e) {
                return Flux.error(() -> new RuntimeException(e));
            }
        });
    }
}
