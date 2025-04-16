<template>
  <header class="bg-white shadow-sm">
    <div class="container mx-auto px-4 py-3 flex justify-between items-center">
      <div class="flex items-center">
        <router-link to="/" class="text-xl font-bold text-primary">学习平台</router-link>
      </div>
      
      <div class="flex items-center space-x-4">
        <div v-if="userStore.isLoggedIn" class="flex items-center">
          <div class="relative group">
            <button class="flex items-center space-x-2 focus:outline-none">
              <img 
                :src="userStore.userInfo?.avatar || '/default-avatar.png'" 
                alt="用户头像" 
                class="w-8 h-8 rounded-full object-cover"
              />
              <span class="text-gray-700">{{ userStore.userInfo?.name || userStore.userInfo?.username }}</span>
              <i class="fas fa-chevron-down text-xs text-gray-500"></i>
            </button>
            
            <div class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10 hidden group-hover:block">
              <router-link 
                :to="profileRoute" 
                class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                个人中心
              </router-link>
              <a 
                href="#" 
                class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                @click.prevent="logout"
              >
                退出登录
              </a>
            </div>
          </div>
        </div>
        
        <template v-else>
          <router-link to="/login" class="text-primary hover:text-primary-dark">登录</router-link>
          <router-link to="/register" class="bg-primary text-white px-4 py-2 rounded hover:bg-primary-dark">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();

// 根据用户角色确定个人中心路由
const profileRoute = computed(() => {
  const role = userStore.userInfo?.role;
  if (role === 'STUDENT') {
    return '/student/profile';
  } else if (role === 'TEACHER') {
    return '/teacher/profile';
  } else if (role === 'ADMIN') {
    return '/admin/dashboard';
  }
  return '/';
});

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
