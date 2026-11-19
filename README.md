# Student Profile & Skill Development Management System

**Course:** CS336B - Object Oriented Prog. Using Java (ODD SEM-2026-27)  
**Mid Semester Examination-II (Blended)**  
**Developer:** Hardik  
**Institution:** KIET Group of Institutions  

---

## 📌 Problem Statement
To develop a **Student Profile and Skill Development Management System** using Java Spring Boot, Spring JDBC (`JdbcTemplate`), MySQL, and Spring AOP. The application maintains student personal records, academic examination scores, and technical skill interests. It performs business calculations, leverages the Java Collections framework for in-memory rankings and grouping, and exposes clean REST APIs consumed by a modern SaaS admin dashboard.

---

## 🛠️ Technology Stack
- **Backend:** Java 21 / 26, Spring Boot 4.1.2
- **Database Access:** Spring JDBC (`JdbcTemplate`) & Raw JDBC (`DriverManager`, `PreparedStatement`)
- **Database:** MySQL 8.0 (`mse_java_exam_db`)
- **AOP:** Spring AOP / AspectJ (`@Before`, `@After`, `@AfterReturning`)
- **Architecture:** Strict MVC (Model, DAO, Service, Controller, Config, Aspect)
- **Frontend:** HTML5, CSS3 (Modern SaaS Dark-Navy Theme), Vanilla JavaScript (Fetch API)

---

## 🚀 Key Academic Features

1. **Academic Analytics & Business Rules (CO4 Q2):**
   - **Average Score Calculation:** `(programming_score + mathematics_score) / 2.0`
   - **Performance Categories:**
     - `85.0+`: Excellent
     - `70.0 - 84.99`: Good
     - `50.0 - 69.99`: Satisfactory
     - `Below 50.0`: Needs Improvement
   - **Stronger Subject Identification:** Determines whether Programming or Mathematics is the student's strength.
   - **Special Attention Filter:** Automatically flags students with `Programming Score < 50` and `Mathematics Score >= 75`.
   - **Validation:** Strict range bounds `[0.0, 100.0]` and duplicate Student ID / Email prevention.

2. **Java Collections Framework (Viva Requirement):**
   - **Merit List:** Sorted descending using `List<Student>` and `Comparator.comparingDouble(Student::getAverageScore).reversed()`.
   - **Skill Distribution:** Dynamic grouping using `Map<String, List<Student>>` (TreeMap) to identify students sharing technical skills.

3. **Database Layer (Spring JDBC & Raw JDBC):**
   - Primary CRUD implemented via Spring `JdbcTemplate` with parameterized queries and `RowMapper`.
   - Dedicated Raw JDBC helper demonstrating `Class.forName("com.mysql.cj.jdbc.Driver")`, `DriverManager.getConnection()`, and `PreparedStatement`.

4. **Spring Core, Scopes & AOP:**
   - **Dependency Injection:** Constructor Injection as primary + `@Autowired` Field Injection explicitly demonstrated for syllabus viva.
   - **Bean Scopes:** Prototype-scoped bean (`StudentAuditHelper`) requested dynamically via `ObjectProvider`.
   - **Lifecycle Callbacks:** `@PostConstruct` and `@PreDestroy` in `StudentService`.
   - **Spring AOP:** `LoggingAspect` intercepting method execution across Controller and Service layers.

---

## 🌐 REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/students` | Retrieve all student records |
| `GET` | `/api/students/{id}` | Search student by primary key ID |
| `POST` | `/api/students` | Add a new student with validation |
| `PUT` | `/api/students/{id}` | Update student marks and skills |
| `DELETE` | `/api/students/{id}` | Delete student record |
| `GET` | `/api/students/merit-list` | Display Merit List (descending order) |
| `GET` | `/api/students/search?query=...` | Search by ID, name, email, or course |
| `GET` | `/api/students/by-skill?skill=...` | Filter students by technical skill |
| `GET` | `/api/students/skill-distribution` | Group students by technical skills |
| `GET` | `/api/students/special-attention` | Students needing programming help (Prog < 50, Math >= 75) |
| `GET` | `/api/students/stats` | Aggregated dashboard summary metrics |
| `GET` | `/api/students/raw-jdbc/{id}` | Raw JDBC demonstration query |
| `GET` | `/api/students/prototype-demo` | Prototype bean scope verification |

---

## ⚙️ How to Run the Project

### 1. Database Setup
Create database in MySQL:
```sql
CREATE DATABASE IF NOT EXISTS mse_java_exam_db;
```
The application will automatically execute `schema.sql` on startup to initialize the `students` table.

### 2. Configure Credentials
In `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mse_java_exam_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=Hardik@2006
```

### 3. Run Application
From project directory:
```bash
mvn spring-boot:run
```
*(or `.\mvnw.cmd spring-boot:run`)*

### 4. Access Web Interface
Open browser and navigate to:
```text
http://localhost:8080/
```
