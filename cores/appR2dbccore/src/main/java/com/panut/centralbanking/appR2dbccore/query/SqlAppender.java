package com.panut.centralbanking.appR2dbccore.query;

import com.panut.centralbanking.appR2dbccore.data.SingleReturnFunction;
import com.panut.centralbanking.appcore.util.ListIndex;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.List;
import java.util.function.Function;




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

    public static class Update {

        public static <T,C,V,V2> SqlAppenderResult bindStatement(final int currentBindingIndex,
                                                   final DatabaseClient.GenericExecuteSpec any,
                                              final List<T> entityList,
                                                final Function<T,V> bindConditionValueFunction,
                                                final Function<T,V2> bindValueFunction) {
            DatabaseClient.GenericExecuteSpec statement = any;

            log.info("starting bindStatement");

            int bindingIndex = currentBindingIndex;
            for (int i = 0; i < entityList.size(); i++) {
                T entity = entityList.get(i);
                V conditionValue = bindConditionValueFunction.apply(entity);
                V2 bindValue = bindValueFunction.apply(entity);

                log.info("current binding index {}, conditon {}, value {}",
                        bindingIndex, conditionValue, bindValue);

                statement = statement.bind(bindingIndex++, conditionValue);
                statement = statement.bind(bindingIndex++, bindValue);
            }


            return new SqlAppenderResult(bindingIndex, statement);
        }

        public static <T,C,V> SqlAppenderResult endBinding(final int currentBindingIndex,
                                                   final DatabaseClient.GenericExecuteSpec any,
                                                   final List<T> entityList,
                                                   final Function<T,V> bindValueFunction) {
            DatabaseClient.GenericExecuteSpec statement = any;
            log.info("starting bindStatement");

            int bindingIndex = currentBindingIndex;
            for (int i = 0; i < entityList.size(); i++) {
                T entity = entityList.get(i);
                V bindValue = bindValueFunction.apply(entity);

                log.info("current binding index {}, value {}",
                        bindingIndex, bindValue);

                statement = statement.bind(bindingIndex++, bindValue);
            }


            return new SqlAppenderResult(bindingIndex, statement);
        }

        public static StringBuilder appendCondition(int elementSize,
                                                        final StringBuilder fromSql,
                                                        final StringBuilder updatingField,
                                                        final String defaultToField,
                                                        final boolean addingNextField) {
            var sql = fromSql;
            var field = updatingField;

            for (int i = 0; i < elementSize; i++) {
                field.append("WHEN ? THEN ? \n");

                if (!ListIndex.isNotLast(i, elementSize)) {
                    String end = addingNextField
                            ? " END, \n"
                            : " END \n";

                    field.append("ELSE " + defaultToField + end);
                }
            }

            StringBuilder after = sql.append(field);
            log.info("appendCondition -> {}", after.toString());
            return after;
        }

        public static StringBuilder endWithWHERE_FIELD_IN(int elementSize,
                                                    final StringBuilder fromSql,
                                                    final String field) {
            var sql = fromSql;
            sql.append("\n WHERE " + field + " IN (");

            for (int i = 0; i < elementSize; i++) {


                if (ListIndex.isNotLast(i, elementSize)) {
                    sql.append("?,");
                }else {
                    sql.append("?) \n");
                }
            }



            log.info("endWithWHERE_FIELD_IN -> {}", sql.toString());
            return sql;
        }


    }


}
