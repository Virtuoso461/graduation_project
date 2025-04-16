<template>
  <div class="bg-gray-100 py-2 px-4 mb-4 rounded">
    <div class="flex items-center text-sm">
      <router-link to="/" class="text-gray-600 hover:text-primary">
        <i class="fas fa-home"></i>
      </router-link>
      
      <template v-for="(item, index) in breadcrumbs" :key="index">
        <span class="mx-2 text-gray-400">/</span>
        
        <router-link 
          v-if="item.path && index < breadcrumbs.length - 1" 
          :to="item.path" 
          class="text-gray-600 hover:text-primary"
        >
          {{ item.title }}
        </router-link>
        
        <span v-else class="text-primary font-medium">
          {{ item.title }}
        </span>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

// 根据当前路由生成面包屑
const breadcrumbs = computed(() => {
  const { matched, meta } = route;
  
  // 如果路由有自定义的面包屑，则使用自定义的
  if (meta.breadcrumb) {
    return meta.breadcrumb;
  }
  
  // 否则根据路由匹配生成面包屑
  return matched
    .filter(item => item.meta && item.meta.title)
    .map(item => ({
      title: item.meta.title,
      path: item.path
    }));
});
</script>
