<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="greeting">
        <h1>
          <span class="highlight">你好，{{ userStore.userInfo?.name || '同学' }}！</span>
          {{ timeGreeting }}
        </h1>
        <p class="sub-greeting">{{ motivationalQuote }}</p>
      </div>
      <div class="quick-stats">
        <div class="stat-card">
          <div class="stat-icon completed-icon">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-content">
            <h3>今日任务</h3>
            <div class="stat-value">{{ stats.completedTasks }}/{{ stats.totalTasks }}</div>
            <el-progress 
              :percentage="taskCompletionRate" 
              :color="progressColors.tasks"
              :show-text="false"
              :stroke-width="6"
            />
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon study-icon">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-content">
            <h3>学习时长</h3>
            <div class="stat-value">{{ stats.studyTime }}小时</div>
            <el-progress 
              :percentage="studyTimePercentage" 
              :color="progressColors.study"
              :show-text="false"
              :stroke-width="6"
            />
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon streak-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <h3>连续学习</h3>
            <div class="stat-value">{{ stats.streak }}天</div>
            <el-progress 
              :percentage="streakPercentage" 
              :color="progressColors.streak"
              :show-text="false"
              :stroke-width="6"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 卡片内容区域 -->
    <div class="cards-wrapper">
      <el-row :gutter="8">
        <!-- 学习进度 -->
        <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="16">
          <div class="dashboard-card learning-progress">
            <div class="card-header">
              <h2>学习进度</h2>
              <el-button type="primary" text>查看全部</el-button>
            </div>
            <div class="courses-grid">
              <div 
                v-for="course in currentCourses" 
                :key="course.id" 
                class="course-card"
                @click="navigateToCourse(course.id)"
              >
                <div class="course-header" :style="{ backgroundColor: course.color }">
                  <el-icon><component :is="course.icon" /></el-icon>
                </div>
                <div class="course-content">
                  <h3 class="course-title">{{ course.title }}</h3>
                  <div class="course-info">
                    <span class="course-instructor">{{ course.instructor }}</span>
                    <span class="course-progress">{{ course.progress }}%</span>
                  </div>
                  <el-progress 
                    :percentage="course.progress" 
                    :color="course.color"
                    :show-text="false"
                    :stroke-width="4"
                  />
                  <div class="course-footer">
                    <el-tag size="small" :style="{ backgroundColor: course.color + '20', color: course.color, border: 'none' }">
                      {{ course.category }}
                    </el-tag>
                    <el-button link type="primary" text size="small">继续学习</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 侧边栏：待办事项和个人学习分析 -->
        <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
          <div class="dashboard-card todo-list">
            <div class="card-header">
              <h2>待办事项</h2>
              <el-button type="primary" text @click="addTodo">添加</el-button>
            </div>
            <el-scrollbar :height="isMobile ? '250px' : '300px'">
              <transition-group name="todo-list" tag="ul" class="todo-items">
                <li v-for="todo in todoList" :key="todo.id" class="todo-item">
                  <el-checkbox 
                    v-model="todo.completed" 
                    @change="toggleTodo(todo.id)"
                    :disabled="todo.completed"
                  >
                    <span :class="{ 'completed': todo.completed }">{{ todo.title }}</span>
                  </el-checkbox>
                  <div class="todo-item-right">
                    <el-tag size="small" :type="todo.priority">{{ priorityText[todo.priority] }}</el-tag>
                    <el-dropdown trigger="click" @command="handleTodoCommand($event, todo.id)">
                      <el-button type="primary" text size="small">
                        <el-icon><More /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item command="edit">编辑</el-dropdown-item>
                          <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </li>
              </transition-group>
            </el-scrollbar>
            <div v-if="todoList.length === 0" class="empty-state">
              <el-icon><Document /></el-icon>
              <p>暂无待办事项</p>
            </div>
          </div>

          <div class="dashboard-card study-analytics">
            <div class="card-header">
              <h2>学习分析</h2>
              <el-button type="primary" text>全部数据</el-button>
            </div>
            <div class="analytics-chart">
              <div ref="weeklyChart" class="chart-container"></div>
            </div>
            <div class="analytics-summary">
              <div class="summary-item">
                <div class="summary-label">本周学习时长</div>
                <div class="summary-value">{{ stats.weeklyStudyTime }}小时</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">较上周</div>
                <div class="summary-value" :class="stats.weeklyChangeRate >= 0 ? 'positive' : 'negative'">
                  <el-icon><component :is="stats.weeklyChangeRate >= 0 ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
                  {{ Math.abs(stats.weeklyChangeRate) }}%
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 知识图谱和推荐资源 -->
      <el-row :gutter="8" class="second-row">
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <div class="dashboard-card knowledge-map">
            <div class="card-header">
              <h2>知识图谱概览</h2>
              <el-button type="primary" text @click="navigateToKnowledgeMap">查看详情</el-button>
            </div>
            <div class="knowledge-preview">
              <div ref="knowledgeGraph" class="graph-container">
                <!-- 此处将通过echarts渲染知识图谱 -->
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <div class="dashboard-card recommended-resources">
            <div class="card-header">
              <h2>推荐学习资源</h2>
              <el-button type="primary" text>更多推荐</el-button>
            </div>
            <el-scrollbar :height="isMobile ? '250px' : '300px'">
              <div class="resource-list">
                <div 
                  v-for="resource in recommendedResources" 
                  :key="resource.id" 
                  class="resource-item"
                  @click="openResource(resource)"
                >
                  <div class="resource-icon" :class="resource.type">
                    <el-icon><component :is="resourceIcons[resource.type]" /></el-icon>
                  </div>
                  <div class="resource-content">
                    <h3 class="resource-title">{{ resource.title }}</h3>
                    <p class="resource-desc">{{ resource.description }}</p>
                    <div class="resource-meta">
                      <span class="resource-type">{{ resourceTypes[resource.type] }}</span>
                      <span class="resource-author">{{ resource.author }}</span>
                      <span class="resource-rating">
                        <el-rate 
                          v-model="resource.rating" 
                          disabled 
                          :colors="['#FFCA3A', '#FFCA3A', '#FFCA3A']"
                          :max="5"
                          :score-template="String(resource.rating)"
                        />
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </el-scrollbar>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'
import { 
  Check, 
  Timer, 
  Calendar, 
  Document, 
  More, 
  Reading, 
  VideoPlay, 
  Link, 
  Collection,
  ArrowUp,
  ArrowDown,
  Connection,
  DataAnalysis
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 导入布局修复样式
import '@/assets/styles/dashboard-fix.css'

const userStore = useUserStore()
const router = useRouter()

// 检测移动设备
const isMobile = computed(() => {
  return window.innerWidth <= 768
})

// 图表引用
const weeklyChart = ref()
const knowledgeGraph = ref()

// 欢迎信息
const timeGreeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，注意休息'
  if (hour < 9) return '早上好，开启美好的一天'
  if (hour < 12) return '上午好，学习效率最佳时段'
  if (hour < 14) return '中午好，适当休息一下'
  if (hour < 18) return '下午好，再接再厉'
  if (hour < 22) return '晚上好，坚持就是胜利'
  return '夜深了，注意休息'
})

