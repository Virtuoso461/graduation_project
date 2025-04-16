import request from '@/utils/request';

/**
 * 获取考试列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 搜索关键词
 * @param {string} params.status - 考试状态
 * @param {number} params.courseId - 课程ID
 * @returns {Promise} - 返回考试列表
 */
export function getExams(params) {
  return request({
    url: '/api/student/exams',
    method: 'get',
    params
  });
}

/**
 * 获取考试详情
 * @param {number} examId - 考试ID
 * @returns {Promise} - 返回考试详情
 */
export function getExamDetail(examId) {
  return request({
    url: `/api/student/exams/${examId}`,
    method: 'get'
  });
}

/**
 * 开始考试
 * @param {number} examId - 考试ID
 * @returns {Promise} - 返回考试信息
 */
export function startExam(examId) {
  return request({
    url: `/api/student/exams/${examId}/start`,
    method: 'post'
  });
}

/**
 * 提交考试
 * @param {Object} data - 考试提交数据
 * @param {number} data.examId - 考试ID
 * @param {Array} data.answers - 答案列表
 * @returns {Promise} - 返回提交结果
 */
export function submitExam(data) {
  return request({
    url: '/api/student/exams/submit',
    method: 'post',
    data
  });
}

/**
 * 上传考试附件
 * @param {number} examId - 考试ID
 * @param {FormData} data - 包含附件文件的表单数据
 * @returns {Promise} - 返回上传结果
 */
export function uploadExamFile(examId, data) {
  return request({
    url: `/api/student/exams/${examId}/upload`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 获取考试结果
 * @param {number} examId - 考试ID
 * @returns {Promise} - 返回考试结果
 */
export function getExamResult(examId) {
  return request({
    url: `/api/student/exams/${examId}/result`,
    method: 'get'
  });
}

/**
 * 获取考试历史
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @returns {Promise} - 返回考试历史
 */
export function getExamHistory(params) {
  return request({
    url: '/api/student/exams/history',
    method: 'get',
    params
  });
}

/**
 * 获取考试统计
 * @returns {Promise} - 返回考试统计数据
 */
export function getExamStatistics() {
  return request({
    url: '/api/student/exams/statistics',
    method: 'get'
  });
}

/**
 * 获取错题集
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.subject - 科目
 * @returns {Promise} - 返回错题集
 */
export function getWrongQuestions(params) {
  return request({
    url: '/api/student/exams/wrong-questions',
    method: 'get',
    params
  });
}

/**
 * 获取知识点掌握情况
 * @param {Object} params - 查询参数
 * @param {string} params.subject - 科目
 * @returns {Promise} - 返回知识点掌握情况
 */
export function getKnowledgePoints(params) {
  return request({
    url: '/api/student/exams/knowledge-points',
    method: 'get',
    params
  });
}

/**
 * 获取考试排名
 * @param {number} examId - 考试ID
 * @returns {Promise} - 返回考试排名
 */
export function getExamRanking(examId) {
  return request({
    url: `/api/student/exams/${examId}/ranking`,
    method: 'get'
  });
}
