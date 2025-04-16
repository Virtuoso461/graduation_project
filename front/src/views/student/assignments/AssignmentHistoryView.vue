<template>
  <div class="assignment-history-view">
    <div class="page-header">
      <h1>作业提交历史记录</h1>
      <div class="header-actions">
        <el-button type="primary" @click="refreshData">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </div>
    </div>

    <div class="history-container" v-loading="isLoading">
      <el-card>
        <template #header>
          <div class="filter-section">
            <el-select v-model="filterCourse" placeholder="课程筛选" clearable>
              <el-option
                v-for="course in courses"
                :key="course.id"
                :label="course.name"
                :value="course.id"
              />
            </el-select>
            <el-select v-model="filterStatus" placeholder="状态筛选" clearable>
              <el-option label="已提交" value="SUBMITTED" />
              <el-option label="已批改" value="GRADED" />
            </el-select>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              unlink-panels
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :shortcuts="datePickerShortcuts"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </div>
        </template>

        <div class="table-container">
          <el-table
            :data="filteredHistory"
            style="width: 100%"
            :header-cell-style="{ background: '#f5f7fa' }"
            border
          >
            <el-table-column prop="assignmentTitle" label="作业标题" min-width="200">
              <template #default="scope">
                <el-link
                  type="primary"
                  @click="viewSubmission(scope.row.assignmentId, scope.row.id)"
                >
                  {{ scope.row.assignmentTitle }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="courseName" label="所属课程" min-width="150"></el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag type="warning" v-if="scope.row.status === 'SUBMITTED'">待批改</el-tag>
                <el-tag type="success" v-else-if="scope.row.status === 'GRADED'">已批改</el-tag>
                <span v-else>{{ scope.row.status }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="submitTime" label="提交时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.submitTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="score" label="成绩" width="120" align="center">
              <template #default="scope">
                <span v-if="scope.row.status === 'GRADED'">
                  {{ scope.row.score || 0 }}/{{ scope.row.totalPoints || 100 }}
                </span>
                <el-tag type="info" v-else>未批改</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewSubmission(scope.row.assignmentId, scope.row.id)"
                >
                  查看详情
                </el-button>
                <el-button
                  v-if="scope.row.status === 'SUBMITTED'"
                  type="success"
                  size="small"
                  @click="resubmit(scope.row.assignmentId)"
                >
                  重新提交
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalItems"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>

        <div v-if="filteredHistory.length === 0" class="empty-data">
          <el-empty description="暂无作业提交历史"></el-empty>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { assignmentApi } from '@/utils/http/api'
import { Refresh } from '@element-plus/icons-vue'

const router = useRouter()
const isLoading = ref(false)
const submissions = ref([])
const filterCourse = ref('')
const filterStatus = ref('')
const dateRange = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)

// 模拟课程数据
const courses = ref([
  { id: '1', name: '数据结构与算法' },
  { id: '2', name: 'Web前端开发' },
  { id: '3', name: '数据库原理' },
  { id: '4', name: '操作系统' },
  { id: '5', name: '计算机网络' }
])

// 日期选择器快捷选项
const datePickerShortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    }
  }
]

// 获取提交历史
const fetchSubmissionHistory = async () => {
  try {
    isLoading.value = true
    const response = await assignmentApi.getSubmissionHistory()
    
    if (response && response.code === 200 && response.data) {
      submissions.value = response.data
      totalItems.value = submissions.value.length
      console.log('获取提交历史成功:', submissions.value)
    } else {
      ElMessage.error('获取提交历史失败')
      console.error('获取提交历史失败:', response)
    }
  } catch (error) {
    ElMessage.error(`获取提交历史失败: ${error instanceof Error ? error.message : '未知错误'}`)
    console.error('获取提交历史出错:', error)
  } finally {
    isLoading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 过滤后的历史记录
const filteredHistory = computed(() => {
  let filtered = [...submissions.value]
  
  // 按课程筛选
  if (filterCourse.value) {
    filtered = filtered.filter(item => item.courseId === filterCourse.value)
  }
  
  // 按状态筛选
  if (filterStatus.value) {
    filtered = filtered.filter(item => item.status === filterStatus.value)
  }
  
  // 按日期范围筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0])
    const endDate = new Date(dateRange.value[1])
    endDate.setHours(23, 59, 59, 999) // 设置为当天结束时间
    
    filtered = filtered.filter(item => {
      const submitDate = new Date(item.submitTime)
      return submitDate >= startDate && submitDate <= endDate
    })
  }
  
  // 计算总数
  totalItems.value = filtered.length
  
  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  
  return filtered.slice(start, end)
})

// 查看提交详情
const viewSubmission = (assignmentId, submissionId) => {
  router.push(`/assignments/submissions/${assignmentId}`)
}

// 重新提交作业
const resubmit = (assignmentId) => {
  router.push(`/assignments/${assignmentId}/submit`)
}

// 刷新数据
const refreshData = () => {
  fetchSubmissionHistory()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 页面加载时获取提交历史
onMounted(() => {
  fetchSubmissionHistory()
})
</script>

<style lang="scss" scoped>
.assignment-history-view {
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

  .history-container {
    .filter-section {
      display: flex;
      gap: 15px;
      margin-bottom: 5px;
      flex-wrap: wrap;

      @media (max-width: 768px) {
        flex-direction: column;
      }
    }

    .table-container {
      margin-bottom: 20px;
    }

    .pagination-container {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
    }

    .empty-data {
      padding: 30px 0;
    }
  }
}
</style> 