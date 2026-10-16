package com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.handler;

import com.panut.centralbanking.appcore.exceptions.Unauthorized;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsMono;
import com.panut.centralbanking.appsecuritycore.servicesecurity.context.ServiceSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

@Slf4j
@Aspect
public class ServiceHasAuthorityExpectsMonoAspect {



    @Around("@annotation(anno)")
    public Object hasAuthority(ProceedingJoinPoint jp,
                               ServiceHasAuthorityExpectsMono anno) {
        return Mono.deferContextual(ctx -> {
            String requiredAuthority = anno.value();
            log.debug("Required service authority: {}", requiredAuthority);

            ServiceSecurityContext securityContext = ctx
                    .getOrDefault(ServiceSecurityContext.class,
                            ServiceSecurityContext.defaultContext(requiredAuthority));

            securityContext.getAuthorities().forEach(a ->
                    log.debug("From ServiceSecurityContext: {}", a));

            if (!securityContext.contain(requiredAuthority)) {
                return Mono.error(() ->
                        new Unauthorized("Unauthorized internal service call."));
            }


            try {
                return (Mono<?>) jp.proceed();
            } catch (Throwable e) {
                return Mono.error(() -> new RuntimeException(e));
            }
        });
    }
}
