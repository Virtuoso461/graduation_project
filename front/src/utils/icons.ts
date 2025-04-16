import type { App } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

/**
 * 全局注册所有Element Plus图标组件
 * 这确保了图标在任何组件中都能被正确渲染
 */
export function registerIcons(app: App) {
  // 注册所有图标为全局组件
  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }
  
  // 记录上次刷新时间，防止频繁刷新
  let lastRefreshTime = 0;
  // 设置最小刷新间隔（毫秒）
  const MIN_REFRESH_INTERVAL = 1000;
  // 是否显示调试日志
  const SHOW_DEBUG_LOGS = false;
  
  // 图标刷新函数
  const refreshIcons = () => {
    const now = Date.now();
    
    // 如果距离上次刷新的时间小于最小间隔，则不执行刷新
    if (now - lastRefreshTime < MIN_REFRESH_INTERVAL) {
      return;
    }
    
    // 更新最后刷新时间
    lastRefreshTime = now;
    
    // 查找所有图标元素
    const icons = document.querySelectorAll('.el-icon, .menu-icon');
    
    // 对每个图标强制重绘
    icons.forEach(icon => {
      const el = icon as HTMLElement
      // 临时隐藏并重新显示以触发重绘
      el.style.display = 'none'
      el.offsetHeight // 强制重排
      el.style.display = ''
      
      // 重置关键CSS属性
      el.style.visibility = 'visible'
      el.style.opacity = '1'
      
      // 额外设置，确保图标可见
      const svgElement = el.querySelector('svg')
      if (svgElement) {
        svgElement.style.visibility = 'visible'
        svgElement.style.opacity = '1'
        svgElement.style.display = 'inline-block'
      }
    })
    
    // 仅在调试模式下显示日志
    if (SHOW_DEBUG_LOGS) {
      console.log('图标已刷新')
    }
  }
  
  // 添加到全局对象，方便在控制台或其他地方调用
  ;(window as any).__refreshIcons = refreshIcons
  
  // DOM变化观察器，用于监测图标元素的变化
  let iconObserver: MutationObserver | null = null
  
  // 设置DOM观察器
  const setupIconObserver = () => {
    // 如果已存在观察器，先断开连接
    if (iconObserver) {
      iconObserver.disconnect()
    }
    
    // 创建新的观察器
    iconObserver = new MutationObserver((mutations) => {
      let shouldRefresh = false
      
      // 检查是否有图标相关元素的变化
      for (const mutation of mutations) {
        if (mutation.type === 'childList' || mutation.type === 'attributes') {
          // 检查变化是否涉及图标元素
          const target = mutation.target as HTMLElement
          if (target.classList?.contains && (
              target.classList.contains('el-icon') || 
              target.classList.contains('menu-icon') ||
              target.closest('.el-icon') || 
              target.closest('.menu-icon'))) {
            shouldRefresh = true
            break
          }
        }
      }
      
      // 如果需要刷新，延迟执行以确保DOM已完全更新
      if (shouldRefresh) {
        setTimeout(refreshIcons, 100)
      }
    })
    
    // 开始观察整个文档
    iconObserver.observe(document.body, {
      childList: true,
      subtree: true,
      attributes: true,
      attributeFilter: ['class', 'style', 'display', 'visibility']
    })
  }
  
  // 添加开发者工具状态变化监听器
  const detectDevTools = () => {
    const threshold = 160;
    const widthThreshold = window.outerWidth - window.innerWidth > threshold;
    const heightThreshold = window.outerHeight - window.innerHeight > threshold;
    
    if (widthThreshold || heightThreshold) {
      // 开发者工具状态变化
      console.log('检测到开发者工具状态变化');
      
      // 延迟处理，确保在开发者工具完全关闭后执行
      setTimeout(handleDevToolsChange, 300);
    }
  }
  
  // 增加专门处理开发者工具切换的函数
  const handleDevToolsChange = () => {
    // 强制进行多次图标刷新，以确保在开发者工具关闭后图标能够正确显示
    refreshIcons();
    
    // 多次延迟刷新，解决某些浏览器渲染延迟问题
    setTimeout(refreshIcons, 200);
    setTimeout(refreshIcons, 500);
    setTimeout(refreshIcons, 1000);
    
    // 强制触发布局重排
    document.body.style.display = 'none';
    document.body.offsetHeight; // 触发重排
    document.body.style.display = '';
    
    // 特殊处理侧边栏图标
    const sidebarIcons = document.querySelectorAll('.app-sidebar .el-icon, .sidebar-menu .el-icon');
    if (sidebarIcons.length > 0) {
      sidebarIcons.forEach(icon => {
        (icon as HTMLElement).style.visibility = 'visible';
        (icon as HTMLElement).style.opacity = '1';
        (icon as HTMLElement).style.display = 'inline-flex';
      });
    }
  };
  
  // 添加键盘事件监听，检测F12键
  window.addEventListener('keydown', (event) => {
    // 检测F12按键或Ctrl+Shift+I组合键
    if (event.key === 'F12' || (event.ctrlKey && event.shiftKey && event.key === 'I')) {
      console.log('检测到开发者工具快捷键');
      setTimeout(handleDevToolsChange, 500);
    }
  });
  
  // 页面加载完成后只刷新一次
  let initialized = false;
  window.addEventListener('load', () => {
    if (!initialized) {
      // 设置观察器
      setupIconObserver()
      
      // 初始刷新
      setTimeout(refreshIcons, 500)
      initialized = true;
    }
  })
  
  // 如果已经加载完成，立即进行一次性初始化
  if (document.readyState === 'complete' && !initialized) {
    setupIconObserver();
    setTimeout(refreshIcons, 500);
    initialized = true;
  }
  
  // 监听窗口大小变化事件，但限制频率
  let resizeTimeout: number | null = null;
  window.addEventListener('resize', () => {
    if (resizeTimeout) {
      window.clearTimeout(resizeTimeout);
    }
    resizeTimeout = window.setTimeout(detectDevTools, 300);
  });
  
  // 改为只在页面载入后检查一次，而不是定期检查
  setTimeout(() => {
    const icons = document.querySelectorAll('.el-icon:not([style*="visible"]), .menu-icon:not([style*="visible"])');
    if (icons.length > 0) {
      refreshIcons();
    }
  }, 2000);
} 