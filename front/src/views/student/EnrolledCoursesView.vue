<template>
  <div class="enrolled-courses">
    <!-- 欢迎区域 -->
    <div class="welcome-area">
      <h1 class="welcome-title">你好，同学</h1>
      <p class="welcome-subtitle">继续你的学习之旅，探索更多精彩课程</p>
    </div>

    <!-- 过滤区域 -->
    <div class="filter-section">
      <el-input
        v-model="searchQuery"
        placeholder="搜索课程名称或讲师"
        class="search-input"
        :prefix-icon="Search"
        clearable
      />
      
      <el-select v-model="filter.category" placeholder="所有类别" clearable style="width: 150px">
        <el-option
          v-for="item in categories"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      
      <el-select v-model="filter.sort" style="width: 150px">
        <el-option label="最近活动" value="recentActivity" />
        <el-option label="学习进度" value="progress" />
        <el-option label="课程名称" value="name" />
      </el-select>
      
      <div class="controls">
        <div class="view-mode-toggle">
          <button 
            class="view-mode-btn" 
            :class="{ active: viewMode === 'grid' }"
            @click="viewMode = 'grid'"
          >
            <el-icon><Grid /></el-icon>
          </button>
          <button 
            class="view-mode-btn" 
            :class="{ active: viewMode === 'list' }"
            @click="viewMode = 'list'"
          >
            <el-icon><Menu /></el-icon>
          </button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner">
        <el-icon><Loading /></el-icon>
      </div>
      <div class="loading-text">加载课程中...</div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="filteredCourses.length === 0" class="empty-state">
      <div class="empty-icon">
        <el-icon><Document /></el-icon>
      </div>
      <h3 class="empty-title">没有找到课程</h3>
      <p class="empty-subtitle">尝试修改搜索条件，或者浏览推荐课程</p>
      <el-button type="primary" @click="navigateToCourseCatalog">浏览课程目录</el-button>
    </div>

    <!-- 课程列表 -->
    <div v-else :class="['courses-container', `courses-${viewMode}`]">
      <div 
        v-for="course in filteredCourses" 
        :key="course.id"
        class="course-card"
      >
        <div class="card-left" :style="{ backgroundColor: course.color }">
          <div class="card-icon">
            <el-icon v-if="course.icon === 'DataAnalysis'"><DataAnalysis /></el-icon>
            <el-icon v-else-if="course.icon === 'Reading'"><Reading /></el-icon>
            <el-icon v-else-if="course.icon === 'Monitor'"><Monitor /></el-icon>
            <el-icon v-else-if="course.icon === 'Setting'"><Setting /></el-icon>
            <el-icon v-else-if="course.icon === 'Coffee'"><Coffee /></el-icon>
            <el-icon v-else-if="course.icon === 'Document'"><Document /></el-icon>
          </div>
          <div class="card-progress">
            <el-progress 
              :percentage="course.progress" 
              :stroke-width="8" 
              :color="getProgressColor(course.progress)"
            />
          </div>
        </div>
        <div class="card-content">
          <h3 class="course-title" @click="viewCourseDetail(course.id)">{{ course.title }}</h3>
          <div class="course-instructor">
            <el-icon><User /></el-icon>
            {{ course.instructor }}
          </div>
          <div class="course-description">
            {{ course.description }}
          </div>
          <div class="course-meta">
            <div class="last-activity">
              <el-icon><Clock /></el-icon>
              {{ course.lastActivity }}
            </div>
            <div class="course-category">{{ course.category }}</div>
          </div>
          <div class="card-actions">
            <el-button 
              class="action-button" 
              type="primary" 
              @click="continueStudy(course.id)"
            >
              继续学习
            </el-button>
            <el-button 
              class="action-button" 
              type="info" 
              plain
              @click="viewAssignments(course.id)"
            >
              查看作业
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐课程 -->
    <div class="recommendations">
      <h2 class="recommendations-title">推荐课程</h2>
      <p class="recommendations-subtitle">基于你的学习历史和兴趣，这些课程可能适合你</p>
      
      <div class="recommended-courses">
        <div 
          v-for="course in recommendedCourses" 
          :key="course.id"
          class="recommended-card"
        >
          <div class="recommended-icon" :style="{ backgroundColor: course.color }">
            <el-icon v-if="course.icon === 'DataAnalysis'"><DataAnalysis /></el-icon>
            <el-icon v-else-if="course.icon === 'Reading'"><Reading /></el-icon>
            <el-icon v-else-if="course.icon === 'Monitor'"><Monitor /></el-icon>
          </div>
          <div class="recommended-info">
            <div class="recommended-title">{{ course.title }}</div>
            <div class="recommended-instructor">{{ course.instructor }}</div>
          </div>
          <el-button 
            class="add-course-btn" 
            type="success" 
            circle
            size="small"
            @click="handleAddCourse(course.id)"
          >
            <el-icon><Plus /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  User, 
  Clock, 
  VideoPlay,
  Document,
  TrendCharts,
  Reading,
  DataAnalysis,
  Monitor,
  Setting,
  Coffee,
  Plus,
  Collection,
  Grid,
  Menu,
  Loading,
  Search
} from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(true)
const searchQuery = ref('')
const viewMode = ref('grid')

