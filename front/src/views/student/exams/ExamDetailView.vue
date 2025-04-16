<template>
  <div class="exam-detail-view">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" text>
          <el-icon><ArrowLeft /></el-icon>返回列表
        </el-button>
        <h1>{{ exam.title }}</h1>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="startExam">
          开始测试
        </el-button>
      </div>
    </div>

    <div class="exam-container">
      <el-skeleton :loading="isLoading" animated>
        <template #template>
          <div class="skeleton-container">
            <el-skeleton-item variant="h1" style="width: 50%;" />
            <el-skeleton-item variant="text" style="width: 80%;" />
            <el-skeleton-item variant="text" style="width: 60%;" />
            <el-skeleton-item variant="text" style="width: 70%;" />
            <el-skeleton-item variant="text" style="width: 85%;" />
          </div>
        </template>
        
        <template #default>
          <div class="exam-info-card">
            <div class="main-info">
              <div class="info-item">
                <div class="label">考试状态</div>
                <div class="value">
                  <el-tag 
                    :type="getStatusType(exam.status)"
                    effect="dark"
                  >
                    {{ getStatusText(exam.status) }}
                  </el-tag>
                </div>
              </div>
              <div class="info-item">
                <div class="label">所属课程</div>
                <div class="value course-name">{{ exam.courseName }}</div>
              </div>
              <div class="info-item">
                <div class="label">考试类型</div>
                <div class="value">
                  <el-tag 
                    :type="getExamTypeTag(exam.type)"
                  >
                    {{ getExamTypeLabel(exam.type) }}
                  </el-tag>
                </div>
              </div>
              <div class="info-item">
                <div class="label">测试时长</div>
                <div class="value">{{ exam.duration }} 分钟</div>
              </div>
              <div class="info-item">
                <div class="label">题目数量</div>
                <div class="value">{{ exam.questionCount }} 题</div>
              </div>
              <div class="info-item">
                <div class="label">总分值</div>
                <div class="value">{{ exam.totalScore }} 分</div>
              </div>
              <div class="info-item">
                <div class="label">开放时间</div>
                <div class="value">{{ exam.startTime }}</div>
              </div>
              <div class="info-item">
                <div class="label">截止时间</div>
                <div class="value">{{ exam.deadline }}</div>
              </div>
            </div>
            
            <el-divider />
            
            <div class="description-container">
              <h3>考试说明</h3>
              <div class="exam-description">
                {{ exam.description }}
              </div>
            </div>
            
            <el-divider />
            
            <div class="composition-container">
              <h3>试题构成</h3>
              <div class="question-composition">
                <div v-for="(item, index) in exam.composition" :key="index" class="composition-item">
                  <div class="question-type">{{ getQuestionTypeLabel(item.type) }}</div>
                  <div class="item-details">
                    <div class="question-count">{{ item.count }} 题</div>
                    <div class="question-points">{{ item.points }} 分/题</div>
                    <div class="question-total">共 {{ item.count * item.points }} 分</div>
                  </div>
                </div>
              </div>
            </div>
            
            <el-divider />
            
            <div class="rules-container">
              <h3>考试须知</h3>
              <div class="exam-rules">
                <ul>
                  <li>请在规定的时间内完成考试，超时系统将自动提交。</li>
                  <li>考试过程中请勿刷新页面或关闭浏览器，否则可能导致答案丢失。</li>
                  <li>提交考试后将立即显示成绩和错题分析。</li>
                  <li>考试期间请保持网络连接稳定。</li>
                  <li v-if="exam.isProctored">本次考试启用了监控系统，请确保摄像头正常工作。</li>
                </ul>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <div class="action-footer">
      <el-button @click="goBack">返回列表</el-button>
      <el-button type="primary" @click="startExam" :disabled="!canStartExam">
        开始测试
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const examId = computed(() => route.params.id as string)
const isLoading = ref(true)

// 模拟考试数据
interface ExamComposition {
  type: string;
  count: number;
  points: number;
}

interface ExamData {
  id: string;
  title: string;
  courseName: string;
  courseId: string;
  type: string;
  status: string;
  description: string;
  duration: number;
  questionCount: number;
  totalScore: number;
  startTime: string;
  deadline: string;
  isProctored: boolean;
  composition: ExamComposition[];
}

const exam = ref<ExamData>({
  id: '',
  title: '',
  courseName: '',
  courseId: '',
  type: '',
  status: '',
  description: '',
  duration: 0,
  questionCount: 0,
  totalScore: 0,
  startTime: '',
  deadline: '',
  isProctored: false,
  composition: []
})

