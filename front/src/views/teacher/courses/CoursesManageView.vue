<template>
  <div class="courses-manage">
    <div class="page-header">
      <h2>课程管理</h2>
      <el-button type="primary" @click="showCreateCourseDialog">创建新课程</el-button>
    </div>

    <el-tabs v-model="activeTab" class="course-tabs">
      <el-tab-pane label="全部课程" name="all">
        <el-table :data="courses" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="课程名称" min-width="180">
            <template #default="scope">
              <el-link type="primary" @click="viewCourseDetail(scope.row.id)">{{ scope.row.title }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="code" label="课程代码" width="120"></el-table-column>
          <el-table-column prop="category" label="分类" width="120"></el-table-column>
          <el-table-column prop="students" label="学生数" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="editCourse(scope.row)">编辑</el-button>
              <el-button size="small" type="primary" @click="manageCourse(scope.row.id)">管理</el-button>
              <el-dropdown trigger="click" @command="(command) => handleCommand(command, scope.row)">
                <el-button size="small">
                  更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="resources">教学资源</el-dropdown-item>
                    <el-dropdown-item command="assignments">作业管理</el-dropdown-item>
                    <el-dropdown-item command="exams">考试管理</el-dropdown-item>
                    <el-dropdown-item command="students">学生管理</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除课程</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="进行中" name="active">
        <el-table :data="activeCourses" style="width: 100%" v-loading="loading">
          <!-- 表格内容与全部课程相同 -->
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="已结束" name="ended">
        <el-table :data="endedCourses" style="width: 100%" v-loading="loading">
          <!-- 表格内容与全部课程相同 -->
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="草稿" name="draft">
        <el-table :data="draftCourses" style="width: 100%" v-loading="loading">
          <!-- 表格内容与全部课程相同 -->
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 创建/编辑课程对话框 -->
    <el-dialog 
      :title="dialogType === 'create' ? '创建新课程' : '编辑课程'" 
      v-model="courseDialogVisible"
      width="60%"
    >
      <el-form :model="courseForm" label-position="top" :rules="courseRules" ref="courseFormRef">
        <el-form-item label="课程名称" prop="title">
          <el-input v-model="courseForm.title" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        
        <el-form-item label="课程代码" prop="code">
          <el-input v-model="courseForm.code" placeholder="请输入课程代码"></el-input>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程分类" prop="category">
              <el-select v-model="courseForm.category" placeholder="请选择课程分类" style="width: 100%">
                <el-option
                  v-for="item in categories"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程学分" prop="credits">
              <el-input-number v-model="courseForm.credits" :min="0" :max="10" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="courseForm.startDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="courseForm.endDate"
                type="date"
                placeholder="选择结束日期"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="课程简介" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程简介"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="课程封面">
          <el-upload
            action="/api/upload/course-cover"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="courseForm.coverImage" :src="courseForm.coverImage" class="course-cover-preview">
            <el-button v-else type="primary">上传封面图片</el-button>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="课程状态" prop="status">
          <el-radio-group v-model="courseForm.status">
            <el-radio label="draft">保存为草稿</el-radio>
            <el-radio label="published">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="courseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCourse">保存</el-button>
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
const courseDialogVisible = ref(false)
const dialogType = ref('create')
const courseFormRef = ref<FormInstance>()

// 课程表单
const courseForm = reactive({
  id: '',
  title: '',
  code: '',
  category: '',
  credits: 3,
  startDate: '',
  endDate: '',
  description: '',
  coverImage: '',
  status: 'draft'
})

// 表单验证规则
const courseRules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入课程代码', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择课程分类', trigger: 'change' }
  ],
  credits: [
    { required: true, message: '请输入课程学分', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入课程简介', trigger: 'blur' }
  ]
})

// 课程分类选项
const categories = [
  { value: 'computer_science', label: '计算机科学' },
  { value: 'mathematics', label: '数学' },
  { value: 'physics', label: '物理' },
  { value: 'chemistry', label: '化学' },
  { value: 'biology', label: '生物' },
  { value: 'literature', label: '文学' },
  { value: 'history', label: '历史' },
  { value: 'philosophy', label: '哲学' },
  { value: 'economics', label: '经济学' },
  { value: 'management', label: '管理学' }
]

