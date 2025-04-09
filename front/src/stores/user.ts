import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { profileApi, userApi } from '@/utils/http/api'
import Cookies from 'js-cookie'
import { ApiResponse, ExtendedUserInfo } from '@/types/api'
import { clearCurrentUserInfo } from '@/utils/indexedDB'

// 登录响应数据类型
interface LoginResponseData {
  id: string
  username: string
  role: string
  token?: string
}

interface UserState {
  id: string,
  name: string,
  avatar: string,
  role: string,
  email: string, // 用户名就是邮箱
  createdAt: string,
  lastLoginAt: string,
  // 添加默认值
  courseCount: String,
  studyHours: String,

}

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref<ExtendedUserInfo | null>(null)

  // 登录状态
  const isLoggedIn = ref(false)

  // 加载状态
  const loading = ref(false)

  // 初始化用户状态
  function initUserState() {
    const storedUser = localStorage.getItem('user')
    const token = localStorage.getItem('token')

    if (storedUser && token) {
      try {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true

        // 恢复email cookie
        if (userInfo.value?.email) {
          Cookies.set('userEmail', userInfo.value.email, { expires: 7 })
        }
      } catch (error) {
        clearUserState()
      }
    }
  }

  // 清除用户状态
  function clearUserState() {
    userInfo.value = null
    isLoggedIn.value = false
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    localStorage.removeItem('currentUserId')
    Cookies.remove('userEmail')
  }

  // 登录
  async function login(credentials: { username: string; password: string }) {
    loading.value = true

    try {
      // 调用真实的登录API
      // console.log('开始登录，请求参数:', credentials);

      const result = await userApi.login(credentials) as ApiResponse<LoginResponseData>;
      console.log('登录API返回结果:', result);

      if (result.code === 200 && result.data) {
        const userData = result.data;
        console.log('登录成功，登录接口返回的用户信息:', userData);

        // 将用户ID保存到localStorage，以便后续使用
        localStorage.setItem('currentUserId', credentials.username);

        // 构建基本用户信息（仅包含登录必需的字段，其他字段将从后端获取）
        const user: ExtendedUserInfo = {
          id: userData.id,
          name: '', // 留空，等待从后端获取真实姓名
          avatar: '', // 留空，等待从后端获取头像
          role: userData.role,
          email: credentials.username, // 用户名就是邮箱
          createdAt: new Date().toISOString(),
          lastLoginAt: new Date().toISOString(),
          // 添加默认值
          courseCount: 0,
          studyHours: 0,
          points: 0
        };

        // 从后端获取profile信息，并直接保存到IndexedDB
        try {
          // 将用户的username作为profile表的email主键去查询结果
          console.log('尝试从后端获取用户资料，使用email:', credentials.username);

          let profileData = null;
          let useDefaultData = false;

          try {
            // 尝试从后端获取profile数据
            const profile = await profileApi.getProfile(credentials.username) as ApiResponse<any>;
            console.log('获取到的profile响应:', profile);

            // 检查是否成功获取到数据
            if (profile && profile.code === 200 && profile.data) {
              console.log('成功获取用户资料，准备保存到IndexedDB:', profile.data);

              // 使用后端返回的数据
              profileData = {
                ...profile.data,
                email: credentials.username // 确保有email字段作为主键
              };
              console.log('将保存到IndexedDB的真实后端数据:', profileData);
            } else {
              console.warn('从后端获取用户资料失败或返回空数据');
              useDefaultData = true;
            }
          } catch (profileError) {
            console.error('获取用户资料失败:', profileError);
            useDefaultData = true;
          }

          // 如果需要使用默认数据
          if (useDefaultData) {
            // 创建默认的profile数据
            profileData = {
              email: credentials.username,
              name: '',
              avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
              phoneNumber: '',
              gender: '',
              bio: '',
              birthday: '',
              username: credentials.username.split('@')[0]
            };
            console.log('使用默认的profile数据:', profileData);
          }

          // 保存数据到IndexedDB
          if (profileData) {
            try {
              const { saveUserInfo } = await import('@/utils/indexedDB');
              await saveUserInfo(profileData);
              console.log('已将profile数据保存到IndexedDB');

              // 将profile数据合并到user对象中，用于前端显示
              user.name = profileData.name || '';
              user.avatar = profileData.avatar || '';
              user.role = userData.role; // 保留登录接口返回的role
              user.email = credentials.username;
            } catch (dbError) {
              console.error('保存profile数据到IndexedDB失败:', dbError);
            }
          }
        } catch (error) {
          console.error('处理profile数据过程中发生错误:', error);
        }

        console.log('最终构建的用户信息:', user);

        // 注意：我们已经在上面将profile数据保存到IndexedDB了
        // 这里不需要重复保存

        // 保存用户信息和令牌
        userInfo.value = user;
        isLoggedIn.value = true;
        // 存储到本地存储
        localStorage.setItem('user', JSON.stringify(user));
        localStorage.setItem('token', userData.token || 'mock-token');

        console.log('登录成功，已保存用户信息和令牌');
        ElMessage.success('登录成功');
        return true;
      } else {
        console.error('登录失败，API响应错误:', result);
        ElMessage.error(result.message || '登录失败，请检查用户名和密码');
        return false;
      }
    } catch (error) {
      console.error('登录请求出错:', error);
      ElMessage.error('登录失败，请检查用户名和密码')
      return false
    } finally {
      loading.value = false
    }
  }

  // 注册
  async function register(userData: {
    username: string
    email: string
    password: string
    phone: string
    studentId?: string
  }) {
    loading.value = true

    try {
      // 在实际应用中，这里会调用真实的注册API
      // 这里使用模拟数据代替
      await new Promise(resolve => setTimeout(resolve, 1000))

      // 模拟注册成功
      const mockUser: ExtendedUserInfo = {
        id: Math.random().toString(36).substr(2, 9),
        name: userData.username,
        avatar: '',
        role: '学生',
        email: userData.email,
        phone: userData.phone,
        studentId: userData.studentId,
        department: '计算机科学与技术学院',
        major: '软件工程',
        grade: '2023级',
        createdAt: new Date().toISOString(),
        lastLoginAt: new Date().toISOString(),
        courseCount: 0,
        studyHours: 0,
        points: 0
      }

      // 保存用户信息和令牌
      userInfo.value = mockUser
      isLoggedIn.value = true

      // 存储到本地存储
      localStorage.setItem('user', JSON.stringify(mockUser))
      localStorage.setItem('token', 'mock-jwt-token')

      ElMessage.success('注册成功')
      return true
    } catch (error) {
      ElMessage.error('注册失败，请稍后重试')
      return false
    } finally {
      loading.value = false
    }
  }

  // 退出登录
  async function logout() {
    try {
      // 清除IndexedDB中的用户数据
      await clearCurrentUserInfo();
      // 清除其他用户状态
      clearUserState();
      ElMessage.success('已退出登录');
    } catch (error) {
      console.error('退出登录时清除数据失败:', error);
      // 即使清除IndexedDB失败，也继续清除其他状态
      clearUserState();
      ElMessage.success('已退出登录');
    }
  }

  // 更新用户信息
  function updateUserInfo(newUserInfo: Partial<ExtendedUserInfo>) {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...newUserInfo }
      localStorage.setItem('user', JSON.stringify(userInfo.value))

      // 如果更新了邮箱，也更新cookie
      if (newUserInfo.email) {
        Cookies.set('userEmail', newUserInfo.email, { expires: 7 })
      }
    }
  }

  // 获取当前登录用户的邮箱
  function getUserEmail(): string | undefined {
    return Cookies.get('userEmail') || userInfo.value?.email
  }

  // 初始化
  initUserState()

  return {
    userInfo,
    isLoggedIn,
    loading,
    login,
    register,
    logout,
    updateUserInfo,
    getUserEmail
  }
})