// 随机励志语
const motivationalQuotes = [
  '勤奋是天才的摇篮，耐心是成功的驿站。',
  '知识就是力量，学习改变命运。',
  '学习是永不停息的进行时，每一天都在成长。',
  '不积跬步，无以至千里；不积小流，无以成江海。',
  '学习的痛苦是暂时的，未学到的痛苦是终生的。'
]
const motivationalQuote = motivationalQuotes[Math.floor(Math.random() * motivationalQuotes.length)]

// 统计数据
const stats = reactive({
  completedTasks: 3,
  totalTasks: 5,
  studyTime: 4.5,
  streak: 12,
  weeklyStudyTime: 18.5,
  weeklyChangeRate: 12
})

// 计算进度百分比
const taskCompletionRate = computed(() => {
  return Math.round((stats.completedTasks / Math.max(stats.totalTasks, 1)) * 100)
})

const studyTimePercentage = computed(() => {
  // 假设每日目标是6小时
  return Math.min(Math.round((stats.studyTime / 6) * 100), 100)
})

const streakPercentage = computed(() => {
  // 假设目标是连续学习30天
  return Math.min(Math.round((stats.streak / 30) * 100), 100)
})

// 进度条颜色
const progressColors = {
  tasks: '#409EFF',
  study: '#67C23A',
  streak: '#E6A23C'
}

// 当前课程列表
const currentCourses = reactive([
  {
    id: 1,
    title: '数据结构与算法',
    instructor: '张教授',
    progress: 65,
    color: '#409EFF',
    category: '计算机科学',
    icon: 'Connection'
  },
  {
    id: 2,
    title: '机器学习基础',
    instructor: '李教授',
    progress: 42,
    color: '#67C23A',
    category: '人工智能',
    icon: 'DataAnalysis'
  },
  {
    id: 3,
    title: '高等数学',
    instructor: '王教授',
    progress: 78,
    color: '#E6A23C',
    category: '数学',
    icon: 'Reading'
  },
  {
    id: 4,
    title: '操作系统原理',
    instructor: '刘教授',
    progress: 30,
    color: '#F56C6C',
    category: '计算机科学',
    icon: 'Connection'
  }
])

