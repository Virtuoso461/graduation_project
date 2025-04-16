<template>
  <div class="forgot-password-container">
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
    
    <!-- 找回密码卡片 -->
    <div class="forgot-password-card">
      <div class="card-header">
        <div class="logo-container">
          <div class="logo-icon"></div>
          <h1 class="logo-text">学习矩阵</h1>
        </div>
        <p class="slogan">高校智能学习助手</p>
      </div>
      
      <el-steps :active="activeStep" finish-status="success" simple>
        <el-step title="验证邮箱" />
        <el-step title="重置密码" />
        <el-step title="完成" />
      </el-steps>
      
      <!-- 步骤1：验证邮箱 -->
      <div v-if="activeStep === 0" class="step-content">
        <el-form
          ref="emailFormRef"
          :model="emailForm"
          :rules="emailRules"
          class="forgot-form"
          label-position="top"
        >
          <el-form-item label="邮箱" prop="email">
            <el-input 
              v-model="emailForm.email" 
              placeholder="请输入您的注册邮箱"
              type="email"
            />
          </el-form-item>
          
          <el-form-item v-if="codeSent" label="验证码" prop="code">
            <div class="verification-code-container">
              <el-input 
                v-model="emailForm.code" 
                placeholder="请输入验证码"
              />
              <el-button 
                :disabled="countdown > 0" 
                @click="sendVerificationCode"
              >
                {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <div class="form-actions">
            <el-button @click="$router.push('/login')">返回登录</el-button>
            <el-button 
              v-if="!codeSent" 
              type="primary" 
              :loading="loading" 
              @click="sendVerificationCode"
            >
              获取验证码
            </el-button>
            <el-button 
              v-else 
              type="primary" 
              :loading="loading" 
              @click="verifyEmailCode"
            >
              验证
            </el-button>
          </div>
        </el-form>
      </div>
      
      <!-- 步骤2：重置密码 -->
      <div v-if="activeStep === 1" class="step-content">
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          class="forgot-form"
          label-position="top"
        >
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="passwordForm.newPassword" 
              placeholder="请输入新密码"
              type="password"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="passwordForm.confirmPassword" 
              placeholder="请再次输入新密码"
              type="password"
              show-password
            />
          </el-form-item>
          
          <div class="form-actions">
            <el-button @click="activeStep = 0">上一步</el-button>
            <el-button 
              type="primary" 
              :loading="loading" 
              @click="resetPassword"
            >
              重置密码
            </el-button>
          </div>
        </el-form>
      </div>
      
      <!-- 步骤3：完成 -->
      <div v-if="activeStep === 2" class="step-content success-step">
        <div class="success-icon">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <h2>密码重置成功!</h2>
        <p>您的密码已经重置，请使用新密码登录系统。</p>
        <el-button type="primary" @click="$router.push('/login')">
          返回登录
        </el-button>
      </div>
    </div>
    
    <!-- 底部版权信息 -->
    <footer class="footer">
      <p>© {{ currentYear }} 学习矩阵 - 高校智能学习平台</p>
      <p>推荐使用Chrome或Edge浏览器访问本系统</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance } from 'element-plus'
import { userApi } from '@/utils/http/api'
import { CircleCheck } from '@element-plus/icons-vue'

// API响应类型定义
interface ApiResponse {
  code: number;
  message: string;
  data: any;
}

const router = useRouter()
const activeStep = ref(0)
const loading = ref(false)
const codeSent = ref(false)
const countdown = ref(0)
let countdownTimer: NodeJS.Timeout | null = null
const currentYear = computed(() => new Date().getFullYear())

// 邮箱表单
const emailFormRef = ref<FormInstance>()
const emailForm = reactive({
  email: '',
  code: ''
})

// 密码表单
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const emailRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '验证码长度为6位', trigger: 'blur' }
  ]
}

const passwordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 发送验证码
const sendVerificationCode = async () => {
  if (!codeSent.value) {
    if (!emailForm.email) {
      ElMessage.warning('请输入邮箱地址')
      return
    }
    
    try {
      await emailFormRef.value?.validateField('email')
    } catch (error) {
      return
    }
  }
  
  try {
    loading.value = true
    const res = await userApi.sendResetCode(emailForm.email) as ApiResponse
    
    if (res.code === 200) {
      ElMessage.success('验证码已发送，请检查您的邮箱')
      codeSent.value = true
      startCountdown()
    } else {
      ElMessage.error(res.message || '发送验证码失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '发送验证码失败')
  } finally {
    loading.value = false
  }
}

// 验证邮箱验证码
const verifyEmailCode = async () => {
  try {
    await emailFormRef.value?.validate()
    
    loading.value = true
    const res = await userApi.verifyResetCode(emailForm.email, emailForm.code) as ApiResponse
    
    if (res.code === 200) {
      ElMessage.success('验证成功')
      activeStep.value = 1
    } else {
      ElMessage.error(res.message || '验证码错误')
    }
  } catch (error: any) {
    console.error('验证码验证错误:', error)
    ElMessage.error(error.response?.data?.message || '验证失败，请检查验证码是否正确')
  } finally {
    loading.value = false
  }
}

// 重置密码
const resetPassword = async () => {
  try {
    await passwordFormRef.value?.validate()
    
    loading.value = true
    const res = await userApi.resetPassword({
      email: emailForm.email,
      verificationCode: emailForm.code,
      newPassword: passwordForm.newPassword
    }) as ApiResponse
    
    if (res.code === 200) {
      ElMessage.success('密码重置成功')
      activeStep.value = 2
    } else {
      ElMessage.error(res.message || '密码重置失败')
    }
  } catch (error: any) {
    console.error('密码重置错误:', error)
    ElMessage.error(error.response?.data?.message || '密码重置失败')
  } finally {
    loading.value = false
  }
}

// 倒计时功能
const startCountdown = () => {
  countdown.value = 60
  
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  
  countdownTimer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      if (countdownTimer) clearInterval(countdownTimer)
    }
  }, 1000)
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

onBeforeUnmount(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style lang="scss" scoped>
.forgot-password-container {
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
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 30vh;
  z-index: 1;
  pointer-events: none;
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

/* 找回密码卡片 */
.forgot-password-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 480px;
  padding: 40px;
  z-index: 10;
  animation: fadeIn 0.8s ease-out;
  
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

.step-content {
  margin-top: 30px;
}

.forgot-form {
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
  
  .verification-code-container {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .el-input {
      flex: 1;
    }
    
    .el-button {
      white-space: nowrap;
    }
  }
  
  .form-actions {
    display: flex;
    justify-content: space-between;
    margin-top: 30px;
    
    .el-button {
      min-width: 100px;
    }
  }
}

.success-step {
  text-align: center;
  padding: 30px 0;
  
  .success-icon {
    font-size: 60px;
    color: #67c23a;
    margin-bottom: 20px;
  }
  
  h2 {
    font-size: 24px;
    color: #303133;
    margin-bottom: 16px;
  }
  
  p {
    color: #606266;
    margin-bottom: 30px;
  }
  
  .el-button {
    min-width: 120px;
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
@media (max-width: 520px) {
  .forgot-password-card {
    padding: 30px 20px;
  }
  
  .card-header .logo-text {
    font-size: 1.6rem;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 10px;
    
    .el-button {
      width: 100%;
    }
  }
}
</style> 