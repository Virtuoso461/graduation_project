// 学生相关API
import api from './index';

// 个人中心
export const getProfile = () => {
  return api.get('/student/profile');
};

export const updateProfile = (data) => {
  return api.post('/student/profile', data);
};

export const uploadAvatar = (formData) => {
  return api.post('/student/profile/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const changePassword = (data) => {
  return api.post('/student/profile/change-password', data);
};

export const getStudyOverview = () => {
  return api.get('/student/profile/overview');
};

export const getLearningRecords = () => {
  return api.get('/student/profile/learning-records');
};

export const getProfileAssignments = () => {
  return api.get('/student/profile/assignments');
};

export const getProfileExams = () => {
  return api.get('/student/profile/exams');
};

export const getStatistics = () => {
  return api.get('/student/profile/statistics');
};

export const getStudyTrend = () => {
  return api.get('/student/profile/trend');
};

// 作业相关
export const getAssignments = () => {
  return api.get('/student/assignments');
};

export const getAssignmentDetail = (assignmentId) => {
  return api.get(`/student/assignments/${assignmentId}`);
};

export const submitAssignment = (data) => {
  return api.post('/student/assignments/submit', data);
};

export const uploadAssignment = (assignmentId, formData) => {
  return api.post(`/student/assignments/${assignmentId}/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const getAssignmentHistory = () => {
  return api.get('/student/assignments/history');
};

export const getAssignmentSubmission = (assignmentId) => {
  return api.get(`/student/assignments/submissions/${assignmentId}`);
};

export const getPendingAssignments = () => {
  return api.get('/student/assignments/pending');
};

export const getCompletedAssignments = () => {
  return api.get('/student/assignments/completed');
};

// 考试相关
export const getExams = () => {
  return api.get('/student/exams');
};

export const getExamDetail = (examId) => {
  return api.get(`/student/exams/${examId}`);
};

export const startExam = (examId) => {
  return api.post(`/student/exams/${examId}/start`);
};

export const submitExam = (data) => {
  return api.post('/student/exams/submit', data);
};

export const uploadExam = (examId, formData) => {
  return api.post(`/student/exams/${examId}/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const getExamResult = (examId) => {
  return api.get(`/student/exams/${examId}/result`);
};

export const getExamHistory = () => {
  return api.get('/student/exams/history');
};

export const getExamStatistics = () => {
  return api.get('/student/exams/statistics');
};

export const getWrongQuestions = () => {
  return api.get('/student/exams/wrong-questions');
};

export const getKnowledgePoints = () => {
  return api.get('/student/exams/knowledge-points');
};

export const getExamRanking = (examId) => {
  return api.get(`/student/exams/${examId}/ranking`);
};

// 社区相关
export const getCommunityPosts = () => {
  return api.get('/student/community/posts');
};

export const getPostDetail = (postId) => {
  return api.get(`/student/community/posts/${postId}`);
};

export const createPost = (data) => {
  return api.post('/student/community/posts', data);
};

export const updatePost = (postId, data) => {
  return api.put(`/student/community/posts/${postId}`, data);
};

export const deletePost = (postId) => {
  return api.delete(`/student/community/posts/${postId}`);
};

export const createReply = (postId, data) => {
  return api.post(`/student/community/posts/${postId}/replies`, data);
};

export const deleteReply = (replyId) => {
  return api.delete(`/student/community/replies/${replyId}`);
};

export const likePost = (postId) => {
  return api.post(`/student/community/posts/${postId}/like`);
};

export const unlikePost = (postId) => {
  return api.delete(`/student/community/posts/${postId}/like`);
};

export const getMyPosts = () => {
  return api.get('/student/community/my-posts');
};

export const getMyReplies = () => {
  return api.get('/student/community/my-replies');
};

export const getHotPosts = () => {
  return api.get('/student/community/hot-posts');
};

export const getCategories = () => {
  return api.get('/student/community/categories');
};

// 资源相关
export const getResources = () => {
  return api.get('/student/resources');
};

export const getResourceDetail = (resourceId) => {
  return api.get(`/student/resources/${resourceId}`);
};

export const collectResource = (resourceId) => {
  return api.post(`/student/resources/${resourceId}/collect`);
};

export const uncollectResource = (resourceId) => {
  return api.delete(`/student/resources/${resourceId}/collect`);
};

export const getCollections = () => {
  return api.get('/student/resources/collections');
};

export const getFolders = () => {
  return api.get('/student/resources/folders');
};

export const createFolder = (data) => {
  return api.post('/student/resources/folders', data);
};

export const updateFolder = (data) => {
  return api.put('/student/resources/folders', data);
};

export const moveResource = (resourceId, data) => {
  return api.put(`/student/resources/${resourceId}/move`, data);
};

export const updateResourceNotes = (resourceId, data) => {
  return api.put(`/student/resources/${resourceId}/notes`, data);
};

export const getHotResources = () => {
  return api.get('/student/resources/hot');
};

export const getLatestResources = () => {
  return api.get('/student/resources/latest');
};

export const accessResource = (resourceId) => {
  return api.post(`/student/resources/${resourceId}/access`);
};

// 课程相关
export const getAvailableCourses = () => {
  return api.get('/student/courses/available');
};

export const getCourseDetail = (courseId) => {
  return api.get(`/student/courses/${courseId}`);
};

export const enrollCourse = (courseId) => {
  return api.post(`/student/courses/${courseId}/enroll`);
};

export const getEnrolledCourses = () => {
  return api.get('/student/courses/enrolled');
};

export const updateCourseProgress = (courseId, data) => {
  return api.post(`/student/courses/${courseId}/progress`, data);
};

export const getCourseProgress = (courseId) => {
  return api.get(`/student/courses/${courseId}/progress`);
};

export const getCourseChapters = (courseId) => {
  return api.get(`/student/courses/${courseId}/chapters`);
};

export const getChapterDetail = (chapterId) => {
  return api.get(`/student/courses/chapters/${chapterId}`);
};

export const recordStudyTime = (courseId, data) => {
  return api.post(`/student/courses/${courseId}/study-time`, data);
};

export const getCourseStatistics = () => {
  return api.get('/student/courses/statistics');
};

export const getTranscript = () => {
  return api.get('/student/courses/transcript');
};

export const getRecommendations = () => {
  return api.get('/student/courses/recommendations');
};

// AI辅导相关
export const askQuestion = (data) => {
  return api.post('/student/ai-guidance/question', data);
};

export const getAIHistory = () => {
  return api.get('/student/ai-guidance/history');
};

export default {
  // 个人中心
  getProfile,
  updateProfile,
  uploadAvatar,
  changePassword,
  getStudyOverview,
  getLearningRecords,
  getProfileAssignments,
  getProfileExams,
  getStatistics,
  getStudyTrend,
  
  // 作业相关
  getAssignments,
  getAssignmentDetail,
  submitAssignment,
  uploadAssignment,
  getAssignmentHistory,
  getAssignmentSubmission,
  getPendingAssignments,
  getCompletedAssignments,
  
  // 考试相关
  getExams,
  getExamDetail,
  startExam,
  submitExam,
  uploadExam,
  getExamResult,
  getExamHistory,
  getExamStatistics,
  getWrongQuestions,
  getKnowledgePoints,
  getExamRanking,
  
  // 社区相关
  getCommunityPosts,
  getPostDetail,
  createPost,
  updatePost,
  deletePost,
  createReply,
  deleteReply,
  likePost,
  unlikePost,
  getMyPosts,
  getMyReplies,
  getHotPosts,
  getCategories,
  
  // 资源相关
  getResources,
  getResourceDetail,
  collectResource,
  uncollectResource,
  getCollections,
  getFolders,
  createFolder,
  updateFolder,
  moveResource,
  updateResourceNotes,
  getHotResources,
  getLatestResources,
  accessResource,
  
  // 课程相关
  getAvailableCourses,
  getCourseDetail,
  enrollCourse,
  getEnrolledCourses,
  updateCourseProgress,
  getCourseProgress,
  getCourseChapters,
  getChapterDetail,
  recordStudyTime,
  getCourseStatistics,
  getTranscript,
  getRecommendations,
  
  // AI辅导相关
  askQuestion,
  getAIHistory
};
