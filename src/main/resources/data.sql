-- Sample data for SQL problem solving
INSERT INTO departments (department_id, department_name, location) VALUES
(1, 'Engineering', 'New York'),
(2, 'Marketing', 'Los Angeles'),
(3, 'Sales', 'Chicago'),
(4, 'HR', 'Boston'),
(5, 'Finance', 'San Francisco');

INSERT INTO employees (employee_id, employee_name, department_id, salary, hire_date, email) VALUES
(101, 'Alice Johnson', 1, 95000.00, '2020-01-15', 'alice.j@company.com'),
(102, 'Bob Smith', 1, 87000.00, '2019-03-20', 'bob.s@company.com'),
(103, 'Charlie Brown', 1, 92000.00, '2021-02-10', 'charlie.b@company.com'),
(104, 'David Wilson', 1, 98000.00, '2018-11-30', 'david.w@company.com'),
(201, 'Frank Miller', 2, 65000.00, '2020-06-15', 'frank.m@company.com'),
(202, 'Grace Lee', 2, 68000.00, '2019-08-20', 'grace.l@company.com'),
(203, 'Henry Clark', 2, 70000.00, '2021-04-12', 'henry.c@company.com'),
(301, 'Jack Thompson', 3, 55000.00, '2021-07-18', 'jack.t@company.com'),
(302, 'Karen White', 3, 78000.00, '2019-12-08', 'karen.w@company.com'),
(303, 'Leo Martinez', 3, 82000.00, '2020-05-25', 'leo.m@company.com'),
(401, 'Nathan Anderson', 4, 52000.00, '2020-09-22', 'nathan.a@company.com'),
(402, 'Olivia Garcia', 4, 48000.00, '2021-01-30', 'olivia.g@company.com'),
(501, 'Quinn Williams', 5, 85000.00, '2018-04-15', 'quinn.w@company.com'),
(502, 'Rachel Johnson', 5, 88000.00, '2019-02-28', 'rachel.j@company.com'),
(503, 'Sam Brown', 5, 91000.00, '2020-12-05', 'sam.b@company.com');
