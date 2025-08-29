-- Database Schema for Webhook SQL Application
DROP TABLE IF EXISTS solution_results;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS departments;

-- Create departments table
CREATE TABLE departments (
    department_id INT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL,
    location VARCHAR(100)
);

-- Create employees table
CREATE TABLE employees (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    department_id INT,
    salary DECIMAL(10,2) NOT NULL,
    hire_date DATE,
    email VARCHAR(100),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- Create solution results table for audit trail
CREATE TABLE solution_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reg_no VARCHAR(50) NOT NULL,
    sql_solution TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_employees_dept ON employees(department_id);
CREATE INDEX idx_employees_salary ON employees(salary);