// 待办事项列表
const todoList = ref([
  { id: 1, title: '完成数据结构作业', completed: false, priority: 'danger' as const },
  { id: 2, title: '预习机器学习第5章', completed: false, priority: 'warning' as const },
  { id: 3, title: '复习高数知识点', completed: false, priority: 'info' as const },
  { id: 4, title: '观看操作系统视频课程', completed: true, priority: 'success' as const }
])

const priorityText: Record<string, string> = {
  danger: '紧急',
  warning: '重要',
  info: '普通',
  success: '已完成'
}

// 推荐资源列表
const recommendedResources = ref([
  {
    id: 1,
    title: '数据结构精讲：树与图',
    description: '本课程深入讲解树与图的基本概念、实现方式及应用场景',
    type: 'video' as const,
    author: '张教授',
    rating: 4.8
  },
  {
    id: 2,
    title: '机器学习算法详解',
    description: '深入浅出讲解常见机器学习算法原理及Python实现',
    type: 'document' as const,
    author: '李教授',
    rating: 4.6
  },
  {
    id: 3,
    title: '高等数学习题集',
    description: '针对期末考试的高等数学重点习题及详解',
    type: 'pdf' as const,
    author: '王教授',
    rating: 4.5
  },
  {
    id: 4,
    title: '操作系统实验指导',
    description: '操作系统课程配套实验指导与代码示例',
    type: 'link' as const,
    author: '刘教授',
    rating: 4.7
  }
])

// 资源类型图标映射
const resourceIcons: Record<string, string> = {
  video: 'VideoPlay',
  document: 'Reading',
  pdf: 'Document',
  link: 'Link'
}

// 资源类型文本映射
const resourceTypes: Record<string, string> = {
  video: '视频',
  document: '文档',
  pdf: 'PDF',
  link: '链接'
}

// 导航到课程详情
const navigateToCourse = (courseId: number) => {
  router.push(`/courses/${courseId}`)
}

// 导航到知识图谱
const navigateToKnowledgeMap = () => {
  router.push('/knowledge')
}

// 打开资源
const openResource = (resource: any) => {
  ElMessage.success(`正在打开资源：${resource.title}`)
  // 实际应用中这里会跳转到资源页面或打开资源
}

// 添加待办事项
const addTodo = () => {
  ElMessageBox.prompt('请输入待办事项内容', '添加待办', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '内容不能为空'
  }).then(({ value }) => {
    const newTodo = {
      id: Date.now(),
      title: value,
      completed: false,
      priority: 'info' as const
    }
    todoList.value.unshift(newTodo)
    ElMessage({
      type: 'success',
      message: '添加成功'
    })
  }).catch(() => {})
}

// 切换待办状态
const toggleTodo = (id: number) => {
  const todo = todoList.value.find(item => item.id === id)
  if (todo && todo.completed) {
    todo.priority = 'success'
    ElMessage.success('恭喜你完成一项任务！')
  }
}

// 处理待办操作
const handleTodoCommand = (command: string, id: number) => {
  if (command === 'delete') {
    ElMessageBox.confirm('确定要删除这项待办吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      todoList.value = todoList.value.filter(item => item.id !== id)
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
    }).catch(() => {})
  } else if (command === 'edit') {
    const todo = todoList.value.find(item => item.id === id)
    if (!todo) return
    
    ElMessageBox.prompt('编辑待办事项', '编辑', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: todo.title,
      inputPattern: /\S+/,
      inputErrorMessage: '内容不能为空'
    }).then(({ value }) => {
      todo.title = value
      ElMessage({
        type: 'success',
        message: '编辑成功'
      })
    }).catch(() => {})
  }
}

