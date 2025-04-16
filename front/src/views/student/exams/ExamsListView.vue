<template>
  <div class="exams-list-container">
    <h1>考试列表</h1>
    
    <!-- 考试列表筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="form-inline">
        <el-form-item label="考试状态">
          <el-select v-model="filterForm.status" placeholder="考试状态" clearable>
            <el-option label="即将开始" value="upcoming"></el-option>
            <el-option label="进行中" value="ongoing"></el-option>
            <el-option label="已结束" value="completed"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="filterForm.subject" placeholder="科目" clearable>
            <el-option v-for="subject in subjects" :key="subject.value" :label="subject.label" :value="subject.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchExams">筛选</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 考试列表 -->
    <div class="exams-list">
      <el-empty v-if="exams.length === 0" description="暂无考试"></el-empty>
      <el-card v-for="exam in exams" :key="exam.id" class="exam-card">
        <div class="exam-header">
          <h2 class="exam-title">{{ exam.title }}</h2>
          <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
        </div>
        <div class="exam-info">
          <p><i class="el-icon-date"></i> 开始时间: {{ formatDateTime(exam.startTime) }}</p>
          <p><i class="el-icon-time"></i> 结束时间: {{ formatDateTime(exam.endTime) }}</p>
          <p><i class="el-icon-timer"></i> 考试时长: {{ exam.duration }}分钟</p>
          <p><i class="el-icon-notebook-2"></i> 总分: {{ exam.totalScore }}分</p>
        </div>
        <div class="exam-actions">
          <el-button type="primary" @click="viewExamDetail(exam.id)" :disabled="exam.status === 'completed'">
            {{ getActionButtonText(exam.status) }}
          </el-button>
          <el-button v-if="exam.status === 'completed'" @click="viewExamResult(exam.id)">查看结果</el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-if="exams.length > 0"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
      </el-pagination>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()

// 考试数据
const exams = ref([])
const subjects = ref([
  { label: '数学', value: 'math' },
  { label: '语文', value: 'chinese' },
  { label: '英语', value: 'english' },
  { label: '物理', value: 'physics' },
  { label: '化学', value: 'chemistry' },
  { label: '生物', value: 'biology' }
])

// 筛选表单
const filterForm = reactive({
  status: '',
  subject: ''
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 初始化
onMounted(() => {
  fetchExams()
})

// 获取考试列表
const fetchExams = async () => {
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      status: filterForm.status || undefined,
      subject: filterForm.subject || undefined
    }
    
    const response = await axios.get('/api/student/exams', { params })
    
    if (response.data.success) {
      exams.value = response.data.data.list || []
      pagination.total = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取考试列表失败')
    }
  } catch (error) {
    console.error('获取考试列表失败', error)
    ElMessage.error('网络错误，请重试')
  }
}

// 重置筛选
const resetFilter = () => {
  filterForm.status = ''
  filterForm.subject = ''
  pagination.currentPage = 1
  fetchExams()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchExams()
}

// 处理每页数量变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchExams()
}

// 获取状态显示文本
const getStatusText = (status) => {
  switch (status) {
    case 'upcoming': return '即将开始'
    case 'ongoing': return '进行中'
    case 'completed': return '已结束'
    default: return '未知状态'
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 'upcoming': return 'info'
    case 'ongoing': return 'success'
    case 'completed': return ''
    default: return 'info'
  }
}

// 获取操作按钮文本
const getActionButtonText = (status) => {
  switch (status) {
    case 'upcoming': return '查看详情'
    case 'ongoing': return '开始考试'
    case 'completed': return '已结束'
    default: return '查看详情'
  }
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 查看考试详情
const viewExamDetail = (examId) => {
  router.push(`/student/exams/detail/${examId}`)
}

// 查看考试结果
const viewExamResult = (examId) => {
  router.push(`/student/exams/result/${examId}`)
}
</script>

<style scoped>
.exams-list-container {
  padding: 20px;
}

.filter-section {
  margin-bottom: 20px;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.exams-list {
  margin-bottom: 20px;
}

.exam-card {
  margin-bottom: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.exam-card:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.exam-title {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.exam-info {
  margin-bottom: 15px;
  color: #606266;
}

.exam-info p {
  margin: 5px 0;
}

.exam-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 