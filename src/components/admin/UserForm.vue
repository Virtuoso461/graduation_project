<template>
  <form @submit.prevent="submitForm" class="space-y-6">
    <!-- 用户基本信息 -->
    <div class="bg-white p-6 rounded-lg shadow-md">
      <h3 class="text-lg font-medium text-gray-900 mb-4">基本信息</h3>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- 用户名/邮箱 -->
        <div>
          <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
            用户名/邮箱 <span class="text-red-500">*</span>
          </label>
          <input
            id="username"
            v-model="formData.username"
            type="email"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.username }"
            placeholder="请输入邮箱地址"
            :disabled="isEdit"
            required
          />
          <p v-if="errors.username" class="mt-1 text-sm text-red-500">{{ errors.username }}</p>
        </div>
        
        <!-- 姓名 -->
        <div>
          <label for="name" class="block text-sm font-medium text-gray-700 mb-1">
            姓名 <span class="text-red-500">*</span>
          </label>
          <input
            id="name"
            v-model="formData.name"
            type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.name }"
            placeholder="请输入姓名"
            required
          />
          <p v-if="errors.name" class="mt-1 text-sm text-red-500">{{ errors.name }}</p>
        </div>
        
        <!-- 密码 -->
        <div v-if="!isEdit">
          <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
            密码 <span class="text-red-500">*</span>
          </label>
          <input
            id="password"
            v-model="formData.password"
            type="password"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.password }"
            placeholder="请输入密码"
            required
          />
          <p v-if="errors.password" class="mt-1 text-sm text-red-500">{{ errors.password }}</p>
        </div>
        
        <!-- 确认密码 -->
        <div v-if="!isEdit">
          <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">
            确认密码 <span class="text-red-500">*</span>
          </label>
          <input
            id="confirmPassword"
            v-model="formData.confirmPassword"
            type="password"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.confirmPassword }"
            placeholder="请再次输入密码"
            required
          />
          <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-500">{{ errors.confirmPassword }}</p>
        </div>
        
        <!-- 用户角色 -->
        <div>
          <label for="role" class="block text-sm font-medium text-gray-700 mb-1">
            用户角色 <span class="text-red-500">*</span>
          </label>
          <select
            id="role"
            v-model="formData.role"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.role }"
            required
          >
            <option value="" disabled>请选择用户角色</option>
            <option value="STUDENT">学生</option>
            <option value="TEACHER">教师</option>
            <option value="ADMIN">管理员</option>
          </select>
          <p v-if="errors.role" class="mt-1 text-sm text-red-500">{{ errors.role }}</p>
        </div>
        
        <!-- 状态 -->
        <div>
          <label for="enabled" class="block text-sm font-medium text-gray-700 mb-1">
            状态
          </label>
          <select
            id="enabled"
            v-model="formData.enabled"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
          >
            <option :value="true">启用</option>
            <option :value="false">禁用</option>
          </select>
        </div>
      </div>
    </div>
    
    <!-- 用户详细信息 -->
    <div class="bg-white p-6 rounded-lg shadow-md">
      <h3 class="text-lg font-medium text-gray-900 mb-4">详细信息</h3>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- 性别 -->
        <div>
          <label for="gender" class="block text-sm font-medium text-gray-700 mb-1">
            性别
          </label>
          <select
            id="gender"
            v-model="formData.gender"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
          >
            <option value="" disabled>请选择性别</option>
            <option value="MALE">男</option>
            <option value="FEMALE">女</option>
            <option value="OTHER">其他</option>
          </select>
        </div>
        
        <!-- 生日 -->
        <div>
          <label for="birthday" class="block text-sm font-medium text-gray-700 mb-1">
            生日
          </label>
          <input
            id="birthday"
            v-model="formData.birthday"
            type="date"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
          />
        </div>
        
        <!-- 手机号 -->
        <div>
          <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">
            手机号
          </label>
          <input
            id="phone"
            v-model="formData.phone"
            type="tel"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.phone }"
            placeholder="请输入手机号"
          />
          <p v-if="errors.phone" class="mt-1 text-sm text-red-500">{{ errors.phone }}</p>
        </div>
        
        <!-- 学号/工号 -->
        <div>
          <label for="studentId" class="block text-sm font-medium text-gray-700 mb-1">
            {{ formData.role === 'STUDENT' ? '学号' : '工号' }}
          </label>
          <input
            id="studentId"
            v-model="formData.studentId"
            type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :placeholder="formData.role === 'STUDENT' ? '请输入学号' : '请输入工号'"
          />
        </div>
        
        <!-- 头像 -->
        <div class="col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-1">
            头像
          </label>
          <div class="flex items-center space-x-4">
            <div 
              class="w-24 h-24 border border-gray-300 rounded-full overflow-hidden flex items-center justify-center bg-gray-100"
            >
              <img 
                v-if="formData.avatar" 
                :src="formData.avatar" 
                alt="用户头像" 
                class="w-full h-full object-cover"
              />
              <span v-else class="text-gray-400">
                <i class="fas fa-user text-3xl"></i>
              </span>
            </div>
            
            <div>
              <button
                type="button"
                class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 focus:outline-none"
                @click="triggerFileInput"
              >
                选择图片
              </button>
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                class="hidden"
                @change="handleFileChange"
              />
              <p class="mt-1 text-xs text-gray-500">
                支持 JPG、PNG 格式，建议尺寸 200x200 像素
              </p>
            </div>
          </div>
          <p v-if="errors.avatar" class="mt-1 text-sm text-red-500">{{ errors.avatar }}</p>
        </div>
        
        <!-- 简介 -->
        <div class="col-span-2">
          <label for="bio" class="block text-sm font-medium text-gray-700 mb-1">
            简介
          </label>
          <textarea
            id="bio"
            v-model="formData.bio"
            rows="3"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            placeholder="请输入用户简介"
          ></textarea>
        </div>
      </div>
    </div>
    
    <!-- 提交按钮 -->
    <div class="flex justify-end space-x-4">
      <button
        type="button"
        class="px-6 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none"
        @click="$emit('cancel')"
      >
        取消
      </button>
      <button
        type="submit"
        class="px-6 py-2 bg-primary text-white rounded-md hover:bg-primary-dark focus:outline-none"
        :disabled="isSubmitting"
      >
        <span v-if="isSubmitting">
          <i class="fas fa-spinner fa-spin mr-2"></i>提交中...
        </span>
        <span v-else>{{ isEdit ? '保存修改' : '创建用户' }}</span>
      </button>
    </div>
  </form>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  },
  isEdit: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['submit', 'cancel']);