// 初始化每周学习统计图表
const initWeeklyChart = () => {
  if (!weeklyChart.value) return
  
  const chart = echarts.init(weeklyChart.value, null, {
    renderer: 'canvas',
    useDirtyRect: true,
    devicePixelRatio: window.devicePixelRatio
  })
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisTick: {
        alignWithLabel: true
      },
      axisLabel: {
        fontSize: isMobile.value ? 10 : 12
      }
    },
    yAxis: {
      type: 'value',
      name: '学习时长(小时)',
      nameTextStyle: {
        fontSize: isMobile.value ? 10 : 12
      },
      axisLabel: {
        fontSize: isMobile.value ? 10 : 12
      }
    },
    series: [
      {
        name: '学习时长',
        type: 'bar',
        barWidth: isMobile.value ? '50%' : '60%',
        data: [3.5, 2.8, 4.2, 2.0, 3.5, 3.0, 1.5],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应窗口大小变化
  const resizeHandler = () => {
    chart.resize({ width: 'auto', height: 'auto' }) // 自动调整大小以适应容器
  }
  
  window.addEventListener('resize', resizeHandler)
  
  // 确保在组件卸载时移除事件监听
  onBeforeUnmount(() => {
    window.removeEventListener('resize', resizeHandler)
    chart.dispose()
  })
}

// 初始化知识图谱预览
const initKnowledgeGraph = () => {
  if (!knowledgeGraph.value) return
  
  const chart = echarts.init(knowledgeGraph.value, null, {
    renderer: 'canvas',
    useDirtyRect: true,
    devicePixelRatio: window.devicePixelRatio
  })
  
  const symbolSize = isMobile.value ? 0.7 : 1 // 移动设备上缩小节点
  
  const option = {
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      data: [
        { name: '数据结构', value: 80, symbolSize: 50 * symbolSize, itemStyle: { color: '#4facfe' } },
        { name: '算法', value: 70, symbolSize: 40 * symbolSize, itemStyle: { color: '#00f2fe' } },
        { name: '数学', value: 60, symbolSize: 35 * symbolSize, itemStyle: { color: '#45aaf2' } },
        { name: '计算机原理', value: 50, symbolSize: 30 * symbolSize, itemStyle: { color: '#2d98da' } },
        { name: '网络', value: 40, symbolSize: 25 * symbolSize, itemStyle: { color: '#0fb9b1' } },
        { name: '数据库', value: 45, symbolSize: 28 * symbolSize, itemStyle: { color: '#26de81' } },
        { name: '操作系统', value: 55, symbolSize: 32 * symbolSize, itemStyle: { color: '#20bf6b' } }
      ],
      links: [
        { source: '数据结构', target: '算法' },
        { source: '数据结构', target: '数学' },
        { source: '算法', target: '数学' },
        { source: '计算机原理', target: '操作系统' },
        { source: '计算机原理', target: '网络' },
        { source: '数据结构', target: '数据库' },
        { source: '操作系统', target: '网络' }
      ],
      categories: [{ name: '知识点' }],
      roam: true,
      label: {
        show: true,
        position: 'right',
        formatter: '{b}',
        fontSize: isMobile.value ? 10 : 12
      },
      force: {
        repulsion: isMobile.value ? 150 : 200,
        edgeLength: isMobile.value ? 60 : 80
      },
      emphasis: {
        focus: 'adjacency',
        lineStyle: {
          width: 4
        }
      }
    }]
  }
  
  chart.setOption(option)
  
  // 响应窗口大小变化
  const resizeHandler = () => {
    chart.resize({ width: 'auto', height: 'auto' }) // 自动调整大小以适应容器
  }
  
  window.addEventListener('resize', resizeHandler)
  
  // 确保在组件卸载时移除事件监听
  onBeforeUnmount(() => {
    window.removeEventListener('resize', resizeHandler)
    chart.dispose()
  })
}

// 页面加载时初始化图表
onMounted(() => {
  // 初始化每周学习时间图表
  initWeeklyChart()
  
  // 初始化知识图谱
  initKnowledgeGraph()
  
  // 添加窗口调整大小监听，以更新移动设备检测
  window.addEventListener('resize', () => {
    // isMobile会自动重新计算
    
    if (weeklyChart.value) {
      const weeklyChartInstance = echarts.getInstanceByDom(weeklyChart.value)
      weeklyChartInstance?.resize({ width: 'auto', height: 'auto' })
    }
    
    if (knowledgeGraph.value) {
      const knowledgeGraphInstance = echarts.getInstanceByDom(knowledgeGraph.value)
      knowledgeGraphInstance?.resize({ width: 'auto', height: 'auto' })
    }
  })
})
</script>

<style>
/* 删除之前的内联样式，因为已经通过外部CSS文件导入了 */
</style>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  position: relative;
  box-sizing: border-box;
  background-color: var(--el-fill-color-light);
  overflow-y: auto;
  overflow-x: hidden;
  flex: 1;
  margin: 0;
  padding: 0;
  min-width: 100%;
}

.welcome-section {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #4a6cf7 0%, #7b5aff 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;
  margin-bottom: 12px;
}

.greeting {
  flex: 1;
}

