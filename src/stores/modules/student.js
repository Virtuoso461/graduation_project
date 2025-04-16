import { defineStore } from 'pinia';
import { ref, reactive, computed } from 'vue';
import { 
  getProfile, 
  getAssignments, 
  getExams, 
  getCourses,
  getResources,
  getStatistics
} from '@/api/student';

export const useStudentStore = defineStore('student', () => {
  // 状态
  const profile = ref(null);
  const assignments = ref([]);
  const exams = ref([]);
  const courses = ref([]);
  const resources = ref([]);
  const statistics = ref(null);
  const isLoading = ref(false);
  const error = ref(null);
  
  // 计算属性
  const pendingAssignments = computed(() => {
    return assignments.value.filter(assignment => assignment.status === 'PENDING');
  });
  
  const completedAssignments = computed(() => {
    return assignments.value.filter(assignment => assignment.status === 'COMPLETED' || assignment.status === 'GRADED');
  });
  
  const upcomingExams = computed(() => {
    return exams.value.filter(exam => exam.status === 'UPCOMING');
  });
  
  const completedExams = computed(() => {
    return exams.value.filter(exam => exam.status === 'COMPLETED');
  });
  
  const enrolledCourses = computed(() => {
    return courses.value.filter(course => course.enrolled);
  });
  
  const collectedResources = computed(() => {
    return resources.value.filter(resource => resource.collected);
  });
  
  // 方法
  // 获取个人资料
  const fetchProfile = async () => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getProfile();
      profile.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取个人资料失败';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 获取作业列表
  const fetchAssignments = async (params = {}) => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getAssignments(params);
      assignments.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取作业列表失败';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 获取考试列表
  const fetchExams = async (params = {}) => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getExams(params);
      exams.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取考试列表失败';
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
  
  // 获取资源列表
  const fetchResources = async (params = {}) => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getResources(params);
      resources.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取资源列表失败';
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
    profile.value = null;
    assignments.value = [];
    exams.value = [];
    courses.value = [];
    resources.value = [];
    statistics.value = null;
    error.value = null;
  };
  
  return {
    // 状态
    profile,
    assignments,
    exams,
    courses,
    resources,
    statistics,
    isLoading,
    error,
    
    // 计算属性
    pendingAssignments,
    completedAssignments,
    upcomingExams,
    completedExams,
    enrolledCourses,
    collectedResources,
    
    // 方法
    fetchProfile,
    fetchAssignments,
    fetchExams,
    fetchCourses,
    fetchResources,
    fetchStatistics,
    resetState
  };
});
