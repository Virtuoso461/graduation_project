<template>
  <div class="settings-container">
    <div class="page-header">
      <h2>系统设置</h2>
    </div>

    <el-tabs v-model="activeTab" tab-position="left" class="settings-tabs">
      <el-tab-pane label="基本设置" name="basic">
        <h3>基本设置</h3>
        
        <el-form
          ref="basicFormRef"
          :model="basicForm"
          :rules="basicRules"
          label-width="120px"
          label-position="right"
        >
          <el-form-item label="平台名称" prop="siteName">
            <el-input v-model="basicForm.siteName" />
          </el-form-item>
          
          <el-form-item label="平台描述" prop="siteDescription">
            <el-input
              v-model="basicForm.siteDescription"
              type="textarea"
              :rows="3"
            />
          </el-form-item>
          
          <el-form-item label="平台Logo">
            <el-upload
              class="logo-uploader"
              action="#"
              :auto-upload="false"
              :limit="1"
              :on-change="handleLogoChange"
              :before-upload="beforeLogoUpload"
            >
              <img v-if="basicForm.logoUrl" :src="basicForm.logoUrl" class="logo-preview" />
              <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">建议尺寸: 200x60px，格式: PNG, JPG</div>
          </el-form-item>
          
          <el-form-item label="备案信息" prop="icp">
            <el-input v-model="basicForm.icp" placeholder="例如：粤ICP备12345678号" />
          </el-form-item>
          
          <el-form-item label="客服邮箱" prop="supportEmail">
            <el-input v-model="basicForm.supportEmail" placeholder="例如：support@example.com" />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="saveBasicSettings" :loading="saving">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="邮件设置" name="email">
        <h3>邮件设置</h3>
        
        <el-form
          ref="emailFormRef"
          :model="emailForm"
          :rules="emailRules"
          label-width="120px"
          label-position="right"
        >
          <el-form-item label="SMTP服务器" prop="smtpServer">
            <el-input v-model="emailForm.smtpServer" placeholder="例如：smtp.example.com" />
          </el-form-item>
          
          <el-form-item label="SMTP端口" prop="smtpPort">
            <el-input-number v-model="emailForm.smtpPort" :min="1" :max="65535" />
          </el-form-item>
          
          <el-form-item label="发件邮箱" prop="senderEmail">
            <el-input v-model="emailForm.senderEmail" placeholder="例如：noreply@example.com" />
          </el-form-item>
          
          <el-form-item label="邮箱密码" prop="emailPassword">
            <el-input
              v-model="emailForm.emailPassword"
              type="password"
              placeholder="输入邮箱密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="启用SSL">
            <el-switch v-model="emailForm.enableSsl" />
          </el-form-item>
          
          <el-form-item>
            <el-button @click="testEmailSettings" :loading="testing">测试连接</el-button>
            <el-button type="primary" @click="saveEmailSettings" :loading="saving">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="注册设置" name="registration">
        <h3>注册设置</h3>
        
        <el-form
          ref="registrationFormRef"
          :model="registrationForm"
          label-width="180px"
          label-position="right"
        >
          <el-form-item label="开放注册">
            <el-switch v-model="registrationForm.allowRegistration" />
          </el-form-item>
          
          <el-form-item label="需要邮箱验证">
            <el-switch v-model="registrationForm.requireEmailVerification" />
          </el-form-item>
          
          <el-form-item label="默认用户角色">
            <el-select v-model="registrationForm.defaultRole" style="width: 100%">
              <el-option label="学生" value="学生" />
              <el-option label="教师" value="教师" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="用户名最小长度">
            <el-input-number v-model="registrationForm.usernameMinLength" :min="2" :max="20" />
          </el-form-item>
          
          <el-form-item label="密码最小长度">
            <el-input-number v-model="registrationForm.passwordMinLength" :min="6" :max="30" />
          </el-form-item>
          
          <el-form-item label="密码强度要求">
            <el-checkbox-group v-model="registrationForm.passwordRequirements">
              <el-checkbox label="uppercase">必须包含大写字母</el-checkbox>
              <el-checkbox label="lowercase">必须包含小写字母</el-checkbox>
              <el-checkbox label="number">必须包含数字</el-checkbox>
              <el-checkbox label="special">必须包含特殊字符</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="saveRegistrationSettings" :loading="saving">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="安全设置" name="security">
        <h3>安全设置</h3>
        
        <el-form
          ref="securityFormRef"
          :model="securityForm"
          label-width="180px"
          label-position="right"
        >
          <el-form-item label="会话超时时间（分钟）">
            <el-input-number v-model="securityForm.sessionTimeout" :min="5" :max="1440" />
          </el-form-item>
          
          <el-form-item label="登录失败锁定阈值">
            <el-input-number v-model="securityForm.maxLoginAttempts" :min="3" :max="10" />
          </el-form-item>
          
          <el-form-item label="锁定时间（分钟）">
            <el-input-number v-model="securityForm.lockoutDuration" :min="5" :max="1440" />
          </el-form-item>
          
          <el-form-item label="启用Google reCAPTCHA">
            <el-switch v-model="securityForm.enableRecaptcha" />
          </el-form-item>
          
          <el-form-item label="reCAPTCHA站点密钥" v-if="securityForm.enableRecaptcha">
            <el-input v-model="securityForm.recaptchaSiteKey" />
          </el-form-item>
          
          <el-form-item label="reCAPTCHA密钥" v-if="securityForm.enableRecaptcha">
            <el-input v-model="securityForm.recaptchaSecretKey" show-password />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="saveSecuritySettings" :loading="saving">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

// 当前激活的标签页
const activeTab = ref('basic')

// 加载状态
const saving = ref(false)
const testing = ref(false)

