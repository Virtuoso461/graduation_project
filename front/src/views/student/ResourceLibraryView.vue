<template>
  <div class="resource-library">
    <div class="page-header">
      <h2>资源库</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleUpload">
          <el-icon><Upload /></el-icon>
          上传资源
        </el-button>
      </div>
    </div>
    
    <div class="resource-filters">
      <el-input
        v-model="searchQuery"
        placeholder="搜索资源..."
        class="search-input"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select v-model="resourceType" placeholder="资源类型" clearable>
        <el-option label="文档" value="document" />
        <el-option label="视频" value="video" />
        <el-option label="音频" value="audio" />
        <el-option label="图片" value="image" />
      </el-select>
      
      <el-select v-model="sortBy" placeholder="排序方式">
        <el-option label="上传时间" value="uploadTime" />
        <el-option label="下载次数" value="downloads" />
        <el-option label="文件大小" value="size" />
      </el-select>
    </div>
    
    <div class="resource-grid">
      <el-empty v-if="resources.length === 0" description="暂无资源" />
      
      <el-card v-for="resource in resources" 
               :key="resource.id" 
               class="resource-card"
               shadow="hover">
        <div class="resource-icon">
          <el-icon :size="40">
            <component :is="getResourceIcon(resource.type)" />
          </el-icon>
        </div>
        
        <div class="resource-info">
          <h3>{{ resource.name }}</h3>
          <p class="description">{{ resource.description }}</p>
          <div class="meta">
            <span>{{ resource.size }}</span>
            <span>{{ resource.uploadTime }}</span>
            <span>{{ resource.downloads }} 次下载</span>
          </div>
        </div>
        
        <div class="resource-actions">
          <el-button type="primary" link @click="handleDownload(resource)">
            下载
          </el-button>
          <el-button type="primary" link @click="handlePreview(resource)">
            预览
          </el-button>
          <el-button type="success" link @click="handleAddToFavorites(resource)">
            <el-icon><Star /></el-icon>收藏
          </el-button>
        </div>
      </el-card>
    </div>
    
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Document, VideoCamera, Picture, Headset, Upload, Search, Star } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchQuery = ref('')
const resourceType = ref('')
const sortBy = ref('uploadTime')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(100)

// 模拟资源数据
const resources = ref([
  {
    id: 1,
    name: 'Vue.js 开发指南',
    type: 'document',
    description: 'Vue.js 框架的详细开发指南，包含最佳实践和示例代码。',
    size: '2.5MB',
    uploadTime: '2024-03-15',
    downloads: 128
  },
  {
    id: 2,
    name: 'React 基础教程',
    type: 'video',
    description: 'React 框架的基础教程视频，适合初学者。',
    size: '156MB',
    uploadTime: '2024-03-14',
    downloads: 256
  }
])

const getResourceIcon = (type: string) => {
  const icons: Record<string, any> = {
    document: Document,
    video: VideoCamera,
    image: Picture,
    audio: Headset
  }
  return icons[type] || Document
}

const handleUpload = () => {
  // 实现上传功能
}

const handleDownload = (resource: any) => {
  // 实现下载功能
}

const handlePreview = (resource: any) => {
  // 实现预览功能
}

const handleAddToFavorites = (resource: any) => {
  ElMessageBox.confirm(
    '确定将该资源添加到收藏夹吗？',
    '添加收藏',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 这里模拟添加收藏的逻辑
    // 实际项目中会调用API添加到收藏夹
    ElMessage.success(`已将"${resource.name}"添加到收藏夹`)
  }).catch(() => {
    // 用户取消操作
  })
}
</script>

<style lang="scss" scoped>
.resource-library {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h2 {
      margin: 0;
      font-size: 24px;
      color: #303133;
    }
  }
  
  .resource-filters {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    
    .search-input {
      width: 300px;
    }
    
    @include respond-to('md') {
      flex-wrap: wrap;
      
      .search-input {
        width: 100%;
      }
    }
  }
  
  .resource-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    margin-bottom: 24px;
    
    @include respond-to('md') {
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 16px;
    }
  }
  
  .resource-card {
    display: flex;
    flex-direction: column;
    gap: 16px;
    
    .resource-icon {
      display: flex;
      justify-content: center;
      color: #409eff;
    }
    
    .resource-info {
      flex: 1;
      
      h3 {
        margin: 0 0 8px;
        font-size: 16px;
        color: #303133;
      }
      
      .description {
        margin: 0 0 12px;
        font-size: 14px;
        color: #606266;
        line-height: 1.5;
      }
      
      .meta {
        display: flex;
        gap: 16px;
        font-size: 12px;
        color: #909399;
      }
    }
    
    .resource-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
  }
}
</style> 