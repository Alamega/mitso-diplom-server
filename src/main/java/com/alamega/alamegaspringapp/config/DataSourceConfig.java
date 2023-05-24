package com.alamega.alamegaspringapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    String defaultUrl;
    @Value("${spring.datasource.username}")
    String defaultUsername;
    @Value("${spring.datasource.password}")
    String defaultPassword;

    @Bean
    public DataSource dataSource() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("config.ini"));
        } catch (IOException e) {
            System.out.println("Не удалось загрузить настройки, так как не обнаружен файл config.ini, используются значения по умолчанию.");
        }

        final String url  = props.getProperty("DATABASE_URL", defaultUrl);
        final String username  = props.getProperty("DATABASE_USERNAME", defaultUsername);
        final String password  = props.getProperty("DATABASE_PASSWORD", defaultPassword);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
