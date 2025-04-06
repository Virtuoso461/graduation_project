<template>
  <main class="content-area">
    <slot></slot>
  </main>
</template>

<script setup lang="ts">
// 主内容区域无需额外逻辑，仅作为插槽容器
</script>

<style scoped>
.content-area {
  padding: 12px;
  overflow-y: auto;
  background-color: var(--el-fill-color-light);
  flex: 1;
  box-sizing: border-box;
  min-height: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  margin: 0;
}

@media (max-width: 768px) {
  .content-area {
    padding: 8px;
  }
}

@media (max-width: 480px) {
  .content-area {
    padding: 4px;
  }
}
</style> 