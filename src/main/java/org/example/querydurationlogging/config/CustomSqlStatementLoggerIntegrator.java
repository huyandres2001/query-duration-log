package org.example.querydurationlogging.config;


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
        boolean logToStdout = true; // Set based on your requirements
        boolean format = true;      // Set based on your requirements
        int slowQueryThreshold = 1000; // Set based on your requirements

        // Create your custom logger
        String property = System.getProperty("spring.jpa.properties.hibernate.highlight_sql");
        ;

        SqlStatementLogger customLogger = new CustomSqlStatementLogger(
                logToStdout, format,Boolean.parseBoolean(property), slowQueryThreshold);

        // Replace the logger in the JDBC services
//        jdbcServices.getSqlStatementLogger().disableLogging(); // Disable the original logger
// no need to disable since we can change the log level
        // Use reflection to replace the logger
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