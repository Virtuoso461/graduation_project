<template>
  <div class="register-container">
    <!-- 背景粒子 -->
    <div class="particles-container">
      <div v-for="n in 50" :key="n" class="particle" :style="getParticleStyle()"></div>
    </div>
    
    <!-- 波浪效果 -->
    <div class="wave-container">
      <div class="wave wave1"></div>
      <div class="wave wave2"></div>
      <div class="wave wave3"></div>
    </div>
    
    <!-- 注册卡片 -->
    <div class="register-card">
      <div class="card-header">
        <div class="logo-container">
          <div class="logo-icon"></div>
          <h1 class="logo-text">学习矩阵</h1>
        </div>
        <p class="slogan">创建您的学习账号</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="top"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="选择角色" prop="role">
              <div class="role-selector">
                <div
                  class="role-card"
                  :class="{ active: registerForm.role === 'student' }"
                  @click="registerForm.role = 'student'"
                >
                  <el-icon class="role-icon"><User /></el-icon>
                  <div class="role-title">学生</div>
                  <div class="role-desc">在平台学习和提升</div>
                </div>
                <div
                  class="role-card"
                  :class="{ active: registerForm.role === 'teacher' }"
                  @click="registerForm.role = 'teacher'"
                >
                  <el-icon class="role-icon"><Promotion /></el-icon>
                  <div class="role-title">教师</div>
                  <div class="role-desc">创建和管理课程</div>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="姓名" prop="name">
              <el-input
                v-model="registerForm.name"
                placeholder="请输入姓名"
                :prefix-icon="User"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="学号" prop="number">
              <el-input
                v-model="registerForm.number"
                placeholder="请输入学号"
                :prefix-icon="School"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="电子邮箱" prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入电子邮箱"
                :prefix-icon="Message"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="手机号" prop="phoneNumber">
              <el-input
                v-model="registerForm.phoneNumber"
                placeholder="请输入手机号"
                :prefix-icon="Phone"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请设置密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item class="agreement">
          <el-checkbox v-model="agreeTerms">
            我已阅读并同意
            <el-button link class="link-btn" @click="showTerms">《用户协议》</el-button>
            和
            <el-button link class="link-btn" @click="showPrivacy">《隐私政策》</el-button>
          </el-checkbox>
        </el-form-item>
        
        <el-button 
          type="primary" 
          class="register-button" 
          :loading="loading" 
          :disabled="!agreeTerms"
          @click="handleRegister"
        >
          创建账号
        </el-button>
        
        <div class="login-link">
          已有账号？ <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </div>
    
    <!-- 底部版权信息 -->
    <footer class="footer">
      <p>© {{ currentYear }} 学习矩阵 - 高校智能学习平台</p>
      <p>推荐使用Chrome或Edge浏览器访问本系统</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance } from 'element-plus'
import { User, Lock, Message, Phone, Promotion, School } from '@element-plus/icons-vue'
import { api } from '@/utils/http'

const router = useRouter()
const registerFormRef = ref<FormInstance>()
const agreeTerms = ref(false)
const currentYear = computed(() => new Date().getFullYear())
const loading = ref(false)

// 注册表单
const registerForm = reactive({
  role: 'student',
  name: '',
  username: '',
  phoneNumber: '',
  number: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const validatePass = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      if (!registerFormRef.value) return
      registerFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  username : [
    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的电子邮箱格式', trigger: 'blur' }
  ],
  number: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{8,12}$/, message: '请输入8-12位数字学号', trigger: 'blur' }
  ],
  phoneNumber: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!agreeTerms.value) {
        ElMessage.warning('请阅读并同意用户协议和隐私政策')
        return
      }
      
      loading.value = true
      try {
        const requestData = {
          password: registerForm.password,
          username: registerForm.username,
          name: registerForm.name,
          phoneNumber: registerForm.phoneNumber,
          number: registerForm.number,
          role: registerForm.role === 'student' ? 'STUDENT' : 'TEACHER'
        }

        console.log('发送的注册数据:', requestData) // 调试用

        // 使用封装后的API
        const apiMethod = registerForm.role === 'student' 
          ? api.userApi.studentRegister
          : api.userApi.teacherRegister
          
        await apiMethod(requestData)
        
        // 注册成功
        ElMessage.success('注册成功，即将跳转到登录页面')
        setTimeout(() => {
          // 跳转到登录页面并携带用户名和密码
          router.push({
            path: '/login',
            query: {
              username: registerForm.username,
              password: registerForm.password
            }
          })
        }, 1500)
      } catch (error: any) {
        // 错误已在全局处理器中处理，这里可以添加特定于注册页面的逻辑
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 显示用户协议
const showTerms = () => {
  ElMessage.info('用户协议正在编写中')
}

// 显示隐私政策
const showPrivacy = () => {
  ElMessage.info('隐私政策正在编写中')
}

// 生成随机粒子样式
const getParticleStyle = () => {
  const size = Math.random() * 10 + 3
  const left = Math.random() * 100
  const animDuration = Math.random() * 20 + 10
  const delay = Math.random() * 10
  const opacity = Math.random() * 0.6 + 0.2
  
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDuration: `${animDuration}s`,
    animationDelay: `${delay}s`,
    opacity: opacity
  }
}

// 页面加载动画
onMounted(() => {
  document.body.classList.add('register-page')
  
  return () => {
    document.body.classList.remove('register-page')
  }
})
</script>

