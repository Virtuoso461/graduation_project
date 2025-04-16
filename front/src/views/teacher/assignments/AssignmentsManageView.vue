<template>
  <div class="assignments-manage">
    <div class="page-header">
      <h2>作业管理</h2>
      <el-button type="primary" @click="showCreateAssignmentDialog">创建新作业</el-button>
    </div>

    <el-tabs v-model="activeTab" class="assignment-tabs">
      <el-tab-pane label="全部作业" name="all">
        <el-table :data="assignments" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="作业标题" min-width="180">
            <template #default="scope">
              <el-link type="primary" @click="viewAssignmentDetail(scope.row.id)">{{ scope.row.title }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="course" label="所属课程" width="180"></el-table-column>
          <el-table-column prop="dueDate" label="截止日期" width="120"></el-table-column>
          <el-table-column prop="submissionCount" label="提交数/总数" width="120">
            <template #default="scope">
              {{ scope.row.submissionCount }}/{{ scope.row.totalStudents }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="editAssignment(scope.row)">编辑</el-button>
              <el-button size="small" type="primary" @click="gradeAssignments(scope.row.id)">批改</el-button>
              <el-dropdown trigger="click" @command="(command) => handleCommand(command, scope.row)">
                <el-button size="small">
                  更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="extend">延长截止日期</el-dropdown-item>
                    <el-dropdown-item command="publish" v-if="scope.row.status === '草稿'">发布作业</el-dropdown-item>
                    <el-dropdown-item command="close" v-if="scope.row.status === '进行中'">关闭提交</el-dropdown-item>
                    <el-dropdown-item command="reopen" v-if="scope.row.status === '已关闭'">重新开放</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除作业</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="待批改" name="pending">
        <el-table :data="pendingAssignments" style="width: 100%" v-loading="loading">
          <!-- 表格内容与全部作业相同 -->
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="进行中" name="active">
        <el-table :data="activeAssignments" style="width: 100%" v-loading="loading">
          <!-- 表格内容与全部作业相同 -->
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="已结束" name="ended">
        <el-table :data="endedAssignments" style="width: 100%" v-loading="loading">
          <!-- 表格内容与全部作业相同 -->
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 创建/编辑作业对话框 -->
    <el-dialog 
      :title="dialogType === 'create' ? '创建新作业' : '编辑作业'" 
      v-model="assignmentDialogVisible"
      width="60%"
    >
      <el-form :model="assignmentForm" label-position="top" :rules="assignmentRules" ref="assignmentFormRef">
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="assignmentForm.title" placeholder="请输入作业标题"></el-input>
        </el-form-item>
        
        <el-form-item label="所属课程" prop="courseId">
          <el-select v-model="assignmentForm.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.title"
              :value="course.id"
            ></el-option>
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="assignmentForm.startDate"
                type="datetime"
                placeholder="选择开始日期"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期" prop="dueDate">
              <el-date-picker
                v-model="assignmentForm.dueDate"
                type="datetime"
                placeholder="选择截止日期"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总分" prop="totalPoints">
              <el-input-number v-model="assignmentForm.totalPoints" :min="0" :max="100" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="允许迟交" prop="allowLateSubmission">
              <el-switch v-model="assignmentForm.allowLateSubmission"></el-switch>
              <span v-if="assignmentForm.allowLateSubmission" style="margin-left: 10px;">
                扣分比例:
                <el-input-number 
                  v-model="assignmentForm.latePenaltyPercentage" 
                  :min="0" 
                  :max="100"
                  :step="5"
                  size="small"
                  style="width: 80px;"
                ></el-input-number>%
              </span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="作业说明" prop="description">
          <el-input
            v-model="assignmentForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入作业说明"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="附件">
          <el-upload
            action="/api/upload/assignment-attachment"
            :file-list="assignmentForm.attachments"
            :on-success="handleAttachmentSuccess"
            :on-remove="handleAttachmentRemove"
            multiple
          >
            <el-button type="primary">上传附件</el-button>
            <template #tip>
              <div class="el-upload__tip">可上传任意类型文件，单个文件不超过10MB</div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="作业状态" prop="status">
          <el-radio-group v-model="assignmentForm.status">
            <el-radio label="draft">保存为草稿</el-radio>
            <el-radio label="published">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignmentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAssignment">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 延长截止日期对话框 -->
    <el-dialog 
      title="延长截止日期" 
      v-model="extendDueDateDialogVisible"
      width="30%"
    >
      <el-form :model="extendDueDateForm" label-position="top">
        <el-form-item label="当前截止日期">
          <div>{{ formatDateTime(currentAssignment.dueDate) }}</div>
        </el-form-item>
        
        <el-form-item label="新截止日期" prop="newDueDate">
          <el-date-picker
            v-model="extendDueDateForm.newDueDate"
            type="datetime"
            placeholder="选择新截止日期"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item label="通知学生" prop="notifyStudents">
          <el-switch v-model="extendDueDateForm.notifyStudents"></el-switch>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="extendDueDateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmExtendDueDate">确认延长</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const activeTab = ref('all')
const assignmentDialogVisible = ref(false)
const extendDueDateDialogVisible = ref(false)
const dialogType = ref('create')
const assignmentFormRef = ref<FormInstance>()
const currentAssignment = ref({} as any)

// 作业表单
const assignmentForm = reactive({
  id: '',
  title: '',
  courseId: '',
  startDate: '',
  dueDate: '',
  totalPoints: 100,
  allowLateSubmission: false,
  latePenaltyPercentage: 10,
  description: '',
  attachments: [],
  status: 'draft'
})

// 延长截止日期表单
const extendDueDateForm = reactive({
  newDueDate: '',
  notifyStudents: true
})

// 表单验证规则
const assignmentRules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入作业标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  dueDate: [
    { required: true, message: '请选择截止日期', trigger: 'change' }
  ],
  totalPoints: [
    { required: true, message: '请输入总分', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入作业说明', trigger: 'blur' }
  ]
})

// 模拟课程数据
const courses = ref([
  { id: 1, title: '数据结构与算法' },
  { id: 2, title: '机器学习基础' },
  { id: 3, title: '高等数学' },
  { id: 4, title: '操作系统原理' }
])

// 模拟作业数据
const assignments = ref([
  {
    id: 1,
    title: '数据结构作业一：链表实现',
    course: '数据结构与算法',
    courseId: 1,
    startDate: '2023-04-01T00:00:00',
    dueDate: '2023-04-15T23:59:59',
    submissionCount: 35,
    totalStudents: 45,
    status: '进行中',
    totalPoints: 100,
    description: '请实现单链表、双链表和循环链表的基本操作。'
  },
  {
    id: 2,
    title: '机器学习作业：线性回归',
    course: '机器学习基础',
    courseId: 2,
    startDate: '2023-04-05T00:00:00',
    dueDate: '2023-04-20T23:59:59',
    submissionCount: 28,
    totalStudents: 38,
    status: '进行中',
    totalPoints: 100,
    description: '使用线性回归算法对给定数据集进行预测。'
  },
  {
    id: 3,
    title: '高等数学期末复习',
    course: '高等数学',
    courseId: 3,
    startDate: '2023-01-01T00:00:00',
    dueDate: '2023-01-10T23:59:59',
    submissionCount: 48,
    totalStudents: 52,
    status: '已结束',
    totalPoints: 100,
    description: '完成教材后的复习题。'
  },
  {
    id: 4,
    title: '操作系统实验：进程调度',
    course: '操作系统原理',
    courseId: 4,
    startDate: '',
    dueDate: '',
    submissionCount: 0,
    totalStudents: 0,
    status: '草稿',
    totalPoints: 100,
    description: '实现简单的进程调度算法。'
  }
])

// 根据状态筛选作业
const pendingAssignments = computed(() => 
  assignments.value.filter(assignment => 
    assignment.status === '进行中' && assignment.submissionCount > 0
  )
)
const activeAssignments = computed(() => assignments.value.filter(assignment => assignment.status === '进行中'))
const endedAssignments = computed(() => assignments.value.filter(assignment => assignment.status === '已结束'))

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case '进行中':
      return 'success'
    case '已结束':
      return 'info'
    case '草稿':
      return 'warning'
    case '已关闭':
      return 'danger'
    default:
      return 'info'
  }
}

