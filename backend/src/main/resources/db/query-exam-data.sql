-- 查询 2416050435@qq.com 作为教师创建的所有测试
SELECT * FROM exams WHERE email = '2416050435@qq.com' ORDER BY created_at DESC;

-- 查询 2416050435@qq.com 创建的所有可用测试（enabled = true 且 deadline 在当前时间之后）
SELECT * FROM exams 
WHERE email = '2416050435@qq.com' 
  AND enabled = true 
  AND deadline > NOW() 
ORDER BY deadline ASC;

-- 查询 2416050435@qq.com 作为学生参加的所有测试结果
SELECT er.*, e.title, e.type, e.course_name 
FROM exam_results er
JOIN exams e ON er.exam_id = e.id
WHERE er.email = '2416050435@qq.com'
ORDER BY er.completed_at DESC;

-- 统计 2416050435@qq.com 作为教师创建的测试数量
SELECT COUNT(*) AS total_exams FROM exams WHERE email = '2416050435@qq.com';

-- 统计 2416050435@qq.com 创建的测试被参加的总人次
SELECT COUNT(*) AS total_participants 
FROM exam_results er
JOIN exams e ON er.exam_id = e.id
WHERE e.email = '2416050435@qq.com';

-- 查询 2416050435@qq.com 创建的每个测试的平均分
SELECT e.id, e.title, AVG(er.score) AS average_score, COUNT(er.id) AS participant_count
FROM exams e
LEFT JOIN exam_results er ON e.id = er.exam_id
WHERE e.email = '2416050435@qq.com'
GROUP BY e.id, e.title
ORDER BY e.created_at DESC;

-- 查询 2416050435@qq.com 参加的测试中的最高分
SELECT MAX(er.score) AS highest_score 
FROM exam_results er
WHERE er.email = '2416050435@qq.com';

-- 查询 2416050435@qq.com 参加的测试中的平均分
SELECT AVG(er.score) AS average_score 
FROM exam_results er
WHERE er.email = '2416050435@qq.com';

-- 查询特定类型的测试（例如：期中测试）
SELECT * FROM exams 
WHERE email = '2416050435@qq.com' AND type = 'midterm' 
ORDER BY created_at DESC;

-- 查询特定课程的测试
SELECT * FROM exams 
WHERE email = '2416050435@qq.com' AND course_id = 'CS101' 
ORDER BY created_at DESC;

-- 获取最近一周内 2416050435@qq.com 创建的测试
SELECT * FROM exams 
WHERE email = '2416050435@qq.com' AND created_at > NOW() - INTERVAL 7 DAY 
ORDER BY created_at DESC; 