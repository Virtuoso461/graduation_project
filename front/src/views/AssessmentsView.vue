<template>
  <div class="assessments-view">
    <div class="page-header">
      <h1>学习评估</h1>
      <el-button type="primary" @click="startNewAssessment">
        <el-icon><Plus /></el-icon>开始新评估
      </el-button>
    </div>

    <div class="assessments-container">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="待完成" name="pending">
          <div v-if="isLoading" class="loading-state">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else-if="pendingAssessments.length === 0" class="empty-state">
            <el-empty description="暂无待完成评估" />
          </div>
          <div v-else class="assessment-list">
            <el-card v-for="assessment in pendingAssessments" :key="assessment.id" class="assessment-card">
              <template #header>
                <div class="card-header">
                  <h3>{{ assessment.title }}</h3>
                  <el-tag :type="assessment.type === 'quiz' ? 'success' : 'warning'">
                    {{ assessment.type === 'quiz' ? '测验' : '作业' }}
                  </el-tag>
                </div>
              </template>
              <div class="assessment-content">
                <p>{{ assessment.description }}</p>
                <div class="assessment-info">
                  <span>
                    <el-icon><Timer /></el-icon>
                    截止时间：{{ assessment.deadline }}
                  </span>
                  <span>
                    <el-icon><Document /></el-icon>
                    总分：{{ assessment.totalScore }}分
                  </span>
                </div>
                <div class="assessment-actions">
                  <el-button type="primary" @click="startAssessment(assessment)">
                    开始{{ assessment.type === 'quiz' ? '测验' : '作业' }}
                  </el-button>
                  <el-button @click="viewDetails(assessment)">查看详情</el-button>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="已完成" name="completed">
          <div v-if="isLoading" class="loading-state">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else-if="completedAssessments.length === 0" class="empty-state">
            <el-empty description="暂无已完成评估" />
          </div>
          <div v-else class="assessment-list">
            <el-card v-for="assessment in completedAssessments" :key="assessment.id" class="assessment-card">
              <template #header>
                <div class="card-header">
                  <h3>{{ assessment.title }}</h3>
                  <el-tag :type="assessment.type === 'quiz' ? 'success' : 'warning'">
                    {{ assessment.type === 'quiz' ? '测验' : '作业' }}
                  </el-tag>
                </div>
              </template>
              <div class="assessment-content">
                <p>{{ assessment.description }}</p>
                <div class="assessment-info">
                  <span>
                    <el-icon><Timer /></el-icon>
                    完成时间：{{ assessment.completedDate }}
                  </span>
                  <span>
                    <el-icon><Document /></el-icon>
                    得分：{{ assessment.score }}分
                  </span>
                </div>
                <div class="assessment-actions">
                  <el-button @click="viewResults(assessment)">查看结果</el-button>
                  <el-button @click="reviewAssessment(assessment)">复习</el-button>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus, Timer, Document } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const activeTab = ref('pending')
const isLoading = ref(false)

// 评估数据
const pendingAssessments = ref<AssessmentItem[]>([])
const completedAssessments = ref<AssessmentItem[]>([])

// 定义接口类型
interface AssessmentItem {
  id: number;
  title: string;
  description: string;
  type: string;
  status: string;
  deadline: string;
  totalScore: number;
  completedDate?: string;
  score?: number;
}

interface AssessmentResponse {
  pendingCount: number;
  gradedCount: number;
  submittedCount: number;
  totalCount: number;
  pendingAssignments: AssessmentItem[];
  gradedAssignments?: AssessmentItem[];
  submittedAssignments?: AssessmentItem[];
}

// 获取评估数据
const fetchAssessments = async () => {
  try {
    isLoading.value = true
    
    // 从 localStorage 获取用户邮箱
    const userJson = localStorage.getItem('user')
    let email = ''
    
    if (userJson) {
      try {
        const user = JSON.parse(userJson)
        email = user.username || user.email
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
    
    if (!email) {
      ElMessage.error('未获取到用户邮箱，请重新登录')
      return
    }
    
    console.log('使用邮箱获取评估数据:', email)
    
    // 调用后端API获取数据
    const response = await axios.get(`http://localhost:8080/api/assignments/stats/${email}`)
    console.log('评估数据响应:', response)
    
    // 检查响应数据结构
    if (response.data && response.data.data) {
      const responseData = response.data.data as AssessmentResponse
      console.log('成功获取评估数据:', responseData)
      
      // 根据返回的数据结构提取数组
      pendingAssessments.value = responseData.pendingAssignments || []
      
      // 合并已提交和已评分的作业到已完成列表
      const submitted = responseData.submittedAssignments || []
      const graded = responseData.gradedAssignments || []
      completedAssessments.value = [...submitted, ...graded]
    } else {
      console.error('返回数据格式不正确:', response.data)
      ElMessage.error('获取评估列表失败：数据格式不正确')
    }
  } catch (error) {
    console.error('获取评估列表失败:', error)
    ElMessage.error('获取评估列表失败')
  } finally {
    isLoading.value = false
  }
}

const startNewAssessment = () => {
  // 实现开始新评估的逻辑
}

const startAssessment = (assessment: any) => {
  // 实现开始评估的逻辑
}

const viewDetails = (assessment: any) => {
  // 实现查看详情的逻辑
}

const viewResults = (assessment: any) => {
  // 实现查看结果的逻辑
}

const reviewAssessment = (assessment: any) => {
  // 实现复习评估的逻辑
}

// 页面加载时获取数据
onMounted(() => {
  fetchAssessments()
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/responsive' as responsive;

.assessments-view {
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

  .assessments-container {
    .loading-state, .empty-state {
      margin: 40px 0;
      text-align: center;
    }

    .assessment-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;
      margin-top: 20px;

      @include responsive.respond-to('md') {
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      }

      @include responsive.respond-to('sm') {
        grid-template-columns: 1fr;
      }
    }

    .assessment-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        h3 {
          margin: 0;
          font-size: 18px;
        }
      }

      .assessment-content {
        p {
          margin: 0 0 15px;
          color: var(--el-text-color-regular);
        }

        .assessment-info {
          display: flex;
          flex-direction: column;
          gap: 10px;
          margin-bottom: 15px;
          color: var(--el-text-color-secondary);

          span {
            display: flex;
            align-items: center;
            gap: 5px;
          }
        }

        .assessment-actions {
          display: flex;
          gap: 10px;
        }
      }
    }
  }
}
</style> 