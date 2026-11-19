package com.kiet.examproject.config;

import com.kiet.examproject.model.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * CONFIGURATION LAYER: AppConfig
 * =============================================================================
 * Course: CS336B - Object Oriented Prog. Using Java (ODD SEM-2026-27)
 * Core Concepts: Spring Bean Configuration, IoC Container & Bean Scopes
 *
 * 1. What is @Configuration?
 *    Indicates that a class declares one or more @Bean methods and may be processed
 *    by the Spring IoC container to generate bean definitions and service requests.
 *
 * 2. SPRING BEAN SCOPES EXPLANATION (Viva Preparation):
 *    - Singleton (Default Scope):
 *      Spring creates ONLY ONE instance of the bean per Spring IoC Container.
 *      Whenever requested, the exact same shared instance is returned.
 *      Ideal for stateless beans such as Controllers, Services, and DAOs.
 *
 *    - Prototype Scope (@Scope("prototype")):
 *      Spring creates a BRAND NEW INSTANCE every single time the bean is requested
 *      from the container (e.g., via ObjectProvider.getObject() or ApplicationContext.getBean()).
 *      Ideal for stateful objects, record processors, or temporary audit helpers.
 *
 *    - Web-Aware Scopes (Available only in Web Applications):
 *      a) Request Scope (@RequestScope):
 *         A single instance is created for the lifecycle of a single HTTP request.
 *      b) Session Scope (@SessionScope):
 *         A single instance is created for the lifecycle of an HTTP Session.
 *      c) Application Scope (@ApplicationScope):
 *         A single instance is created for the lifecycle of the ServletContext.
 *      d) WebSocket Scope:
 *         A single instance is bound to the lifecycle of a WebSocket session.
 * =============================================================================
 */
@Configuration
public class AppConfig {

    /**
     * Declares a Prototype-scoped Spring Bean.
     * Every time this bean is requested (via ObjectProvider in StudentService),
     * the Spring container invokes this factory method to produce a fresh,
     * independent instance.
     *
     * @return a new StudentAuditHelper instance
     */
    @Bean
    @Scope("prototype")
    public StudentAuditHelper studentAuditHelper() {
        return new StudentAuditHelper();
    }

    /**
     * Helper utility class demonstrating stateful prototype bean behavior.
     * Tracks its unique instance ID and timestamp of creation.
     */
    public static class StudentAuditHelper {

        private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger(0);
        private final int instanceId;
        private final LocalDateTime createdAt;

        public StudentAuditHelper() {
            this.instanceId = INSTANCE_COUNTER.incrementAndGet();
            this.createdAt = LocalDateTime.now();
            System.out.println("[AppConfig] >> New PROTOTYPE Bean (StudentAuditHelper) Instance #" +
                               instanceId + " created at " + createdAt.format(DateTimeFormatter.ISO_LOCAL_TIME));
        }

        public int getInstanceId() {
            return instanceId;
        }

        public String getCreatedAtFormatted() {
            return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        /**
         * Formats an audit log message for student profile processing.
         * Demonstrates practical usage of this prototype bean in business operations.
         */
        public String formatAuditLog(Student student, String operation) {
            return String.format("[Audit #%d | %s] Student ID: %s, Name: %s, Course: %s, Avg: %.2f (%s)",
                    instanceId, operation,
                    student.getStudentId(), student.getName(), student.getCourse(),
                    student.getAverageScore(), student.getPerformanceCategory());
        }
    }
}
