<template>
  <div class="exams-view">
    <div class="page-header">
      <div class="header-left">
      <h1>在线测试</h1>
        <span class="page-desc">参加测试、查看成绩并跟踪学习进度</span>
      </div>
      <div class="header-actions">
        <el-button v-if="userInfo.role === 'teacher'" type="primary" @click="navigateToCreate">
          <el-icon><Plus /></el-icon>创建测试
        </el-button>
        <el-button type="primary" @click="refreshExams" :loading="isLoading">
        <el-icon><Refresh /></el-icon>刷新列表
      </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <el-skeleton :loading="isLoading" animated :rows="10" v-if="isLoading">
      <template #template>
        <div style="padding: 20px;">
          <el-skeleton-item variant="p" style="width: 100%; height: 50px; margin-bottom: 20px" />
          <div style="display: flex; gap: 20px; margin-bottom: 20px;">
            <el-skeleton-item variant="text" style="width: 20%" />
            <el-skeleton-item variant="text" style="width: 20%" />
          </div>
          <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px;">
            <div style="height: 200px;">
              <el-skeleton-item variant="p" style="width: 100%; height: 100%;" />
            </div>
            <div style="height: 200px;">
              <el-skeleton-item variant="p" style="width: 100%; height: 100%;" />
            </div>
            <div style="height: 200px;">
              <el-skeleton-item variant="p" style="width: 100%; height: 100%;" />
            </div>
          </div>
        </div>
      </template>
    </el-skeleton>

    <!-- 错误信息 -->
    <el-alert v-if="error && !isLoading" type="error" :title="error" show-icon closable />

    <div v-if="!isLoading" class="exams-container">
      <el-tabs v-model="activeTab" stretch>
        <!-- 教师可见:我创建的测试 -->
        <el-tab-pane v-if="userInfo.role === 'teacher'" label="我创建的测试" name="created">
          <div class="filter-bar">
            <el-select v-model="filterCourse" placeholder="按课程筛选" clearable>
              <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id"></el-option>
            </el-select>
            <el-select v-model="filterType" placeholder="测试类型" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="章节测试" value="chapter"></el-option>
              <el-option label="期中测试" value="midterm"></el-option>
              <el-option label="期末测试" value="final"></el-option>
            </el-select>
          </div>

          <div class="created-exams-content">
            <el-table 
              v-if="createdExams.length > 0"
              :data="createdExams" 
              style="width: 100%"
              border
              stripe
              highlight-current-row
              :row-class-name="tableRowClassName">
              <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip></el-table-column>
              <el-table-column label="类型" width="120">
                <template #default="scope">
                  <el-tag :type="getExamTypeTag(scope.row.type)">
                    {{ getExamTypeLabel(scope.row.type) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="courseName" label="课程" width="150" show-overflow-tooltip></el-table-column>
              <el-table-column prop="deadlineStr" label="截止日期" width="180"></el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.enabled ? 'success' : 'info'">
                    {{ scope.row.enabled ? '已启用' : '已禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="220">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="viewExamDetail(scope.row)">
                    <el-icon><View /></el-icon>查看
                  </el-button>
                  <el-button size="small" type="success" @click="editExam(scope.row)">
                    <el-icon><Edit /></el-icon>编辑
                  </el-button>
                  <el-button size="small" type="danger" @click="handleDeleteExam(scope.row)">
                    <el-icon><Delete /></el-icon>删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <el-empty 
              v-else 
              description="暂无创建的测试" 
              :image-size="200">
              <el-button type="primary" @click="navigateToCreate">立即创建</el-button>
            </el-empty>
          </div>
        </el-tab-pane>

        <el-tab-pane label="可参加测试" name="available">
          <div class="filter-bar">
            <el-select v-model="filterCourse" placeholder="按课程筛选" clearable>
              <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id"></el-option>
            </el-select>
            <el-select v-model="filterType" placeholder="测试类型" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="章节测试" value="chapter"></el-option>
              <el-option label="期中测试" value="midterm"></el-option>
              <el-option label="期末测试" value="final"></el-option>
            </el-select>
          </div>

          <div class="available-exams-content">
            <div v-if="filteredAvailableExams.length > 0" class="exam-list">
              <el-card v-for="exam in filteredAvailableExams" :key="exam.id" class="exam-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3>{{ exam.title }}</h3>
                  <el-tag :type="getExamTypeTag(exam.type)">
                    {{ getExamTypeLabel(exam.type) }}
                  </el-tag>
                </div>
              </template>
              <div class="exam-content">
                <p class="exam-course">
                  <el-icon><Reading /></el-icon>
                  所属课程：{{ exam.courseName }}
                </p>
                <p class="exam-desc">{{ exam.description }}</p>
                <div class="exam-info">
                  <span>
                    <el-icon><Clock /></el-icon>
                    时长：{{ exam.duration }}分钟
                  </span>
                  <span>
                    <el-icon><Document /></el-icon>
                    题目数：{{ exam.questionCount }}题
                  </span>
                  <span>
                    <el-icon><Star /></el-icon>
                    总分：{{ exam.totalScore }}分
                  </span>
                  <span>
                    <el-icon><Timer /></el-icon>
                      截止：{{ exam.deadlineStr }}
                  </span>
                </div>
                  <div class="time-remaining" v-if="calculateTimeRemaining(exam.deadline)">
                    <span class="label">剩余时间</span>
                    <el-progress 
                      :percentage="calculateTimePercentage(exam.deadline)" 
                      :color="getTimeRemainingColor(exam.deadline)"
                      :format="() => formatTimeRemaining(exam.deadline)"
                      :stroke-width="10"
                    ></el-progress>
                </div>
                <div class="exam-actions">
                  <el-button type="primary" @click="startExam(exam)">开始测试</el-button>
                  <el-button @click="viewExamDetail(exam)">查看详情</el-button>
                </div>
              </div>
            </el-card>
            </div>

            <el-empty 
              v-else 
              description="暂无可参加的测试" 
              :image-size="200">
              <p class="empty-text">目前没有可参加的测试，请稍后再查看</p>
            </el-empty>
          </div>
        </el-tab-pane>

        <el-tab-pane label="已完成测试" name="completed">
          <div class="filter-bar">
            <el-select v-model="filterCompletedCourse" placeholder="按课程筛选" clearable>
              <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id"></el-option>
            </el-select>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              clearable
            ></el-date-picker>
          </div>

          <div class="completed-exams-content">
            <div v-if="filteredCompletedExams.length > 0" class="exam-list">
              <el-card v-for="exam in filteredCompletedExams" :key="exam.id" class="exam-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3>{{ exam.title }}</h3>
                  <div class="header-right">
                    <el-tag :type="getExamTypeTag(exam.type)">
                      {{ getExamTypeLabel(exam.type) }}
                    </el-tag>
                      <el-tag type="success" v-if="exam.score / exam.totalScore >= 0.6" class="score-tag">及格</el-tag>
                    <el-tag type="danger" v-else class="score-tag">不及格</el-tag>
                  </div>
                </div>
              </template>
              <div class="exam-content">
                <p class="exam-course">
                  <el-icon><Reading /></el-icon>
                  所属课程：{{ exam.courseName }}
                </p>
                <div class="exam-result">
                  <div class="result-item">
                    <span class="label">得分</span>
                      <span class="value score">{{ exam.score }}/{{ exam.totalScore }}</span>
                  </div>
                  <div class="result-item">
                    <span class="label">排名</span>
                    <span class="value">{{ exam.ranking }}</span>
                  </div>
                  <div class="result-item">
                    <span class="label">正确率</span>
                      <span class="value">{{ exam.correctRate ? exam.correctRate.toFixed(1) : 0 }}%</span>
                  </div>
                  <div class="result-item">
                    <span class="label">完成时间</span>
                    <span class="value">{{ exam.completedTime }}</span>
                  </div>
                </div>
                  <div class="score-progress">
                    <span class="progress-label">成绩评分</span>
                    <el-progress 
                      :percentage="Math.round(exam.score / exam.totalScore * 100)" 
                      :status="getScoreStatus(exam.score, exam.totalScore)"
                      :stroke-width="16"
                    ></el-progress>
                </div>
                <div class="exam-actions">
                  <el-button type="primary" @click="viewExamResult(exam)">查看结果</el-button>
                  <el-button @click="reviewExam(exam)">错题复习</el-button>
                </div>
              </div>
            </el-card>
            </div>

            <el-empty 
              v-else 
              description="暂无已完成的测试" 
              :image-size="200">
              <p class="empty-text">你还没有完成任何测试</p>
              <el-button v-if="availableExams.length > 0" type="primary" @click="activeTab = 'available'">
                去参加测试
              </el-button>
            </el-empty>
          </div>
        </el-tab-pane>

        <el-tab-pane label="测试统计" name="stats">
          <div class="stats-dashboard">
            <div class="stats-cards">
              <el-card class="stats-card" shadow="hover">
                <template #header>
                  <div class="stats-card-header">
                    <el-icon><Document /></el-icon>
                    <span>{{ userInfo.role === 'teacher' ? '创建的测试' : '参加的测试' }}</span>
                  </div>
                </template>
                <div class="stats-value">{{ stats.createdCount || stats.completedCount || 0 }}</div>
              </el-card>
              
              <el-card class="stats-card" shadow="hover">
                <template #header>
                  <div class="stats-card-header">
                    <el-icon><Check /></el-icon>
                    <span>{{ userInfo.role === 'teacher' ? '学生参与数' : '完成的测试' }}</span>
                  </div>
                </template>
                <div class="stats-value">{{ stats.completedCount || 0 }}</div>
              </el-card>
              
              <el-card class="stats-card" shadow="hover">
                <template #header>
                  <div class="stats-card-header">
                    <el-icon><Star /></el-icon>
                    <span>平均分数</span>
                  </div>
                </template>
                <div class="stats-value">{{ stats.averageScore ? stats.averageScore.toFixed(1) : 0 }}</div>
              </el-card>
              
              <el-card class="stats-card" shadow="hover">
                <template #header>
                  <div class="stats-card-header">
                    <el-icon><Trophy /></el-icon>
                    <span>最高分数</span>
                  </div>
                </template>
                <div class="stats-value">{{ stats.highestScore || 0 }}</div>
              </el-card>
            </div>

            <div class="stats-charts">
              <el-card shadow="hover">
                <template #header>
                  <div class="chart-header">
                    <span>测试类型分布</span>
                  </div>
                </template>
                <div class="chart-placeholder">
                  <div v-if="stats.typeDistribution && stats.typeDistribution.length > 0" class="simple-chart">
                    <div 
                      v-for="(item, index) in stats.typeDistribution" 
                      :key="index"
                      class="chart-bar"
                    >
                      <div class="bar-label">{{ getExamTypeLabel(item.type) }}</div>
                      <div class="bar-container">
                        <div 
                          class="bar" 
                          :style="{ width: (item.count / Math.max(...stats.typeDistribution.map(t => t.count)) * 100) + '%' }"
                          :class="{ 'bar-chapter': item.type === 'chapter', 'bar-midterm': item.type === 'midterm', 'bar-final': item.type === 'final' }"
                        ></div>
                      </div>
                      <div class="bar-value">{{ item.count }}</div>
                    </div>
                  </div>
                  <el-empty v-else description="暂无数据" :image-size="150"></el-empty>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Refresh, 
  Clock, 
  Timer, 
  Document, 
  Star, 
  Reading,
  Check,
  Trophy,
  Plus,
  View,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import { examApi } from '@/utils/http/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Exam, ExamResult, ExamStats } from '@/types/exam'
import type { ApiResponse } from '@/types/api'
import { courseApi } from '@/utils/http/api'

const router = useRouter()
const activeTab = ref('available')
const filterCourse = ref('')
const filterType = ref('')
const filterCompletedCourse = ref('')
const dateRange = ref<[string, string] | null>(null)
const isLoading = ref(false)

// 从本地存储获取用户信息
const getUserInfoFromStorage = (): { email: string; role: string } => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      return JSON.parse(userInfoStr)
    }
  } catch (err) {
    console.error('解析用户信息失败:', err)
  }
  // 使用默认邮箱和角色
  return { email: '2416050435@qq.com', role: 'student' }
}

// 课程列表 - 后续从API获取
const courses = ref([
  { id: '1', name: '数据结构与算法' },
  { id: '2', name: 'Web前端开发' },
  { id: '3', name: '数据库原理' },
  { id: '4', name: '操作系统' }
])

// 数据状态
const availableExams = ref<Exam[]>([])
const completedExams = ref<ExamResult[]>([])
const createdExams = ref<Exam[]>([])
const stats = ref<ExamStats>({
  createdCount: 0,
  completedCount: 0,
  averageScore: 0,
  highestScore: 0
})
const error = ref<string | null>(null)

// 用户信息 - 尝试从本地存储获取，如果没有则使用默认值
const userInfo = ref(getUserInfoFromStorage())

// 格式化日期显示
const formatDate = (dateStr: string): string => {
  if (!dateStr) return '未知时间'
  try {
    const date = new Date(dateStr)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  } catch (err) {
    console.error('日期格式化错误:', err)
    return dateStr
  }
}

// 定义接口类型
interface ExamResponse {
  exams?: any[];
  [key: string]: any;
}

interface CompletedExamResponse {
  exams?: any[];
  [key: string]: any;
}

interface ExamStatsResponse {
  completedExams?: number;
  averageScore?: number;
  totalScore?: number;
  totalExams?: number;
  averageCorrectRate?: number;
  bestRanking?: number;
  bestExamTitle?: string;
  [key: string]: any;
}

// 模拟考试数据，用于在API返回错误时提供默认数据
const mockExamData = () => {
  return [
    {
      id: 1,
      title: '数据结构期中测试',
      description: '覆盖线性表、栈、队列、树等基础数据结构',
      courseName: '数据结构与算法',
      courseId: '1',
      duration: 90,
      questionCount: 50,
      totalScore: 100,
      deadline: new Date(Date.now() + 5*24*60*60*1000).toISOString(),
      deadlineStr: new Date(Date.now() + 5*24*60*60*1000).toLocaleDateString('zh-CN', {
        year: 'numeric', month: '2-digit', day: '2-digit', 
        hour: '2-digit', minute: '2-digit'
      }),
      type: 'midterm',
      difficulty: '中等',
      timeRemaining: 5*24*60*60*1000,
      enabled: true,
      email: 'admin@example.com',
      createdAt: new Date().toISOString()
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
      deadline: new Date(Date.now() + 2*24*60*60*1000).toISOString(),
      deadlineStr: new Date(Date.now() + 2*24*60*60*1000).toLocaleDateString('zh-CN', {
        year: 'numeric', month: '2-digit', day: '2-digit', 
        hour: '2-digit', minute: '2-digit'
      }),
      type: 'chapter',
      difficulty: '简单',
      timeRemaining: 2*24*60*60*1000,
      enabled: true,
      email: 'admin@example.com',
      createdAt: new Date().toISOString()
    }
  ]
}

// 根据当前标签页获取数据
const fetchData = () => {
  isLoading.value = true
  error.value = null
  
  // 先加载课程列表
  fetchCourseList()
  
  switch (activeTab.value) {
    case 'available':
      fetchAvailableExams()
      break
    case 'completed':
      fetchCompletedExams()
      break
    case 'created':
      fetchCreatedExams()
      break
    case 'stats':
      fetchExamStats()
      break
  }
}

// 过滤后的可参加考试
const filteredAvailableExams = computed(() => {
  return availableExams.value.filter(exam => {
    const courseMatch = !filterCourse.value || exam.courseId === filterCourse.value
    const typeMatch = !filterType.value || exam.type === filterType.value
    return courseMatch && typeMatch
  })
})

// 过滤后的已完成考试
const filteredCompletedExams = computed(() => {
  return completedExams.value.filter(exam => {
    const courseMatch = !filterCompletedCourse.value || exam.courseId === filterCompletedCourse.value
    
    // 日期范围过滤
    let dateMatch = true
    if (dateRange.value && dateRange.value.length === 2) {
      const examDate = new Date(exam.completedTime).setHours(0, 0, 0, 0)
      const startDate = new Date(dateRange.value[0]).setHours(0, 0, 0, 0)
      const endDate = new Date(dateRange.value[1]).setHours(0, 0, 0, 0)
      dateMatch = examDate >= startDate && examDate <= endDate
    }
    
    return courseMatch && dateMatch
  })
})

// 获取考试类型标签样式
const getExamTypeTag = (type: string) => {
  switch (type) {
    case 'chapter': return 'info'
    case 'midterm': return 'warning'
    case 'final': return 'danger'
    default: return 'info'
  }
}

// 获取考试类型显示文本
const getExamTypeLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    'chapter': '章节测试',
    'midterm': '期中测试',
    'final': '期末测试'
  }
  return typeMap[type] || type
}

