<template>
  <div class="take-exam-view">
    <div class="exam-header">
      <div class="exam-info">
        <h1>{{ exam.title }}</h1>
        <div class="exam-meta">
          <div class="meta-item">
            <el-icon><Timer /></el-icon>
            <span>时长: {{ exam.duration }}分钟</span>
          </div>
          <div class="meta-item">
            <el-icon><Document /></el-icon>
            <span>总分: {{ exam.totalScore }}分</span>
          </div>
          <div class="meta-item">
            <el-icon><Reading /></el-icon>
            <span>{{ exam.courseName }}</span>
          </div>
        </div>
      </div>
      
      <div class="exam-timer">
        <div class="timer-label">剩余时间</div>
        <div class="timer-value" :class="{ 'timer-warning': remainingMinutes < 5 }">
          {{ formatTime(remainingSeconds) }}
        </div>
      </div>
    </div>
    
    <div class="exam-container">
      <div class="question-navigation">
        <div class="nav-header">题目导航</div>
        <el-scrollbar height="calc(100vh - 250px)">
          <div class="question-buttons">
            <el-button
              v-for="(question, index) in exam.questions"
              :key="question.id"
              :type="getQuestionButtonType(index)"
              size="small"
              :class="{ 'active': currentQuestionIndex === index }"
              @click="navigateToQuestion(index)"
            >
              {{ index + 1 }}
            </el-button>
          </div>
        </el-scrollbar>
        
        <div class="nav-footer">
          <div class="legend-item">
            <div class="legend-color" style="background-color: var(--el-color-primary)"></div>
            <span>已回答</span>
          </div>
          <div class="legend-item">
            <div class="legend-color" style="background-color: var(--el-color-warning)"></div>
            <span>标记</span>
          </div>
          <div class="legend-item">
            <div class="legend-color" style="background-color: var(--el-color-info)"></div>
            <span>未回答</span>
          </div>
        </div>
      </div>
      
      <div class="question-content">
        <el-scrollbar height="calc(100vh - 180px)">
          <div class="question-area">
            <div class="question-header">
              <div class="question-number">
                第 {{ currentQuestionIndex + 1 }} 题 / 共 {{ exam.questions.length }} 题
                <el-tag 
                  size="small" 
                  :type="currentQuestion.type === 'single' ? 'success' : currentQuestion.type === 'multiple' ? 'warning' : 'info'"
                >
                  {{ getQuestionTypeLabel(currentQuestion.type) }}
                </el-tag>
              </div>
              <div class="question-actions">
                <el-button 
                  :type="isQuestionMarked(currentQuestionIndex) ? 'warning' : 'info'"
                  size="small"
                  @click="toggleMarkQuestion(currentQuestionIndex)"
                >
                  <el-icon><Flag /></el-icon>
                  {{ isQuestionMarked(currentQuestionIndex) ? '取消标记' : '标记题目' }}
                </el-button>
              </div>
            </div>
            
            <div class="question-body">
              <div class="question-text" v-html="currentQuestion.content"></div>
              
              <!-- 单选题 -->
              <div v-if="currentQuestion.type === 'single'" class="options-container">
                <el-radio-group v-model="userAnswers[currentQuestionIndex]">
                  <el-radio 
                    v-for="option in currentQuestion.options" 
                    :key="option.id" 
                    :value="option.id"
                    class="option-item"
                  >
                    {{ option.text }}
                  </el-radio>
                </el-radio-group>
              </div>
              
              <!-- 多选题 -->
              <div v-else-if="currentQuestion.type === 'multiple'" class="options-container">
                <el-checkbox-group v-model="userAnswers[currentQuestionIndex]">
                  <el-checkbox 
                    v-for="option in currentQuestion.options" 
                    :key="option.id" 
                    :label="option.id"
                    class="option-item"
                  >
                    {{ option.text }}
                  </el-checkbox>
                </el-checkbox-group>
              </div>
              
              <!-- 判断题 -->
              <div v-else-if="currentQuestion.type === 'truefalse'" class="options-container">
                <el-radio-group v-model="userAnswers[currentQuestionIndex]">
                  <el-radio value="true" class="option-item">正确</el-radio>
                  <el-radio value="false" class="option-item">错误</el-radio>
                </el-radio-group>
              </div>
              
              <!-- 填空题 -->
              <div v-else-if="currentQuestion.type === 'fillblank'" class="options-container">
                <el-input
                  v-model="userAnswers[currentQuestionIndex]"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入答案..."
                />
              </div>
              
              <!-- 简答题 -->
              <div v-else-if="currentQuestion.type === 'shortanswer'" class="options-container">
                <el-input
                  v-model="userAnswers[currentQuestionIndex]"
                  type="textarea"
                  :rows="5"
                  placeholder="请输入答案..."
                />
              </div>
            </div>
            
            <div class="question-footer">
              <el-button @click="prevQuestion" :disabled="currentQuestionIndex === 0">
                <el-icon><ArrowLeft /></el-icon>上一题
              </el-button>
              <el-button @click="nextQuestion" :disabled="currentQuestionIndex === exam.questions.length - 1">
                下一题<el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </el-scrollbar>
      </div>
    </div>
    
    <div class="exam-footer">
      <el-button type="primary" @click="openSubmitConfirm">
        交卷
      </el-button>
    </div>
    
    <!-- 提交确认对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="确认提交"
      width="30%"
      :close-on-click-modal="false"
    >
      <div class="submit-dialog-content">
        <p>您即将提交本次测试，请确认：</p>
        <ul>
          <li>
            <span class="answered">已回答：{{ answeredCount }}</span> / 总题数：{{ exam.questions.length }}
          </li>
          <li v-if="markedQuestions.length > 0">
            <span class="marked">标记题目：{{ markedQuestions.length }}</span>
          </li>
          <li v-if="unansweredCount > 0">
            <span class="unanswered">未回答：{{ unansweredCount }}</span>
          </li>
        </ul>
        <p v-if="unansweredCount > 0" class="warning-text">
          您还有未回答的题目，确定要提交吗？
        </p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="submitDialogVisible = false">继续答题</el-button>
          <el-button type="primary" @click="submitExam" :loading="isSubmitting">
            确认提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Timer, 
  Document, 
  Reading, 
  Flag,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'
