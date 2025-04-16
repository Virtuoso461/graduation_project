<template>
  <div class="home-page">
    <!-- 顶部横幅 -->
    <section class="bg-gradient-to-r from-primary to-primary-dark text-white py-16">
      <div class="container mx-auto px-4">
        <div class="flex flex-col md:flex-row items-center justify-between">
          <div class="md:w-1/2 mb-8 md:mb-0">
            <h1 class="text-4xl font-bold mb-4">在线学习平台</h1>
            <p class="text-xl mb-6">提供高质量的在线课程、作业和考试，帮助您实现学习目标</p>
            <div class="flex space-x-4">
              <router-link 
                to="/register" 
                class="bg-white text-primary px-6 py-3 rounded-lg font-medium hover:bg-gray-100 transition duration-300"
              >
                立即注册
              </router-link>
              <router-link 
                to="/login" 
                class="border border-white text-white px-6 py-3 rounded-lg font-medium hover:bg-white hover:text-primary transition duration-300"
              >
                登录
              </router-link>
            </div>
          </div>
          <div class="md:w-1/2">
            <img src="@/assets/images/hero-image.svg" alt="学习插图" class="w-full max-w-md mx-auto" />
          </div>
        </div>
      </div>
    </section>
    
    <!-- 特色功能 -->
    <section class="py-16 bg-gray-50">
      <div class="container mx-auto px-4">
        <h2 class="text-3xl font-bold text-center mb-12">平台特色</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="w-16 h-16 bg-primary-light rounded-full flex items-center justify-center mb-4 mx-auto">
              <i class="fas fa-book text-primary text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold text-center mb-2">丰富的课程资源</h3>
            <p class="text-gray-600 text-center">提供各种学科的优质课程，满足不同学习需求</p>
          </div>
          
          <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="w-16 h-16 bg-primary-light rounded-full flex items-center justify-center mb-4 mx-auto">
              <i class="fas fa-tasks text-primary text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold text-center mb-2">在线作业与考试</h3>
            <p class="text-gray-600 text-center">支持在线提交作业和参加考试，实时获取反馈</p>
          </div>
          
          <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="w-16 h-16 bg-primary-light rounded-full flex items-center justify-center mb-4 mx-auto">
              <i class="fas fa-robot text-primary text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold text-center mb-2">AI学习辅导</h3>
            <p class="text-gray-600 text-center">智能AI助手，解答疑问，提供个性化学习建议</p>
          </div>
        </div>
      </div>
    </section>
    
    <!-- 热门课程 -->
    <section class="py-16">
      <div class="container mx-auto px-4">
        <div class="flex justify-between items-center mb-8">
          <h2 class="text-3xl font-bold">热门课程</h2>
          <router-link to="/student/courses" class="text-primary hover:text-primary-dark">
            查看全部 <i class="fas fa-arrow-right ml-1"></i>
          </router-link>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <!-- 课程卡片 -->
          <div v-for="(course, index) in popularCourses" :key="index" class="bg-white rounded-lg shadow-md overflow-hidden">
            <div class="h-48 bg-gray-200">
              <img 
                v-if="course.coverImage" 
                :src="course.coverImage" 
                :alt="course.name" 
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-r from-blue-500 to-indigo-600">
                <span class="text-white text-xl font-bold">{{ course.name }}</span>
              </div>
            </div>
            
            <div class="p-4">
              <h3 class="text-lg font-semibold mb-2">{{ course.name }}</h3>
              <p class="text-gray-600 text-sm mb-3 line-clamp-2">{{ course.description }}</p>
              
              <div class="flex justify-between items-center">
                <div class="flex items-center">
                  <i class="fas fa-user-tie mr-1 text-gray-500"></i>
                  <span class="text-sm text-gray-500">{{ course.teacherName }}</span>
                </div>
                
                <div class="flex items-center">
                  <i class="fas fa-star text-yellow-400 mr-1"></i>
                  <span class="text-sm font-medium">{{ course.rating }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <!-- 学习社区 -->
    <section class="py-16 bg-gray-50">
      <div class="container mx-auto px-4">
        <div class="flex justify-between items-center mb-8">
          <h2 class="text-3xl font-bold">学习社区</h2>
          <router-link to="/student/community" class="text-primary hover:text-primary-dark">
            进入社区 <i class="fas fa-arrow-right ml-1"></i>
          </router-link>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <!-- 热门帖子 -->
          <div v-for="(post, index) in hotPosts" :key="index" class="bg-white p-6 rounded-lg shadow-md">
            <h3 class="text-lg font-semibold mb-2">{{ post.title }}</h3>
            <p class="text-gray-600 text-sm mb-4 line-clamp-2">{{ post.content }}</p>
            
            <div class="flex justify-between items-center text-sm text-gray-500">
              <div class="flex items-center">
                <img 
                  :src="post.authorAvatar || '/default-avatar.png'" 
                  :alt="post.authorName" 
                  class="w-6 h-6 rounded-full mr-2"
                />
                <span>{{ post.authorName }}</span>
              </div>
              
              <div class="flex space-x-4">
                <div class="flex items-center">
                  <i class="far fa-comment mr-1"></i>
                  <span>{{ post.replies }}</span>
                </div>
                <div class="flex items-center">
                  <i class="far fa-thumbs-up mr-1"></i>
                  <span>{{ post.likes }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <!-- 注册引导 -->
    <section class="py-16 bg-primary text-white">
      <div class="container mx-auto px-4 text-center">
        <h2 class="text-3xl font-bold mb-4">立即加入我们的学习平台</h2>
        <p class="text-xl mb-8 max-w-2xl mx-auto">注册账号，开始您的在线学习之旅，探索丰富的课程资源，提升学习效率</p>
        <router-link 
          to="/register" 
          class="bg-white text-primary px-8 py-3 rounded-lg font-medium hover:bg-gray-100 transition duration-300 inline-block"
        >
          免费注册
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 模拟数据
const popularCourses = ref([
  {
    id: 1,
    name: 'Java编程基础',
    description: '从零开始学习Java编程，掌握核心概念和基础语法，为进阶学习打下坚实基础。',
    teacherName: '张教授',
    rating: 4.8,
    coverImage: null
  },
  {
    id: 2,
    name: '数据结构与算法',
    description: '深入学习常用数据结构和算法，提高编程能力和解决问题的效率。',
    teacherName: '李博士',
    rating: 4.7,
    coverImage: null
  },
  {
    id: 3,
    name: '前端开发实战',
    description: '学习HTML、CSS和JavaScript，掌握现代前端框架，开发响应式网站。',
    teacherName: '王老师',
    rating: 4.9,
    coverImage: null
  }
]);

const hotPosts = ref([
  {
    id: 1,
    title: 'Java多线程编程问题',
    content: '在实现多线程同步时遇到了一些问题，线程间的通信似乎不太顺畅，有没有好的解决方案？',
    authorName: '学习者小王',
    authorAvatar: null,
    replies: 15,
    likes: 23
  },
  {
    id: 2,
    title: '数据结构课程学习心得',
    content: '最近学习了红黑树，感觉很有挑战性但也很有收获，分享一下我的学习方法和笔记。',
    authorName: '程序员小李',
    authorAvatar: null,
    replies: 28,
    likes: 45
  },
  {
    id: 3,
    title: '求推荐前端学习资源',
    content: '想系统学习前端开发，有没有好的书籍、视频或者网站推荐？特别是关于Vue和React的。',
    authorName: '新手小张',
    authorAvatar: null,
    replies: 32,
    likes: 18
  },
  {
    id: 4,
    title: '分享一个算法题的解题思路',
    content: '最近做了一道关于动态规划的题目，思路比较巧妙，分享给大家讨论一下。',
    authorName: '算法达人',
    authorAvatar: null,
    replies: 20,
    likes: 37
  }
]);
</script>