// 模拟课程数据
const courses = ref([
  {
    id: 1,
    title: '数据结构与算法',
    code: 'CS101',
    category: '计算机科学',
    students: 45,
    status: '进行中',
    startDate: '2023-03-01',
    endDate: '2023-06-30',
    description: '本课程介绍基本的数据结构和算法设计技术。'
  },
  {
    id: 2,
    title: '机器学习基础',
    code: 'CS201',
    category: '计算机科学',
    students: 38,
    status: '进行中',
    startDate: '2023-03-01',
    endDate: '2023-06-30',
    description: '本课程介绍机器学习的基本概念和算法。'
  },
  {
    id: 3,
    title: '高等数学',
    code: 'MATH101',
    category: '数学',
    students: 52,
    status: '已结束',
    startDate: '2022-09-01',
    endDate: '2023-01-15',
    description: '本课程介绍微积分、线性代数等高等数学知识。'
  },
  {
    id: 4,
    title: '操作系统原理',
    code: 'CS301',
    category: '计算机科学',
    students: 0,
    status: '草稿',
    startDate: '',
    endDate: '',
    description: '本课程介绍操作系统的基本原理和设计。'
  }
])

// 根据状态筛选课程
const activeCourses = computed(() => courses.value.filter(course => course.status === '进行中'))
const endedCourses = computed(() => courses.value.filter(course => course.status === '已结束'))
const draftCourses = computed(() => courses.value.filter(course => course.status === '草稿'))

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case '进行中':
      return 'success'
    case '已结束':
      return 'info'
    case '草稿':
      return 'warning'
    default:
      return 'info'
  }
}

// 显示创建课程对话框
const showCreateCourseDialog = () => {
  dialogType.value = 'create'
  resetCourseForm()
  courseDialogVisible.value = true
}

// 编辑课程
const editCourse = (course) => {
  dialogType.value = 'edit'
  Object.assign(courseForm, {
    id: course.id,
    title: course.title,
    code: course.code,
    category: course.category.toLowerCase().replace(' ', '_'),
    credits: 3, // 假设值
    startDate: course.startDate,
    endDate: course.endDate,
    description: course.description,
    coverImage: '', // 假设没有封面
    status: course.status === '草稿' ? 'draft' : 'published'
  })
  courseDialogVisible.value = true
}

// 重置课程表单
const resetCourseForm = () => {
  courseForm.id = ''
  courseForm.title = ''
  courseForm.code = ''
  courseForm.category = ''
  courseForm.credits = 3
  courseForm.startDate = ''
  courseForm.endDate = ''
  courseForm.description = ''
  courseForm.coverImage = ''
  courseForm.status = 'draft'
  
  if (courseFormRef.value) {
    courseFormRef.value.resetFields()
  }
}

// 保存课程
const saveCourse = async () => {
  if (!courseFormRef.value) return
  
  await courseFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用API保存课程
        // const response = await api.saveCourse(courseForm)
        
        ElMessage.success(dialogType.value === 'create' ? '课程创建成功' : '课程更新成功')
        courseDialogVisible.value = false
        
        // 刷新课程列表
        fetchCourses()
      } catch (error) {
        console.error('保存课程失败:', error)
        ElMessage.error('保存失败，请稍后重试')
      }
    } else {
      return false
    }
  })
}

// 封面上传前的校验
const beforeCoverUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('封面只能是图片格式!')
  }
  if (!isLt2M) {
    ElMessage.error('封面图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

// 封面上传成功的回调
const handleCoverSuccess = (response: any) => {
  courseForm.coverImage = response.url
  ElMessage.success('封面上传成功')
}

// 查看课程详情
const viewCourseDetail = (courseId: number) => {
  router.push(`/teacher/courses/${courseId}`)
}

// 管理课程
const manageCourse = (courseId: number) => {
  router.push(`/teacher/courses/${courseId}/manage`)
}

// 处理下拉菜单命令
const handleCommand = (command: string, course: any) => {
  switch (command) {
    case 'resources':
      router.push(`/teacher/courses/${course.id}/resources`)
      break
    case 'assignments':
      router.push(`/teacher/courses/${course.id}/assignments`)
      break
    case 'exams':
      router.push(`/teacher/courses/${course.id}/exams`)
      break
    case 'students':
      router.push(`/teacher/courses/${course.id}/students`)
      break
    case 'delete':
      deleteCourse(course)
      break
  }
}

// 删除课程
const deleteCourse = (course: any) => {
  ElMessageBox.confirm(
    `确定要删除课程"${course.title}"吗？此操作不可逆。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 这里应该调用API删除课程
      // await api.deleteCourse(course.id)
      
      ElMessage.success('课程删除成功')
      
      // 刷新课程列表
      fetchCourses()
    } catch (error) {
      console.error('删除课程失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取课程列表
    // const response = await api.getTeacherCourses()
    // courses.value = response.data
    
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 500))
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败，请刷新页面重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped lang="scss">
.courses-manage {
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
  
  .course-tabs {
    margin-bottom: 20px;
  }
  
  .course-cover-preview {
    width: 200px;
    height: 120px;
    object-fit: cover;
    border-radius: 4px;
    margin-bottom: 10px;
  }
}
</style>
