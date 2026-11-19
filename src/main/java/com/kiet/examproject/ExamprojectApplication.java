package com.kiet.examproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * =============================================================================
 * APPLICATION ENTRY POINT: ExamprojectApplication
 * =============================================================================
 * Course: CS336B - Object Oriented Prog. Using Java (ODD SEM-2026-27)
 * Project: Student Profile and Skill Development Management System
 * Package: com.kiet.examproject
 *
 * Core Annotations & Architecture (Viva Preparation):
 * 1. @SpringBootApplication:
 *    A composite annotation that bundles three essential Spring annotations:
 *    - @Configuration: Designates this class as a source of Spring bean definitions.
 *    - @EnableAutoConfiguration: Configures beans automatically according to
 *      dependencies found on the classpath (e.g. DataSource, JdbcTemplate, DispatcherServlet).
 *    - @ComponentScan: Scans the base package 'com.kiet.examproject' and all sub-packages
 *      (model, dao, service, controller, config, aspect) to discover Spring-managed
 *      components (@Component, @Repository, @Service, @RestController).
 *
 * 2. @EnableAspectJAutoProxy:
 *    Enables Spring's AspectJ runtime proxy generation, allowing LoggingAspect to
 *    intercept method executions in the controller and service packages.
 * =============================================================================
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class ExamprojectApplication {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  STARTING STUDENT PROFILE & SKILL DEVELOPMENT MANAGEMENT SYSTEM");
        System.out.println("  Target Port: 8080 | Base REST API: /api/students");
        System.out.println("  Database: MySQL (mse_java_exam_db) | Stack: Spring JDBC + AOP");
        System.out.println("=================================================================");

        SpringApplication.run(ExamprojectApplication.class, args);

        System.out.println("=================================================================");
        System.out.println("  Student Profile & Skill Development Management System is LIVE!");
        System.out.println("  Access Web Dashboard at: http://localhost:8080/");
        System.out.println("  Core REST API Endpoints:");
        System.out.println("    - GET    /api/students                   (Retrieve All Students)");
        System.out.println("    - GET    /api/students/{id}              (Search Student by ID)");
        System.out.println("    - POST   /api/students                   (Add Student with Validation)");
        System.out.println("    - PUT    /api/students/{id}              (Update Scores and Skills)");
        System.out.println("    - DELETE /api/students/{id}              (Delete Student Record)");
        System.out.println("    - GET    /api/students/merit-list        (Display Merit List - Descending)");
        System.out.println("    - GET    /api/students/search?query=...  (Search by ID, Email, Course)");
        System.out.println("    - GET    /api/students/by-skill?skill=.. (Filter by Technical Skill)");
        System.out.println("    - GET    /api/students/special-attention (Programming < 50 & Math >= 75)");
        System.out.println("    - GET    /api/students/stats             (Aggregated Dashboard Stats)");
        System.out.println("    - GET    /api/students/raw-jdbc/{id}     (Raw JDBC Demonstration)");
        System.out.println("    - GET    /api/students/prototype-demo    (Prototype Bean Scope Demo)");
        System.out.println("=================================================================");
    }
}
