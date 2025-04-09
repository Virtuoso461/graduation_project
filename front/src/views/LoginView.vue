<template>
  <div class="login-container">
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

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="card-header">
        <div class="logo-container">
          <div class="logo-icon"></div>
          <h1 class="logo-text">学习矩阵</h1>
        </div>
        <p class="slogan">高校智能学习助手</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="login-form"
        autocomplete="on"
        label-position="left"
      >
        <div class="title-container">
          <h3 class="title">在线考试系统</h3>
        </div>

        <el-form-item prop="username">
          <el-input
            ref="usernameRef"
            v-model="form.username"
            placeholder="用户名"
            name="username"
            type="text"
            tabindex="1"
            autocomplete="on"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            ref="passwordRef"
            v-model="form.password"
            :type="passwordVisible ? 'text' : 'password'"
            placeholder="密码"
            name="password"
            tabindex="2"
            autocomplete="on"
            @keyup.enter="handleLogin"
          />
          <span class="show-pwd" @click="passwordVisible = !passwordVisible">
            <el-icon><View v-if="passwordVisible" /><Hide v-else /></el-icon>
          </span>
        </el-form-item>

        <el-form-item prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="student">学生</el-radio>
            <el-radio value="teacher">教师</el-radio>
          </el-radio-group>
        </el-form-item>

        <div class="form-options">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <el-button link class="forgot-password" @click="forgotPassword">
            忘记密码？
          </el-button>
        </div>

        <el-button
          type="primary"
          class="login-button"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </el-button>

        <div class="divider">
          <span>或</span>
        </div>

        <div class="oauth-options">
          <el-tooltip content="微信登录" placement="top">
            <el-button class="oauth-button wechat">
              <el-icon><component :is="Platform.wechat" /></el-icon>
            </el-button>
          </el-tooltip>

          <el-tooltip content="QQ登录" placement="top">
            <el-button class="oauth-button qq">
              <el-icon><component :is="Platform.qq" /></el-icon>
            </el-button>
          </el-tooltip>

          <el-tooltip content="学校统一认证" placement="top">
            <el-button class="oauth-button school">
              <el-icon><School /></el-icon>
            </el-button>
          </el-tooltip>
        </div>

        <div class="register-link">
          还没有账号？ <router-link to="/register">立即注册</router-link>
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
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, FormInstance } from 'element-plus'
import { User, Lock, School, Message, View, Hide } from '@element-plus/icons-vue'
import { api } from '@/utils/http'
import { saveUserInfo } from '@/utils/indexedDB'
import { userApi, profileApi } from '@/utils/http/api'

const Platform = {
  wechat: 'Platform', // 替代微信图标
  qq: 'Platform' // 替代QQ图标
}

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance>()
const rememberMe = ref(false)
const currentYear = computed(() => new Date().getFullYear())
const loading = ref(false)
const formRef = ref()
const form = ref({
  username: '',
  password: '',
  role: 'student'
})
const passwordVisible = ref(false)

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 定义登录响应接口
interface LoginResponse {
  token: string;
  email: string;
  role: string;
}

interface ApiResponse {
  code: number;
  message: string;
  data: LoginResponse;
}

// 处理登录
const handleLogin = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    console.log('开始登录流程，提交表单数据:', form.value)

    // 调用 userStore 的登录方法
    const success = await userStore.login({
      username: form.value.username,
      password: form.value.password
    })

    console.log('登录结果:', success)

    // 如果勾选了"记住我"，保存用户名
    if (success && rememberMe.value) {
      localStorage.setItem('rememberedUser', form.value.username)
    } else if (!rememberMe.value) {
      localStorage.removeItem('rememberedUser')
    }

    // 登录成功，用户资料已在userStore.login中处理
    if (success) {
      // 注意：我们已经在userStore.login中处理了用户资料的获取和保存
      // 这里不需要重复处理

      // 根据用户角色决定跳转路径
      const redirectPath = route.query.redirect as string || '/dashboard'

      console.log('用户角色:', form.value.role)
      console.log('重定向路径:', redirectPath)

      // 如果用户手动设置了角色，就使用该角色进行跳转
      if (form.value.role === 'teacher') {
        console.log('教师角色，准备跳转到: /course-catalog')
        router.push('/course-catalog') // 教师跳转到课程目录
      } else if (form.value.role === 'student') {
        console.log('学生角色，准备跳转到: /dashboard')
        router.push('/dashboard') // 学生跳转到学习中心
      } else {
        // 默认跳转到设置的重定向路径
        console.log('其他角色，准备跳转到:', redirectPath)
        router.push(redirectPath)
      }
    }
  } catch (error: any) {
    console.error('登录过程中发生错误:', error)
    ElMessage.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

// 忘记密码
const forgotPassword = () => {
  router.push('/forgot-password')
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

// 从URL参数中获取用户名和密码
onMounted(() => {
  document.body.classList.add('login-page')

  // 检查是否已登录
  if (userStore.isLoggedIn) {
    // 如果已登录，直接跳转到首页
    router.push('/')
  }

  // 检查URL参数中是否有用户名和密码
  const username = route.query.username as string
  const password = route.query.password as string

  if (username) {
    form.value.username = username
  } else {
    // 如果URL中没有用户名，尝试从本地存储中获取记住的用户名
    const rememberedUser = localStorage.getItem('rememberedUser')
    if (rememberedUser) {
      form.value.username = rememberedUser
      rememberMe.value = true
    }
  }

  if (password) {
    form.value.password = password
    // 如果有密码，自动勾选"记住我"
    rememberMe.value = true

    // 自动聚焦到登录按钮
    setTimeout(() => {
      const loginButton = document.querySelector('.login-button')
      if (loginButton) {
        (loginButton as HTMLElement).focus()
      }
    }, 500)
  }

  return () => {
    document.body.classList.remove('login-page')
  }
})
</script>

<style lang="scss" scoped>
.login-container {
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

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 420px;
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

.login-form {
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

  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .forgot-password {
      font-size: 0.9rem;
      color: #4facfe;
    }
  }

  .login-button {
    width: 100%;
    height: 50px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    border: none;
    font-size: 1.1rem;
    font-weight: 500;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(79, 172, 254, 0.3);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .divider {
    display: flex;
    align-items: center;
    margin: 30px 0;

    &::before, &::after {
      content: '';
      flex: 1;
      height: 1px;
      background-color: #eee;
    }

    span {
      padding: 0 16px;
      color: #999;
      font-size: 0.9rem;
    }
  }

  .oauth-options {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-bottom: 20px;

    .oauth-button {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.4rem;
      color: white;
      border: none;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
      }

      &.wechat {
        background-color: #07c160;
      }

      &.qq {
        background-color: #12b7f5;
      }

      &.school {
        background-color: #f56c6c;
      }
    }
  }

  .register-link {
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
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }

  .card-header .logo-text {
    font-size: 1.6rem;
  }

  .oauth-options {
    gap: 15px;

    .oauth-button {
      width: 42px;
      height: 42px;
    }
  }
}
</style>