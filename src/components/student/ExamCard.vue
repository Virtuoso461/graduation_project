<template>
  <div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
    <div class="p-5">
      <div class="flex justify-between items-start">
        <h3 class="text-lg font-semibold text-gray-800 mb-2 truncate">
          {{ exam.title }}
        </h3>
        <div 
          class="text-xs font-medium px-2 py-1 rounded-full"
          :class="statusClass"
        >
          {{ statusText }}
        </div>
      </div>
      
      <p class="text-gray-600 text-sm mb-4 line-clamp-2">
        {{ exam.description }}
      </p>
      
      <div class="grid grid-cols-2 gap-2 mb-4 text-sm">
        <div class="flex items-center text-gray-500">
          <i class="far fa-calendar-alt mr-1"></i>
          <span>开始: {{ formatDate(exam.startTime) }}</span>
        </div>
        
        <div class="flex items-center text-gray-500">
          <i class="far fa-calendar-alt mr-1"></i>
          <span>结束: {{ formatDate(exam.endTime) }}</span>
        </div>
        
        <div class="flex items-center text-gray-500">
          <i class="far fa-clock mr-1"></i>
          <span>时长: {{ exam.duration }} 分钟</span>
        </div>
        
        <div class="flex items-center text-gray-500">
          <i class="fas fa-star mr-1"></i>
          <span>总分: {{ exam.totalScore }} 分</span>
        </div>
      </div>
      
      <div v-if="exam.status === 'COMPLETED'" class="mb-4">
        <div class="flex items-center justify-between">
          <span class="text-sm text-gray-600">得分:</span>
          <span class="font-medium" :class="scoreClass">
            {{ exam.score }}/{{ exam.totalScore }}
          </span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-2 mt-1">
          <div 
            class="h-2 rounded-full" 
            :class="scoreBarClass"
            :style="{ width: `${(exam.score / exam.totalScore) * 100}%` }"
          ></div>
        </div>
      </div>
      
      <div class="mt-4 flex justify-between items-center">
        <div class="text-xs text-gray-500">
          <i class="far fa-folder mr-1"></i>
          <span>{{ exam.courseName || '未关联课程' }}</span>
        </div>
        
        <router-link 
          :to="actionLink" 
          class="text-primary hover:text-primary-dark text-sm font-medium"
          :class="{ 'pointer-events-none opacity-50': !isActionEnabled }"
        >
          {{ actionText }}
          <i class="fas fa-chevron-right ml-1 text-xs"></i>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  exam: {
    type: Object,
    required: true
  }
});

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未设置';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 状态样式
const statusClass = computed(() => {
  const status = props.exam.status;
  if (status === 'UPCOMING') return 'bg-blue-100 text-blue-800';
  if (status === 'ONGOING') return 'bg-yellow-100 text-yellow-800';
  if (status === 'COMPLETED') return 'bg-green-100 text-green-800';
  if (status === 'MISSED') return 'bg-red-100 text-red-800';
  return 'bg-gray-100 text-gray-800';
});

// 状态文本
const statusText = computed(() => {
  const status = props.exam.status;
  if (status === 'UPCOMING') return '即将开始';
  if (status === 'ONGOING') return '进行中';
  if (status === 'COMPLETED') return '已完成';
  if (status === 'MISSED') return '已错过';
  return '未知状态';
});

// 分数样式
const scoreClass = computed(() => {
  const score = props.exam.score;
  const totalScore = props.exam.totalScore;
  const percentage = (score / totalScore) * 100;
  
  if (percentage >= 90) return 'text-green-600';
  if (percentage >= 60) return 'text-blue-600';
  return 'text-red-600';
});

// 分数条样式
const scoreBarClass = computed(() => {
  const score = props.exam.score;
  const totalScore = props.exam.totalScore;
  const percentage = (score / totalScore) * 100;
  
  if (percentage >= 90) return 'bg-green-500';
  if (percentage >= 60) return 'bg-blue-500';
  return 'bg-red-500';
});

// 操作链接
const actionLink = computed(() => {
  const status = props.exam.status;
  const id = props.exam.id;
  
  if (status === 'ONGOING') return `/student/exams/${id}/take`;
  if (status === 'COMPLETED') return `/student/exams/${id}/result`;
  return `/student/exams/${id}`;
});

// 操作文本
const actionText = computed(() => {
  const status = props.exam.status;
  if (status === 'UPCOMING') return '查看详情';
  if (status === 'ONGOING') return '开始考试';
  if (status === 'COMPLETED') return '查看结果';
  if (status === 'MISSED') return '查看详情';
  return '查看详情';
});

// 是否启用操作
const isActionEnabled = computed(() => {
  const status = props.exam.status;
  return status !== 'UPCOMING' || isExamStartingSoon.value;
});

// 考试是否即将开始（24小时内）
const isExamStartingSoon = computed(() => {
  if (!props.exam.startTime) return false;
  
  const now = new Date();
  const startTime = new Date(props.exam.startTime);
  const diffHours = (startTime - now) / (1000 * 60 * 60);
  
  return diffHours > 0 && diffHours <= 24;
});
</script>
