<template>
  <div class="knowledge-graph">
    <div class="graph-container" ref="graphContainer">
      <!-- 这里将使用 ECharts 或其他图形库来渲染知识图谱 -->
      <div class="graph-placeholder">
        <el-empty description="暂无知识图谱数据" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const graphContainer = ref<HTMLElement | null>(null)
let chart: echarts.ECharts | null = null

// 初始化图表
const initChart = () => {
  if (!graphContainer.value) return
  
  chart = echarts.init(graphContainer.value)
  
  // 示例数据
  const option = {
    title: {
      text: '知识图谱',
      top: 'top',
      left: 'center'
    },
    tooltip: {},
    animationDurationUpdate: 1500,
    animationEasingUpdate: 'quinticInOut' as const,
    series: [{
      type: 'graph' as const,
      layout: 'force' as const,
      data: [],
      links: [],
      roam: true,
      label: {
        show: true
      },
      force: {
        repulsion: 100
      },
      lineStyle: {
        color: '#409EFF',
        curveness: 0.3
      }
    }]
  }
  
  chart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  chart?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style lang="scss" scoped>
.knowledge-graph {
  width: 100%;
  height: 100%;
  min-height: 500px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  
  .graph-container {
    width: 100%;
    height: 100%;
  }
  
  .graph-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style> 