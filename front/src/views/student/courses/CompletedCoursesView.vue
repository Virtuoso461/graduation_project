<template>
  <div class="completed-courses">
    <div class="course-filters">
      <el-input
        v-model="searchQuery"
        placeholder="搜索课程"
        prefix-icon="Search"
        clearable
        class="search-input"
      />
      <el-select v-model="filter.category" placeholder="课程分类" clearable>
        <el-option
          v-for="item in categories"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select v-model="filter.sort" placeholder="排序方式">
        <el-option label="完成时间" value="completionDate" />
        <el-option label="课程评分" value="rating" />
        <el-option label="名称" value="name" />
      </el-select>
    </div>
    
    <div v-if="loading" class="courses-loading">
      <div class="loading-animation">
        <div class="loading-circle"></div>
        <div class="loading-text">加载中...</div>
      </div>
      <el-skeleton :rows="3" animated />
      <el-skeleton :rows="3" animated style="margin-top: 20px" />
    </div>
    
    <div v-else-if="filteredCourses.length === 0" class="empty-state">
      <div class="empty-illustration">
        <el-icon class="empty-icon"><Collection /></el-icon>
      </div>
      <h3 class="empty-title">暂无已完成课程</h3>
      <p class="empty-desc">您当前没有已完成的课程</p>
      <el-button type="primary" @click="navigateToCourses" class="empty-action">
        <el-icon><Plus /></el-icon>
        浏览课程
      </el-button>
    </div>
    
    <div v-else class="course-grid">
      <el-card 
        v-for="course in filteredCourses" 
        :key="course.id"
        class="course-card"
        :body-style="{ padding: '0px' }"
        shadow="hover"
        @click="viewCourseDetail(course.id)"
      >
        <div class="course-cover" :style="{ backgroundColor: course.color }">
          <span class="completion-badge">
            <el-icon><CircleCheckFilled /></el-icon>
            已完成
          </span>
          <el-icon class="course-icon"><component :is="course.icon" /></el-icon>
          <div class="course-overlay">
            <el-button class="view-details-btn" round>
              查看详情
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
        <div class="course-content">
          <h3 class="course-title">{{ course.title }}</h3>
          <div class="course-info">
            <span class="info-item">
              <el-icon><User /></el-icon>
              {{ course.instructor }}
            </span>
            <span class="info-item">
              <el-icon><Calendar /></el-icon>
              {{ course.completionDate }}
            </span>
          </div>
          <div class="course-rating">
            <span class="rating-label">评分:</span>
            <el-rate
              v-model="course.rating"
              disabled
              :colors="ratingColors"
              :show-score="true"
              score-template="{value}"
              text-color="#ff9900"
              class="rating-stars"
            />
          </div>
          <div class="course-description" v-if="course.description">
            {{ course.description }}
          </div>
          <div class="course-footer">
            <el-tag size="small" :style="{ backgroundColor: course.color + '20', color: course.color, border: 'none' }">
              {{ course.category }}
            </el-tag>
            <div class="action-buttons">
              <el-button type="primary" text size="small" @click.stop="viewCertificate(course.id)" class="action-btn">
                <el-icon><Document /></el-icon>查看证书
              </el-button>
              <el-button type="success" text size="small" @click.stop="reviewCourse(course.id)" class="action-btn">
                <el-icon><View /></el-icon>复习课程
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  User, 
  Calendar, 
  Document,
  View,
  CircleCheckFilled,
  Collection,
  Reading,
  DataAnalysis,
  Monitor,
  Setting,
  VideoPlay,
  Plus,
  ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(true)
const searchQuery = ref('')

// 课程分类
const categories = [
  { value: '计算机科学', label: '计算机科学' },
  { value: '数学', label: '数学' },
  { value: '人工智能', label: '人工智能' },
  { value: '工程学', label: '工程学' }
]

// 评分颜色
const ratingColors = ['#99A9BF', '#F7BA2A', '#FF9900']

// 过滤和排序
const filter = reactive({
  category: '',
  sort: 'completionDate'
})

