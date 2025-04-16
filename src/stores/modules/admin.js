import { defineStore } from 'pinia';
import { ref, reactive, computed } from 'vue';
import { 
  getUsers, 
  getCourses, 
  getSettings,
  getStatistics
} from '@/api/admin';

export const useAdminStore = defineStore('admin', () => {
  // 状态
  const users = ref([]);
  const courses = ref([]);
  const settings = ref(null);
  const statistics = ref(null);
  const isLoading = ref(false);
  const error = ref(null);
  
  // 计算属性
  const studentUsers = computed(() => {
    return users.value.filter(user => user.role === 'STUDENT');
  });
  
  const teacherUsers = computed(() => {
    return users.value.filter(user => user.role === 'TEACHER');
  });
  
  const adminUsers = computed(() => {
    return users.value.filter(user => user.role === 'ADMIN');
  });
  
  const pendingCourses = computed(() => {
    return courses.value.filter(course => course.status === 'PENDING');
  });
  
  const approvedCourses = computed(() => {
    return courses.value.filter(course => course.status === 'APPROVED');
  });
  
  const rejectedCourses = computed(() => {
    return courses.value.filter(course => course.status === 'REJECTED');
  });
  
  // 方法
  // 获取用户列表
  const fetchUsers = async (params = {}) => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getUsers(params);
      users.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取用户列表失败';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 获取课程列表
  const fetchCourses = async (params = {}) => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getCourses(params);
      courses.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取课程列表失败';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 获取系统设置
  const fetchSettings = async () => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getSettings();
      settings.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取系统设置失败';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 获取统计数据
  const fetchStatistics = async () => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getStatistics();
      statistics.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取统计数据失败';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 重置状态
  const resetState = () => {
    users.value = [];
    courses.value = [];
    settings.value = null;
    statistics.value = null;
    error.value = null;
  };
  
  return {
    // 状态
    users,
    courses,
    settings,
    statistics,
    isLoading,
    error,
    
    // 计算属性
    studentUsers,
    teacherUsers,
    adminUsers,
    pendingCourses,
    approvedCourses,
    rejectedCourses,
    
    // 方法
    fetchUsers,
    fetchCourses,
    fetchSettings,
    fetchStatistics,
    resetState
  };
});
