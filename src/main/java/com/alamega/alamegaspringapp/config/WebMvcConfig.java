package com.alamega.alamegaspringapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                //Тут возможны любые совакупления с полученным запросом
                //TODO Логгер
                //System.out.println(request.getRequestURL());
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }
        });
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
