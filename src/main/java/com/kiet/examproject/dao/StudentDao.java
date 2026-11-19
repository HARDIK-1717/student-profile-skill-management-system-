package com.kiet.examproject.dao;

import com.kiet.examproject.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * =============================================================================
 * DAO LAYER: StudentDao (Data Access Object)
 * =============================================================================
 * Course: CS336B - Object Oriented Prog. Using Java (ODD SEM-2026-27)
 * Core Concepts & Viva Topics:
 *
 * 1. What is a DAO (Data Access Object)?
 *    A design pattern that encapsulates all direct database interactions,
 *    isolating the application's business layer from database implementation details.
 *
 * 2. @Repository:
 *    Spring stereotype annotation marking this class as a Data Access component.
 *    It enables Spring's automatic exception translation, converting low-level
 *    SQLExceptions into Spring's unchecked DataAccessException hierarchy.
 *
 * 3. What is JdbcTemplate?
 *    A central Spring JDBC utility class that executes SQL queries, updates,
 *    and stored procedures. It automates connection acquisition, statement creation,
 *    parameter binding, result set iteration, and resource cleanup (closing connections).
 *
 * 4. What is a RowMapper?
 *    An interface used by JdbcTemplate to map each row of a ResultSet to a
 *    Java domain object (Student in this case).
 *
 * 5. Constructor Injection vs Field Injection:
 *    - Constructor Injection (Primary): JdbcTemplate is injected via constructor.
 *      Best practice for testability, immutability, and dependency safety.
 *    - Field Injection (Demonstration): @Autowired DataSource dataSource demonstrates
 *      field injection explicitly for the exam syllabus.
 *
 * 6. Raw JDBC Demonstration (Section 5.F):
 *    Demonstrates traditional raw JDBC using Class.forName(), DriverManager,
 *    Connection, PreparedStatement, and ResultSet inside try-with-resources.
 * =============================================================================
 */
@Repository
public class StudentDao {

    // Primary Dependency: Spring JdbcTemplate
    private final JdbcTemplate jdbcTemplate;

    // =========================================================================
    // SYLLABUS DEMO: @Autowired Field Injection
    // =========================================================================
    // In modern Spring applications, Constructor Injection is preferred (shown below).
    // Here, @Autowired field injection is explicitly demonstrated on DataSource
    // to satisfy the exam syllabus and enable viva evaluation.
    @Autowired
    private DataSource dataSource;

