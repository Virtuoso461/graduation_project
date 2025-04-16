<template>
  <div class="exam-result-view">
    <div class="result-header">
      <h1>{{ examResult.title }}</h1>
      <div class="result-meta">
        <div class="meta-item">
          <el-icon><Reading /></el-icon>
          <span>{{ examResult.courseName }}</span>
        </div>
        <div class="meta-item">
          <el-icon><Calendar /></el-icon>
          <span>完成时间：{{ examResult.completedTime }}</span>
        </div>
      </div>
    </div>
    
    <div class="result-container">
      <!-- 分数卡片 -->
      <div class="score-card">
        <div :class="['score-value', getScoreClass(examResult.scoreRate)]">
          {{ examResult.score }}
        </div>
        <div class="score-label">总分 {{ examResult.totalScore }}</div>
        
        <div class="score-details">
          <div class="detail-item">
            <div class="label">得分率</div>
            <div class="value">{{ examResult.scoreRate }}%</div>
          </div>
          <div class="detail-item">
            <div class="label">班级排名</div>
            <div class="value">{{ examResult.ranking }}</div>
          </div>
          <div class="detail-item">
            <div class="label">用时</div>
            <div class="value">{{ examResult.timeTaken }}</div>
          </div>
          <div class="detail-item">
            <div class="label">正确率</div>
            <div class="value">{{ examResult.correctRate }}%</div>
          </div>
        </div>
      </div>
      
      <!-- 知识点掌握情况 -->
      <div class="knowledge-card">
        <div class="card-header">
          <div class="card-title">知识点掌握情况</div>
        </div>
        
        <div class="knowledge-chart">
          <div ref="knowledgeChartRef" class="chart-container"></div>
        </div>
        
        <div class="knowledge-list">
          <div v-for="(point, index) in examResult.knowledgePoints" :key="index" class="knowledge-item">
            <div class="knowledge-name">{{ point.name }}</div>
            <div class="knowledge-bar-container">
              <div 
                class="knowledge-bar" 
                :style="{ width: `${point.masteryRate}%`, backgroundColor: getKnowledgeColor(point.masteryRate) }">
              </div>
            </div>
            <div class="knowledge-rate" :style="{ color: getKnowledgeColor(point.masteryRate) }">
              {{ point.masteryRate }}%
            </div>
          </div>
        </div>
      </div>
      
      <!-- 错题分析 -->
      <div class="wrong-questions-card">
        <div class="card-header">
          <div class="card-title">错题分析</div>
          <div class="card-subtitle">共 {{ examResult.wrongQuestions.length }} 道错题</div>
        </div>
        
        <div class="wrong-questions-list">
          <el-collapse>
            <el-collapse-item v-for="question in examResult.wrongQuestions" :key="question.id" :name="question.id">
              <template #title>
                <div class="question-collapse-title">
                  <span class="question-number">第 {{ question.number }} 题</span>
                  <el-tag size="small" class="question-type-tag">{{ getQuestionTypeLabel(question.type) }}</el-tag>
                  <span class="question-points">得分：0 / {{ question.score }}</span>
                </div>
              </template>
              
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
                
                <!-- 答案解析 -->
                <div class="answer-analysis">
                  <div class="analysis-title">解析：</div>
                  <div class="analysis-content" v-html="question.analysis"></div>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
    </div>
    
    <div class="result-footer">
      <el-button @click="goBack" plain>返回考试列表</el-button>
      <el-button @click="reviewAllWrongQuestions" type="primary" plain>复习所有错题</el-button>
      <el-button @click="downloadReport" type="success">
        <el-icon><Download /></el-icon>
        下载考试报告
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import * as echarts from 'echarts'
import { 
  Reading, 
  Calendar, 
  Download,
} from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const examId = computed(() => route.params.id as string)
const knowledgeChartRef = ref<HTMLElement | null>(null)
let knowledgeChart: echarts.ECharts | null = null

// 定义考试结果数据结构
interface KnowledgePoint {
  name: string;
  masteryRate: number;
}

interface Question {
  id: string;
  number: number;
  type: string;
  content: string;
  score: number;
  options?: Array<{id: string, text: string}>;
  userAnswer: any;
  correctAnswer: any;
  analysis: string;
}

interface ExamResult {
  id: string;
  title: string;
  courseName: string;
  completedTime: string;
  score: number;
  totalScore: number;
  scoreRate: number;
  ranking: string;
  timeTaken: string;
  correctRate: number;
  knowledgePoints: KnowledgePoint[];
  wrongQuestions: Question[];
}

const examResult = ref<ExamResult>({
  id: '',
  title: '',
  courseName: '',
  completedTime: '',
  score: 0,
  totalScore: 100,
  scoreRate: 0,
  ranking: '',
  timeTaken: '',
  correctRate: 0,
  knowledgePoints: [],
  wrongQuestions: []
})

