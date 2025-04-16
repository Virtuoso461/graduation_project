<template>
  <div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
    <!-- 课程封面 -->
    <div class="relative h-40 bg-gray-200">
      <img 
        v-if="course.coverImage" 
        :src="course.coverImage" 
        :alt="course.name" 
        class="w-full h-full object-cover"
      />
      <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-r from-blue-500 to-indigo-600">
        <span class="text-white text-xl font-bold">{{ course.name }}</span>
      </div>
      
      <!-- 难度标签 -->
      <div 
        class="absolute top-3 right-3 text-xs font-medium px-2 py-1 rounded-full"
        :class="difficultyClass"
      >
        {{ difficultyText }}
      </div>
      
      <!-- 进度条（仅已选课程显示） -->
      <div v-if="course.progress !== undefined" class="absolute bottom-0 left-0 right-0 h-1 bg-gray-300">
        <div 
          class="h-full bg-primary" 
          :style="{ width: `${course.progress}%` }"
        ></div>
      </div>
    </div>
    
    <div class="p-4">
      <div class="flex justify-between items-start mb-2">
        <h3 class="text-lg font-semibold text-gray-800 truncate">
          {{ course.name }}
        </h3>
        
        <!-- 评分 -->
        <div class="flex items-center">
          <i class="fas fa-star text-yellow-400 mr-1"></i>
          <span class="text-sm font-medium">{{ course.rating || '暂无评分' }}</span>
        </div>
      </div>
      
      <p class="text-gray-600 text-sm mb-3 line-clamp-2">
        {{ course.description }}
      </p>
      
      <div class="flex flex-wrap gap-2 mb-3">
        <span 
          v-for="(tag, index) in course.tags" 
          :key="index"
          class="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded"
        >
          {{ tag }}
        </span>
      </div>
      
      <div class="flex justify-between items-center text-sm text-gray-500 mb-3">
        <div class="flex items-center">
          <i class="fas fa-user-tie mr-1"></i>
          <span>{{ course.teacherName || '未知教师' }}</span>
        </div>
        
        <div class="flex items-center">
          <i class="far fa-clock mr-1"></i>
          <span>{{ course.duration || 0 }} 小时</span>
        </div>
      </div>
      
      <!-- 已选课程显示进度信息 -->
      <div v-if="course.progress !== undefined" class="mb-3">
        <div class="flex justify-between items-center text-sm">
          <span class="text-gray-600">学习进度</span>
          <span class="font-medium text-primary">{{ course.progress }}%</span>
        </div>
      </div>
      
      <div class="flex justify-between items-center">
        <!-- 学分或价格 -->
        <div class="text-sm font-medium">
          <span v-if="course.credits" class="text-orange-600">
            {{ course.credits }} 学分
          </span>
          <span v-else-if="course.price" class="text-orange-600">
            ¥{{ course.price }}
          </span>
          <span v-else class="text-green-600">
            免费
          </span>
        </div>
        
        <!-- 操作按钮 -->
        <router-link 
          :to="`/student/courses/${course.id}`" 
          class="text-primary hover:text-primary-dark text-sm font-medium"
        >
          {{ course.enrolled ? '继续学习' : '查看详情' }}
          <i class="fas fa-chevron-right ml-1 text-xs"></i>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';

interface Course {
  id: number;
  name: string;
  description: string;
  coverImage?: string;
  difficulty: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
  rating?: number;
  tags?: string[];
  teacherName?: string;
  duration?: number;
  progress?: number;
  credits?: number;
  price?: number;
  enrolled?: boolean;
}

const props = defineProps<{
  course: Course;
}>();

// 难度样式
const difficultyClass = computed(() => {
  const difficulty = props.course.difficulty;
  if (difficulty === 'BEGINNER') return 'bg-green-100 text-green-800';
  if (difficulty === 'INTERMEDIATE') return 'bg-blue-100 text-blue-800';
  if (difficulty === 'ADVANCED') return 'bg-red-100 text-red-800';
  return 'bg-gray-100 text-gray-800';
});

// 难度文本
const difficultyText = computed(() => {
  const difficulty = props.course.difficulty;
  if (difficulty === 'BEGINNER') return '初级';
  if (difficulty === 'INTERMEDIATE') return '中级';
  if (difficulty === 'ADVANCED') return '高级';
  return '未知';
});
</script>
