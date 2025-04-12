<template>
  <div class="exam-result-view">
    <div class="result-header">
      <h1>{{ examResult.title }} - 测试结果</h1>
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
      <!-- 成绩卡片 -->
      <div class="score-card">
        <div class="score-value" :class="getScoreClass(examResult.score)">
          {{ examResult.score }}
        </div>
        <div class="score-label">总分</div>
        
        <div class="score-details">
          <div class="detail-item">
            <span class="label">得分率</span>
            <span class="value">{{ examResult.scoreRate }}%</span>
          </div>
          <div class="detail-item">
            <span class="label">排名</span>
            <span class="value">{{ examResult.ranking }}</span>
          </div>
          <div class="detail-item">
            <span class="label">用时</span>
            <span class="value">{{ examResult.timeTaken }}</span>
          </div>
          <div class="detail-item">
            <span class="label">正确率</span>
            <span class="value">{{ examResult.correctRate }}%</span>
          </div>
        </div>
      </div>
      
      <!-- 知识点掌握情况 -->
      <el-card class="knowledge-card">
        <template #header>
          <div class="card-header">
            <span>知识点掌握情况</span>
          </div>
        </template>
        <div class="knowledge-chart">
          <div ref="knowledgeChartRef" class="chart-container"></div>
        </div>
        
        <div class="knowledge-list">
          <div 
            v-for="(item, index) in examResult.knowledgePoints" 
            :key="index"
            class="knowledge-item"
          >
            <div class="knowledge-name">{{ item.name }}</div>
            <div class="knowledge-bar-container">
              <div 
                class="knowledge-bar" 
                :style="{ width: item.masteryRate + '%', backgroundColor: getKnowledgeColor(item.masteryRate) }"
              ></div>
            </div>
            <div class="knowledge-rate">{{ item.masteryRate }}%</div>
          </div>
        </div>
      </el-card>
      
      <!-- 错题分析 -->
      <el-card class="wrong-questions-card">
        <template #header>
          <div class="card-header">
            <span>错题分析</span>
            <el-button type="primary" link @click="reviewAllWrongQuestions">
              错题复习
            </el-button>
          </div>
        </template>
        
        <el-empty v-if="examResult.wrongQuestions.length === 0" description="恭喜你，没有错题！" />
        
        <div v-else class="wrong-questions-list">
          <el-collapse accordion>
            <el-collapse-item 
              v-for="(question, index) in examResult.wrongQuestions" 
              :key="question.id"
              :name="index"
            >
              <template #title>
                <div class="question-collapse-title">
                  <span class="question-number">第 {{ question.number }} 题</span>
                  <span class="question-type-tag">
                    <el-tag 
                      size="small" 
                      :type="question.type === 'single' ? 'success' : question.type === 'multiple' ? 'warning' : 'info'"
                    >
                      {{ getQuestionTypeLabel(question.type) }}
                    </el-tag>
                  </span>
                  <span class="question-points">{{ question.score }} 分</span>
                </div>
              </template>
              
              <div class="question-content">
                <div class="question-text" v-html="question.content"></div>
                
                <!-- 单选题 -->
                <div v-if="question.type === 'single'" class="options-container">
                  <div 
                    v-for="option in question.options" 
                    :key="option.id"
                    :class="[
                      'option-item',
                      option.id === question.userAnswer ? 'user-answer' : '',
                      option.id === question.correctAnswer ? 'correct-answer' : ''
                    ]"
                  >
                    <span class="option-mark">{{ option.id }}</span>
                    <span class="option-text">{{ option.text }}</span>
                  </div>
                </div>
                
                <!-- 多选题 -->
                <div v-else-if="question.type === 'multiple'" class="options-container">
                  <div 
                    v-for="option in question.options" 
                    :key="option.id"
                    :class="[
                      'option-item',
                      question.userAnswer && question.userAnswer.includes(option.id) ? 'user-answer' : '',
                      question.correctAnswer.includes(option.id) ? 'correct-answer' : ''
                    ]"
                  >
                    <span class="option-mark">{{ option.id }}</span>
                    <span class="option-text">{{ option.text }}</span>
                  </div>
                </div>
                
                <!-- 判断题 -->
                <div v-else-if="question.type === 'truefalse'" class="options-container">
                  <div 
                    :class="[
                      'option-item',
                      question.userAnswer === 'true' ? 'user-answer' : '',
                      question.correctAnswer === 'true' ? 'correct-answer' : ''
                    ]"
                  >
                    <span class="option-mark">A</span>
                    <span class="option-text">正确</span>
                  </div>
                  <div 
                    :class="[
                      'option-item',
                      question.userAnswer === 'false' ? 'user-answer' : '',
                      question.correctAnswer === 'false' ? 'correct-answer' : ''
                    ]"
                  >
                    <span class="option-mark">B</span>
                    <span class="option-text">错误</span>
                  </div>
                </div>
                
                <!-- 填空题或简答题 -->
                <div v-else class="compare-answers">
                  <div class="compare-item">
                    <div class="compare-label">你的答案：</div>
                    <div class="compare-content user-answer">{{ question.userAnswer || '(未作答)' }}</div>
                  </div>
                  <div class="compare-item">
                    <div class="compare-label">正确答案：</div>
                    <div class="compare-content correct-answer">{{ question.correctAnswer }}</div>
                  </div>
                </div>
                
                <!-- 解析 -->
                <div class="answer-analysis">
                  <div class="analysis-title">解析</div>
                  <div class="analysis-content" v-html="question.analysis"></div>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </el-card>
    </div>
    
    <div class="result-footer">
      <el-button @click="goBack">返回列表</el-button>
      <el-button type="primary" @click="downloadReport">
        <el-icon><Download /></el-icon>下载报告
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { 
  Reading, 
  Calendar, 
  Download,
} from '@element-plus/icons-vue'
// 后续可引入API
// import { examApi } from '@/utils/http/api'

