import request from '@/utils/request';

/**
 * 获取学生个人资料
 * @returns {Promise} - 返回个人资料
 */
export function getProfile() {
  return request({
    url: '/api/student/profile',
    method: 'get'
  });
}

/**
 * 更新学生个人资料
 * @param {Object} data - 个人资料数据
 * @returns {Promise} - 返回更新结果
 */
export function updateProfile(data) {
  return request({
    url: '/api/student/profile',
    method: 'post',
    data
  });
}

/**
 * 上传头像
 * @param {FormData} data - 包含头像文件的表单数据
 * @returns {Promise} - 返回上传结果
 */
export function uploadAvatar(data) {
  return request({
    url: '/api/student/profile/avatar',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 修改密码
 * @param {Object} data - 密码数据
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @returns {Promise} - 返回修改结果
 */
export function changePassword(data) {
  return request({
    url: '/api/student/profile/change-password',
    method: 'post',
    data
  });
}

/**
 * 获取学习概览
 * @returns {Promise} - 返回学习概览数据
 */
export function getStudyOverview() {
  return request({
    url: '/api/student/profile/overview',
    method: 'get'
  });
}

/**
 * 获取学习记录
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @returns {Promise} - 返回学习记录
 */
export function getLearningRecords(params) {
  return request({
    url: '/api/student/profile/learning-records',
    method: 'get',
    params
  });
}

/**
 * 获取个人作业列表
 * @returns {Promise} - 返回作业列表
 */
export function getProfileAssignments() {
  return request({
    url: '/api/student/profile/assignments',
    method: 'get'
  });
}

/**
 * 获取个人考试列表
 * @returns {Promise} - 返回考试列表
 */
export function getProfileExams() {
  return request({
    url: '/api/student/profile/exams',
    method: 'get'
  });
}

/**
 * 获取学习统计
 * @returns {Promise} - 返回学习统计数据
 */
export function getStatistics() {
  return request({
    url: '/api/student/profile/statistics',
    method: 'get'
  });
}

/**
 * 获取学习趋势
 * @param {Object} params - 查询参数
 * @param {string} params.period - 时间段（week/month/year）
 * @returns {Promise} - 返回学习趋势数据
 */
export function getStudyTrend(params) {
  return request({
    url: '/api/student/profile/trend',
    method: 'get',
    params
  });
}
