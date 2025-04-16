<template>
  <div class="courses-container">
    <div class="page-header">
      <h2>课程管理</h2>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="课程名称">
          <el-input v-model="filterForm.name" placeholder="搜索课程名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filterForm.category" placeholder="选择分类" clearable>
            <el-option label="编程语言" value="编程语言" />
            <el-option label="数据科学" value="数据科学" />
            <el-option label="人工智能" value="人工智能" />
            <el-option label="计算机科学" value="计算机科学" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable>
            <el-option label="已发布" value="已发布" />
            <el-option label="待审核" value="待审核" />
            <el-option label="已拒绝" value="已拒绝" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table v-loading="loading" :data="courseList" style="width: 100%" border>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="课程信息" min-width="250">
          <template #default="scope">
            <div class="course-info">
              <el-avatar :size="50" :src="scope.row.cover" shape="square">
                {{ scope.row.name ? scope.row.name.charAt(0) : 'C' }}
              </el-avatar>
              <div class="course-details">
                <div class="course-name">{{ scope.row.name }}</div>
                <div class="course-category">{{ scope.row.category }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="instructor" label="讲师" width="120" />
        <el-table-column label="学生数量" width="100" align="center">
          <template #default="scope">
            {{ scope.row.studentCount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              effect="plain"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              v-if="scope.row.status === '待审核'"
              size="small"
              type="success"
              @click="handleApprove(scope.row)"
            >
              审核
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
            >
              删除
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
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

// 路由
const router = useRouter()

// 课程类型接口
interface Course {
  id: string
  name: string
  cover: string
  category: string
  instructor: string
  studentCount: number
  status: string
  createdAt: string
}

// 加载状态
const loading = ref(false)

// 课程列表数据
const courseList = ref<Course[]>([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 筛选表单
const filterForm = reactive({
  name: '',
  category: '',
  status: ''
})

// 获取状态标签类型
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

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 搜索课程
const handleSearch = () => {
  currentPage.value = 1
  fetchCourseList()
}

// 重置筛选条件
const resetFilter = () => {
  filterForm.name = ''
  filterForm.category = ''
  filterForm.status = ''
  handleSearch()
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchCourseList()
}

// 当前页变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchCourseList()
}

// 查看课程
const handleView = (course: any) => {
  // 跳转到前台的课程详情页
  router.push(`/courses/${course.id}`)
}

// 审核课程
const handleApprove = (course: any) => {
  router.push(`/admin/courses/approval`)
}

// 删除课程
const handleDelete = (course: any) => {
  ElMessageBox.confirm(
    `确定要删除课程 "${course.name}" 吗？此操作不可恢复。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(() => {
      deleteCourse(course.id)
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

// 获取课程列表
const fetchCourseList = async () => {
  loading.value = true
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据
    const mockCourses = [
      {
        id: 'C001',
        name: 'Java编程基础',
        cover: '',
        category: '编程语言',
        instructor: '张教授',
        studentCount: 156,
        status: '已发布',
        createdAt: '2023-01-15T08:00:00Z'
      },
      {
        id: 'C002',
        name: 'Python数据分析',
        cover: '',
        category: '数据科学',
        instructor: '李教授',
        studentCount: 243,
        status: '已发布',
        createdAt: '2023-02-10T09:30:00Z'
      },
      {
        id: 'C003',
        name: '机器学习入门',
        cover: '',
        category: '人工智能',
        instructor: '王教授',
        studentCount: 0,
        status: '待审核',
        createdAt: '2023-03-05T14:20:00Z'
      },
      {
        id: 'C004',
        name: '数据库系统',
        cover: '',
        category: '计算机科学',
        instructor: '赵教授',
        studentCount: 0,
        status: '已拒绝',
        createdAt: '2023-03-01T11:15:00Z'
      }
    ]
    
    // 根据筛选条件过滤
    const filteredCourses = mockCourses.filter(course => {
      if (filterForm.name && !course.name.includes(filterForm.name)) {
        return false
      }
      if (filterForm.category && course.category !== filterForm.category) {
        return false
      }
      if (filterForm.status && course.status !== filterForm.status) {
        return false
      }
      return true
    })
    
    total.value = filteredCourses.length
    
    // 分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    courseList.value = filteredCourses.slice(start, end)
    
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 删除课程
const deleteCourse = async (courseId: string): Promise<void> => {
  // 模拟API调用
  await new Promise(resolve => setTimeout(resolve, 500))
  
  // 在实际应用中，这里会调用API删除课程
  console.log('删除课程ID:', courseId)
  
  // 更新列表
  courseList.value = courseList.value.filter(course => course.id !== courseId)
  total.value--
  
  ElMessage.success('课程删除成功')
}

// 初始化加载
onMounted(() => {
  fetchCourseList()
})
</script>

<style scoped lang="scss">
.courses-container {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      font-weight: 600;
    }
  }
  
  .filter-card {
    margin-bottom: 20px;
  }
  
  .table-card {
    margin-bottom: 20px;
  }
  
  .course-info {
    display: flex;
    align-items: center;
    
    .course-details {
      margin-left: 10px;
      
      .course-name {
        font-weight: 500;
        margin-bottom: 4px;
      }
      
      .course-category {
        font-size: 12px;
        color: #909399;
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 