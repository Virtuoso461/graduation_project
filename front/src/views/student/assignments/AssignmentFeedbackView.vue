<template>
  <div class="assignment-feedback-view">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" text>
          <el-icon><ArrowLeft /></el-icon>返回列表
        </el-button>
        <h1>作业评分结果</h1>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="downloadFeedback">
          <el-icon><Download /></el-icon>下载评分报告
        </el-button>
      </div>
    </div>

    <div class="feedback-container">
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
          <div class="feedback-content">
            <div class="score-overview">
              <div class="score-card">
                <div class="assignment-title">{{ assignment.title }}</div>
                <div class="score-display">
                  <div class="score-number" :class="getScoreClass(assignment.score)">
                    {{ assignment.score }}
                  </div>
                  <div class="score-info">
                    <div class="total-points">总分: {{ assignment.totalPoints }}</div>
                    <div class="status">
                      <el-tag :type="getStatusType(assignment.score)" effect="dark">
                        {{ getStatusText(assignment.score) }}
                      </el-tag>
                    </div>
                  </div>
                </div>
                <div class="meta-info">
                  <div class="info-item">
                    <el-icon><Reading /></el-icon>
                    <span>{{ assignment.courseName }}</span>
                  </div>
                  <div class="info-item">
                    <el-icon><User /></el-icon>
                    <span>批阅教师: {{ assignment.teacherName }}</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Calendar /></el-icon>
                    <span>提交时间: {{ assignment.submittedTime }}</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Timer /></el-icon>
                    <span>批阅时间: {{ assignment.gradedTime }}</span>
                  </div>
                </div>
              </div>
              
              <div class="chart-container">
                <h3>评分详情</h3>
                <div class="radar-chart-container">
                  <!-- 雷达图在这里 (实际项目中可以使用echarts等实现) -->
                  <div class="radar-chart"></div>
                  
                  <!-- 简单的分值展示 -->
                  <div class="criteria-scores">
                    <div v-for="(criteria, index) in assignment.gradingCriteria" 
                      :key="index" 
                      class="criteria-item"
                    >
                      <div class="criteria-label">{{ criteria.name }}</div>
                      <el-progress 
                        :percentage="getPercentage(criteria.score, criteria.maxScore)" 
                        :color="getProgressColor(criteria.score, criteria.maxScore)"
                        :stroke-width="12"
                        :show-text="false"
                      />
                      <div class="criteria-value">
                        <span>{{ criteria.score }}/{{ criteria.maxScore }}</span>
                        <span class="criteria-percentage">{{ getPercentage(criteria.score, criteria.maxScore) }}%</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="teacher-feedback">
              <div class="feedback-header">
                <h3>教师评语</h3>
              </div>
              
              <div class="overall-comment">
                <div class="comment-content">
                  <el-icon :size="24" color="#409EFF"><ChatRound /></el-icon>
                  <p v-if="assignment.teacherComment">{{ assignment.teacherComment }}</p>
                  <p v-else class="no-comment">教师未留下总体评语</p>
                </div>
              </div>
              
              <div class="detailed-feedback" v-if="assignment.detailedFeedback && assignment.detailedFeedback.length > 0">
                <h4>具体反馈</h4>
                
                <el-collapse accordion>
                  <el-collapse-item 
                    v-for="(feedback, index) in assignment.detailedFeedback" 
                    :key="index"
                    :title="feedback.title"
                    :name="index"
                  >
                    <div class="feedback-item">
                      <div class="feedback-question">
                        <div class="question-title">{{ feedback.questionTitle }}</div>
                        <div class="question-answer-summary" v-if="feedback.answerSummary">
                          <strong>您的回答:</strong> 
                          <span>{{ feedback.answerSummary }}</span>
                        </div>
                      </div>
                      
                      <div class="feedback-details">
                        <div class="feedback-score">
                          <div class="score-label">得分:</div>
                          <div class="score-value">{{ feedback.score }}/{{ feedback.maxScore }}</div>
                        </div>
                        
                        <div class="feedback-comment">
                          <div class="comment-label">评语:</div>
                          <div class="comment-text">{{ feedback.comment || '无评语' }}</div>
                        </div>
                        
                        <div class="feedback-suggestion" v-if="feedback.suggestion">
                          <div class="suggestion-label">改进建议:</div>
                          <div class="suggestion-text">{{ feedback.suggestion }}</div>
                        </div>
                      </div>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
            
            <div class="attachments-section" v-if="assignment.attachedFiles && assignment.attachedFiles.length > 0">
              <h3>批改附件</h3>
              
              <div class="attachment-list">
                <div v-for="(file, index) in assignment.attachedFiles" :key="index" class="attachment-item">
                  <div class="file-type-icon">
                    <el-icon v-if="file.type === 'pdf'"><Document /></el-icon>
                    <el-icon v-else-if="file.type === 'doc'"><Notebook /></el-icon>
                    <el-icon v-else-if="file.type === 'image'"><Picture /></el-icon>
                    <el-icon v-else><Files /></el-icon>
                  </div>
                  
                  <div class="file-info">
                    <div class="file-name">{{ file.name }}</div>
                    <div class="file-description">{{ file.description }}</div>
                  </div>
                  
                  <div class="file-action">
                    <el-button type="primary" text @click="downloadFile(file)">
                      <el-icon><Download /></el-icon>下载
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="student-message-section" v-if="assignment.studentMessage">
              <h3>我的留言</h3>
              
              <div class="message-content">
                <p>{{ assignment.studentMessage }}</p>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <div class="action-footer">
      <el-button @click="goBack">返回列表</el-button>
      <el-button type="primary" @click="viewOriginalWork">查看提交内容</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, Download, Reading, User, Calendar, Timer,
  ChatRound, Document, Notebook, Picture, Files 
} from '@element-plus/icons-vue'
// 后续可引入API
// import { assignmentApi } from '@/utils/http/api'

