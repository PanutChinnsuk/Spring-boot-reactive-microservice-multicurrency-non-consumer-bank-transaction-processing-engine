package com.panut.centralbanking.appR2dbccore.data;

@FunctionalInterface
public interface SingleReturnFunction<R> {
    R apply();
}
