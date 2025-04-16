<template>
  <div class="knowledge-points-container">
    <div class="page-header">
      <h2>知识点掌握程度</h2>
      <div class="filter-area">
        <el-select v-model="filterForm.courseId" clearable placeholder="课程筛选" @change="fetchKnowledgePoints">
          <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
        </el-select>
        <el-select v-model="filterForm.sortBy" placeholder="排序方式" @change="fetchKnowledgePoints">
          <el-option label="掌握程度（从低到高）" value="mastery_asc" />
          <el-option label="掌握程度（从高到低）" value="mastery_desc" />
          <el-option label="错题数量（从多到少）" value="wrong_count_desc" />
          <el-option label="题目数量（从多到少）" value="question_count_desc" />
        </el-select>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :md="8">
        <div class="statistics-card">
          <h3>整体掌握情况</h3>
          <div class="stats-container" v-loading="loading">
            <div class="stat-item">
              <div class="stat-circle" :style="{ background: getProgressBackground(overallMastery) }">
                {{ Math.round(overallMastery) }}%
              </div>
              <div class="stat-label">整体掌握度</div>
            </div>
            <div class="stats-grid">
              <div class="mini-stat">
                <div class="mini-stat-value">{{ totalKnowledgePoints }}</div>
                <div class="mini-stat-label">知识点总数</div>
              </div>
              <div class="mini-stat">
                <div class="mini-stat-value">{{ wellMasteredCount }}</div>
                <div class="mini-stat-label">良好掌握</div>
              </div>
              <div class="mini-stat">
                <div class="mini-stat-value">{{ mediumMasteredCount }}</div>
                <div class="mini-stat-label">基本掌握</div>
              </div>
              <div class="mini-stat">
                <div class="mini-stat-value">{{ weakMasteredCount }}</div>
                <div class="mini-stat-label">需要加强</div>
              </div>
            </div>
          </div>
        </div>

        <div class="distribution-card">
          <h3>掌握程度分布</h3>
          <div class="distribution-chart" v-loading="loading">
            <div ref="chartRef" style="width: 100%; height: 300px;"></div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :md="16">
        <div class="knowledge-list-card">
          <h3>知识点列表</h3>
          <el-input 
            v-model="searchQuery" 
            placeholder="搜索知识点..." 
            prefix-icon="Search"
            clearable
            @input="handleSearch"
            class="search-input"
          />
          
          <el-empty v-if="filteredKnowledgePoints.length === 0" description="暂无知识点数据" />
          
          <el-scrollbar height="500px" v-else>
            <div class="knowledge-list">
              <div 
                v-for="point in filteredKnowledgePoints" 
                :key="point.id" 
                class="knowledge-item"
                :class="getMasteryClass(point.masteryPercentage)"
              >
                <div class="knowledge-header">
                  <h4>{{ point.name }}</h4>
                  <div class="mastery-tag" :class="getMasteryClass(point.masteryPercentage)">
                    {{ getMasteryLabel(point.masteryPercentage) }}
                  </div>
                </div>
                
                <div class="knowledge-stats">
                  <div class="progress-bar">
                    <div class="progress-fill" :style="{ width: `${point.masteryPercentage}%`, background: getProgressColor(point.masteryPercentage) }"></div>
                  </div>
                  <div class="progress-text">掌握度: {{ point.masteryPercentage }}%</div>
                </div>
                
                <div class="knowledge-detail">
                  <div class="detail-item">
                    <div class="detail-label">总题数:</div>
                    <div class="detail-value">{{ point.questionCount }}</div>
                  </div>
                  <div class="detail-item">
                    <div class="detail-label">答对:</div>
                    <div class="detail-value">{{ point.correctCount }}</div>
                  </div>
                  <div class="detail-item">
                    <div class="detail-label">答错:</div>
                    <div class="detail-value">{{ point.wrongCount }}</div>
                  </div>
                  <div class="detail-item">
                    <div class="detail-label">未答:</div>
                    <div class="detail-value">{{ point.questionCount - point.correctCount - point.wrongCount }}</div>
                  </div>
                </div>
                
                <div class="knowledge-actions">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="viewRelatedQuestions(point.id)"
                  >
                    相关题目
                  </el-button>
                  <el-button 
                    type="danger" 
                    size="small" 
                    v-if="point.wrongCount > 0"
                    @click="viewWrongQuestions(point.id)"
                  >
                    错题练习
                  </el-button>
                </div>
              </div>
            </div>
          </el-scrollbar>
          
          <div class="pagination-container" v-if="filteredKnowledgePoints.length > 0">
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
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import axios from 'axios'
import * as echarts from 'echarts'

