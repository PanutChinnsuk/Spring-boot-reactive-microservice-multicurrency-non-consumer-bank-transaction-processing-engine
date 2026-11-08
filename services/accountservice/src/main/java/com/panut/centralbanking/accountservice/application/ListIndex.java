package com.panut.centralbanking.accountservice.application;

public class ListIndex {

    public static boolean isNotLast(int currentIndex,
                                    int listSize) {
        return currentIndex < listSize - 1;
    }
}
