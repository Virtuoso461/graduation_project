<template>
  <div class="create-todo-page">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">创建待办事项</h1>
      <p class="text-gray-600">添加新的教学相关待办任务</p>
    </div>
    
    <div class="bg-white rounded-lg shadow-md overflow-hidden">
      <div class="p-6">
        <form @submit.prevent="submitForm">
          <div class="space-y-6">
            <!-- 标题 -->
            <div>
              <label for="title" class="block text-sm font-medium text-gray-700 mb-1">
                标题 <span class="text-red-500">*</span>
              </label>
              <input
                id="title"
                v-model="formData.title"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                :class="{ 'border-red-500': errors.title }"
                placeholder="请输入待办事项标题"
                required
              />
              <p v-if="errors.title" class="mt-1 text-sm text-red-500">{{ errors.title }}</p>
            </div>
            
            <!-- 描述 -->
            <div>
              <label for="description" class="block text-sm font-medium text-gray-700 mb-1">
                描述
              </label>
              <textarea
                id="description"
                v-model="formData.description"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                :class="{ 'border-red-500': errors.description }"
                placeholder="请输入待办事项描述"
              ></textarea>
              <p v-if="errors.description" class="mt-1 text-sm text-red-500">{{ errors.description }}</p>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
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
              
              <!-- 优先级 -->
              <div>
                <label for="priority" class="block text-sm font-medium text-gray-700 mb-1">
                  优先级 <span class="text-red-500">*</span>
                </label>
                <select
                  id="priority"
                  v-model="formData.priority"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                  :class="{ 'border-red-500': errors.priority }"
                  required
                >
                  <option value="" disabled>请选择优先级</option>
                  <option value="HIGH">高</option>
                  <option value="MEDIUM">中</option>
                  <option value="LOW">低</option>
                </select>
                <p v-if="errors.priority" class="mt-1 text-sm text-red-500">{{ errors.priority }}</p>
              </div>
            </div>
            
            <!-- 关联项 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                关联项
              </label>
              
              <div class="space-y-4">
                <div class="flex items-center">
                  <input
                    id="hasRelated"
                    v-model="hasRelated"
                    type="checkbox"
                    class="form-checkbox h-4 w-4 text-primary focus:ring-primary"
                  />
                  <label for="hasRelated" class="ml-2 text-sm text-gray-700">
                    关联到课程、作业、考试或学生
                  </label>
                </div>
                
                <div v-if="hasRelated" class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <!-- 关联类型 -->
                  <div>
                    <label for="relatedType" class="block text-sm font-medium text-gray-700 mb-1">
                      关联类型
                    </label>
                    <select
                      id="relatedType"
                      v-model="formData.relatedType"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                      @change="fetchRelatedItems"
                    >
                      <option value="" disabled>请选择关联类型</option>
                      <option value="COURSE">课程</option>
                      <option value="ASSIGNMENT">作业</option>
                      <option value="EXAM">考试</option>
                      <option value="STUDENT">学生</option>
                    </select>
                  </div>
                  
                  <!-- 关联项 -->
                  <div>
                    <label for="relatedId" class="block text-sm font-medium text-gray-700 mb-1">
                      关联项
                    </label>
                    <select
                      id="relatedId"
                      v-model="formData.relatedId"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                      :disabled="!formData.relatedType || isLoadingRelatedItems"
                    >
                      <option v-if="isLoadingRelatedItems" value="" disabled>加载中...</option>
                      <option v-else-if="relatedItems.length === 0" value="" disabled>暂无可选项</option>
                      <option v-else value="" disabled>请选择关联项</option>
                      <option 
                        v-for="item in relatedItems" 
                        :key="item.id" 
                        :value="item.id"
                      >
                        {{ item.title || item.name }}
                      </option>
                    </select>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 提醒设置 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                提醒设置
              </label>
              
              <div class="space-y-4">
                <div class="flex items-center">
                  <input
                    id="enableReminder"
                    v-model="formData.enableReminder"
                    type="checkbox"
                    class="form-checkbox h-4 w-4 text-primary focus:ring-primary"
                  />
                  <label for="enableReminder" class="ml-2 text-sm text-gray-700">
                    启用提醒
                  </label>
                </div>
                
                <div v-if="formData.enableReminder" class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <!-- 提醒时间 -->
                  <div>
                    <label for="reminderTime" class="block text-sm font-medium text-gray-700 mb-1">
                      提醒时间
                    </label>
                    <select
                      id="reminderTime"
                      v-model="formData.reminderTime"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                    >
                      <option value="AT_TIME">准时提醒</option>
                      <option value="FIVE_MINUTES">提前5分钟</option>
                      <option value="FIFTEEN_MINUTES">提前15分钟</option>
                      <option value="THIRTY_MINUTES">提前30分钟</option>
                      <option value="ONE_HOUR">提前1小时</option>
                      <option value="TWO_HOURS">提前2小时</option>
                      <option value="ONE_DAY">提前1天</option>
                      <option value="TWO_DAYS">提前2天</option>
                    </select>
                  </div>
                  
                  <!-- 提醒方式 -->
                  <div>
                    <label for="reminderType" class="block text-sm font-medium text-gray-700 mb-1">
                      提醒方式
                    </label>
                    <select
                      id="reminderType"
                      v-model="formData.reminderType"
                      class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                    >
                      <option value="SYSTEM">系统提醒</option>
                      <option value="EMAIL">邮件提醒</option>
                      <option value="BOTH">系统和邮件</option>
                    </select>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 提交按钮 -->
            <div class="flex justify-end space-x-4">
              <router-link 
                to="/teacher/todos" 
                class="px-6 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none"
              >
                取消
              </router-link>
              <button
                type="submit"
                class="px-6 py-2 bg-primary text-white rounded-md hover:bg-primary-dark focus:outline-none"
                :disabled="isSubmitting"
              >
                <span v-if="isSubmitting">
                  <i class="fas fa-spinner fa-spin mr-2"></i>提交中...
                </span>
                <span v-else>创建待办</span>
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { useRouter } from 'vue-router';
import { createTodo } from '@/api/teacher/todos';
import { getCourses } from '@/api/teacher/courses';
import { getAssignments } from '@/api/teacher/assignments';
import { getExams } from '@/api/teacher/exams';
import { getStudents } from '@/api/teacher/students';
import { validateForm } from '@/utils/validation';

