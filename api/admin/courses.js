import request from '@/utils/request';

/**
 * 获取课程列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 搜索关键词
 * @param {string} params.status - 课程状态
 * @param {number} params.categoryId - 分类ID
 * @returns {Promise} - 返回课程列表
 */
export function getCourses(params) {
  return request({
    url: '/api/admin/courses',
    method: 'get',
    params
  });
}

/**
 * 获取课程详情
 * @param {number} courseId - 课程ID
 * @returns {Promise} - 返回课程详情
 */
export function getCourseDetail(courseId) {
  return request({
    url: `/api/admin/courses/${courseId}`,
    method: 'get'
  });
}

/**
 * 审核课程
 * @param {number} courseId - 课程ID
 * @param {Object} data - 审核数据
 * @param {boolean} data.approved - 是否通过
 * @param {string} data.feedback - 反馈意见
 * @returns {Promise} - 返回审核结果
 */
export function approveCourse(courseId, data) {
  return request({
    url: `/api/admin/courses/${courseId}/approval`,
    method: 'put',
    data
  });
}

/**
 * 删除课程
 * @param {number} courseId - 课程ID
 * @returns {Promise} - 返回删除结果
 */
export function deleteCourse(courseId) {
  return request({
    url: `/api/admin/courses/${courseId}`,
    method: 'delete'
  });
}

/**
 * 获取课程分类列表
 * @returns {Promise} - 返回分类列表
 */
export function getCourseCategories() {
  return request({
    url: '/api/admin/courses/categories',
    method: 'get'
  });
}

/**
 * 创建课程分类
 * @param {Object} data - 分类数据
 * @param {string} data.name - 分类名称
 * @param {string} data.description - 分类描述
 * @returns {Promise} - 返回创建结果
 */
export function createCourseCategory(data) {
  return request({
    url: '/api/admin/courses/categories',
    method: 'post',
    data
  });
}

/**
 * 更新课程分类
 * @param {number} categoryId - 分类ID
 * @param {Object} data - 分类数据
 * @param {string} data.name - 分类名称
 * @param {string} data.description - 分类描述
 * @returns {Promise} - 返回更新结果
 */
export function updateCourseCategory(categoryId, data) {
  return request({
    url: `/api/admin/courses/categories/${categoryId}`,
    method: 'put',
    data
  });
}

/**
 * 删除课程分类
 * @param {number} categoryId - 分类ID
 * @returns {Promise} - 返回删除结果
 */
export function deleteCourseCategory(categoryId) {
  return request({
    url: `/api/admin/courses/categories/${categoryId}`,
    method: 'delete'
  });
}

/**
 * 获取课程统计
 * @returns {Promise} - 返回课程统计数据
 */
export function getCourseStatistics() {
  return request({
    url: '/api/admin/courses/statistics',
    method: 'get'
  });
}
