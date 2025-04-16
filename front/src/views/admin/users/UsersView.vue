<template>
  <div class="users-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/admin/users/add')">
          添加用户
        </el-button>
      </div>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="用户名">
          <el-input v-model="filterForm.username" placeholder="搜索用户名" clearable />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="filterForm.email" placeholder="搜索邮箱" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="filterForm.role" placeholder="选择角色" clearable>
            <el-option label="学生" value="学生" />
            <el-option label="教师" value="教师" />
            <el-option label="管理员" value="管理员" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table v-loading="loading" :data="userList" style="width: 100%" border>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="用户信息" min-width="250">
          <template #default="scope">
            <div class="user-info">
              <el-avatar :size="40" :src="scope.row.avatar">
                {{ scope.row.name ? scope.row.name.charAt(0).toUpperCase() : 'U' }}
              </el-avatar>
              <div class="user-details">
                <div class="user-name">{{ scope.row.name }}</div>
                <div class="user-email">{{ scope.row.email }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag
              :type="getRoleTagType(scope.row.role)"
              effect="plain"
            >
              {{ scope.row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="联系方式" min-width="180">
          <template #default="scope">
            <div v-if="scope.row.phone">{{ scope.row.phone }}</div>
            <div v-else class="empty-value">未设置</div>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="上次登录" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.lastLoginAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
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

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑用户" width="600px">
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="用户名" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="editForm.role" placeholder="选择角色">
            <el-option label="学生" value="学生" />
            <el-option label="教师" value="教师" />
            <el-option label="管理员" value="管理员" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="department" v-if="editForm.role !== '管理员'">
          <el-input v-model="editForm.department" />
        </el-form-item>
        <el-form-item label="专业" prop="major" v-if="editForm.role === '学生'">
          <el-input v-model="editForm.major" />
        </el-form-item>
        <el-form-item label="年级" prop="grade" v-if="editForm.role === '学生'">
          <el-input v-model="editForm.grade" />
        </el-form-item>
        <el-form-item label="学号" prop="studentId" v-if="editForm.role === '学生'">
          <el-input v-model="editForm.studentId" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { ExtendedUserInfo } from '@/types/api'

// 加载状态
const loading = ref(false)

// 用户列表数据
const userList = ref<ExtendedUserInfo[]>([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 筛选表单
const filterForm = reactive({
  username: '',
  email: '',
  role: ''
})

// 编辑表单
const editFormRef = ref<FormInstance>()
const editDialogVisible = ref(false)
const editForm = reactive<Partial<ExtendedUserInfo>>({
  id: '',
  name: '',
  email: '',
  phone: '',
  role: '',
  department: '',
  major: '',
  grade: '',
  studentId: ''
})

// 编辑表单规则
const editRules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})

// 获取角色标签类型
const getRoleTagType = (role: string) => {
  switch (role) {
    case '管理员':
      return 'danger'
    case '教师':
      return 'warning'
    case '学生':
      return 'success'
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

// 搜索用户
const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

// 重置筛选条件
const resetFilter = () => {
  filterForm.username = ''
  filterForm.email = ''
  filterForm.role = ''
  handleSearch()
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchUserList()
}

// 当前页变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchUserList()
}

// 编辑用户
const handleEdit = (user: ExtendedUserInfo) => {
  // 复制用户数据到编辑表单
  Object.assign(editForm, user)
  editDialogVisible.value = true
}

// 删除用户
const handleDelete = (user: ExtendedUserInfo) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${user.name}" 吗？此操作不可恢复。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(() => {
      deleteUser(user.id)
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

// 提交编辑表单
const submitEditForm = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateUser(editForm as ExtendedUserInfo)
        editDialogVisible.value = false
        ElMessage.success('用户信息更新成功')
        fetchUserList()
      } catch (error) {
        ElMessage.error('更新用户信息失败')
      }
    }
  })
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据
    const mockUsers: ExtendedUserInfo[] = [
      {
        id: '1',
        name: '张三',
        avatar: '',
        role: '学生',
        email: 'zhangsan@example.com',
        phone: '13800138000',
        studentId: '2020001',
        department: '计算机科学与技术学院',
        major: '软件工程',
        grade: '2020级',
        createdAt: '2022-09-01T08:00:00Z',
        lastLoginAt: '2023-03-15T10:30:00Z'
      },
      {
        id: '2',
        name: '李四',
        avatar: '',
        role: '教师',
        email: 'lisi@example.com',
        phone: '13900139000',
        department: '计算机科学与技术学院',
        createdAt: '2022-08-15T08:00:00Z',
        lastLoginAt: '2023-03-14T14:20:00Z'
      },
      {
        id: '3',
        name: '王五',
        avatar: '',
        role: '学生',
        email: 'wangwu@example.com',
        phone: '13700137000',
        studentId: '2020002',
        department: '计算机科学与技术学院',
        major: '计算机科学与技术',
        grade: '2020级',
        createdAt: '2022-09-02T10:00:00Z',
        lastLoginAt: '2023-03-13T09:15:00Z'
      },
      {
        id: '4',
        name: '赵六',
        avatar: '',
        role: '管理员',
        email: 'zhaoliu@example.com',
        phone: '13600136000',
        createdAt: '2022-07-01T08:00:00Z',
        lastLoginAt: '2023-03-15T16:45:00Z'
      }
    ]
    
    // 根据筛选条件过滤
    const filteredUsers = mockUsers.filter(user => {
      if (filterForm.username && !user.name.includes(filterForm.username)) {
        return false
      }
      if (filterForm.email && !user.email.includes(filterForm.email)) {
        return false
      }
      if (filterForm.role && user.role !== filterForm.role) {
        return false
      }
      return true
    })
    
    total.value = filteredUsers.length
    
    // 分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    userList.value = filteredUsers.slice(start, end)
    
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 更新用户
const updateUser = async (user: ExtendedUserInfo): Promise<void> => {
  // 模拟API调用
  await new Promise(resolve => setTimeout(resolve, 500))
  
  // 在实际应用中，这里会调用API更新用户
  console.log('更新用户:', user)
  
  // 模拟成功响应
  return Promise.resolve()
}

// 删除用户
const deleteUser = async (userId: string): Promise<void> => {
  // 模拟API调用
  await new Promise(resolve => setTimeout(resolve, 500))
  
  // 在实际应用中，这里会调用API删除用户
  console.log('删除用户ID:', userId)
  
  // 更新列表
  userList.value = userList.value.filter(user => user.id !== userId)
  total.value--
  
  ElMessage.success('用户删除成功')
}

// 初始化加载
onMounted(() => {
  fetchUserList()
})
</script>

<style scoped lang="scss">
.users-container {
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
  
  .user-info {
    display: flex;
    align-items: center;
    
    .user-details {
      margin-left: 10px;
      
      .user-name {
        font-weight: 500;
      }
      
      .user-email {
        font-size: 12px;
        color: #909399;
      }
    }
  }
  
  .empty-value {
    color: #909399;
    font-style: italic;
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 