<template>
  <app-layout>
    <div class="search-view">
      <div class="page-header">
        <h1 class="page-title">搜索结果</h1>
        <div class="search-stats">
          找到 {{ searchResults.length }} 个相关结果
        </div>
      </div>
      
      <div class="search-filters">
        <el-select v-model="searchType" placeholder="搜索类型">
          <el-option label="全部" value="all" />
          <el-option label="课程" value="course" />
          <el-option label="知识点" value="knowledge" />
          <el-option label="资源" value="resource" />
        </el-select>
        
        <el-select v-model="sortBy" placeholder="排序方式">
          <el-option label="相关度" value="relevance" />
          <el-option label="最新" value="latest" />
          <el-option label="最热" value="popular" />
        </el-select>
      </div>
      
      <div class="search-results">
        <div v-if="searchResults.length > 0" class="result-list">
          <div 
            v-for="result in searchResults" 
            :key="result.id"
            class="result-item"
          >
            <div class="result-icon">
              <el-icon :class="getResultIcon(result.type)"></el-icon>
            </div>
            <div class="result-content">
              <h3 class="result-title">{{ result.title }}</h3>
              <p class="result-description">{{ result.description }}</p>
              <div class="result-meta">
                <span class="result-type">{{ getResultTypeLabel(result.type) }}</span>
                <span class="result-date">{{ result.date }}</span>
                <span class="result-views">{{ result.views }}次浏览</span>
              </div>
            </div>
            <div class="result-actions">
              <el-button type="primary" size="small">查看详情</el-button>
            </div>
          </div>
        </div>
        <div v-else class="no-results">
          <el-empty description="暂无搜索结果" />
        </div>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
        />
      </div>
    </div>
  </app-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import { 
  Document,
  VideoPlay,
  Link,
  Collection
} from '@element-plus/icons-vue'

// 搜索类型
const searchType = ref('all')

// 排序方式
const sortBy = ref('relevance')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 搜索结果
const searchResults = ref([
  {
    id: 1,
    type: 'course',
    title: '数据结构与算法分析',
    description: '本课程介绍基本的数据结构和算法，包括数组、链表、栈、队列、树、图等数据结构，以及排序、搜索等算法。',
    date: '2024-03-18',
    views: 1234
  },
  {
    id: 2,
    type: 'knowledge',
    title: '二叉树的基本概念',
    description: '二叉树是一种特殊的树形数据结构，每个节点最多有两个子节点，分别称为左子节点和右子节点。',
    date: '2024-03-17',
    views: 856
  },
  {
    id: 3,
    type: 'resource',
    title: '算法可视化工具',
    description: '一个交互式的算法可视化工具，帮助理解各种算法的执行过程。',
    date: '2024-03-16',
    views: 2341
  }
])

// 获取结果类型对应的图标
const getResultIcon = (type: string) => {
  switch(type) {
    case 'course': return 'Collection'
    case 'knowledge': return 'Document'
    case 'resource': return 'Link'
    default: return 'Document'
  }
}

// 获取结果类型对应的标签
const getResultTypeLabel = (type: string) => {
  switch(type) {
    case 'course': return '课程'
    case 'knowledge': return '知识点'
    case 'resource': return '资源'
    default: return '其他'
  }
}
</script>

<style lang="scss" scoped>
// 导入响应式样式工具
@use '@/assets/styles/responsive' as responsive;

.search-view {
  padding-bottom: 24px;
  min-height: 100vh;
  width: 100%;
}

.page-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  @include responsive.respond-to('md') {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .page-title {
    font-size: 1.75rem;
    font-weight: 600;
    margin: 0;
    
    @include responsive.respond-to('md') {
      font-size: 1.5rem;
    }
    
    @include responsive.respond-to('sm') {
      font-size: 1.25rem;
    }
  }
  
  .search-stats {
    color: #606266;
    font-size: 0.95rem;
    
    @include responsive.respond-to('md') {
      font-size: 0.9rem;
    }
  }
}

.search-filters {
  margin-bottom: 24px;
  display: flex;
  gap: 16px;
  
  @include responsive.respond-to('md') {
    gap: 12px;
  }
  
  @include responsive.respond-to('sm') {
    flex-direction: column;
    gap: 12px;
    
    .el-select {
      width: 100%;
    }
  }
}

.search-results {
  .result-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    
    @include responsive.respond-to('md') {
      gap: 12px;
    }
    
    .result-item {
      display: flex;
      gap: 16px;
      padding: 20px;
      background-color: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      transition: all 0.3s;
      
      @include responsive.respond-to('md') {
        padding: 16px;
        gap: 12px;
      }
      
      @include responsive.respond-to('sm') {
        flex-wrap: wrap;
      }
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      .result-icon {
        font-size: 24px;
        color: #409EFF;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        
        @include responsive.respond-to('md') {
          font-size: 20px;
        }
      }
      
      .result-content {
        flex: 1;
        min-width: 0; // 防止文本溢出
        
        .result-title {
          font-size: 1.1rem;
          font-weight: 600;
          margin: 0 0 8px;
          color: #303133;
          
          @include responsive.respond-to('md') {
            font-size: 1rem;
          }
        }
        
        .result-description {
          font-size: 0.95rem;
          color: #606266;
          margin: 0 0 12px;
          line-height: 1.5;
          
          @include responsive.respond-to('md') {
            font-size: 0.9rem;
            margin: 0 0 8px;
          }
          
          @include responsive.respond-to('sm') {
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
        }
        
        .result-meta {
          display: flex;
          gap: 16px;
          font-size: 0.85rem;
          color: #909399;
          
          @include responsive.respond-to('md') {
            gap: 12px;
            font-size: 0.8rem;
            flex-wrap: wrap;
          }
          
          .result-type {
            color: #409EFF;
          }
        }
      }
      
      .result-actions {
        display: flex;
        align-items: center;
        flex-shrink: 0;
        
        @include responsive.respond-to('sm') {
          margin-top: 8px;
          width: 100%;
          
          .el-button {
            width: 100%;
          }
        }
      }
    }
  }
  
  .no-results {
    padding: 40px;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    
    @include responsive.respond-to('md') {
      padding: 30px;
    }
    
    @include responsive.respond-to('sm') {
      padding: 20px;
    }
  }
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
  
  @include responsive.respond-to('md') {
    margin-top: 20px;
  }
  
  @include responsive.respond-to('sm') {
    :deep(.el-pagination) {
      flex-wrap: wrap;
      justify-content: center;
      
      .el-pagination__sizes {
        margin-bottom: 10px;
      }
    }
  }
}
</style> 