// 刷新考试列表
const refreshExams = () => {
  fetchData()
  ElMessage.success('刷新成功')
}

// 获取课程列表
const fetchCourseList = async () => {
  try {
    console.log('开始获取课程列表...')
    const res = await courseApi.getCourses()
    
    if (res && typeof res === 'object') {
      // 首先尝试标准格式: res.code === 200 && Array.isArray(res.data)
      if ('code' in res && res.code === 200 && 'data' in res && Array.isArray(res.data)) {
        courses.value = res.data.map((course: any) => ({
          id: String(course.id || course.courseId || ''),
          name: course.name || course.courseName || '未命名课程'
        }))
        console.log('获取课程列表成功:', courses.value)
      } 
      // 然后尝试直接是数组的情况
      else if (Array.isArray(res)) {
        courses.value = res.map((course: any) => ({
          id: String(course.id || course.courseId || ''),
          name: course.name || course.courseName || '未命名课程'
        }))
        console.log('获取课程列表成功 (直接数组):', courses.value)
      }
      else {
        console.warn('课程API返回格式不符合预期:', res)
        // 不抛出错误，因为courseApi已有错误处理并返回模拟数据
      }
    } else {
      console.warn('课程API返回无效数据:', res)
    }
  } catch (err) {
    console.error('处理课程列表时出错:', err)
    // 确保至少有一些默认课程数据
    if (courses.value.length === 0) {
      courses.value = [
        { id: '1', name: '数据结构与算法' },
        { id: '2', name: 'Web前端开发' },
        { id: '3', name: '数据库原理' },
        { id: '4', name: '操作系统' }
      ]
    }
  }
}

