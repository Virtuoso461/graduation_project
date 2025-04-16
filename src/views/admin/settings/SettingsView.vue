<template>
  <div class="settings-page">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">系统设置</h1>
      <p class="text-gray-600">管理系统全局配置和参数</p>
    </div>
    
    <div class="bg-white rounded-lg shadow-md overflow-hidden">
      <div class="p-6">
        <form @submit.prevent="saveSettings">
          <!-- 基本设置 -->
          <div class="mb-8">
            <h2 class="text-lg font-semibold text-gray-800 mb-4 pb-2 border-b">基本设置</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- 系统名称 -->
              <div>
                <label for="systemName" class="block text-sm font-medium text-gray-700 mb-1">
                  系统名称
                </label>
                <input
                  id="systemName"
                  v-model="formData.systemName"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                  placeholder="请输入系统名称"
                />
              </div>
              
              <!-- 系统Logo -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  系统Logo
                </label>
                <div class="flex items-center space-x-4">
                  <div 
                    class="w-12 h-12 border border-gray-300 rounded-md overflow-hidden flex items-center justify-center bg-gray-100"
                  >
                    <img 
                      v-if="formData.logo" 
                      :src="formData.logo" 
                      alt="系统Logo" 
                      class="w-full h-full object-contain"
                    />
                    <span v-else class="text-gray-400">
                      <i class="fas fa-image text-xl"></i>
                    </span>
                  </div>
                  
                  <button
                    type="button"
                    class="px-3 py-1 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 focus:outline-none text-sm"
                    @click="triggerLogoUpload"
                  >
                    选择图片
                  </button>
                  <input
                    ref="logoInput"
                    type="file"
                    accept="image/*"
                    class="hidden"
                    @change="handleLogoChange"
                  />
                </div>
              </div>
              
              <!-- 系统描述 -->
              <div class="md:col-span-2">
                <label for="systemDescription" class="block text-sm font-medium text-gray-700 mb-1">
                  系统描述
                </label>
                <textarea
                  id="systemDescription"
                  v-model="formData.systemDescription"
                  rows="3"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                  placeholder="请输入系统描述"
                ></textarea>
              </div>
            </div>
          </div>
          
          <!-- 注册设置 -->
          <div class="mb-8">
            <h2 class="text-lg font-semibold text-gray-800 mb-4 pb-2 border-b">注册设置</h2>
            
            <div class="space-y-4">
              <!-- 允许注册 -->
              <div class="flex items-center">
                <input
                  id="allowRegistration"
                  v-model="formData.allowRegistration"
                  type="checkbox"
                  class="form-checkbox h-4 w-4 text-primary focus:ring-primary"
                />
                <label for="allowRegistration" class="ml-2 text-sm text-gray-700">
                  允许用户注册
                </label>
              </div>
              
              <!-- 注册需要审核 -->
              <div class="flex items-center">
                <input
                  id="registrationRequiresApproval"
                  v-model="formData.registrationRequiresApproval"
                  type="checkbox"
                  class="form-checkbox h-4 w-4 text-primary focus:ring-primary"
                />
                <label for="registrationRequiresApproval" class="ml-2 text-sm text-gray-700">
                  注册需要管理员审核
                </label>
              </div>
              
              <!-- 默认用户角色 -->
              <div>
                <label for="defaultUserRole" class="block text-sm font-medium text-gray-700 mb-1">
                  默认用户角色
                </label>
                <select
                  id="defaultUserRole"
                  v-model="formData.defaultUserRole"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                >
                  <option value="STUDENT">学生</option>
                  <option value="TEACHER">教师</option>
                </select>
              </div>
            </div>
          </div>
          
          <!-- 邮件设置 -->
          <div class="mb-8">
            <h2 class="text-lg font-semibold text-gray-800 mb-4 pb-2 border-b">邮件设置</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- SMTP服务器 -->
              <div>
                <label for="smtpServer" class="block text-sm font-medium text-gray-700 mb-1">
                  SMTP服务器
                </label>
                <input
                  id="smtpServer"
                  v-model="formData.smtpServer"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                  placeholder="例如：smtp.example.com"
                />
              </div>
              
              <!-- SMTP端口 -->
              <div>
                <label for="smtpPort" class="block text-sm font-medium text-gray-700 mb-1">
                  SMTP端口
                </label>
                <input
                  id="smtpPort"
                  v-model.number="formData.smtpPort"
                  type="number"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                  placeholder="例如：587"
                />
              </div>
              
              <!-- 邮箱账号 -->
              <div>
                <label for="smtpUsername" class="block text-sm font-medium text-gray-700 mb-1">
                  邮箱账号
                </label>
                <input
                  id="smtpUsername"
                  v-model="formData.smtpUsername"
                  type="email"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                  placeholder="例如：noreply@example.com"
                />
              </div>
              
              <!-- 邮箱密码 -->
              <div>
                <label for="smtpPassword" class="block text-sm font-medium text-gray-700 mb-1">
                  邮箱密码
                </label>
                <input
                  id="smtpPassword"
                  v-model="formData.smtpPassword"
                  type="password"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                  placeholder="请输入邮箱密码"
                />
              </div>
              
              <!-- 发件人名称 -->
              <div>
                <label for="emailSenderName" class="block text-sm font-medium text-gray-700 mb-1">
                  发件人名称
                </label>
                <input
                  id="emailSenderName"
                  v-model="formData.emailSenderName"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                  placeholder="例如：学习平台"
                />
              </div>
              
              <!-- 测试邮件 -->
              <div class="flex items-end">
                <button
                  type="button"
                  class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 focus:outline-none"
                  @click="testEmailSettings"
                  :disabled="isTestingEmail"
                >
                  <span v-if="isTestingEmail">
                    <i class="fas fa-spinner fa-spin mr-2"></i>测试中...
                  </span>
                  <span v-else>测试邮件设置</span>
                </button>
              </div>
            </div>
          </div>
          
          <!-- 其他设置 -->
          <div class="mb-8">
            <h2 class="text-lg font-semibold text-gray-800 mb-4 pb-2 border-b">其他设置</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- 课程审核 -->
              <div class="flex items-center">
                <input
                  id="courseRequiresApproval"
                  v-model="formData.courseRequiresApproval"
                  type="checkbox"
                  class="form-checkbox h-4 w-4 text-primary focus:ring-primary"
                />
                <label for="courseRequiresApproval" class="ml-2 text-sm text-gray-700">
                  新课程需要管理员审核
                </label>
              </div>
              
              <!-- 资源审核 -->
              <div class="flex items-center">
                <input
                  id="resourceRequiresApproval"
                  v-model="formData.resourceRequiresApproval"
                  type="checkbox"
                  class="form-checkbox h-4 w-4 text-primary focus:ring-primary"
                />
                <label for="resourceRequiresApproval" class="ml-2 text-sm text-gray-700">
                  新资源需要管理员审核
                </label>
              </div>
              
              <!-- 社区帖子审核 -->
              <div class="flex items-center">
                <input
                  id="postRequiresApproval"
                  v-model="formData.postRequiresApproval"
                  type="checkbox"
                  class="form-checkbox h-4 w-4 text-primary focus:ring-primary"
                />
                <label for="postRequiresApproval" class="ml-2 text-sm text-gray-700">
                  社区帖子需要审核
                </label>
              </div>
              
              <!-- 允许AI辅导 -->
              <div class="flex items-center">
                <input
                  id="enableAIGuidance"
                  v-model="formData.enableAIGuidance"
                  type="checkbox"
                  class="form-checkbox h-4 w-4 text-primary focus:ring-primary"
                />
                <label for="enableAIGuidance" class="ml-2 text-sm text-gray-700">
                  启用AI辅导功能
                </label>
              </div>
            </div>
          </div>
          
          <!-- 提交按钮 -->
          <div class="flex justify-end">
            <button
              type="submit"
              class="px-6 py-2 bg-primary text-white rounded-md hover:bg-primary-dark focus:outline-none"
              :disabled="isSaving"
            >
              <span v-if="isSaving">
                <i class="fas fa-spinner fa-spin mr-2"></i>保存中...
              </span>
              <span v-else>保存设置</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getSettings, updateSettings, testEmail } from '@/api/admin/settings';

