<template>
  <div class="bg-white rounded-lg shadow-md overflow-hidden">
    <!-- 课程基本信息 -->
    <div class="p-6 border-b border-gray-200">
      <div class="flex justify-between items-start">
        <h3 class="text-xl font-semibold text-gray-800">{{ course.name }}</h3>
        <div 
          class="text-xs font-medium px-2 py-1 rounded-full"
          :class="statusClass"
        >
          {{ statusText }}
        </div>
      </div>
      
      <div class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
        <div>
          <span class="text-gray-500">教师:</span>
          <span class="ml-2 font-medium">{{ course.teacherName }}</span>
        </div>
        
        <div>
          <span class="text-gray-500">分类:</span>
          <span class="ml-2 font-medium">{{ course.category }}</span>
        </div>
        
        <div>
          <span class="text-gray-500">难度:</span>
          <span class="ml-2 font-medium">{{ difficultyText }}</span>
        </div>
        
        <div>
          <span class="text-gray-500">学分:</span>
          <span class="ml-2 font-medium">{{ course.credits }}</span>
        </div>
        
        <div>
          <span class="text-gray-500">时长:</span>
          <span class="ml-2 font-medium">{{ course.duration }} 小时</span>
        </div>
        
        <div>
          <span class="text-gray-500">创建时间:</span>
          <span class="ml-2 font-medium">{{ formatDate(course.createdAt) }}</span>
        </div>
      </div>
      
      <div class="mt-4">
        <h4 class="text-sm font-medium text-gray-700 mb-2">课程描述</h4>
        <p class="text-gray-600 text-sm">{{ course.description }}</p>
      </div>
    </div>
    
    <!-- 课程内容预览 -->
    <div class="p-6 border-b border-gray-200">
      <h4 class="text-sm font-medium text-gray-700 mb-2">课程内容预览</h4>
      
      <div v-if="course.chapters && course.chapters.length > 0" class="space-y-3">
        <div 
          v-for="(chapter, index) in course.chapters" 
          :key="index"
          class="border border-gray-200 rounded-md overflow-hidden"
        >
          <div class="bg-gray-50 px-4 py-2 flex justify-between items-center">
            <span class="font-medium">{{ chapter.title }}</span>
            <span class="text-xs text-gray-500">{{ chapter.sections?.length || 0 }} 小节</span>
          </div>
          
          <div v-if="chapter.sections && chapter.sections.length > 0" class="divide-y divide-gray-100">
            <div 
              v-for="(section, sIndex) in chapter.sections" 
              :key="sIndex"
              class="px-4 py-2 text-sm flex justify-between items-center"
            >
              <span>{{ section.title }}</span>
              <span class="text-xs text-gray-500">{{ formatDuration(section.duration) }}</span>
            </div>
          </div>
          
          <div v-else class="px-4 py-2 text-sm text-gray-500">
            暂无小节内容
          </div>
        </div>
      </div>
      
      <div v-else class="text-gray-500 text-sm">
        暂无章节内容
      </div>
    </div>
    
    <!-- 审核操作 -->
    <div class="p-6">
      <h4 class="text-sm font-medium text-gray-700 mb-2">审核意见</h4>
      
      <div class="mb-4">
        <textarea
          v-model="feedback"
          rows="3"
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
          placeholder="请输入审核意见（可选）"
        ></textarea>
      </div>
      
      <div class="flex justify-end space-x-3">
        <button
          type="button"
          class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none"
          @click="$emit('cancel')"
        >
          取消
        </button>
        
        <button
          type="button"
          class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none"
          @click="handleReject"
          :disabled="isSubmitting"
        >
          <span v-if="isSubmitting && action === 'reject'">
            <i class="fas fa-spinner fa-spin mr-2"></i>处理中...
          </span>
          <span v-else>拒绝</span>
        </button>
        
        <button
          type="button"
          class="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 focus:outline-none"
          @click="handleApprove"
          :disabled="isSubmitting"
        >
          <span v-if="isSubmitting && action === 'approve'">
            <i class="fas fa-spinner fa-spin mr-2"></i>处理中...
          </span>
          <span v-else>通过</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  course: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['approve', 'reject', 'cancel']);

// 审核意见
const feedback = ref('');

// 提交状态
const isSubmitting = ref(false);

// 当前操作
const action = ref('');

// 状态样式
const statusClass = computed(() => {
  const approved = props.course.approved;
  if (approved === true) return 'bg-green-100 text-green-800';
  if (approved === false) return 'bg-red-100 text-red-800';
  return 'bg-yellow-100 text-yellow-800';
});

// 状态文本
const statusText = computed(() => {
  const approved = props.course.approved;
  if (approved === true) return '已通过';
  if (approved === false) return '已拒绝';
  return '待审核';
});

// 难度文本
const difficultyText = computed(() => {
  const difficulty = props.course.difficulty;
  if (difficulty === 'BEGINNER') return '初级';
  if (difficulty === 'INTERMEDIATE') return '中级';
  if (difficulty === 'ADVANCED') return '高级';
  return '未知';
});

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 格式化时长
const formatDuration = (minutes) => {
  if (!minutes) return '未知';
  
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  
  if (hours > 0) {
    return `${hours}小时${mins > 0 ? ` ${mins}分钟` : ''}`;
  }
  
  return `${mins}分钟`;
};

// 处理通过
const handleApprove = async () => {
  try {
    isSubmitting.value = true;
    action.value = 'approve';
    
    // 提交审核结果
    emit('approve', {
      courseId: props.course.id,
      feedback: feedback.value
    });
  } catch (error) {
    console.error('审核操作失败:', error);
  } finally {
    isSubmitting.value = false;
  }
};

// 处理拒绝
const handleReject = async () => {
  try {
    isSubmitting.value = true;
    action.value = 'reject';
    
    // 提交审核结果
    emit('reject', {
      courseId: props.course.id,
      feedback: feedback.value
    });
  } catch (error) {
    console.error('审核操作失败:', error);
  } finally {
    isSubmitting.value = false;
  }
};
</script>
