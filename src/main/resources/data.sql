-- 插入测试用户数据
INSERT INTO user (username, password, role, create_time, last_login_time, enabled)
VALUES
('admin@example.com', '$2a$10$rPiEAgQNIT1TCoKi3Eqq8eVaRYIRlR29Fc7gSL0YUVj0JF3AoJEqa', 'ADMIN', '2023-01-01 00:00:00', '2023-01-01 00:00:00', true), -- 密码: admin123
('teacher@example.com', '$2a$10$H1bgFfzpkJJf8YlrRJRQY.YGSyS.s9/W5fGjx4K2eww6aES3qEjIS', 'TEACHER', '2023-01-01 00:00:00', '2023-01-01 00:00:00', true), -- 密码: teacher123
('student@example.com', '$2a$10$HqQYctQ8rJ/R6vK0bODdoOzGFN8WOTJGN4.P17XSBgFO9J0fLHjUG', 'STUDENT', '2023-01-01 00:00:00', '2023-01-01 00:00:00', true), -- 密码: student123
('2416050435@qq.com', '$2a$10$rPiEAgQNIT1TCoKi3Eqq8eVaRYIRlR29Fc7gSL0YUVj0JF3AoJEqa', 'ADMIN', '2023-01-01 00:00:00', '2023-01-01 00:00:00', true); -- 密码: admin123 