// 格式化日期时间
const formatDateTime = (dateTimeStr: string) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 显示创建作业对话框
const showCreateAssignmentDialog = () => {
  dialogType.value = 'create'
  resetAssignmentForm()
  assignmentDialogVisible.value = true
}

// 编辑作业
const editAssignment = (assignment) => {
  dialogType.value = 'edit'
  Object.assign(assignmentForm, {
    id: assignment.id,
    title: assignment.title,
    courseId: assignment.courseId,
    startDate: assignment.startDate,
    dueDate: assignment.dueDate,
    totalPoints: assignment.totalPoints,
    allowLateSubmission: false, // 假设值
    latePenaltyPercentage: 10, // 假设值
    description: assignment.description,
    attachments: [], // 假设没有附件
    status: assignment.status === '草稿' ? 'draft' : 'published'
  })
  assignmentDialogVisible.value = true
}

// 重置作业表单
const resetAssignmentForm = () => {
  assignmentForm.id = ''
  assignmentForm.title = ''
  assignmentForm.courseId = ''
  assignmentForm.startDate = ''
  assignmentForm.dueDate = ''
  assignmentForm.totalPoints = 100
  assignmentForm.allowLateSubmission = false
  assignmentForm.latePenaltyPercentage = 10
  assignmentForm.description = ''
  assignmentForm.attachments = []
  assignmentForm.status = 'draft'
  
  if (assignmentFormRef.value) {
    assignmentFormRef.value.resetFields()
  }
}

