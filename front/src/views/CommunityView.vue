<template>
  <div class="community-view">
    <div class="page-header">
      <h1>学习社区</h1>
      <el-button type="primary" @click="createPost">
        <el-icon><Plus /></el-icon>发布帖子
      </el-button>
    </div>

    <div class="community-container">
      <div class="sidebar">
        <el-card class="filter-card">
          <template #header>
            <div class="card-header">
              <span>筛选</span>
            </div>
          </template>
          <div class="filter-content">
            <el-input
              v-model="searchQuery"
              placeholder="搜索帖子"
              prefix-icon="Search"
              clearable
              @input="handleSearch"
            />
            <el-select v-model="category" placeholder="选择分类" clearable>
              <el-option label="全部" value="" />
              <el-option label="讨论" value="discussion" />
              <el-option label="问答" value="qa" />
              <el-option label="分享" value="share" />
            </el-select>
            <el-select v-model="sortBy" placeholder="排序方式">
              <el-option label="最新发布" value="latest" />
              <el-option label="最多回复" value="mostReplies" />
              <el-option label="最多浏览" value="mostViews" />
            </el-select>
          </div>
        </el-card>

        <el-card class="hot-topics-card">
          <template #header>
            <div class="card-header">
              <span>热门话题</span>
            </div>
          </template>
          <div class="hot-topics-list">
            <div v-for="topic in hotTopics" :key="topic.id" class="topic-item">
              <el-tag size="small" :type="topic.type">{{ topic.name }}</el-tag>
              <span class="topic-count">{{ topic.count }}个讨论</span>
            </div>
          </div>
        </el-card>
      </div>

      <div class="main-content">
        <div class="post-list">
          <el-card v-for="post in posts" :key="post.id" class="post-card">
            <div class="post-header">
              <div class="user-info">
                <el-avatar :size="40" :src="post.author.avatar" />
                <div class="user-details">
                  <span class="username">{{ post.author.name }}</span>
                  <span class="post-time">{{ post.createTime }}</span>
                </div>
              </div>
              <el-tag size="small" :type="post.category === 'discussion' ? 'primary' : 
                post.category === 'qa' ? 'success' : 'warning'">
                {{ post.category === 'discussion' ? '讨论' : 
                   post.category === 'qa' ? '问答' : '分享' }}
              </el-tag>
            </div>
            <div class="post-content">
              <h3 class="post-title">{{ post.title }}</h3>
              <p class="post-summary">{{ post.summary }}</p>
              <div class="post-images" v-if="post.images && post.images.length">
                <el-image
                  v-for="(image, index) in post.images"
                  :key="index"
                  :src="image"
                  :preview-src-list="post.images"
                  fit="cover"
                  class="post-image"
                />
              </div>
            </div>
            <div class="post-footer">
              <div class="post-stats">
                <span>
                  <el-icon><View /></el-icon>
                  {{ post.views }}
                </span>
                <span>
                  <el-icon><ChatDotRound /></el-icon>
                  {{ post.replies }}
                </span>
                <span>
                  <el-icon><Star /></el-icon>
                  {{ post.likes }}
                </span>
              </div>
              <el-button type="primary" link @click="viewPost(post)">
                查看详情
              </el-button>
            </div>
          </el-card>
        </div>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus, Search, View, ChatDotRound, Star } from '@element-plus/icons-vue'

const searchQuery = ref('')
const category = ref('')
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 模拟数据
const hotTopics = ref([
  { id: 1, name: '编程语言', type: 'primary', count: 128 },
  { id: 2, name: '算法', type: 'success', count: 96 },
  { id: 3, name: '前端开发', type: 'warning', count: 85 },
  { id: 4, name: '后端开发', type: 'info', count: 72 },
  { id: 5, name: '数据库', type: 'danger', count: 64 }
])

const posts = ref([
  {
    id: 1,
    title: '如何提高编程效率？',
    summary: '分享一些实用的编程技巧和工具，帮助提高开发效率...',
    category: 'discussion',
    createTime: '2024-03-18 14:30',
    views: 256,
    replies: 32,
    likes: 45,
    images: ['https://example.com/image1.jpg'],
    author: {
      name: '张三',
      avatar: 'https://example.com/avatar1.jpg'
    }
  },
  {
    id: 2,
    title: 'Vue3 组件开发问题求助',
    summary: '在使用 Vue3 开发组件时遇到了一些问题，希望得到帮助...',
    category: 'qa',
    createTime: '2024-03-18 13:15',
    views: 189,
    replies: 15,
    likes: 23,
    author: {
      name: '李四',
      avatar: 'https://example.com/avatar2.jpg'
    }
  }
])

const handleSearch = () => {
  // 实现搜索逻辑
}

const createPost = () => {
  // 实现发布帖子逻辑
}

const viewPost = (post: any) => {
  // 实现查看帖子详情逻辑
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  // 重新加载数据
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  // 重新加载数据
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/responsive' as responsive;

.community-view {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h1 {
      margin: 0;
      font-size: 24px;
      color: var(--el-text-color-primary);
    }
  }

  .community-container {
    display: grid;
    grid-template-columns: 300px 1fr;
    gap: 20px;

    @include responsive.respond-to('md') {
      grid-template-columns: 250px 1fr;
    }

    @include responsive.respond-to('sm') {
      grid-template-columns: 1fr;
    }

    .sidebar {
      .filter-card {
        margin-bottom: 20px;

        .filter-content {
          display: flex;
          flex-direction: column;
          gap: 15px;
        }
      }

      .hot-topics-card {
        .hot-topics-list {
          display: flex;
          flex-direction: column;
          gap: 10px;

          .topic-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;

            &:hover {
              background-color: var(--el-fill-color-light);
            }

            .topic-count {
              color: var(--el-text-color-secondary);
              font-size: 14px;
            }
          }
        }
      }
    }

    .main-content {
      .post-list {
        display: flex;
        flex-direction: column;
        gap: 20px;

        .post-card {
          .post-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;

            .user-info {
              display: flex;
              align-items: center;
              gap: 10px;

              .user-details {
                display: flex;
                flex-direction: column;

                .username {
                  font-weight: 500;
                  color: var(--el-text-color-primary);
                }

                .post-time {
                  font-size: 12px;
                  color: var(--el-text-color-secondary);
                }
              }
            }
          }

          .post-content {
            .post-title {
              margin: 0 0 10px;
              font-size: 18px;
              color: var(--el-text-color-primary);
            }

            .post-summary {
              margin: 0 0 15px;
              color: var(--el-text-color-regular);
            }

            .post-images {
              display: grid;
              grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
              gap: 10px;
              margin-bottom: 15px;

              .post-image {
                width: 100%;
                height: 150px;
                border-radius: 4px;
                object-fit: cover;
              }
            }
          }

          .post-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .post-stats {
              display: flex;
              gap: 20px;
              color: var(--el-text-color-secondary);

              span {
                display: flex;
                align-items: center;
                gap: 5px;
              }
            }
          }
        }
      }

      .pagination-container {
        display: flex;
        justify-content: center;
        margin-top: 20px;
      }
    }
  }
}
</style> 