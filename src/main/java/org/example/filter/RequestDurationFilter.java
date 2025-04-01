package org.example.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Spliterators;
import java.util.Spliterator;

@Component
public class RequestDurationFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String REQUEST_DURATION_KEY = "requestDuration";
    private static final Logger logger = LogManager.getLogger(RequestDurationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString();
        ThreadContext.put(TRACE_ID_KEY, traceId);
        
        // Wrap request and response to allow multiple reads
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        // Log request details
        Map<String, Object> requestLog = new HashMap<>();
        requestLog.put("method", request.getMethod());
        requestLog.put("uri", request.getRequestURI());
        requestLog.put("queryString", request.getQueryString());
        requestLog.put("headers", getHeaders(request));
        requestLog.put("body", getRequestBody(requestWrapper));
        
        logger.info("HTTP Request: {}", requestLog);
        
        long startTime = System.currentTimeMillis();
        
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            ThreadContext.put(REQUEST_DURATION_KEY, String.valueOf(duration));
            
            // Log response details
            Map<String, Object> responseLog = new HashMap<>();
            responseLog.put("status", response.getStatus());
            responseLog.put("headers", getHeaders(response));
            responseLog.put("body", getResponseBody(responseWrapper));
            responseLog.put("duration", duration);
            
            logger.info("HTTP Response: {}", responseLog);
            
            // Copy response body back to original response
            responseWrapper.copyBodyToResponse();
            ThreadContext.clearAll();
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                request.getHeaderNames().asIterator(), Spliterator.ORDERED), false)
                .collect(Collectors.toMap(
                        headerName -> headerName,
                        request::getHeader
                ));
    }

    private Map<String, String> getHeaders(HttpServletResponse response) {
        return response.getHeaderNames().stream()
                .collect(Collectors.toMap(
                        headerName -> headerName,
                        response::getHeader
                ));
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return null;
        }
        try {
            return new String(content, request.getCharacterEncoding());
        } catch (Exception e) {
            return "[Unable to read request body]";
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length == 0) {
            return null;
        }
        try {
            return new String(content, response.getCharacterEncoding());
        } catch (Exception e) {
            return "[Unable to read response body]";
        }
    }
} 