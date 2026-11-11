package com.panut.centralbanking.appR2dbccore.query;

import com.panut.centralbanking.appcore.util.ListIndex;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlAppender {
    public static final String insertAppendPrefix = "@prefix";

    public static String multiRowInsert(int elementSize,
                                     final StringBuilder sql,
                                     final String appendingInsertValue) {
        StringBuilder building = sql;


        for (int i = 0; i < elementSize; i++) {
            String appending = appendingInsertValue + insertAppendPrefix;

            if (!appending.endsWith(")" + insertAppendPrefix))
                throw new IllegalStateException("Invalid appendingInsertValue, current: "
                + appending + ", need to end with ) | example: " +
                        "(?,?,?,?)");


            if (ListIndex.isNotLast(i, elementSize)) {
                appending = appending.replace(insertAppendPrefix,
                        ",");
            }else {
                appending = appending.replace(insertAppendPrefix,
                        ";");
            }

            log.info("Appending multirow sql insert");

            building.append(appending);
        }

        return building.toString();
    }
}
