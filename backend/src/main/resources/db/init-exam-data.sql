-- 先清空现有数据（注意：先删除有外键关联的表）
DELETE FROM exam_results;
DELETE FROM exams;

-- 插入与邮箱 2416050435@qq.com 相关的测试数据
-- 1. 作为教师创建的测试
INSERT INTO exams (title, type, description, duration, question_count, total_score, deadline, course_id, course_name, email, created_at, enabled)
VALUES 
('Java 基础知识测试', 'chapter', 'Java 编程语言基础知识，包括语法、数据类型、面向对象等内容', 60, 20, 100, NOW() + INTERVAL 7 DAY, 'CS101', 'Java 程序设计', '2416050435@qq.com', NOW(), true),
('数据结构与算法测试', 'midterm', '包括数组、链表、树、图等数据结构和常见算法', 90, 30, 100, NOW() + INTERVAL 14 DAY, 'CS102', '数据结构与算法', '2416050435@qq.com', NOW(), true),
('数据库系统期末考试', 'final', '关系数据库理论、SQL、数据库设计等内容', 120, 40, 100, NOW() + INTERVAL 30 DAY, 'CS103', '数据库系统', '2416050435@qq.com', NOW(), true),
('人工智能导论测试', 'chapter', '机器学习、神经网络、自然语言处理等内容', 60, 20, 100, NOW() - INTERVAL 10 DAY, 'CS104', '人工智能导论', '2416050435@qq.com', NOW() - INTERVAL 15 DAY, false),
('软件工程期中考试', 'midterm', '软件开发生命周期、需求分析、设计模式等内容', 90, 25, 100, NOW() - INTERVAL 20 DAY, 'CS105', '软件工程', '2416050435@qq.com', NOW() - INTERVAL 25 DAY, false);

-- 2. 其他教师创建的测试（用于对比）
INSERT INTO exams (title, type, description, duration, question_count, total_score, deadline, course_id, course_name, email, created_at, enabled)
VALUES 
('Web 开发基础', 'chapter', 'HTML, CSS, JavaScript 基础知识', 60, 20, 100, NOW() + INTERVAL 10 DAY, 'CS201', 'Web 开发', 'teacher1@example.com', NOW(), true),
('网络安全导论', 'midterm', '网络协议、加密技术、安全防护等', 90, 30, 100, NOW() + INTERVAL 15 DAY, 'CS202', '网络安全', 'teacher2@example.com', NOW(), true);

-- 3. 学生参加测试的结果（假设 2416050435@qq.com 既是老师也是学生，参加了其他教师的测试）
-- 获取上面插入的考试ID
-- 注意：由于不同数据库获取最后插入ID的方式不同，这里假设ID是连续的
-- 如果在实际环境中，应该使用查询语句获取准确的ID

-- 插入考试结果数据
INSERT INTO exam_results (exam_id, email, score, correct_count, wrong_count, correct_rate, ranking, completed_at, time_spent, created_at)
VALUES 
-- 2416050435@qq.com 作为学生参加的考试结果
(6, '2416050435@qq.com', 85, 17, 3, 85.0, 2, NOW() - INTERVAL 2 DAY, 45, NOW() - INTERVAL 2 DAY),
(7, '2416050435@qq.com', 92, 28, 2, 93.3, 1, NOW() - INTERVAL 5 DAY, 75, NOW() - INTERVAL 5 DAY),

-- 其他学生参加 2416050435@qq.com 创建的考试结果
(1, 'student1@example.com', 78, 16, 4, 80.0, 3, NOW() - INTERVAL 1 DAY, 55, NOW() - INTERVAL 1 DAY),
(1, 'student2@example.com', 88, 18, 2, 90.0, 2, NOW() - INTERVAL 1 DAY, 48, NOW() - INTERVAL 1 DAY),
(1, 'student3@example.com', 95, 19, 1, 95.0, 1, NOW() - INTERVAL 2 DAY, 50, NOW() - INTERVAL 2 DAY),

(2, 'student1@example.com', 82, 25, 5, 83.3, 2, NOW() - INTERVAL 3 DAY, 85, NOW() - INTERVAL 3 DAY),
(2, 'student2@example.com', 90, 27, 3, 90.0, 1, NOW() - INTERVAL 3 DAY, 80, NOW() - INTERVAL 3 DAY),

(3, 'student1@example.com', 75, 30, 10, 75.0, 3, NOW() - INTERVAL 5 DAY, 110, NOW() - INTERVAL 5 DAY),
(3, 'student2@example.com', 85, 34, 6, 85.0, 2, NOW() - INTERVAL 5 DAY, 105, NOW() - INTERVAL 5 DAY),
(3, 'student3@example.com', 92, 37, 3, 92.5, 1, NOW() - INTERVAL 6 DAY, 100, NOW() - INTERVAL 6 DAY),

(4, 'student1@example.com', 80, 16, 4, 80.0, 2, NOW() - INTERVAL 8 DAY, 58, NOW() - INTERVAL 8 DAY),
(4, 'student2@example.com', 85, 17, 3, 85.0, 1, NOW() - INTERVAL 8 DAY, 55, NOW() - INTERVAL 8 DAY),

(5, 'student1@example.com', 78, 20, 5, 80.0, 3, NOW() - INTERVAL 18 DAY, 85, NOW() - INTERVAL 18 DAY),
(5, 'student2@example.com', 84, 21, 4, 84.0, 2, NOW() - INTERVAL 18 DAY, 80, NOW() - INTERVAL 18 DAY),
(5, 'student3@example.com', 92, 23, 2, 92.0, 1, NOW() - INTERVAL 19 DAY, 75, NOW() - INTERVAL 19 DAY); 