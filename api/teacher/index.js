import request from '@/utils/request';

/**
 * 获取教师个人资料
 * @returns {Promise} - 返回个人资料
 */
export function getProfile() {
  return request({
    url: '/api/teacher/profile',
    method: 'get'
  });
}

/**
 * 更新教师个人资料
 * @param {Object} data - 个人资料数据
 * @returns {Promise} - 返回更新结果
 */
export function updateProfile(data) {
  return request({
    url: '/api/teacher/profile',
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
    url: '/api/teacher/profile/avatar',
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
    url: '/api/teacher/profile/change-password',
    method: 'post',
    data
  });
}
