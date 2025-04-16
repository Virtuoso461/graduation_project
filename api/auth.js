// 认证相关API
import api from './index';

export const login = (data) => {
  return api.post('/auth/login', data);
};

export const logout = () => {
  return api.post('/auth/logout');
};

export const refreshToken = () => {
  return api.post('/auth/refresh-token');
};

export const resetPassword = (email) => {
  return api.post('/auth/password-reset', { email });
};

export const confirmResetPassword = (data) => {
  return api.post('/auth/password-reset/confirm', data);
};

export default {
  login,
  logout,
  refreshToken,
  resetPassword,
  confirmResetPassword
};
