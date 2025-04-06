<template>
  <div class="approval-container">
    <div class="page-header">
      <h2>课程审核</h2>
      <el-button @click="$router.push('/admin/courses')">返回课程管理</el-button>
    </div>

    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="待审核" name="pending">
        <el-card v-if="pendingCourses.length === 0" class="empty-card">
          <div class="empty-content">
            <el-icon><Box /></el-icon>
            <p>没有待审核的课程</p>
          </div>
        </el-card>
        <el-card v-else v-for="course in pendingCourses" :key="course.id" class="course-card">
          <div class="course-header">
            <div class="course-info">
              <el-avatar :size="50" :src="course.cover" shape="square">
                {{ course.name.charAt(0) }}
              </el-avatar>
              <div class="course-details">
                <h3>{{ course.name }}</h3>
                <div class="course-meta">
                  <span><el-tag size="small">{{ course.category }}</el-tag></span>
                  <span>讲师: {{ course.instructor }}</span>
                  <span>提交时间: {{ formatDate(course.submittedAt) }}</span>
                </div>
              </div>
            </div>
            <div class="course-actions">
              <el-button type="primary" @click="viewCourseDetails(course)">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="已审核" name="reviewed">
        <el-table :data="reviewedCourses" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="课程名称" min-width="200" />
          <el-table-column prop="instructor" label="讲师" width="120" />
          <el-table-column prop="category" label="分类" width="120" />
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
          <el-table-column prop="reviewedAt" label="审核时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.reviewedAt) }}
            </template>
          </el-table-column>
          <el-table-column prop="reviewer" label="审核人" width="120" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="viewCourseDetails(scope.row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 课程详情对话框 -->
    <el-dialog v-model="detailsVisible" title="课程详情" width="800px">
      <div v-if="currentCourse" class="course-details-dialog">
        <div class="dialog-header">
          <div class="course-title">
            <h3>{{ currentCourse.name }}</h3>
            <el-tag :type="getStatusType(currentCourse.status)">{{ currentCourse.status }}</el-tag>
          </div>
          <div class="course-meta">
            <span><el-icon><User /></el-icon> 讲师: {{ currentCourse.instructor }}</span>
            <span><el-icon><Collection /></el-icon> 分类: {{ currentCourse.category }}</span>
          </div>
        </div>
        
        <el-divider />
        
        <div class="course-content">
          <h4>课程简介</h4>
          <p>{{ currentCourse.description }}</p>
          
          <h4>课程大纲</h4>
          <div class="course-outline">
            <el-collapse>
              <el-collapse-item v-for="(section, index) in currentCourse.outline" :key="index" :title="`${index+1}. ${section.title}`" :name="index">
                <div v-for="(item, i) in section.items" :key="i" class="outline-item">
                  <span>{{ i+1 }}. {{ item }}</span>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
          
          <h4>教学目标</h4>
          <p>{{ currentCourse.learningObjectives }}</p>
          
          <h4>适合人群</h4>
          <p>{{ currentCourse.targetAudience }}</p>
        </div>
        
        <el-divider />
        
        <div v-if="currentCourse.status === '待审核'" class="approval-actions">
          <el-form ref="approvalFormRef" :model="approvalForm" :rules="approvalRules" label-width="80px">
            <el-form-item label="审核结果" prop="result">
              <el-radio-group v-model="approvalForm.result">
                <el-radio label="approve">通过</el-radio>
                <el-radio label="reject">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="反馈意见" prop="feedback" v-if="approvalForm.result === 'reject'">
              <el-input
                v-model="approvalForm.feedback"
                type="textarea"
                :rows="3"
                placeholder="请填写拒绝原因"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailsVisible = false">取消</el-button>
          <el-button v-if="currentCourse?.status === '待审核'" type="primary" @click="submitApproval">
            提交审核结果
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Box, User, Collection } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

// 课程类型接口
interface CourseSection {
  title: string
  items: string[]
}