const router = useRouter();

// 表单数据
const formData = reactive({
  title: '',
  description: '',
  dueDate: '',
  priority: 'MEDIUM',
  relatedType: '',
  relatedId: '',
  enableReminder: false,
  reminderTime: 'ONE_HOUR',
  reminderType: 'SYSTEM'
});

// 状态
const errors = reactive({});
const isSubmitting = ref(false);
const hasRelated = ref(false);
const relatedItems = ref([]);
const isLoadingRelatedItems = ref(false);

// 监听关联状态变化
watch(hasRelated, (newValue) => {
  if (!newValue) {
    formData.relatedType = '';
    formData.relatedId = '';
    relatedItems.value = [];
  }
});

// 获取关联项列表
const fetchRelatedItems = async () => {
  if (!formData.relatedType) return;
  
  try {
    isLoadingRelatedItems.value = true;
    relatedItems.value = [];
    formData.relatedId = '';
    
    let response;
    
    switch (formData.relatedType) {
      case 'COURSE':
        response = await getCourses();
        break;
      case 'ASSIGNMENT':
        response = await getAssignments();
        break;
      case 'EXAM':
        response = await getExams();
        break;
      case 'STUDENT':
        response = await getStudents();
        break;
      default:
        return;
    }
    
    relatedItems.value = response.data || [];
  } catch (error) {
    console.error('获取关联项列表失败:', error);
  } finally {
    isLoadingRelatedItems.value = false;
  }
};

// 验证表单
const validateTodoForm = () => {
  const rules = {
    title: [
      { required: true, message: '请输入待办事项标题' },
      { max: 100, message: '标题长度不能超过100个字符' }
    ],
    dueDate: [
      { required: true, message: '请选择截止日期' }
    ],
    priority: [
      { required: true, message: '请选择优先级' }
    ]
  };
  
  const result = validateForm(formData, rules);
  
  // 清除错误
  Object.keys(errors).forEach(key => {
    errors[key] = '';
  });
  
  // 设置错误
  if (!result.valid) {
    Object.keys(result.errors).forEach(key => {
      errors[key] = result.errors[key];
    });
  }
  
  return result.valid;
};

// 提交表单
const submitForm = async () => {
  // 验证表单
  if (!validateTodoForm()) {
    return;
  }
  
  try {
    isSubmitting.value = true;
    
    // 准备提交数据
    const submitData = { ...formData };
    
    // 如果没有关联项，清除关联字段
    if (!hasRelated.value) {
      submitData.relatedType = null;
      submitData.relatedId = null;
    }
    
    // 创建待办
    await createTodo(submitData);
    
    // 跳转到待办列表页
    router.push('/teacher/todos');
  } catch (error) {
    console.error('创建待办失败:', error);
    alert(`创建待办失败: ${error.message || '未知错误'}`);
  } finally {
    isSubmitting.value = false;
  }
};
</script>
