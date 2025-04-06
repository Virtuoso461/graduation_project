import { post, get, del, put } from './request'
import request from './request'
import { getCurrentUserInfo, getUserInfo, saveUserInfo } from '@/utils/indexedDB'

/**
 * 用户相关接口
 */
export const userApi = {
  // 学生登录
  login: (data: { username: string; password: string }) => 
    post('/student/login', data),
  
  // 教师登录
  teacherLogin: (data: { username: string; password: string }) => 
    post('/teacher/login', data),
  
  // 学生注册
  studentRegister: (data: {
    username: string;
    password: string;
    name: string;
    phoneNumber: string;
    number: string;
    role: string;
  }) => post('/student/register', data),
  
  // 教师注册
  teacherRegister: (data: {
    username: string;
    password: string;
    name: string;
    phoneNumber: string;
    number: string;
    role: string;
  }) => post('/teacher/register', data),
  
  // 获取用户信息
  getUserInfo: () => get('/user/info'),
  
  // 退出登录
  logout: () => post('/user/logout'),
  
  // 发送密码重置验证码
  sendResetCode: (email: string) => 
    get('/reset-password/code', { email }),
  
  // 验证重置验证码
  verifyResetCode: (email: string, code: string) => 
    get('/reset-password/verify', { email, code }),
  
  // 重置密码
  resetPassword: (data: { 
    email: string; 
    verificationCode: string; 
    newPassword: string 
  }) => post('/reset-password/reset', data)
}

/**
 * 个人资料相关接口
 */
export const profileApi = {
  // 获取个人资料 - 直接使用IndexedDB而不是模拟数据
  getProfile: async (email?: string) => {
    try {
      // 获取用户ID，优先使用传入的email，其次使用localStorage
      const userId = email || localStorage.getItem('currentUserId')
      if (!userId) {
        console.error('获取个人资料失败：未提供用户ID')
        return {
          code: 400,
          message: '未提供用户ID',
          data: {}
        }
      }
      
      // 从IndexedDB获取用户信息
      const userInfo = await getUserInfo(userId)
      
      if (userInfo) {
        console.log('从IndexedDB获取到用户信息:', userInfo)
        return {
          code: 200,
          message: 'success',
          data: userInfo
        }
      } else {
        // 如果找不到用户信息，则返回错误
        console.warn('未找到用户信息:', userId)
        return {
          code: 404,
          message: '未找到用户信息',
          data: {}
        }
      }
    } catch (error) {
      console.error('获取个人资料失败:', error)
      return {
        code: 500,
        message: '获取个人资料失败',
        data: {}
      }
    }
  },
  
  // 更新个人资料 - 直接保存到IndexedDB
  updateProfile: async (data: any) => {
    try {
      const userId = localStorage.getItem('currentUserId')
      if (!userId) {
        console.error('更新个人资料失败：未找到当前用户ID')
        return {
          code: 400,
          message: '未找到当前用户ID',
          data: {}
        }
      }
      
      // 获取现有用户信息
      const existingUser = await getUserInfo(userId)
      
      if (!existingUser) {
        console.warn('更新失败：未找到用户信息', userId)
        return {
          code: 404,
          message: '未找到用户信息',
          data: {}
        }
      }
      
      // 合并新的个人资料
      const updatedUserInfo = {
        ...existingUser,
        ...data,
        // 确保ID和关键字段不变
        id: existingUser.id,
        username: existingUser.username,
        role: existingUser.role
      }
      
      // 保存到IndexedDB
      await saveUserInfo(updatedUserInfo)
      console.log('已更新用户信息到IndexedDB:', updatedUserInfo)
      
      return {
        code: 200,
        message: 'success',
        data: updatedUserInfo
      }
    } catch (error) {
      console.error('更新个人资料失败:', error)
      return {
        code: 500,
        message: '更新个人资料失败',
        data: {}
      }
    }
  },
  
  // 初始化个人资料
  initProfile: async (data: any) => {
    try {
      if (!data || !data.id) {
        console.error('初始化个人资料失败：缺少用户ID')
        return {
          code: 400,
          message: '缺少用户ID',
          data: {}
        }
      }
      
      // 确保基本字段存在
      const profileData = {
        id: data.id,
        username: data.username || data.id,
        email: data.email || data.id,
        role: data.role || 'student',
        name: data.name || '',
        phoneNumber: data.phoneNumber || '',
        gender: data.gender || '',
        bio: data.bio || '',
        birthday: data.birthday || '',
        avatar: data.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        courseCount: data.courseCount || 0,
        studyHours: data.studyHours || 0,
        points: data.points || 0
      }
      
      // 保存到IndexedDB
      await saveUserInfo(profileData)
      console.log('已初始化用户个人资料到IndexedDB:', profileData)
      
      return {
        code: 200,
        message: 'success',
        data: profileData
      }
    } catch (error) {
      console.error('初始化个人资料失败:', error)
      return {
        code: 500,
        message: '初始化个人资料失败',
        data: {}
      }
    }
  }
}

