<template>
  <div class="favorites-view">
    <div class="page-header">
      <h2>学习收藏夹</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleCreateFolder">
          <el-icon><FolderAdd /></el-icon>
          创建收藏夹
        </el-button>
      </div>
    </div>
    
    <div class="resource-filters">
      <el-input
        v-model="searchQuery"
        placeholder="搜索收藏..."
        class="search-input"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select v-model="resourceType" placeholder="资源类型" clearable>
        <el-option label="全部" value="" />
        <el-option label="文档" value="document" />
        <el-option label="视频" value="video" />
        <el-option label="音频" value="audio" />
        <el-option label="图片" value="image" />
        <el-option label="链接" value="link" />
        <el-option label="笔记" value="note" />
      </el-select>
      
      <el-select v-model="folderFilter" placeholder="收藏夹" clearable>
        <el-option label="全部收藏" value="" />
        <el-option 
          v-for="folder in folders" 
          :key="folder.id" 
          :label="folder.name" 
          :value="folder.id" 
        />
      </el-select>

      <el-select v-model="sortBy" placeholder="排序方式">
        <el-option label="收藏时间" value="collectionTime" />
        <el-option label="名称" value="name" />
        <el-option label="最近访问" value="lastAccessTime" />
      </el-select>
    </div>

    <!-- 收藏夹分组 -->
    <div class="folder-tabs">
      <el-tabs v-model="activeFolder" @tab-click="handleFolderChange">
        <el-tab-pane label="全部收藏" name="all"></el-tab-pane>
        <el-tab-pane 
          v-for="folder in folders" 
          :key="folder.id" 
          :label="folder.name" 
          :name="folder.id"
        ></el-tab-pane>
      </el-tabs>
    </div>
    
    <div class="resources-grid">
      <el-empty v-if="filteredResources.length === 0" description="暂无收藏资源" />
      
      <el-card v-for="resource in filteredResources" 
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
            <el-tag size="small" effect="plain">{{ getResourceTypeName(resource.type) }}</el-tag>
            <span>收藏于: {{ resource.collectionTime }}</span>
            <span v-if="resource.courseReference">来自: {{ resource.courseReference }}</span>
          </div>
          <div class="folder-tag" v-if="resource.folder">
            <el-tag size="small" type="success">{{ getFolderName(resource.folder) }}</el-tag>
          </div>
        </div>
        
        <div class="resource-actions">
          <el-button type="primary" link @click="handleOpen(resource)">
            打开
          </el-button>
          <el-dropdown trigger="click">
            <el-button type="primary" link>
              <span>更多</span>
              <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleMoveToFolder(resource)">
                  <el-icon><FolderOpened /></el-icon>移动收藏夹
                </el-dropdown-item>
                <el-dropdown-item @click="handleRemoveFromFavorite(resource)">
                  <el-icon><Delete /></el-icon>取消收藏
                </el-dropdown-item>
                <el-dropdown-item @click="handleShareResource(resource)">
                  <el-icon><Share /></el-icon>分享
                </el-dropdown-item>
                <el-dropdown-item @click="handleAddNote(resource)">
                  <el-icon><EditPen /></el-icon>添加笔记
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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

    <!-- 新建收藏夹对话框 -->
    <el-dialog
      v-model="folderDialogVisible"
      title="创建收藏夹"
      width="30%"
    >
      <el-form :model="newFolder" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="newFolder.name" placeholder="请输入收藏夹名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input 
            v-model="newFolder.description" 
            type="textarea" 
            placeholder="请输入收藏夹描述（选填）" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="folderDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveFolder">创建</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 移动到收藏夹对话框 -->
    <el-dialog
      v-model="moveDialogVisible"
      title="移动到收藏夹"
      width="30%"
    >
      <el-form label-width="80px">
        <el-form-item label="收藏夹">
          <el-select v-model="targetFolderId" placeholder="请选择收藏夹" style="width: 100%">
            <el-option 
              v-for="folder in folders" 
              :key="folder.id" 
              :label="folder.name" 
              :value="folder.id" 
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="moveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmMove">确认移动</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, 
  VideoCamera, 
  Picture, 
  Headset, 
  Link as LinkIcon, 
  Notebook, 
  Search, 
  FolderAdd, 
  FolderOpened,
  Delete,
  Share,
  EditPen,
  ArrowDown
} from '@element-plus/icons-vue'

// 搜索和筛选
const searchQuery = ref('')
const resourceType = ref('')
const folderFilter = ref('')
const sortBy = ref('collectionTime')
const activeFolder = ref('all')

// 分页
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(100)

// 收藏夹对话框
const folderDialogVisible = ref(false)
const newFolder = ref({
  name: '',
  description: ''
})

