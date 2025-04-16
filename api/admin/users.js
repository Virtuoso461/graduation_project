import request from '@/utils/request';

/**
 * 获取用户列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 搜索关键词
 * @param {string} params.role - 用户角色
 * @param {boolean} params.enabled - 是否启用
 * @returns {Promise} - 返回用户列表
 */
export function getUsers(params) {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params
  });
}

/**
 * 创建用户
 * @param {Object} data - 用户数据
 * @returns {Promise} - 返回创建结果
 */
export function createUser(data) {
  return request({
    url: '/api/admin/users',
    method: 'post',
    data
  });
}

/**
 * 获取用户详情
 * @param {number} userId - 用户ID
 * @returns {Promise} - 返回用户详情
 */
export function getUserDetail(userId) {
  return request({
    url: `/api/admin/users/${userId}`,
    method: 'get'
  });
}

/**
 * 更新用户
 * @param {number} userId - 用户ID
 * @param {Object} data - 用户数据
 * @returns {Promise} - 返回更新结果
 */
export function updateUser(userId, data) {
  return request({
    url: `/api/admin/users/${userId}`,
    method: 'put',
    data
  });
}

/**
 * 删除用户
 * @param {number} userId - 用户ID
 * @returns {Promise} - 返回删除结果
 */
export function deleteUser(userId) {
  return request({
    url: `/api/admin/users/${userId}`,
    method: 'delete'
  });
}

/**
 * 重置用户密码
 * @param {number} userId - 用户ID
 * @param {Object} data - 密码数据
 * @param {string} data.password - 新密码
 * @returns {Promise} - 返回重置结果
 */
export function resetUserPassword(userId, data) {
  return request({
    url: `/api/admin/users/${userId}/reset-password`,
    method: 'post',
    data
  });
}

/**
 * 更新用户状态
 * @param {number} userId - 用户ID
 * @param {Object} data - 状态数据
 * @param {boolean} data.enabled - 是否启用
 * @returns {Promise} - 返回更新结果
 */
export function updateUserStatus(userId, data) {
  return request({
    url: `/api/admin/users/${userId}/status`,
    method: 'put',
    data
  });
}

/**
 * 获取学生列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 搜索关键词
 * @returns {Promise} - 返回学生列表
 */
export function getStudents(params) {
  return request({
    url: '/api/admin/users/students',
    method: 'get',
    params
  });
}

/**
 * 获取教师列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 搜索关键词
 * @returns {Promise} - 返回教师列表
 */
export function getTeachers(params) {
  return request({
    url: '/api/admin/users/teachers',
    method: 'get',
    params
  });
}

/**
 * 获取管理员列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 搜索关键词
 * @returns {Promise} - 返回管理员列表
 */
export function getAdmins(params) {
  return request({
    url: '/api/admin/users/admins',
    method: 'get',
    params
  });
}
