<template>
  <div class="assignment-detail-view">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" text>
          <el-icon><ArrowLeft /></el-icon>返回列表
        </el-button>
        <h1>{{ assignment.title }}</h1>
      </div>
      <div class="header-actions">
        <el-button 
          type="primary" 
          @click="startAssignment" 
          v-if="!assignment.isSubmitted"
          :disabled="isPastDeadline"
        >
          {{ assignment.status === 'inProgress' ? '继续完成' : '开始作业' }}
        </el-button>
        <el-tag v-else :type="getScoreTagType(assignment.score)" size="large">
          {{ getScoreText(assignment.score) }}
        </el-tag>
      </div>
    </div>

    <div class="assignment-container">
      <el-skeleton :loading="isLoading" animated>
        <template #template>
          <div class="skeleton-container">
            <el-skeleton-item variant="h1" style="width: 50%;" />
            <el-skeleton-item variant="text" style="width: 80%;" />
            <el-skeleton-item variant="text" style="width: 60%;" />
            <el-skeleton-item variant="text" style="width: 70%;" />
            <el-skeleton-item variant="text" style="width: 85%;" />
          </div>
        </template>
        
        <template #default>
          <el-card class="assignment-card">
            <div class="assignment-status">
              <div class="status-tags">
                <el-tag 
                  :type="getStatusType(assignment.status)" 
                  effect="dark"
                >
                  {{ getStatusLabel(assignment.status) }}
                </el-tag>
                
                <el-tag type="danger" v-if="isPastDeadline && !assignment.isSubmitted">
                  已过期
                </el-tag>
                
                <el-tag type="success" v-if="assignment.isSubmitted">
                  已提交
                </el-tag>
              </div>
              
              <div class="deadline-info" :class="{'urgent-text': isUrgent && !assignment.isSubmitted}">
                <el-icon><Timer /></el-icon>
                <span v-if="!assignment.isSubmitted">
                  {{ getDeadlineText() }}
                </span>
                <span v-else>
                  提交于: {{ assignment.submittedTime }}
                </span>
              </div>
            </div>
            
            <div class="info-grid">
              <div class="info-item">
                <div class="label">所属课程</div>
                <div class="value course-name">{{ assignment.courseName }}</div>
              </div>
              
              <div class="info-item">
                <div class="label">发布教师</div>
                <div class="value">{{ assignment.teacherName }}</div>
              </div>
              
              <div class="info-item">
                <div class="label">发布时间</div>
                <div class="value">{{ assignment.publishTime }}</div>
              </div>
              
              <div class="info-item">
                <div class="label">截止时间</div>
                <div class="value">{{ assignment.deadline }}</div>
              </div>
              
              <div class="info-item">
                <div class="label">题目数量</div>
                <div class="value">{{ assignment.questionCount }} 个</div>
              </div>
              
              <div class="info-item">
                <div class="label">总分值</div>
                <div class="value">{{ assignment.totalPoints }} 分</div>
              </div>
              
              <div class="info-item">
                <div class="label">作业难度</div>
                <div class="value difficulty">
                  <el-rate
                    v-model="assignment.difficulty"
                    disabled
                    text-color="#ff9900"
                  />
                </div>
              </div>
              
              <div class="info-item">
                <div class="label">预计用时</div>
                <div class="value">{{ getEstimatedTimeText(assignment.estimatedTime) }}</div>
              </div>
            </div>
            
            <el-divider content-position="left">作业描述</el-divider>
            
            <div class="description-section">
              <p class="description-text">{{ assignment.description }}</p>
              
              <div class="requirements-list" v-if="assignment.requirements && assignment.requirements.length > 0">
                <h3>具体要求</h3>
                <el-collapse>
                  <el-collapse-item 
                    v-for="(req, index) in assignment.requirements" 
                    :key="index"
                    :title="req.title"
                    :name="index"
                  >
                    <div class="requirement-content">
                      <p>{{ req.content }}</p>
                      <ul v-if="req.items && req.items.length > 0">
                        <li v-for="(item, i) in req.items" :key="i">{{ item }}</li>
                      </ul>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
            
            <el-divider content-position="left">提交要求</el-divider>
            
            <div class="submission-section">
              <div class="submission-types">
                <h3>提交类型</h3>
                <div class="submission-type-list">
                  <div v-for="(type, index) in assignment.submissionTypes" :key="index" class="submission-type-item">
                    <el-icon><Document /></el-icon>
                    <span>{{ type }}</span>
                  </div>
                </div>
              </div>
              
              <div class="submission-format">
                <h3>格式要求</h3>
                <ul>
                  <li v-for="(format, index) in assignment.formatRequirements" :key="index">
                    {{ format }}
                  </li>
                </ul>
              </div>
              
              <div class="grading-criteria" v-if="assignment.gradingCriteria && assignment.gradingCriteria.length > 0">
                <h3>评分标准</h3>
                <el-table :data="assignment.gradingCriteria" style="width: 100%">
                  <el-table-column prop="criteria" label="评分项" width="180" />
                  <el-table-column prop="percentage" label="占比" width="100">
                    <template #default="scope">
                      {{ scope.row.percentage }}%
                    </template>
                  </el-table-column>
                  <el-table-column prop="description" label="说明" />
                </el-table>
              </div>
            </div>
            
            <el-divider content-position="left">参考资料</el-divider>
            
            <div class="reference-section">
              <div class="reference-list" v-if="assignment.references && assignment.references.length > 0">
                <div v-for="(ref, index) in assignment.references" :key="index" class="reference-item">
                  <div class="ref-type">
                    <el-icon v-if="ref.type === 'pdf'"><Document /></el-icon>
                    <el-icon v-else-if="ref.type === 'video'"><VideoPlay /></el-icon>
                    <el-icon v-else-if="ref.type === 'link'"><Link /></el-icon>
                    <el-icon v-else><InfoFilled /></el-icon>
                  </div>
                  <div class="ref-content">
                    <div class="ref-title">{{ ref.title }}</div>
                    <div class="ref-description">{{ ref.description }}</div>
                  </div>
                  <div class="ref-action">
                    <el-button type="primary" text @click="openReference(ref)">
                      {{ getRefActionText(ref.type) }}
                    </el-button>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无参考资料"></el-empty>
            </div>
          </el-card>
        </template>
      </el-skeleton>
    </div>
    
    <div class="action-footer">
      <el-button @click="goBack">返回列表</el-button>
      <el-button 
        type="primary" 
        @click="startAssignment" 
        v-if="!assignment.isSubmitted"
        :disabled="isPastDeadline"
      >
        {{ assignment.status === 'inProgress' ? '继续完成' : '开始作业' }}
      </el-button>
      <el-button 
        type="success" 
        @click="viewFeedback" 
        v-if="assignment.isSubmitted && assignment.hasFeedback"
      >
        查看批改结果
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Timer, Document, InfoFilled, 
  VideoPlay, Link 
} from '@element-plus/icons-vue'
import { assignmentApi } from '@/utils/http/api'

