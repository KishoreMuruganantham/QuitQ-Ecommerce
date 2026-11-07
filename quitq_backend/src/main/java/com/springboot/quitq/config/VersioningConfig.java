package com.springboot.quitq.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class VersioningConfig {

    @Bean
    public FilterRegistrationBean<ApiVersionFilter> versioningFilterRegistration(ApiVersionFilter filter) {
        FilterRegistrationBean<ApiVersionFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
