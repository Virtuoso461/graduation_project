<template>
  <div class="wrong-questions-view">
    <div class="header">
      <h1>错题本</h1>
      <div class="filter-bar">
        <el-select v-model="filterParams.course" placeholder="选择课程" clearable @change="fetchWrongQuestions">
          <el-option
            v-for="course in courses"
            :key="course.id"
            :label="course.name"
            :value="course.id"
          />
        </el-select>
        
        <el-select v-model="filterParams.questionType" placeholder="题目类型" clearable @change="fetchWrongQuestions">
          <el-option label="单选题" value="single" />
          <el-option label="多选题" value="multiple" />
          <el-option label="判断题" value="truefalse" />
          <el-option label="填空题" value="fillblank" />
          <el-option label="简答题" value="shortanswer" />
        </el-select>
        
        <el-select v-model="filterParams.timeRange" placeholder="时间范围" clearable @change="fetchWrongQuestions">
          <el-option label="最近一周" value="week" />
          <el-option label="最近一个月" value="month" />
          <el-option label="最近三个月" value="quarter" />
          <el-option label="全部" value="all" />
        </el-select>
      </div>
    </div>
    
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-label">错题总数</span>
        <span class="stat-value">{{ totalWrongQuestions }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已掌握</span>
        <span class="stat-value">{{ masteredCount }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">待复习</span>
        <span class="stat-value">{{ reviewCount }}</span>
      </div>
    </div>
    
    <div class="question-list-container">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <el-empty v-else-if="wrongQuestions.length === 0" description="暂无错题记录" />
      
      <div v-else class="question-list">
        <div 
          v-for="(question, index) in wrongQuestions" 
          :key="question.id"
          class="question-card"
        >
          <div class="question-header">
            <span class="question-index">{{ index + 1 }}</span>
            <el-tag size="small" class="question-type">{{ getQuestionTypeLabel(question.type) }}</el-tag>
            <div class="question-meta">
              <span class="course-name">{{ question.courseName }}</span>
              <span class="exam-time">{{ question.examTime }}</span>
            </div>
            <div class="mastery-badge" :class="getMasteryClass(question.masteryLevel)">
              {{ getMasteryLabel(question.masteryLevel) }}
            </div>
          </div>
          
          <div class="question-content">
            <div class="question-text" v-html="question.content"></div>
            
            <!-- 选择题显示 -->
            <div v-if="['single', 'multiple', 'truefalse'].includes(question.type)" class="options-container">
              <div 
                v-for="option in question.options" 
                :key="option.id"
                :class="[
                  'option-item',
                  (Array.isArray(question.userAnswer) ? question.userAnswer.includes(option.id) : question.userAnswer === option.id) ? 'user-answer' : '',
                  (Array.isArray(question.correctAnswer) ? question.correctAnswer.includes(option.id) : question.correctAnswer === option.id) ? 'correct-answer' : ''
                ]"
              >
                <div class="option-mark">{{ option.id.toUpperCase() }}</div>
                <div class="option-text" v-html="option.text"></div>
              </div>
            </div>
            
            <!-- 填空题和简答题显示 -->
            <div v-else class="compare-answers">
              <div class="compare-item">
                <div class="compare-label">你的答案：</div>
                <div class="compare-content user-answer" v-html="question.userAnswer"></div>
              </div>
              <div class="compare-item">
                <div class="compare-label">正确答案：</div>
                <div class="compare-content correct-answer" v-html="question.correctAnswer"></div>
              </div>
            </div>
          </div>
          
          <div class="question-footer">
            <div class="knowledge-points">
              <span class="label">知识点：</span>
              <div class="tags">
                <el-tag 
                  v-for="point in question.knowledgePoints" 
                  :key="point.id" 
                  size="small" 
                  effect="plain"
                >
                  {{ point.name }}
                </el-tag>
              </div>
            </div>
            
            <div class="answer-analysis" v-if="showAnalysisMap[question.id]">
              <div class="analysis-title">解析：</div>
              <div class="analysis-content" v-html="question.analysis"></div>
            </div>
            
            <div class="actions">
              <el-button 
                type="text" 
                size="small" 
                @click="toggleAnalysis(question.id)"
              >
                {{ showAnalysisMap[question.id] ? '收起解析' : '查看解析' }}
              </el-button>
              
              <el-button 
                type="text" 
                size="small" 
                :class="{ 'mastered-btn': question.masteryLevel >= 3 }"
                @click="updateMasteryLevel(question.id, question.masteryLevel < 3 ? question.masteryLevel + 1 : 1)"
              >
                {{ question.masteryLevel >= 3 ? '取消掌握' : '标记为已掌握' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <el-pagination
        v-if="totalPages > 1"
        layout="prev, pager, next"
        :total="totalWrongQuestions"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
        class="pagination"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

// 定义错题数据结构
interface KnowledgePoint {
  id: string;
  name: string;
}

interface QuestionOption {
  id: string;
  text: string;
}

interface WrongQuestion {
  id: string;
  type: string;
  content: string;
  options?: QuestionOption[];
  userAnswer: any;
  correctAnswer: any;
  analysis: string;
  courseName: string;
  examTime: string;
  masteryLevel: number;
  knowledgePoints: KnowledgePoint[];
}

interface Course {
  id: string;
  name: string;
}

// 过滤参数
const filterParams = reactive({
  course: '',
  questionType: '',
  timeRange: 'all',
  examId: route.query.examId as string || ''
})

// 页面状态
const wrongQuestions = ref<WrongQuestion[]>([])
const courses = ref<Course[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalWrongQuestions = ref(0)
const showAnalysisMap = reactive<Record<string, boolean>>({})

// 计算属性
const totalPages = computed(() => Math.ceil(totalWrongQuestions.value / pageSize.value))
const masteredCount = computed(() => wrongQuestions.value.filter(q => q.masteryLevel >= 3).length)
const reviewCount = computed(() => wrongQuestions.value.length - masteredCount.value)

// 获取错题列表
const fetchWrongQuestions = async () => {
  const loadingInstance = ElLoading.service({
    text: '加载错题中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  
  loading.value = true
  
  try {
    // 构建请求参数
    const params: Record<string, any> = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    if (filterParams.course) params.courseId = filterParams.course
    if (filterParams.questionType) params.questionType = filterParams.questionType
    if (filterParams.timeRange && filterParams.timeRange !== 'all') params.timeRange = filterParams.timeRange
    if (filterParams.examId) params.examId = filterParams.examId
    
    // 请求错题列表
    const response = await axios.get('/api/student/exams/wrong-questions', { params })
    
    wrongQuestions.value = response.data.content
    totalWrongQuestions.value = response.data.totalElements
    
    // 初始化解析显示状态
    wrongQuestions.value.forEach(question => {
      if (!showAnalysisMap[question.id]) {
        showAnalysisMap[question.id] = false
      }
    })
    
  } catch (error) {
    console.error('加载错题失败:', error)
    ElMessage.error('加载错题失败，请稍后重试')
  } finally {
    loading.value = false
    loadingInstance.close()
  }
}

// 获取课程列表
const fetchCourses = async () => {
  try {
    const response = await axios.get('/api/student/courses')
    courses.value = response.data
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }
}

// 切换解析显示状态
const toggleAnalysis = (questionId: string) => {
  showAnalysisMap[questionId] = !showAnalysisMap[questionId]
}

// 更新掌握程度
const updateMasteryLevel = async (questionId: string, level: number) => {
  try {
    await axios.put(`/api/student/exams/wrong-questions/${questionId}/mastery`, {
      masteryLevel: level
    })
    
    // 更新本地数据
    const questionIndex = wrongQuestions.value.findIndex(q => q.id === questionId)
    if (questionIndex !== -1) {
      wrongQuestions.value[questionIndex].masteryLevel = level
    }
    
    ElMessage.success(level >= 3 ? '已标记为掌握' : '已更新掌握状态')
  } catch (error) {
    console.error('更新掌握程度失败:', error)
    ElMessage.error('更新掌握程度失败，请稍后重试')
  }
}

// 分页处理
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchWrongQuestions()
}

// 获取题目类型标签
const getQuestionTypeLabel = (type: string) => {
  switch (type) {
    case 'single': return '单选题'
    case 'multiple': return '多选题'
    case 'truefalse': return '判断题'
    case 'fillblank': return '填空题'
    case 'shortanswer': return '简答题'
    default: return '未知类型'
  }
}

// 获取掌握程度标签
const getMasteryLabel = (level: number) => {
  switch (level) {
    case 1: return '未掌握'
    case 2: return '部分掌握'
    case 3: return '已掌握'
    case 4: return '熟练掌握'
    default: return '未知'
  }
}

// 获取掌握程度样式类
const getMasteryClass = (level: number) => {
  switch (level) {
    case 1: return 'mastery-low'
    case 2: return 'mastery-medium'
    case 3: return 'mastery-high'
    case 4: return 'mastery-perfect'
    default: return 'mastery-low'
  }
}

// 生命周期
onMounted(() => {
  // 加载课程列表
  fetchCourses()
  // 加载错题列表
  fetchWrongQuestions()
})
</script>

<style lang="scss" scoped>
.wrong-questions-view {
  padding: 20px;
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h1 {
      font-size: 1.8rem;
      margin: 0;
      color: #333;
    }
    
    .filter-bar {
      display: flex;
      gap: 12px;
      
      .el-select {
        width: 160px;
      }
    }
  }
  
  .stats-bar {
    display: flex;
    background-color: #f8f9fa;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 20px;
    
    .stat-item {
      flex: 1;
      text-align: center;
      border-right: 1px solid #e0e0e0;
      
      &:last-child {
        border-right: none;
      }
      
      .stat-label {
        display: block;
        color: #606266;
        font-size: 14px;
        margin-bottom: 5px;
      }
      
      .stat-value {
        display: block;
        font-size: 24px;
        font-weight: bold;
        color: #409EFF;
      }
    }
  }
  
  .question-list-container {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    
    .loading-container {
      padding: 20px 0;
    }
    
    .question-list {
      .question-card {
        border: 1px solid #ebeef5;
        border-radius: 8px;
        margin-bottom: 20px;
        padding: 15px;
        transition: box-shadow 0.3s;
        
        &:hover {
          box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
        }
        
        .question-header {
          display: flex;
          align-items: center;
          margin-bottom: 15px;
          
          .question-index {
            width: 30px;
            height: 30px;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #409EFF;
            color: white;
            border-radius: 50%;
            font-weight: bold;
            margin-right: 10px;
          }
          
          .question-type {
            margin-right: 15px;
          }
          
          .question-meta {
            flex: 1;
            display: flex;
            flex-direction: column;
            font-size: 12px;
            color: #909399;
            
            .course-name {
              font-weight: bold;
            }
          }
          
          .mastery-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
            
            &.mastery-low {
              background-color: #fef0f0;
              color: #f56c6c;
            }
            
            &.mastery-medium {
              background-color: #fdf6ec;
              color: #e6a23c;
            }
            
            &.mastery-high {
              background-color: #f0f9eb;
              color: #67c23a;
            }
            
            &.mastery-perfect {
              background-color: #ecf5ff;
              color: #409EFF;
            }
          }
        }
        
        .question-content {
          margin-bottom: 15px;
          
          .question-text {
            font-size: 15px;
            line-height: 1.6;
            margin-bottom: 15px;
          }
          
          .options-container {
            margin-top: 10px;
            
            .option-item {
              display: flex;
              align-items: center;
              padding: 8px 12px;
              border-radius: 4px;
              margin-bottom: 8px;
              background-color: #f5f7fa;
              
              &.user-answer {
                background-color: #fef0f0;
                border-left: 3px solid #f56c6c;
              }
              
              &.correct-answer {
                background-color: #f0f9eb;
                border-left: 3px solid #67c23a;
              }
              
              &.user-answer.correct-answer {
                background-color: #f0f9eb;
                border-left: 3px solid #67c23a;
              }
              
              .option-mark {
                width: 22px;
                height: 22px;
                display: flex;
                align-items: center;
                justify-content: center;
                background-color: #dcdfe6;
                color: #606266;
                border-radius: 50%;
                font-size: 12px;
                margin-right: 10px;
              }
              
              .option-text {
                flex: 1;
              }
            }
          }
          
          .compare-answers {
            margin-top: 10px;
            
            .compare-item {
              margin-bottom: 10px;
              
              .compare-label {
                font-weight: bold;
                margin-bottom: 5px;
                color: #606266;
              }
              
              .compare-content {
                padding: 10px;
                border-radius: 4px;
                line-height: 1.6;
                
                &.user-answer {
                  background-color: #fef0f0;
                  border-left: 3px solid #f56c6c;
                }
                
                &.correct-answer {
                  background-color: #f0f9eb;
                  border-left: 3px solid #67c23a;
                }
              }
            }
          }
        }
        
        .question-footer {
          .knowledge-points {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            
            .label {
              font-size: 13px;
              color: #606266;
              margin-right: 10px;
              white-space: nowrap;
            }
            
            .tags {
              display: flex;
              flex-wrap: wrap;
              gap: 5px;
            }
          }
          
          .answer-analysis {
            background-color: #f5f7fa;
            padding: 12px;
            border-radius: 4px;
            margin-bottom: 15px;
            
            .analysis-title {
              font-weight: bold;
              margin-bottom: 8px;
              color: #606266;
            }
            
            .analysis-content {
              line-height: 1.6;
              color: #303133;
            }
          }
          
          .actions {
            display: flex;
            justify-content: space-between;
            
            .mastered-btn {
              color: #67c23a;
            }
          }
        }
      }
    }
    
    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}

@media (max-width: 768px) {
  .wrong-questions-view {
    padding: 10px;
    
    .header {
      flex-direction: column;
      align-items: flex-start;
      gap: 10px;
      
      .filter-bar {
        width: 100%;
        flex-wrap: wrap;
        
        .el-select {
          width: 100%;
        }
      }
    }
    
    .stats-bar {
      flex-direction: column;
      gap: 10px;
      
      .stat-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 5px 0;
        border-right: none;
        border-bottom: 1px solid #e0e0e0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .stat-label {
          margin-bottom: 0;
        }
        
        .stat-value {
          font-size: 18px;
        }
      }
    }
  }
}
</style>