// 获取可参加的考试
const fetchAvailableExams = async () => {
  if (!userInfo.value.email) return

  isLoading.value = true
  error.value = null
  
  try {
    // 调用API获取可参加的考试
    const response = await examApi.getExamList() as any
    console.log('可参加的考试数据:', response)
    
    if (response && response.code === 200) {
      // 检查不同的可能的数据结构
      const examsData = response.exams || response.data || [];
      
      if (Array.isArray(examsData)) {
        // 处理返回的数据
        availableExams.value = examsData.map((exam: any) => {
          // 格式化截止日期
          const deadlineDate = new Date(exam.endTime || exam.deadline || Date.now() + 7*24*60*60*1000);
          const deadlineStr = deadlineDate.toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
          })
          
          // 返回处理后的考试数据，添加缺少的必需字段
          return {
            ...exam,
            id: exam.id,
            title: exam.title || '未命名测试',
            description: exam.description || '暂无描述',
            courseName: exam.courseName || '未知课程',
            courseId: exam.courseId || '',
            duration: exam.duration || 60,
            questionCount: exam.questionCount || 0,
            totalScore: exam.totalScore || 100,
            deadline: exam.endTime || exam.deadline,
            deadlineStr: deadlineStr,
            type: exam.category || exam.type || '未分类',
            difficulty: exam.difficulty || '中等',
            timeRemaining: calculateTimeRemaining(exam.endTime || exam.deadline),
            // 添加缺少的必需字段
            enabled: exam.enabled !== undefined ? exam.enabled : true,
            email: exam.email || userInfo.value.email,
            createdAt: exam.createdAt || new Date().toISOString()
          }
        })
      } else {
        console.error('API返回数据格式不正确: exams不是数组', response)
        throw new Error('API返回数据格式不正确')
      }
    } else {
      console.error('API返回数据格式不正确:', response)
      // 使用模拟数据
      throw new Error('API返回数据格式不正确')
    }
  } catch (err) {
    console.error('获取可参加考试出错:', err)
    error.value = '获取考试列表失败，使用模拟数据替代'
    
    // 使用模拟数据
    availableExams.value = mockExamData();
  } finally {
    isLoading.value = false
  }
}

