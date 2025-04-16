<template>
  <form @submit.prevent="submitForm" class="space-y-6">
    <!-- 作业基本信息 -->
    <div class="bg-white p-6 rounded-lg shadow-md">
      <h3 class="text-lg font-medium text-gray-900 mb-4">基本信息</h3>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- 作业标题 -->
        <div class="col-span-2">
          <label for="title" class="block text-sm font-medium text-gray-700 mb-1">
            作业标题 <span class="text-red-500">*</span>
          </label>
          <input
            id="title"
            v-model="formData.title"
            type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.title }"
            placeholder="请输入作业标题"
            required
          />
          <p v-if="errors.title" class="mt-1 text-sm text-red-500">{{ errors.title }}</p>
        </div>
        
        <!-- 关联课程 -->
        <div>
          <label for="courseId" class="block text-sm font-medium text-gray-700 mb-1">
            关联课程 <span class="text-red-500">*</span>
          </label>
          <select
            id="courseId"
            v-model="formData.courseId"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.courseId }"
            required
          >
            <option value="" disabled>请选择关联课程</option>
            <option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.name }}
            </option>
          </select>
          <p v-if="errors.courseId" class="mt-1 text-sm text-red-500">{{ errors.courseId }}</p>
        </div>
        
        <!-- 截止日期 -->
        <div>
          <label for="dueDate" class="block text-sm font-medium text-gray-700 mb-1">
            截止日期 <span class="text-red-500">*</span>
          </label>
          <input
            id="dueDate"
            v-model="formData.dueDate"
            type="datetime-local"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.dueDate }"
            required
          />
          <p v-if="errors.dueDate" class="mt-1 text-sm text-red-500">{{ errors.dueDate }}</p>
        </div>
        
        <!-- 最高分数 -->
        <div>
          <label for="maxScore" class="block text-sm font-medium text-gray-700 mb-1">
            最高分数 <span class="text-red-500">*</span>
          </label>
          <input
            id="maxScore"
            v-model.number="formData.maxScore"
            type="number"
            min="0"
            max="100"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.maxScore }"
            placeholder="请输入最高分数"
            required
          />
          <p v-if="errors.maxScore" class="mt-1 text-sm text-red-500">{{ errors.maxScore }}</p>
        </div>
        
        <!-- 作业描述 -->
        <div class="col-span-2">
          <label for="description" class="block text-sm font-medium text-gray-700 mb-1">
            作业描述 <span class="text-red-500">*</span>
          </label>
          <textarea
            id="description"
            v-model="formData.description"
            rows="4"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.description }"
            placeholder="请输入作业描述"
            required
          ></textarea>
          <p v-if="errors.description" class="mt-1 text-sm text-red-500">{{ errors.description }}</p>
        </div>
      </div>
    </div>
    
    <!-- 作业内容 -->
    <div class="bg-white p-6 rounded-lg shadow-md">
      <h3 class="text-lg font-medium text-gray-900 mb-4">作业内容</h3>
      
      <div class="space-y-4">
        <!-- 作业类型 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            作业类型 <span class="text-red-500">*</span>
          </label>
          <div class="flex space-x-4">
            <label class="inline-flex items-center">
              <input
                type="radio"
                v-model="formData.type"
                value="TEXT"
                class="form-radio text-primary focus:ring-primary"
              />
              <span class="ml-2">文本作业</span>
            </label>
            <label class="inline-flex items-center">
              <input
                type="radio"
                v-model="formData.type"
                value="FILE"
                class="form-radio text-primary focus:ring-primary"
              />
              <span class="ml-2">文件作业</span>
            </label>
            <label class="inline-flex items-center">
              <input
                type="radio"
                v-model="formData.type"
                value="MIXED"
                class="form-radio text-primary focus:ring-primary"
              />
              <span class="ml-2">混合作业</span>
            </label>
          </div>
          <p v-if="errors.type" class="mt-1 text-sm text-red-500">{{ errors.type }}</p>
        </div>
        
        <!-- 作业内容 -->
        <div>
          <label for="content" class="block text-sm font-medium text-gray-700 mb-1">
            作业内容 <span class="text-red-500">*</span>
          </label>
          <textarea
            id="content"
            v-model="formData.content"
            rows="8"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.content }"
            placeholder="请输入作业内容，可以包含问题、要求等"
            required
          ></textarea>
          <p v-if="errors.content" class="mt-1 text-sm text-red-500">{{ errors.content }}</p>
        </div>
        
        <!-- 附件 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            附件
          </label>
          
          <div class="space-y-2">
            <!-- 已上传的附件 -->
            <div 
              v-for="(file, index) in formData.attachments" 
              :key="index"
              class="flex items-center justify-between p-2 border border-gray-200 rounded-md"
            >
              <div class="flex items-center">
                <i class="fas fa-file-alt text-gray-500 mr-2"></i>
                <span class="text-sm">{{ file.name }}</span>
              </div>
              <button
                type="button"
                class="text-red-500 hover:text-red-700 focus:outline-none"
                @click="removeAttachment(index)"
              >
                <i class="fas fa-times"></i>
              </button>
            </div>
            
            <!-- 上传按钮 -->
            <div class="flex items-center">
              <button
                type="button"
                class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 focus:outline-none"
                @click="triggerFileInput"
              >
                <i class="fas fa-upload mr-2"></i>
                上传附件
              </button>
              <input
                ref="fileInput"
                type="file"
                class="hidden"
                @change="handleFileChange"
                multiple
              />
              <span class="ml-2 text-xs text-gray-500">
                支持多种文件格式，单个文件大小不超过 10MB
              </span>
            </div>
          </div>
          <p v-if="errors.attachments" class="mt-1 text-sm text-red-500">{{ errors.attachments }}</p>
        </div>
      </div>
    </div>
    
    <!-- 提交设置 -->
    <div class="bg-white p-6 rounded-lg shadow-md">
      <h3 class="text-lg font-medium text-gray-900 mb-4">提交设置</h3>
      
      <div class="space-y-4">
        <!-- 允许迟交 -->
        <div class="flex items-center">
          <input
            id="allowLateSubmission"
            v-model="formData.allowLateSubmission"
            type="checkbox"
            class="form-checkbox text-primary focus:ring-primary h-4 w-4"
          />
          <label for="allowLateSubmission" class="ml-2 text-sm text-gray-700">
            允许迟交
          </label>
        </div>
        
        <!-- 迟交截止日期 -->
        <div v-if="formData.allowLateSubmission">
          <label for="lateSubmissionDeadline" class="block text-sm font-medium text-gray-700 mb-1">
            迟交截止日期 <span class="text-red-500">*</span>
          </label>
          <input
            id="lateSubmissionDeadline"
            v-model="formData.lateSubmissionDeadline"
            type="datetime-local"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
            :class="{ 'border-red-500': errors.lateSubmissionDeadline }"
            required
          />
          <p v-if="errors.lateSubmissionDeadline" class="mt-1 text-sm text-red-500">{{ errors.lateSubmissionDeadline }}</p>
        </div>
        
        <!-- 迟交扣分 -->
        <div v-if="formData.allowLateSubmission">
          <label for="lateSubmissionPenalty" class="block text-sm font-medium text-gray-700 mb-1">
            迟交扣分百分比
          </label>
          <div class="flex items-center">
            <input
              id="lateSubmissionPenalty"
              v-model.number="formData.lateSubmissionPenalty"
              type="number"
              min="0"
              max="100"
              class="w-24 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
              :class="{ 'border-red-500': errors.lateSubmissionPenalty }"
            />
            <span class="ml-2">%</span>
          </div>
          <p v-if="errors.lateSubmissionPenalty" class="mt-1 text-sm text-red-500">{{ errors.lateSubmissionPenalty }}</p>
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
        <span v-else>{{ isEdit ? '保存修改' : '创建作业' }}</span>
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
  title: '',
  courseId: '',
  dueDate: '',
  maxScore: 100,
  description: '',
  type: 'TEXT',
  content: '',
  attachments: [],
  allowLateSubmission: false,
  lateSubmissionDeadline: '',
  lateSubmissionPenalty: 10
});

