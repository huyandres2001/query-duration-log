package org.example.querydurationlogging.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.internal.StatisticsImpl;

@Slf4j
public class CustomStatisticsImpl extends StatisticsImpl {

    public CustomStatisticsImpl(
            @UnknownKeyFor @NonNull @Initialized SessionFactoryImplementor sessionFactory
    ) {
        super(sessionFactory);
    }
//
//    @Override
//    public void queryExecuted(
//            @UnknownKeyFor @NonNull @Initialized String hql,
//            @UnknownKeyFor @NonNull @Initialized int rows,
//            @UnknownKeyFor @NonNull @Initialized long time
//    ) {
//        //for read only query
//        ThreadContext.put("duration", Long.toString(time));
//        log.info("[CustomStatisticsImpl][queryExecuted] hql: {} rows: {}", hql, rows);
//        super.queryExecuted(hql, rows, time);
//        ThreadContext.remove("duration");
//    }
}