interface GradingCriteria {
  criteria: string;
  percentage: number;
  description: string;
}

interface Reference {
  type: string;
  title: string;
  description: string;
  url: string;
}

interface Requirement {
  title: string;
  content: string;
  items?: string[];
}

interface AssignmentData {
  id: string;
  title: string;
  courseId: string;
  courseName: string;
  teacherName: string;
  description: string;
  status: string;
  publishTime: string;
  deadline: string;
  difficulty: number;
  estimatedTime: number;
  questionCount: number;
  totalPoints: number;
  requirements: Requirement[];
  submissionTypes: string[];
  formatRequirements: string[];
  gradingCriteria: GradingCriteria[];
  references: Reference[];
  isSubmitted: boolean;
  submittedTime?: string;
  score?: number;
  hasFeedback?: boolean;
}

const route = useRoute()
const router = useRouter()
const assignmentId = computed(() => route.params.id as string)
const isLoading = ref(true)

// 模拟作业数据
const assignment = ref<AssignmentData>({
  id: '',
  title: '',
  courseId: '',
  courseName: '',
  teacherName: '',
  description: '',
  status: '',
  publishTime: '',
  deadline: '',
  difficulty: 0,
  estimatedTime: 0,
  questionCount: 0,
  totalPoints: 0,
  requirements: [],
  submissionTypes: [],
  formatRequirements: [],
  gradingCriteria: [],
  references: [],
  isSubmitted: false
})

