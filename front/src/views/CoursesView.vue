<template>
  <div class="courses-view">
    <div class="page-header">
      <h1 class="page-title">我的课程</h1>
      <div class="page-actions">
        <el-button type="primary" @click="handleAddCourse" class="add-course-btn">
          <el-icon><Plus /></el-icon>
          选课
        </el-button>
      </div>
    </div>
    
    <div class="courses-container">
      <div class="courses-nav">
        <router-link 
          to="/courses/enrolled" 
          class="nav-item" 
          active-class="active"
        >
          <el-icon><Document /></el-icon>
          <span>进行中课程</span>
        </router-link>
        <router-link 
          to="/courses/completed" 
          class="nav-item" 
          active-class="active"
        >
          <el-icon><Check /></el-icon>
          <span>已完成课程</span>
        </router-link>
      </div>

      <div class="courses-content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Plus,
  Timer,
  CircleCheckFilled,
  Star,
  Document,
  Check
} from '@element-plus/icons-vue'

const router = useRouter()

// 当前激活的标签页
const activeTab = ref('ongoing')

// 进行中的课程
const ongoingCourses = ref([
  {
    id: 1,
    title: '数据结构与算法分析',
    description: '本课程介绍基本的数据结构和算法，包括数组、链表、栈、队列、树、图等数据结构，以及排序、搜索等算法。',
    cover: 'https://via.placeholder.com/300x200',
    progress: 65,
    schedule: '每周二、四 14:00-16:00',
    duration: '12周'
  },
  {
    id: 2,
    title: '计算机网络原理',
    description: '深入理解计算机网络的基本概念、协议和架构，包括物理层、数据链路层、网络层、传输层和应用层。',
    cover: 'https://via.placeholder.com/300x200',
    progress: 45,
    schedule: '每周一、三 10:00-12:00',
    duration: '10周'
  }
])

// 已完成的课程
const completedCourses = ref([
  {
    id: 3,
    title: '操作系统原理',
    description: '学习操作系统的基本概念、进程管理、内存管理、文件系统等核心内容。',
    cover: 'https://via.placeholder.com/300x200',
    completedDate: '2024-02-15',
    score: '95分'
  },
  {
    id: 4,
    title: '数据库系统概论',
    description: '掌握数据库系统的基本概念、关系模型、SQL语言、数据库设计和优化。',
    cover: 'https://via.placeholder.com/300x200',
    completedDate: '2024-01-20',
    score: '92分'
  }
])

// 获取进度条颜色
const getProgressColor = (progress: number) => {
  if (progress < 30) return '#909399'
  if (progress < 70) return '#E6A23C'
  return '#67C23A'
}

// 处理添加课程
const handleAddCourse = () => {
  // 这里可以跳转到选课页面或显示选课对话框
  // 暂时简单跳转到已选课程页
  router.push('/course-catalog')
}
</script>

<style lang="scss" scoped>
// 导入响应式样式工具
@use '@/assets/styles/responsive' as responsive;

.courses-view {
  padding-bottom: 24px;
  min-height: 100vh;
  width: 100%;
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  @include responsive.respond-to('md') {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .page-title {
    font-size: 1.75rem;
    font-weight: 600;
    margin: 0;
    background: linear-gradient(120deg, #4a6cf7, #7b5aff);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    
    @include responsive.respond-to('md') {
      font-size: 1.5rem;
    }
    
    @include responsive.respond-to('sm') {
      font-size: 1.25rem;
    }
  }
  
  .page-actions {
    @include responsive.respond-to('md') {
      width: 100%;
    }
    
    .add-course-btn {
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(74, 108, 247, 0.3);
      }
    }
  }
}

.courses-container {
  display: flex;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  }
  
  @include responsive.respond-to('md') {
    flex-direction: column;
  }
}

.courses-nav {
  width: 200px;
  background: linear-gradient(180deg, #f8f9fa, #f2f4f8);
  padding: 24px 0;
  border-right: 1px solid #ebeef5;
  
  @include responsive.respond-to('md') {
    width: 100%;
    display: flex;
    padding: 0;
    border-right: none;
    border-bottom: 1px solid #ebeef5;
  }
  
  .nav-item {
    display: flex;
    align-items: center;
    padding: 14px 20px;
    color: #606266;
    text-decoration: none;
    transition: all 0.3s;
    border-left: 3px solid transparent;
    margin-bottom: 4px;
    
    @include responsive.respond-to('md') {
      flex: 1;
      justify-content: center;
      padding: 16px;
      border-left: none;
      border-bottom: 2px solid transparent;
      margin-bottom: 0;
    }
    
    &:hover {
      background-color: rgba(74, 108, 247, 0.05);
      color: #4a6cf7;
    }
    
    &.active {
      background: linear-gradient(90deg, rgba(74, 108, 247, 0.1), rgba(123, 90, 255, 0.05));
      color: #4a6cf7;
      font-weight: 500;
      border-left-color: #4a6cf7;
      
      @include responsive.respond-to('md') {
        border-left-color: transparent;
        border-bottom-color: #4a6cf7;
        background: rgba(74, 108, 247, 0.05);
      }
    }
    
    .el-icon {
      margin-right: 10px;
      font-size: 18px;
      transition: transform 0.3s;
    }
    
    &:hover .el-icon, &.active .el-icon {
      transform: scale(1.2);
    }
  }
}

.courses-content {
  flex: 1;
  padding: 24px;
  min-height: 500px;
  
  @include responsive.respond-to('md') {
    padding: 20px;
  }
  
  @include responsive.respond-to('sm') {
    padding: 16px;
    min-height: 400px;
  }
}
</style> 