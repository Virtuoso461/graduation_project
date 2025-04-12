<template>
  <div class="exams-manage">
    <div class="page-header">
      <h2>考试管理</h2>
      <el-button type="primary" @click="showCreateExamDialog">创建新考试</el-button>
    </div>

    <el-tabs v-model="activeTab" class="exam-tabs">
      <el-tab-pane label="全部考试" name="all">
        <el-table :data="exams" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="考试标题" min-width="180">
            <template #default="scope">
              <el-link type="primary" @click="viewExamDetail(scope.row.id)">{{ scope.row.title }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="course" label="所属课程" width="180"></el-table-column>
          <el-table-column prop="examDate" label="考试日期" width="120"></el-table-column>
          <el-table-column prop="duration" label="时长(分钟)" width="100"></el-table-column>
          <el-table-column prop="participantCount" label="参与人数/总数" width="120">
            <template #default="scope">
              {{ scope.row.participantCount }}/{{ scope.row.totalStudents }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="editExam(scope.row)">编辑</el-button>
              <el-button size="small" type="primary" @click="gradeExams(scope.row.id)">批改</el-button>
              <el-dropdown trigger="click" @command="(command) => handleCommand(command, scope.row)">
                <el-button size="small">
                  更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="questions">管理题目</el-dropdown-item>
                    <el-dropdown-item command="results">查看成绩</el-dropdown-item>
                    <el-dropdown-item command="publish" v-if="scope.row.status === '草稿'">发布考试</el-dropdown-item>
                    <el-dropdown-item command="close" v-if="scope.row.status === '进行中'">结束考试</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除考试</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="待批改" name="pending">
        <el-table :data="pendingExams" style="width: 100%" v-loading="loading">
          <!-- 表格内容与全部考试相同 -->
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="进行中" name="active">
        <el-table :data="activeExams" style="width: 100%" v-loading="loading">
          <!-- 表格内容与全部考试相同 -->
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="已结束" name="ended">
        <el-table :data="endedExams" style="width: 100%" v-loading="loading">
          <!-- 表格内容与全部考试相同 -->
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 创建/编辑考试对话框 -->
    <el-dialog 
      :title="dialogType === 'create' ? '创建新考试' : '编辑考试'" 
      v-model="examDialogVisible"
      width="60%"
    >
      <el-form :model="examForm" label-position="top" :rules="examRules" ref="examFormRef">
        <el-form-item label="考试标题" prop="title">
          <el-input v-model="examForm.title" placeholder="请输入考试标题"></el-input>
        </el-form-item>
        
        <el-form-item label="所属课程" prop="courseId">
          <el-select v-model="examForm.courseId" placeholder="请选择课程" style="width: 100%">
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
            <el-form-item label="考试日期" prop="examDate">
              <el-date-picker
                v-model="examForm.examDate"
                type="date"
                placeholder="选择考试日期"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考试时间" prop="examTime">
              <el-time-picker
                v-model="examForm.examTime"
                format="HH:mm"
                placeholder="选择考试时间"
                style="width: 100%"
              ></el-time-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试时长(分钟)" prop="duration">
              <el-input-number v-model="examForm.duration" :min="10" :max="240" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总分" prop="totalPoints">
              <el-input-number v-model="examForm.totalPoints" :min="0" :max="100" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="考试说明" prop="description">
          <el-input
            v-model="examForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入考试说明"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="考试设置">
          <el-checkbox v-model="examForm.randomizeQuestions">随机题目顺序</el-checkbox>
          <el-checkbox v-model="examForm.preventCheating">开启防作弊模式</el-checkbox>
          <el-checkbox v-model="examForm.showResultsAfterSubmission">提交后立即显示成绩</el-checkbox>
        </el-form-item>
        
        <el-form-item label="考试状态" prop="status">
          <el-radio-group v-model="examForm.status">
            <el-radio label="draft">保存为草稿</el-radio>
            <el-radio label="published">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="examDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveExam">保存</el-button>
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
const examDialogVisible = ref(false)
const dialogType = ref('create')
const examFormRef = ref<FormInstance>()

// 考试表单
const examForm = reactive({
  id: '',
  title: '',
  courseId: '',
  examDate: '',
  examTime: '',
  duration: 60,
  totalPoints: 100,
  description: '',
  randomizeQuestions: false,
  preventCheating: false,
  showResultsAfterSubmission: true,
  status: 'draft'
})

// 表单验证规则
const examRules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入考试标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'change' }
  ],
  examDate: [
    { required: true, message: '请选择考试日期', trigger: 'change' }
  ],
  examTime: [
    { required: true, message: '请选择考试时间', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入考试时长', trigger: 'blur' }
  ],
  totalPoints: [
    { required: true, message: '请输入总分', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入考试说明', trigger: 'blur' }
  ]
})

// 模拟课程数据
const courses = ref([
  { id: 1, title: '数据结构与算法' },
  { id: 2, title: '机器学习基础' },
  { id: 3, title: '高等数学' },
  { id: 4, title: '操作系统原理' }
])