// 获取考试详情
const fetchExamDetail = async () => {
  try {
    isLoading.value = true
    const response = await axios.get(`/api/student/exams/${examId.value}`)
    
    if (response.data.success) {
      exam.value = response.data.data
      // 处理考试状态
      calculateExamStatus()
    } else {
      ElMessage.error(response.data.message || '获取考试详情失败')
    }
  } catch (error) {
    console.error('获取考试详情失败', error)
    ElMessage.error('网络错误，请重试')
  } finally {
    isLoading.value = false
  }
}

// 获取考试状态样式
const getStatusType = (status: string) => {
  switch (status) {
    case 'upcoming': return 'info'
    case 'active': return 'success'
    case 'ended': return 'danger'
    default: return 'info'
  }
}

// 获取考试状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'upcoming': return '即将开始'
    case 'active': return '进行中'
    case 'ended': return '已结束'
    default: return '未知状态'
  }
}

// 获取考试类型标签
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
  switch (type) {
    case 'chapter': return '章节测试'
    case 'midterm': return '期中测试'
    case 'final': return '期末测试'
    case 'single': return '单选题'
    case 'multiple': return '多选题'
    case 'truefalse': return '判断题'
    case 'fillblank': return '填空题'
    case 'shortanswer': return '简答题'
    default: return '其他测试'
  }
}

// 是否可以开始考试
const canStartExam = computed(() => {
  return exam.value.status === 'active' || exam.value.status === 'upcoming'
})

// 开始考试
const startExam = async () => {
  try {
    const response = await axios.post(`/api/student/exams/${examId.value}/start`)
    
    if (response.data.success) {
      // 获取考试会话信息
      const examSession = response.data.data
      
      // 存储考试会话信息到本地存储或状态管理
      localStorage.setItem('currentExamSession', JSON.stringify({
        examId: examId.value,
        startTime: new Date().getTime(),
        sessionId: examSession.sessionId || null
      }))
      
      // 跳转到考试页面
      router.push(`/student/exams/take/${examId.value}`)
    } else {
      ElMessage.error(response.data.message || '开始考试失败')
    }
  } catch (error) {
    console.error('开始考试失败', error)
    ElMessage.error('网络错误，请重试')
  }
}

// 返回列表
const goBack = () => {
  router.push('/exams')
}

// 生命周期钩子
onMounted(() => {
  fetchExamDetail()
})
</script>

<style lang="scss" scoped>
.exam-detail-view {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 10px;
      
      h1 {
        margin: 0;
        font-size: 24px;
        color: var(--el-text-color-primary);
      }
    }
  }
  
  .exam-container {
    margin-bottom: 20px;
    
    .skeleton-container {
      padding: 20px;
      background-color: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      
      .el-skeleton-item {
        margin-bottom: 15px;
      }
    }
    
    .exam-info-card {
      padding: 24px;
      background-color: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      
      .main-info {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 20px;
        
        .info-item {
          .label {
            font-size: 14px;
            color: var(--el-text-color-secondary);
            margin-bottom: 5px;
          }
          
          .value {
            font-size: 16px;
            font-weight: 500;
            color: var(--el-text-color-primary);
            
            &.course-name {
              color: var(--el-color-primary);
            }
          }
        }
      }
      
      h3 {
        margin-top: 0;
        margin-bottom: 15px;
        font-size: 18px;
        color: var(--el-text-color-primary);
      }
      
      .exam-description {
        line-height: 1.6;
        color: var(--el-text-color-regular);
        text-align: justify;
      }
      
      .question-composition {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 15px;
        
        .composition-item {
          padding: 15px;
          border-radius: 6px;
          background-color: var(--el-fill-color-light);
          
          .question-type {
            font-weight: 600;
            color: var(--el-color-primary);
            margin-bottom: 10px;
          }
          
          .item-details {
            display: flex;
            justify-content: space-between;
            
            div {
              font-size: 14px;
              color: var(--el-text-color-regular);
            }
            
            .question-total {
              font-weight: 600;
              color: var(--el-color-danger);
            }
          }
        }
      }
      
      .exam-rules {
        ul {
          padding-left: 20px;
          margin: 0;
          
          li {
            margin-bottom: 10px;
            color: var(--el-text-color-regular);
            line-height: 1.5;
          }
        }
      }
    }
  }
  
  .action-footer {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 30px;
  }
}

@media (max-width: 768px) {
  .exam-detail-view {
    padding: 15px;
    
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 15px;
      
      .header-left {
        width: 100%;
        
        h1 {
          font-size: 20px;
        }
      }
      
      .header-actions {
        width: 100%;
        display: flex;
        justify-content: flex-end;
      }
    }
    
    .exam-info-card {
      padding: 15px !important;
      
      .main-info {
        grid-template-columns: 1fr !important;
        gap: 15px !important;
      }
      
      .question-composition {
        grid-template-columns: 1fr !important;
      }
    }
  }
}
</style> 