// 判断是否紧急（距离截止时间不到48小时）
const isUrgent = computed(() => {
  if (assignment.value.isSubmitted) return false
  
  const now = new Date().getTime()
  const deadlineTime = new Date(assignment.value.deadline).getTime()
  const timeLeft = deadlineTime - now
  
  return timeLeft > 0 && timeLeft < 48 * 60 * 60 * 1000
})

// 判断是否已过期
const isPastDeadline = computed(() => {
  const now = new Date().getTime()
  const deadlineTime = new Date(assignment.value.deadline).getTime()
  
  return now > deadlineTime
})

// 获取状态类型
const getStatusType = (status: string): string => {
  switch (status) {
    case 'notStarted': return 'info'
    case 'inProgress': return 'warning'
    default: return 'info'
  }
}

// 获取状态文本
const getStatusLabel = (status: string): string => {
  switch (status) {
    case 'notStarted': return '未开始'
    case 'inProgress': return '进行中'
    default: return '未知状态'
  }
}

// 获取截止时间文本
const getDeadlineText = (): string => {
  const now = new Date().getTime()
  const deadlineTime = new Date(assignment.value.deadline).getTime()
  const timeLeft = deadlineTime - now
  
  if (timeLeft < 0) {
    return `已过期：${assignment.value.deadline}`
  } else if (timeLeft < 24 * 60 * 60 * 1000) {
    const hoursLeft = Math.floor(timeLeft / (60 * 60 * 1000))
    return `剩余 ${hoursLeft} 小时`
  } else {
    const daysLeft = Math.floor(timeLeft / (24 * 60 * 60 * 1000))
    return `剩余 ${daysLeft} 天`
  }
}

// 获取预计完成时间文本
const getEstimatedTimeText = (minutes: number): string => {
  if (minutes < 60) {
    return `约${minutes}分钟`
  } else {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return `约${hours}小时${mins > 0 ? ` ${mins}分钟` : ''}`
  }
}