/**
 * 课程相关接口
 */
export const courseApi = {
  // 获取课程列表
  getCourses: async (params?: any) => {
    try {
      console.log('尝试从后端获取课程列表')
      const response = await get('/api/courses', params) as any
      console.log('成功获取课程列表:', response)
      
      if (response && response.code === 200 && response.data) {
        return response
      }
      throw new Error('API返回数据无效')
    } catch (error) {
      console.error('API返回数据无效，使用模拟数据')
      // 返回模拟数据
      return {
        code: 200,
        message: 'success',
        data: [
          { id: '1', name: '数据结构与算法' },
          { id: '2', name: 'Web前端开发' },
          { id: '3', name: '数据库原理' },
          { id: '4', name: '操作系统' },
          { id: '5', name: '计算机网络' }
        ]
      }
    }
  },
  
  // 获取课程详情
  getCourseDetail: (id: string) => {
    console.log('使用模拟数据提供课程详情')
    return {
      code: 200,
      message: 'success',
      data: {
        id,
        name: id === '1' ? '数据结构与算法' :
              id === '2' ? 'Web前端开发' :
              id === '3' ? '数据库原理' :
              id === '4' ? '操作系统' : '计算机网络',
        description: '课程描述内容',
        teacherId: 1,
        approved: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        category: '专业课程',
        coverImage: 'https://example.com/cover.jpg',
        duration: 48,
        difficulty: '中级',
        credits: 4
      }
    }
  },
  
  // 获取已选课程
  getEnrolledCourses: (email: string) => {
    console.log('使用模拟数据提供已选课程')
    return {
      code: 200,
      message: 'success',
      data: [
        { id: '1', name: '数据结构与算法' },
        { id: '2', name: 'Web前端开发' }
      ]
    }
  },
  
  // 获取已完成课程
  getCompletedCourses: (email: string) => {
    console.log('使用模拟数据提供已完成课程')
    return {
      code: 200,
      message: 'success',
      data: [
        { id: '3', name: '数据库原理' }
      ]
    }
  },
  
  // 选课
  enrollCourse: (courseId: string, email: string) => {
    console.log('模拟选课请求')
    return {
      code: 200,
      message: '选课成功',
      data: { courseId, email }
    }
  }
}

/**
 * 学习收藏夹相关接口
 */
export const favoritesApi = {
  // 获取收藏夹列表
  getFolders: () => get('/favorites/folders'),
  
  // 创建收藏夹
  createFolder: (data: { name: string; description?: string }) => 
    post('/favorites/folders', data),
  
  // 获取收藏夹中的资源
  getFolderResources: (folderId: string) => 
    get(`/favorites/folders/${folderId}/resources`),
  
  // 获取所有收藏资源
  getAllFavorites: (params?: any) => 
    get('/favorites/resources', params),
  
  // 添加资源到收藏夹
  addToFavorites: (data: { resourceId: number; resourceType: string; folderId?: string }) => 
    post('/favorites/resources', data),
  
  // 从收藏夹中移除资源
  removeFromFavorites: (resourceId: number) => 
    post(`/favorites/resources/${resourceId}/remove`),
  
  // 移动收藏资源到其他收藏夹
  moveResource: (resourceId: number, folderId: string) => 
    post(`/favorites/resources/${resourceId}/move`, { folderId })
}

