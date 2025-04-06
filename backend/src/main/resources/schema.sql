-- 创建考试表
CREATE TABLE IF NOT EXISTS exams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    duration INT NOT NULL,
    start_time DATETIME,
    end_time DATETIME NOT NULL,
    total_score INT NOT NULL,
    passing_score INT,
    creator_email VARCHAR(255) NOT NULL,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    category VARCHAR(50) NOT NULL DEFAULT '未分类',
    difficulty VARCHAR(50) NOT NULL DEFAULT '中等',
    question_count INT DEFAULT 0
);

-- 创建考试结果表
CREATE TABLE IF NOT EXISTS exam_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    score INT NOT NULL,
    correct_count INT,
    total_count INT,
    correct_rate DOUBLE,
    ranking INT,
    time_spent INT,
    submit_time DATETIME NOT NULL,
    exam_title VARCHAR(255),
    course_name VARCHAR(255)
);

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `enabled` boolean NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 