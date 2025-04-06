<template>
  <div class="assignments-view">
    <div class="page-header">
      <h1>在线作业</h1>
      <el-button type="primary" @click="refreshAssignments">
        <el-icon><Refresh /></el-icon>刷新列表
      </el-button>
    </div>

    <div class="assignments-container">
      <div v-if="isLoading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>
      <el-tabs v-else v-model="activeTab">
        <el-tab-pane label="待完成作业" name="pending">
          <div class="filter-bar">
            <el-select v-model="filterCourse" placeholder="按课程筛选" clearable>
              <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id"></el-option>
            </el-select>
            <el-select v-model="sortOption" placeholder="排序方式">
              <el-option label="截止日期优先" value="deadline"></el-option>
              <el-option label="最新发布优先" value="publish"></el-option>
            </el-select>
          </div>

          <div class="assignment-list">
            <el-empty v-if="filteredPendingAssignments.length === 0" description="暂无待完成作业"></el-empty>
            
            <div v-else class="assignment-cards">
              <el-card 
                v-for="assignment in filteredPendingAssignments" 
                :key="assignment.id" 
                class="assignment-card"
                :class="{'urgent': isUrgent(assignment.dueDate)}"
              >
                <div class="assignment-status">
                  <el-tag 
                    :type="getStatusType(assignment.status)" 
                    effect="plain"
                    size="large"
                    class="status-tag"
                  >
                    {{ getStatusLabel(assignment.status) }}
                  </el-tag>
                  <div class="deadline-info" :class="{'urgent-text': isUrgent(assignment.dueDate)}">
                    <el-icon><Timer /></el-icon>
                    <span>{{ getDeadlineText(assignment.dueDate) }}</span>
                  </div>
                </div>
                
                <div class="assignment-header">
                  <h3>{{ assignment.title }}</h3>
                  <div class="difficulty" v-if="assignment.difficulty">
                    <span>难度：</span>
                    <el-rate
                      v-model="assignment.difficulty"
                      disabled
                      text-color="#ff9900"
                      :score-template="String(assignment.difficulty)"
                    />
                  </div>
                </div>
                
                <div class="assignment-info">
                  <div class="course-info" v-if="assignment.courseName">
                    <el-icon><Reading /></el-icon>
                    <span>{{ assignment.courseName }}</span>
                  </div>
                  
                  <div class="teacher-info" v-if="assignment.teacherName">
                    <el-icon><User /></el-icon>
                    <span>{{ assignment.teacherName }}</span>
                  </div>
                  
                  <div class="publish-time">
                    <el-icon><Calendar /></el-icon>
                    <span>发布于：{{ formatDate(assignment.createdAt) }}</span>
                  </div>
                  
                  <div class="assignment-desc">
                    {{ assignment.description }}
                  </div>
                </div>
                
                <div class="assignment-meta">
                  <div class="meta-item" v-if="assignment.questionCount">
                    <el-icon><Document /></el-icon>
                    <span>{{ assignment.questionCount }}个问题</span>
                  </div>
                  <div class="meta-item" v-if="assignment.totalPoints">
                    <el-icon><Star /></el-icon>
                    <span>{{ assignment.totalPoints }}分</span>
                  </div>
                  <div class="meta-item" v-if="assignment.estimatedTime">
                    <el-icon><InfoFilled /></el-icon>
                    <span>{{ getEstimatedTimeText(assignment.estimatedTime) }}</span>
                  </div>
                </div>
                
                <div class="assignment-actions">
                  <el-button type="primary" @click="startAssignment(assignment)">
                    开始作业
                  </el-button>
                  <el-button @click="viewAssignmentDetail(assignment)">
                    查看详情
                  </el-button>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="已完成作业" name="completed">
          <div class="filter-bar">
            <el-select v-model="filterCompletedCourse" placeholder="按课程筛选" clearable>
              <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id"></el-option>
            </el-select>
            <el-select v-model="filterScore" placeholder="按评分筛选" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="优秀 (90-100)" value="excellent"></el-option>
              <el-option label="良好 (80-89)" value="good"></el-option>
              <el-option label="一般 (60-79)" value="average"></el-option>
              <el-option label="不及格 (<60)" value="fail"></el-option>
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

          <div class="assignment-list">
            <el-empty v-if="filteredCompletedAssignments.length === 0" description="暂无已完成作业"></el-empty>
            
            <div v-else class="assignment-cards completed">
              <el-card 
                v-for="assignment in filteredCompletedAssignments" 
                :key="assignment.id" 
                class="assignment-card"
              >
                <div class="assignment-header">
                  <h3>{{ assignment.title }}</h3>
                  <div class="score-display" :class="getScoreClass(assignment.score || 0)">
                    {{ assignment.score || 0 }} <span>分</span>
                  </div>
                </div>
                
                <div class="assignment-info">
                  <div class="course-info" v-if="assignment.courseName">
                    <el-icon><Reading /></el-icon>
                    <span>{{ assignment.courseName }}</span>
                  </div>
                  
                  <div class="completion-info" v-if="assignment.submissionDate">
                    <el-icon><SuccessFilled /></el-icon>
                    <span>提交于：{{ formatDate(assignment.submissionDate) }}</span>
                  </div>
                  
                  <div class="feedback-status">
                    <el-icon><ChatDotRound /></el-icon>
                    <span>{{ assignment.hasFeedback ? '已批改' : '待批改' }}</span>
                  </div>
                </div>
                
                <div class="grade-info" v-if="assignment.score !== undefined">
                  <div class="grade-item">
                    <span class="label">评分</span>
                    <span class="value">{{ assignment.score }}/{{ assignment.totalPoints || 100 }}</span>
                  </div>
                  <div class="grade-item" v-if="assignment.ranking">
                    <span class="label">排名</span>
                    <span class="value">{{ assignment.ranking }}</span>
                  </div>
                  <div class="grade-item" v-if="assignment.correctRate">
                    <span class="label">正确率</span>
                    <span class="value">{{ assignment.correctRate }}%</span>
                  </div>
                </div>
                
                <div v-if="assignment.teacherComment" class="teacher-comment">
                  <div class="comment-header">
                    <el-icon><ChatLineRound /></el-icon>
                    <span>教师评语</span>
                  </div>
                  <div class="comment-content">
                    {{ assignment.teacherComment }}
                  </div>
                </div>
                
                <div class="assignment-actions">
                  <el-button type="primary" @click="viewFeedback(assignment)" :disabled="!assignment.hasFeedback">
                    查看批改
                  </el-button>
                  <el-button @click="viewSubmittedWork(assignment)">
                    查看提交内容
                  </el-button>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Refresh, Clock, Timer, Document, Star, Reading, 
  User, Calendar, InfoFilled, SuccessFilled, 
  ChatDotRound, ChatLineRound
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import type { Assignment } from '@/types/assignment'
import { getCurrentUserInfo } from '@/utils/indexedDB'