// 后续可引入API
// import { examApi } from '@/utils/http/api'

const route = useRoute()
const router = useRouter()
const examId = computed(() => route.params.id as string)

// 考试信息
const exam = ref({
  id: '',
  title: '',
  courseName: '',
  duration: 0,
  totalScore: 0,
  questions: []
})

// 用户作答信息
const userAnswers = ref<(string | string[] | null)[]>([])
const markedQuestions = ref<number[]>([])
const currentQuestionIndex = ref(0)
const submitDialogVisible = ref(false)
const isSubmitting = ref(false)

// 计时相关
const totalSeconds = ref(0)
const remainingSeconds = ref(0)
const timerInterval = ref<number | null>(null)

// 获取当前题目
const currentQuestion = computed(() => {
  if (!exam.value.questions.length) return null
  return exam.value.questions[currentQuestionIndex.value]
})

// 计算剩余分钟
const remainingMinutes = computed(() => {
  return Math.floor(remainingSeconds.value / 60)
})

// 计算已答题数量
const answeredCount = computed(() => {
  return userAnswers.value.filter(answer => 
    answer !== null && 
    (Array.isArray(answer) ? answer.length > 0 : answer !== '')
  ).length
})

// 计算未答题数量
const unansweredCount = computed(() => {
  return exam.value.questions.length - answeredCount.value
})

// 加载考试数据
const loadExamData = async () => {
  try {
    // 后续使用API请求数据
    // const response = await examApi.getExamQuestions(examId.value)
    // exam.value = response
    
    // 模拟加载延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟考试数据
    exam.value = {
      id: examId.value as string,
      title: '数据结构期中测试',
      courseName: '数据结构与算法',
      duration: 90, // 分钟
      totalScore: 100,
      questions: [
        {
          id: '1',
          type: 'single',
          content: '以下哪种数据结构不是线性表？',
          score: 2,
          options: [
            { id: 'A', text: '栈' },
            { id: 'B', text: '队列' },
            { id: 'C', text: '树' },
            { id: 'D', text: '数组' }
          ]
        },
        {
          id: '2',
          type: 'multiple',
          content: '以下哪些是排序算法？（多选）',
          score: 4,
          options: [
            { id: 'A', text: '快速排序' },
            { id: 'B', text: '二分查找' },
            { id: 'C', text: '冒泡排序' },
            { id: 'D', text: '深度优先搜索' }
          ]
        },
        {
          id: '3',
          type: 'truefalse',
          content: '二叉树的前序遍历是指：根节点->左子树->右子树',
          score: 2
        },
        {
          id: '4',
          type: 'fillblank',
          content: '一个栈的入栈序列为1,2,3,4,5，则可能的出栈序列有________种。',
          score: 5
        },
        {
          id: '5',
          type: 'shortanswer',
          content: '简述快速排序的基本思想和算法步骤。',
          score: 10
        }
      ]
    }
    
    // 初始化用户作答数组
    userAnswers.value = new Array(exam.value.questions.length).fill(null)
    
    // 初始化计时器
    totalSeconds.value = exam.value.duration * 60
    remainingSeconds.value = totalSeconds.value
    startTimer()
    
  } catch (error) {
    console.error('加载考试数据失败:', error)
    ElMessage.error('加载考试数据失败，请稍后重试')
  }
}

