package org.example.querydurationlogging.service;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.example.querydurationlogging.common.JsonUtils;
import org.springframework.stereotype.Service;

@Service
@Log
public class LoggingServiceImpl implements LoggingService {

    @Override
    public void logRequest(HttpServletRequest httpServletRequest, Object body) {
        Map<String, String> parameters = buildParametersMap(httpServletRequest);
        String logMessage = """
                REQUEST
                Method: %s
                Path: %s
                Headers: %s%s%s""".formatted(
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                buildHeadersMap(httpServletRequest),
                !parameters.isEmpty() ? "\nParameters: " + parameters : "",
                body != null ? "\nBody: " + JsonUtils.toJson(body) : ""
        );

        log.info(logMessage);
    }

    @Override
    public void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body) {
        String logMessage = """
                RESPONSE
                Method: %s
                Path: %s
                Headers: %s
                Body: %s""".formatted(
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                buildHeadersMap(httpServletResponse),
                JsonUtils.toJson(body)
        );

        log.info(logMessage);
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

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
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