package com.panut.centralbanking.appcore.integrationtest;

import java.util.UUID;

public class SeededDatabaseIdFlag {
    public static final String NONE = "NONE%";
    public static final String TEST = "TEST%";
    public static final String INTERNAL = "INTERNAL%";

    public static final String getTestRandomUUIDString() {
        return TEST.replace("%", "_") + UUID.randomUUID().toString();
    }

}
