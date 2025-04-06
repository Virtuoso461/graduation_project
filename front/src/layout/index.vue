<template>
  <div class="layout-container" :class="{ 'sidebar-collapsed': isCollapsed }">
    <!-- 侧边栏 -->
    <Sidebar />
    
    <!-- 侧边栏遮罩层 -->
    <SidebarOverlay :visible="isMobile && sidebarExpanded" @close="closeSidebar" />
    
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <Header />
      
      <!-- 主内容区域 -->
      <Content>
        <router-view />
      </Content>
      
      <!-- 页脚 -->
      <Footer />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, onBeforeUnmount } from 'vue'
import { useLayoutStore } from '@/stores/layout'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Content from './components/Content.vue'
import Footer from './components/Footer.vue'
import SidebarOverlay from './components/SidebarOverlay.vue'

const layoutStore = useLayoutStore()

// 计算属性
const isCollapsed = computed(() => layoutStore.sidebarCollapsed)
const isMobile = computed(() => layoutStore.isMobile)
const sidebarExpanded = computed(() => layoutStore.sidebarExpanded)

// 关闭侧边栏方法
const closeSidebar = () => {
  layoutStore.closeSidebar()
}

// 检测移动设备
const checkMobile = () => {
  const isMobileDevice = window.innerWidth <= 768
  layoutStore.updateMobileStatus(isMobileDevice)
  
  // 如果是移动设备，默认不展开侧边栏
  if (isMobileDevice && !isCollapsed.value) {
    layoutStore.toggleSidebarCollapse()
  }
}

// 生命周期钩子
onMounted(() => {
  // 初始检测设备类型
  checkMobile()
  
  // 添加窗口大小变化事件监听
  window.addEventListener('resize', checkMobile)
})

// 组件卸载前移除事件监听
onBeforeUnmount(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
  height: 100%;
  width: 100%;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  max-width: 100vw;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
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
  box-sizing: border-box;
  max-width: 100%;
  padding: 0;
}

.sidebar-collapsed .main-container {
  margin-left: 64px;
  width: calc(100% - 64px);
}

@media (max-width: 992px) {
  .main-container {
    margin-left: 64px;
    width: calc(100% - 64px);
  }
}

@media (max-width: 768px) {
  .main-container {
    margin-left: 0;
    width: 100%;
  }
  
  .sidebar-collapsed .main-container {
    margin-left: 0;
    width: 100%;
  }
}
</style> 