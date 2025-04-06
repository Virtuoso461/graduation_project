<template>
  <div class="submit-assignment-view">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="confirmBack" text>
          <el-icon><ArrowLeft /></el-icon>返回作业详情
        </el-button>
        <h1>{{ assignment.title }}</h1>
      </div>
      <div class="header-actions">
        <el-tag :type="remainingTimeType" effect="dark">
          {{ remainingTimeText }}
        </el-tag>
      </div>
    </div>

    <div class="content-container">
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
          <div class="assignment-content">
            <el-card class="requirement-card">
              <template #header>
                <div class="card-header">
                  <h3>作业要求</h3>
                  <el-tag size="small">{{ assignment.courseName }}</el-tag>
                </div>
              </template>
              
              <div class="description-text">
                {{ assignment.description }}
              </div>
              
              <div v-if="assignment.requirements && assignment.requirements.length > 0" class="requirements">
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
              
              <div class="submission-format">
                <h4>提交要求</h4>
                <ul>
                  <li v-for="(format, index) in assignment.formatRequirements" :key="index">
                    {{ format }}
                  </li>
                </ul>
              </div>
            </el-card>
            
            <el-card class="submission-card">
              <template #header>
                <div class="card-header">
                  <h3>提交作业</h3>
                  <div class="progress-info">
                    <span>完成度：</span>
                    <el-progress 
                      :percentage="completionPercentage" 
                      :status="completionPercentage === 100 ? 'success' : ''"
                    ></el-progress>
                  </div>
                </div>
              </template>
              
              <el-form 
                ref="submissionForm"
                :model="submissionData"
                :rules="rules"
                label-position="top"
                size="large"
              >
                <el-divider content-position="left">文本答案</el-divider>
                
                <div v-for="(question, index) in assignment.textQuestions" :key="`text-${index}`" class="question-item">
                  <el-form-item :label="question.title" :prop="`textAnswers.${index}`">
                    <el-input
                      v-if="question.type === 'short'"
                      v-model="submissionData.textAnswers[index]"
                      type="text"
                      placeholder="请输入您的答案"
                    ></el-input>
                    
                    <el-input
                      v-else
                      v-model="submissionData.textAnswers[index]"
                      type="textarea"
                      :rows="6"
                      placeholder="请输入您的答案"
                    ></el-input>
                    
                    <div class="tips" v-if="question.tips">
                      <el-icon><InfoFilled /></el-icon>
                      <span>{{ question.tips }}</span>
                    </div>
                  </el-form-item>
                </div>
                
                <el-divider content-position="left">文件上传</el-divider>
                
                <div v-for="(fileReq, index) in assignment.fileRequirements" :key="`file-${index}`" class="file-upload-item">
                  <el-form-item :label="fileReq.name" :prop="`fileUploads.${index}`">
                    <el-upload
                      v-model:file-list="submissionData.fileUploads[index]"
                      action="#"
                      :auto-upload="false"
                      :accept="fileReq.accept"
                      :limit="fileReq.limit || 1"
                      :multiple="fileReq.limit > 1"
                      class="upload-container"
                    >
                      <template #trigger>
                        <el-button type="primary">
                          <el-icon><Upload /></el-icon>选择文件
                        </el-button>
                      </template>
                      
                      <template #tip>
                        <div class="el-upload__tip">
                          {{ fileReq.description }}
                          <span v-if="fileReq.required" class="required">(必需)</span>
                        </div>
                      </template>
                    </el-upload>
                  </el-form-item>
                </div>
                
                <el-divider v-if="assignment.codeQuestions && assignment.codeQuestions.length > 0" content-position="left">代码编辑</el-divider>
                
                <div v-for="(codeQ, index) in assignment.codeQuestions" :key="`code-${index}`" class="code-editor-item">
                  <el-form-item :label="codeQ.title" :prop="`codeAnswers.${index}`">
                    <div class="code-editor-header">
                      <span class="language-label">{{ codeQ.language }}</span>
                      <el-button 
                        v-if="codeQ.hasTemplate" 
                        type="info" 
                        text 
                        @click="resetCodeTemplate(index)"
                      >
                        重置模板
                      </el-button>
                    </div>
                    
                    <div class="code-editor-container">
                      <textarea
                        v-model="submissionData.codeAnswers[index]"
                        class="code-editor"
                        placeholder="请在此编写代码..."
                        spellcheck="false"
                      ></textarea>
                    </div>
                    
                    <div class="code-requirements" v-if="codeQ.description">
                      <p>{{ codeQ.description }}</p>
                    </div>
                  </el-form-item>
                </div>
                
                <el-divider content-position="left">附加说明</el-divider>
                
                <el-form-item label="给老师的留言（可选）" prop="comments">
                  <el-input
                    v-model="submissionData.comments"
                    type="textarea"
                    :rows="4"
                    placeholder="如有需要，请在此处添加补充说明或问题..."
                  ></el-input>
                </el-form-item>
                
                <div class="form-actions">
                  <el-button @click="saveAsDraft">保存草稿</el-button>
                  <el-button type="primary" @click="submitAssignment" :disabled="isSubmitting">
                    <el-icon v-if="isSubmitting"><Loading /></el-icon>
                    <span>{{ isSubmitting ? '提交中...' : '提交作业' }}</span>
                  </el-button>
                </div>
              </el-form>
            </el-card>
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <el-dialog
      v-model="showSubmitConfirmation"
      title="确认提交"
      width="500px"
    >
      <div class="submit-confirmation">
        <el-alert
          v-if="!isAllComplete"
          title="您有未完成的部分"
          type="warning"
          :closable="false"
          show-icon
        >
          <div class="incomplete-warning">
            提交前请确认您已完成所有必要的部分。
          </div>
        </el-alert>
        
        <p class="confirm-text">确定要提交作业吗？提交后将无法修改。</p>
        
        <div class="submission-summary">
          <h4>提交摘要</h4>
          <ul class="summary-list">
            <li>
              <el-icon><Document /></el-icon>
              <span>文本答案：{{ completedTextCount }}/{{ assignment.textQuestions.length }}题</span>
            </li>
            <li>
              <el-icon><Upload /></el-icon>
              <span>上传文件：{{ completedFileCount }}/{{ assignment.fileRequirements.length }}个</span>
            </li>
            <li v-if="assignment.codeQuestions && assignment.codeQuestions.length > 0">
              <el-icon><Edit /></el-icon>
              <span>代码问题：{{ completedCodeCount }}/{{ assignment.codeQuestions.length }}题</span>
            </li>
          </ul>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSubmitConfirmation = false">取消</el-button>
          <el-button type="primary" @click="confirmSubmit" :loading="isSubmitting">
            确认提交
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <el-dialog
      v-model="showBackConfirmation"
      title="确认离开"
      width="400px"
    >
      <div class="leave-confirmation">
        <p>您有未保存的更改，离开页面将导致这些更改丢失。</p>
        <p>是否保存为草稿后离开？</p>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showBackConfirmation = false">取消</el-button>
          <el-button @click="leaveWithoutSaving">直接离开</el-button>
          <el-button type="primary" @click="saveAndLeave">保存并离开</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, InfoFilled, Upload, 
  Loading, Document, Edit
} from '@element-plus/icons-vue'
// 后续可引入API
// import { assignmentApi } from '@/utils/http/api'