// 启动计时器
const startTimer = () => {
  timerInterval.value = window.setInterval(() => {
    if (remainingSeconds.value > 0) {
      remainingSeconds.value--
    } else {
      // 时间到，自动交卷
      clearInterval(timerInterval.value as number)
      ElMessageBox.alert('考试时间已结束，系统将自动提交您的答案', '提示', {
        confirmButtonText: '确定',
        callback: () => {
          submitExam()
        }
      })
    }
  }, 1000)
}

// 格式化时间
const formatTime = (seconds: number) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 获取题目按钮类型
const getQuestionButtonType = (index: number) => {
  if (markedQuestions.value.includes(index)) {
    return 'warning'
  }
  
  const answer = userAnswers.value[index]
  if (answer !== null && (Array.isArray(answer) ? answer.length > 0 : answer !== '')) {
    return 'primary'
  }
  
  return 'info'
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

// 跳转到指定题目
const navigateToQuestion = (index: number) => {
  currentQuestionIndex.value = index
}

// 判断题目是否已标记
const isQuestionMarked = (index: number) => {
  return markedQuestions.value.includes(index)
}

// 标记/取消标记题目
const toggleMarkQuestion = (index: number) => {
  const markedIndex = markedQuestions.value.indexOf(index)
  if (markedIndex === -1) {
    markedQuestions.value.push(index)
  } else {
    markedQuestions.value.splice(markedIndex, 1)
  }
}

// 上一题
const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// 下一题
const nextQuestion = () => {
  if (currentQuestionIndex.value < exam.value.questions.length - 1) {
    currentQuestionIndex.value++
  }
}

// 打开提交确认对话框
const openSubmitConfirm = () => {
  submitDialogVisible.value = true
}

// 提交考试
const submitExam = async () => {
  try {
    isSubmitting.value = true
    
    // 后续使用API提交答案
    // const response = await examApi.submitExam(examId.value, {
    //   answers: userAnswers.value
    // })
    
    // 模拟提交延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('考试提交成功！')
    
    // 清除计时器
    if (timerInterval.value) {
      clearInterval(timerInterval.value)
    }
    
    // 跳转到结果页面
    router.push(`/exams/${examId.value}/result`)
    
  } catch (error) {
    console.error('提交考试失败:', error)
    ElMessage.error('提交考试失败，请稍后重试')
    
  } finally {
    isSubmitting.value = false
    submitDialogVisible.value = false
  }
}

// 离开页面确认
const handleBeforeUnload = (event: BeforeUnloadEvent) => {
  event.preventDefault()
  event.returnValue = '您的考试尚未完成，确定要离开吗？'
}

// 生命周期钩子
onMounted(() => {
  // 加载考试数据
  loadExamData()
  
  // 添加页面离开确认
  window.addEventListener('beforeunload', handleBeforeUnload)
})

onUnmounted(() => {
  // 清除计时器
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
  }
  
  // 移除页面离开确认
  window.removeEventListener('beforeunload', handleBeforeUnload)
})

// 监听路由变化，防止用户意外离开
watch(
  () => route.path,
  (newPath, oldPath) => {
    if (oldPath && oldPath.includes(`/exams/${examId.value}/take`) && !isSubmitting.value) {
      // 由于路由已经开始变化，这里使用setTimeout来阻止跳转
      setTimeout(() => {
        router.push(oldPath)
        ElMessageBox.confirm(
          '您的考试尚未完成，确定要离开吗？',
          '确认离开',
          {
            confirmButtonText: '确定离开',
            cancelButtonText: '继续考试',
            type: 'warning'
          }
        ).then(() => {
          // 用户确认离开
          router.push(newPath)
        }).catch(() => {
          // 用户取消，继续考试
        })
      }, 0)
    }
  }
)
</script>