/**
 * AI学习助手相关接口
 */
export const aiApi = {
  // 发送聊天消息
  sendChatMessage: (data: {
    message: string;
    history?: Array<{type: 'user' | 'assistant', content: string}>;
    temperature?: number;
    max_tokens?: number;
  }) => post('/api/ai/zhipu/chat', {
    messages: [
      ...((data.history || []).map(msg => ({
        role: msg.type,
        content: msg.content
      }))),
      { role: 'user', content: data.message }
    ],
    temperature: data.temperature || 0.7,
    max_tokens: data.max_tokens || 500
  })
}

/**
 * 在线测试相关接口
 */
export const examApi = {
  // 获取可参加的考试列表
  getExamList: async (params?: any) => {
    console.log('尝试从后端获取考试列表数据')
    try {
      const response = await get('/api/exams', params)
      console.log('成功获取考试列表:', response)
      return response
    } catch (error) {
      console.error('获取考试列表失败，使用模拟数据:', error)
      // 返回模拟数据
      return {
        code: 200,
        message: 'success',
        exams: [
          {
            id: 1,
            title: '数据结构期中测试',
            description: '覆盖线性表、栈、队列、树等基础数据结构',
            courseName: '数据结构与算法',
            courseId: '1',
            duration: 90,
            questionCount: 50,
            totalScore: 100,
            endTime: new Date(Date.now() + 5*24*60*60*1000).toISOString(),
            category: 'midterm',
            difficulty: '中等',
            isPublished: true
          },
          {
            id: 2,
            title: 'Web前端基础测试',
            description: '测试HTML、CSS和JavaScript基础知识',
            courseName: 'Web前端开发',
            courseId: '2',
            duration: 60,
            questionCount: 40,
            totalScore: 100,
            endTime: new Date(Date.now() + 2*24*60*60*1000).toISOString(),
            category: 'chapter',
            difficulty: '简单',
            isPublished: true
          }
        ]
      }
    }
  },
  
  // 获取考试详情
  getExamDetail: (id: string) => {
    console.log('从后端获取考试详情')
    return get(`/api/exams/${id}`)
  },
  
  // 获取考试题目
  getExamQuestions: (examId: string) => {
    console.log('从后端获取考试题目')
    return get(`/api/exams/${examId}/questions`)
  },
  
  // 提交考试答案
  submitExam: (id: string, data: any) => {
    console.log('向后端提交考试答案')
    return post(`/api/exams/${id}/submit`, data)
  },
  
  // 获取考试结果
  getExamResult: (id: string, email: string) => {
    console.log('从后端获取考试结果')
    return get(`/api/exams/${id}/result`, { email })
  },
  
  // 获取考试排名
  getExamRanking: (examId: string) => {
    console.log('从后端获取考试排名')
    return get(`/api/exams/${examId}/ranking`)
  },
  
  // 获取错题解析
  getWrongQuestions: (examId: string, email: string) => {
    console.log('从后端获取错题解析')
    return get(`/api/exams/${examId}/wrong-questions`, { email })
  },
  
  // 获取知识点掌握情况
  getKnowledgePoints: (examId: string, email: string) => {
    console.log('从后端获取知识点掌握情况')
    return get(`/api/exams/${examId}/knowledge-points`, { email })
  },
  
  // 获取已完成的考试列表
  getCompletedExams: async (email: string) => {
    console.log('尝试从后端获取已完成考试数据')
    try {
      const response = await get('/api/exams/completed', { email })
      console.log('成功获取已完成考试:', response)
      return response
    } catch (error) {
      console.error('获取已完成考试失败，使用模拟数据:', error)
      // 返回模拟数据
      return {
        code: 200,
        message: 'success',
        exams: [
          {
            id: 3,
            examId: 3,
            title: '数据库原理测试',
            description: '关系型数据库基础知识及SQL语法',
            courseName: '数据库原理',
            courseId: '3',
            score: 85,
            totalScore: 100,
            ranking: '12/56',
            correctRate: 0.85,
            completedTime: new Date(Date.now() - 5*24*60*60*1000).toISOString(),
            category: 'midterm'
          }
        ]
      }
    }
  },
  
  // 获取考试统计数据
  getExamStats: async (email: string) => {
    console.log('尝试从后端获取考试统计数据')
    try {
      const response = await get('/api/exams/stats', { email })
      console.log('成功获取考试统计:', response)
      return response
    } catch (error) {
      console.error('获取考试统计失败，使用模拟数据:', error)
      // 返回模拟数据
      return {
        code: 200,
        message: 'success',
        data: {
          completedExams: 3,
          averageScore: 82.5,
          highestScore: 95,
          totalExams: 5,
          averageCorrectRate: 0.78,
          bestRanking: 5,
          bestExamTitle: '数据结构期中测试',
          typeDistribution: [
            { type: 'chapter', count: 2 },
            { type: 'midterm', count: 1 },
            { type: 'final', count: 0 }
          ]
        }
      }
    }
  },
  
  // 创建考试
  createExam: (data: any) => {
    console.log('向后端发送创建考试请求')
    return post('/api/exams', data)
  },
  
  // 更新考试
  updateExam: (id: string, data: any) => {
    console.log('向后端发送更新考试请求')
    return put(`/api/exams/${id}`, data)
  },
  
  // 删除考试
  deleteExam: (id: number) => {
    console.log('向后端发送删除考试请求')
    return del(`/api/exams/${id}`)
  }
}

