package org.example.querydurationlogging.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.example.querydurationlogging.common.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Type;
import java.util.*;

import static org.example.querydurationlogging.common.Constants.Logging.*;


@ControllerAdvice
@Slf4j
@Component
public class HttpLoggingFilter extends RequestBodyAdviceAdapter implements HandlerInterceptor, ResponseBodyAdvice<Object> {

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    HttpServletResponse httpServletResponse;

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) {
        httpServletRequest.setAttribute(START_TIME, System.nanoTime());
        ThreadContext.put(TRACE_ID, StringUtils.defaultIfBlank(httpServletRequest.getHeader(TRACE_ID), UUID.randomUUID().toString()));
        ThreadContext.put(TYPE, HTTP_REQUEST);
        ThreadContext.remove(DURATION);
        log.info((JsonUtils.toJson(new HttpMessage(httpServletRequest))));
        ThreadContext.remove(TYPE);

        return true;
    }

    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    public Object afterBodyRead(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        this.httpServletRequest.setAttribute(START_TIME, System.nanoTime());
        ThreadContext.put(TYPE, HTTP_REQUEST);
        ThreadContext.remove(DURATION);
        String s = new HttpMessage(this.httpServletRequest, JsonUtils.toJson(body)).toString();
        log.info(s);
        ThreadContext.remove(TYPE);
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        String traceId = ThreadContext.get(TRACE_ID);
        if (traceId != null) {
            long start = (Long) this.httpServletRequest.getAttribute(START_TIME);
            long duration = System.nanoTime() - start;
            ThreadContext.put(TYPE, HTTP_RESPONSE);
            ThreadContext.put(DURATION, String.valueOf(duration));
            ThreadContext.put(RESPONSE_CODE, "" + this.httpServletResponse.getStatus());
            log.info((new HttpMessage(this.httpServletRequest, this.httpServletResponse, JsonUtils.toJson(body))).toString());
            ThreadContext.clearMap();
        }

        return body;
    }

    @Data
    private static class HttpMessage {
        private String remoteHost;

        private String localAddr;

        private int localPort;

        private String requestUri;

        private String method;

        private int responseCode;

        private Map<String, String> header = new HashMap<>();

        private String body = null;

        public HttpMessage(HttpServletRequest httpRequest) {
            this.remoteHost = httpRequest.getRemoteHost();
            this.localAddr = httpRequest.getLocalAddr();
            this.localPort = httpRequest.getLocalPort();
            this.method = httpRequest.getMethod();
            this.requestUri = httpRequest.getRequestURI();
            Enumeration<String> headerNames = httpRequest.getHeaderNames();

            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (headerName.equalsIgnoreCase("Authorization")) {
                    this.header.put(headerName, "******");
                } else {
                    this.header.put(headerName, httpRequest.getHeader(headerName));
                }
            }

        }

        public HttpMessage(HttpServletRequest httpRequest, String body) {
            this(httpRequest);
            this.body = body;
        }

        public HttpMessage(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String body) {
            this.remoteHost = httpRequest.getRemoteHost();
            this.localAddr = httpRequest.getLocalAddr();
            this.localPort = httpRequest.getLocalPort();
            this.method = httpRequest.getMethod();
            this.requestUri = httpRequest.getRequestURI();
            this.responseCode = httpResponse.getStatus();
            Collection<String> headerNames = httpResponse.getHeaderNames();

            for (String headerName : headerNames) {
                this.header.put(headerName, httpResponse.getHeader(headerName));
            }

            this.body = body;
        }
    }
}