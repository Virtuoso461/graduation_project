<template>
  <div class="analytics-view">
    <div class="page-header">
      <h1>学习数据分析</h1>
      <el-select v-model="timeRange" placeholder="选择时间范围">
        <el-option label="最近7天" value="7d" />
        <el-option label="最近30天" value="30d" />
        <el-option label="最近90天" value="90d" />
        <el-option label="全部" value="all" />
      </el-select>
    </div>

    <div class="analytics-container">
      <el-row :gutter="20">
        <el-col :span="6" v-for="stat in statistics" :key="stat.title">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" :style="{ backgroundColor: stat.color }">
                <el-icon><component :is="stat.icon" /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-title">{{ stat.title }}</div>
                <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
                  {{ Math.abs(stat.trend) }}%
                  <el-icon>
                    <component :is="stat.trend > 0 ? 'ArrowUp' : 'ArrowDown'" />
                  </el-icon>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="16">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>学习时长趋势</span>
                <el-radio-group v-model="durationChartType" size="small">
                  <el-radio-button value="day">按天</el-radio-button>
                  <el-radio-button value="week">按周</el-radio-button>
                  <el-radio-button value="month">按月</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div class="chart-container">
              <!-- 这里将集成图表库，如 ECharts -->
              <div class="chart-placeholder">学习时长趋势图表</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>课程完成率</span>
              </div>
            </template>
            <div class="chart-container">
              <!-- 这里将集成图表库，如 ECharts -->
              <div class="chart-placeholder">课程完成率饼图</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>知识点掌握度</span>
              </div>
            </template>
            <div class="chart-container">
              <!-- 这里将集成图表库，如 ECharts -->
              <div class="chart-placeholder">知识点掌握度雷达图</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>学习时段分布</span>
              </div>
            </template>
            <div class="chart-container">
              <!-- 这里将集成图表库，如 ECharts -->
              <div class="chart-placeholder">学习时段分布热力图</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="detail-card">
        <template #header>
          <div class="card-header">
            <span>详细数据</span>
            <el-button type="primary" link>
              <el-icon><Download /></el-icon>导出数据
            </el-button>
          </div>
        </template>
        <el-table :data="detailData" style="width: 100%">
          <el-table-column prop="date" label="日期" width="180" />
          <el-table-column prop="duration" label="学习时长" width="180" />
          <el-table-column prop="courses" label="完成课程数" />
          <el-table-column prop="points" label="获得积分" />
          <el-table-column prop="efficiency" label="学习效率" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import {
  Timer,
  Document,
  Star,
  Trophy,
  ArrowUp,
  ArrowDown,
  Download
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { learningDataApi } from '@/utils/http/api'

const timeRange = ref('7d')
const durationChartType = ref('day')
const isLoading = ref(false)

// 响应式数据
const statistics = ref([
  {
    title: '总学习时长',
    value: '0小时',
    icon: 'Timer',
    color: '#409EFF',
    trend: 0
  },
  {
    title: '完成课程数',
    value: '0',
    icon: 'Document',
    color: '#67C23A',
    trend: 0
  },
  {
    title: '获得积分',
    value: '0',
    icon: 'Star',
    color: '#E6A23C',
    trend: 0
  },
  {
    title: '学习排名',
    value: '第0名',
    icon: 'Trophy',
    color: '#F56C6C',
    trend: 0
  }
])

const detailData = ref([])

// 获取学习概况数据
const fetchOverview = async () => {
  try {
    isLoading.value = true
    const response = await learningDataApi.getOverview()
    
    if (response && response.code === 200 && response.data) {
      const data = response.data
      
      // 更新统计卡片数据
      statistics.value = [
        {
          title: '总学习时长',
          value: `${data.totalStudyHours || 0}小时`,
          icon: 'Timer',
          color: '#409EFF',
          trend: data.studyHoursTrend || 0
        },
        {
          title: '完成课程数',
          value: String(data.completedCourses || 0),
          icon: 'Document',
          color: '#67C23A',
          trend: data.completedCoursesTrend || 0
        },
        {
          title: '获得积分',
          value: String(data.totalPoints || 0),
          icon: 'Star',
          color: '#E6A23C',
          trend: data.pointsTrend || 0
        },
        {
          title: '学习排名',
          value: `第${data.ranking || 0}名`,
          icon: 'Trophy',
          color: '#F56C6C',
          trend: data.rankingTrend || 0
        }
      ]
      
      console.log('学习概况数据更新成功')
    } else {
      console.warn('获取学习概况数据失败:', response)
    }
  } catch (error) {
    console.error('获取学习概况出错:', error)
    ElMessage.error('获取学习概况数据失败')
  } finally {
    isLoading.value = false
  }
}

// 获取学习记录详细数据
const fetchLearningRecords = async () => {
  try {
    isLoading.value = true
    
    // 根据选择的时间范围确定开始日期
    let startDate
    const now = new Date()
    
    switch (timeRange.value) {
      case '7d':
        startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
        break
      case '30d':
        startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
        break
      case '90d':
        startDate = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000)
        break
      default:
        startDate = null
    }
    
    const params = startDate ? {
      startDate: startDate.toISOString().split('T')[0],
      endDate: now.toISOString().split('T')[0]
    } : {}
    
    const response = await learningDataApi.getLearningRecords(params)
    
    if (response && response.code === 200 && response.data) {
      // 更新详细数据表格
      detailData.value = response.data.records.map((record: any) => ({
        date: record.date,
        duration: `${record.studyHours || 0}小时`,
        courses: record.completedCourses || 0,
        points: record.points || 0,
        efficiency: `${record.efficiency || 0}%`
      }))
      
      console.log('学习记录数据更新成功')
    } else {
      console.warn('获取学习记录数据失败:', response)
    }
  } catch (error) {
    console.error('获取学习记录出错:', error)
    ElMessage.error('获取学习记录数据失败')
  } finally {
    isLoading.value = false
  }
}

