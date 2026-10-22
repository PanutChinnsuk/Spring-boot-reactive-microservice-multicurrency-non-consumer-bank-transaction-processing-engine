package com.panut.centralbanking.appcore.exceptions.handler;

import com.panut.centralbanking.appcore.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalMonoExceptionHandler {

    public Mono<ResponseEntity<Map<String,String>>> createError(Throwable t,
                                                                HttpStatus httpStatus,
                                                                ServerWebExchange ex) {
        return Mono.fromCallable(() -> {
            Map<String,String> errMap = new HashMap<>();


            errMap.put("Localized Message", t.getLocalizedMessage());

            log.error("Error stack trace, ", t);

            return new ResponseEntity<Map<String,String>>(errMap, httpStatus);
        });
    }

    @ExceptionHandler({Forbidden.class,
            SystemMisused.class,
            BusinessException.class,
            SystemMisused.class})
    public Mono<ResponseEntity<Map<String,String>>> handleForbidden(Exception e,
                                                                    ServerWebExchange ex) {
        return createError(e, HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(Conflicted.class)
    public Mono<ResponseEntity<Map<String,String>>> handleConflict(Conflicted e,
                                                                    ServerWebExchange ex) {
        return createError(e, HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler({Unauthorized.class, InvalidCredential.class})
    public Mono<ResponseEntity<Map<String,String>>> handleUnauthorized(Exception e,
                                                                   ServerWebExchange ex) {
        return createError(e, HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(RateLimtExceeded.class)
    public Mono<ResponseEntity<Map<String,String>>> handleTooManyRequest(RateLimtExceeded e,
                                                                       ServerWebExchange ex) {
        return createError(e, HttpStatus.TOO_MANY_REQUESTS, ex);
    }

    @ExceptionHandler(NotFound.class)
    public Mono<ResponseEntity<Map<String,String>>> handleNotFound(NotFound e,
                                                                   ServerWebExchange ex) {
        return createError(e, HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({Exception.class, OptimisticLockingFailureException.class,
    PessimisticLockingFailureException.class, DeadlockLoserDataAccessException.class})
    public Mono<ResponseEntity<Map<String,String>>> handleException(Exception e,
                                                                   ServerWebExchange ex) {
        return createError(e, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

}
