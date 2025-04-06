/**
 * 考试相关类型定义
 */

/**
 * 考试基本信息
 */
export interface Exam {
  id: number;
  title: string;
  type: string;
  description: string;
  duration: number;
  questionCount: number;
  totalScore: number;
  deadline: string;
  courseId: string;
  courseName: string;
  enabled: boolean;
  email: string;
  createdAt: string;
  deadlineStr?: string; // 格式化的截止日期展示
  difficulty?: string; // 考试难度
  timeRemaining?: number; // 剩余时间
}

/**
 * 考试结果信息
 */
export interface ExamResult {
  id: number;
  examId: number;
  title: string;
  type: string;
  description: string;
  courseId: string;
  courseName: string;
  score: number;
  correctCount: number;
  wrongCount: number;
  correctRate: number;
  ranking: string;
  completedAt: string;
  completedTime: string; // 格式化的完成时间
  timeSpent: number;
  totalScore: number;
}

/**
 * 考试统计信息
 */
export interface ExamStats {
  createdCount: number;
  completedCount: number;
  averageScore: number;
  highestScore: number;
  totalExams?: number;
  averageCorrectRate?: number; // 平均正确率
  bestRanking?: number; // 最佳排名
  bestExamTitle?: string; // 最佳考试标题
  typeDistribution?: Array<{type: string, count: number}>;
  scoreDistribution?: Array<{range: string, count: number}>;
  recentScores?: Array<{examTitle: string, score: number, date: string}>;
} 