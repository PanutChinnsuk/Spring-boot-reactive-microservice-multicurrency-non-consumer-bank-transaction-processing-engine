package com.panut.centralbanking.appR2dbccore.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MapListSorter {


    public static <T> void sortMapList(final Map<String, List<T>> anyMap,
                                       final SingleReturnFunction<String> getKeyMethod,
                                       final T entity) {
        String key = getKeyMethod.apply();

        sortMapList(anyMap, key, entity);
    }

    public static <T> void sortMapList(final Map<String, List<T>> anyMap,
                                       final String key,
                                       final T entity) {
        List<T> myList = anyMap.computeIfAbsent(key,
                string -> new ArrayList<>());

        myList.add(entity);

        anyMap.put(key, myList);
    }
}