// 表单数据
const formData = reactive({
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
  role: '',
  enabled: true,
  gender: '',
  birthday: '',
  phone: '',
  studentId: '',
  avatar: '',
  bio: ''
});

// 错误信息
const errors = reactive({});

// 文件输入引用
const fileInput = ref(null);

// 提交状态
const isSubmitting = ref(false);

// 初始化表单数据
onMounted(() => {
  if (props.initialData && Object.keys(props.initialData).length > 0) {
    Object.keys(formData).forEach(key => {
      if (props.initialData[key] !== undefined) {
        formData[key] = props.initialData[key];
      }
    });
  }
});

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value.click();
};

// 处理文件选择
const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  // 验证文件类型
  if (!file.type.includes('image/')) {
    errors.avatar = '请选择图片文件';
    return;
  }
  
  // 验证文件大小（最大 2MB）
  if (file.size > 2 * 1024 * 1024) {
    errors.avatar = '图片大小不能超过 2MB';
    return;
  }
  
  // 清除错误
  errors.avatar = '';
  
  // 创建预览 URL
  const reader = new FileReader();
  reader.onload = (e) => {
    formData.avatar = e.target.result;
  };
  reader.readAsDataURL(file);
};

// 验证表单
const validateForm = () => {
  // 基本信息验证
  errors.username = !formData.username.trim() ? '请输入用户名/邮箱' : '';
  if (formData.username && !isValidEmail(formData.username)) {
    errors.username = '请输入有效的邮箱地址';
  }
  
  errors.name = !formData.name.trim() ? '请输入姓名' : '';
  errors.role = !formData.role ? '请选择用户角色' : '';
  
  // 密码验证（仅创建用户时）
  if (!props.isEdit) {
    errors.password = !formData.password ? '请输入密码' : '';
    if (formData.password && formData.password.length < 6) {
      errors.password = '密码长度不能少于6个字符';
    }
    
    errors.confirmPassword = !formData.confirmPassword ? '请确认密码' : '';
    if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = '两次输入的密码不一致';
    }
  }
  
  // 手机号验证
  if (formData.phone && !isValidPhone(formData.phone)) {
    errors.phone = '请输入有效的手机号';
  }
  
  // 检查是否有错误
  return !Object.values(errors).some(error => error);
};

// 验证邮箱
const isValidEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
};

// 验证手机号
const isValidPhone = (phone) => {
  const re = /^1[3-9]\d{9}$/;
  return re.test(phone);
};

// 提交表单
const submitForm = async () => {
  // 验证表单
  if (!validateForm()) {
    return;
  }
  
  try {
    isSubmitting.value = true;
    
    // 提交表单数据
    const submitData = { ...formData };
    
    // 如果是编辑模式，不提交密码字段
    if (props.isEdit) {
      delete submitData.password;
      delete submitData.confirmPassword;
    }
    
    emit('submit', submitData);
  } catch (error) {
    console.error('提交表单失败:', error);
  } finally {
    isSubmitting.value = false;
  }
};
</script>