interface TextQuestion {
  title: string;
  type: 'short' | 'long';
  required: boolean;
  tips?: string;
}

interface FileRequirement {
  name: string;
  description: string;
  accept: string;
  limit?: number;
  required: boolean;
}

interface CodeQuestion {
  title: string;
  language: string;
  description?: string;
  template?: string;
  hasTemplate: boolean;
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
  description: string;
  deadline: string;
  requirements: Requirement[];
  formatRequirements: string[];
  textQuestions: TextQuestion[];
  fileRequirements: FileRequirement[];
  codeQuestions?: CodeQuestion[];
}

interface SubmissionData {
  textAnswers: string[];
  fileUploads: any[][];
  codeAnswers: string[];
  comments: string;
}

const route = useRoute()
const router = useRouter()
const assignmentId = computed(() => route.params.id as string)
const isLoading = ref(true)
const isSubmitting = ref(false)
const submissionForm = ref(null)
const showSubmitConfirmation = ref(false)
const showBackConfirmation = ref(false)
const hasChanges = ref(false)

// 模拟作业数据
const assignment = ref<AssignmentData>({
  id: '',
  title: '',
  courseId: '',
  courseName: '',
  description: '',
  deadline: '',
  requirements: [],
  formatRequirements: [],
  textQuestions: [],
  fileRequirements: [],
  codeQuestions: []
})

// 提交数据
const submissionData = reactive<SubmissionData>({
  textAnswers: [],
  fileUploads: [],
  codeAnswers: [],
  comments: ''
})

// 表单验证规则
const rules = reactive({
  // 这里可以根据需要添加验证规则
})

// 计算剩余时间
const remainingTime = computed(() => {
  const now = new Date().getTime()
  const deadlineTime = new Date(assignment.value.deadline).getTime()
  return deadlineTime - now
})