interface Course {
  id: string
  name: string
  cover: string
  category: string
  instructor: string
  status: string
  submittedAt: string
  reviewedAt?: string
  reviewer?: string
  description?: string
  outline?: CourseSection[]
  learningObjectives?: string
  targetAudience?: string
}

const router = useRouter()
const activeTab = ref('pending')
const loading = ref(false)
const detailsVisible = ref(false)
const currentCourse = ref<Course | null>(null)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 审核表单
const approvalFormRef = ref<FormInstance>()
const approvalForm = reactive({
  result: '',
  feedback: ''
})

// 表单规则
const approvalRules = reactive<FormRules>({
  result: [
    { required: true, message: '请选择审核结果', trigger: 'change' }
  ],
  feedback: [
    { required: true, message: '请填写拒绝原因', trigger: 'blur' }
  ]
})

// 待审核课程
const pendingCourses = ref<Course[]>([])

// 已审核课程
const reviewedCourses = ref<Course[]>([])

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

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchReviewedCourses()
}

// 当前页变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchReviewedCourses()
}

// 查看课程详情
const viewCourseDetails = (course: Course) => {
  currentCourse.value = course
  
  // 如果是待审核的课程，重置审核表单
  if (course.status === '待审核') {
    approvalForm.result = ''
    approvalForm.feedback = ''
  }
  
  detailsVisible.value = true
}

// 提交审核结果
const submitApproval = async () => {
  if (!approvalFormRef.value) return
  
  await approvalFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请正确填写审核信息')
      return
    }
    
    if (approvalForm.result === 'reject' && !approvalForm.feedback) {
      ElMessage.warning('请填写拒绝原因')
      return
    }
    
    // 模拟API调用
    loading.value = true
    await new Promise(resolve => setTimeout(resolve, 500))
    
    if (currentCourse.value) {
      // 更新课程状态
      const courseId = currentCourse.value.id
      const newStatus = approvalForm.result === 'approve' ? '已发布' : '已拒绝'
      
      // 在实际应用中，这里会调用API提交审核结果
      console.log('提交审核结果:', {
        courseId,
        result: approvalForm.result,
        feedback: approvalForm.feedback || '通过审核',
        status: newStatus
      })
      
      // 更新列表数据
      pendingCourses.value = pendingCourses.value.filter(c => c.id !== courseId)
      
      // 添加到已审核列表
      const reviewed = {
        ...currentCourse.value,
        status: newStatus,
        reviewedAt: new Date().toISOString(),
        reviewer: '管理员'
      }
      
      reviewedCourses.value.unshift(reviewed)
      
      ElMessage.success(`课程已${approvalForm.result === 'approve' ? '通过' : '拒绝'}`)
      detailsVisible.value = false
    }
    
    loading.value = false
  })
}

// 获取待审核课程
const fetchPendingCourses = async () => {
  loading.value = true
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据
    pendingCourses.value = [
      {
        id: 'C003',
        name: '机器学习入门',
        cover: '',
        category: '人工智能',
        instructor: '王教授',
        status: '待审核',
        submittedAt: '2023-03-05T14:20:00Z',
        description: '本课程介绍机器学习的基本概念、算法和应用场景，帮助学生建立机器学习的基础知识体系。',
        outline: [
          {
            title: '机器学习概述',
            items: ['什么是机器学习', '机器学习的分类', '机器学习的应用场景']
          },
          {
            title: '监督学习算法',
            items: ['线性回归', '逻辑回归', '决策树', '支持向量机']
          },
          {
            title: '无监督学习算法',
            items: ['聚类算法', '主成分分析']
          }
        ],
        learningObjectives: '通过本课程的学习，学生将掌握机器学习的基本概念和常用算法，能够应用所学知识解决简单的机器学习问题。',
        targetAudience: '本课程适合对人工智能和机器学习感兴趣的初学者，要求有基础的线性代数和概率统计知识。'
      },
      {
        id: 'C005',
        name: 'Web前端开发实战',
        cover: '',
        category: '编程语言',
        instructor: '张教授',
        status: '待审核',
        submittedAt: '2023-03-08T10:15:00Z',
        description: '本课程涵盖现代前端开发的核心技术，包括HTML5、CSS3和JavaScript，以及流行的框架和工具。',
        outline: [
          {
            title: 'HTML5与CSS3基础',
            items: ['HTML5新特性', 'CSS3新特性', '响应式设计']
          },
          {
            title: 'JavaScript编程',
            items: ['ES6+新特性', '异步编程', 'DOM操作']
          },
          {
            title: '前端框架',
            items: ['Vue.js基础', 'React基础', '状态管理']
          }
        ],
        learningObjectives: '通过本课程的学习，学生将掌握现代前端开发技术，能够独立开发简单的Web应用。',
        targetAudience: '本课程适合有基础编程知识的学生和想要转型为前端开发的从业者。'
      }
    ]
  } catch (error) {
    ElMessage.error('获取待审核课程失败')
  } finally {
    loading.value = false
  }
}