/**
 * 作业相关接口
 */
export const assignmentApi = {
  // 获取作业列表
  getAssignments: () => get('/assignments'),
  
  // 获取作业详情
  getAssignmentDetail: (id: string) => get(`/assignments/${id}`),
  
  // 提交作业
  submitAssignment: (id: string, data: any) => post(`/assignments/${id}/submit`, data),
  
  // 获取作业反馈
  getAssignmentFeedback: (id: string) => get(`/assignments/${id}/feedback`),
  
  // 创建作业
  createAssignment: (data: any) => post('/assignments', data),
  
  // 更新作业
  updateAssignment: (id: string, data: any) => post(`/assignments/${id}`, data),
  
  // 删除作业
  deleteAssignment: (id: number) => del(`/assignments/${id}`)
}

/**
 * 资源库相关接口
 */
export const resourceLibraryApi = {
  // 获取资源列表
  getResources: (params?: { type?: string; category?: string }) => 
    get('/resource-library', params),
  
  // 获取资源详情
  getResourceDetail: (id: string) => 
    get(`/resource-library/${id}`),
  
  // 获取资源分类
  getResourceCategories: () => 
    get('/resource-library/categories'),
  
  // 获取资源类型
  getResourceTypes: () => 
    get('/resource-library/types')
}

/**
 * 学习社区相关接口
 */
export const communityApi = {
  // 获取帖子列表
  getPosts: (params?: any) => get('/community/posts', params),
  
  // 获取帖子详情
  getPostDetail: (id: string) => get(`/community/posts/${id}`),
  
  // 发布帖子
  createPost: (data: any) => post('/community/posts', data),
  
  // 回复帖子
  replyPost: (postId: number, data: any) => post(`/community/posts/${postId}/replies`, data),
  
  // 点赞帖子
  likePost: (postId: number) => post(`/community/posts/${postId}/like`),
  
  // 取消点赞
  unlikePost: (postId: number) => post(`/community/posts/${postId}/unlike`)
}

/**
 * 学习分析相关接口
 */
export const analyticsApi = {
  // 获取学习进度
  getLearningProgress: (email: string) => 
    get('/analytics/progress', { email }),
  
  // 获取学习时长统计
  getStudyTimeStats: (email: string, period: string) => 
    get('/analytics/study-time', { email, period }),
  
  // 获取考试成绩统计
  getExamScoreStats: (email: string) => 
    get('/analytics/exam-scores', { email }),
  
  // 获取知识点掌握情况
  getKnowledgePointMastery: (email: string) => 
    get('/analytics/knowledge-points', { email })
}

// 可根据需要扩展更多API 