// 基本设置表单
const basicFormRef = ref<FormInstance>()
const basicForm = reactive({
  siteName: '智能学习平台',
  siteDescription: '智能学习平台是一个面向高校学生和教师的在线学习平台，提供丰富的课程资源和交互式学习体验。',
  logoUrl: '',
  icp: '粤ICP备12345678号',
  supportEmail: 'support@example.com'
})

// 基本设置表单规则
const basicRules = reactive<FormRules>({
  siteName: [
    { required: true, message: '请输入平台名称', trigger: 'blur' },
    { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
  ],
  siteDescription: [
    { required: true, message: '请输入平台描述', trigger: 'blur' }
  ],
  supportEmail: [
    { required: true, message: '请输入客服邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
})

// 邮件设置表单
const emailFormRef = ref<FormInstance>()
const emailForm = reactive({
  smtpServer: 'smtp.163.com',
  smtpPort: 465,
  senderEmail: 'noreply@example.com',
  emailPassword: '',
  enableSsl: true
})

// 邮件设置表单规则
const emailRules = reactive<FormRules>({
  smtpServer: [
    { required: true, message: '请输入SMTP服务器', trigger: 'blur' }
  ],
  smtpPort: [
    { required: true, message: '请输入SMTP端口', trigger: 'blur' }
  ],
  senderEmail: [
    { required: true, message: '请输入发件邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  emailPassword: [
    { required: true, message: '请输入邮箱密码', trigger: 'blur' }
  ]
})

// 注册设置表单
const registrationFormRef = ref<FormInstance>()
const registrationForm = reactive({
  allowRegistration: true,
  requireEmailVerification: true,
  defaultRole: '学生',
  usernameMinLength: 4,
  passwordMinLength: 8,
  passwordRequirements: ['uppercase', 'number']
})

// 安全设置表单
const securityFormRef = ref<FormInstance>()
const securityForm = reactive({
  sessionTimeout: 60,
  maxLoginAttempts: 5,
  lockoutDuration: 30,
  enableRecaptcha: false,
  recaptchaSiteKey: '',
  recaptchaSecretKey: ''
})

// 处理上传Logo
const handleLogoChange = (file: any) => {
  const isImage = file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
  if (!isImage) {
    ElMessage.error('只能上传JPG或PNG格式的图片!')
    return false
  }
  
  // 使用FileReader预览图片
  const reader = new FileReader()
  reader.onload = (e: any) => {
    basicForm.logoUrl = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 上传前校验
const beforeLogoUpload = (file: any) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传JPG或PNG格式的图片!')
  }
  
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB!')
  }
  
  return isImage && isLt2M
}

// 保存基本设置
const saveBasicSettings = async () => {
  if (!basicFormRef.value) return
  
  await basicFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 在实际应用中，这里会调用API保存设置
        console.log('保存基本设置:', basicForm)
        
        ElMessage.success('基本设置已保存')
      } catch (error) {
        ElMessage.error('保存设置失败，请重试')
      } finally {
        saving.value = false
      }
    }
  })
}

// 测试邮件设置
const testEmailSettings = async () => {
  if (!emailFormRef.value) return
  
  await emailFormRef.value.validate(async (valid) => {
    if (valid) {
      testing.value = true
      
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        // 在实际应用中，这里会调用API测试邮件连接
        console.log('测试邮件设置:', emailForm)
        
        ElMessage.success('邮件服务器连接测试成功')
      } catch (error) {
        ElMessage.error('连接测试失败，请检查配置')
      } finally {
        testing.value = false
      }
    }
  })
}

// 保存邮件设置
const saveEmailSettings = async () => {
  if (!emailFormRef.value) return
  
  await emailFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 在实际应用中，这里会调用API保存设置
        console.log('保存邮件设置:', emailForm)
        
        ElMessage.success('邮件设置已保存')
      } catch (error) {
        ElMessage.error('保存设置失败，请重试')
      } finally {
        saving.value = false
      }
    }
  })
}

// 保存注册设置
const saveRegistrationSettings = async () => {
  saving.value = true
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 在实际应用中，这里会调用API保存设置
    console.log('保存注册设置:', registrationForm)
    
    ElMessage.success('注册设置已保存')
  } catch (error) {
    ElMessage.error('保存设置失败，请重试')
  } finally {
    saving.value = false
  }
}

// 保存安全设置
const saveSecuritySettings = async () => {
  saving.value = true
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 在实际应用中，这里会调用API保存设置
    console.log('保存安全设置:', securityForm)
    
    ElMessage.success('安全设置已保存')
  } catch (error) {
    ElMessage.error('保存设置失败，请重试')
  } finally {
    saving.value = false
  }
}

// 初始化
onMounted(() => {
  // 在实际应用中，这里会从API获取当前设置
})
</script>

<style scoped lang="scss">
.settings-container {
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
  
  .settings-tabs {
    border: 1px solid #ebeef5;
    border-radius: 4px;
    
    h3 {
      margin: 0 0 20px;
      padding-bottom: 10px;
      border-bottom: 1px solid #ebeef5;
      font-size: 18px;
      font-weight: 600;
    }
    
    :deep(.el-tabs__header) {
      padding: 20px 0;
    }
    
    :deep(.el-tabs__item) {
      height: 50px;
      line-height: 50px;
      text-align: left;
      padding-left: 20px !important;
    }
    
    :deep(.el-tabs__content) {
      padding: 20px;
    }
  }
  
  .logo-uploader {
    width: 200px;
    height: 60px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    
    .logo-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 200px;
      height: 60px;
      line-height: 60px;
      text-align: center;
    }
    
    .logo-preview {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }
  
  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 8px;
  }
}
</style> 