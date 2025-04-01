//package org.example.querydurationlogging;
//
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.SessionEventListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class SlowQuerySessionEventListener implements SessionEventListener {
//
//    @Override
//    public void transactionCompletion(SessionEvent event) {
//        if (event.getSession().getStatistics().getQueryExecutionCount() > 0) {
//            long executionTime = event.getSession().getStatistics().getQueryExecutionMaxTime();
//            if (executionTime > 5000) { // 5 seconds
//                log.warn("Slow query detected: execution time was {} ms", executionTime);
//                String slowQuery = event.getSession().getStatistics().getQueryExecutionMaxTimeQueryString();
//                log.warn("Slow query: {}", slowQuery);
//            }
//        }
//    }
//
//    // other methods can be overridden here
//}