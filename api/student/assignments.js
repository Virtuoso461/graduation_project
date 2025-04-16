import request from '@/utils/request';

/**
 * 获取作业列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 搜索关键词
 * @param {string} params.status - 作业状态
 * @param {number} params.courseId - 课程ID
 * @returns {Promise} - 返回作业列表
 */
export function getAssignments(params) {
  return request({
    url: '/api/student/assignments',
    method: 'get',
    params
  });
}

/**
 * 获取作业详情
 * @param {number} assignmentId - 作业ID
 * @returns {Promise} - 返回作业详情
 */
export function getAssignmentDetail(assignmentId) {
  return request({
    url: `/api/student/assignments/${assignmentId}`,
    method: 'get'
  });
}

/**
 * 提交作业
 * @param {Object} data - 作业提交数据
 * @param {number} data.assignmentId - 作业ID
 * @param {string} data.content - 作业内容
 * @param {Array} data.attachments - 附件列表
 * @returns {Promise} - 返回提交结果
 */
export function submitAssignment(data) {
  return request({
    url: '/api/student/assignments/submit',
    method: 'post',
    data
  });
}

/**
 * 上传作业附件
 * @param {number} assignmentId - 作业ID
 * @param {FormData} data - 包含附件文件的表单数据
 * @returns {Promise} - 返回上传结果
 */
export function uploadAssignmentFile(assignmentId, data) {
  return request({
    url: `/api/student/assignments/${assignmentId}/upload`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 获取作业提交历史
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @returns {Promise} - 返回作业提交历史
 */
export function getAssignmentHistory(params) {
  return request({
    url: '/api/student/assignments/history',
    method: 'get',
    params
  });
}

/**
 * 获取作业提交详情
 * @param {number} assignmentId - 作业ID
 * @returns {Promise} - 返回作业提交详情
 */
export function getSubmissionDetail(assignmentId) {
  return request({
    url: `/api/student/assignments/submissions/${assignmentId}`,
    method: 'get'
  });
}

/**
 * 获取待完成作业
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @returns {Promise} - 返回待完成作业列表
 */
export function getPendingAssignments(params) {
  return request({
    url: '/api/student/assignments/pending',
    method: 'get',
    params
  });
}

/**
 * 获取已完成作业
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @returns {Promise} - 返回已完成作业列表
 */
export function getCompletedAssignments(params) {
  return request({
    url: '/api/student/assignments/completed',
    method: 'get',
    params
  });
}
