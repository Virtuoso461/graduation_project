<template>
  <form @submit.prevent="submitForm" class="space-y-6">
    <!-- 课程基本信息 -->
    <div class="bg-white p-6 rounded-lg shadow-md">
      <h3 class="text-lg font-medium text-gray-900 mb-4">基本信息</h3>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- 课程名称 -->
        <div class="col-span-2">
          <label for="name" class="block text-sm font-medium text-gray-700 mb-1">
            课程名称 <span class="text-red-500">*</span>
          </label>
          <input
            id="name"
            v-model="formData.name"
            type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.name }"
            placeholder="请输入课程名称"
            required
          />
          <p v-if="errors.name" class="mt-1 text-sm text-red-500">{{ errors.name }}</p>
        </div>
        
        <!-- 课程分类 -->
        <div>
          <label for="category" class="block text-sm font-medium text-gray-700 mb-1">
            课程分类 <span class="text-red-500">*</span>
          </label>
          <select
            id="category"
            v-model="formData.category"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.category }"
            required
          >
            <option value="" disabled>请选择课程分类</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
          <p v-if="errors.category" class="mt-1 text-sm text-red-500">{{ errors.category }}</p>
        </div>
        
        <!-- 课程难度 -->
        <div>
          <label for="difficulty" class="block text-sm font-medium text-gray-700 mb-1">
            课程难度 <span class="text-red-500">*</span>
          </label>
          <select
            id="difficulty"
            v-model="formData.difficulty"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.difficulty }"
            required
          >
            <option value="" disabled>请选择课程难度</option>
            <option value="BEGINNER">初级</option>
            <option value="INTERMEDIATE">中级</option>
            <option value="ADVANCED">高级</option>
          </select>
          <p v-if="errors.difficulty" class="mt-1 text-sm text-red-500">{{ errors.difficulty }}</p>
        </div>
        
        <!-- 课程学分 -->
        <div>
          <label for="credits" class="block text-sm font-medium text-gray-700 mb-1">
            课程学分 <span class="text-red-500">*</span>
          </label>
          <input
            id="credits"
            v-model.number="formData.credits"
            type="number"
            min="0"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.credits }"
            placeholder="请输入课程学分"
            required
          />
          <p v-if="errors.credits" class="mt-1 text-sm text-red-500">{{ errors.credits }}</p>
        </div>
        
        <!-- 课程时长 -->
        <div>
          <label for="duration" class="block text-sm font-medium text-gray-700 mb-1">
            课程时长（小时） <span class="text-red-500">*</span>
          </label>
          <input
            id="duration"
            v-model.number="formData.duration"
            type="number"
            min="0"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.duration }"
            placeholder="请输入课程时长"
            required
          />
          <p v-if="errors.duration" class="mt-1 text-sm text-red-500">{{ errors.duration }}</p>
        </div>
        
        <!-- 课程描述 -->
        <div class="col-span-2">
          <label for="description" class="block text-sm font-medium text-gray-700 mb-1">
            课程描述 <span class="text-red-500">*</span>
          </label>
          <textarea
            id="description"
            v-model="formData.description"
            rows="4"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.description }"
            placeholder="请输入课程描述"
            required
          ></textarea>
          <p v-if="errors.description" class="mt-1 text-sm text-red-500">{{ errors.description }}</p>
        </div>
        
        <!-- 课程封面 -->
        <div class="col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-1">
            课程封面
          </label>
          <div class="flex items-center space-x-4">
            <div 
              class="w-32 h-32 border border-gray-300 rounded-md overflow-hidden flex items-center justify-center bg-gray-100"
            >
              <img 
                v-if="formData.coverImage" 
                :src="formData.coverImage" 
                alt="课程封面" 
                class="w-full h-full object-cover"
              />
              <span v-else class="text-gray-400">
                <i class="fas fa-image text-3xl"></i>
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
                支持 JPG、PNG 格式，建议尺寸 800x450 像素
              </p>
            </div>
          </div>
          <p v-if="errors.coverImage" class="mt-1 text-sm text-red-500">{{ errors.coverImage }}</p>
        </div>
      </div>
    </div>
    
    <!-- 课程标签 -->
    <div class="bg-white p-6 rounded-lg shadow-md">
      <h3 class="text-lg font-medium text-gray-900 mb-4">课程标签</h3>
      
      <div class="space-y-4">
        <div class="flex flex-wrap gap-2">
          <div 
            v-for="(tag, index) in formData.tags" 
            :key="index"
            class="bg-gray-100 text-gray-700 px-3 py-1 rounded-full flex items-center"
          >
            <span>{{ tag }}</span>
            <button 
              type="button" 
              class="ml-2 text-gray-500 hover:text-red-500 focus:outline-none"
              @click="removeTag(index)"
            >
              <i class="fas fa-times"></i>
            </button>
          </div>
          
          <div v-if="formData.tags.length === 0" class="text-gray-500 text-sm">
            暂无标签，请添加课程相关标签
          </div>
        </div>
        
        <div class="flex">
          <input
            v-model="newTag"
            type="text"
            class="flex-1 px-3 py-2 border border-gray-300 rounded-l-md focus:outline-none focus:ring-primary focus:border-primary"
            placeholder="输入标签名称"
            @keyup.enter.prevent="addTag"
          />
          <button
            type="button"
            class="px-4 py-2 bg-primary text-white rounded-r-md hover:bg-primary-dark focus:outline-none"
            @click="addTag"
          >
            添加
          </button>
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
        <span v-else>{{ isEdit ? '保存修改' : '创建课程' }}</span>
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
  name: '',
  category: '',
  difficulty: '',
  credits: null,
  duration: null,
  description: '',
  coverImage: '',
  tags: []
});

