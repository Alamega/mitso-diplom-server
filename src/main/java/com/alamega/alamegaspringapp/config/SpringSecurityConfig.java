package com.alamega.alamegaspringapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Value("${security.key}")
    String key;
    final DataSource dataSource;
    public SpringSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests()
                //Пускать только админов
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                //Пускать только авторизированных
                .requestMatchers("/authenticated/**").authenticated()
                //Пускать всех
                .requestMatchers("/**").permitAll()
            .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            .and()
                .rememberMe()
                .key(key)
                .rememberMeCookieName("rememberMe")
                .tokenValiditySeconds(60*60*24)
                .alwaysRemember(true)
                .useSecureCookie(true)
            .and()
                .logout()
                .deleteCookies("JSESSIONID", "rememberMe")
                .logoutSuccessUrl("/")
                .permitAll();
        return http.build();
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
        jdbcUserDetailsManager.setDataSource(dataSource);
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select username,role from users where username = ?");
        return jdbcUserDetailsManager;
    }
}