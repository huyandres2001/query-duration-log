package org.example.querydurationlogging.config;

//import org.example.querydurationlogging.filter.HttpLoggingFilter;
import org.example.querydurationlogging.interceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LogInterceptor logInterceptor;
//
//    @Autowired
//    private HttpLoggingFilter httpLoggingFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
//        registry.addInterceptor(httpLoggingFilter);
    }
}