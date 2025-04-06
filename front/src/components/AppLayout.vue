<template>
  <div class="app-container" :class="{ 'collapsed': isCollapsed }">
    <!-- 侧边栏 -->
    <aside class="app-sidebar" :class="{ 'expanded': sidebarExpanded }">
      <div class="sidebar-header">
        <div class="logo-area">
          <img src="../assets/logo.svg" alt="Logo" class="logo-img" />
          <h2 class="logo-text" v-show="!isCollapsed">学习矩阵</h2>
        </div>
        <el-icon 
          class="collapse-icon"
          @click="toggleSidebar"
          v-show="!isMobile"
        >
          <component :is="isCollapsed ? 'Expand' : 'Fold'" />
        </el-icon>
      </div>
      
      <div class="sidebar-user" v-if="user">
        <el-avatar :src="user.avatar" :size="isCollapsed ? 40 : 50">
          {{ userInitials }}
        </el-avatar>
        <div class="user-info" v-show="!isCollapsed">
          <h3 class="user-name">{{ user.name }}</h3>
          <p class="user-role">{{ user.role }}</p>
        </div>
      </div>

      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed"
          :unique-opened="true"
          class="sidebar-menu"
          :router="true"
        >
          <template v-for="item in menuItems" :key="item.path">
            <!-- 带子菜单的项目 -->
            <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.path">
              <template #title>
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.title }}</span>
              </template>
              <el-menu-item 
                v-for="child in item.children" 
                :key="child.path"
                :index="child.path"
                :route="child.path"
              >
                <el-icon><component :is="child.icon" /></el-icon>
                <span>{{ child.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            
            <!-- 没有子菜单的项目 -->
            <el-menu-item v-else :index="item.path" :route="item.path">
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </aside>

    <!-- 移动设备侧边栏蒙层 -->
    <div 
      v-if="sidebarExpanded && isMobile" 
      class="sidebar-overlay"
      @click="closeSidebar"
    ></div>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <header class="app-header">
        <div class="header-left">
          <!-- 移动设备菜单按钮 -->
          <div class="mobile-menu-btn" v-if="isMobile" @click="toggleMobileSidebar">
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

      <!-- 页面内容 -->
      <main class="page-content">
        <div class="content-wrapper">
          <slot></slot>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  ArrowDown, 
  Bell, 
  QuestionFilled, 
  Search, 
  Fold, 
  Expand,
  Menu
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 侧边栏状态
const isCollapsed = ref(false)
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

// 检测是否为移动设备
const isMobile = ref(false)
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 992
  // 在移动设备上自动折叠侧边栏
  if (isMobile.value && !isCollapsed.value) {
    isCollapsed.value = true
  }
}

// 监听窗口大小变化
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

// 组件卸载前移除事件监听
onBeforeUnmount(() => {
  window.removeEventListener('resize', checkMobile)
})

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

// 导航菜单
const menuItems = [
  {
    path: '/dashboard',
    title: '学习仪表盘',
    icon: 'HomeFilled'
  },
  {
    path: '/courses',
    title: '我的课程',
    icon: 'Collection',
    children: [
      {
        path: '/courses/enrolled',
        title: '已选课程',
        icon: 'Document'
      },
      {
        path: '/courses/completed',
        title: '已完成课程',
        icon: 'Check'
      }
    ]
  },
  {
    path: '/learning-path',
    title: '学习路径',
    icon: 'Sort'
  },
  {
    path: '/ai-assistant',
    title: 'AI学习助手',
    icon: 'Service'
  },
  {
    path: '/resource-library',
    title: '资源库',
    icon: 'Folder'
  },
  {
    path: '/exams',
    title: '在线测试',
    icon: 'EditPen'
  },
  {
    path: '/assessments',
    title: '测评中心',
    icon: 'EditPen'
  },
  {
    path: '/community',
    title: '学习社区',
    icon: 'ChatDotRound'
  },
  {
    path: '/analytics',
    title: '学习分析',
    icon: 'DataLine'
  }
]

// 当前活动菜单
const activeMenu = computed(() => {
  return route.path
})

// 面包屑导航
const breadcrumbs = computed(() => {
  const crumbs = [{ title: '首页', path: '/dashboard' }]
  
  const currentRoute = route.path
  const routeParts = currentRoute.split('/').filter(Boolean)
  
  let path = ''
  routeParts.forEach(part => {
    path += `/${part}`
    const matchedMenuItem = findMenuItem(menuItems, path)
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

// 移动设备侧边栏状态
const sidebarExpanded = ref(false)
const toggleMobileSidebar = () => {
  sidebarExpanded.value = !sidebarExpanded.value
}
const closeSidebar = () => {
  sidebarExpanded.value = false
}
</script>

<style lang="scss" scoped>
.app-container {
  display: flex;
  min-height: 100vh;
  height: 100%;
  width: 100%;
  position: relative;
  background-color: var(--el-bg-color);
  transition: all 0.3s;
  overflow: hidden;
}

.app-sidebar {
  width: 260px;
  height: 100vh;
  background: linear-gradient(180deg, #001529 0%, #003366 100%);
  color: white;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 1000;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  transition: all 0.3s;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 2px;
  }
  
  .sidebar-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    height: 70px;
    
    .logo-area {
      display: flex;
      align-items: center;
      
      .logo-img {
        width: 36px;
        height: 36px;
      }
      
      .logo-text {
        margin-left: 10px;
        font-size: 1.2rem;
        font-weight: 600;
        color: white;
        white-space: nowrap;
      }
    }
    
    .collapse-icon {
      cursor: pointer;
      font-size: 20px;
      color: rgba(255, 255, 255, 0.7);
      
      &:hover {
        color: white;
      }
    }
  }
  
  .sidebar-user {
    padding: 12px 16px;
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    background: rgba(255, 255, 255, 0.05);
    
    .user-info {
      overflow: hidden;
      
      .user-name {
        margin: 0;
        font-size: 0.95rem;
        font-weight: 500;
        color: white;
        line-height: 1.2;
      }
      
      .user-role {
        margin: 0;
        font-size: 0.8rem;
        color: rgba(255, 255, 255, 0.7);
      }
    }
  }
  
  .sidebar-menu {
    border-right: none;
    background: transparent;
    
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      color: rgba(255, 255, 255, 0.8);
      
      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }
      
      &.is-active {
        color: #4facfe;
        background: rgba(79, 172, 254, 0.1);
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 0;
          bottom: 0;
          width: 4px;
          background: #4facfe;
        }
      }
    }
    
    :deep(.el-sub-menu__title:hover) {
      background-color: rgba(255, 255, 255, 0.1);
    }
    
    :deep(.el-sub-menu) {
      .el-menu {
        background-color: rgba(0, 0, 0, 0.15);
      }
    }
  }
}

.collapsed {
  .app-sidebar {
    width: 64px;
    
    @media (max-width: 480px) {
      width: 0;
      padding: 0;
      opacity: 0;
      visibility: hidden;
    }
  }
  
  .main-container {
    margin-left: 64px;
    width: calc(100% - 64px);
    max-width: calc(100% - 64px);
    
    @media (max-width: 480px) {
      margin-left: 0;
      width: 100%;
      max-width: 100%;
    }
  }
}

.main-container {
  flex: 1;
  margin-left: 260px;
  min-height: 100vh;
  height: 100%;
  width: calc(100% - 260px);
  display: flex;
  flex-direction: column;
  transition: all 0.3s;
  overflow: hidden;
  max-width: calc(100% - 260px);
  
  @media (max-width: 480px) {
    width: 100%;
    max-width: 100%;
    margin-left: 0;
  }
}

.app-header {
  height: 70px;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 10;
  
  .header-left {
    display: flex;
    align-items: center;
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .header-search {
      width: 320px;
      
      :deep(.el-input__wrapper) {
        border-radius: 40px;
        padding-left: 8px;
      }
    }
    
    .header-actions {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .action-item {
        font-size: 20px;
        color: var(--el-text-color-secondary);
        cursor: pointer;
        transition: all 0.2s;
        
        &:hover {
          color: var(--el-color-primary);
        }
      }
      
      .user-dropdown {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
        
        .username {
          font-size: 0.9rem;
          color: var(--el-text-color-primary);
        }
      }
    }
  }
}

.page-content {
  flex: 1;
  padding: 0;
  background-color: var(--el-fill-color-light);
  overflow-y: auto;
  height: calc(100vh - 70px);
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  position: relative;
  padding: 0;
  margin: 0;
  max-width: 100%;
  box-sizing: border-box;
}

// 响应式调整
@media (max-width: 992px) {
  .app-sidebar {
    width: 64px;
    
    .sidebar-user {
      padding: 12px 8px;
      justify-content: center;
    }
  }
  
  .main-container {
    margin-left: 64px;
    width: calc(100% - 64px);
    max-width: calc(100% - 64px);
  }
  
  .sidebar-header {
    justify-content: center;
    padding: 16px 0;
  }
  
  .header-search {
    width: 200px !important;
    
    @media (max-width: 768px) {
      display: none;
    }
  }
  
  .user-dropdown .username {
    display: none;
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 0 16px;
  }
  
  .header-actions {
    gap: 12px !important;
  }
}

@media (max-width: 480px) {
  .app-header {
    padding: 0 12px;
    height: 60px;
  }
  
  .page-content {
    height: calc(100vh - 60px);
    padding: 0;
  }
  
  .header-actions {
    gap: 8px !important;
  }
  
  .action-item {
    font-size: 18px !important;
  }
  
  .app-sidebar {
    transform: translateX(-100%);
    opacity: 0;
    
    &.expanded {
      transform: translateX(0);
      opacity: 1;
      width: 230px;
    }
  }
  
  .main-container {
    margin-left: 0;
    width: 100%;
    max-width: 100%;
  }
}

// 横屏手机模式
@media screen and (orientation: landscape) and (max-height: 500px) {
  .app-sidebar {
    height: 100vh;
    overflow-y: auto;
  }
  
  .page-content {
    max-height: 100vh;
    overflow-y: auto;
  }
  
  .app-header {
    height: 50px;
  }
  
  .page-content {
    height: calc(100vh - 50px);
    padding: 0;
  }
}

// 添加移动设备菜单按钮样式
.mobile-menu-btn {
  font-size: 24px;
  cursor: pointer;
  margin-right: 15px;
  color: var(--el-text-color-primary);
  display: none;
  
  @media (max-width: 768px) {
    display: flex;
    align-items: center;
  }
}

// 侧边栏蒙层
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style> 