// 模拟考试数据
const exams = ref([
  {
    id: 1,
    title: '数据结构期中考试',
    course: '数据结构与算法',
    courseId: 1,
    examDate: '2023-04-20',
    duration: 90,
    participantCount: 40,
    totalStudents: 45,
    status: '进行中',
    totalPoints: 100,
    description: '本次考试涵盖链表、栈、队列、树等数据结构的基本概念和操作。'
  },
  {
    id: 2,
    title: '机器学习算法测验',
    course: '机器学习基础',
    courseId: 2,
    examDate: '2023-04-25',
    duration: 60,
    participantCount: 0,
    totalStudents: 38,
    status: '未开始',
    totalPoints: 100,
    description: '本次测验主要考察线性回归、逻辑回归等基础算法的理解。'
  },
  {
    id: 3,
    title: '高等数学期末考试',
    course: '高等数学',
    courseId: 3,
    examDate: '2023-01-15',
    duration: 120,
    participantCount: 50,
    totalStudents: 52,
    status: '已结束',
    totalPoints: 100,
    description: '本次考试涵盖本学期所学的所有内容。'
  },
  {
    id: 4,
    title: '操作系统概念测验',
    course: '操作系统原理',
    courseId: 4,
    examDate: '',
    duration: 60,
    participantCount: 0,
    totalStudents: 0,
    status: '草稿',
    totalPoints: 100,
    description: '本次测验主要考察操作系统的基本概念和原理。'
  }
])

// 根据状态筛选考试
const pendingExams = computed(() => 
  exams.value.filter(exam => 
    exam.status === '已结束' && exam.participantCount > 0
  )
)
const activeExams = computed(() => exams.value.filter(exam => exam.status === '进行中' || exam.status === '未开始'))
const endedExams = computed(() => exams.value.filter(exam => exam.status === '已结束'))

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case '进行中':
      return 'success'
    case '未开始':
      return 'warning'
    case '已结束':
      return 'info'
    case '草稿':
      return 'warning'
    default:
      return 'info'
  }
}

// 显示创建考试对话框
const showCreateExamDialog = () => {
  dialogType.value = 'create'
  resetExamForm()
  examDialogVisible.value = true
}

// 编辑考试
const editExam = (exam) => {
  dialogType.value = 'edit'
  Object.assign(examForm, {
    id: exam.id,
    title: exam.title,
    courseId: exam.courseId,
    examDate: exam.examDate ? new Date(exam.examDate) : '',
    examTime: exam.examDate ? new Date(`${exam.examDate}T12:00:00`) : '', // 假设中午12点
    duration: exam.duration,
    totalPoints: exam.totalPoints,
    description: exam.description,
    randomizeQuestions: false, // 假设值
    preventCheating: false, // 假设值
    showResultsAfterSubmission: true, // 假设值
    status: exam.status === '草稿' ? 'draft' : 'published'
  })
  examDialogVisible.value = true
}

// 重置考试表单
const resetExamForm = () => {
  examForm.id = ''
  examForm.title = ''
  examForm.courseId = ''
  examForm.examDate = ''
  examForm.examTime = ''
  examForm.duration = 60
  examForm.totalPoints = 100
  examForm.description = ''
  examForm.randomizeQuestions = false
  examForm.preventCheating = false
  examForm.showResultsAfterSubmission = true
  examForm.status = 'draft'
  
  if (examFormRef.value) {
    examFormRef.value.resetFields()
  }
}

// 保存考试
const saveExam = async () => {
  if (!examFormRef.value) return
  
  await examFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用API保存考试
        // const response = await api.saveExam(examForm)
        
        ElMessage.success(dialogType.value === 'create' ? '考试创建成功' : '考试更新成功')
        examDialogVisible.value = false
        
        // 刷新考试列表
        fetchExams()
      } catch (error) {
        console.error('保存考试失败:', error)
        ElMessage.error('保存失败，请稍后重试')
      }
    } else {
      return false
    }
  })
}

// 查看考试详情
const viewExamDetail = (examId: number) => {
  router.push(`/teacher/exams/${examId}`)
}

// 批改考试
const gradeExams = (examId: number) => {
  router.push(`/teacher/exams/${examId}/grade`)
}

// 处理下拉菜单命令
const handleCommand = (command: string, exam: any) => {
  switch (command) {
    case 'questions':
      router.push(`/teacher/exams/${exam.id}/questions`)
      break
    case 'results':
      router.push(`/teacher/exams/${exam.id}/results`)
      break
    case 'publish':
      publishExam(exam)
      break
    case 'close':
      closeExam(exam)
      break
    case 'delete':
      deleteExam(exam)
      break
  }
}

// 发布考试
const publishExam = (exam: any) => {
  ElMessageBox.confirm(
    `确定要发布考试"${exam.title}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      // 这里应该调用API发布考试
      // await api.publishExam(exam.id)
      
      ElMessage.success('考试发布成功')
      
      // 刷新考试列表
      fetchExams()
    } catch (error) {
      console.error('发布考试失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }).catch(() => {
    // 取消发布
  })
}

// 结束考试
const closeExam = (exam: any) => {
  ElMessageBox.confirm(
    `确定要结束考试"${exam.title}"吗？学生将无法再参加此考试。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 这里应该调用API结束考试
      // await api.closeExam(exam.id)
      
      ElMessage.success('考试已结束')
      
      // 刷新考试列表
      fetchExams()
    } catch (error) {
      console.error('结束考试失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }).catch(() => {
    // 取消结束
  })
}

// 删除考试
const deleteExam = (exam: any) => {
  ElMessageBox.confirm(
    `确定要删除考试"${exam.title}"吗？此操作不可逆。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 这里应该调用API删除考试
      // await api.deleteExam(exam.id)
      
      ElMessage.success('考试删除成功')
      
      // 刷新考试列表
      fetchExams()
    } catch (error) {
      console.error('删除考试失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 获取考试列表
const fetchExams = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取考试列表
    // const response = await api.getTeacherExams()
    // exams.value = response.data
    
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 500))
  } catch (error) {
    console.error('获取考试列表失败:', error)
    ElMessage.error('获取考试列表失败，请刷新页面重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchExams()
})
</script>

<style scoped lang="scss">
.exams-manage {
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
  
  .exam-tabs {
    margin-bottom: 20px;
  }
}
</style>