// 移动对话框
const moveDialogVisible = ref(false)
const targetFolderId = ref('')
const currentResource = ref<Resource | null>(null)

interface Folder {
  id: string;
  name: string;
  description: string;
  resourceCount: number;
}

interface Resource {
  id: number;
  name: string;
  type: string;
  description: string;
  collectionTime: string;
  folder: string;
  courseReference?: string;
  url: string;
}

// 收藏夹数据
const folders = ref<Folder[]>([
  { id: 'web', name: '前端开发', description: '前端开发相关资源', resourceCount: 12 },
  { id: 'algorithm', name: '算法学习', description: '数据结构与算法资源', resourceCount: 8 },
  { id: 'python', name: 'Python编程', description: 'Python编程学习资源', resourceCount: 15 }
])

// 收藏的资源数据
const favoriteResources = ref<Resource[]>([
  {
    id: 1,
    name: 'Vue.js 3 完全指南',
    type: 'document',
    description: 'Vue.js 3的完整学习教程，从入门到精通的最佳学习资料。',
    collectionTime: '2024-04-10',
    folder: 'web',
    courseReference: '现代前端开发',
    url: '#'
  },
  {
    id: 2,
    name: 'React Hooks 详解',
    type: 'video',
    description: 'React Hooks的详细教程视频，包含常用hooks的使用场景和示例。',
    collectionTime: '2024-04-05',
    folder: 'web',
    courseReference: 'React前端开发',
    url: '#'
  },
  {
    id: 3,
    name: '数据结构与算法分析',
    type: 'document',
    description: '经典的数据结构与算法教程，包含详细的示例和练习题。',
    collectionTime: '2024-03-22',
    folder: 'algorithm',
    courseReference: '数据结构与算法',
    url: '#'
  },
  {
    id: 4,
    name: '二叉树遍历算法讲解',
    type: 'video',
    description: '深入讲解前序、中序、后序遍历的实现和应用。',
    collectionTime: '2024-03-15',
    folder: 'algorithm',
    courseReference: '数据结构与算法',
    url: '#'
  },
  {
    id: 5,
    name: 'Python高级编程技巧',
    type: 'document',
    description: 'Python进阶开发的核心技巧和最佳实践指南。',
    collectionTime: '2024-04-12',
    folder: 'python',
    courseReference: 'Python编程基础',
    url: '#'
  },
  {
    id: 6,
    name: '机器学习入门指南',
    type: 'link',
    description: '使用Python进行机器学习入门的在线教程链接。',
    collectionTime: '2024-04-01',
    folder: 'python',
    courseReference: '人工智能导论',
    url: 'https://example.com/ml-guide'
  },
  {
    id: 7,
    name: 'CSS布局学习笔记',
    type: 'note',
    description: '关于CSS Grid和Flexbox布局的个人学习笔记。',
    collectionTime: '2024-04-08',
    folder: 'web',
    courseReference: '现代前端开发',
    url: '#'
  }
])

// 过滤资源
const filteredResources = computed(() => {
  let result = [...favoriteResources.value]
  
  // 按搜索关键词筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(resource => 
      resource.name.toLowerCase().includes(query) ||
      resource.description.toLowerCase().includes(query)
    )
  }
  
  // 按资源类型筛选
  if (resourceType.value) {
    result = result.filter(resource => resource.type === resourceType.value)
  }
  
  // 按收藏夹筛选（如果使用下拉选择器筛选）
  if (folderFilter.value) {
    result = result.filter(resource => resource.folder === folderFilter.value)
  }
  
  // 按选中的标签页筛选
  if (activeFolder.value !== 'all') {
    result = result.filter(resource => resource.folder === activeFolder.value)
  }
  
  // 排序
  result.sort((a, b) => {
    if (sortBy.value === 'name') {
      return a.name.localeCompare(b.name)
    } else if (sortBy.value === 'collectionTime') {
      return new Date(b.collectionTime).getTime() - new Date(a.collectionTime).getTime()
    }
    // 默认按收藏时间排序
    return new Date(b.collectionTime).getTime() - new Date(a.collectionTime).getTime()
  })
  
  return result
})

// 获取资源类型图标
const getResourceIcon = (type: string) => {
  const icons: Record<string, any> = {
    document: Document,
    video: VideoCamera,
    image: Picture,
    audio: Headset,
    link: LinkIcon,
    note: Notebook
  }
  return icons[type] || Document
}

// 获取资源类型名称
const getResourceTypeName = (type: string) => {
  const typeNames: Record<string, string> = {
    document: '文档',
    video: '视频',
    image: '图片',
    audio: '音频',
    link: '链接',
    note: '笔记'
  }
  return typeNames[type] || '其他'
}

