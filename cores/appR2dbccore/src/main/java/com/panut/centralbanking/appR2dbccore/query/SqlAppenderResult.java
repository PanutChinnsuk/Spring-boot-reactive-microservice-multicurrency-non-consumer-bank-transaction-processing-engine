package com.panut.centralbanking.appR2dbccore.query;

import org.springframework.r2dbc.core.DatabaseClient;

public record SqlAppenderResult(int incrementedBindingIndex,
                                DatabaseClient.GenericExecuteSpec modifiedExecuteSpec) {
}
