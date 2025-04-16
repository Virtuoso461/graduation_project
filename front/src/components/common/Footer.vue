<template>
  <footer class="bg-gray-800 text-white py-8">
    <div class="container mx-auto px-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <div>
          <h3 class="text-lg font-semibold mb-4">关于我们</h3>
          <p class="text-gray-300">
            我们致力于提供高质量的在线教育平台，帮助学生和教师实现更好的教学互动和学习体验。
          </p>
        </div>
        
        <div>
          <h3 class="text-lg font-semibold mb-4">快速链接</h3>
          <ul class="space-y-2">
            <li><router-link to="/" class="text-gray-300 hover:text-white">首页</router-link></li>
            <li><router-link to="/student/courses" class="text-gray-300 hover:text-white">课程</router-link></li>
            <li><router-link to="/student/resources" class="text-gray-300 hover:text-white">资源</router-link></li>
            <li><router-link to="/student/community" class="text-gray-300 hover:text-white">社区</router-link></li>
          </ul>
        </div>
        
        <div>
          <h3 class="text-lg font-semibold mb-4">联系我们</h3>
          <ul class="space-y-2 text-gray-300">
            <li class="flex items-center">
              <i class="fas fa-envelope mr-2"></i>
              <span>contact@example.com</span>
            </li>
            <li class="flex items-center">
              <i class="fas fa-phone mr-2"></i>
              <span>+86 123 4567 8901</span>
            </li>
            <li class="flex items-center">
              <i class="fas fa-map-marker-alt mr-2"></i>
              <span>北京市海淀区</span>
            </li>
          </ul>
        </div>
        
        <div>
          <h3 class="text-lg font-semibold mb-4">关注我们</h3>
          <div class="flex space-x-4">
            <a href="#" class="text-gray-300 hover:text-white">
              <i class="fab fa-weixin text-2xl"></i>
            </a>
            <a href="#" class="text-gray-300 hover:text-white">
              <i class="fab fa-weibo text-2xl"></i>
            </a>
            <a href="#" class="text-gray-300 hover:text-white">
              <i class="fab fa-qq text-2xl"></i>
            </a>
            <a href="#" class="text-gray-300 hover:text-white">
              <i class="fab fa-github text-2xl"></i>
            </a>
          </div>
        </div>
      </div>
      
      <div class="border-t border-gray-700 mt-8 pt-6 text-center text-gray-400">
        <p>&copy; {{ new Date().getFullYear() }} 学习平台. 保留所有权利.</p>
      </div>
    </div>
  </footer>
</template>

<script lang="ts" setup>
// 无需额外逻辑
</script>