// 获取分数标签类型
const getScoreTagType = (score: number): string => {
  if (score >= 90) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 获取分数文本
const getScoreText = (score: number): string => {
  if (score >= 90) return `优秀: ${score}分`
  if (score >= 80) return `良好: ${score}分`
  if (score >= 60) return `及格: ${score}分`
  return `不及格: ${score}分`
}

// 获取参考资料操作文本
const getRefActionText = (type: string): string => {
  switch (type) {
    case 'pdf': return '查看文档'
    case 'video': return '观看视频'
    case 'link': return '访问链接'
    default: return '查看'
  }
}

// 获取作业详情
const fetchAssignmentDetail = async () => {
  try {
    setLoading(true)
    const response = await assignmentApi.getAssignmentDetail(assignmentId.value)
    
    if (response && response.code === 200 && response.data) {
      assignment.value = response.data
      console.log('获取作业详情成功:', assignment.value)
    } else {
      ElMessage.error('获取作业详情失败')
      console.error('获取作业详情失败:', response)
    }
  } catch (error) {
    ElMessage.error(`获取作业详情失败: ${error.message || '未知错误'}`)
    console.error('获取作业详情出错:', error)
  } finally {
    setLoading(false)
  }
}

// 开始作业
const startAssignment = () => {
  if (isPastDeadline.value) {
    ElMessage.warning('此作业已过截止日期，无法提交')
    return
  }
  
  ElMessageBox.confirm(
    `您即将开始《${assignment.value.title}》，预计需要 ${getEstimatedTimeText(assignment.value.estimatedTime)}。确认现在开始吗？`,
    '开始作业',
    {
      confirmButtonText: '开始作业',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    router.push(`/assignments/${assignmentId.value}/submit`)
  }).catch(() => {
    // 用户取消操作
  })
}

// 查看批改反馈
const viewFeedback = () => {
  router.push(`/assignments/${assignmentId.value}/feedback`)
}

// 打开参考资料
const openReference = (ref: Reference) => {
  window.open(ref.url, '_blank')
}

// 返回列表
const goBack = () => {
  router.push('/assignments')
}

// 生命周期钩子
onMounted(() => {
  fetchAssignmentDetail()
})
</script>

<style lang="scss" scoped>
.assignment-detail-view {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 10px;
      
      h1 {
        margin: 0;
        font-size: 24px;
        color: var(--el-text-color-primary);
      }
    }
  }
  
  .assignment-container {
    margin-bottom: 20px;
    
    .skeleton-container {
      padding: 20px;
      background-color: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      
      .el-skeleton-item {
        margin-bottom: 15px;
      }
    }
    
    .assignment-card {
      .assignment-status {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        .status-tags {
          display: flex;
          gap: 10px;
        }
        
        .deadline-info {
          display: flex;
          align-items: center;
          gap: 5px;
          color: var(--el-text-color-secondary);
          
          &.urgent-text {
            color: var(--el-color-danger);
            font-weight: 600;
          }
        }
      }
      
      .info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 15px;
        margin-bottom: 20px;
        
        .info-item {
          .label {
            font-size: 14px;
            color: var(--el-text-color-secondary);
            margin-bottom: 5px;
          }
          
          .value {
            font-size: 16px;
            font-weight: 500;
            color: var(--el-text-color-primary);
            
            &.course-name {
              color: var(--el-color-primary);
            }
            
            &.difficulty {
              display: flex;
              align-items: center;
            }
          }
        }
      }
      
      .description-section {
        margin-bottom: 20px;
        
        .description-text {
          line-height: 1.8;
          text-align: justify;
          color: var(--el-text-color-regular);
          margin-bottom: 20px;
        }
        
        .requirements-list {
          h3 {
            margin-top: 0;
            margin-bottom: 15px;
            font-size: 18px;
            color: var(--el-text-color-primary);
          }
          
          .requirement-content {
            color: var(--el-text-color-regular);
            line-height: 1.6;
            
            p {
              margin-top: 0;
              margin-bottom: 10px;
            }
            
            ul {
              padding-left: 20px;
              margin: 0;
              
              li {
                margin-bottom: 5px;
              }
            }
          }
        }
      }
      
      .submission-section {
        margin-bottom: 20px;
        
        h3 {
          margin-top: 0;
          margin-bottom: 15px;
          font-size: 18px;
          color: var(--el-text-color-primary);
        }
        
        .submission-types {
          margin-bottom: 20px;
          
          .submission-type-list {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            
            .submission-type-item {
              display: flex;
              align-items: center;
              gap: 5px;
              padding: 8px 15px;
              border-radius: 4px;
              background-color: var(--el-fill-color-light);
              color: var(--el-text-color-regular);
              
              .el-icon {
                color: var(--el-color-primary);
              }
            }
          }
        }
        
        .submission-format {
          margin-bottom: 20px;
          
          ul {
            padding-left: 20px;
            margin: 0;
            
            li {
              margin-bottom: 10px;
              color: var(--el-text-color-regular);
              line-height: 1.5;
            }
          }
        }
        
        .grading-criteria {
          margin-top: 20px;
        }
      }
      
      .reference-section {
        .reference-list {
          display: flex;
          flex-direction: column;
          gap: 15px;
          
          .reference-item {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 15px;
            border-radius: 6px;
            background-color: var(--el-fill-color-light);
            
            .ref-type {
              font-size: 24px;
              color: var(--el-color-primary);
            }
            
            .ref-content {
              flex: 1;
              
              .ref-title {
                font-weight: 600;
                margin-bottom: 5px;
                color: var(--el-text-color-primary);
              }
              
              .ref-description {
                font-size: 14px;
                color: var(--el-text-color-secondary);
              }
            }
          }
        }
      }
    }
  }
  
  .action-footer {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 30px;
  }
}

@media (max-width: 768px) {
  .assignment-detail-view {
    padding: 15px;
    
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 15px;
      
      .header-left {
        width: 100%;
        
        h1 {
          font-size: 20px;
        }
      }
      
      .header-actions {
        width: 100%;
        display: flex;
        justify-content: flex-end;
      }
    }
    
    .assignment-card {
      .assignment-status {
        flex-direction: column;
        align-items: flex-start;
        gap: 10px;
      }
      
      .info-grid {
        grid-template-columns: 1fr !important;
      }
      
      .reference-item {
        flex-direction: column;
        align-items: flex-start !important;
        
        .ref-action {
          width: 100%;
          margin-top: 10px;
          
          .el-button {
            width: 100%;
          }
        }
      }
    }
  }
}
</style> 