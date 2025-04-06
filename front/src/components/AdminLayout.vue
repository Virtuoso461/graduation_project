<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="240px" class="aside">
        <div class="logo">
          <h1>管理后台</h1>
        </div>
        <el-menu
          class="admin-menu"
          :default-active="activeMenu"
          :router="true"
          :unique-opened="true"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>控制面板</span>
          </el-menu-item>
          
          <el-sub-menu index="1">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/users">
              <el-icon><List /></el-icon>
              <span>用户列表</span>
            </el-menu-item>
            <el-menu-item index="/admin/users/add">
              <el-icon><Plus /></el-icon>
              <span>添加用户</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="2">
            <template #title>
              <el-icon><Reading /></el-icon>
              <span>课程管理</span>
            </template>
            <el-menu-item index="/admin/courses">
              <el-icon><List /></el-icon>
              <span>课程列表</span>
            </el-menu-item>
            <el-menu-item index="/admin/courses/approval">
              <el-icon><Check /></el-icon>
              <span>课程审核</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-menu-item index="/admin/settings">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>
          
          <el-menu-item index="/">
            <el-icon><Back /></el-icon>
            <span>返回前台</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container class="main-container">
        <el-header class="header">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
                {{ item }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="user-info">
            <el-dropdown trigger="click">
              <div class="admin-avatar">
                <el-avatar :size="32" :src="userAvatar">
                  {{ userInfo?.name?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span class="admin-name">{{ userInfo?.name }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="main">
          <router-view v-slot="{ Component }">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DataBoard,
  User,
  Reading, 
  Setting,
  List,
  Plus,
  Check,
  ArrowDown,
  Back
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const userAvatar = computed(() => userStore.userInfo?.avatar || '')

// 激活的菜单项
const activeMenu = computed(() => route.path)

// 面包屑导航
const breadcrumbs = ref<string[]>([])

// 根据路由设置面包屑
onMounted(() => {
  updateBreadcrumb()
})

// 监听路由变化
router.afterEach(() => {
  updateBreadcrumb()
})

// 更新面包屑
const updateBreadcrumb = () => {
  const path = route.path
  const matched = route.matched

  // 清空当前面包屑
  breadcrumbs.value = []

  // 从路由匹配记录中获取标题
  for (let i = 1; i < matched.length; i++) {
    if (matched[i].meta.title) {
      breadcrumbs.value.push(matched[i].meta.title as string)
    }
  }
}

// 退出登录
const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
    ElMessage.success('退出成功')
  }).catch(() => {})
}
</script>

<style scoped lang="scss">
.admin-layout {
  height: 100vh;
  display: flex;
  overflow: hidden;
  
  .aside {
    background-color: #304156;
    color: #fff;
    
    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-bottom: 1px solid #1f2d3d;
      
      h1 {
        color: #fff;
        font-size: 18px;
        margin: 0;
      }
    }
    
    .admin-menu {
      border-right: none;
      background-color: #304156;
      
      :deep(.el-menu),
      :deep(.el-menu-item),
      :deep(.el-sub-menu__title) {
        background-color: #304156;
        color: #bfcbd9;
        
        &:hover, &.is-active {
          background-color: #263445;
          color: #409EFF;
        }
      }
      
      :deep(.el-menu-item.is-active) {
        background-color: #263445;
        color: #409EFF;
      }
    }
  }
  
  .main-container {
    background-color: #f0f2f5;
    
    .header {
      background-color: #fff;
      color: #333;
      display: flex;
      align-items: center;
      justify-content: space-between;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      
      .breadcrumb {
        font-size: 14px;
      }
      
      .user-info {
        display: flex;
        align-items: center;
        
        .admin-avatar {
          display: flex;
          align-items: center;
          cursor: pointer;
          
          .admin-name {
            margin: 0 8px;
            font-size: 14px;
          }
        }
      }
    }
    
    .main {
      padding: 20px;
      overflow-y: auto;
    }
  }
}
</style> 