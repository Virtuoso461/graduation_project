.sidebar-item-icon {
  display: inline-flex !important; /* 强制显示 */
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  margin-right: 10px;
  visibility: visible !important; /* 确保可见性 */
  opacity: 1 !important; /* 确保不透明 */
}

/* 确保图标容器有足够空间 */
.el-menu-item, .el-sub-menu__title {
  display: flex !important;
  align-items: center;
  height: 56px;
  line-height: 56px;
}

<script setup lang="ts">
import { onMounted, onBeforeUnmount } from 'vue'
import { useLayoutStore } from '@/stores/layout'

const layoutStore = useLayoutStore()

const handleResize = () => {
  if (document.documentElement.clientWidth < 768) {
    layoutStore.updateDeviceType('mobile')
    layoutStore.closeSidebar(true)
  } else {
    layoutStore.updateDeviceType('desktop')
  }
  layoutStore.refreshSidebar()
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  // 初始化时也调用一次
  handleResize()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script> 