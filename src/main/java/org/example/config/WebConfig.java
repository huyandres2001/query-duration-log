package org.example.config;

import org.example.filter.RequestDurationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<RequestDurationFilter> requestDurationFilter() {
        FilterRegistrationBean<RequestDurationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestDurationFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
} 