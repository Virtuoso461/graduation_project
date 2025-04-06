<template>
  <footer class="app-footer">
    <div class="footer-links">
      <a href="#" target="_blank">关于我们</a>
      <a href="#" target="_blank">联系我们</a>
      <a href="#" target="_blank">使用帮助</a>
      <a href="#" target="_blank">隐私条款</a>
    </div>
    <div class="copyright">
      © {{ new Date().getFullYear() }} 学习矩阵 - 让学习更高效
    </div>
  </footer>
</template>

<script setup lang="ts">
// 页脚组件无需特殊逻辑
</script>

<style scoped>
.app-footer {
  padding: 12px 16px;
  background-color: #fff;
  border-top: 1px solid var(--el-border-color-light);
  text-align: center;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  width: 100%;
  box-sizing: border-box;
  margin: 0;
}

.footer-links {
  margin-bottom: 10px;
}

.footer-links a {
  color: var(--el-text-color-regular);
  margin: 0 12px;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: var(--el-color-primary);
}

.copyright {
  color: var(--el-text-color-secondary);
}

@media (max-width: 768px) {
  .app-footer {
    padding: 12px;
  }
  
  .footer-links a {
    margin: 0 8px;
    font-size: 12px;
  }
  
  .copyright {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .footer-links {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
  }
  
  .footer-links a {
    margin: 0 5px;
  }
}
</style> 