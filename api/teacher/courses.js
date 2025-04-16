import request from '@/utils/request';

/**
 * 获取课程列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 搜索关键词
 * @param {string} params.status - 课程状态
 * @returns {Promise} - 返回课程列表
 */
export function getCourses(params) {
  return request({
    url: '/api/teacher/courses',
    method: 'get',
    params
  });
}

/**
 * 创建课程
 * @param {Object} data - 课程数据
 * @returns {Promise} - 返回创建结果
 */
export function createCourse(data) {
  return request({
    url: '/api/teacher/courses',
    method: 'post',
    data
  });
}

/**
 * 获取课程详情
 * @param {number} courseId - 课程ID
 * @returns {Promise} - 返回课程详情
 */
export function getCourseDetail(courseId) {
  return request({
    url: `/api/teacher/courses/${courseId}`,
    method: 'get'
  });
}

/**
 * 更新课程
 * @param {number} courseId - 课程ID
 * @param {Object} data - 课程数据
 * @returns {Promise} - 返回更新结果
 */
export function updateCourse(courseId, data) {
  return request({
    url: `/api/teacher/courses/${courseId}`,
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
    url: `/api/teacher/courses/${courseId}`,
    method: 'delete'
  });
}

/**
 * 创建章节
 * @param {number} courseId - 课程ID
 * @param {Object} data - 章节数据
 * @returns {Promise} - 返回创建结果
 */
export function createChapter(courseId, data) {
  return request({
    url: `/api/teacher/courses/${courseId}/chapters`,
    method: 'post',
    data
  });
}

/**
 * 更新章节
 * @param {number} chapterId - 章节ID
 * @param {Object} data - 章节数据
 * @returns {Promise} - 返回更新结果
 */
export function updateChapter(chapterId, data) {
  return request({
    url: `/api/teacher/courses/chapters/${chapterId}`,
    method: 'put',
    data
  });
}

/**
 * 删除章节
 * @param {number} chapterId - 章节ID
 * @returns {Promise} - 返回删除结果
 */
export function deleteChapter(chapterId) {
  return request({
    url: `/api/teacher/courses/chapters/${chapterId}`,
    method: 'delete'
  });
}

/**
 * 创建小节
 * @param {number} chapterId - 章节ID
 * @param {Object} data - 小节数据
 * @returns {Promise} - 返回创建结果
 */
export function createSection(chapterId, data) {
  return request({
    url: `/api/teacher/courses/chapters/${chapterId}/sections`,
    method: 'post',
    data
  });
}

/**
 * 更新小节
 * @param {number} sectionId - 小节ID
 * @param {Object} data - 小节数据
 * @returns {Promise} - 返回更新结果
 */
export function updateSection(sectionId, data) {
  return request({
    url: `/api/teacher/courses/sections/${sectionId}`,
    method: 'put',
    data
  });
}

/**
 * 删除小节
 * @param {number} sectionId - 小节ID
 * @returns {Promise} - 返回删除结果
 */
export function deleteSection(sectionId) {
  return request({
    url: `/api/teacher/courses/sections/${sectionId}`,
    method: 'delete'
  });
}

/**
 * 获取课程统计
 * @param {number} courseId - 课程ID
 * @returns {Promise} - 返回课程统计数据
 */
export function getCourseStatistics(courseId) {
  return request({
    url: `/api/teacher/courses/${courseId}/statistics`,
    method: 'get'
  });
}