// 课程数据
const courses = ref([
  {
    id: 1,
    title: '操作系统原理',
    instructor: '刘教授',
    completionDate: '2024-01-15',
    rating: 4.5,
    color: '#F56C6C',
    category: '计算机科学',
    icon: 'Monitor',
    certificate: true,
    description: '操作系统核心概念与设计原则，包括进程管理、内存管理和文件系统等。'
  },
  {
    id: 2,
    title: '线性代数',
    instructor: '王教授',
    completionDate: '2024-02-10',
    rating: 5.0,
    color: '#E6A23C',
    category: '数学',
    icon: 'Reading',
    certificate: true
  },
  {
    id: 3,
    title: '计算机网络',
    instructor: '张教授',
    completionDate: '2023-12-20',
    rating: 4.0,
    color: '#409EFF',
    category: '计算机科学',
    icon: 'Connection',
    certificate: true
  },
  {
    id: 4,
    title: '软件工程概论',
    instructor: '李教授',
    completionDate: '2023-11-30',
    rating: 4.7,
    color: '#67C23A',
    category: '工程学',
    icon: 'Setting',
    certificate: true
  },
  {
    id: 5,
    title: '多媒体技术基础',
    instructor: '赵教授',
    completionDate: '2023-10-25',
    rating: 3.8,
    color: '#9B59B6',
    category: '计算机科学',
    icon: 'VideoPlay',
    certificate: false
  }
])

// 过滤和排序课程
const filteredCourses = computed(() => {
  let result = [...courses.value]
  
  // 根据搜索关键词过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(course => 
      course.title.toLowerCase().includes(query) || 
      course.instructor.toLowerCase().includes(query)
    )
  }
  
  // 根据分类过滤
  if (filter.category) {
    result = result.filter(course => course.category === filter.category)
  }
  
  // 排序
  switch (filter.sort) {
    case 'rating':
      result.sort((a, b) => b.rating - a.rating)
      break
    case 'name':
      result.sort((a, b) => a.title.localeCompare(b.title))
      break
    case 'completionDate':
    default:
      result.sort((a, b) => new Date(b.completionDate).getTime() - new Date(a.completionDate).getTime())
      break
  }
  
  return result
})

// 查看课程详情
const viewCourseDetail = (courseId: number) => {
  router.push(`/courses/${courseId}`)
}

// 查看证书
const viewCertificate = (courseId: number) => {
  // 模拟查看证书功能
  console.log(`查看课程 ${courseId} 的证书`)
}

// 复习课程
const reviewCourse = (courseId: number) => {
  // 模拟复习课程功能
  router.push(`/courses/${courseId}?mode=review`)
}

// 导航到课程列表
const navigateToCourses = () => {
  router.push('/courses/enrolled')
}

// 初始化
onMounted(() => {
  // 模拟加载
  setTimeout(() => {
    loading.value = false
  }, 800)
})
</script>