// 课程分类
const categories = [
  { value: '计算机科学', label: '计算机科学' },
  { value: '数学', label: '数学' },
  { value: '人工智能', label: '人工智能' },
  { value: '工程学', label: '工程学' },
  { value: '商业管理', label: '商业管理' },
  { value: '外语', label: '外语' }
]

// 获取进度条颜色
const getProgressColor = (progress: number) => {
  if (progress < 30) return '#909399'
  if (progress < 70) return '#E6A23C'
  return '#67C23A'
}

// 过滤和排序
const filter = reactive({
  category: '',
  sort: 'recentActivity'
})

// 课程数据
const courses = ref([
  {
    id: 1,
    title: '高级数据结构与算法',
    instructor: '李教授',
    lastActivity: '3小时前',
    progress: 75,
    color: '#409EFF',
    description: '掌握高级数据结构与算法，提升编程效率和代码质量。',
    category: '计算机科学',
    icon: 'DataAnalysis'
  },
  {
    id: 2,
    title: '人工智能基础',
    instructor: '王教授',
    lastActivity: '昨天',
    progress: 45,
    color: '#67C23A',
    description: '学习人工智能基础理论和应用实践，为智能时代做准备。',
    category: '人工智能',
    icon: 'Monitor'
  },
  {
    id: 3,
    title: '高等微积分',
    instructor: '张教授',
    lastActivity: '上周',
    progress: 60,
    color: '#E6A23C',
    description: '深入理解微积分原理及其在工程领域的应用。',
    category: '数学',
    icon: 'Reading'
  },
  {
    id: 4,
    title: '前端工程化实践',
    instructor: '赵教授',
    lastActivity: '刚刚',
    progress: 90,
    color: '#F56C6C',
    description: '学习现代前端工程化实践，掌握最新的开发工具和方法。',
    category: '计算机科学',
    icon: 'Setting'
  },
  {
    id: 5,
    title: '项目管理方法论',
    instructor: '刘教授',
    lastActivity: '3天前',
    progress: 30,
    color: '#9B59B6',
    description: '学习敏捷开发、Scrum等现代项目管理方法和实践。',
    category: '工程学',
    icon: 'Coffee'
  },
  {
    id: 6,
    title: '商业数据分析',
    instructor: '钱教授',
    lastActivity: '2天前',
    progress: 65,
    color: '#1ABC9C',
    description: '掌握数据分析技术，为商业决策提供有力支持。',
    category: '商业管理',
    icon: 'DataAnalysis'
  },
  {
    id: 7,
    title: '系统架构设计',
    instructor: '孙教授',
    lastActivity: '4天前',
    progress: 80,
    color: '#3498DB',
    description: '学习分布式系统架构设计原则和实践方法。',
    category: '计算机科学',
    icon: 'Monitor'
  },
  {
    id: 8,
    title: '高级英语写作',
    instructor: '周教授',
    lastActivity: '1周前',
    progress: 50,
    color: '#E74C3C',
    description: '提升学术和专业英语写作能力，掌握高效表达技巧。',
    category: '外语',
    icon: 'Document'
  }
])