// 错误信息
const errors = reactive({});

// 文件输入引用
const fileInput = ref(null);

// 提交状态
const isSubmitting = ref(false);

// 课程列表（实际应用中应从API获取）
const courses = ref([
  { id: 1, name: 'Java编程基础' },
  { id: 2, name: '数据结构与算法' },
  { id: 3, name: '数据库原理' },
  { id: 4, name: '操作系统' },
  { id: 5, name: '计算机网络' }
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
  const files = event.target.files;
  if (!files || files.length === 0) return;
  
  // 验证文件大小（最大 10MB）
  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    
    if (file.size > 10 * 1024 * 1024) {
      errors.attachments = `文件 ${file.name} 大小超过 10MB`;
      return;
    }
    
    // 添加文件
    formData.attachments.push({
      name: file.name,
      size: file.size,
      type: file.type,
      file: file
    });
  }
  
  // 清除错误
  errors.attachments = '';
  
  // 清除文件输入
  event.target.value = '';
};

// 移除附件
const removeAttachment = (index) => {
  formData.attachments.splice(index, 1);
};

// 验证表单
const validateForm = () => {
  errors.title = !formData.title.trim() ? '请输入作业标题' : '';
  errors.courseId = !formData.courseId ? '请选择关联课程' : '';
  errors.dueDate = !formData.dueDate ? '请选择截止日期' : '';
  errors.maxScore = formData.maxScore === null || formData.maxScore <= 0 ? '请输入有效的最高分数' : '';
  errors.description = !formData.description.trim() ? '请输入作业描述' : '';
  errors.type = !formData.type ? '请选择作业类型' : '';
  errors.content = !formData.content.trim() ? '请输入作业内容' : '';
  
  // 验证迟交设置
  if (formData.allowLateSubmission) {
    errors.lateSubmissionDeadline = !formData.lateSubmissionDeadline ? '请选择迟交截止日期' : '';
    
    // 验证迟交截止日期是否晚于正常截止日期
    if (formData.dueDate && formData.lateSubmissionDeadline) {
      const dueDate = new Date(formData.dueDate);
      const lateDeadline = new Date(formData.lateSubmissionDeadline);
      
      if (lateDeadline <= dueDate) {
        errors.lateSubmissionDeadline = '迟交截止日期必须晚于正常截止日期';
      }
    }
    
    // 验证迟交扣分百分比
    if (formData.lateSubmissionPenalty < 0 || formData.lateSubmissionPenalty > 100) {
      errors.lateSubmissionPenalty = '迟交扣分百分比必须在 0-100 之间';
    }
  }
  
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
