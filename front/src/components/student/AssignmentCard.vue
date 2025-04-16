<template>
  <div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
    <div class="p-5">
      <div class="flex justify-between items-start">
        <h3 class="text-lg font-semibold text-gray-800 mb-2 truncate">
          {{ assignment.title }}
        </h3>
        <div 
          class="text-xs font-medium px-2 py-1 rounded-full"
          :class="statusClass"
        >
          {{ statusText }}
        </div>
      </div>
      
      <p class="text-gray-600 text-sm mb-4 line-clamp-2">
        {{ assignment.description }}
      </p>
      
      <div class="flex justify-between items-center text-sm text-gray-500">
        <div class="flex items-center">
          <i class="far fa-calendar-alt mr-1"></i>
          <span>截止日期: {{ formatDate(assignment.dueDate) }}</span>
        </div>
        
        <div v-if="assignment.score !== undefined && assignment.score !== null">
          <span class="font-medium" :class="scoreClass">
            {{ assignment.score }}/{{ assignment.maxScore || 100 }}
          </span>
        </div>
      </div>
      
      <div class="mt-4 flex justify-between items-center">
        <div class="text-xs text-gray-500">
          <i class="far fa-folder mr-1"></i>
          <span>{{ assignment.courseName || '未关联课程' }}</span>
        </div>
        
        <router-link 
          :to="`/student/assignments/${assignment.id}`" 
          class="text-primary hover:text-primary-dark text-sm font-medium"
        >
          {{ actionText }}
          <i class="fas fa-chevron-right ml-1 text-xs"></i>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { formatDate } from '@/utils/date';

interface Assignment {
  id: number;
  title: string;
  description: string;
  dueDate: string;
  status: 'PENDING' | 'SUBMITTED' | 'GRADED' | 'OVERDUE';
  score?: number;
  maxScore?: number;
  courseName?: string;
}

const props = defineProps<{
  assignment: Assignment;
}>();

// 状态样式
const statusClass = computed(() => {
  const status = props.assignment.status;
  if (status === 'PENDING') return 'bg-yellow-100 text-yellow-800';
  if (status === 'SUBMITTED') return 'bg-blue-100 text-blue-800';
  if (status === 'GRADED') return 'bg-green-100 text-green-800';
  if (status === 'OVERDUE') return 'bg-red-100 text-red-800';
  return 'bg-gray-100 text-gray-800';
});

// 状态文本
const statusText = computed(() => {
  const status = props.assignment.status;
  if (status === 'PENDING') return '待完成';
  if (status === 'SUBMITTED') return '已提交';
  if (status === 'GRADED') return '已批改';
  if (status === 'OVERDUE') return '已逾期';
  return '未知状态';
});

// 分数样式
const scoreClass = computed(() => {
  const score = props.assignment.score;
  const maxScore = props.assignment.maxScore || 100;
  if (!score) return '';
  
  const percentage = (score / maxScore) * 100;
  
  if (percentage >= 90) return 'text-green-600';
  if (percentage >= 60) return 'text-blue-600';
  return 'text-red-600';
});

// 操作文本
const actionText = computed(() => {
  const status = props.assignment.status;
  if (status === 'PENDING') return '去完成';
  if (status === 'SUBMITTED') return '查看详情';
  if (status === 'GRADED') return '查看批改';
  if (status === 'OVERDUE') return '查看详情';
  return '查看详情';
});
</script>
