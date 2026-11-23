package com.panut.centralbanking.transactionservice.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.springframework.boot.r2dbc.autoconfigure.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {



    @Bean
    public Flyway flyway(R2dbcProperties r2dbcProperties) {
        var flyway = Flyway.configure()
                .dataSource(r2dbcProperties.getUrl()
                                .replace("r2dbc", "jdbc"),
                        r2dbcProperties.getUsername(),
                        r2dbcProperties.getPassword())
                .batch(true)
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();


        return flyway;
    }
}
