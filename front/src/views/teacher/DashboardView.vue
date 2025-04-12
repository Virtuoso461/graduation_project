<template>
  <div class="teacher-dashboard">
    <div class="dashboard-header">
      <h2>教师控制面板</h2>
      <p>欢迎回来，{{ userInfo?.name }}</p>
    </div>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalCourses }}</div>
          <div class="stat-label">我的课程</div>
          <el-progress :percentage="stats.courseProgress" :format="format" :color="'#409EFF'" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalStudents }}</div>
          <div class="stat-label">学生总数</div>
          <el-progress :percentage="stats.studentProgress" :format="format" :color="'#67C23A'" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.pendingAssignments }}</div>
          <div class="stat-label">待批改作业</div>
          <el-button type="primary" size="small" @click="$router.push('/teacher/assignments/pending')">
            批改作业
          </el-button>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.pendingExams }}</div>
          <div class="stat-label">待批改考试</div>
          <el-button type="primary" size="small" @click="$router.push('/teacher/exams/pending')">
            批改考试
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>课程学生分布</span>
          </div>
          <div class="chart-placeholder">
            课程学生分布图表
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>学生成绩分布</span>
          </div>
          <div class="chart-placeholder">
            学生成绩分布图表
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="table-card">
          <div slot="header" class="card-header">
            <span>最近课程活动</span>
            <el-button type="text" @click="$router.push('/teacher/courses')">查看全部</el-button>
          </div>
          <el-table :data="recentCourseActivities" style="width: 100%">
            <el-table-column prop="course" label="课程" width="180"></el-table-column>
            <el-table-column prop="activity" label="活动"></el-table-column>
            <el-table-column prop="date" label="日期" width="180"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="table-card">
          <div slot="header" class="card-header">
            <span>待办事项</span>
            <el-button type="text" @click="addTodo">添加</el-button>
          </div>
          <el-table :data="todos" style="width: 100%">
            <el-table-column width="40">
              <template #default="scope">
                <el-checkbox v-model="scope.row.completed" @change="toggleTodo(scope.row)"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="任务"></el-table-column>
            <el-table-column prop="dueDate" label="截止日期" width="120"></el-table-column>
            <el-table-column width="80">
              <template #default="scope">
                <el-dropdown trigger="click" @command="handleTodoCommand($event, scope.row)">
                  <el-button type="text" icon="el-icon-more"></el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="edit">编辑</el-dropdown-item>
                      <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 进度条格式化
const format = (percentage: number) => {
  return percentage >= 0 ? `${percentage}%` : `${percentage}%`
}

// 统计数据
const stats = reactive({
  totalCourses: 5,
  courseProgress: 80,
  totalStudents: 128,
  studentProgress: 65,
  pendingAssignments: 12,
  pendingExams: 3
})

// 最近课程活动
const recentCourseActivities = ref([
  {
    course: '数据结构与算法',
    activity: '发布了新作业',
    date: '2023-04-10'
  },
  {
    course: '机器学习基础',
    activity: '更新了课程资料',
    date: '2023-04-09'
  },
  {
    course: '高等数学',
    activity: '发布了期中考试',
    date: '2023-04-08'
  },
  {
    course: '操作系统原理',
    activity: '批改了学生作业',
    date: '2023-04-07'
  }
])

// 待办事项
const todos = ref([
  {
    id: 1,
    title: '批改数据结构作业',
    completed: false,
    dueDate: '2023-04-15'
  },
  {
    id: 2,
    title: '准备机器学习课件',
    completed: false,
    dueDate: '2023-04-18'
  },
  {
    id: 3,
    title: '设计高数期中考试',
    completed: true,
    dueDate: '2023-04-08'
  }
])

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
      dueDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
    }
    todos.value.unshift(newTodo)
    ElMessage({
      type: 'success',
      message: '添加成功'
    })
  }).catch(() => {})
}

// 切换待办状态
const toggleTodo = (todo) => {
  if (todo.completed) {
    ElMessage.success('恭喜你完成一项任务！')
  }
}

// 处理待办操作
const handleTodoCommand = (command, todo) => {
  if (command === 'delete') {
    ElMessageBox.confirm('确定要删除这项待办吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      todos.value = todos.value.filter(item => item.id !== todo.id)
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
    }).catch(() => {})
  } else if (command === 'edit') {
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

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    // 从API获取数据
    // const response = await fetch('/api/teacher/dashboard')
    // const data = await response.json()
    // stats.value = data.stats
    // recentCourseActivities.value = data.recentActivities
    // todos.value = data.todos
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped lang="scss">
.teacher-dashboard {
  padding: 20px;

  .dashboard-header {
    margin-bottom: 24px;

    h2 {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
    }

    p {
      margin: 6px 0 0;
      color: #606266;
    }
  }

  .stat-cards {
    margin-bottom: 20px;
  }

  .stat-card {
    transition: all 0.3s;
    height: 140px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .stat-value {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }

    .stat-label {
      font-size: 14px;
      color: #606266;
      margin-bottom: 12px;
    }
  }

  .chart-row {
    margin-bottom: 20px;
  }

  .chart-card {
    height: 350px;
  }

  .chart-placeholder {
    height: 300px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #909399;
    background-color: #f5f7fa;
    border-radius: 4px;
  }

  .table-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
