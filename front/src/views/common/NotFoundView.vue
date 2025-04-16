<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h1 class="error-title">页面不存在</h1>
      <p class="error-description">
        抱歉，您访问的页面不存在或已被移除。
      </p>
      <div class="action-buttons">
        <el-button type="primary" @click="goBack">返回上一页</el-button>
        <el-button @click="goHome">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.back()
}

const goHome = () => {
  router.push('/dashboard')
}
</script>

<style lang="scss" scoped>
.not-found-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  padding: 20px;
}

.not-found-content {
  text-align: center;
  max-width: 500px;
  padding: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  
  .error-code {
    font-size: 7rem;
    font-weight: 700;
    line-height: 1;
    color: #409EFF;
    margin-bottom: 20px;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .error-title {
    font-size: 1.75rem;
    font-weight: 600;
    margin: 0 0 16px;
    color: #303133;
  }
  
  .error-description {
    font-size: 1rem;
    color: #606266;
    margin-bottom: 32px;
  }
  
  .action-buttons {
    display: flex;
    justify-content: center;
    gap: 16px;
  }
}
</style> 