// 获取教师创建的考试
const fetchCreatedExams = async () => {
  if (!userInfo.value.email || userInfo.value.role !== 'teacher') return
  
  isLoading.value = true
  error.value = null
  
  try {
    // 调用API获取教师创建的考试
    const response = await examApi.getExamList({ email: userInfo.value.email }) as any
    console.log('教师创建的考试数据:', response)
    
    if (response && response.code === 200 && response.exams && Array.isArray(response.exams)) {
      // 处理返回的数据
      createdExams.value = response.exams.map((exam: any) => {
        // 格式化截止日期
        const deadlineDate = new Date(exam.endTime)
        const deadlineStr = deadlineDate.toLocaleDateString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        })
        
        // 返回处理后的考试数据
        return {
          ...exam,
          id: exam.id,
          title: exam.title,
          description: exam.description || '暂无描述',
          courseName: exam.courseName || '未知课程',
          courseId: exam.courseId || '',
          duration: exam.duration || 60,
          questionCount: exam.questionCount || 0,
          totalScore: exam.totalScore || 100,
          deadline: exam.endTime,
          deadlineStr: deadlineStr,
          type: exam.category || '未分类',
          difficulty: exam.difficulty || '中等',
          enabled: exam.isPublished === true
        }
      })
    } else {
      console.error('API返回数据格式不正确:', response)
      throw new Error('API返回数据格式不正确')
    }
  } catch (err) {
    console.error('获取教师创建的考试出错:', err)
    error.value = '获取考试列表失败，请刷新页面重试'
    createdExams.value = []
  } finally {
    isLoading.value = false
  }
}