// 错误信息
const errors = reactive({});

// 新标签
const newTag = ref('');

// 文件输入引用
const fileInput = ref(null);

// 提交状态
const isSubmitting = ref(false);

// 课程分类列表（实际应用中应从API获取）
const categories = ref([
  { id: 1, name: '编程开发' },
  { id: 2, name: '数据科学' },
  { id: 3, name: '人工智能' },
  { id: 4, name: '网络安全' },
  { id: 5, name: '云计算' },
  { id: 6, name: '移动开发' },
  { id: 7, name: '前端开发' },
  { id: 8, name: '后端开发' },
  { id: 9, name: '数据库' },
  { id: 10, name: '运维' }
]);

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
    errors.coverImage = '请选择图片文件';
    return;
  }
  
  // 验证文件大小（最大 2MB）
  if (file.size > 2 * 1024 * 1024) {
    errors.coverImage = '图片大小不能超过 2MB';
    return;
  }
  
  // 清除错误
  errors.coverImage = '';
  
  // 创建预览 URL
  const reader = new FileReader();
  reader.onload = (e) => {
    formData.coverImage = e.target.result;
  };
  reader.readAsDataURL(file);
};

// 添加标签
const addTag = () => {
  if (!newTag.value.trim()) return;
  
  // 检查标签是否已存在
  if (formData.tags.includes(newTag.value.trim())) {
    return;
  }
  
  // 添加标签
  formData.tags.push(newTag.value.trim());
  newTag.value = '';
};

// 移除标签
const removeTag = (index) => {
  formData.tags.splice(index, 1);
};

// 验证表单
const validateForm = () => {
  errors.name = !formData.name.trim() ? '请输入课程名称' : '';
  errors.category = !formData.category ? '请选择课程分类' : '';
  errors.difficulty = !formData.difficulty ? '请选择课程难度' : '';
  errors.credits = formData.credits === null || formData.credits < 0 ? '请输入有效的课程学分' : '';
  errors.duration = formData.duration === null || formData.duration < 0 ? '请输入有效的课程时长' : '';
  errors.description = !formData.description.trim() ? '请输入课程描述' : '';
  
  // 检查是否有错误
  return !Object.values(errors).some(error => error);
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
    emit('submit', { ...formData });
  } catch (error) {
    console.error('提交表单失败:', error);
  } finally {
    isSubmitting.value = false;
  }
};
</script>
