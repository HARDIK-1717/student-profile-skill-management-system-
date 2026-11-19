package com.kiet.examproject.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * =============================================================================
 * MODEL LAYER: Student (Entity Class)
 * =============================================================================
 * Course: CS336B - Object Oriented Prog. Using Java (ODD SEM-2026-27)
 * Exam Question 1 (CO4 - 5 Marks):
 * Attributes:
 * - Student ID
 * - Student name
 * - Email address
 * - Course name
 * - Programming score
 * - Mathematics score
 * - List of technical skills (Java Collections: List<String>)
 *
 * Core OOP & Java Concepts (Viva Preparation):
 * 1. Encapsulation: All fields are declared private to prevent direct external
 *    access. Controlled access and business validation are provided through
 *    public getters and setters.
 * 2. POJO (Plain Old Java Object): Acts as a simple data transfer and domain model
 *    independent of any heavy framework bindings or ORMs.
 * 3. Java Collections: Utilizes java.util.List<String> to hold skills dynamically,
 *    demonstrating collection manipulation (splitting, joining, trimming).
 * 4. Derived Business Attributes (Question 2):
 *    - averageScore = (programmingScore + mathematicsScore) / 2.0
 *    - performanceCategory = Excellent (>=85), Good (70-84.99), Satisfactory (50-69.99), Needs Improvement (<50)
 *    - strongerSubject = Programming, Mathematics, or Equal
 *    - needsProgrammingImprovement = programmingScore < 50 && mathematicsScore >= 75
 * =============================================================================
 */
public class Student {

    // Database Primary Key (Auto-Increment)
    private Long id;

    // Student Identification Attributes
    private String studentId;      // e.g., "STU101"
    private String name;           // e.g., "Aarav Sharma"
    private String email;          // e.g., "aarav.sharma@kiet.edu"
    private String course;         // e.g., "B.Tech CSE"

    // Academic Scores (Validated between 0.0 and 100.0)
    private Double programmingScore;
    private Double mathematicsScore;

    // Database representation: comma-separated string (e.g., "Java, Spring Boot, MySQL")
    private String skills;

    // Java Collections representation: List of strings for syllabus collection requirement
    private List<String> technicalSkills = new ArrayList<>();

    // Business Logic / Calculated Fields
    private Double averageScore;
    private String performanceCategory;
    private String strongerSubject;
    private Boolean needsProgrammingImprovement;

    /**
     * Default No-Argument Constructor.
     * Required by frameworks for reflection and JSON deserialization.
     */
    public Student() {
    }

    /**
     * Parameterized Constructor without primary key id.
     * Commonly used when creating a new Student before database insertion.
     */
    public Student(String studentId, String name, String email, String course,
                   Double programmingScore, Double mathematicsScore, List<String> technicalSkills) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.course = course;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        setTechnicalSkills(technicalSkills);
        calculateMetrics();
    }

    /**
     * Full Parameterized Constructor including database ID.
     */
    public Student(Long id, String studentId, String name, String email, String course,
                   Double programmingScore, Double mathematicsScore, String skills) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.course = course;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        setSkills(skills);
        calculateMetrics();
    }

    /**
     * Business Logic Method (Exam Question 2):
     * 1. Calculates average score.
     * 2. Assigns performance category based on defined thresholds.
     * 3. Identifies the stronger subject.
     * 4. Flags special attention students (programming < 50 && mathematics >= 75).
     */
    public void calculateMetrics() {
        if (programmingScore != null && mathematicsScore != null) {
            // 1. Calculate Average Score rounded to 2 decimal places
            double avg = (programmingScore + mathematicsScore) / 2.0;
            BigDecimal bd = BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
            this.averageScore = bd.doubleValue();

            // 2. Assign Performance Category based on average
            if (this.averageScore >= 85.0) {
                this.performanceCategory = "Excellent";
            } else if (this.averageScore >= 70.0) {
                this.performanceCategory = "Good";
            } else if (this.averageScore >= 50.0) {
                this.performanceCategory = "Satisfactory";
            } else {
                this.performanceCategory = "Needs Improvement";
            }

            // 3. Identify Stronger Subject
            if (Double.compare(programmingScore, mathematicsScore) > 0) {
                this.strongerSubject = "Programming";
            } else if (Double.compare(mathematicsScore, programmingScore) > 0) {
                this.strongerSubject = "Mathematics";
            } else {
                this.strongerSubject = "Equal";
            }

            // 4. Special Rule (Q2.7): Needs programming improvement (<50) but math >= 75
            this.needsProgrammingImprovement = (programmingScore < 50.0 && mathematicsScore >= 75.0);
        } else {
            this.averageScore = 0.0;
            this.performanceCategory = "N/A";
            this.strongerSubject = "N/A";
            this.needsProgrammingImprovement = false;
        }
    }

    // =========================================================================
    // GETTERS AND SETTERS (Encapsulation)
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(Double programmingScore) {
        this.programmingScore = programmingScore;
        calculateMetrics();
    }

    public Double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(Double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
        calculateMetrics();
    }

    public String getSkills() {
        return skills;
    }

    /**
     * Sets comma-separated skills string and synchronizes the Java Collection List.
     */
    public void setSkills(String skills) {
        this.skills = skills != null ? skills.trim() : "";
        if (this.skills.isEmpty()) {
            this.technicalSkills = new ArrayList<>();
        } else {
            this.technicalSkills = Arrays.stream(this.skills.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    public List<String> getTechnicalSkills() {
        if (technicalSkills == null || technicalSkills.isEmpty()) {
            if (skills != null && !skills.trim().isEmpty()) {
                this.technicalSkills = Arrays.stream(skills.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            } else {
                this.technicalSkills = new ArrayList<>();
            }
        }
        return technicalSkills;
    }

    /**
     * Sets technical skills list using Java Collections and synchronizes the comma-separated string.
     */
    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = technicalSkills != null ? technicalSkills : new ArrayList<>();
        this.skills = String.join(", ", this.technicalSkills);
    }

    public Double getAverageScore() {
        if (averageScore == null) {
            calculateMetrics();
        }
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public String getPerformanceCategory() {
        if (performanceCategory == null) {
            calculateMetrics();
        }
        return performanceCategory;
    }

    public void setPerformanceCategory(String performanceCategory) {
        this.performanceCategory = performanceCategory;
    }

    public String getStrongerSubject() {
        if (strongerSubject == null) {
            calculateMetrics();
        }
        return strongerSubject;
    }

    public void setStrongerSubject(String strongerSubject) {
        this.strongerSubject = strongerSubject;
    }

    public Boolean getNeedsProgrammingImprovement() {
        if (needsProgrammingImprovement == null) {
            calculateMetrics();
        }
        return needsProgrammingImprovement;
    }

    public void setNeedsProgrammingImprovement(Boolean needsProgrammingImprovement) {
        this.needsProgrammingImprovement = needsProgrammingImprovement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) || Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", course='" + course + '\'' +
                ", programmingScore=" + programmingScore +
                ", mathematicsScore=" + mathematicsScore +
                ", averageScore=" + averageScore +
                ", performanceCategory='" + performanceCategory + '\'' +
                ", strongerSubject='" + strongerSubject + '\'' +
                ", technicalSkills=" + technicalSkills +
                '}';
    }
}