const router = useRouter()
const chartRef = ref<HTMLElement | null>(null)
const chartInstance = ref<echarts.ECharts | null>(null)

// 接口定义
interface KnowledgePoint {
  id: string;
  name: string;
  courseId: string;
  courseName: string;
  description?: string;
  questionCount: number;
  correctCount: number;
  wrongCount: number;
  masteryPercentage: number;
}

interface Course {
  id: string;
  name: string;
}

// 数据
const knowledgePoints = ref<KnowledgePoint[]>([])
const courses = ref<Course[]>([])
const loading = ref(false)
const searchQuery = ref('')

// 分页
const currentPage = ref(1)
const pageSize = ref(20)
const totalItems = ref(0)

// 筛选
const filterForm = reactive({
  courseId: '',
  sortBy: 'mastery_asc'
})

// 计算属性
const filteredKnowledgePoints = computed(() => {
  if (!searchQuery.value) return knowledgePoints.value
  
  const query = searchQuery.value.toLowerCase()
  return knowledgePoints.value.filter(point => 
    point.name.toLowerCase().includes(query) || 
    point.description?.toLowerCase().includes(query)
  )
})

const totalKnowledgePoints = computed(() => knowledgePoints.value.length)

const wellMasteredCount = computed(() => 
  knowledgePoints.value.filter(p => p.masteryPercentage >= 80).length
)

const mediumMasteredCount = computed(() => 
  knowledgePoints.value.filter(p => p.masteryPercentage >= 60 && p.masteryPercentage < 80).length
)

const weakMasteredCount = computed(() => 
  knowledgePoints.value.filter(p => p.masteryPercentage < 60).length
)

const overallMastery = computed(() => {
  if (knowledgePoints.value.length === 0) return 0
  
  const sum = knowledgePoints.value.reduce((acc, point) => acc + point.masteryPercentage, 0)
  return sum / knowledgePoints.value.length
})

// 方法
const fetchKnowledgePoints = async () => {
  const loadingInstance = ElLoading.service({
    text: '加载知识点数据中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  
  loading.value = true
  
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      courseId: filterForm.courseId || undefined,
      sortBy: filterForm.sortBy
    }
    
    const response = await axios.get('/api/student/exams/knowledge-points', { params })
    knowledgePoints.value = response.data.items
    totalItems.value = response.data.total
    
    nextTick(() => {
      initializeChart()
    })
  } catch (error) {
    console.error('获取知识点数据失败:', error)
    ElMessage.error('获取知识点数据失败，请稍后重试')
  } finally {
    loading.value = false
    loadingInstance.close()
  }
}

const fetchCourses = async () => {
  try {
    const response = await axios.get('/api/student/courses')
    courses.value = response.data
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

const handleSearch = () => {
  // 客户端筛选，不需要重新请求
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchKnowledgePoints()
}

const getMasteryLabel = (percentage: number) => {
  if (percentage >= 80) return '良好掌握'
  if (percentage >= 60) return '基本掌握'
  return '需要加强'
}

const getMasteryClass = (percentage: number) => {
  if (percentage >= 80) return 'mastery-good'
  if (percentage >= 60) return 'mastery-medium'
  return 'mastery-weak'
}

const getProgressColor = (percentage: number) => {
  if (percentage >= 80) return '#67C23A'
  if (percentage >= 60) return '#E6A23C'
  return '#F56C6C'
}

const getProgressBackground = (percentage: number) => {
  return `conic-gradient(${getProgressColor(percentage)} ${percentage}%, #f0f0f0 0)`
}

const viewRelatedQuestions = (knowledgePointId: string) => {
  router.push(`/student/exams/knowledge-questions/${knowledgePointId}`)
}

const viewWrongQuestions = (knowledgePointId: string) => {
  router.push(`/student/exams/wrong-questions?knowledgePointId=${knowledgePointId}`)
}

const initializeChart = () => {
  if (!chartRef.value) return
  
  if (chartInstance.value) {
    chartInstance.value.dispose()
  }
  
  chartInstance.value = echarts.init(chartRef.value)
  
  // 准备图表数据
  const categories = ['良好掌握 (80%+)', '基本掌握 (60-79%)', '需要加强 (<60%)']
  const data = [wellMasteredCount.value, mediumMasteredCount.value, weakMasteredCount.value]
  const colors = ['#67C23A', '#E6A23C', '#F56C6C']
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: categories
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: categories.map((name, index) => ({
          value: data[index],
          name,
          itemStyle: {
            color: colors[index]
          }
        }))
      }
    ]
  }
  
  chartInstance.value.setOption(option)
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    if (chartInstance.value) {
      chartInstance.value.resize()
    }
  })
}