const router = useRouter()
const activeTab = ref('pending')
const filterCourse = ref('')
const sortOption = ref('deadline')
const filterCompletedCourse = ref('')
const filterScore = ref('')
const dateRange = ref<[string, string] | null>(null)
const isLoading = ref(false)

// 作业列表数据
const assignments = ref<Assignment[]>([])
const pendingAssignments = ref<Assignment[]>([])
const completedAssignments = ref<Assignment[]>([])

// 模拟数据 - 课程列表
const courses = ref([
  { id: '1', name: '数据结构与算法' },
  { id: '2', name: 'Web前端开发' },
  { id: '3', name: '数据库原理' },
  { id: '4', name: '操作系统' },
  { id: '5', name: '计算机网络' }
])

// 处理接口数据的字段映射
const processAssignmentData = (data: any): Assignment => {
  return {
    id: data.id,
    title: data.title || '未命名作业',
    description: data.description || '',
    status: data.status || 'PENDING',
    dueDate: data.dueDate || data.deadline || new Date().toISOString(),
    createdAt: data.createdAt || data.createTime || data.publishTime || new Date().toISOString(),
    submissionDate: data.submissionDate || data.submitTime || null,
    courseName: data.courseName || '未知课程',
    courseId: data.courseId || '',
    teacherName: data.teacherName || data.teacherEmail || data.email || '',
    difficulty: data.difficulty || 3,
    questionCount: data.questionCount || 0,
    totalPoints: data.totalPoints || 100,
    estimatedTime: data.estimatedTime || 60,
    score: data.score || 0,
    hasFeedback: !!data.feedback || !!data.teacherComment,
    teacherComment: data.teacherComment || data.feedback || '',
    ranking: data.ranking || null,
    correctRate: data.correctRate || null
  }
}