// 推荐课程
const recommendedCourses = ref([
  {
    id: 101,
    title: '机器学习实践',
    instructor: '郑教授',
    color: '#F39C12',
    icon: 'Monitor'
  },
  {
    id: 102,
    title: '区块链技术基础',
    instructor: '吴教授',
    color: '#2ECC71',
    icon: 'Monitor'
  },
  {
    id: 103,
    title: '金融管理学',
    instructor: '陈教授',
    color: '#8E44AD',
    icon: 'Reading'
  },
  {
    id: 104,
    title: '数据库系统原理',
    instructor: '林教授',
    color: '#16A085',
    icon: 'DataAnalysis'
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
      course.instructor.toLowerCase().includes(query) ||
      course.description.toLowerCase().includes(query)
    )
  }
  
  // 根据分类过滤
  if (filter.category) {
    result = result.filter(course => course.category === filter.category)
  }
  
  // 排序
  switch (filter.sort) {
    case 'progress':
      result.sort((a, b) => b.progress - a.progress)
      break
    case 'name':
      result.sort((a, b) => a.title.localeCompare(b.title))
      break
    case 'recentActivity':
    default:
      // 这里假设最近活动通过string来表示，实际应用中可能需要转换为日期进行比较
      const activityOrder = {
        '刚刚': 6,
        '小时前': 5,
        '昨天': 4,
        '天前': 3,
        '上周': 2,
        '周前': 1
      }
      result.sort((a, b) => {
        for (const [key, value] of Object.entries(activityOrder)) {
          if (a.lastActivity.includes(key) && !b.lastActivity.includes(key)) {
            return -1
          }
          if (!a.lastActivity.includes(key) && b.lastActivity.includes(key)) {
            return 1
          }
        }
        return 0
      })
      break
  }
  
  return result
})

// 查看课程详情
const viewCourseDetail = (courseId: number) => {
  router.push(`/courses/${courseId}`)
}

// 继续学习
const continueStudy = (courseId: number) => {
  router.push(`/courses/${courseId}/learn`)
}

// 查看作业
const viewAssignments = (courseId: number) => {
  router.push(`/assignments?courseId=${courseId}`)
}

// 导航到课程目录
const navigateToCourseCatalog = () => {
  router.push('/course-catalog')
}

// 添加推荐课程
const handleAddCourse = (courseId: number) => {
  // 模拟添加课程
  alert(`已添加课程ID: ${courseId}`)
  // 实际应用中需要调用API添加课程
}

// 初始化
onMounted(() => {
  // 模拟加载
  setTimeout(() => {
    loading.value = false
  }, 1000)
})
</script>

