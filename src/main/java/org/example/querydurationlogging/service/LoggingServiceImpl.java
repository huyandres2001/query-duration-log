package org.example.querydurationlogging.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.example.querydurationlogging.common.JsonUtils;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.example.querydurationlogging.common.Constants.Logging.*;

@Service
@Log
public class LoggingServiceImpl implements LoggingService {

    @Override
    public void logRequest(HttpServletRequest httpServletRequest, Object body) {
        Map<String, String> parameters = buildParametersMap(httpServletRequest);
        ThreadContext.put(HTTP_REQUEST_START_TIME, String.valueOf(System.currentTimeMillis()));
        ThreadContext.put(TRACE_ID, StringUtils.defaultIfBlank(httpServletRequest.getHeader(TRACE_ID), UUID.randomUUID().toString()));
        ThreadContext.put(HTTP_METHOD, httpServletRequest.getMethod());
        ThreadContext.put(HTTP_PATH, httpServletRequest.getRequestURI());
        String logMessage = """
                REQUEST
                Method: %s
                Path: %s
                Headers: %s%s%s""".formatted(
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), buildHeadersMap(httpServletRequest),
                !parameters.isEmpty() ? "\nParameters: " + parameters : "", body != null ? "\nBody: " + JsonUtils.toJson(body) : ""
        );

        log.info(logMessage);
    }

    @Override
    public void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body) {
        String startTime = ThreadContext.get(HTTP_REQUEST_START_TIME);
        ThreadContext.remove(HTTP_REQUEST_START_TIME);
        Long elapsedTime = System.currentTimeMillis() - Long.parseLong(startTime);
        ThreadContext.put(HTTP_REQUEST_DURATION, String.valueOf(elapsedTime));
        String logMessage = """
                RESPONSE
                Method: %s
                Path: %s
                Headers: %s
                Body: %s""".formatted(
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), buildHeadersMap(httpServletResponse),
                JsonUtils.toJson(body)
        );
        log.info(logMessage);
        ThreadContext.remove(HTTP_METHOD);
        ThreadContext.remove(HTTP_PATH);
        ThreadContext.remove(HTTP_REQUEST_DURATION);
    }

    private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    private Map<String, String> buildHeadersMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    private Map<String, String> buildHeadersMap(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();

        Collection<String> headerNames = response.getHeaderNames();
        for (String header : headerNames) {
            map.put(header, response.getHeader(header));
        }

        return map;
    }
}