const loading = ref(false)

// 获取考试结果
const loadExamResult = async () => {
  const loadingInstance = ElLoading.service({
    text: '加载考试结果中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  
  loading.value = true
  
  try {
    const response = await axios.get(`/api/student/exams/${examId.value}/result`)
    examResult.value = response.data
    
    // 初始化知识点雷达图
    nextTick(() => {
      initKnowledgeChart()
    })
    
  } catch (error) {
    console.error('加载考试结果失败:', error)
    ElMessage.error('加载考试结果失败，请稍后重试')
  } finally {
    loading.value = false
    loadingInstance.close()
  }
}

// 初始化知识点雷达图
const initKnowledgeChart = () => {
  if (!knowledgeChartRef.value) return
  
  // 创建图表实例
  knowledgeChart = echarts.init(knowledgeChartRef.value)
  
  // 准备数据
  const indicators = examResult.value.knowledgePoints.map(point => ({
    name: point.name,
    max: 100
  }))
  
  const data = examResult.value.knowledgePoints.map(point => point.masteryRate)
  
  // 设置图表选项
  const option = {
    radar: {
      indicator: indicators,
      radius: '65%',
      shape: 'polygon',
      splitNumber: 4,
      name: {
        textStyle: {
          color: '#333',
          fontSize: 12
        }
      },
      splitLine: {
        lineStyle: {
          color: ['#ddd']
        }
      },
      splitArea: {
        show: true,
        areaStyle: {
          color: ['rgba(250,250,250,0.3)', 'rgba(200,200,200,0.2)']
        }
      },
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      }
    },
    series: [{
      name: '知识点掌握度',
      type: 'radar',
      data: [
        {
          value: data,
          name: '掌握度',
          areaStyle: {
            color: 'rgba(64, 158, 255, 0.6)'
          },
          lineStyle: {
            color: '#409EFF',
            width: 2
          },
          symbol: 'circle',
          symbolSize: 6,
          itemStyle: {
            color: '#409EFF'
          }
        }
      ]
    }]
  }
  
  // 设置图表
  knowledgeChart.setOption(option)
  
  // 响应窗口大小变化
  window.addEventListener('resize', () => {
    knowledgeChart?.resize()
  })
}

// 获取知识点掌握度颜色
const getKnowledgeColor = (rate: number) => {
  if (rate >= 80) return '#67c23a' // 优秀
  if (rate >= 60) return '#409eff' // 良好
  return '#f56c6c' // 需要提高
}

// 获取分数等级颜色
const getScoreClass = (score: number) => {
  if (score >= 90) return 'excellent'
  if (score >= 80) return 'good'
  if (score >= 60) return 'pass'
  return 'fail'
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

// 复习所有错题
const reviewAllWrongQuestions = () => {
  router.push(`/student/exams/wrong-questions?examId=${examId.value}`)
}

// 返回考试列表
const goBack = () => {
  router.push('/student/exams')
}

// 下载考试报告
const downloadReport = async () => {
  try {
    const response = await axios.get(`/api/student/exams/${examId.value}/report`, {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `考试报告-${examResult.value.title}.pdf`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('报告已开始下载')
  } catch (error) {
    console.error('下载报告失败:', error)
    ElMessage.error('下载报告失败，请稍后重试')
  }
}

// 生命周期
onMounted(() => {
  // 加载考试结果
  loadExamResult()
})
</script>

<style lang="scss" scoped>
.exam-result-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  
  .result-header {
    margin-bottom: 20px;
    
    h1 {
      margin: 0 0 10px 0;
      font-size: 24px;
      color: var(--el-text-color-primary);
    }
    
    .result-meta {
      display: flex;
      gap: 20px;
      
      .meta-item {
        display: flex;
        align-items: center;
        color: var(--el-text-color-secondary);
        
        .el-icon {
          margin-right: 8px;
          color: var(--el-color-primary);
        }
      }
    }
  }
  
  .result-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
    
    .score-card {
      background-color: #fff;
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .score-value {
        font-size: 72px;
        font-weight: 700;
        line-height: 1;
        margin-bottom: 10px;
        
        &.excellent {
          color: #67c23a;
        }
        
        &.good {
          color: #409eff;
        }
        
        &.pass {
          color: #e6a23c;
        }
        
        &.fail {
          color: #f56c6c;
        }
      }
      
      .score-label {
        font-size: 16px;
        color: var(--el-text-color-secondary);
        margin-bottom: 20px;
      }
      
      .score-details {
        width: 100%;
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 15px;
        
        .detail-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 10px;
          background-color: var(--el-fill-color-light);
          border-radius: 6px;
          
          .label {
            font-size: 14px;
            color: var(--el-text-color-secondary);
            margin-bottom: 5px;
          }
          
          .value {
            font-size: 18px;
            font-weight: 600;
            color: var(--el-text-color-primary);
          }
        }
      }
    }
    
    .knowledge-card {
      .knowledge-chart {
        height: 300px;
        margin-bottom: 20px;
        
        .chart-container {
          width: 100%;
          height: 100%;
        }
      }
      
      .knowledge-list {
        .knowledge-item {
          display: flex;
          align-items: center;
          margin-bottom: 15px;
          
          .knowledge-name {
            width: 20%;
            font-size: 14px;
            color: var(--el-text-color-primary);
          }
          
          .knowledge-bar-container {
            flex: 1;
            height: 12px;
            background-color: var(--el-fill-color);
            border-radius: 6px;
            margin: 0 15px;
            overflow: hidden;
            
            .knowledge-bar {
              height: 100%;
              border-radius: 6px;
            }
          }
          
          .knowledge-rate {
            width: 50px;
            text-align: right;
            font-size: 14px;
            font-weight: 600;
            color: var(--el-text-color-primary);
          }
        }
      }
    }
    
    .wrong-questions-card {
      grid-column: 1 / -1;
      
      .wrong-questions-list {
        .question-collapse-title {
          display: flex;
          align-items: center;
          
          .question-number {
            margin-right: 10px;
            font-weight: 600;
          }
          
          .question-type-tag {
            margin-right: 10px;
          }
          
          .question-points {
            color: var(--el-color-danger);
            font-weight: 600;
          }
        }
        
        .question-content {
          padding: 15px 0;
          
          .question-text {
            font-size: 16px;
            color: var(--el-text-color-primary);
            margin-bottom: 15px;
            line-height: 1.6;
          }
          
          .options-container {
            .option-item {
              display: flex;
              align-items: center;
              padding: 8px 12px;
              border-radius: 4px;
              margin-bottom: 10px;
              
              .option-mark {
                width: 24px;
                height: 24px;
                display: flex;
                align-items: center;
                justify-content: center;
                background-color: #f5f7fa;
                border-radius: 50%;
                margin-right: 10px;
                font-size: 14px;
                font-weight: 600;
                color: var(--el-text-color-secondary);
              }
              
              &.user-answer {
                background-color: rgba(245, 108, 108, 0.1);
                border: 1px dashed #f56c6c;
                
                .option-mark {
                  background-color: #f56c6c;
                  color: white;
                }
              }
              
              &.correct-answer {
                background-color: rgba(103, 194, 58, 0.1);
                border: 1px dashed #67c23a;
                
                .option-mark {
                  background-color: #67c23a;
                  color: white;
                }
              }
              
              &.user-answer.correct-answer {
                background-color: rgba(103, 194, 58, 0.1);
                border: 1px dashed #67c23a;
              }
            }
          }
          
          .compare-answers {
            margin-bottom: 15px;
            
            .compare-item {
              margin-bottom: 10px;
              
              .compare-label {
                font-weight: 600;
                margin-bottom: 5px;
                color: var(--el-text-color-primary);
              }
              
              .compare-content {
                padding: 10px;
                border-radius: 4px;
                
                &.user-answer {
                  background-color: rgba(245, 108, 108, 0.1);
                  border: 1px dashed #f56c6c;
                }
                
                &.correct-answer {
                  background-color: rgba(103, 194, 58, 0.1);
                  border: 1px dashed #67c23a;
                }
              }
            }
          }
          
          .answer-analysis {
            margin-top: 20px;
            padding: 15px;
            background-color: #fdf6ec;
            border-radius: 4px;
            
            .analysis-title {
              font-weight: 600;
              color: #e6a23c;
              margin-bottom: 10px;
            }
            
            .analysis-content {
              line-height: 1.6;
              color: var(--el-text-color-primary);
            }
          }
        }
      }
    }
  }
  
  .result-footer {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 20px;
  }
}

@media (max-width: 768px) {
  .exam-result-view {
    padding: 15px;
    
    .result-container {
      grid-template-columns: 1fr;
      
      .score-details {
        .detail-item {
          padding: 8px;
          
          .value {
            font-size: 16px;
          }
        }
      }
      
      .knowledge-list {
        .knowledge-item {
          flex-direction: column;
          align-items: flex-start;
          
          .knowledge-name {
            width: 100%;
            margin-bottom: 5px;
          }
          
          .knowledge-bar-container {
            width: 100%;
            margin: 5px 0;
          }
          
          .knowledge-rate {
            width: 100%;
            text-align: left;
          }
        }
      }
    }
  }
}
</style> 