// 获取作业列表
const fetchAssignments = async () => {
  try {
    isLoading.value = true
    
    // 从 localStorage 获取用户邮箱
    const userJson = localStorage.getItem('user')
    let email = ''
    
    if (userJson) {
      try {
        const user = JSON.parse(userJson)
        email = user.username || user.email
        console.log('从用户信息获取到邮箱:', email)
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
    
    // 如果从localStorage获取失败，尝试从cookie获取
    if (!email) {
      const cookies = document.cookie.split(';')
      for (const cookie of cookies) {
        const [name, value] = cookie.trim().split('=')
        if (name === 'userEmail') {
          email = decodeURIComponent(value)
          console.log('从cookie获取到邮箱:', email)
          break
        }
      }
    }
    
    if (!email) {
      console.error('无法获取用户邮箱')
      ElMessage.error('未获取到用户邮箱，请重新登录')
      return
    }
    
    console.log('使用邮箱获取作业列表:', email)
    
    // 调用后端API获取数据
    const response = await axios.get(`http://localhost:8080/api/assignments/stats/${email}`)
    console.log('作业列表响应:', response)
    
    // 检查响应数据结构
    if (response.data && response.data.data) {
      const statsData = response.data.data
      console.log('原始统计数据:', statsData)
      
      // 根据返回的特定数据结构处理
      if (statsData.pendingAssignments) {
        // 处理待完成作业
        pendingAssignments.value = statsData.pendingAssignments.map(item => processAssignmentData(item))
        console.log('待完成作业:', pendingAssignments.value)
      } else {
        pendingAssignments.value = []
      }
      
      // 如果有已提交和已批改的作业数据，也处理它们
      if (Array.isArray(statsData.submittedAssignments)) {
        const submittedData = statsData.submittedAssignments.map(item => processAssignmentData(item))
        if (Array.isArray(statsData.gradedAssignments)) {
          const gradedData = statsData.gradedAssignments.map(item => processAssignmentData(item))
          completedAssignments.value = [...submittedData, ...gradedData]
        } else {
          completedAssignments.value = submittedData
        }
      } else {
        // 如果没有明确的已提交和已批改区分，则可能所有非待完成的都是已完成的
        const allAssignments = Array.isArray(statsData.assignments) ? statsData.assignments : []
        completedAssignments.value = allAssignments
          .filter(a => a.status !== 'PENDING')
          .map(item => processAssignmentData(item))
      }
      
      console.log('已完成作业:', completedAssignments.value)
      
      // 更新统计数据
      if (statsData.pendingCount !== undefined) {
        console.log(`待完成: ${statsData.pendingCount}, 已提交: ${statsData.submittedCount}, 已批改: ${statsData.gradedCount}, 总计: ${statsData.totalCount}`)
      }
    } else {
      console.error('返回数据格式不正确:', response.data)
      ElMessage.error('获取作业列表失败：数据格式不正确')
    }
  } catch (error) {
    console.error('获取作业列表失败:', error)
    ElMessage.error(`获取作业列表失败: ${error.message || '未知错误'}`)
  } finally {
    isLoading.value = false
  }
}

// 刷新作业列表
const refreshAssignments = () => {
  fetchAssignments()
}

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 过滤后的待完成作业
const filteredPendingAssignments = computed(() => {
  let filtered = [...pendingAssignments.value]
  
  // 按课程筛选
  if (filterCourse.value) {
    filtered = filtered.filter(a => a.courseId === filterCourse.value)
  }
  
  // 排序
  switch (sortOption.value) {
    case 'deadline':
      filtered.sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
      break
    case 'publish':
      filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      break
  }
  
  return filtered
})

// 过滤后的已完成作业
const filteredCompletedAssignments = computed(() => {
  let filtered = [...completedAssignments.value]
  
  // 按课程筛选
  if (filterCompletedCourse.value) {
    filtered = filtered.filter(a => a.courseId === filterCompletedCourse.value)
  }
  
  // 按分数筛选
  if (filterScore.value) {
    filtered = filtered.filter(a => {
      const score = a.score || 0
      switch (filterScore.value) {
        case 'excellent': return score >= 90
        case 'good': return score >= 80 && score < 90
        case 'average': return score >= 60 && score < 80
        case 'fail': return score < 60
        default: return true
      }
    })
  }
  
  // 按日期范围筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const [start, end] = dateRange.value
    filtered = filtered.filter(a => {
      if (!a.submissionDate) return false
      const submitDate = new Date(a.submissionDate).toISOString().split('T')[0]
      return submitDate >= start && submitDate <= end
    })
  }
  
  return filtered
})

// 获取状态标签
const getStatusLabel = (status: string) => {
  switch (status) {
    case 'PENDING': return '待完成'
    case 'SUBMITTED': return '已提交'
    case 'GRADED': return '已批改'
    default: return '未知状态'
  }
}

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case 'PENDING': return 'warning'
    case 'SUBMITTED': return 'info'
    case 'GRADED': return 'success'
    default: return ''
  }
}

