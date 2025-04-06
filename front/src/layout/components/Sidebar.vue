<template>
  <aside class="app-sidebar" :class="{ 'collapsed': isCollapsed, 'expanded': sidebarExpanded }">
    <div class="sidebar-header">
      <div class="logo-area">
        <el-icon class="logo-icon"><School /></el-icon>
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
            <el-icon class="menu-icon">
              <component :is="item.icon" v-if="item.icon" />
              <Document v-else />
            </el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </aside>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useLayoutStore } from '@/stores/layout'
import { 
  Fold, 
  Expand, 
  School, 
  HomeFilled,
  Connection,
  Collection, 
  Document,
  Check,
  Sort,
  Service,
  Folder,
  EditPen,
  ChatDotRound,
  DataLine
} from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()
const layoutStore = useLayoutStore()

// 侧边栏状态
const isCollapsed = computed(() => layoutStore.sidebarCollapsed)
const isMobile = computed(() => layoutStore.isMobile)
const sidebarExpanded = computed(() => layoutStore.sidebarExpanded)

const toggleSidebar = () => {
  layoutStore.toggleSidebarCollapse()
}

// 菜单项
const menuItems = computed(() => layoutStore.menuItems)

// 当前活动菜单
const activeMenu = computed(() => {
  return route.path
})

// 用户信息
const user = computed(() => userStore.userInfo)
const userInitials = computed(() => {
  if (!user.value?.name) return ''
  return user.value.name.substring(0, 1).toUpperCase()
})

// 添加窗口大小变化处理
const handleResize = () => {
  layoutStore.updateDeviceType()
  layoutStore.refreshSidebar()
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.app-sidebar {
  width: 260px;
  height: 100vh;
  background: linear-gradient(180deg, #001529 0%, #003366 100%);
  color: white;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  will-change: width;
  overflow-y: auto;
  margin: 0;
  padding: 0;
}

.app-sidebar::-webkit-scrollbar {
  width: 4px;
}

.app-sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 12px;
  height: 64px;
  box-sizing: border-box;
}

.logo-area {
  display: flex;
  align-items: center;
}

.logo-icon {
  font-size: 32px;
  color: #4facfe;
}

.logo-text {
  margin-left: 10px;
  font-size: 1.2rem;
  font-weight: 600;
  color: white;
  white-space: nowrap;
}

.collapse-icon {
  cursor: pointer;
  font-size: 20px;
  color: rgba(255, 255, 255, 0.7);
}

.collapse-icon:hover {
  color: white;
}

.sidebar-user {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.05);
}

.user-info {
  overflow: hidden;
}

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

.sidebar-menu {
  border-right: none;
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.8);
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.1);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  color: #4facfe;
  background: rgba(79, 172, 254, 0.1);
}

.sidebar-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: #4facfe;
}

.sidebar-menu :deep(.el-sub-menu) .el-menu {
  background-color: rgba(0, 0, 0, 0.15);
}

.collapsed {
  width: 64px;
}

@media (max-width: 992px) {
  .app-sidebar {
    width: 64px;
  }
  
  .sidebar-user {
    padding: 12px 8px;
    justify-content: center;
  }
  
  .sidebar-header {
    justify-content: center;
    padding: 16px 0;
  }
}

@media (max-width: 768px) {
  .app-sidebar {
    transform: translateX(-100%);
    opacity: 0;
    transition: all 0.3s;
    width: 240px;
  }
  
  .app-sidebar.expanded {
    transform: translateX(0);
    opacity: 1;
    width: 240px;
  }
  
  .collapsed {
    width: 0;
    padding: 0;
    opacity: 0;
    visibility: hidden;
  }
}

/* 添加图标相关样式 */
.el-menu :deep(.el-icon) {
  width: 24px;
  height: 24px;
  display: inline-flex !important;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}

/* 确保图标在折叠状态下也能正确显示 */
.collapsed .el-menu :deep(.el-icon) {
  margin: 0;
}
</style> 