// 生命周期钩子
onMounted(() => {
  fetchKnowledgePoints()
  fetchCourses()
})
</script>

<style scoped lang="scss">
.knowledge-points-container {
  padding: 20px;
  max-width: 1400px;
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
    
    .filter-area {
      display: flex;
      gap: 10px;
    }
  }
  
  .statistics-card, .distribution-card, .knowledge-list-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    padding: 20px;
    margin-bottom: 20px;
    
    h3 {
      margin-top: 0;
      margin-bottom: 15px;
      color: #303133;
      font-size: 18px;
    }
  }
  
  .stats-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 20px;
      
      .stat-circle {
        width: 120px;
        height: 120px;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 24px;
        font-weight: bold;
        color: #303133;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      }
      
      .stat-label {
        margin-top: 10px;
        color: #606266;
      }
    }
    
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 15px;
      width: 100%;
      
      .mini-stat {
        text-align: center;
        padding: 10px;
        background: #f9f9f9;
        border-radius: 6px;
        
        .mini-stat-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
        }
        
        .mini-stat-label {
          margin-top: 5px;
          color: #909399;
          font-size: 14px;
        }
      }
    }
  }
  
  .search-input {
    margin-bottom: 15px;
  }
  
  .knowledge-list {
    .knowledge-item {
      background: #f9f9f9;
      border-radius: 8px;
      padding: 15px;
      margin-bottom: 15px;
      transition: transform 0.3s ease, box-shadow 0.3s ease;
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
      }
      
      &.mastery-good {
        border-left: 4px solid #67C23A;
      }
      
      &.mastery-medium {
        border-left: 4px solid #E6A23C;
      }
      
      &.mastery-weak {
        border-left: 4px solid #F56C6C;
      }
      
      .knowledge-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
        
        h4 {
          margin: 0;
          font-size: 16px;
          color: #303133;
        }
        
        .mastery-tag {
          padding: 4px 8px;
          border-radius: 4px;
          font-size: 12px;
          
          &.mastery-good {
            background: rgba(103, 194, 58, 0.1);
            color: #67C23A;
          }
          
          &.mastery-medium {
            background: rgba(230, 162, 60, 0.1);
            color: #E6A23C;
          }
          
          &.mastery-weak {
            background: rgba(245, 108, 108, 0.1);
            color: #F56C6C;
          }
        }
      }
      
      .knowledge-stats {
        margin-bottom: 15px;
        
        .progress-bar {
          height: 8px;
          background: #ebeef5;
          border-radius: 4px;
          overflow: hidden;
          margin-bottom: 5px;
          
          .progress-fill {
            height: 100%;
            border-radius: 4px;
          }
        }
        
        .progress-text {
          text-align: right;
          font-size: 13px;
          color: #606266;
        }
      }
      
      .knowledge-detail {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 10px;
        margin-bottom: 15px;
        
        .detail-item {
          display: flex;
          align-items: center;
          
          .detail-label {
            color: #909399;
            margin-right: 5px;
            font-size: 13px;
          }
          
          .detail-value {
            font-weight: bold;
            color: #303133;
          }
        }
      }
      
      .knowledge-actions {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}

@media (max-width: 768px) {
  .knowledge-points-container {
    padding: 10px;
    
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      
      .filter-area {
        width: 100%;
        margin-top: 10px;
        flex-direction: column;
      }
    }
    
    .stats-container {
      .stat-item {
        .stat-circle {
          width: 100px;
          height: 100px;
          font-size: 20px;
        }
      }
    }
    
    .knowledge-list {
      .knowledge-item {
        .knowledge-header {
          flex-direction: column;
          align-items: flex-start;
          
          .mastery-tag {
            margin-top: 5px;
          }
        }
        
        .knowledge-detail {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style> 