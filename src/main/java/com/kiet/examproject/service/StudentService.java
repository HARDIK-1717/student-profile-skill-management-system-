package com.kiet.examproject.service;

import com.kiet.examproject.config.AppConfig;
import com.kiet.examproject.dao.StudentDao;
import com.kiet.examproject.model.Student;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * =============================================================================
 * SERVICE LAYER: StudentService
 * =============================================================================
 * Course: CS336B - Object Oriented Prog. Using Java (ODD SEM-2026-27)
 * Core Concepts & Viva Topics:
 *
 * 1. What is the Service Layer?
 *    The Service layer contains business logic, orchestrates workflows, enforces
 *    validation rules, and coordinates data access via the DAO layer. It sits
 *    between the Controller (handling HTTP requests) and the DAO (handling database queries).
 *
 * 2. @Service:
 *    Spring stereotype annotation designating this class as a Service component
 *    in the business layer, enabling Spring component scanning and transaction management.
 *
 * 3. Constructor Dependency Injection:
 *    Dependencies (StudentDao and ObjectProvider) are injected via constructor.
 *    This ensures fail-fast initialization, immutability, and simple unit testability.
 *
 * 4. Spring Bean Lifecycle Callbacks (@PostConstruct & @PreDestroy):
 *    - @PostConstruct: Executed immediately after dependency injection is complete
 *      and the bean is fully initialized.
 *    - @PreDestroy: Executed just before the Spring container destroys the bean
 *      during graceful application shutdown.
 *
 * 5. Java Collections Usage (Viva Requirement):
 *    Demonstrates Java Collections framework:
 *    - List<Student> (ArrayList) for ordered collections
 *    - Map<String, List<Student>> (TreeMap / HashMap) for grouping students by technical skills
 *    - Comparator.comparingDouble() and Collections.sort() for generating the Merit List
 * =============================================================================
 */
@Service
public class StudentService {

    // Primary DAO Dependency
    private final StudentDao studentDao;

    // Provider for Prototype-scoped Bean demonstration
    private final ObjectProvider<AppConfig.StudentAuditHelper> studentAuditHelperProvider;

    /**
     * Constructor Dependency Injection.
     *
     * @param studentDao Injected DAO bean
     * @param studentAuditHelperProvider Provider to obtain new prototype bean instances
     */
    public StudentService(StudentDao studentDao,
                          ObjectProvider<AppConfig.StudentAuditHelper> studentAuditHelperProvider) {
        this.studentDao = studentDao;
        this.studentAuditHelperProvider = studentAuditHelperProvider;
    }

    // =========================================================================
    // BEAN LIFECYCLE CALLBACKS
    // =========================================================================

    /**
     * Lifecycle Callback: @PostConstruct
     * Invoked by Spring after bean properties are initialized and dependencies injected.
     */
    @PostConstruct
    public void init() {
        System.out.println("=================================================================");
        System.out.println("[StudentService] >> @PostConstruct: StudentService bean initialized!");
        System.out.println("[StudentService] >> Ready to process student profiles and skills.");
        System.out.println("=================================================================");
    }

    /**
     * Lifecycle Callback: @PreDestroy
     * Invoked by Spring when the ApplicationContext is closing.
     */
    @PreDestroy
    public void cleanup() {
        System.out.println("=================================================================");
        System.out.println("[StudentService] >> @PreDestroy: StudentService bean is shutting down.");
        System.out.println("[StudentService] >> Releasing cached resources and completing audits.");
        System.out.println("=================================================================");
    }

    // =========================================================================
    // BUSINESS LOGIC & CRUD OPERATIONS
    // =========================================================================