// 剩余时间文本
const remainingTimeText = computed(() => {
  if (remainingTime.value <= 0) {
    return '已截止'
  }
  
  const hours = Math.floor(remainingTime.value / (1000 * 60 * 60))
  const minutes = Math.floor((remainingTime.value % (1000 * 60 * 60)) / (1000 * 60))
  
  if (hours > 24) {
    const days = Math.floor(hours / 24)
    return `剩余 ${days} 天 ${hours % 24} 小时`
  }
  
  return `剩余 ${hours} 小时 ${minutes} 分钟`
})

// 剩余时间类型
const remainingTimeType = computed(() => {
  if (remainingTime.value <= 0) {
    return 'danger'
  } else if (remainingTime.value < 24 * 60 * 60 * 1000) {
    return 'warning'
  } else {
    return 'info'
  }
})

// 已完成的文本题目数量
const completedTextCount = computed(() => {
  return submissionData.textAnswers.filter(answer => answer && answer.trim() !== '').length
})

// 已完成的文件上传数量
const completedFileCount = computed(() => {
  return submissionData.fileUploads.filter(files => files && files.length > 0).length
})

// 已完成的代码题目数量
const completedCodeCount = computed(() => {
  return submissionData.codeAnswers.filter(code => code && code.trim() !== '').length
})

// 总完成度百分比
const completionPercentage = computed(() => {
  const totalRequired = assignment.value.textQuestions.filter(q => q.required).length
    + assignment.value.fileRequirements.filter(f => f.required).length
    + (assignment.value.codeQuestions ? assignment.value.codeQuestions.length : 0)
  
  if (totalRequired === 0) return 100
  
  const completedRequired = completedTextCount.value + completedFileCount.value + completedCodeCount.value
  return Math.min(100, Math.round((completedRequired / totalRequired) * 100))
})

// 是否所有必填项都已完成
const isAllComplete = computed(() => {
  return completionPercentage.value === 100
})

