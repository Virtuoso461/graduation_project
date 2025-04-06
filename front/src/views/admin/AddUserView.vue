<template>
  <div class="add-user-container">
    <div class="page-header">
      <h2>添加用户</h2>
      <el-button @click="$router.push('/admin/users')">返回用户列表</el-button>
    </div>

    <el-card class="form-card">
      <el-form
        ref="formRef"
        :model="userForm"
        :rules="rules"
        label-width="120px"
        label-position="right"
        status-icon
      >
        <el-divider content-position="left">基本信息</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="name">
              <el-input v-model="userForm.name" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
                <el-option label="学生" value="学生" />
                <el-option label="教师" value="教师" />
                <el-option label="管理员" value="管理员" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="userForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="userForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 根据角色显示不同的表单项 -->
        <template v-if="userForm.role === '学生'">
          <el-divider content-position="left">学生信息</el-divider>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学号" prop="studentId">
                <el-input v-model="userForm.studentId" placeholder="请输入学号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="入学年份" prop="enrollmentYear">
                <el-date-picker
                  v-model="userForm.enrollmentYear"
                  type="year"
                  placeholder="选择入学年份"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学院" prop="department">
                <el-select v-model="userForm.department" placeholder="请选择学院" style="width: 100%">
                  <el-option label="计算机科学与技术学院" value="计算机科学与技术学院" />
                  <el-option label="电子信息工程学院" value="电子信息工程学院" />
                  <el-option label="机械工程学院" value="机械工程学院" />
                  <el-option label="经济管理学院" value="经济管理学院" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="专业" prop="major">
                <el-select v-model="userForm.major" placeholder="请选择专业" style="width: 100%">
                  <el-option label="软件工程" value="软件工程" />
                  <el-option label="计算机科学与技术" value="计算机科学与技术" />
                  <el-option label="人工智能" value="人工智能" />
                  <el-option label="数据科学与大数据技术" value="数据科学与大数据技术" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <template v-if="userForm.role === '教师'">
          <el-divider content-position="left">教师信息</el-divider>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="职称" prop="title">
                <el-select v-model="userForm.title" placeholder="请选择职称" style="width: 100%">
                  <el-option label="教授" value="教授" />
                  <el-option label="副教授" value="副教授" />
                  <el-option label="讲师" value="讲师" />
                  <el-option label="助教" value="助教" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学院" prop="department">
                <el-select v-model="userForm.department" placeholder="请选择学院" style="width: 100%">
                  <el-option label="计算机科学与技术学院" value="计算机科学与技术学院" />
                  <el-option label="电子信息工程学院" value="电子信息工程学院" />
                  <el-option label="机械工程学院" value="机械工程学院" />
                  <el-option label="经济管理学院" value="经济管理学院" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="教师简介" prop="introduction">
                <el-input
                  v-model="userForm.introduction"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入教师简介"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <div class="form-actions">
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="submitForm" :loading="loading">创建用户</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

// 用户表单
const userForm = reactive({
  name: '',
  email: '',
  role: '学生', // 默认为学生角色
  phone: '',
  password: '',
  confirmPassword: '',
  studentId: '',
  enrollmentYear: '',
  department: '',
  major: '',
  grade: '',
  title: '',
  introduction: ''
})

// 校验密码是否一致
const validatePass = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== userForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = reactive<FormRules>({
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
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass, trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择学院', trigger: 'change' }
  ],
  major: [
    { required: true, message: '请选择专业', trigger: 'change' }
  ]
})

// 监听角色变化，重置表单验证
watch(() => userForm.role, () => {
  nextTick(() => {
    formRef.value?.clearValidate()
  })
})

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  userForm.role = '学生' // 重置角色为学生
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        const userData = {
          name: userForm.name,
          email: userForm.email,
          role: userForm.role,
          phone: userForm.phone,
          password: userForm.password,
          studentId: userForm.role === '学生' ? userForm.studentId : undefined,
          department: userForm.department || undefined,
          major: userForm.role === '学生' ? userForm.major : undefined,
          grade: userForm.role === '学生' ? userForm.enrollmentYear + '级' : undefined,
          title: userForm.role === '教师' ? userForm.title : undefined,
          introduction: userForm.role === '教师' ? userForm.introduction : undefined,
          createdAt: new Date().toISOString(),
          lastLoginAt: null
        }
        
        // 在实际应用中，这里会调用API创建用户
        console.log('创建用户数据:', userData)
        
        ElMessage.success('用户创建成功')
        router.push('/admin/users')
      } catch (error) {
        ElMessage.error('创建用户失败，请稍后重试')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请正确填写表单信息')
    }
  })
}
</script>

<style scoped lang="scss">
.add-user-container {
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
  
  .form-card {
    margin-bottom: 20px;
  }
  
  .form-actions {
    margin-top: 30px;
    display: flex;
    justify-content: center;
    gap: 10px;
  }
  
  .el-divider {
    margin-top: 20px;
    margin-bottom: 20px;
    
    :deep(.el-divider__text) {
      font-size: 16px;
      font-weight: 500;
      color: #409eff;
    }
  }
}
</style> 