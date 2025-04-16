<template>
  <div class="exam-history-container">
    <div class="page-header">
      <h2>考试历史记录</h2>
      <el-select v-model="filterForm.courseId" clearable placeholder="课程筛选" @change="fetchExamHistory">
        <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
      </el-select>
    </div>

    <el-empty v-if="examHistory.length === 0" description="暂无考试记录" />

    <div v-else class="history-list">
      <el-card v-for="exam in examHistory" :key="exam.id" class="history-item">
        <div class="history-header">
          <h3>{{ exam.title }}</h3>
          <el-tag :type="getStatusType(exam.status)">{{ getStatusLabel(exam.status) }}</el-tag>
        </div>

        <div class="history-content">
          <div class="history-info">
            <p><el-icon><Calendar /></el-icon> 考试时间：{{ formatDate(exam.startTime) }}</p>
            <p><el-icon><Clock /></el-icon> 用时：{{ exam.timeTaken || '未完成' }}</p>
            <p><el-icon><Collection /></el-icon> 课程：{{ exam.courseName }}</p>
          </div>
          <div class="history-score" v-if="exam.status === 'completed'">
            <div class="score-circle" :class="getScoreClass(exam.score, exam.totalScore)">
              {{ exam.score }}
            </div>
            <div class="score-text">得分</div>
          </div>
        </div>

        <div class="history-footer">
          <el-button 
            type="primary" 
            @click="viewResult(exam.id)" 
            v-if="exam.status === 'completed'">
            查看结果
          </el-button>
          <el-button 
            type="success" 
            @click="reviewExam(exam.id)"
            v-if="exam.status === 'completed'">
            复习题目
          </el-button>
          <el-button v-if="exam.status === 'not_started' || exam.status === 'paused'" @click="continueExam(exam.id)">
            继续考试
          </el-button>
        </div>
      </el-card>
    </div>

    <div class="pagination-container" v-if="examHistory.length > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="totalItems"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { Calendar, Clock, Collection } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 考试状态
interface ExamHistoryItem {
  id: string;
  title: string;
  courseName: string;
  courseId: string;
  startTime: string;
  endTime: string | null;
  timeTaken: string | null;
  score: number | null;
  totalScore: number;
  status: 'not_started' | 'in_progress' | 'paused' | 'completed';
}

interface Course {
  id: string;
  name: string;
}

// 数据
const examHistory = ref<ExamHistoryItem[]>([])
const courses = ref<Course[]>([])
const loading = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)

// 筛选
const filterForm = reactive({
  courseId: '',
})

// 获取考试历史记录
const fetchExamHistory = async () => {
  const loadingInstance = ElLoading.service({
    text: '加载考试历史中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  
  loading.value = true
  
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      courseId: filterForm.courseId || undefined
    }
    
    const response = await axios.get('/api/student/exams/history', { params })
    examHistory.value = response.data.items
    totalItems.value = response.data.total
  } catch (error) {
    console.error('获取考试历史记录失败:', error)
    ElMessage.error('获取考试历史记录失败，请稍后重试')
  } finally {
    loading.value = false
    loadingInstance.close()
  }
}

// 获取课程列表（用于筛选）
const fetchCourses = async () => {
  try {
    const response = await axios.get('/api/student/courses')
    courses.value = response.data
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

// 考试状态标签
const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    'not_started': '未开始',
    'in_progress': '进行中',
    'paused': '已暂停',
    'completed': '已完成'
  }
  return statusMap[status] || status
}

// 考试状态类型（用于el-tag颜色）
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    'not_started': 'info',
    'in_progress': 'warning',
    'paused': 'warning',
    'completed': 'success'
  }
  return typeMap[status] || ''
}

// 分数样式
const getScoreClass = (score: number | null, totalScore: number) => {
  if (score === null) return ''
  
  const scoreRate = (score / totalScore) * 100
  if (scoreRate >= 90) return 'excellent'
  if (scoreRate >= 75) return 'good'
  if (scoreRate >= 60) return 'pass'
  return 'fail'
}

// 日期格式化
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchExamHistory()
}

// 查看结果
const viewResult = (examId: string) => {
  router.push(`/student/exams/result/${examId}`)
}

// 复习题目
const reviewExam = (examId: string) => {
  router.push(`/student/exams/review/${examId}`)
}

// 继续考试
const continueExam = (examId: string) => {
  router.push(`/student/exams/detail/${examId}`)
}

// 生命周期钩子
onMounted(() => {
  fetchExamHistory()
  fetchCourses()
})
</script>

<style scoped lang="scss">
.exam-history-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      color: #303133;
    }
  }
  
  .history-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
  }
  
  .history-item {
    transition: transform 0.3s ease;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
    }
    
    .history-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
      
      h3 {
        margin: 0;
        font-size: 18px;
        color: #303133;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        max-width: 250px;
      }
    }
    
    .history-content {
      display: flex;
      justify-content: space-between;
      margin-bottom: 15px;
      
      .history-info {
        flex: 1;
        
        p {
          margin: 8px 0;
          display: flex;
          align-items: center;
          color: #606266;
          
          .el-icon {
            margin-right: 8px;
          }
        }
      }
      
      .history-score {
        display: flex;
        flex-direction: column;
        align-items: center;
        
        .score-circle {
          width: 70px;
          height: 70px;
          display: flex;
          justify-content: center;
          align-items: center;
          border-radius: 50%;
          font-size: 24px;
          font-weight: bold;
          color: #fff;
          
          &.excellent {
            background-color: #67C23A;
          }
          
          &.good {
            background-color: #409EFF;
          }
          
          &.pass {
            background-color: #E6A23C;
          }
          
          &.fail {
            background-color: #F56C6C;
          }
        }
        
        .score-text {
          margin-top: 8px;
          color: #909399;
        }
      }
    }
    
    .history-footer {
      display: flex;
      justify-content: flex-end;
      gap: 10px;
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}

// 移动端适配
@media (max-width: 768px) {
  .exam-history-container {
    padding: 10px;
    
    .history-list {
      grid-template-columns: 1fr;
    }
    
    .history-item {
      .history-content {
        flex-direction: column;
        
        .history-score {
          margin-top: 15px;
          align-items: flex-start;
          flex-direction: row;
          align-items: center;
          
          .score-circle {
            width: 50px;
            height: 50px;
            font-size: 18px;
          }
          
          .score-text {
            margin-top: 0;
            margin-left: 10px;
          }
        }
      }
    }
  }
}
</style> 