    // Database connection details injected from application.properties for Raw JDBC Demo
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    /**
     * RowMapper implementation for Student entity.
     * Maps database columns from the SQL ResultSet into a Student POJO.
     */
    private final RowMapper<Student> studentRowMapper = (rs, rowNum) -> {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setStudentId(rs.getString("student_id"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setCourse(rs.getString("course"));
        student.setProgrammingScore(rs.getDouble("programming_score"));
        student.setMathematicsScore(rs.getDouble("mathematics_score"));
        student.setSkills(rs.getString("skills"));
        student.calculateMetrics();
        return student;
    };

    /**
     * Constructor Dependency Injection (Recommended Best Practice).
     * The Spring IoC container automatically provides the configured JdbcTemplate bean.
     *
     * @param jdbcTemplate Spring's JdbcTemplate bean
     */
    public StudentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================================
    // SPRING JDBCTEMPLATE CRUD OPERATIONS
    // =========================================================================

    /**
     * A. INSERT: Inserts a new student record into MySQL using parameterized SQL.
     *
     * @param student The student model to persist
     * @return Number of rows affected (1 on success)
     */
    public int insert(Student student) {
        String sql = "INSERT INTO students (student_id, name, email, course, programming_score, mathematics_score, skills) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getCourse(),
                student.getProgrammingScore(),
                student.getMathematicsScore(),
                student.getSkills());
    }

    /**
     * B. SELECT ALL: Retrieves all student records from MySQL.
     * Uses RowMapper to transform each database row into a Student object.
     *
     * @return List of all students
     */
    public List<Student> findAll() {
        String sql = "SELECT * FROM students ORDER BY id ASC";
        return jdbcTemplate.query(sql, studentRowMapper);
    }

    /**
     * C. SELECT BY ID: Retrieves a single student by primary key ID.
     * Handles EmptyResultDataAccessException gracefully and returns Optional.
     *
     * @param id The auto-increment primary key
     * @return Optional containing the student if found, or Optional.empty()
     */
    public Optional<Student> findById(Long id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, studentRowMapper, id);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * SELECT BY STUDENT ID: Retrieves a student by unique academic studentId (e.g. STU101).
     *
     * @param studentId The unique student code
     * @return Optional containing the student if found
     */
    public Optional<Student> findByStudentId(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, studentRowMapper, studentId);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * SELECT BY EMAIL: Retrieves a student by unique email address.
     *
     * @param email The unique student email
     * @return Optional containing the student if found
     */
    public Optional<Student> findByEmail(String email) {
        String sql = "SELECT * FROM students WHERE email = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, studentRowMapper, email);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * D. UPDATE BY ID: Updates academic scores, skills, course, and personal details.
     *
     * @param id The primary key of the student to update
     * @param student The updated student values
     * @return Number of rows updated (1 if updated, 0 if ID not found)
     */
    public int update(Long id, Student student) {
        String sql = "UPDATE students SET student_id = ?, name = ?, email = ?, course = ?, " +
                     "programming_score = ?, mathematics_score = ?, skills = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getCourse(),
                student.getProgrammingScore(),
                student.getMathematicsScore(),
                student.getSkills(),
                id);
    }

    /**
     * E. DELETE BY ID: Deletes a student record by ID.
     *
     * @param id The primary key ID to delete
     * @return true if a row was deleted, false otherwise
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM students WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    // =========================================================================
    // EXAM QUESTION 2 QUERIES & VALIDATIONS
    // =========================================================================

    /**
     * Q2.4: Search students by studentId, name, email, or course.
     *
     * @param query Search keyword
     * @return List of matching students
     */
    public List<Student> search(String query) {
        String pattern = "%" + query.trim() + "%";
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR name LIKE ? OR email LIKE ? OR course LIKE ? ORDER BY id ASC";
        return jdbcTemplate.query(sql, studentRowMapper, pattern, pattern, pattern, pattern);
    }

    /**
     * Q2.5: Retrieves merit list sorted by average score in descending order.
     *
     * @return List of students sorted by descending average score
     */
    public List<Student> findAllOrderByAverageScoreDesc() {
        String sql = "SELECT * FROM students ORDER BY ((programming_score + mathematics_score) / 2.0) DESC";
        return jdbcTemplate.query(sql, studentRowMapper);
    }

    /**
     * Q2.6: Finds students who possess the specified technical skill.
     *
     * @param skill The technical skill to filter by (e.g. "Java", "Python")
     * @return List of students possessing the skill
     */
    public List<Student> findByTechnicalSkill(String skill) {
        String pattern = "%" + skill.trim() + "%";
        String sql = "SELECT * FROM students WHERE skills LIKE ? ORDER BY id ASC";
        return jdbcTemplate.query(sql, studentRowMapper, pattern);
    }

    /**
     * Q2.7: Displays students who need improvement in programming (< 50)
     * but have a mathematics score of at least 75.
     *
     * @return List of students requiring targeted programming intervention
     */
    public List<Student> findSpecialAttentionStudents() {
        String sql = "SELECT * FROM students WHERE programming_score < 50 AND mathematics_score >= 75 ORDER BY id ASC";
        return jdbcTemplate.query(sql, studentRowMapper);
    }

    /**
     * Q2.8: Checks whether a studentId already exists (for duplicate prevention).
     *
     * @param studentId The student ID to check
     * @param excludeId Optional ID to exclude (used during updates)
     * @return true if studentId already exists in another record
     */
    public boolean existsByStudentId(String studentId, Long excludeId) {
        if (excludeId == null) {
            String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
            return count != null && count > 0;
        } else {
            String sql = "SELECT COUNT(*) FROM students WHERE student_id = ? AND id <> ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId, excludeId);
            return count != null && count > 0;
        }
    }

    /**
     * Checks whether an email already exists (for duplicate prevention).
     *
     * @param email The email to check
     * @param excludeId Optional ID to exclude (used during updates)
     * @return true if email already exists in another record
     */
    public boolean existsByEmail(String email, Long excludeId) {
        if (excludeId == null) {
            String sql = "SELECT COUNT(*) FROM students WHERE email = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
            return count != null && count > 0;
        } else {
            String sql = "SELECT COUNT(*) FROM students WHERE email = ? AND id <> ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email, excludeId);
            return count != null && count > 0;
        }
    }

    // =========================================================================
    // F. RAW JDBC DEMONSTRATION (Mandatory Section 5.F Requirement)
    // =========================================================================

    /**
     * Demonstrates traditional Raw JDBC execution without Spring's JdbcTemplate.
     * Explicitly uses:
     * - Class.forName("com.mysql.cj.jdbc.Driver")
     * - DriverManager.getConnection(url, username, password)
     * - PreparedStatement with parameter binding
     * - ResultSet iteration
     * - Try-with-resources for automatic resource closing
     *
     * VIVA EXPLANATION:
     * 1. Driver Loading: Class.forName() explicitly loads and registers the MySQL JDBC
     *    driver class into the JVM's DriverManager.
     * 2. Connection: DriverManager.getConnection() establishes a physical TCP socket
     *    connection to MySQL.
     * 3. Statement Preparation: PreparedStatement compiles the SQL query once and allows
     *    parameter binding via placeholders (?), protecting against SQL Injection.
     * 4. Indexing: PreparedStatement and ResultSet use 1-based indexing (not 0-based).
     * 5. Resource Closing: The try-with-resources block guarantees that Connection,
     *    PreparedStatement, and ResultSet are closed even if an exception occurs,
     *    preventing database connection leaks.
     * 6. Why JdbcTemplate is superior: JdbcTemplate eliminates repetitive try-catch-finally
     *    blocks, participates in Spring transactions, uses connection pooling (HikariCP),
     *    and translates SQLExceptions into meaningful runtime exceptions.
     *
     * @param id The student database ID to retrieve
     * @return Optional containing the Student if found via Raw JDBC
     */
    public Optional<Student> findByIdRawJdbc(Long id) {
        String sql = "SELECT id, student_id, name, email, course, programming_score, mathematics_score, skills " +
                     "FROM students WHERE id = ?";

        // Step 1: Explicitly load the MySQL JDBC Driver class
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[Raw JDBC Demo] MySQL JDBC Driver class not found: " + e.getMessage());
            throw new RuntimeException("MySQL JDBC Driver not found on classpath", e);
        }

        // Step 2 & 3: Establish connection and prepare statement using try-with-resources
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Step 4: Bind the input parameter (1-based index)
            preparedStatement.setLong(1, id);

            // Step 5: Execute the query and process the ResultSet
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Student student = new Student();
                    student.setId(resultSet.getLong("id"));
                    student.setStudentId(resultSet.getString("student_id"));
                    student.setName(resultSet.getString("name"));
                    student.setEmail(resultSet.getString("email"));
                    student.setCourse(resultSet.getString("course"));
                    student.setProgrammingScore(resultSet.getDouble("programming_score"));
                    student.setMathematicsScore(resultSet.getDouble("mathematics_score"));
                    student.setSkills(resultSet.getString("skills"));
                    student.calculateMetrics();

                    System.out.println("[Raw JDBC Demo] Successfully retrieved student: " + student.getName() +
                                       " (ID: " + student.getId() + ") using DriverManager & PreparedStatement.");
                    return Optional.of(student);
                }
            }

        } catch (SQLException e) {
            System.err.println("[Raw JDBC Demo] SQLException occurred while querying student: " + e.getMessage());
            throw new RuntimeException("Raw JDBC Database Error: " + e.getMessage(), e);
        }

        return Optional.empty();
    }
}
