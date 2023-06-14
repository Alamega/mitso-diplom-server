package com.alamega.alamegaspringapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class ServerConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Value("${server.port}")
    String defaultPort;
    public void customize(ConfigurableServletWebServerFactory factory){
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("config.ini"));
        } catch (IOException ignored) {}
        final int port  = Integer.parseInt(props.getProperty("PORT", defaultPort));
        factory.setPort(port);
    }
}