<style lang="scss" scoped>
.take-exam-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
  
  .exam-header {
    background-color: #fff;
    padding: 15px 20px;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .exam-info {
      h1 {
        margin: 0 0 10px 0;
        font-size: 20px;
        color: #303133;
      }
      
      .exam-meta {
        display: flex;
        gap: 20px;
        
        .meta-item {
          display: flex;
          align-items: center;
          color: #606266;
          font-size: 14px;
          
          .el-icon {
            margin-right: 6px;
            color: var(--el-color-primary);
          }
        }
      }
    }
    
    .exam-timer {
      text-align: center;
      
      .timer-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 5px;
      }
      
      .timer-value {
        font-size: 24px;
        font-weight: 600;
        color: var(--el-color-primary);
        
        &.timer-warning {
          color: var(--el-color-danger);
          animation: blink 1s infinite alternate;
        }
      }
    }
  }
  
  .exam-container {
    display: flex;
    flex: 1;
    overflow: hidden;
    
    .question-navigation {
      width: 200px;
      background-color: #fff;
      border-right: 1px solid #e4e7ed;
      display: flex;
      flex-direction: column;
      
      .nav-header {
        padding: 15px;
        font-weight: 600;
        color: #303133;
        border-bottom: 1px solid #e4e7ed;
      }
      
      .question-buttons {
        padding: 15px;
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 10px;
        
        .el-button {
          height: 32px;
          width: 32px;
          padding: 0;
          border-radius: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          
          &.active {
            border: 2px solid var(--el-color-primary);
            transform: scale(1.1);
          }
        }
      }
      
      .nav-footer {
        margin-top: auto;
        padding: 15px;
        border-top: 1px solid #e4e7ed;
        
        .legend-item {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          
          .legend-color {
            width: 12px;
            height: 12px;
            border-radius: 3px;
            margin-right: 8px;
          }
          
          span {
            font-size: 12px;
            color: #606266;
          }
        }
      }
    }
    
    .question-content {
      flex: 1;
      overflow: hidden;
      
      .question-area {
        padding: 20px;
        max-width: 800px;
        margin: 0 auto;
        
        .question-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
          
          .question-number {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            display: flex;
            align-items: center;
            gap: 10px;
          }
        }
        
        .question-body {
          background-color: #fff;
          border-radius: 8px;
          padding: 20px;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
          
          .question-text {
            margin-bottom: 20px;
            font-size: 16px;
            color: #303133;
            line-height: 1.6;
          }
          
          .options-container {
            .option-item {
              display: block;
              margin-bottom: 15px;
              padding: 10px;
              border-radius: 4px;
              transition: all 0.2s;
              
              &:hover {
                background-color: #f5f7fa;
              }
            }
          }
        }
        
        .question-footer {
          display: flex;
          justify-content: space-between;
          margin-top: 20px;
        }
      }
    }
  }
  
  .exam-footer {
    background-color: #fff;
    padding: 15px 20px;
    border-top: 1px solid #e4e7ed;
    display: flex;
    justify-content: center;
  }
  
  .submit-dialog-content {
    ul {
      padding-left: 20px;
      margin: 15px 0;
      
      li {
        margin-bottom: 8px;
        
        .answered {
          color: var(--el-color-primary);
          font-weight: 600;
        }
        
        .marked {
          color: var(--el-color-warning);
          font-weight: 600;
        }
        
        .unanswered {
          color: var(--el-color-danger);
          font-weight: 600;
        }
      }
    }
    
    .warning-text {
      color: var(--el-color-danger);
      font-size: 14px;
    }
  }
}

@keyframes blink {
  from {
    opacity: 1;
  }
  to {
    opacity: 0.6;
  }
}

@media (max-width: 768px) {
  .take-exam-view {
    .exam-header {
      flex-direction: column;
      align-items: flex-start;
      
      .exam-info {
        margin-bottom: 10px;
        
        .exam-meta {
          flex-wrap: wrap;
          gap: 10px;
        }
      }
    }
    
    .exam-container {
      flex-direction: column;
      
      .question-navigation {
        width: 100%;
        height: auto;
        border-right: none;
        border-bottom: 1px solid #e4e7ed;
        
        .question-buttons {
          grid-template-columns: repeat(5, 1fr);
        }
      }
    }
  }
}
</style> 