interface GradingCriteria {
  name: string;
  score: number;
  maxScore: number;
}

interface DetailedFeedback {
  title: string;
  questionTitle: string;
  answerSummary?: string;
  score: number;
  maxScore: number;
  comment?: string;
  suggestion?: string;
}

interface AttachedFile {
  name: string;
  type: string;
  description: string;
  url: string;
}

interface AssignmentFeedback {
  id: string;
  title: string;
  courseName: string;
  teacherName: string;
  submittedTime: string;
  gradedTime: string;
  score: number;
  totalPoints: number;
  teacherComment?: string;
  gradingCriteria: GradingCriteria[];
  detailedFeedback: DetailedFeedback[];
  attachedFiles?: AttachedFile[];
  studentMessage?: string;
}

const route = useRoute()
const router = useRouter()
const assignmentId = computed(() => route.params.id as string)
const isLoading = ref(true)

// 模拟作业评分数据
const assignment = ref<AssignmentFeedback>({
  id: '',
  title: '',
  courseName: '',
  teacherName: '',
  submittedTime: '',
  gradedTime: '',
  score: 0,
  totalPoints: 0,
  gradingCriteria: [],
  detailedFeedback: []
})

// 加载作业评分详情
const loadFeedbackDetail = async () => {
  try {
    isLoading.value = true
    
    // 模拟加载延迟
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 后续使用API请求数据
    // const response = await assignmentApi.getAssignmentFeedback(assignmentId.value)
    // assignment.value = response
    
    // 模拟作业评分数据
    assignment.value = {
      id: assignmentId.value,
      title: '二叉树遍历算法实现',
      courseName: '数据结构与算法',
      teacherName: '王教授',
      submittedTime: '2024-04-05 14:30',
      gradedTime: '2024-04-08 09:15',
      score: 88,
      totalPoints: 100,
      teacherComment: '整体表现良好，代码规范，算法实现正确。复杂度分析较为到位，但在非递归实现的空间复杂度分析上还有提升空间。测试用例设计较为全面，覆盖了各种情况。建议加强对非递归实现算法细节的理解。',
      gradingCriteria: [
        {
          name: '算法实现正确性',
          score: 38,
          maxScore: 40
        },
        {
          name: '代码质量',
          score: 18,
          maxScore: 20
        },
        {
          name: '复杂度分析',
          score: 20,
          maxScore: 25
        },
        {
          name: '测试用例设计',
          score: 12,
          maxScore: 15
        }
      ],
      detailedFeedback: [
        {
          title: '问题1评分反馈',
          questionTitle: '请简要描述二叉树的三种遍历方式的区别和应用场景',
          answerSummary: '分析了前序、中序和后序遍历的特点和区别，举例了表达式树等应用场景...',
          score: 9,
          maxScore: 10,
          comment: '答案比较全面，分析了三种遍历的区别和各自适用场景，举例恰当。',
          suggestion: '可以再补充一些实际应用中的具体例子，如编译器中的语法树处理等。'
        },
        {
          title: '问题2评分反馈',
          questionTitle: '分析递归与非递归实现在空间复杂度上的差异',
          answerSummary: '分析了递归隐式使用系统栈，非递归显式使用数据结构的区别...',
          score: 8,
          maxScore: 10,
          comment: '分析基本正确，但对非递归实现的空间复杂度分析不够深入。',
          suggestion: '建议深入分析不同情况下（平衡树vs非平衡树）的空间复杂度差异。'
        },
        {
          title: '算法实现评分反馈',
          questionTitle: '各种遍历算法的实现',
          score: 35,
          maxScore: 40,
          comment: '递归实现完全正确，非递归实现略有瑕疵但功能正常。代码风格良好，注释充分。',
          suggestion: '非递归的后序遍历算法实现可以进一步优化，建议参考课本上的双栈实现方式。'
        },
        {
          title: '报告评分反馈',
          questionTitle: '算法分析报告',
          score: 36,
          maxScore: 40,
          comment: '报告结构清晰，分析过程详实。时间复杂度分析准确，空间复杂度分析有小瑕疵。测试用例设计全面。',
          suggestion: '建议在报告中增加算法执行过程的可视化图示，帮助理解算法执行流程。'
        }
      ],
      attachedFiles: [
        {
          name: '批改详细说明.pdf',
          type: 'pdf',
          description: '包含对代码和报告的详细批注和建议',
          url: '#'
        },
        {
          name: '代码修改建议.doc',
          type: 'doc',
          description: '针对非递归实现的具体改进建议',
          url: '#'
        },
        {
          name: '参考实现示例.java',
          type: 'doc',
          description: '优化后的后序遍历非递归实现示例',
          url: '#'
        }
      ],
      studentMessage: '老师，我在实现非递归的后序遍历时遇到了一些困难，特别是处理右子树访问状态的问题，希望能得到一些指导。'
    }
  } catch (error) {
    console.error('加载作业评分详情失败:', error)
    ElMessage.error('加载作业评分详情失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

// 获取评分样式
const getScoreClass = (score: number): string => {
  if (score >= 90) return 'excellent'
  if (score >= 80) return 'good'
  if (score >= 60) return 'average'
  return 'fail'
}

// 获取状态类型
const getStatusType = (score: number): string => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'warning'
  if (score >= 60) return 'info'
  return 'danger'
}

// 获取状态文本
const getStatusText = (score: number): string => {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 60) return '及格'
  return '不及格'
}

// 计算百分比
const getPercentage = (score: number, maxScore: number): number => {
  return Math.round((score / maxScore) * 100)
}

// 获取进度条颜色
const getProgressColor = (score: number, maxScore: number): string => {
  const percentage = getPercentage(score, maxScore)
  if (percentage >= 90) return '#67c23a'
  if (percentage >= 80) return '#e6a23c'
  if (percentage >= 60) return '#409eff'
  return '#f56c6c'
}

// 下载评分报告
const downloadFeedback = () => {
  ElMessage.success('评分报告下载中...')
  // 实际应用中这里需要调用API进行文件下载
}

// 下载文件
const downloadFile = (file: AttachedFile) => {
  ElMessage.success(`${file.name} 下载中...`)
  // 实际应用中这里需要调用API进行文件下载
  // window.open(file.url, '_blank')
}

// 查看提交内容
const viewOriginalWork = () => {
  router.push(`/assignments/${assignmentId.value}`)
}

// 返回列表
const goBack = () => {
  router.push('/assignments')
}

// 生命周期钩子
onMounted(() => {
  loadFeedbackDetail()
})
</script>

<style lang="scss" scoped>
.assignment-feedback-view {
  padding: 20px;
  
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
  
  .feedback-container {
    margin-bottom: 30px;
    
    .skeleton-container {
      padding: 20px;
      background-color: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      
      .el-skeleton-item {
        margin-bottom: 15px;
      }
    }
    
    .feedback-content {
      display: flex;
      flex-direction: column;
      gap: 25px;
      
      .score-overview {
        display: grid;
        grid-template-columns: minmax(300px, 1fr) 2fr;
        gap: 20px;
        
        .score-card {
          background-color: #fff;
          border-radius: 8px;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
          padding: 20px;
          
          .assignment-title {
            font-size: 18px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            margin-bottom: 15px;
          }
          
          .score-display {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 20px;
            
            .score-number {
              font-size: 60px;
              font-weight: 700;
              line-height: 1;
              
              &.excellent {
                color: #67c23a;
              }
              
              &.good {
                color: #e6a23c;
              }
              
              &.average {
                color: #409eff;
              }
              
              &.fail {
                color: #f56c6c;
              }
            }
            
            .score-info {
              .total-points {
                font-size: 16px;
                color: var(--el-text-color-secondary);
                margin-bottom: 8px;
              }
            }
          }
          
          .meta-info {
            .info-item {
              display: flex;
              align-items: center;
              gap: 8px;
              margin-bottom: 10px;
              color: var(--el-text-color-regular);
              
              .el-icon {
                color: var(--el-color-primary);
              }
            }
          }
        }
        
        .chart-container {
          background-color: #fff;
          border-radius: 8px;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
          padding: 20px;
          
          h3 {
            margin-top: 0;
            margin-bottom: 20px;
            font-size: 18px;
            color: var(--el-text-color-primary);
          }
          
          .radar-chart-container {
            display: flex;
            flex-direction: column;
            
            .radar-chart {
              height: 100px; // 在实际项目中应该替换为真实的雷达图
              display: flex;
              justify-content: center;
              align-items: center;
              color: var(--el-text-color-secondary);
              font-style: italic;
              margin-bottom: 20px;
              
              &:after {
                content: '此处将显示评分维度雷达图';
              }
            }
            
            .criteria-scores {
              .criteria-item {
                margin-bottom: 15px;
                
                .criteria-label {
                  display: flex;
                  justify-content: space-between;
                  margin-bottom: 5px;
                  color: var(--el-text-color-regular);
                  font-size: 14px;
                }
                
                .criteria-value {
                  display: flex;
                  justify-content: space-between;
                  margin-top: 5px;
                  color: var(--el-text-color-secondary);
                  font-size: 14px;
                  
                  .criteria-percentage {
                    font-weight: 600;
                  }
                }
              }
            }
          }
        }
      }
      
      .teacher-feedback {
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
        padding: 20px;
        
        .feedback-header {
          h3 {
            margin-top: 0;
            margin-bottom: 20px;
            font-size: 18px;
            color: var(--el-text-color-primary);
          }
        }
        
        .overall-comment {
          background-color: var(--el-color-primary-light-9);
          border-left: 4px solid var(--el-color-primary);
          padding: 15px;
          border-radius: 4px;
          margin-bottom: 20px;
          
          .comment-content {
            display: flex;
            gap: 15px;
            
            p {
              margin: 0;
              color: var(--el-text-color-regular);
              line-height: 1.6;
              flex: 1;
            }
            
            .no-comment {
              color: var(--el-text-color-secondary);
              font-style: italic;
            }
          }
        }
        
        .detailed-feedback {
          h4 {
            margin-top: 0;
            margin-bottom: 15px;
            font-size: 16px;
            color: var(--el-text-color-primary);
          }
          
          .feedback-item {
            .feedback-question {
              margin-bottom: 15px;
              
              .question-title {
                font-weight: 600;
                color: var(--el-text-color-primary);
                margin-bottom: 5px;
              }
              
              .question-answer-summary {
                color: var(--el-text-color-secondary);
                font-size: 14px;
                
                strong {
                  color: var(--el-text-color-primary);
                }
              }
            }
            
            .feedback-details {
              background-color: var(--el-fill-color-light);
              padding: 15px;
              border-radius: 4px;
              
              .feedback-score {
                display: flex;
                gap: 10px;
                margin-bottom: 10px;
                
                .score-label {
                  font-weight: 600;
                  color: var(--el-text-color-primary);
                }
                
                .score-value {
                  color: var(--el-color-danger);
                  font-weight: 600;
                }
              }
              
              .feedback-comment, .feedback-suggestion {
                margin-bottom: 10px;
                
                .comment-label, .suggestion-label {
                  font-weight: 600;
                  color: var(--el-text-color-primary);
                  margin-bottom: 5px;
                }
                
                .comment-text, .suggestion-text {
                  color: var(--el-text-color-regular);
                  line-height: 1.5;
                }
              }
            }
          }
        }
      }
      
      .attachments-section {
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
        padding: 20px;
        
        h3 {
          margin-top: 0;
          margin-bottom: 20px;
          font-size: 18px;
          color: var(--el-text-color-primary);
        }
        
        .attachment-list {
          display: flex;
          flex-direction: column;
          gap: 10px;
          
          .attachment-item {
            display: flex;
            align-items: center;
            padding: 12px;
            border-radius: 4px;
            background-color: var(--el-fill-color-light);
            
            .file-type-icon {
              font-size: 24px;
              color: var(--el-color-primary);
              margin-right: 15px;
            }
            
            .file-info {
              flex: 1;
              
              .file-name {
                font-weight: 600;
                color: var(--el-text-color-primary);
                margin-bottom: 5px;
              }
              
              .file-description {
                font-size: 14px;
                color: var(--el-text-color-secondary);
              }
            }
          }
        }
      }
      
      .student-message-section {
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
        padding: 20px;
        
        h3 {
          margin-top: 0;
          margin-bottom: 15px;
          font-size: 18px;
          color: var(--el-text-color-primary);
        }
        
        .message-content {
          background-color: var(--el-fill-color-lighter);
          padding: 15px;
          border-radius: 4px;
          font-style: italic;
          
          p {
            margin: 0;
            color: var(--el-text-color-regular);
            line-height: 1.6;
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

@media (max-width: 1000px) {
  .assignment-feedback-view {
    .score-overview {
      grid-template-columns: 1fr !important;
    }
  }
}

@media (max-width: 768px) {
  .assignment-feedback-view {
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
      }
    }
  }
}
</style> 