// 加载作业详情
const loadAssignmentDetail = async () => {
  try {
    isLoading.value = true
    
    // 模拟加载延迟
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 后续使用API请求数据
    // const response = await assignmentApi.getAssignmentForSubmission(assignmentId.value)
    // assignment.value = response
    
    // 模拟作业数据
    assignment.value = {
      id: assignmentId.value,
      title: '二叉树遍历算法实现',
      courseId: '1',
      courseName: '数据结构与算法',
      description: '本次作业要求实现二叉树的前序、中序和后序遍历算法，并分析各种遍历算法的时间复杂度和空间复杂度。',
      deadline: new Date(new Date().getTime() + 3 * 24 * 60 * 60 * 1000).toLocaleString(),
      requirements: [
        {
          title: '算法实现',
          content: '使用C/C++/Java/Python等编程语言实现以下二叉树遍历算法:',
          items: [
            '前序遍历（递归方式和非递归方式）',
            '中序遍历（递归方式和非递归方式）',
            '后序遍历（递归方式和非递归方式）',
            '层序遍历'
          ]
        },
        {
          title: '复杂度分析',
          content: '对每种遍历算法进行时间复杂度和空间复杂度分析，并比较递归实现和非递归实现的差异。',
          items: []
        }
      ],
      formatRequirements: [
        '所有源代码文件需包含必要的注释，说明函数功能、参数和返回值。',
        '分析报告应包含算法原理、实现思路、复杂度分析和测试结果。',
        '所有文件打包为一个zip文件提交，命名格式为"学号_姓名_二叉树遍历"。'
      ],
      textQuestions: [
        {
          title: '1. 请简要描述二叉树的三种遍历方式的区别和应用场景',
          type: 'long',
          required: true,
          tips: '可以结合具体的应用场景，例如表达式树的求值等'
        },
        {
          title: '2. 分析递归与非递归实现在空间复杂度上的差异',
          type: 'long',
          required: true
        }
      ],
      fileRequirements: [
        {
          name: '源代码文件',
          description: '上传包含遍历算法实现的源代码文件（.c/.cpp/.java/.py）',
          accept: '.c,.cpp,.java,.py',
          limit: 5,
          required: true
        },
        {
          name: '算法分析报告',
          description: '上传分析报告PDF文件',
          accept: '.pdf',
          limit: 1,
          required: true
        },
        {
          name: '测试结果截图',
          description: '上传测试结果的截图',
          accept: '.png,.jpg,.jpeg',
          limit: 3,
          required: false
        }
      ],
      codeQuestions: [
        {
          title: '3. 实现二叉树的前序遍历（递归方式）',
          language: 'Java',
          description: '完成下面的代码，实现二叉树的前序遍历',
          template: `public void preOrderTraversal(TreeNode root) {
  // 在此处实现前序遍历算法
  
}`,
          hasTemplate: true
        },
        {
          title: '4. 实现二叉树的层序遍历',
          language: 'Java',
          description: '使用队列实现二叉树的层序遍历',
          hasTemplate: false
        }
      ]
    }
    
    // 初始化提交数据结构
    initSubmissionData()
    
    // 加载草稿（如果有）
    loadDraft()
    
  } catch (error) {
    console.error('加载作业详情失败:', error)
    ElMessage.error('加载作业详情失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

// 初始化提交数据结构
const initSubmissionData = () => {
  // 初始化文本答案数组
  submissionData.textAnswers = new Array(assignment.value.textQuestions.length).fill('')
  
  // 初始化文件上传数组
  submissionData.fileUploads = new Array(assignment.value.fileRequirements.length).fill([])
  
  // 初始化代码答案数组
  if (assignment.value.codeQuestions) {
    submissionData.codeAnswers = assignment.value.codeQuestions.map(q => q.template || '')
  } else {
    submissionData.codeAnswers = []
  }
}

// 加载草稿
const loadDraft = () => {
  try {
    const draftKey = `assignment_draft_${assignmentId.value}`
    const savedDraft = localStorage.getItem(draftKey)
    
    if (savedDraft) {
      const draftData = JSON.parse(savedDraft)
      
      // 恢复文本答案
      if (draftData.textAnswers) {
        draftData.textAnswers.forEach((answer, index) => {
          if (index < submissionData.textAnswers.length) {
            submissionData.textAnswers[index] = answer
          }
        })
      }
      
      // 文件无法从草稿恢复，需要重新上传
      
      // 恢复代码答案
      if (draftData.codeAnswers) {
        draftData.codeAnswers.forEach((code, index) => {
          if (index < submissionData.codeAnswers.length) {
            submissionData.codeAnswers[index] = code
          }
        })
      }
      
      // 恢复留言
      if (draftData.comments) {
        submissionData.comments = draftData.comments
      }
      
      ElMessage.success('已恢复上次的草稿')
    }
  } catch (error) {
    console.error('加载草稿失败:', error)
  }
}

// 保存草稿
const saveAsDraft = () => {
  try {
    const draftKey = `assignment_draft_${assignmentId.value}`
    const draftData = {
      textAnswers: submissionData.textAnswers,
      codeAnswers: submissionData.codeAnswers,
      comments: submissionData.comments,
      timestamp: new Date().toISOString()
    }
    
    localStorage.setItem(draftKey, JSON.stringify(draftData))
    ElMessage.success('草稿保存成功')
    hasChanges.value = false
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存草稿失败')
  }
}

// 重置代码模板
const resetCodeTemplate = (index: number) => {
  if (assignment.value.codeQuestions && assignment.value.codeQuestions[index]) {
    submissionData.codeAnswers[index] = assignment.value.codeQuestions[index].template || ''
  }
}

// 提交作业
const submitAssignment = () => {
  showSubmitConfirmation.value = true
}

// 确认提交
const confirmSubmit = async () => {
  try {
    isSubmitting.value = true
    
    // 模拟提交延迟
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    // 后续使用API提交作业
    // await assignmentApi.submitAssignment(assignmentId.value, submissionData)
    
    // 清除草稿
    const draftKey = `assignment_draft_${assignmentId.value}`
    localStorage.removeItem(draftKey)
    
    ElMessage.success('作业提交成功')
    
    // 跳转到提交成功页面或作业详情
    router.push(`/assignments/${assignmentId.value}/feedback`)
  } catch (error) {
    console.error('提交作业失败:', error)
    ElMessage.error('提交作业失败，请稍后重试')
  } finally {
    isSubmitting.value = false
    showSubmitConfirmation.value = false
  }
}

// 确认返回
const confirmBack = () => {
  if (hasChanges.value) {
    showBackConfirmation.value = true
  } else {
    goBack()
  }
}

// 不保存直接离开
const leaveWithoutSaving = () => {
  hasChanges.value = false
  goBack()
}

// 保存并离开
const saveAndLeave = () => {
  saveAsDraft()
  goBack()
}

// 返回
const goBack = () => {
  router.push(`/assignments/${assignmentId.value}`)
}

// 监听变化
const startChangeTracking = () => {
  // 这里简化处理，实际应用中可能需要更复杂的脏检查
  setTimeout(() => {
    hasChanges.value = true
  }, 5000)
}

// 生命周期钩子
onMounted(() => {
  loadAssignmentDetail()
  startChangeTracking()
})

// 离开前确认
onBeforeUnmount(() => {
  if (hasChanges.value) {
    saveAsDraft()
  }
})
</script>

<style lang="scss" scoped>
.submit-assignment-view {
  padding: 20px;
  
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
  
  .content-container {
    margin-bottom: 30px;
    
    .skeleton-container {
      padding: 20px;
      background-color: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      
      .el-skeleton-item {
        margin-bottom: 15px;
      }
    }
    
    .assignment-content {
      display: grid;
      grid-template-columns: 1fr 2fr;
      gap: 20px;
      
      .requirement-card {
        position: sticky;
        top: 20px;
        align-self: start;
        max-height: calc(100vh - 40px);
        overflow-y: auto;
        
        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          h3 {
            margin: 0;
            font-size: 18px;
          }
        }
        
        .description-text {
          margin-bottom: 20px;
          color: var(--el-text-color-regular);
          line-height: 1.6;
        }
        
        .requirements {
          margin-bottom: 20px;
          
          .requirement-content {
            color: var(--el-text-color-regular);
            line-height: 1.5;
            
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
        
        .submission-format {
          h4 {
            margin-top: 0;
            margin-bottom: 10px;
            font-size: 16px;
            color: var(--el-text-color-primary);
          }
          
          ul {
            padding-left: 20px;
            margin: 0;
            
            li {
              margin-bottom: 8px;
              color: var(--el-text-color-regular);
              line-height: 1.5;
            }
          }
        }
      }
      
      .submission-card {
        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          h3 {
            margin: 0;
            font-size: 18px;
          }
          
          .progress-info {
            display: flex;
            align-items: center;
            gap: 10px;
            width: 200px;
            
            span {
              white-space: nowrap;
              color: var(--el-text-color-secondary);
            }
          }
        }
        
        .question-item {
          margin-bottom: 25px;
          
          .tips {
            display: flex;
            align-items: flex-start;
            gap: 5px;
            margin-top: 5px;
            color: var(--el-color-info);
            font-size: 14px;
            
            .el-icon {
              margin-top: 2px;
            }
          }
        }
        
        .file-upload-item {
          margin-bottom: 25px;
          
          .el-upload__tip {
            line-height: 1.5;
            
            .required {
              color: var(--el-color-danger);
              font-weight: bold;
            }
          }
        }
        
        .code-editor-item {
          margin-bottom: 25px;
          
          .code-editor-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 5px;
            
            .language-label {
              color: var(--el-color-primary);
              font-size: 14px;
              font-weight: 500;
            }
          }
          
          .code-editor-container {
            border: 1px solid var(--el-border-color);
            border-radius: 4px;
            
            .code-editor {
              width: 100%;
              min-height: 200px;
              padding: 15px;
              font-family: 'Consolas', 'Monaco', monospace;
              font-size: 14px;
              line-height: 1.5;
              color: var(--el-text-color-primary);
              background: #f8f9fa;
              border: none;
              outline: none;
              resize: vertical;
            }
          }
          
          .code-requirements {
            margin-top: 10px;
            color: var(--el-text-color-secondary);
            font-size: 14px;
            line-height: 1.5;
            
            p {
              margin: 0;
            }
          }
        }
        
        .form-actions {
          display: flex;
          justify-content: flex-end;
          gap: 15px;
          margin-top: 30px;
        }
      }
    }
  }
  
  .submit-confirmation {
    .incomplete-warning {
      margin-top: 5px;
      font-size: 14px;
    }
    
    .confirm-text {
      margin: 20px 0;
      font-size: 16px;
      text-align: center;
      color: var(--el-text-color-primary);
    }
    
    .submission-summary {
      background-color: var(--el-fill-color-light);
      padding: 15px;
      border-radius: 6px;
      
      h4 {
        margin-top: 0;
        margin-bottom: 15px;
        font-size: 16px;
        color: var(--el-text-color-primary);
      }
      
      .summary-list {
        list-style: none;
        padding: 0;
        margin: 0;
        
        li {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-bottom: 10px;
          color: var(--el-text-color-regular);
          
          .el-icon {
            color: var(--el-color-primary);
          }
        }
      }
    }
  }
}

@media (max-width: 1000px) {
  .submit-assignment-view {
    .assignment-content {
      grid-template-columns: 1fr !important;
      
      .requirement-card {
        position: relative !important;
        top: 0 !important;
        max-height: none !important;
      }
    }
  }
}

@media (max-width: 768px) {
  .submit-assignment-view {
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
      }
    }
    
    .card-header {
      flex-direction: column;
      align-items: flex-start !important;
      gap: 10px;
      
      .progress-info {
        width: 100% !important;
      }
    }
  }
}
</style> 