// 获取已审核课程
const fetchReviewedCourses = async () => {
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
        instructor: '李教授',
        status: '已发布',
        submittedAt: '2023-01-10T09:30:00Z',
        reviewedAt: '2023-01-15T08:00:00Z',
        reviewer: '管理员'
      },
      {
        id: 'C002',
        name: 'Python数据分析',
        cover: '',
        category: '数据科学',
        instructor: '王教授',
        status: '已发布',
        submittedAt: '2023-02-05T11:20:00Z',
        reviewedAt: '2023-02-10T09:30:00Z',
        reviewer: '管理员'
      },
      {
        id: 'C004',
        name: '数据库系统',
        cover: '',
        category: '计算机科学',
        instructor: '赵教授',
        status: '已拒绝',
        submittedAt: '2023-02-25T13:40:00Z',
        reviewedAt: '2023-03-01T11:15:00Z',
        reviewer: '管理员'
      }
    ]
    
    total.value = mockCourses.length
    
    // 分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    reviewedCourses.value = mockCourses.slice(start, end)
    
  } catch (error) {
    ElMessage.error('获取已审核课程失败')
  } finally {
    loading.value = false
  }
}

// 初始化加载
onMounted(() => {
  fetchPendingCourses()
  fetchReviewedCourses()
})
</script>

<style scoped lang="scss">
.approval-container {
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
  
  .empty-card {
    .empty-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 30px;
      
      .el-icon {
        font-size: 48px;
        color: #909399;
        margin-bottom: 16px;
      }
      
      p {
        color: #909399;
        font-size: 16px;
      }
    }
  }
  
  .course-card {
    margin-bottom: 16px;
    
    .course-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .course-info {
        display: flex;
        align-items: center;
        
        .course-details {
          margin-left: 12px;
          
          h3 {
            margin: 0 0 8px;
            font-size: 16px;
          }
          
          .course-meta {
            display: flex;
            align-items: center;
            color: #606266;
            font-size: 13px;
            
            span {
              margin-right: 16px;
              
              &:last-child {
                margin-right: 0;
              }
            }
          }
        }
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .course-details-dialog {
    .dialog-header {
      margin-bottom: 20px;
      
      .course-title {
        display: flex;
        align-items: center;
        
        h3 {
          margin: 0 16px 0 0;
          font-size: 18px;
        }
      }
      
      .course-meta {
        margin-top: 12px;
        display: flex;
        color: #606266;
        
        span {
          margin-right: 20px;
          display: flex;
          align-items: center;
          
          .el-icon {
            margin-right: 4px;
          }
        }
      }
    }
    
    .course-content {
      h4 {
        margin: 20px 0 12px;
        font-size: 16px;
        color: #303133;
      }
      
      p {
        margin: 8px 0;
        color: #606266;
        line-height: 1.6;
      }
      
      .course-outline {
        margin: 10px 0;
        
        .outline-item {
          padding: 4px 0;
          color: #606266;
        }
      }
    }
    
    .approval-actions {
      margin-top: 20px;
    }
  }
}
</style> 