<style lang="scss" scoped>
.enrolled-courses {
  padding: 0 20px 40px;
  
  /* 欢迎区域 */
  .welcome-area {
    margin-bottom: 30px;
    padding: 40px 0 20px;
    border-bottom: 1px solid #ebeef5;
    
    .welcome-title {
      font-size: 28px;
      font-weight: 600;
      margin-bottom: 10px;
      background: linear-gradient(45deg, #409EFF, #67C23A);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      display: inline-block;
    }
    
    .welcome-subtitle {
      color: #606266;
      font-size: 16px;
      margin-bottom: 20px;
    }
  }
  
  /* 过滤区域 */
  .filter-section {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    margin-bottom: 20px;
    gap: 15px;
    
    .search-input {
      width: 260px;
    }
    
    .controls {
      display: flex;
      gap: 10px;
      margin-left: auto;
    }
  }
  
  .view-mode-toggle {
    margin-left: 10px;
    display: flex;
    align-items: center;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;
    
    .view-mode-btn {
      padding: 6px 10px;
      background: white;
      border: none;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s;
      
      &.active {
        background: #ecf5ff;
        color: #409EFF;
      }
    }
  }
  
  /* 加载状态 */
  .loading-state {
    text-align: center;
    padding: 60px 0;
    
    .loading-spinner {
      font-size: 40px;
      color: #409EFF;
      margin-bottom: 20px;
      animation: spin 1.5s linear infinite;
    }
    
    .loading-text {
      font-size: 18px;
      color: #606266;
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  }
  
  /* 空状态 */
  .empty-state {
    text-align: center;
    padding: 60px 20px;
    background: #f9f9f9;
    border-radius: 8px;
    
    .empty-icon {
      font-size: 60px;
      color: #909399;
      margin-bottom: 20px;
      opacity: 0.5;
    }
    
    .empty-title {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 10px;
    }
    
    .empty-subtitle {
      color: #606266;
      margin-bottom: 25px;
    }
  }
  
  /* 课程容器 */
  .courses-container {
    margin-bottom: 40px;
  }
  
  /* 网格视图 */
  .courses-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(290px, 1fr));
    gap: 20px;
  }
  
  /* 列表视图 */
  .courses-list {
    .course-card {
      display: flex;
      margin-bottom: 15px;
      border-radius: 8px;
      
      .card-left {
        width: 120px;
        flex-shrink: 0;
      }
      
      .card-content {
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        padding: 15px;
        justify-content: space-between;
      }
      
      .card-icon {
        font-size: 30px;
        margin-bottom: 5px;
      }
      
      .course-description {
        -webkit-line-clamp: 1;
      }
      
      .card-actions {
        margin-top: 5px;
      }
    }
  }
  
  /* 课程卡片 */
  .course-card {
    position: relative;
    border-radius: 8px;
    background-color: white;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    transition: transform 0.3s, box-shadow 0.3s;
    
    &:hover {
      transform: translateY(-6px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }
    
    .card-left {
      padding: 25px 20px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: white;
      
      .card-icon {
        font-size: 40px;
        margin-bottom: 15px;
      }
      
      .card-progress {
        width: 100%;
        margin-top: 10px;
      }
    }
    
    .card-content {
      background: white;
      padding: 20px;
      
      .course-title {
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 8px;
        color: #303133;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        height: 52px;
        cursor: pointer;
      }
      
      .course-instructor {
        display: flex;
        align-items: center;
        color: #606266;
        margin-bottom: 12px;
        font-size: 14px;
        
        .el-icon {
          margin-right: 5px;
        }
      }
      
      .course-description {
        color: #606266;
        margin-bottom: 15px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        font-size: 14px;
        height: 42px;
      }
      
      .course-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        color: #909399;
        font-size: 14px;
        
        .last-activity {
          display: flex;
          align-items: center;
          
          .el-icon {
            margin-right: 5px;
          }
        }
      }
      
      .card-actions {
        display: flex;
        gap: 10px;
        
        .action-button {
          flex: 1;
        }
      }
    }
  }
  
  /* 推荐课程 */
  .recommendations {
    margin-top: 40px;
    padding-top: 30px;
    border-top: 1px solid #ebeef5;
    
    .recommendations-title {
      font-size: 22px;
      font-weight: 600;
      margin-bottom: 20px;
      color: #303133;
    }
    
    .recommendations-subtitle {
      color: #606266;
      margin-bottom: 20px;
    }
    
    .recommended-courses {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 20px;
      
      .recommended-card {
        border-radius: 8px;
        padding: 20px;
        background: white;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        display: flex;
        align-items: center;
        transition: all 0.3s;
        overflow: hidden;
        position: relative;
        
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
          
          .add-course-btn {
            opacity: 1;
          }
        }
        
        .recommended-icon {
          width: 40px;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 50%;
          color: white;
          margin-right: 15px;
          flex-shrink: 0;
        }
        
        .recommended-info {
          flex-grow: 1;
          
          .recommended-title {
            font-weight: 600;
            margin-bottom: 5px;
            color: #303133;
          }
          
          .recommended-instructor {
            font-size: 14px;
            color: #606266;
          }
        }
        
        .add-course-btn {
          position: absolute;
          right: 20px;
          top: 50%;
          transform: translateY(-50%);
          opacity: 0;
          transition: opacity 0.3s;
        }
      }
    }
  }
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .enrolled-courses {
    .courses-grid {
      grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    }
  }
}

@media (max-width: 768px) {
  .enrolled-courses {
    .filter-section {
      flex-direction: column;
      align-items: flex-start;
      
      .search-input {
        width: 100%;
      }
      
      .controls {
        width: 100%;
        margin-left: 0;
        justify-content: space-between;
      }
    }
    
    .welcome-title {
      font-size: 24px;
    }
    
    .courses-grid {
      grid-template-columns: repeat(auto-fill, minmax(100%, 1fr));
    }
    
    .recommendations {
      .recommended-courses {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style> 