// 表单数据
const formData = reactive({
  systemName: '在线学习平台',
  logo: '',
  systemDescription: '提供高质量的在线教育服务',
  allowRegistration: true,
  registrationRequiresApproval: false,
  defaultUserRole: 'STUDENT',
  smtpServer: '',
  smtpPort: 587,
  smtpUsername: '',
  smtpPassword: '',
  emailSenderName: '学习平台',
  courseRequiresApproval: true,
  resourceRequiresApproval: false,
  postRequiresApproval: false,
  enableAIGuidance: true
});

// 状态
const isSaving = ref(false);
const isTestingEmail = ref(false);
const logoInput = ref(null);

// 获取设置
const fetchSettings = async () => {
  try {
    const response = await getSettings();
    
    if (response.data) {
      Object.keys(response.data).forEach(key => {
        if (formData[key] !== undefined) {
          formData[key] = response.data[key];
        }
      });
    }
  } catch (error) {
    console.error('获取设置失败:', error);
  }
};

// 保存设置
const saveSettings = async () => {
  try {
    isSaving.value = true;
    
    await updateSettings(formData);
    
    // 显示成功提示
    alert('设置保存成功');
  } catch (error) {
    console.error('保存设置失败:', error);
    
    // 显示错误提示
    alert(`保存设置失败: ${error.message || '未知错误'}`);
  } finally {
    isSaving.value = false;
  }
};

// 测试邮件设置
const testEmailSettings = async () => {
  try {
    isTestingEmail.value = true;
    
    const emailSettings = {
      smtpServer: formData.smtpServer,
      smtpPort: formData.smtpPort,
      smtpUsername: formData.smtpUsername,
      smtpPassword: formData.smtpPassword,
      emailSenderName: formData.emailSenderName
    };
    
    await testEmail(emailSettings);
    
    // 显示成功提示
    alert('测试邮件发送成功，请检查您的邮箱');
  } catch (error) {
    console.error('测试邮件发送失败:', error);
    
    // 显示错误提示
    alert(`测试邮件发送失败: ${error.message || '未知错误'}`);
  } finally {
    isTestingEmail.value = false;
  }
};

// 触发Logo上传
const triggerLogoUpload = () => {
  logoInput.value.click();
};

// 处理Logo变更
const handleLogoChange = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  // 验证文件类型
  if (!file.type.includes('image/')) {
    alert('请选择图片文件');
    return;
  }
  
  // 验证文件大小（最大 2MB）
  if (file.size > 2 * 1024 * 1024) {
    alert('图片大小不能超过 2MB');
    return;
  }
  
  // 创建预览 URL
  const reader = new FileReader();
  reader.onload = (e) => {
    formData.logo = e.target.result;
  };
  reader.readAsDataURL(file);
};

// 初始化
onMounted(() => {
  fetchSettings();
});
</script>
