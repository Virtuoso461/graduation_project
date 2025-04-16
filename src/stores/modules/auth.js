import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { login, logout, refreshToken } from '@/api/auth';

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '');
  const refreshTokenValue = ref(localStorage.getItem('refreshToken') || '');
  const expiresAt = ref(localStorage.getItem('expiresAt') ? parseInt(localStorage.getItem('expiresAt')) : 0);
  const isLoading = ref(false);
  const error = ref(null);
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value);
  const isTokenExpired = computed(() => {
    if (!expiresAt.value) return true;
    return Date.now() >= expiresAt.value;
  });
  
  // 方法
  // 登录
  const loginUser = async (credentials) => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await login(credentials);
      
      if (response.data && response.data.token) {
        setToken(response.data.token);
        setRefreshToken(response.data.refreshToken);
        setExpiresAt(response.data.expiresAt);
        return response.data;
      } else {
        throw new Error('登录失败，请检查用户名和密码');
      }
    } catch (err) {
      error.value = err.message || '登录失败，请稍后再试';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 登出
  const logoutUser = async () => {
    try {
      isLoading.value = true;
      error.value = null;
      
      if (token.value) {
        await logout();
      }
      
      clearAuth();
      return true;
    } catch (err) {
      error.value = err.message || '登出失败，请稍后再试';
      // 即使API调用失败，也清除本地存储
      clearAuth();
      return true;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 刷新令牌
  const refreshUserToken = async () => {
    try {
      if (!refreshTokenValue.value) {
        throw new Error('没有可用的刷新令牌');
      }
      
      isLoading.value = true;
      error.value = null;
      
      const response = await refreshToken({ refreshToken: refreshTokenValue.value });
      
      if (response.data && response.data.token) {
        setToken(response.data.token);
        setRefreshToken(response.data.refreshToken);
        setExpiresAt(response.data.expiresAt);
        return response.data;
      } else {
        throw new Error('刷新令牌失败');
      }
    } catch (err) {
      error.value = err.message || '刷新令牌失败，请重新登录';
      clearAuth();
      throw err;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 设置令牌
  const setToken = (newToken) => {
    token.value = newToken;
    localStorage.setItem('token', newToken);
  };
  
  // 设置刷新令牌
  const setRefreshToken = (newRefreshToken) => {
    refreshTokenValue.value = newRefreshToken;
    localStorage.setItem('refreshToken', newRefreshToken);
  };
  
  // 设置过期时间
  const setExpiresAt = (newExpiresAt) => {
    expiresAt.value = newExpiresAt;
    localStorage.setItem('expiresAt', newExpiresAt.toString());
  };
  
  // 清除认证信息
  const clearAuth = () => {
    token.value = '';
    refreshTokenValue.value = '';
    expiresAt.value = 0;
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('expiresAt');
  };
  
  return {
    // 状态
    token,
    refreshTokenValue,
    expiresAt,
    isLoading,
    error,
    
    // 计算属性
    isLoggedIn,
    isTokenExpired,
    
    // 方法
    loginUser,
    logoutUser,
    refreshUserToken,
    setToken,
    setRefreshToken,
    setExpiresAt,
    clearAuth
  };
});
