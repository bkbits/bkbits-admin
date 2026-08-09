package com.bkbits.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean(name = "db_main", typed = true)
    public DataSource mainDataSource(@Inject("${solon.dataSources.main}") HikariDataSource ds) {
        return ds;
    }
}
