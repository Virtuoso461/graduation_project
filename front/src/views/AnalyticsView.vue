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
                  <el-radio-button label="day">按天</el-radio-button>
                  <el-radio-button label="week">按周</el-radio-button>
                  <el-radio-button label="month">按月</el-radio-button>
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
import { ref } from 'vue'
import {
  Timer,
  Document,
  Star,
  Trophy,
  ArrowUp,
  ArrowDown,
  Download
} from '@element-plus/icons-vue'

const timeRange = ref('7d')
const durationChartType = ref('day')

// 模拟数据
const statistics = ref([
  {
    title: '总学习时长',
    value: '128小时',
    icon: 'Timer',
    color: '#409EFF',
    trend: 12.5
  },
  {
    title: '完成课程数',
    value: '24',
    icon: 'Document',
    color: '#67C23A',
    trend: 8.3
  },
  {
    title: '获得积分',
    value: '2560',
    icon: 'Star',
    color: '#E6A23C',
    trend: -2.1
  },
  {
    title: '学习排名',
    value: '第12名',
    icon: 'Trophy',
    color: '#F56C6C',
    trend: 5.2
  }
])

const detailData = ref([
  {
    date: '2024-03-18',
    duration: '4小时',
    courses: 2,
    points: 120,
    efficiency: '85%'
  },
  {
    date: '2024-03-17',
    duration: '3.5小时',
    courses: 1,
    points: 100,
    efficiency: '90%'
  },
  {
    date: '2024-03-16',
    duration: '5小时',
    courses: 3,
    points: 150,
    efficiency: '88%'
  }
])
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