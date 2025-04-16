<template>
  <div class="students-manage">
    <div class="page-header">
      <h2>学生管理</h2>
      <div class="header-actions">
        <el-select v-model="selectedCourse" placeholder="选择课程" @change="handleCourseChange" style="width: 200px; margin-right: 10px;">
          <el-option
            v-for="course in courses"
            :key="course.id"
            :label="course.title"
            :value="course.id"
          ></el-option>
        </el-select>
        <el-button type="primary" @click="showImportStudentsDialog">导入学生</el-button>
      </div>
    </div>

    <el-card v-if="selectedCourse" class="course-info-card">
      <div class="course-info">
        <h3>{{ currentCourse?.title }}</h3>
        <div class="course-stats">
          <div class="stat-item">
            <div class="stat-label">学生总数</div>
            <div class="stat-value">{{ students.length }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">平均成绩</div>
            <div class="stat-value">{{ averageGrade }}分</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">作业完成率</div>
            <div class="stat-value">{{ assignmentCompletionRate }}%</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">考试参与率</div>
            <div class="stat-value">{{ examParticipationRate }}%</div>
          </div>
        </div>
      </div>
    </el-card>

    <div class="table-actions" v-if="selectedCourse">
      <el-input
        v-model="searchQuery"
        placeholder="搜索学生姓名或学号"
        style="width: 300px; margin-right: 10px;"
        clearable
        @clear="handleSearchClear"
      >
        <template #prefix>
          <el-icon><search /></el-icon>
        </template>
      </el-input>
      
      <el-button-group>
        <el-button @click="exportStudentList">导出名单</el-button>
        <el-button @click="showSendMessageDialog">发送通知</el-button>
        <el-button @click="showCreateGroupDialog">创建分组</el-button>
      </el-button-group>
    </div>

    <el-table 
      v-if="selectedCourse"
      :data="filteredStudents" 
      style="width: 100%" 
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="name" label="姓名" min-width="120">
        <template #default="scope">
          <el-link type="primary" @click="viewStudentDetail(scope.row.id)">{{ scope.row.name }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
      <el-table-column prop="department" label="院系" width="180"></el-table-column>
      <el-table-column prop="assignmentCompletion" label="作业完成率" width="120">
        <template #default="scope">
          <el-progress 
            :percentage="scope.row.assignmentCompletion" 
            :color="getProgressColor(scope.row.assignmentCompletion)"
          ></el-progress>
        </template>
      </el-table-column>
      <el-table-column prop="examParticipation" label="考试参与率" width="120">
        <template #default="scope">
          <el-progress 
            :percentage="scope.row.examParticipation" 
            :color="getProgressColor(scope.row.examParticipation)"
          ></el-progress>
        </template>
      </el-table-column>
      <el-table-column prop="averageGrade" label="平均成绩" width="100">
        <template #default="scope">
          <span :class="getGradeClass(scope.row.averageGrade)">{{ scope.row.averageGrade }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="lastActive" label="最近活动" width="120"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="viewStudentDetail(scope.row.id)">详情</el-button>
          <el-dropdown trigger="click" @command="(command) => handleCommand(command, scope.row)">
            <el-button size="small">
              更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="assignments">查看作业</el-dropdown-item>
                <el-dropdown-item command="exams">查看考试</el-dropdown-item>
                <el-dropdown-item command="message">发送消息</el-dropdown-item>
                <el-dropdown-item command="remove" divided>移出课程</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-else description="请选择一个课程查看学生名单"></el-empty>

    <!-- 导入学生对话框 -->
    <el-dialog 
      title="导入学生" 
      v-model="importStudentsDialogVisible"
      width="40%"
    >
      <el-form :model="importForm" label-position="top">
        <el-form-item label="选择课程" prop="courseId" v-if="!selectedCourse">
          <el-select v-model="importForm.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.title"
              :value="course.id"
            ></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="导入方式">
          <el-radio-group v-model="importForm.importType">
            <el-radio label="file">从文件导入</el-radio>
            <el-radio label="manual">手动输入</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <template v-if="importForm.importType === 'file'">
          <el-form-item label="上传Excel文件">
            <el-upload
              action="/api/upload/student-list"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              accept=".xlsx,.xls,.csv"
            >
              <el-button type="primary">点击上传</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  请上传Excel或CSV文件，文件应包含学生姓名、学号等信息。
                  <el-link type="primary" @click="downloadTemplate">下载模板</el-link>
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </template>
        
        <template v-else>
          <el-form-item label="学生信息">
            <el-input
              v-model="importForm.manualInput"
              type="textarea"
              :rows="8"
              placeholder="请输入学生信息，每行一条记录，格式：姓名,学号,院系,邮箱"
            ></el-input>
          </el-form-item>
        </template>
        
        <el-form-item label="导入选项">
          <el-checkbox v-model="importForm.sendNotification">导入后发送通知给学生</el-checkbox>
          <el-checkbox v-model="importForm.skipExisting">跳过已存在的学生</el-checkbox>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importStudentsDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="importStudents">导入</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 发送通知对话框 -->
    <el-dialog 
      title="发送通知" 
      v-model="sendMessageDialogVisible"
      width="50%"
    >
      <el-form :model="messageForm" label-position="top">
        <el-form-item label="接收者">
          <div v-if="selectedStudents.length > 0">
            已选择 {{ selectedStudents.length }} 名学生
          </div>
          <div v-else>
            全部学生 ({{ students.length }} 人)
          </div>
        </el-form-item>
        
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="messageForm.title" placeholder="请输入通知标题"></el-input>
        </el-form-item>
        
        <el-form-item label="通知内容" prop="content">
          <el-input
            v-model="messageForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入通知内容"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="发送方式">
          <el-checkbox-group v-model="messageForm.sendMethods">
            <el-checkbox label="system">系统通知</el-checkbox>
            <el-checkbox label="email">邮件</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="sendMessageDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="sendMessage">发送</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 创建分组对话框 -->
    <el-dialog 
      title="创建学生分组" 
      v-model="createGroupDialogVisible"
      width="50%"
    >
      <el-form :model="groupForm" label-position="top">
        <el-form-item label="分组名称" prop="name">
          <el-input v-model="groupForm.name" placeholder="请输入分组名称"></el-input>
        </el-form-item>
        
        <el-form-item label="分组方式">
          <el-radio-group v-model="groupForm.groupingMethod">
            <el-radio label="manual">手动分组</el-radio>
            <el-radio label="random">随机分组</el-radio>
            <el-radio label="balanced">均衡分组</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <template v-if="groupForm.groupingMethod === 'random' || groupForm.groupingMethod === 'balanced'">
          <el-form-item label="分组数量" prop="groupCount">
            <el-input-number v-model="groupForm.groupCount" :min="2" :max="10"></el-input-number>
          </el-form-item>
        </template>
        
        <template v-if="groupForm.groupingMethod === 'manual'">
          <el-form-item label="选择学生">
            <el-transfer
              v-model="groupForm.selectedStudents"
              :data="transferData"
              :titles="['可选学生', '已选学生']"
              :button-texts="['移除', '添加']"
              :format="{
                noChecked: '${total}',
                hasChecked: '${checked}/${total}'
              }"
            ></el-transfer>
          </el-form-item>
        </template>
        
        <el-form-item label="分组说明" prop="description">
          <el-input
            v-model="groupForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分组说明"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createGroupDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createGroup">创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, Search } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const selectedCourse = ref('')
const searchQuery = ref('')
const selectedStudents = ref([])
const importStudentsDialogVisible = ref(false)
const sendMessageDialogVisible = ref(false)
const createGroupDialogVisible = ref(false)

// 导入表单
const importForm = reactive({
  courseId: '',
  importType: 'file',
  manualInput: '',
  sendNotification: true,
  skipExisting: true,
  fileList: []
})

// 消息表单
const messageForm = reactive({
  title: '',
  content: '',
  sendMethods: ['system']
})

// 分组表单
const groupForm = reactive({
  name: '',
  groupingMethod: 'manual',
  groupCount: 4,
  selectedStudents: [],
  description: ''
})

// 模拟课程数据
const courses = ref([
  { id: 1, title: '数据结构与算法' },
  { id: 2, title: '机器学习基础' },
  { id: 3, title: '高等数学' },
  { id: 4, title: '操作系统原理' }
])

// 模拟学生数据
const students = ref([
  {
    id: 1,
    name: '张三',
    studentId: '2023001',
    department: '计算机科学与技术学院',
    assignmentCompletion: 85,
    examParticipation: 100,
    averageGrade: 92,
    lastActive: '2023-04-10'
  },
  {
    id: 2,
    name: '李四',
    studentId: '2023002',
    department: '计算机科学与技术学院',
    assignmentCompletion: 70,
    examParticipation: 100,
    averageGrade: 85,
    lastActive: '2023-04-09'
  },
  {
    id: 3,
    name: '王五',
    studentId: '2023003',
    department: '计算机科学与技术学院',
    assignmentCompletion: 100,
    examParticipation: 100,
    averageGrade: 95,
    lastActive: '2023-04-11'
  },
  {
    id: 4,
    name: '赵六',
    studentId: '2023004',
    department: '计算机科学与技术学院',
    assignmentCompletion: 60,
    examParticipation: 50,
    averageGrade: 75,
    lastActive: '2023-04-05'
  },
  {
    id: 5,
    name: '钱七',
    studentId: '2023005',
    department: '计算机科学与技术学院',
    assignmentCompletion: 90,
    examParticipation: 100,
    averageGrade: 88,
    lastActive: '2023-04-10'
  }
])

// 当前选中的课程
const currentCourse = computed(() => {
  return courses.value.find(course => course.id === selectedCourse.value)
})

// 根据搜索条件过滤学生
const filteredStudents = computed(() => {
  if (!searchQuery.value) return students.value
  
  const query = searchQuery.value.toLowerCase()
  return students.value.filter(student => 
    student.name.toLowerCase().includes(query) || 
    student.studentId.toLowerCase().includes(query)
  )
})

// 计算平均成绩
const averageGrade = computed(() => {
  if (students.value.length === 0) return 0
  
  const sum = students.value.reduce((acc, student) => acc + student.averageGrade, 0)
  return Math.round(sum / students.value.length)
})

// 计算作业完成率
const assignmentCompletionRate = computed(() => {
  if (students.value.length === 0) return 0
  
  const sum = students.value.reduce((acc, student) => acc + student.assignmentCompletion, 0)
  return Math.round(sum / students.value.length)
})

// 计算考试参与率
const examParticipationRate = computed(() => {
  if (students.value.length === 0) return 0
  
  const sum = students.value.reduce((acc, student) => acc + student.examParticipation, 0)
  return Math.round(sum / students.value.length)
})

// 转换为穿梭框数据格式
const transferData = computed(() => {
  return students.value.map(student => ({
    key: student.id,
    label: `${student.name} (${student.studentId})`
  }))
})

// 获取进度条颜色
const getProgressColor = (percentage: number) => {
  if (percentage < 60) return '#F56C6C'
  if (percentage < 80) return '#E6A23C'
  return '#67C23A'
}

// 获取成绩样式类
const getGradeClass = (grade: number) => {
  if (grade < 60) return 'grade-fail'
  if (grade < 80) return 'grade-pass'
  if (grade < 90) return 'grade-good'
  return 'grade-excellent'
}

// 处理课程变更
const handleCourseChange = (courseId: number) => {
  // 这里应该调用API获取选中课程的学生列表
  // fetchStudents(courseId)
  
  // 重置搜索和选择
  searchQuery.value = ''
  selectedStudents.value = []
}

// 处理搜索清除
const handleSearchClear = () => {
  searchQuery.value = ''
}

// 处理表格选择变更
const handleSelectionChange = (selection) => {
  selectedStudents.value = selection
}

// 查看学生详情
const viewStudentDetail = (studentId: number) => {
  router.push(`/teacher/students/${studentId}?courseId=${selectedCourse.value}`)
}

// 处理下拉菜单命令
const handleCommand = (command: string, student: any) => {
  switch (command) {
    case 'assignments':
      router.push(`/teacher/students/${student.id}/assignments?courseId=${selectedCourse.value}`)
      break
    case 'exams':
      router.push(`/teacher/students/${student.id}/exams?courseId=${selectedCourse.value}`)
      break
    case 'message':
      showSendMessageToStudent(student)
      break
    case 'remove':
      removeStudentFromCourse(student)
      break
  }
}

// 显示导入学生对话框
const showImportStudentsDialog = () => {
  importForm.courseId = selectedCourse.value || ''
  importStudentsDialogVisible.value = true
}

// 显示发送通知对话框
const showSendMessageDialog = () => {
  messageForm.title = ''
  messageForm.content = ''
  messageForm.sendMethods = ['system']
  sendMessageDialogVisible.value = true
}

// 显示发送通知给单个学生对话框
const showSendMessageToStudent = (student: any) => {
  messageForm.title = ''
  messageForm.content = ''
  messageForm.sendMethods = ['system']
  selectedStudents.value = [student]
  sendMessageDialogVisible.value = true
}

// 显示创建分组对话框
const showCreateGroupDialog = () => {
  groupForm.name = ''
  groupForm.groupingMethod = 'manual'
  groupForm.groupCount = 4
  groupForm.selectedStudents = []
  groupForm.description = ''
  createGroupDialogVisible.value = true
}

// 导出学生名单
const exportStudentList = () => {
  ElMessage.success('学生名单导出成功')
}

// 下载导入模板
const downloadTemplate = () => {
  // 这里应该调用API下载模板
  ElMessage.success('模板下载成功')
}

// 上传前检查
const beforeUpload = (file: File) => {
  const isExcel = file.type === 'application/vnd.ms-excel' || 
                 file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                 file.name.endsWith('.csv')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isExcel) {
    ElMessage.error('只能上传Excel或CSV文件!')
  }
  if (!isLt2M) {
    ElMessage.error('文件大小不能超过2MB!')
  }
  return isExcel && isLt2M
}

// 上传成功回调
const handleUploadSuccess = (response: any) => {
  ElMessage.success('文件上传成功')
  importForm.fileList = [response]
}

// 上传失败回调
const handleUploadError = () => {
  ElMessage.error('文件上传失败')
}

// 导入学生
const importStudents = async () => {
  try {
    // 这里应该调用API导入学生
    // await api.importStudents({
    //   courseId: importForm.courseId || selectedCourse.value,
    //   importType: importForm.importType,
    //   manualInput: importForm.manualInput,
    //   fileList: importForm.fileList,
    //   sendNotification: importForm.sendNotification,
    //   skipExisting: importForm.skipExisting
    // })
    
    ElMessage.success('学生导入成功')
    importStudentsDialogVisible.value = false
    
    // 刷新学生列表
    fetchStudents(selectedCourse.value)
  } catch (error) {
    console.error('导入学生失败:', error)
    ElMessage.error('导入失败，请稍后重试')
  }
}

// 发送通知
const sendMessage = async () => {
  try {
    // 这里应该调用API发送通知
    // await api.sendMessage({
    //   courseId: selectedCourse.value,
    //   studentIds: selectedStudents.value.length > 0 
    //     ? selectedStudents.value.map(student => student.id)
    //     : students.value.map(student => student.id),
    //   title: messageForm.title,
    //   content: messageForm.content,
    //   sendMethods: messageForm.sendMethods
    // })
    
    ElMessage.success('通知发送成功')
    sendMessageDialogVisible.value = false
    selectedStudents.value = []
  } catch (error) {
    console.error('发送通知失败:', error)
    ElMessage.error('发送失败，请稍后重试')
  }
}

// 创建分组
const createGroup = async () => {
  try {
    // 这里应该调用API创建分组
    // await api.createStudentGroup({
    //   courseId: selectedCourse.value,
    //   name: groupForm.name,
    //   groupingMethod: groupForm.groupingMethod,
    //   groupCount: groupForm.groupCount,
    //   selectedStudents: groupForm.selectedStudents,
    //   description: groupForm.description
    // })
    
    ElMessage.success('学生分组创建成功')
    createGroupDialogVisible.value = false
  } catch (error) {
    console.error('创建分组失败:', error)
    ElMessage.error('创建失败，请稍后重试')
  }
}

// 从课程中移除学生
const removeStudentFromCourse = (student: any) => {
  ElMessageBox.confirm(
    `确定要将学生"${student.name}"从课程中移除吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 这里应该调用API移除学生
      // await api.removeStudentFromCourse({
      //   courseId: selectedCourse.value,
      //   studentId: student.id
      // })
      
      ElMessage.success('学生已从课程中移除')
      
      // 刷新学生列表
      fetchStudents(selectedCourse.value)
    } catch (error) {
      console.error('移除学生失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }).catch(() => {
    // 取消移除
  })
}

// 获取学生列表
const fetchStudents = async (courseId: number) => {
  if (!courseId) return
  
  loading.value = true
  try {
    // 这里应该调用API获取学生列表
    // const response = await api.getCourseStudents(courseId)
    // students.value = response.data
    
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 500))
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败，请刷新页面重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 如果有默认选中的课程，则加载学生列表
  if (selectedCourse.value) {
    fetchStudents(selectedCourse.value)
  }
})
</script>

<style scoped lang="scss">
.students-manage {
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
    
    .header-actions {
      display: flex;
      align-items: center;
    }
  }
  
  .course-info-card {
    margin-bottom: 20px;
    
    .course-info {
      h3 {
        margin: 0 0 15px;
        font-size: 18px;
      }
      
      .course-stats {
        display: flex;
        justify-content: space-between;
        
        .stat-item {
          text-align: center;
          padding: 0 15px;
          
          .stat-label {
            font-size: 14px;
            color: #606266;
            margin-bottom: 5px;
          }
          
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
          }
        }
      }
    }
  }
  
  .table-actions {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
  }
  
  .grade-fail {
    color: #F56C6C;
  }
  
  .grade-pass {
    color: #E6A23C;
  }
  
  .grade-good {
    color: #67C23A;
  }
  
  .grade-excellent {
    color: #409EFF;
    font-weight: bold;
  }
}
</style>