// 检查是否紧急
const isUrgent = (deadline: string) => {
  const now = new Date()
  const dueDate = new Date(deadline)
  const diffDays = (dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
  return diffDays <= 3
}

// 获取截止时间文本
const getDeadlineText = (deadline: string) => {
  const dueDate = new Date(deadline)
  return dueDate.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取分数样式
const getScoreClass = (score: number) => {
  if (score >= 90) return 'excellent'
  if (score >= 80) return 'good'
  if (score >= 60) return 'average'
  return 'fail'
}

// 获取预计完成时间文本
const getEstimatedTimeText = (minutes: number) => {
  if (minutes < 60) {
    return `约${minutes}分钟`
  } else {
    const hours = Math.floor(minutes / 60)
    const remainingMinutes = minutes % 60
    return remainingMinutes > 0 ? `约${hours}小时${remainingMinutes}分钟` : `约${hours}小时`
  }
}

// 开始作业
const startAssignment = (assignment: Assignment) => {
  router.push(`/assignments/${assignment.id}/submit`)
}

// 查看作业详情
const viewAssignmentDetail = (assignment: Assignment) => {
  router.push(`/assignments/${assignment.id}`)
}

// 查看批改
const viewFeedback = (assignment: Assignment) => {
  router.push(`/assignments/${assignment.id}/feedback`)
}

// 查看提交内容
const viewSubmittedWork = (assignment: Assignment) => {
  router.push(`/assignments/${assignment.id}/submission`)
}

// 页面加载时获取数据
onMounted(() => {
  fetchAssignments()
})
</script>

<style lang="scss" scoped>
.assignments-view {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h1 {
      margin: 0;
      font-size: 24px;
      color: var(--el-text-color-primary);
    }
  }

  .assignments-container {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    padding: 20px;

    .loading-state {
      padding: 40px;
      text-align: center;
    }

    .filter-bar {
      display: flex;
      gap: 15px;
      margin-bottom: 20px;
      flex-wrap: wrap;

      .el-select, .el-date-picker {
        max-width: 220px;
      }
    }

    .assignment-list {
      .assignment-cards {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
        gap: 20px;
        
        &.completed {
          grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
        }
      }

      .assignment-card {
        border-radius: 8px;
        transition: all 0.3s;
        height: 100%;
        overflow: hidden;
        
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
        }
        
        &.urgent {
          border: 1px solid var(--el-color-danger);
          box-shadow: 0 4px 12px rgba(245, 108, 108, 0.15);
        }
        
        .assignment-status {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 15px;
          
          .status-tag {
            font-size: 14px;
          }
          
          .deadline-info {
            display: flex;
            align-items: center;
            gap: 5px;
            color: var(--el-text-color-secondary);
            font-size: 14px;
            
            &.urgent-text {
              color: var(--el-color-danger);
              font-weight: 600;
            }
          }
        }
        
        .assignment-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 15px;
          
          h3 {
            margin: 0;
            font-size: 18px;
            color: var(--el-text-color-primary);
            flex: 1;
          }
          
          .difficulty {
            display: flex;
            align-items: center;
            gap: 5px;
            
            span {
              color: var(--el-text-color-secondary);
              font-size: 14px;
            }
          }
          
          .score-display {
            font-size: 24px;
            font-weight: 600;
            padding: 4px 10px;
            border-radius: 6px;
            
            span {
              font-size: 16px;
              font-weight: normal;
            }
            
            &.excellent {
              color: #f56c6c;
              background-color: rgba(245, 108, 108, 0.1);
            }
            
            &.good {
              color: #e6a23c;
              background-color: rgba(230, 162, 60, 0.1);
            }
            
            &.average {
              color: #409eff;
              background-color: rgba(64, 158, 255, 0.1);
            }
            
            &.fail {
              color: #909399;
              background-color: rgba(144, 147, 153, 0.1);
            }
          }
        }
        
        .assignment-info {
          margin-bottom: 15px;
          
          .course-info, .teacher-info, .publish-time, .completion-info, .feedback-status {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 10px;
            color: var(--el-text-color-secondary);
            font-size: 14px;
            
            .el-icon {
              color: var(--el-color-primary);
            }
          }
          
          .assignment-desc {
            margin-top: 12px;
            color: var(--el-text-color-regular);
            line-height: 1.6;
            font-size: 14px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
        }
        
        .assignment-meta {
          display: flex;
          flex-wrap: wrap;
          gap: 15px;
          margin-bottom: 20px;
          
          .meta-item {
            display: flex;
            align-items: center;
            gap: 5px;
            color: var(--el-text-color-secondary);
            font-size: 14px;
            
            .el-icon {
              color: var(--el-color-primary);
            }
          }
        }
        
        .grade-info {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 10px;
          margin-bottom: 20px;
          
          .grade-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            background-color: var(--el-fill-color-light);
            padding: 10px;
            border-radius: 6px;
            
            .label {
              font-size: 13px;
              color: var(--el-text-color-secondary);
              margin-bottom: 5px;
            }
            
            .value {
              font-size: 16px;
              font-weight: 600;
              color: var(--el-text-color-primary);
            }
          }
        }
        
        .teacher-comment {
          background-color: #f8f9fa;
          border-radius: 6px;
          padding: 12px;
          margin-bottom: 15px;
          
          .comment-header {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 8px;
            color: var(--el-color-primary);
            font-weight: 600;
            font-size: 14px;
          }
          
          .comment-content {
            font-size: 14px;
            color: var(--el-text-color-regular);
            line-height: 1.5;
          }
        }
        
        .assignment-actions {
          display: flex;
          justify-content: flex-end;
          gap: 10px;
          margin-top: 15px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .assignments-view {
    padding: 15px;
    
    .assignments-container {
      padding: 15px;
      
      .assignment-list {
        .assignment-cards, .assignment-cards.completed {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style> 