// 获取已完成的考试
const fetchCompletedExams = async () => {
  if (!userInfo.value.email) return
  
  isLoading.value = true
  error.value = null
  
  try {
    // 调用API获取已完成的考试
    const response = await examApi.getCompletedExams(userInfo.value.email) as any
    console.log('已完成的考试数据:', response)
    
    if (response && response.code === 200 && response.exams && Array.isArray(response.exams)) {
      // 处理返回的数据
      completedExams.value = response.exams.map((exam: any) => {
        return {
          examId: exam.examId,
          id: exam.id,
          title: exam.title,
          description: exam.description || '暂无描述',
          courseName: exam.courseName || '未知课程',
          courseId: exam.courseId || '',
          duration: exam.duration || 60,
          score: exam.score || 0,
          totalScore: exam.totalScore || 100,
          ranking: exam.ranking || '-',
          correctRate: (exam.correctRate || 0) * 100,
          completedTime: new Date(exam.completedTime || exam.submitTime).toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
          }),
          type: exam.category || '未分类'
        }
      })
    } else {
      console.error('API返回数据格式不正确:', response)
      throw new Error('API返回数据格式不正确')
    }
  } catch (err) {
    console.error('获取已完成考试出错:', err)
    error.value = '获取已完成考试列表失败，请刷新页面重试'
    completedExams.value = []
  } finally {
    isLoading.value = false
  }
}

