import { defineStore } from 'pinia';
import { ref, reactive, computed } from 'vue';
import { 
  getProfile, 
  getCourses, 
  getAssignments, 
  getExams,
  getStudents,
  getResources,
  getTodos
} from '@/api/teacher';

export const useTeacherStore = defineStore('teacher', () => {
  // 状态
  const profile = ref(null);
  const courses = ref([]);
  const assignments = ref([]);
  const exams = ref([]);
  const students = ref([]);
  const resources = ref([]);
  const todos = ref([]);
  const isLoading = ref(false);
  const error = ref(null);
  
  // 计算属性
  const activeCourses = computed(() => {
    return courses.value.filter(course => course.status === 'ACTIVE');
  });
  
  const pendingAssignments = computed(() => {
    return assignments.value.filter(assignment => assignment.status === 'PENDING');
  });
  
  const gradedAssignments = computed(() => {
    return assignments.value.filter(assignment => assignment.status === 'GRADED');
  });
  
  const upcomingExams = computed(() => {
    return exams.value.filter(exam => exam.status === 'UPCOMING');
  });
  
  const completedExams = computed(() => {
    return exams.value.filter(exam => exam.status === 'COMPLETED');
  });
  
  const pendingTodos = computed(() => {
    return todos.value.filter(todo => !todo.completed);
  });
  
  const completedTodos = computed(() => {
    return todos.value.filter(todo => todo.completed);
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
  
  // 获取学生列表
  const fetchStudents = async (params = {}) => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getStudents(params);
      students.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取学生列表失败';
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
  
  // 获取待办事项列表
  const fetchTodos = async (params = {}) => {
    try {
      isLoading.value = true;
      error.value = null;
      
      const response = await getTodos(params);
      todos.value = response.data;
      
      return response.data;
    } catch (err) {
      error.value = err.message || '获取待办事项列表失败';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };
  
  // 重置状态
  const resetState = () => {
    profile.value = null;
    courses.value = [];
    assignments.value = [];
    exams.value = [];
    students.value = [];
    resources.value = [];
    todos.value = [];
    error.value = null;
  };
  
  return {
    // 状态
    profile,
    courses,
    assignments,
    exams,
    students,
    resources,
    todos,
    isLoading,
    error,
    
    // 计算属性
    activeCourses,
    pendingAssignments,
    gradedAssignments,
    upcomingExams,
    completedExams,
    pendingTodos,
    completedTodos,
    
    // 方法
    fetchProfile,
    fetchCourses,
    fetchAssignments,
    fetchExams,
    fetchStudents,
    fetchResources,
    fetchTodos,
    resetState
  };
});
