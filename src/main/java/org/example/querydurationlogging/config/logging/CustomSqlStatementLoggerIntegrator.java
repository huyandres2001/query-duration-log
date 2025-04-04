package org.example.querydurationlogging.config.logging;


import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.integrator.spi.Integrator;


public class CustomSqlStatementLoggerIntegrator implements Integrator {

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {
        JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
        // Get configuration properties
        boolean logToStdout = true;
        boolean format = true;
        int slowQueryThreshold = 1000;

        // Create your custom logger
        String property = System.getProperty("spring.jpa.properties.hibernate.highlight_sql");

        SqlStatementLogger customLogger = new CustomSqlStatementLogger(
                logToStdout, format,Boolean.parseBoolean(property), slowQueryThreshold);

        try {
            java.lang.reflect.Field loggerField = jdbcServices.getClass().getDeclaredField("sqlStatementLogger");
            loggerField.setAccessible(true);
            loggerField.set(jdbcServices, customLogger);
        } catch (Exception e) {
            throw new RuntimeException("Could not replace SqlStatementLogger", e);
        }
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {
        // Nothing to do here
    }
}