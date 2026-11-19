package com.kiet.examproject.controller;

import com.kiet.examproject.model.Student;
import com.kiet.examproject.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * =============================================================================
 * CONTROLLER LAYER: StudentController (REST Controller)
 * =============================================================================
 * Course: CS336B - Object Oriented Prog. Using Java (ODD SEM-2026-27)
 * Core Concepts & Viva Topics:
 *
 * 1. What is @RestController?
 *    A convenience annotation combining @Controller and @ResponseBody.
 *    It eliminates the need to annotate every handler method with @ResponseBody,
 *    automatically serializing returned Java objects directly into JSON/XML HTTP responses.
 *
 * 2. What is @RequestMapping?
 *    Maps web requests to specific controller methods.
 *    Base path: "/api/students".
 *
 * 3. Constructor Dependency Injection:
 *    The StudentService dependency is injected via the constructor.
 *    The controller delegates all business operations to the service layer,
 *    never accessing the DAO or database directly.
 *
 * 4. Key Request Annotations:
 *    - @PathVariable: Extracts values from URI template paths (e.g. /api/students/{id}).
 *    - @RequestBody: Deserializes the incoming JSON payload into a Java model object.
 *    - @RequestParam: Extracts HTTP query parameters (e.g. ?query=... or ?skill=...).
 *    - ResponseEntity: Represents the entire HTTP response (status code, headers, body).
 * =============================================================================
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    /**
     * Constructor Dependency Injection.
     *
     * @param studentService The injected business service bean
     */
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // =========================================================================
    // STANDARD CRUD REST APIS (Q3.1 - Q3.5)
    // =========================================================================

    /**
     * 1. GET /api/students
     * Q3.2: Retrieve all students.
     *
     * @return 200 OK with list of all students
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", students.size());
        response.put("data", students);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. GET /api/students/{id}
     * Q3.3: Search for a student by ID.
     *
     * @param id The primary key ID
     * @return 200 OK with student, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStudentById(@PathVariable Long id) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        Map<String, Object> response = new HashMap<>();

        if (studentOpt.isPresent()) {
            response.put("success", true);
            response.put("data", studentOpt.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Student not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 3. POST /api/students
     * Q3.1: Add a new student record.
     *
     * @param student The student JSON payload
     * @return 201 Created with saved student, or 400 Bad Request if invalid
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createStudent(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        try {
            Student created = studentService.createStudent(student);
            response.put("success", true);
            response.put("message", "Student created successfully.");
            response.put("data", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 4. PUT /api/students/{id}
     * Q3.4: Update student scores and skills.
     *
     * @param id The primary key ID to update
     * @param student The updated student data
     * @return 200 OK with updated student, or 400/404 on error
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();
        try {
            Student updated = studentService.updateStudent(id, student);
            response.put("success", true);
            response.put("message", "Student record updated successfully.");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 5. DELETE /api/students/{id}
     * Q3.5: Delete a student record by ID.
     *
     * @param id The primary key ID to delete
     * @return 200 OK on successful deletion, or 400/404 on error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean deleted = studentService.deleteStudent(id);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Student record with ID " + id + " was deleted successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Student record with ID " + id + " could not be deleted.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // =========================================================================
    // ADVANCED EXAM QUESTION 2 & 3 ENDPOINTS
    // =========================================================================

    /**
     * 6. GET /api/students/merit-list
     * Q3.6 & Q2.5: Display merit list sorted by average score in descending order.
     *
     * @return 200 OK with merit list
     */
    @GetMapping("/merit-list")
    public ResponseEntity<Map<String, Object>> getMeritList() {
        List<Student> meritList = studentService.getMeritList();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", meritList.size());
        response.put("data", meritList);
        return ResponseEntity.ok(response);
    }

    /**
     * 7. GET /api/students/search?query=...
     * Q2.4: Search for students by ID, email, or course.
     *
     * @param query Search keyword
     * @return 200 OK with matching students
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchStudents(@RequestParam(required = false, defaultValue = "") String query) {
        List<Student> results = studentService.searchStudents(query);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("query", query);
        response.put("count", results.size());
        response.put("data", results);
        return ResponseEntity.ok(response);
    }

    /**
     * 8. GET /api/students/by-skill?skill=...
     * Q2.6: Identify students who have the same technical skills.
     *
     * @param skill Target skill name
     * @return 200 OK with students possessing the skill
     */
    @GetMapping("/by-skill")
    public ResponseEntity<Map<String, Object>> getStudentsBySkill(@RequestParam String skill) {
        List<Student> students = studentService.getStudentsBySkill(skill);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("skill", skill);
        response.put("count", students.size());
        response.put("data", students);
        return ResponseEntity.ok(response);
    }

    /**
     * 9. GET /api/students/skill-distribution
     * Q2.6: Grouping of all technical skills and the students who have each skill.
     *
     * @return 200 OK with skill map
     */
    @GetMapping("/skill-distribution")
    public ResponseEntity<Map<String, Object>> getSkillDistribution() {
        Map<String, List<Student>> distribution = studentService.getSkillDistribution();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalUniqueSkills", distribution.size());
        response.put("data", distribution);
        return ResponseEntity.ok(response);
    }

    /**
     * 10. GET /api/students/special-attention
     * Q2.7: Display students who need improvement in programming (< 50)
     * but have a mathematics score of at least 75.
     *
     * @return 200 OK with flagged students
     */
    @GetMapping("/special-attention")
    public ResponseEntity<Map<String, Object>> getSpecialAttentionStudents() {
        List<Student> students = studentService.getSpecialAttentionStudents();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("description", "Students with Programming Score < 50 and Mathematics Score >= 75");
        response.put("count", students.size());
        response.put("data", students);
        return ResponseEntity.ok(response);
    }

    /**
     * 11. GET /api/students/stats
     * Dashboard aggregated summary metrics.
     *
     * @return 200 OK with statistics map
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = studentService.getDashboardStats();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        return ResponseEntity.ok(response);
    }

    /**
     * 12. GET /api/students/raw-jdbc/{id}
     * Section 5.F: Raw JDBC demonstration endpoint.
     * Retrieves student directly using Class.forName() and DriverManager.
     *
     * @param id Student ID
     * @return 200 OK with student and Raw JDBC metadata
     */
    @GetMapping("/raw-jdbc/{id}")
    public ResponseEntity<Map<String, Object>> getStudentByIdRawJdbc(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Student> studentOpt = studentService.getStudentByIdRawJdbc(id);
            if (studentOpt.isPresent()) {
                response.put("success", true);
                response.put("mechanism", "Raw JDBC (DriverManager + PreparedStatement)");
                response.put("driverClass", "com.mysql.cj.jdbc.Driver");
                response.put("data", studentOpt.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No student found via Raw JDBC with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Raw JDBC Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 13. GET /api/students/prototype-demo
     * Section 9: Spring Bean Scope Demonstration.
     * Verifies that each call requests and receives a fresh Prototype bean instance.
     *
     * @return 200 OK with prototype instance metadata
     */
    @GetMapping("/prototype-demo")
    public ResponseEntity<Map<String, Object>> demonstratePrototypeBean() {
        Map<String, Object> demoInfo = studentService.demonstratePrototypeBean();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", demoInfo);
        return ResponseEntity.ok(response);
    }
}