const route = useRoute()
const router = useRouter()
const examId = computed(() => route.params.id as string)
const knowledgeChartRef = ref<HTMLElement | null>(null)
let knowledgeChart: echarts.ECharts | null = null

// 模拟考试结果数据
const examResult = ref({
  id: examId.value,
  title: '数据结构期中测试',
  courseName: '数据结构与算法',
  completedTime: '2024-04-02 15:30',
  score: 85,
  totalScore: 100,
  scoreRate: 85,
  ranking: '15/120',
  timeTaken: '75分钟',
  correctRate: 80,
  
  // 知识点掌握情况
  knowledgePoints: [
    { name: '线性表', masteryRate: 90 },
    { name: '栈与队列', masteryRate: 85 },
    { name: '树', masteryRate: 75 },
    { name: '图', masteryRate: 60 },
    { name: '排序算法', masteryRate: 95 }
  ],
  
  // 错题
  wrongQuestions: [
    {
      id: '2',
      number: 2,
      type: 'multiple',
      content: '以下哪些是排序算法？（多选）',
      score: 4,
      options: [
        { id: 'A', text: '快速排序' },
        { id: 'B', text: '二分查找' },
        { id: 'C', text: '冒泡排序' },
        { id: 'D', text: '深度优先搜索' }
      ],
      userAnswer: ['A', 'B', 'C'],
      correctAnswer: ['A', 'C'],
      analysis: '二分查找是查找算法而非排序算法，深度优先搜索是图的遍历算法。正确答案应该是快速排序和冒泡排序。'
    },
    {
      id: '5',
      number: 5,
      type: 'shortanswer',
      content: '简述快速排序的基本思想和算法步骤。',
      score: 10,
      userAnswer: '快速排序是一种排序算法，它通过选择一个基准元素，把数组分成两部分，小于基准的放左边，大于基准的放右边。',
      correctAnswer: '快速排序是一种分治策略的排序算法。步骤：1.选择一个基准元素（pivot）；2.将小于基准的元素放在左边，大于基准的元素放在右边；3.递归地对左右两个子序列进行快速排序；4.最终将所有子序列合并。快速排序的平均时间复杂度为O(nlogn)。',
      analysis: '答案缺少对分治策略的描述，没有提到递归处理子序列，以及算法的时间复杂度分析。'
    }
  ]
})

// 获取考试结果
const loadExamResult = async () => {
  try {
    // 后续使用API请求数据
    // const response = await examApi.getExamResult(examId.value)
    // examResult.value = response
    
    // 模拟加载延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 初始化知识点雷达图
    initKnowledgeChart()
    
  } catch (error) {
    console.error('加载考试结果失败:', error)
    ElMessage.error('加载考试结果失败，请稍后重试')
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
  router.push(`/exams/${examId.value}/review`)
}

// 返回考试列表
const goBack = () => {
  router.push('/exams')
}

// 下载考试报告
const downloadReport = () => {
  ElMessage.success('报告已开始下载')
  // 实际环境中可以调用后端API进行下载
  // window.open(`/api/exams/${examId.value}/report/download`, '_blank')
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