// 获取学习趋势数据
const fetchTrend = async () => {
  try {
    isLoading.value = true
    
    // 转换durationChartType为API参数
    const period = durationChartType.value === 'day' ? 'week' : 
                  durationChartType.value === 'week' ? 'month' : 'year'
    
    const response = await learningDataApi.getTrend({ period })
    
    if (response && response.code === 200 && response.data) {
      // 这里解析趋势数据，更新图表
      // 实际项目中，这里会将数据传给ECharts组件
      console.log('学习趋势数据更新成功:', response.data)
      
      // 如果有ECharts图表，可以这样更新
      // studyHoursChart.value.setOption({
      //   xAxis: { data: response.data.dates },
      //   series: [{ data: response.data.studyHours }]
      // })
      
    } else {
      console.warn('获取学习趋势数据失败:', response)
    }
  } catch (error) {
    console.error('获取学习趋势出错:', error)
    ElMessage.error('获取学习趋势数据失败')
  } finally {
    isLoading.value = false
  }
}

// 获取学习统计数据
const fetchStatistics = async () => {
  try {
    isLoading.value = true
    const response = await learningDataApi.getStatistics()
    
    if (response && response.code === 200 && response.data) {
      // 这里解析统计数据，更新各种图表
      console.log('学习统计数据更新成功:', response.data)
      
      // 如果有课程完成率饼图
      // courseCompletionChart.value.setOption({
      //   series: [{
      //     data: [
      //       { value: response.data.completedCourses, name: '已完成' },
      //       { value: response.data.totalCourses - response.data.completedCourses, name: '未完成' }
      //     ]
      //   }]
      // })
      
      // 如果有知识点掌握度雷达图
      // knowledgePointsChart.value.setOption({
      //   radar: { indicator: response.data.knowledgePoints.map(item => ({ name: item.name, max: 100 })) },
      //   series: [{ data: [{ value: response.data.knowledgePoints.map(item => item.mastery) }] }]
      // })
      
    } else {
      console.warn('获取学习统计数据失败:', response)
    }
  } catch (error) {
    console.error('获取学习统计出错:', error)
    ElMessage.error('获取学习统计数据失败')
  } finally {
    isLoading.value = false
  }
}

// 加载所有数据
const loadAllData = () => {
  fetchOverview()
  fetchLearningRecords()
  fetchTrend()
  fetchStatistics()
}

// 监听时间范围变化
watch(timeRange, () => {
  fetchLearningRecords()
})

// 监听图表类型变化
watch(durationChartType, () => {
  fetchTrend()
})

// 页面加载时获取数据
onMounted(() => {
  loadAllData()
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/responsive' as responsive;

.analytics-view {
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

  .analytics-container {
    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 15px;

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;

          .el-icon {
            font-size: 24px;
            color: #fff;
          }
        }

        .stat-info {
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            line-height: 1.2;
          }

          .stat-title {
            font-size: 14px;
            color: var(--el-text-color-secondary);
            margin-top: 4px;
          }

          .stat-trend {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;
            margin-top: 4px;

            &.up {
              color: #67C23A;
            }

            &.down {
              color: #F56C6C;
            }
          }
        }
      }
    }

    .chart-row {
      margin-top: 20px;
    }

    .chart-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .chart-container {
        height: 300px;
        display: flex;
        align-items: center;
        justify-content: center;

        .chart-placeholder {
          color: var(--el-text-color-secondary);
          font-size: 14px;
        }
      }
    }

    .detail-card {
      margin-top: 20px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }
  }

  @include responsive.respond-to('md') {
    .analytics-container {
      .el-col-6 {
        width: 50%;
      }

      .el-col-16 {
        width: 100%;
      }

      .el-col-8 {
        width: 100%;
      }

      .el-col-12 {
        width: 100%;
      }
    }
  }

  @include responsive.respond-to('sm') {
    .analytics-container {
      .el-col-6 {
        width: 100%;
      }
    }
  }
}
</style> 