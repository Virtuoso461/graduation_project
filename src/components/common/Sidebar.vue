<template>
  <aside class="bg-gray-800 text-white w-64 min-h-screen flex flex-col">
    <!-- 用户信息 -->
    <div class="p-4 border-b border-gray-700">
      <div class="flex items-center space-x-3">
        <img 
          :src="userStore.userInfo?.avatar || '/default-avatar.png'" 
          alt="用户头像" 
          class="w-10 h-10 rounded-full object-cover"
        />
        <div>
          <div class="font-medium">{{ userStore.userInfo?.name || userStore.userInfo?.username }}</div>
          <div class="text-xs text-gray-400">{{ roleText }}</div>
        </div>
      </div>
    </div>
    
    <!-- 导航菜单 -->
    <nav class="flex-1 overflow-y-auto py-4">
      <ul>
        <!-- 学生菜单 -->
        <template v-if="userStore.userInfo?.role === 'STUDENT'">
          <li v-for="item in studentMenu" :key="item.path">
            <router-link 
              :to="item.path" 
              class="flex items-center px-4 py-3 hover:bg-gray-700"
              :class="{ 'bg-gray-700': isActive(item.path) }"
            >
              <i :class="[item.icon, 'w-5 text-center mr-3']"></i>
              <span>{{ item.title }}</span>
            </router-link>
          </li>
        </template>
        
        <!-- 教师菜单 -->
        <template v-else-if="userStore.userInfo?.role === 'TEACHER'">
          <li v-for="item in teacherMenu" :key="item.path">
            <router-link 
              :to="item.path" 
              class="flex items-center px-4 py-3 hover:bg-gray-700"
              :class="{ 'bg-gray-700': isActive(item.path) }"
            >
              <i :class="[item.icon, 'w-5 text-center mr-3']"></i>
              <span>{{ item.title }}</span>
            </router-link>
          </li>
        </template>
        
        <!-- 管理员菜单 -->
        <template v-else-if="userStore.userInfo?.role === 'ADMIN'">
          <li v-for="item in adminMenu" :key="item.path">
            <router-link 
              :to="item.path" 
              class="flex items-center px-4 py-3 hover:bg-gray-700"
              :class="{ 'bg-gray-700': isActive(item.path) }"
            >
              <i :class="[item.icon, 'w-5 text-center mr-3']"></i>
              <span>{{ item.title }}</span>
            </router-link>
          </li>
        </template>
      </ul>
    </nav>
    
    <!-- 底部操作 -->
    <div class="p-4 border-t border-gray-700">
      <button 
        @click="logout" 
        class="flex items-center text-gray-400 hover:text-white w-full"
      >
        <i class="fas fa-sign-out-alt mr-3"></i>
        <span>退出登录</span>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 角色文本
const roleText = computed(() => {
  const role = userStore.userInfo?.role;
  if (role === 'STUDENT') return '学生';
  if (role === 'TEACHER') return '教师';
  if (role === 'ADMIN') return '管理员';
  return '用户';
});

// 学生菜单
const studentMenu = [
  { title: '仪表盘', path: '/student/dashboard', icon: 'fas fa-tachometer-alt' },
  { title: '个人中心', path: '/student/profile', icon: 'fas fa-user' },
  { title: '课程学习', path: '/student/courses', icon: 'fas fa-book' },
  { title: '作业管理', path: '/student/assignments', icon: 'fas fa-tasks' },
  { title: '在线考试', path: '/student/exams', icon: 'fas fa-file-alt' },
  { title: '学习社区', path: '/student/community', icon: 'fas fa-comments' },
  { title: '学习资源', path: '/student/resources', icon: 'fas fa-folder' },
  { title: 'AI辅导', path: '/student/ai-guidance', icon: 'fas fa-robot' }
];

// 教师菜单
const teacherMenu = [
  { title: '仪表盘', path: '/teacher/dashboard', icon: 'fas fa-tachometer-alt' },
  { title: '个人中心', path: '/teacher/profile', icon: 'fas fa-user' },
  { title: '课程管理', path: '/teacher/courses', icon: 'fas fa-book' },
  { title: '作业管理', path: '/teacher/assignments', icon: 'fas fa-tasks' },
  { title: '考试管理', path: '/teacher/exams', icon: 'fas fa-file-alt' },
  { title: '学生管理', path: '/teacher/students', icon: 'fas fa-users' },
  { title: '资源管理', path: '/teacher/resources', icon: 'fas fa-folder' },
  { title: '待办事项', path: '/teacher/todos', icon: 'fas fa-check-square' }
];

// 管理员菜单
const adminMenu = [
  { title: '仪表盘', path: '/admin/dashboard', icon: 'fas fa-tachometer-alt' },
  { title: '用户管理', path: '/admin/users', icon: 'fas fa-users' },
  { title: '课程管理', path: '/admin/courses', icon: 'fas fa-book' },
  { title: '系统设置', path: '/admin/settings', icon: 'fas fa-cog' }
];

// 判断路由是否激活
const isActive = (path) => {
  return route.path.startsWith(path);
};

// 退出登录
const logout = async () => {
  try {
    await userStore.logout();
    router.push('/login');
  } catch (error) {
    console.error('退出登录失败:', error);
  }
};
</script>