.greeting h1 {
  font-size: 24px;
  margin: 0;
  font-weight: 600;
}

.highlight {
  font-weight: 700;
}

.sub-greeting {
  font-size: 16px;
  margin: 8px 0 0;
  opacity: 0.9;
}

.quick-stats {
  display: flex;
  gap: 16px;
}

.cards-wrapper {
  padding: 0 8px 12px;
  width: 100%;
  box-sizing: border-box;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.stat-card {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  padding: 12px;
  display: flex;
  align-items: center;
  min-width: 180px;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 12px;
  font-size: 20px;
}

.completed-icon {
  background-color: #4caf50;
}

.study-icon {
  background-color: #ffa726;
}

.streak-icon {
  background-color: #ff7043;
}

.stat-content {
  flex: 1;
}

.stat-content h3 {
  font-size: 12px;
  font-weight: 500;
  margin: 0 0 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 4px;
}

.dashboard-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 12px;
  padding: 12px;
  box-sizing: border-box;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.card-header h2 {
  font-size: 18px;
  margin: 0;
  font-weight: 600;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}

.course-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  height: 100%;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.course-header {
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 32px;
}

.course-content {
  padding: 12px;
  display: flex;
  flex-direction: column;
  height: calc(100% - 80px);
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
}

.course-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.todo-items {
  list-style: none;
  padding: 0;
  margin: 0;
}

.todo-item {
  padding: 12px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-item-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.completed {
  text-decoration: line-through;
  color: var(--el-text-color-secondary);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 0;
  color: var(--el-text-color-secondary);
}

.empty-state .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.chart-container {
  height: 250px;
  width: 100%;
}

.analytics-summary {
  display: flex;
  justify-content: space-around;
  margin-top: 16px;
}

.summary-item {
  text-align: center;
}

.summary-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 4px;
}

.summary-value {
  font-size: 18px;
  font-weight: 600;
}

.positive {
  color: #10b981;
}

.negative {
  color: #ef4444;
}

.graph-container {
  height: 300px;
  width: 100%;
}

.resource-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resource-item {
  display: flex;
  padding: 12px;
  border-radius: 8px;
  background-color: var(--el-fill-color-light);
  transition: background-color 0.3s;
  cursor: pointer;
  width: 100%;
}

.resource-item:hover {
  background-color: var(--el-fill-color);
}

.resource-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 12px;
  font-size: 20px;
  color: white;
}

.resource-icon.video {
  background-color: #ff7043;
}

.resource-icon.document {
  background-color: #26c6da;
}

.resource-icon.pdf {
  background-color: #66bb6a;
}

.resource-icon.link {
  background-color: #7986cb;
}

.resource-content {
  flex: 1;
}

.resource-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px;
}

.resource-desc {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.resource-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.resource-type {
  padding: 2px 8px;
  border-radius: 4px;
  background-color: var(--el-fill-color);
}

.second-row {
  margin-top: 16px;
  width: 100%;
}

.todo-list-enter-active,
.todo-list-leave-active {
  transition: all 0.3s;
}

.todo-list-enter-from,
.todo-list-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

/* 媒体查询，适配移动设备 */
@media (max-width: 768px) {
  .welcome-section {
    flex-direction: column;
    padding: 16px;
  }
  
  .greeting {
    margin-bottom: 12px;
  }
  
  .greeting h1 {
    font-size: 18px;
  }
  
  .sub-greeting {
    font-size: 14px;
  }
  
  .quick-stats {
    flex-direction: column;
    width: 100%;
    gap: 8px;
  }
  
  .stat-card {
    min-width: unset;
    width: 100%;
  }
  
  .cards-wrapper {
    padding: 0 4px 8px;
  }
  
  .dashboard-card {
    padding: 10px;
    margin-bottom: 10px;
  }
  
  .card-header h2 {
    font-size: 16px;
  }
  
  .courses-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }
}

/* 特小屏幕设备 */
@media (max-width: 480px) {
  .welcome-section {
    padding: 12px;
  }
  
  .cards-wrapper {
    padding: 0 2px 4px;
  }
  
  .dashboard-card {
    padding: 8px;
    margin-bottom: 8px;
  }
  
  .card-header {
    margin-bottom: 8px;
  }
  
  .card-header h2 {
    font-size: 15px;
  }
}

/* 为了让el-row和el-col没有多余边距 */
:deep(.el-row) {
  margin-left: 0 !important;
  margin-right: 0 !important;
  width: 100%;
}

:deep(.el-col) {
  padding-left: 4px !important;
  padding-right: 4px !important;
}
</style> 