// 获取收藏夹名称
const getFolderName = (folderId: string) => {
  const folder = folders.value.find(f => f.id === folderId)
  return folder ? folder.name : '默认收藏夹'
}

// 处理收藏夹切换
const handleFolderChange = (tab: any) => {
  // 这里可以根据标签页切换更新资源列表
  console.log('切换到收藏夹:', tab.props.name)
}

// 打开资源
const handleOpen = (resource: any) => {
  // 根据资源类型打开不同的查看方式
  console.log('打开资源:', resource)
  
  // 可以根据资源类型打开不同的查看方式
  if (resource.type === 'link') {
    window.open(resource.url, '_blank')
  } else {
    // 其他类型资源可以使用模态框或导航到详情页面
    ElMessage.success(`打开${getResourceTypeName(resource.type)}: ${resource.name}`)
  }
}

// 创建收藏夹
const handleCreateFolder = () => {
  folderDialogVisible.value = true
  newFolder.value = {
    name: '',
    description: ''
  }
}

// 保存收藏夹
const handleSaveFolder = () => {
  if (!newFolder.value.name) {
    ElMessage.warning('请输入收藏夹名称')
    return
  }
  
  // 生成新收藏夹ID
  const newId = `folder_${Date.now()}`
  
  // 添加到收藏夹列表
  folders.value.push({
    id: newId,
    name: newFolder.value.name,
    description: newFolder.value.description,
    resourceCount: 0
  })
  
  ElMessage.success(`收藏夹 ${newFolder.value.name} 创建成功`)
  folderDialogVisible.value = false
  
  // 切换到新创建的收藏夹
  activeFolder.value = newId
}

// 移动到收藏夹
const handleMoveToFolder = (resource: any) => {
  currentResource.value = resource
  targetFolderId.value = resource.folder || ''
  moveDialogVisible.value = true
}

// 确认移动
const handleConfirmMove = () => {
  if (!targetFolderId.value) {
    ElMessage.warning('请选择目标收藏夹')
    return
  }
  
  if (currentResource.value) {
    // 更新资源的收藏夹
    const index = favoriteResources.value.findIndex(r => r.id === currentResource.value.id)
    if (index !== -1) {
      favoriteResources.value[index].folder = targetFolderId.value
      ElMessage.success(`已将资源移动到 ${getFolderName(targetFolderId.value)}`)
    }
  }
  
  moveDialogVisible.value = false
}

// 从收藏夹中移除
const handleRemoveFromFavorite = (resource: any) => {
  ElMessageBox.confirm(
    `确定要取消收藏 "${resource.name}" 吗？`,
    '取消收藏',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 从收藏列表中移除
    const index = favoriteResources.value.findIndex(r => r.id === resource.id)
    if (index !== -1) {
      favoriteResources.value.splice(index, 1)
      ElMessage.success('已取消收藏')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 分享资源
const handleShareResource = (resource: any) => {
  ElMessage.success(`已生成 ${resource.name} 的分享链接`)
}

// 添加笔记
const handleAddNote = (resource: any) => {
  ElMessage.info('打开笔记编辑器')
}
</script>

<style lang="scss" scoped>
.favorites-view {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h2 {
      margin: 0;
      font-size: 24px;
      font-weight: 600;
    }
  }
  
  .resource-filters {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    flex-wrap: wrap;
    
    .search-input {
      min-width: 240px;
    }
    
    .el-select {
      min-width: 160px;
    }
  }

  .folder-tabs {
    margin-bottom: 24px;
  }
  
  .resources-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
    gap: 20px;
    margin-bottom: 24px;
    
    .resource-card {
      display: flex;
      height: 100%;
      transition: transform 0.3s;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
      }
      
      .resource-icon {
        padding: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--el-color-primary);
      }
      
      .resource-info {
        flex: 1;
        padding: 16px 16px 16px 0;
        
        h3 {
          margin: 0 0 8px;
          font-size: 18px;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }
        
        .description {
          margin: 0 0 12px;
          color: var(--el-text-color-regular);
          font-size: 14px;
          line-height: 1.5;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .meta {
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
          margin-bottom: 8px;
          font-size: 13px;
          color: var(--el-text-color-secondary);
        }
        
        .folder-tag {
          margin-top: 8px;
        }
      }
      
      .resource-actions {
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 8px;
        padding-right: 16px;
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}

// 响应式样式
@media (max-width: 768px) {
  .favorites-view {
    padding: 16px;
    
    .resource-filters {
      .search-input,
      .el-select {
        width: 100%;
      }
    }
    
    .resources-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style> 