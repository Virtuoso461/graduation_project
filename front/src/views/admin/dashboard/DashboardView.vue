<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h2>管理员控制面板</h2>
      <p>欢迎回来，{{ userInfo?.name }}</p>
    </div>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">总用户数</div>
          <el-progress :percentage="stats.userGrowth" :format="format" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalCourses }}</div>
          <div class="stat-label">总课程数</div>
          <el-progress :percentage="stats.courseGrowth" :format="format" :color="'#67C23A'" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalExams }}</div>
          <div class="stat-label">总考试数</div>
          <el-progress :percentage="stats.examGrowth" :format="format" :color="'#E6A23C'" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.pendingApprovals }}</div>
          <div class="stat-label">待审核课程</div>
          <el-button type="primary" size="small" @click="$router.push('/admin/courses/approval')">
            审核课程
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>用户增长趋势</span>
          </div>
          <div class="chart-placeholder">
            用户增长趋势图表
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>课程分布情况</span>
          </div>
          <div class="chart-placeholder">
            课程分布图表
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="table-card">
          <template #header>
            <div class="card-header">
              <span>最近注册用户</span>
              <el-button class="button" type="text" @click="$router.push('/admin/users')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentUsers" style="width: 100%" :border="false">
            <el-table-column prop="id" label="ID" width="100" />
            <el-table-column prop="name" label="用户名" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="role" label="角色" />
            <el-table-column prop="createdAt" label="注册时间">
              <template #default="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="table-card">
          <template #header>
            <div class="card-header">
              <span>最近添加的课程</span>
              <el-button class="button" type="text" @click="$router.push('/admin/courses')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentCourses" style="width: 100%" :border="false">
            <el-table-column prop="id" label="ID" width="100" />
            <el-table-column prop="name" label="课程名称" />
            <el-table-column prop="category" label="分类" />
            <el-table-column prop="instructor" label="讲师" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
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

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case '已发布':
      return 'success'
    case '待审核':
      return 'warning'
    case '已拒绝':
      return 'danger'
    default:
      return 'info'
  }
}

// 进度条格式化
const format = (percentage: number) => {
  return percentage >= 0 ? `+${percentage}%` : `${percentage}%`
}

// 统计数据
const stats = reactive({
  totalUsers: 256,
  userGrowth: 12,
  totalCourses: 45,
  courseGrowth: 24,
  totalExams: 32,
  examGrowth: 5,
  pendingApprovals: 7
})

// 最近注册用户
const recentUsers = ref([
  {
    id: 'U001',
    name: '张三',
    email: 'zhangsan@example.com',
    role: '学生',
    createdAt: '2023-03-15'
  },
  {
    id: 'U002',
    name: '李四',
    email: 'lisi@example.com',
    role: '教师',
    createdAt: '2023-03-14'
  },
  {
    id: 'U003',
    name: '王五',
    email: 'wangwu@example.com',
    role: '学生',
    createdAt: '2023-03-13'
  },
  {
    id: 'U004',
    name: '赵六',
    email: 'zhaoliu@example.com',
    role: '学生',
    createdAt: '2023-03-12'
  }
])

// 最近添加的课程
const recentCourses = ref([
  {
    id: 'C001',
    name: 'Java编程基础',
    category: '编程语言',
    instructor: '张教授',
    status: '已发布'
  },
  {
    id: 'C002',
    name: 'Python数据分析',
    category: '数据科学',
    instructor: '李教授',
    status: '已发布'
  },
  {
    id: 'C003',
    name: '机器学习入门',
    category: '人工智能',
    instructor: '王教授',
    status: '待审核'
  },
  {
    id: 'C004',
    name: '数据库系统',
    category: '计算机科学',
    instructor: '赵教授',
    status: '已拒绝'
  }
])

// 加载数据
onMounted(() => {
  // 在真实环境中，这里应该从API获取数据
  // loadDashboardData()
})

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    // 从API获取数据
    // const response = await fetch('/api/admin/dashboard')
    // const data = await response.json()
    // stats.value = data.stats
    // recentUsers.value = data.recentUsers
    // recentCourses.value = data.recentCourses
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}
</script>

<style scoped lang="scss">
.admin-dashboard {
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