    /**
     * Q3.1 & Q2.8: Add a student with business validation:
     * - Validates score range (0.0 to 100.0)
     * - Prevents duplicate student IDs
     * - Prevents duplicate emails
     * - Calculates metrics (average, category, stronger subject)
     * - Audits the operation using a fresh Prototype bean instance
     *
     * @param student The student to add
     * @return The persisted Student with generated ID
     */
    public Student createStudent(Student student) {
        // Validation 1: Required text fields
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is mandatory.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student Name is mandatory.");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email Address is mandatory.");
        }
        if (student.getCourse() == null || student.getCourse().trim().isEmpty()) {
            throw new IllegalArgumentException("Course Name is mandatory.");
        }

        // Validation 2 (Q2.8): Validate scores between 0 and 100
        validateScores(student.getProgrammingScore(), student.getMathematicsScore());

        // Validation 3 (Q2.8): Prevent duplicate student IDs
        if (studentDao.existsByStudentId(student.getStudentId().trim(), null)) {
            throw new IllegalArgumentException("Duplicate Student ID: A student with ID '" +
                    student.getStudentId().trim() + "' already exists.");
        }

        // Validation 4: Prevent duplicate email addresses
        if (studentDao.existsByEmail(student.getEmail().trim(), null)) {
            throw new IllegalArgumentException("Duplicate Email: A student with email '" +
                    student.getEmail().trim() + "' already exists.");
        }

        // Clean values
        student.setStudentId(student.getStudentId().trim());
        student.setName(student.getName().trim());
        student.setEmail(student.getEmail().trim());
        student.setCourse(student.getCourse().trim());

        // Calculate business metrics
        student.calculateMetrics();

        // Persist to MySQL
        int affected = studentDao.insert(student);
        if (affected <= 0) {
            throw new RuntimeException("Failed to insert student record into database.");
        }

        // Retrieve saved record to obtain generated primary key
        Student createdStudent = studentDao.findByStudentId(student.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student saved but could not be re-queried."));

        // Demonstrate Prototype bean usage
        AppConfig.StudentAuditHelper auditHelper = studentAuditHelperProvider.getObject();
        System.out.println(auditHelper.formatAuditLog(createdStudent, "CREATE_STUDENT"));

        return createdStudent;
    }

    /**
     * Q3.2: Retrieve all students.
     *
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        return studentDao.findAll();
    }

    /**
     * Q3.3: Search for a student by primary key ID.
     *
     * @param id Student primary key
     * @return Optional student
     */
    public Optional<Student> getStudentById(Long id) {
        return studentDao.findById(id);
    }

    /**
     * Search for a student by academic studentId (e.g. STU101).
     *
     * @param studentId Academic identifier
     * @return Optional student
     */
    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentDao.findByStudentId(studentId.trim());
    }

    /**
     * Q3.4: Update student scores, skills, personal info.
     *
     * @param id Primary key ID to update
     * @param updatedStudent Updated student data
     * @return Updated Student object
     */
    public Student updateStudent(Long id, Student updatedStudent) {
        // Check if student exists
        Student existing = studentDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student with ID " + id + " does not exist."));

        // Validate text fields if provided
        if (updatedStudent.getStudentId() != null && !updatedStudent.getStudentId().trim().isEmpty()) {
            if (studentDao.existsByStudentId(updatedStudent.getStudentId().trim(), id)) {
                throw new IllegalArgumentException("Duplicate Student ID: '" + updatedStudent.getStudentId().trim() + "' is already in use.");
            }
            existing.setStudentId(updatedStudent.getStudentId().trim());
        }

        if (updatedStudent.getName() != null && !updatedStudent.getName().trim().isEmpty()) {
            existing.setName(updatedStudent.getName().trim());
        }

        if (updatedStudent.getEmail() != null && !updatedStudent.getEmail().trim().isEmpty()) {
            if (studentDao.existsByEmail(updatedStudent.getEmail().trim(), id)) {
                throw new IllegalArgumentException("Duplicate Email: '" + updatedStudent.getEmail().trim() + "' is already in use.");
            }
            existing.setEmail(updatedStudent.getEmail().trim());
        }

        if (updatedStudent.getCourse() != null && !updatedStudent.getCourse().trim().isEmpty()) {
            existing.setCourse(updatedStudent.getCourse().trim());
        }

        // Validate and update scores
        Double progScore = updatedStudent.getProgrammingScore() != null ?
                updatedStudent.getProgrammingScore() : existing.getProgrammingScore();
        Double mathScore = updatedStudent.getMathematicsScore() != null ?
                updatedStudent.getMathematicsScore() : existing.getMathematicsScore();

        validateScores(progScore, mathScore);
        existing.setProgrammingScore(progScore);
        existing.setMathematicsScore(mathScore);

        // Update skills if provided
        if (updatedStudent.getTechnicalSkills() != null && !updatedStudent.getTechnicalSkills().isEmpty()) {
            existing.setTechnicalSkills(updatedStudent.getTechnicalSkills());
        } else if (updatedStudent.getSkills() != null) {
            existing.setSkills(updatedStudent.getSkills());
        }

        // Recalculate metrics
        existing.calculateMetrics();

        // Persist update
        int rows = studentDao.update(id, existing);
        if (rows <= 0) {
            throw new RuntimeException("Database update failed for student ID: " + id);
        }

        // Prototype bean audit
        AppConfig.StudentAuditHelper auditHelper = studentAuditHelperProvider.getObject();
        System.out.println(auditHelper.formatAuditLog(existing, "UPDATE_STUDENT"));

        return existing;
    }

    /**
     * Q3.5: Delete a student record by ID.
     *
     * @param id Primary key ID to delete
     * @return true if deleted
     */
    public boolean deleteStudent(Long id) {
        Student student = studentDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot delete: Student with ID " + id + " does not exist."));

        boolean deleted = studentDao.deleteById(id);
        if (deleted) {
            AppConfig.StudentAuditHelper auditHelper = studentAuditHelperProvider.getObject();
            System.out.println(auditHelper.formatAuditLog(student, "DELETE_STUDENT"));
        }
        return deleted;
    }

    // =========================================================================
    // EXAM QUESTION 2 & 3 ADVANCED BUSINESS QUERIES & COLLECTIONS DEMOS
    // =========================================================================

    /**
     * Q2.5 & Q3.6: Generate a merit list sorted by average score in descending order.
     * Demonstrates Java Collections Framework (List, Comparator, Collections.sort).
     *
     * @return Merit list sorted by average score descending
     */
    public List<Student> getMeritList() {
        List<Student> students = new ArrayList<>(studentDao.findAll());

        // Java Collections sorting demonstration for practical viva
        students.sort(Comparator.comparingDouble(Student::getAverageScore).reversed());

        return students;
    }

    /**
     * Q2.4: Search for students by ID (studentId or primary key), email, or course.
     *
     * @param query Search query keyword
     * @return List of matching students
     */
    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return studentDao.findAll();
        }
        return studentDao.search(query.trim());
    }

    /**
     * Q2.6: Identify students who have the same technical skills.
     * Filters students possessing a given technical skill (case-insensitive).
     *
     * @param skill The skill name (e.g. "Java", "Python")
     * @return List of students who possess this skill
     */
    public List<Student> getStudentsBySkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            return studentDao.findAll();
        }
        String targetSkill = skill.trim().toLowerCase();
        List<Student> all = studentDao.findAll();

        // Java Collections stream filtering demonstration
        return all.stream()
                .filter(s -> s.getTechnicalSkills().stream()
                        .anyMatch(sk -> sk.equalsIgnoreCase(targetSkill)))
                .collect(Collectors.toList());
    }

    /**
     * Q2.6: Generates a Map grouping all students by their technical skills.
     * Demonstrates Java Collections: Map<String, List<Student>>.
     *
     * @return Map where keys are skill names and values are lists of students with that skill
     */
    public Map<String, List<Student>> getSkillDistribution() {
        List<Student> allStudents = studentDao.findAll();
        Map<String, List<Student>> skillMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (Student student : allStudents) {
            for (String skill : student.getTechnicalSkills()) {
                String normalized = skill.trim();
                if (!normalized.isEmpty()) {
                    skillMap.computeIfAbsent(normalized, k -> new ArrayList<>()).add(student);
                }
            }
        }
        return skillMap;
    }

    /**
     * Q2.7: Display students who need improvement in programming (< 50)
     * but have a mathematics score of at least 75.
     *
     * @return Filtered list of students
     */
    public List<Student> getSpecialAttentionStudents() {
        return studentDao.findSpecialAttentionStudents();
    }

    /**
     * Dashboard Summary Statistics.
     * Aggregates key metrics for the modern UI dashboard cards.
     *
     * @return Map containing statistics
     */
    public Map<String, Object> getDashboardStats() {
        List<Student> all = studentDao.findAll();
        Map<String, Object> stats = new HashMap<>();

        int totalStudents = all.size();
        stats.put("totalStudents", totalStudents);

        if (totalStudents == 0) {
            stats.put("classAverage", 0.0);
            stats.put("topPerformer", "N/A");
            stats.put("topScore", 0.0);
            stats.put("specialAttentionCount", 0);
            stats.put("excellentCount", 0);
            stats.put("goodCount", 0);
            stats.put("satisfactoryCount", 0);
            stats.put("needsImprovementCount", 0);
            return stats;
        }

        double totalAvg = all.stream().mapToDouble(Student::getAverageScore).sum();
        double classAvg = Math.round((totalAvg / totalStudents) * 100.0) / 100.0;
        stats.put("classAverage", classAvg);

        Student topStudent = all.stream()
                .max(Comparator.comparingDouble(Student::getAverageScore))
                .orElse(null);

        if (topStudent != null) {
            stats.put("topPerformer", topStudent.getName() + " (" + topStudent.getStudentId() + ")");
            stats.put("topScore", topStudent.getAverageScore());
        } else {
            stats.put("topPerformer", "N/A");
            stats.put("topScore", 0.0);
        }

        long specialCount = all.stream().filter(s -> Boolean.TRUE.equals(s.getNeedsProgrammingImprovement())).count();
        stats.put("specialAttentionCount", specialCount);

        long excellent = all.stream().filter(s -> "Excellent".equalsIgnoreCase(s.getPerformanceCategory())).count();
        long good = all.stream().filter(s -> "Good".equalsIgnoreCase(s.getPerformanceCategory())).count();
        long satisfactory = all.stream().filter(s -> "Satisfactory".equalsIgnoreCase(s.getPerformanceCategory())).count();
        long needsImp = all.stream().filter(s -> "Needs Improvement".equalsIgnoreCase(s.getPerformanceCategory())).count();

        stats.put("excellentCount", excellent);
        stats.put("goodCount", good);
        stats.put("satisfactoryCount", satisfactory);
        stats.put("needsImprovementCount", needsImp);

        return stats;
    }

    /**
     * Raw JDBC Demonstration: Delegates to DAO helper method.
     *
     * @param id Student ID
     * @return Optional Student found via raw JDBC
     */
    public Optional<Student> getStudentByIdRawJdbc(Long id) {
        return studentDao.findByIdRawJdbc(id);
    }

    /**
     * Prototype Bean Demonstration helper.
     *
     * @return Info map showing new prototype bean instance ID and timestamp
     */
    public Map<String, Object> demonstratePrototypeBean() {
        AppConfig.StudentAuditHelper auditHelper = studentAuditHelperProvider.getObject();
        Map<String, Object> info = new HashMap<>();
        info.put("instanceId", auditHelper.getInstanceId());
        info.put("createdAt", auditHelper.getCreatedAtFormatted());
        info.put("scope", "prototype");
        info.put("message", "A brand new prototype bean instance was created by the Spring IoC container.");
        return info;
    }

    // =========================================================================
    // HELPER VALIDATION METHODS
    // =========================================================================

    /**
     * Q2.8: Validates that academic scores are within the valid range [0.0, 100.0].
     *
     * @param prog Programming score
     * @param math Mathematics score
     */
    private void validateScores(Double prog, Double math) {
        if (prog == null || prog < 0.0 || prog > 100.0) {
            throw new IllegalArgumentException("Programming score must be a valid number between 0 and 100. Received: " + prog);
        }
        if (math == null || math < 0.0 || math > 100.0) {
            throw new IllegalArgumentException("Mathematics score must be a valid number between 0 and 100. Received: " + math);
        }
    }
}