<style lang="scss" scoped>
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.completed-courses {
  animation: fadeIn 0.5s ease-out;
  
  .course-filters {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    flex-wrap: wrap;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    padding: 16px;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
    }
    
    .search-input {
      width: 240px;
      transition: all 0.3s ease;
      
      &:focus-within {
        transform: translateY(-2px);
      }
    }
    
    .el-select {
      min-width: 140px;
    }
  }
  
  .courses-loading {
    padding: 32px;
    background-color: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    
    .loading-animation {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 24px;
      
      .loading-circle {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        border: 3px solid #f3f3f3;
        border-top: 3px solid #409EFF;
        animation: spin 1s linear infinite;
        margin-bottom: 12px;
      }
      
      .loading-text {
        color: #909399;
        font-size: 14px;
      }
      
      @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
      }
    }
  }
  
  .empty-state {
    padding: 60px 0;
    background-color: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .empty-illustration {
      width: 100px;
      height: 100px;
      background: linear-gradient(135deg, #f8f9fa, #e9ecef);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 24px;
      
      .empty-icon {
        font-size: 40px;
        color: #adb5bd;
      }
    }
    
    .empty-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }
    
    .empty-desc {
      color: #909399;
      margin-bottom: 24px;
      max-width: 300px;
    }
    
    .empty-action {
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25);
      }
    }
  }
  
  .course-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 24px;
    
    @media (max-width: 768px) {
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 20px;
    }
    
    @media (max-width: 480px) {
      grid-template-columns: 1fr;
      gap: 16px;
    }
    
    .course-card {
      cursor: pointer;
      transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
      border: none;
      overflow: hidden;
      border-radius: 12px;
      animation: fadeIn 0.5s ease-out;
      animation-fill-mode: both;
      
      @for $i from 1 through 10 {
        &:nth-child(#{$i}) {
          animation-delay: #{$i * 0.1}s;
        }
      }
      
      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 16px 32px rgba(0, 0, 0, 0.15);
        
        .course-cover {
          .course-icon {
            transform: scale(1.1) rotate(5deg);
          }
          
          .course-overlay {
            opacity: 1;
          }
        }
      }
      
      .course-cover {
        height: 160px;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;
        position: relative;
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: radial-gradient(circle at center, rgba(255,255,255,0.2) 0%, rgba(0,0,0,0.1) 100%);
        }
        
        .completion-badge {
          position: absolute;
          top: 12px;
          right: 12px;
          background-color: rgba(103, 194, 58, 0.9);
          color: white;
          padding: 6px 10px;
          border-radius: 20px;
          font-size: 12px;
          display: flex;
          align-items: center;
          gap: 4px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
          z-index: 2;
          backdrop-filter: blur(4px);
        }
        
        .course-icon {
          font-size: 70px;
          color: rgba(255, 255, 255, 0.9);
          filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.2));
          transition: all 0.5s ease;
          z-index: 1;
        }
        
        .course-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.4);
          display: flex;
          align-items: center;
          justify-content: center;
          opacity: 0;
          transition: all 0.3s ease;
          z-index: 3;
          backdrop-filter: blur(2px);
          
          .view-details-btn {
            background: rgba(255, 255, 255, 0.9);
            color: #303133;
            border: none;
            padding: 10px 20px;
            font-weight: 500;
            transform: translateY(10px);
            transition: all 0.3s ease;
            
            &:hover {
              background: white;
              transform: translateY(8px) scale(1.05);
            }
          }
        }
      }
      
      .course-content {
        padding: 20px;
        
        .course-title {
          font-size: 18px;
          font-weight: 600;
          margin-bottom: 12px;
          color: #303133;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          line-height: 1.4;
        }
        
        .course-info {
          display: flex;
          justify-content: space-between;
          margin-bottom: 16px;
          flex-wrap: wrap;
          
          .info-item {
            font-size: 14px;
            color: #606266;
            display: flex;
            align-items: center;
            margin-bottom: 4px;
            padding: 4px 0;
            
            .el-icon {
              margin-right: 6px;
              font-size: 16px;
              color: #909399;
            }
          }
        }
        
        .course-rating {
          margin-bottom: 20px;
          display: flex;
          align-items: center;
          flex-wrap: wrap;
          
          .rating-label {
            font-size: 14px;
            color: #606266;
            margin-right: 8px;
          }
          
          .rating-stars {
            .el-rate__icon {
              margin-right: 4px;
              transition: transform 0.2s;
              
              &:hover {
                transform: scale(1.2);
              }
            }
          }
        }
        
        .course-description {
          font-size: 14px;
          color: #606266;
          margin-bottom: 20px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          line-height: 1.6;
          height: 45px;
        }
        
        .course-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          flex-wrap: wrap;
          gap: 12px;
          
          .el-tag {
            font-weight: 500;
            padding: 6px 10px;
            border-radius: 6px;
            font-size: 12px;
            letter-spacing: 0.3px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
          }
          
          .action-buttons {
            display: flex;
            gap: 10px;
            
            @media (max-width: 380px) {
              flex-direction: column;
              width: 100%;
              margin-top: 12px;
              gap: 8px;
            }
            
            .action-btn {
              transition: all 0.3s;
              white-space: nowrap;
              border-radius: 6px;
              padding: 6px 12px;
              
              .el-icon {
                margin-right: 6px;
              }
              
              &:hover {
                transform: translateY(-2px);
                background-color: var(--el-color-primary-light-3);
                color: white;
              }
            }
          }
        }
      }
    }
  }
}
</style> 