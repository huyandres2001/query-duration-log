package org.example.querydurationlogging;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class QueryExecutionLoggingAspect {

    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
    public Object logQueryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // Execute the actual query
        long duration = System.currentTimeMillis() - startTime;
        log.info("Query Method: {} executed in {} ms", joinPoint.getSignature(), duration);
        return result;
    }
}