// 获取考试统计
const fetchExamStats = async () => {
  if (!userInfo.value.email) return
  
  try {
    try {
      // 调用API获取考试统计数据
      const response = await examApi.getExamStats(userInfo.value.email) as ExamStatsResponse
      console.log('考试统计数据:', response)
      
      if (response) {
        // 将API返回的数据映射到组件状态
        stats.value = {
          createdCount: userInfo.value.role === 'teacher' ? createdExams.value.length : 0,
          completedCount: response.completedExams || 0,
          averageScore: response.averageScore || 0,
          highestScore: response.totalScore || 0,
          totalExams: response.totalExams || 0,
          averageCorrectRate: response.averageCorrectRate || 0,
          bestRanking: response.bestRanking || 0,
          bestExamTitle: response.bestExamTitle || ''
        }
      } else {
        throw new Error('API返回数据格式不正确')
      }
    } catch (err) {
      console.error('获取考试统计API出错:', err)
      console.log('使用本地模拟数据代替')
      
      // 使用模拟数据
      stats.value = {
        createdCount: userInfo.value.role === 'teacher' ? createdExams.value.length : 0,
        completedCount: completedExams.value.length,
        averageScore: completedExams.value.length > 0 
          ? completedExams.value.reduce((sum, exam) => sum + exam.score, 0) / completedExams.value.length 
          : 0,
        highestScore: completedExams.value.length > 0 
          ? Math.max(...completedExams.value.map(exam => exam.score)) 
          : 0,
        totalExams: availableExams.value.length + completedExams.value.length,
        averageCorrectRate: completedExams.value.length > 0 
          ? completedExams.value.reduce((sum, exam) => sum + exam.correctRate, 0) / completedExams.value.length 
          : 0,
        bestRanking: 0,
        bestExamTitle: ''
      }
    }
  } catch (err) {
    console.error('获取考试统计出错:', err)
    // 使用默认数据
    stats.value = {
      createdCount: userInfo.value.role === 'teacher' ? createdExams.value.length : 0,
      completedCount: completedExams.value.length,
      averageScore: 0,
      highestScore: 0,
      totalExams: 0,
      averageCorrectRate: 0,
      bestRanking: 0, 
      bestExamTitle: ''
    }
  }
}

// 删除考试
const deleteExam = async (examId: number) => {
  try {
    // 调用API删除考试
    await examApi.deleteExam(examId)
    ElMessage.success('删除成功')
    // 重新获取考试列表
    fetchCreatedExams()
  } catch (err) {
    console.error('删除考试出错:', err)
    ElMessage.error('删除失败，请稍后再试')
  }
}

// 开始测试
const startExam = (exam: Exam) => {
  router.push(`/exams/take/${exam.id}`)
}

// 查看测试详情
const viewExamDetail = (exam: Exam) => {
  router.push(`/exams/detail/${exam.id}`)
}

// 查看测试结果
const viewExamResult = (result: ExamResult) => {
  router.push(`/exams/result/${result.examId}`)
}

// 复习错题
const reviewExam = (result: ExamResult) => {
  router.push(`/exams/review/${result.examId}`)
}

// 表格行样式
const tableRowClassName = ({ row }: { row: Exam }) => {
  const now = new Date()
  const deadline = new Date(row.deadline)
  
  if (!row.enabled) {
    return 'disabled-row'
  } else if (deadline < now) {
    return 'expired-row'
  }
  return ''
}