<style lang="scss" scoped>
.register-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #002c58 0%, #001529 100%);
  overflow: hidden;
  padding: 20px;
}

/* 粒子背景 */
.particles-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.particle {
  position: absolute;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  bottom: -10%;
  animation: floatUp linear infinite;
}

@keyframes floatUp {
  0% {
    transform: translateY(0) rotate(0);
    opacity: 1;
  }
  100% {
    transform: translateY(-1000px) rotate(720deg);
    opacity: 0;
  }
}

/* 波浪效果 */
.wave-container {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 30vh;
  z-index: 1;
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 200%;
  height: 100%;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="%230099ff" fill-opacity="0.2" d="M0,192L48,186.7C96,181,192,171,288,154.7C384,139,480,117,576,133.3C672,149,768,203,864,202.7C960,203,1056,149,1152,138.7C1248,128,1344,160,1392,176L1440,192L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>');
  background-repeat: repeat-x;
  background-position: 0 bottom;
  background-size: 50% 100%;
  transform: translate3d(0, 0, 0);
}

.wave1 {
  opacity: 0.3;
  animation: wave 25s linear infinite;
}

.wave2 {
  opacity: 0.2;
  animation: wave 20s linear infinite reverse;
}

.wave3 {
  opacity: 0.1;
  animation: wave 30s linear infinite;
}

@keyframes wave {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

/* 注册卡片 */
.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 480px;
  padding: 40px;
  z-index: 10;
  animation: fadeIn 0.8s ease-out;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    inset: -1px;
    background: linear-gradient(45deg, #4facfe, #00f2fe, #4facfe);
    border-radius: 17px;
    z-index: -1;
    opacity: 0.6;
  }
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
  
  .logo-container {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
    
    .logo-icon {
      width: 40px;
      height: 40px;
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      border-radius: 8px;
      position: relative;
      margin-right: 12px;
      
      &::before {
        content: '';
        position: absolute;
        width: 20px;
        height: 20px;
        background-color: white;
        border-radius: 5px;
        top: 10px;
        left: 10px;
      }
    }
    
    .logo-text {
      font-size: 1.8rem;
      font-weight: 700;
      color: #001529;
      margin: 0;
      background: linear-gradient(135deg, #001529 0%, #004080 100%);
      -webkit-background-clip: text;
      color: transparent;
    }
  }
  
  .slogan {
    color: #666;
    font-size: 1rem;
    margin-top: 0;
  }
}

.register-form {
  :deep(.el-form-item__label) {
    color: #333;
    font-weight: 500;
    font-size: 0.9rem;
  }
  
  :deep(.el-input__wrapper) {
    border-radius: 8px;
    height: 50px;
    box-shadow: 0 0 0 1px #dcdfe6;
    
    &:hover, &.is-focus {
      box-shadow: 0 0 0 1px #4facfe;
    }
    
    .el-input__inner {
      font-size: 1rem;
    }
  }
  
  :deep(.el-radio-group) {
    display: flex;
    gap: 20px;
    justify-content: center;
  }

  :deep(.el-radio) {
    margin-right: 0;
  }
  
  .agreement {
    margin-top: 10px;
    margin-bottom: 25px;
    
    .link-btn {
      padding: 0;
      font-size: inherit;
      color: #4facfe;
      font-weight: 500;
    }
  }
  
  .register-button {
    width: 100%;
    height: 50px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    border: none;
    font-size: 1.1rem;
    font-weight: 500;
    margin-bottom: 20px;
    
    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(79, 172, 254, 0.3);
    }
    
    &:active:not(:disabled) {
      transform: translateY(0);
    }
  }
  
  .login-link {
    text-align: center;
    font-size: 0.95rem;
    color: #666;
    
    a {
      color: #4facfe;
      text-decoration: none;
      font-weight: 500;
      
      &:hover {
        text-decoration: underline;
      }
    }
  }
}

/* 底部版权信息 */
.footer {
  position: absolute;
  bottom: 20px;
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.8rem;
  z-index: 10;
  
  p {
    margin: 5px 0;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式样式 */
@media (max-width: 576px) {
  .register-card {
    padding: 30px 20px;
  }
  
  .card-header .logo-text {
    font-size: 1.6rem;
  }
}

.role-selector {
  display: flex;
  gap: 30px;
  justify-content: center;
  margin: 20px auto;
  max-width: 500px;
}

.role-card {
  flex: 0 1 200px;
  background: #fff;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    border-color: #4facfe;
    box-shadow: 0 4px 12px rgba(79, 172, 254, 0.15);
  }
  
  &.active {
    border-color: #4facfe;
    background: linear-gradient(to bottom, rgba(79, 172, 254, 0.05), rgba(0, 242, 254, 0.05));
    box-shadow: 0 4px 12px rgba(79, 172, 254, 0.15);
    
    .role-icon {
      color: #4facfe;
      transform: scale(1.1);
    }
    
    .role-title {
      color: #4facfe;
    }
  }
}

.role-icon {
  font-size: 36px;
  color: #909399;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.role-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
  transition: color 0.3s ease;
}

.role-desc {
  font-size: 0.95rem;
  color: #909399;
  line-height: 1.4;
}

/* 响应式调整 */
@media (max-width: 576px) {
  .role-selector {
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }

  .role-card {
    width: 100%;
    max-width: 240px;
    padding: 20px;
  }
}
</style> 