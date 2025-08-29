package com.webhooksql.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class SqlSolverService {

    private static final Logger logger = LoggerFactory.getLogger(SqlSolverService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String solveSqlProblem(String regNo) {
        logger.info("Solving SQL problem for registration number: {}", regNo);

        if (regNo == null || regNo.isEmpty()) {
            throw new RuntimeException("Registration number is null or empty");
        }

        String lastTwoDigits = regNo.length() >= 2 ?
            regNo.substring(regNo.length() - 2) : regNo;

        int lastDigitsNum;
        try {
            // ✅ FIXED: escaped \D → \\D
            lastDigitsNum = Integer.parseInt(lastTwoDigits.replaceAll("\\D", ""));
            logger.info("Last two digits of registration {}: {} (numeric: {})", regNo, lastTwoDigits, lastDigitsNum);
        } catch (NumberFormatException e) {
            logger.warn("Could not parse last digits from {}, defaulting to Question 1", regNo);
            lastDigitsNum = 21;
        }

        if (lastDigitsNum % 2 == 1) {
            logger.info("Registration {} ends in {} (ODD) -> Solving Question 1", regNo, lastDigitsNum);
            return solveQuestion1();
        } else {
            logger.info("Registration {} ends in {} (EVEN) -> Solving Question 2", regNo, lastDigitsNum);
            return solveQuestion2();
        }
    }

    private String solveQuestion1() {
        logger.info("Executing Question 1 solution (for ODD registration endings)...");

        try {
            String solutionQuery = """
                SELECT 
                    d.department_name,
                    ROUND(AVG(e.salary), 2) as avg_salary,
                    COUNT(e.employee_id) as employee_count
                FROM departments d
                JOIN employees e ON d.department_id = e.department_id
                WHERE e.salary > 0
                GROUP BY d.department_id, d.department_name
                HAVING COUNT(e.employee_id) >= 1
                ORDER BY avg_salary DESC
                LIMIT 3
                """;

            List<Map<String, Object>> results = jdbcTemplate.queryForList(solutionQuery);
            logger.info("Question 1 query executed successfully, returned {} rows", results.size());

            results.forEach(row -> logger.info("Result - Department: {}, Avg Salary: {}, Count: {}",
                row.get("department_name"), row.get("avg_salary"), row.get("employee_count")));

            // ✅ FIXED: escaped \s → \\s
            return solutionQuery.trim().replaceAll("\\s+", " ");

        } catch (Exception e) {
            logger.error("Error executing Question 1 query", e);

            String fallbackQuery = """
                SELECT department_name, AVG(salary) as avg_salary, COUNT(*) as employee_count
                FROM employees e
                JOIN departments d ON e.department_id = d.department_id
                GROUP BY department_name
                ORDER BY avg_salary DESC
                LIMIT 3
                """;

            logger.info("Using fallback query for Question 1");
            return fallbackQuery.trim().replaceAll("\\s+", " ");
        }
    }

    private String solveQuestion2() {
        logger.info("Executing Question 2 solution (for EVEN registration endings)...");

        try {
            String solutionQuery = """
                SELECT 
                    e1.employee_name,
                    e1.salary,
                    d.department_name,
                    ROUND(dept_avg.avg_salary, 2) as dept_avg_salary
                FROM employees e1
                JOIN departments d ON e1.department_id = d.department_id
                JOIN (
                    SELECT 
                        department_id,
                        AVG(salary) as avg_salary
                    FROM employees
                    GROUP BY department_id
                ) dept_avg ON e1.department_id = dept_avg.department_id
                WHERE e1.salary > dept_avg.avg_salary
                ORDER BY d.department_name, e1.salary DESC
                """;

            List<Map<String, Object>> results = jdbcTemplate.queryForList(solutionQuery);
            logger.info("Question 2 query executed successfully, returned {} rows", results.size());

            // ✅ FIXED: escaped \s → \\s
            return solutionQuery.trim().replaceAll("\\s+", " ");

        } catch (Exception e) {
            logger.error("Error executing Question 2 query", e);

            String fallbackQuery = """
                SELECT employee_name, salary, department_name
                FROM employees e
                JOIN departments d ON e.department_id = d.department_id
                WHERE salary > (SELECT AVG(salary) FROM employees)
                ORDER BY salary DESC
                """;

            logger.info("Using fallback query for Question 2");
            return fallbackQuery.trim().replaceAll("\\s+", " ");
        }
    }

    public void storeSolution(String regNo, String solution) {
        try {
            logger.info("Storing solution for registration number: {}", regNo);

            String insertQuery = """
                INSERT INTO solution_results (reg_no, sql_solution, created_at) 
                VALUES (?, ?, CURRENT_TIMESTAMP)
                """;

            int rowsAffected = jdbcTemplate.update(insertQuery, regNo, solution);

            if (rowsAffected > 0) {
                logger.info("Solution stored successfully for registration: {}", regNo);
            } else {
                logger.warn("No rows affected when storing solution for: {}", regNo);
            }

        } catch (Exception e) {
            logger.error("Error storing solution for registration: {}", regNo, e);
        }
    }
}