// 创建新测试
const navigateToCreate = () => {
  router.push('/exams/create')
}

// 编辑测试
const editExam = (exam: Exam) => {
  router.push(`/exams/edit/${exam.id}`)
}

// 处理删除测试
const handleDeleteExam = (exam: Exam) => {
  ElMessageBox.confirm(`确定要删除测试"${exam.title}"吗？此操作不可恢复。`, '删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteExam(exam.id)
  }).catch(() => {
    // 用户取消删除
  })
}

// 计算剩余时间（返回毫秒数）
const calculateTimeRemaining = (deadline: string): number => {
  const now = new Date().getTime()
  const endTime = new Date(deadline).getTime()
  return Math.max(0, endTime - now)
}

// 计算剩余时间百分比（用于进度条）
const calculateTimePercentage = (deadline: string): number => {
  const totalDuration = 7 * 24 * 60 * 60 * 1000 // 假设考试通常提前一周发布
  const remaining = calculateTimeRemaining(deadline)
  const elapsed = totalDuration - remaining
  const percentage = Math.min(100, Math.max(0, Math.floor(elapsed / totalDuration * 100)))
  return percentage
}

// 获取剩余时间的颜色
const getTimeRemainingColor = (deadline: string): string => {
  const remaining = calculateTimeRemaining(deadline)
  const hours = remaining / (1000 * 60 * 60)
  
  if (hours < 24) {
    return '#f56c6c' // 红色：不到1天
  } else if (hours < 72) {
    return '#e6a23c' // 橙色：不到3天
  } else {
    return '#67c23a' // 绿色：3天以上
  }
}

// 格式化剩余时间显示
const formatTimeRemaining = (deadline: string): string => {
  const remaining = calculateTimeRemaining(deadline)
  
  if (remaining <= 0) {
    return '已截止'
  }
  
  const days = Math.floor(remaining / (1000 * 60 * 60 * 24))
  const hours = Math.floor((remaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((remaining % (1000 * 60 * 60)) / (1000 * 60))
  
  if (days > 0) {
    return `${days}天${hours}小时`
  } else if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  } else {
    return `${minutes}分钟`
  }
}

// 获取分数状态
const getScoreStatus = (score: number, totalScore: number): '' | 'success' | 'exception' | 'warning' => {
  const percentage = score / totalScore
  if (percentage >= 0.8) {
    return 'success'
  } else if (percentage >= 0.6) {
    return 'warning'
  } else {
    return 'exception'
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})

// 监听标签页变化，重新获取数据
watch(activeTab, () => {
  fetchData()
})
</script>

<style scoped>
.exams-view {
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 100px);
  padding: 24px;
  background-color: #f5f7fa;
}

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.page-header h1 {
      margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  }

  .exams-container {
  flex: 1;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    padding: 20px;
  display: flex;
  flex-direction: column;
}

/* 标签页样式优化 */
:deep(.el-tabs__header) {
  margin-bottom: 24px;
}

:deep(.el-tabs__nav) {
  border-radius: 4px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  height: 48px;
  line-height: 48px;
}

    .filter-bar {
      display: flex;
      flex-wrap: wrap;
  gap: 15px;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f9fafc;
  border-radius: 6px;
}

/* 卡片列表布局 */
    .exam-list {
      display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
  flex: 1;
}

      .exam-card {
        height: 100%;
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.exam-card:hover {
          transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

:deep(.el-card__header) {
  padding: 18px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #f9fafc;
        }

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
}

.card-header h3 {
            margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
          }

          .header-right {
            display: flex;
  gap: 10px;
  flex-shrink: 0;
}

:deep(.el-card__body) {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
        }

        .exam-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

          .exam-course {
            display: flex;
            align-items: center;
  gap: 8px;
            margin-bottom: 12px;
  color: #606266;
  font-weight: 500;
          }
          
          .exam-desc {
  color: #606266;
  margin-bottom: 18px;
  line-height: 1.6;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
          }

          .exam-info {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
  gap: 16px;
            margin-bottom: 20px;
  background-color: #f9fafc;
  padding: 12px;
  border-radius: 6px;
}

.exam-info span {
              display: flex;
              align-items: center;
  gap: 8px;
  color: #606266;
}

.exam-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: auto;
  padding-top: 16px;
}

/* 已完成测试卡片样式 */
          .exam-result {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
  gap: 16px;
            margin-bottom: 20px;
}

            .result-item {
  background-color: #f9fafc;
  border-radius: 6px;
  padding: 14px;
              display: flex;
              flex-direction: column;
              align-items: center;
  transition: all 0.3s ease;
}