// 保存作业
const saveAssignment = async () => {
  if (!assignmentFormRef.value) return
  
  await assignmentFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用API保存作业
        // const response = await api.saveAssignment(assignmentForm)
        
        ElMessage.success(dialogType.value === 'create' ? '作业创建成功' : '作业更新成功')
        assignmentDialogVisible.value = false
        
        // 刷新作业列表
        fetchAssignments()
      } catch (error) {
        console.error('保存作业失败:', error)
        ElMessage.error('保存失败，请稍后重试')
      }
    } else {
      return false
    }
  })
}

// 附件上传成功的回调
const handleAttachmentSuccess = (response: any, file: any) => {
  assignmentForm.attachments.push({
    name: file.name,
    url: response.url
  })
  ElMessage.success('附件上传成功')
}

// 附件移除的回调
const handleAttachmentRemove = (file: any) => {
  const index = assignmentForm.attachments.findIndex(item => item.url === file.url)
  if (index !== -1) {
    assignmentForm.attachments.splice(index, 1)
  }
}

// 查看作业详情
const viewAssignmentDetail = (assignmentId: number) => {
  router.push(`/teacher/assignments/${assignmentId}`)
}

// 批改作业
const gradeAssignments = (assignmentId: number) => {
  router.push(`/teacher/assignments/${assignmentId}/grade`)
}

// 处理下拉菜单命令
const handleCommand = (command: string, assignment: any) => {
  switch (command) {
    case 'extend':
      showExtendDueDateDialog(assignment)
      break
    case 'publish':
      publishAssignment(assignment)
      break
    case 'close':
      closeAssignment(assignment)
      break
    case 'reopen':
      reopenAssignment(assignment)
      break
    case 'delete':
      deleteAssignment(assignment)
      break
  }
}

// 显示延长截止日期对话框
const showExtendDueDateDialog = (assignment: any) => {
  currentAssignment.value = assignment
  extendDueDateForm.newDueDate = new Date(assignment.dueDate)
  // 默认延长一周
  extendDueDateForm.newDueDate.setDate(extendDueDateForm.newDueDate.getDate() + 7)
  extendDueDateForm.notifyStudents = true
  extendDueDateDialogVisible.value = true
}

// 确认延长截止日期
const confirmExtendDueDate = async () => {
  try {
    // 这里应该调用API延长截止日期
    // await api.extendAssignmentDueDate({
    //   assignmentId: currentAssignment.value.id,
    //   newDueDate: extendDueDateForm.newDueDate,
    //   notifyStudents: extendDueDateForm.notifyStudents
    // })
    
    ElMessage.success('截止日期延长成功')
    extendDueDateDialogVisible.value = false
    
    // 刷新作业列表
    fetchAssignments()
  } catch (error) {
    console.error('延长截止日期失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 发布作业
const publishAssignment = (assignment: any) => {
  ElMessageBox.confirm(
    `确定要发布作业"${assignment.title}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      // 这里应该调用API发布作业
      // await api.publishAssignment(assignment.id)
      
      ElMessage.success('作业发布成功')
      
      // 刷新作业列表
      fetchAssignments()
    } catch (error) {
      console.error('发布作业失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }).catch(() => {
    // 取消发布
  })
}

// 关闭作业提交
const closeAssignment = (assignment: any) => {
  ElMessageBox.confirm(
    `确定要关闭作业"${assignment.title}"的提交吗？学生将无法再提交此作业。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 这里应该调用API关闭作业提交
      // await api.closeAssignment(assignment.id)
      
      ElMessage.success('作业提交已关闭')
      
      // 刷新作业列表
      fetchAssignments()
    } catch (error) {
      console.error('关闭作业提交失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }).catch(() => {
    // 取消关闭
  })
}

// 重新开放作业提交
const reopenAssignment = (assignment: any) => {
  ElMessageBox.confirm(
    `确定要重新开放作业"${assignment.title}"的提交吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      // 这里应该调用API重新开放作业提交
      // await api.reopenAssignment(assignment.id)
      
      ElMessage.success('作业提交已重新开放')
      
      // 刷新作业列表
      fetchAssignments()
    } catch (error) {
      console.error('重新开放作业提交失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }).catch(() => {
    // 取消重新开放
  })
}

// 删除作业
const deleteAssignment = (assignment: any) => {
  ElMessageBox.confirm(
    `确定要删除作业"${assignment.title}"吗？此操作不可逆。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 这里应该调用API删除作业
      // await api.deleteAssignment(assignment.id)
      
      ElMessage.success('作业删除成功')
      
      // 刷新作业列表
      fetchAssignments()
    } catch (error) {
      console.error('删除作业失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 获取作业列表
const fetchAssignments = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取作业列表
    // const response = await api.getTeacherAssignments()
    // assignments.value = response.data
    
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 500))
  } catch (error) {
    console.error('获取作业列表失败:', error)
    ElMessage.error('获取作业列表失败，请刷新页面重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchAssignments()
})
</script>

<style scoped lang="scss">
.assignments-manage {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
    }
  }
  
  .assignment-tabs {
    margin-bottom: 20px;
  }
}
</style>
