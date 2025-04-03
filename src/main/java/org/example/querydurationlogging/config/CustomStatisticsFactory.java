package org.example.querydurationlogging.config;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.spi.StatisticsFactory;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unused")
public class CustomStatisticsFactory implements StatisticsFactory {
    @Override
    public StatisticsImplementor buildStatistics(@Autowired SessionFactoryImplementor sessionFactory) {
        return new CustomStatisticsImpl(sessionFactory);
    }

}
