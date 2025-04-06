/**
 * API响应类型
 */
export interface ApiResponse<T> {
  code: number;
  data: T;
  message: string;
  success?: boolean;
}

/**
 * 个人资料类型
 */
export interface ProfileData {
  email: string;
  username: string;
  name: string;
  phoneNumber: string;
  gender: string;
  birthday?: string;
  bio: string;
  avatar: string;
  courseCount?: number;
  studyHours?: number;
  points?: number;
}

/**
 * 扩展的用户信息类型
 */
export interface ExtendedUserInfo {
  id: string;
  name: string;
  avatar?: string;
  role: string;
  email: string;
  phone?: string;
  studentId?: string;
  department?: string;
  major?: string;
  grade?: string;
  createdAt: string;
  lastLoginAt: string;
  courseCount?: number;
  studyHours?: number;
  points?: number;
} 