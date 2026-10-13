package com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.handler;

import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsMono;
import com.panut.centralbanking.appsecuritycore.servicesecurity.context.ServiceSecurityContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

@Aspect
public class ServiceHasAuthorityExpectsMonoAspect {



    @Around("@annotation(anno)")
    public Object hasAuthority(ProceedingJoinPoint jp,
                               ServiceHasAuthorityExpectsMono anno) {
        return Mono.deferContextual(ctx -> {
            String requiredAuthority = anno.value();
            ServiceSecurityContext securityContext = ctx
                    .getOrDefault(ServiceSecurityContext.class,
                            ServiceSecurityContext.defaultContext(requiredAuthority));

            if (!securityContext.contain(requiredAuthority)) {
                return Mono.error(() ->
                        new IllegalStateException("Unauthorized internal service call."));
            }


            try {
                return (Mono<?>) jp.proceed();
            } catch (Throwable e) {
                return Mono.error(() -> new RuntimeException(e));
            }
        });
    }
}
