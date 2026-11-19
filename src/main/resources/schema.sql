-- =============================================================================
-- Schema Definition for Student Profile and Skill Development Management System
-- Course: CS336B - Object Oriented Prog. Using Java (ODD SEM-2026-27)
-- Database: mse_java_exam_db
-- =============================================================================
-- This script is automatically executed by Spring Boot on startup because of:
-- spring.sql.init.mode=always
--
-- NOTE FOR VIVA:
-- Automatic database initialization executes DDL/DML inside an EXISTING database.
-- The database `mse_java_exam_db` itself must be created beforehand in MySQL:
-- CREATE DATABASE IF NOT EXISTS mse_java_exam_db;
-- =============================================================================

CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    course VARCHAR(100) NOT NULL,
    programming_score DOUBLE NOT NULL,
    mathematics_score DOUBLE NOT NULL,
    skills VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
