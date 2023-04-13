package com.example.api.configuration;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FlywayConfiguration {
    public Flyway createFlyway(DataSource dataSource) {
        return new Flyway(new FluentConfiguration().locations("db/migration").dataSource(dataSource));
    }
}
