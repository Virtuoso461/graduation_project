import { useAuthStore } from '@/stores/modules/auth';

/**
 * 获取令牌
 * @returns {string} - 返回令牌
 */
export function getToken() {
  const authStore = useAuthStore();
  return authStore.token;
}

/**
 * 获取刷新令牌
 * @returns {string} - 返回刷新令牌
 */
export function getRefreshToken() {
  const authStore = useAuthStore();
  return authStore.refreshTokenValue;
}

/**
 * 设置令牌
 * @param {string} token - 令牌
 */
export function setToken(token) {
  const authStore = useAuthStore();
  authStore.setToken(token);
}

/**
 * 设置刷新令牌
 * @param {string} refreshToken - 刷新令牌
 */
export function setRefreshToken(refreshToken) {
  const authStore = useAuthStore();
  authStore.setRefreshToken(refreshToken);
}

/**
 * 设置过期时间
 * @param {number} expiresAt - 过期时间戳
 */
export function setExpiresAt(expiresAt) {
  const authStore = useAuthStore();
  authStore.setExpiresAt(expiresAt);
}

/**
 * 清除认证信息
 */
export function clearAuth() {
  const authStore = useAuthStore();
  authStore.clearAuth();
}

/**
 * 检查令牌是否过期
 * @returns {boolean} - 返回令牌是否过期
 */
export function isTokenExpired() {
  const authStore = useAuthStore();
  return authStore.isTokenExpired;
}

/**
 * 检查用户是否已登录
 * @returns {boolean} - 返回用户是否已登录
 */
export function isLoggedIn() {
  const authStore = useAuthStore();
  return authStore.isLoggedIn;
}

/**
 * 刷新令牌
 * @returns {Promise} - 返回刷新结果
 */
export async function refreshUserToken() {
  const authStore = useAuthStore();
  return await authStore.refreshUserToken();
}

/**
 * 获取用户角色
 * @returns {string|null} - 返回用户角色
 */
export function getUserRole() {
  const authStore = useAuthStore();
  return authStore.userInfo?.role || null;
}

/**
 * 检查用户是否有指定角色
 * @param {string|Array} roles - 角色或角色数组
 * @returns {boolean} - 返回用户是否有指定角色
 */
export function hasRole(roles) {
  const userRole = getUserRole();
  if (!userRole) return false;
  
  if (Array.isArray(roles)) {
    return roles.includes(userRole);
  }
  
  return userRole === roles;
}
