<template>
  <header class="app-header">
    <div class="header-left">
      <div class="mobile-menu-btn" @click="toggleSidebar">
        <el-icon><Menu /></el-icon>
      </div>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index" :to="item.path">
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="header-right">
      <div class="header-search">
        <el-input
          v-model="searchQuery"
          placeholder="搜索知识点、课程、资源..."
          prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        />
      </div>
      
      <div class="header-actions">
        <el-tooltip content="消息通知" placement="bottom">
          <el-badge :value="unreadCount" :max="99" :hidden="unreadCount === 0" class="action-item">
            <el-icon><Bell /></el-icon>
          </el-badge>
        </el-tooltip>
        
        <el-tooltip content="帮助中心" placement="bottom">
          <div class="action-item">
            <el-icon><QuestionFilled /></el-icon>
          </div>
        </el-tooltip>
        
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-dropdown">
            <el-avatar :size="32" :src="user?.avatar">{{ userInitials }}</el-avatar>
            <span class="username">{{ user?.name }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="settings">系统设置</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useLayoutStore } from '@/stores/layout'
import { 
  ArrowDown, 
  Bell, 
  QuestionFilled, 
  Search, 
  Menu
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const layoutStore = useLayoutStore()

// 侧边栏切换方法
const toggleSidebar = () => {
  if (layoutStore.isMobile) {
    layoutStore.toggleMobileSidebar()
  } else {
    layoutStore.toggleSidebarCollapse()
  }
}

// 用户信息
const user = computed(() => userStore.userInfo)
const userInitials = computed(() => {
  if (!user.value?.name) return ''
  return user.value.name.substring(0, 1).toUpperCase()
})

// 搜索
const searchQuery = ref('')
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({
      path: '/search',
      query: { q: searchQuery.value }
    })
    searchQuery.value = ''
  }
}

// 未读消息数
const unreadCount = ref(5)

// 面包屑导航
const breadcrumbs = computed(() => {
  const crumbs = [{ title: '首页', path: '/dashboard' }]
  
  const currentRoute = route.path
  const routeParts = currentRoute.split('/').filter(Boolean)
  
  let path = ''
  routeParts.forEach(part => {
    path += `/${part}`
    const matchedMenuItem = findMenuItem(layoutStore.menuItems, path)
    if (matchedMenuItem) {
      crumbs.push({
        title: matchedMenuItem.title,
        path: matchedMenuItem.path
      })
    }
  })
  
  return crumbs
})

// 菜单项接口
interface MenuItem {
  path: string;
  title: string;
  icon?: string;
  children?: MenuItem[];
}

// 查找菜单项
const findMenuItem = (items: MenuItem[], path: string): MenuItem | null => {
  for (const item of items) {
    if (item.path === path) {
      return item
    }
    if (item.children && item.children.length) {
      const found = findMenuItem(item.children, path)
      if (found) return found
    }
  }
  return null
}

// 处理用户下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
  }
}
</script>

<style scoped>
.app-header {
  height: 64px;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  position: sticky;
  top: 0;
  z-index: 10;
  width: 100%;
  box-sizing: border-box;
  margin: 0;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-search {
  width: 320px;
}

.header-search :deep(.el-input__wrapper) {
  border-radius: 40px;
  padding-left: 8px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-item {
  font-size: 20px;
  color: var(--el-text-color-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  color: var(--el-color-primary);
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 0.9rem;
  color: var(--el-text-color-primary);
}

.mobile-menu-btn {
  font-size: 24px;
  cursor: pointer;
  margin-right: 15px;
  color: var(--el-text-color-primary);
  display: none;
}

@media (max-width: 992px) {
  .header-search {
    width: 200px;
  }
  
  .user-dropdown .username {
    display: none;
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 0 16px;
  }
  
  .mobile-menu-btn {
    display: flex;
    align-items: center;
  }
  
  .header-search {
    display: none;
  }
  
  .header-actions {
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .app-header {
    padding: 0 12px;
    height: 56px;
  }
  
  .header-actions {
    gap: 8px;
  }
  
  .action-item {
    font-size: 18px;
  }
}
</style> 