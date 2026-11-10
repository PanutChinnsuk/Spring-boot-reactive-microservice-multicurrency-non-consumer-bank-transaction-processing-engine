package com.panut.centralbanking.appcore.util;

public class ListIndex {

    public static boolean isNotLast(int currentIndex,
                                    int listSize) {
        return currentIndex < listSize - 1;
    }
}
