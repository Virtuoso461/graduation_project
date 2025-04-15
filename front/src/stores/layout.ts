import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface MenuItem {
  path: string;
  title: string;
  icon?: string;
  children?: MenuItem[];
}

export const useLayoutStore = defineStore('layout', () => {
  const device = ref('desktop')
  const sidebar = ref({
    opened: true,
    withoutAnimation: false
  })

  // 侧边栏状态
  const sidebarCollapsed = ref(false)
  const sidebarExpanded = ref(false)
  
  // 移动设备检测
  const isMobile = ref(false)
  
  // 侧边栏切换方法
  const toggleSidebarCollapse = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }
  
  // 移动端侧边栏展开/收起
  const toggleMobileSidebar = () => {
    sidebarExpanded.value = !sidebarExpanded.value
  }
  
  const closeSidebar = (withoutAnimation: boolean) => {
    sidebar.value.opened = false
    sidebar.value.withoutAnimation = withoutAnimation
  }
  
  // 更新移动设备状态
  const updateMobileStatus = (status: boolean) => {
    isMobile.value = status
    // 在移动设备上自动折叠侧边栏
    if (isMobile.value && !sidebarCollapsed.value) {
      sidebarCollapsed.value = true
    }
  }
  
  // 菜单项
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
      path: '/favorites',
      title: '学习收藏夹',
      icon: 'Star'
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
      path: '/assignments',
      title: '在线作业',
      icon: 'Notebook'
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
  
  // 增强 refreshSidebar 方法
  const refreshSidebar = () => {
    // 保存当前的收缩状态
    const currentCollapsed = sidebarCollapsed.value
    
    // 强制触发视图更新 - 通过快速切换状态
    sidebarCollapsed.value = !currentCollapsed
    setTimeout(() => {
      sidebarCollapsed.value = currentCollapsed
      
      // 根据设备类型处理侧边栏状态
      if (device.value === 'mobile') {
        closeSidebar(true)
      } else {
        sidebar.value.opened = true
      }
    }, 50)
  }

  const updateDeviceType = (type: string | number) => {
    if (typeof type === 'number') {
      // 如果是数字类型，根据宽度判断设备类型
      device.value = type <= 768 ? 'mobile' : 'desktop' 
    } else {
      // 如果是字符串类型，直接设置
      device.value = type
    }
  }

  const toggleSidebar = () => {
    sidebar.value.opened = !sidebar.value.opened
    sidebar.value.withoutAnimation = false
  }

  return {
    device,
    sidebar,
    sidebarCollapsed,
    sidebarExpanded,
    isMobile,
    menuItems,
    toggleSidebarCollapse,
    toggleMobileSidebar,
    closeSidebar,
    updateMobileStatus,
    updateDeviceType,  // 导出方法
    toggleSidebar,
    refreshSidebar  // 导出新方法
  }
}) 