.result-item:hover {
  background-color: #f0f2f5;
}

              .label {
                font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
              }

              .value {
  font-size: 20px;
                font-weight: 600;
  color: #606266;
}

.value.score {
  color: #409eff;
}

.score-tag {
  margin-left: 10px;
}

/* 统计页面样式 */
.stats-dashboard {
  margin-top: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 30px;
}

.stats-card {
  transition: all 0.3s ease;
  height: 100%;
}

.stats-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
}

.stats-card-header {
            display: flex;
  align-items: center;
            gap: 10px;
  font-size: 16px;
  font-weight: 500;
}

.stats-value {
  font-size: 36px;
  font-weight: 600;
  color: #409eff;
  text-align: center;
  padding: 30px 0;
}

.stats-charts {
  margin-top: 30px;
  flex: 1;
}

.chart-header {
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-placeholder {
  height: 350px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

/* 简易图表样式 */
.simple-chart {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}

.chart-bar {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.bar-label {
  width: 120px;
  text-align: right;
  padding-right: 15px;
  color: #606266;
  font-weight: 500;
}

.bar-container {
  flex: 1;
  height: 30px;
  background-color: #f5f7fa;
  border-radius: 6px;
  overflow: hidden;
  margin-right: 15px;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);
}

.bar {
  height: 100%;
  background-color: #409eff;
  border-radius: 6px;
  transition: width 0.8s ease;
}

.bar-chapter {
  background-color: #67c23a;
}

.bar-midterm {
  background-color: #e6a23c;
}

.bar-final {
  background-color: #f56c6c;
}

.bar-value {
  width: 50px;
  font-weight: bold;
  color: #303133;
}

/* 空状态美化 */
:deep(.el-empty) {
  padding: 40px 0;
}

/* 加载状态美化 */
:deep(.el-skeleton) {
  padding: 20px;
}

/* 表格样式 */
:deep(.el-table) {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper th) {
  background-color: #f9fafc;
  font-weight: 600;
}

:deep(.el-pagination) {
  margin-top: 20px;
  justify-content: flex-end;
}

/* 适配不同屏幕尺寸 */
@media (max-width: 1200px) {
  .exam-list {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .exams-view {
    padding: 16px;
  }

      .exam-list {
        grid-template-columns: 1fr;
      }

      .exam-info {
    grid-template-columns: 1fr;
  }
  
  .exam-result {
    grid-template-columns: 1fr;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
}

.header-left {
  display: flex;
  flex-direction: column;
}

.page-desc {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 表格样式增强 */
:deep(.el-table .disabled-row) {
  background-color: #f9f9f9;
  color: #c0c4cc;
}

:deep(.el-table .expired-row) {
  background-color: #fef0f0;
  color: #f56c6c;
}

:deep(.el-table .expired-row td) {
  transition: background-color 0.3s;
}

:deep(.el-table .expired-row:hover td) {
  background-color: #fde2e2 !important;
}

.empty-text {
  color: #909399;
  margin-top: 15px;
  margin-bottom: 20px;
  font-size: 14px;
}

/* 时间进度条样式 */
.time-remaining {
  margin: 16px 0;
  padding: 12px;
  background-color: #f9fafc;
  border-radius: 6px;
}

.time-remaining .label {
  display: block;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

/* 成绩进度条样式 */
.score-progress {
  margin: 16px 0;
  padding: 12px;
  background-color: #f9fafc;
  border-radius: 6px;
}

.progress-label {
  display: block;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

/* 响应式容器布局 */
.available-exams-content,
.completed-exams-content,
.created-exams-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 阴影效果增强 */
:deep(.el-card) {
  transition: all 0.3s cubic-bezier(.25,.8,.25,1);
  overflow: hidden;
}

:deep(.el-card:hover) {
  box-shadow: 0 10px 20px rgba(0,0,0,0.1), 0 6px 6px rgba(0,0,0,0.1);
}

/* 标签样式增强 */
:deep(.el-tag) {
  padding: 0 8px;
  height: 24px;
  line-height: 22px;
  border-radius: 4px;
}

/* 按钮组样式增强 */
:deep(.el-button) {
  font-weight: 500;
}

:deep(.el-button [class*="el-icon"] + span) {
  margin-left: 6px;
}

/* 页脚样式 */
.footer {
  margin-top: 40px;
  padding: 20px 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
  border-top: 1px solid #ebeef5;
}
</style> 