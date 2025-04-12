<template>
  <div class="teacher-profile">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人信息</h2>
          <el-button type="primary" @click="editMode = !editMode">
            {{ editMode ? '取消编辑' : '编辑信息' }}
          </el-button>
        </div>
      </template>
      
      <div class="profile-content">
        <div class="profile-avatar">
          <el-avatar :size="120" :src="profile.avatar">
            {{ profile.name ? profile.name.charAt(0) : 'T' }}
          </el-avatar>
          <div v-if="editMode" class="avatar-upload">
            <el-upload
              action="/api/upload/avatar"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <el-button size="small" type="primary">更换头像</el-button>
            </el-upload>
          </div>
        </div>
        
        <div class="profile-info">
          <el-form 
            :model="profile" 
            :disabled="!editMode"
            label-position="top"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名">
                  <el-input v-model="profile.name" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="工号">
                  <el-input v-model="profile.teacherId" disabled />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="电子邮箱">
                  <el-input v-model="profile.email" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号码">
                  <el-input v-model="profile.phone" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="所属院系">
                  <el-select v-model="profile.department" placeholder="请选择院系">
                    <el-option
                      v-for="item in departments"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职称">
                  <el-select v-model="profile.title" placeholder="请选择职称">
                    <el-option
                      v-for="item in titles"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="研究方向">
              <el-input v-model="profile.researchArea" type="textarea" :rows="2" />
            </el-form-item>
            
            <el-form-item label="个人简介">
              <el-input v-model="profile.bio" type="textarea" :rows="4" />
            </el-form-item>
            
            <el-form-item v-if="editMode">
              <el-button type="primary" @click="saveProfile">保存信息</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
    
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <h2>修改密码</h2>
        </div>
      </template>
      
      <el-form 
        :model="passwordForm" 
        :rules="passwordRules"
        ref="passwordFormRef"
        label-position="top"
      >
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input 
            v-model="passwordForm.currentPassword" 
            type="password" 
            show-password
            placeholder="请输入当前密码"
          />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="changePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

// 编辑模式状态
const editMode = ref(false)

// 个人信息表单
const profile = reactive({
  name: '张教授',
  teacherId: 'T20230001',
  email: 'zhang@example.com',
  phone: '13800138000',
  department: 'computer',
  title: 'professor',
  researchArea: '人工智能、机器学习、数据挖掘',
  bio: '从事计算机科学教学与研究工作多年，主要研究方向为人工智能与机器学习。曾主持多项国家级科研项目，发表学术论文数十篇。',
  avatar: ''
})

// 院系选项
const departments = [
  { value: 'computer', label: '计算机科学与技术学院' },
  { value: 'math', label: '数学学院' },
  { value: 'physics', label: '物理学院' },
  { value: 'chemistry', label: '化学学院' },
  { value: 'biology', label: '生物学院' }
]

// 职称选项
const titles = [
  { value: 'assistant', label: '助教' },
  { value: 'lecturer', label: '讲师' },
  { value: 'associate', label: '副教授' },
  { value: 'professor', label: '教授' }
]

// 密码表单
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码表单验证规则
const validatePass = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = reactive<FormRules>({
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
})

// 头像上传前的校验
const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 头像上传成功的回调
const handleAvatarSuccess = (response: any) => {
  profile.avatar = response.url
  ElMessage.success('头像上传成功')
}

// 保存个人信息
const saveProfile = async () => {
  try {
    // 这里应该调用API保存个人信息
    // await api.updateTeacherProfile(profile)
    
    ElMessage.success('个人信息保存成功')
    editMode.value = false
  } catch (error) {
    console.error('保存个人信息失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用API修改密码
        // await api.changePassword({
        //   currentPassword: passwordForm.currentPassword,
        //   newPassword: passwordForm.newPassword
        // })
        
        ElMessage.success('密码修改成功')
        
        // 重置表单
        passwordForm.currentPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        passwordFormRef.value?.resetFields()
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败，请检查当前密码是否正确')
      }
    } else {
      return false
    }
  })
}

// 获取个人信息
const fetchProfile = async () => {
  try {
    // 这里应该调用API获取个人信息
    // const response = await api.getTeacherProfile()
    // Object.assign(profile, response.data)
  } catch (error) {
    console.error('获取个人信息失败:', error)
    ElMessage.error('获取个人信息失败，请刷新页面重试')
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped lang="scss">
.teacher-profile {
  padding: 20px;
  
  .profile-card, .password-card {
    margin-bottom: 20px;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  .profile-content {
    display: flex;
    gap: 30px;
    
    @media (max-width: 768px) {
      flex-direction: column;
    }
  }
  
  .profile-avatar {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .avatar-upload {
      margin-top: 10px;
    }
  }
  
  .profile-info {
    flex: 1;
  }
}
</style>
