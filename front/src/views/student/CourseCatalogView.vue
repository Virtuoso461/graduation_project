<template>
  <div class="course-catalog">
    <div class="catalog-header">
      <h2 class="page-title">课程目录</h2>
      <div class="catalog-filters">
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
        <el-select v-model="filter.level" placeholder="难度级别" clearable>
          <el-option label="入门" value="beginner" />
          <el-option label="中级" value="intermediate" />
          <el-option label="高级" value="advanced" />
        </el-select>
        <el-select v-model="filter.sort" placeholder="排序方式">
          <el-option label="最新" value="newest" />
          <el-option label="热门" value="popular" />
          <el-option label="评分" value="rating" />
        </el-select>
      </div>
    </div>

    <div v-if="loading" class="courses-loading">
      <el-skeleton :rows="3" animated />
      <el-skeleton :rows="3" animated style="margin-top: 20px" />
      <el-skeleton :rows="3" animated style="margin-top: 20px" />
    </div>
    
    <div v-else>
      <div class="catalog-categories">
        <el-tabs v-model="activeCategory" type="card">
          <el-tab-pane label="全部课程" name="all"></el-tab-pane>
          <el-tab-pane v-for="cat in featuredCategories" :key="cat.value" :label="cat.label" :name="cat.value"></el-tab-pane>
        </el-tabs>
      </div>
      
      <div v-if="filteredCourses.length === 0" class="empty-state">
        <el-empty description="没有找到符合条件的课程">
          <template #description>
            <p>尝试更改您的筛选条件</p>
          </template>
          <el-button @click="resetFilters">重置筛选</el-button>
        </el-empty>
      </div>
      
      <div v-else class="course-list">
        <el-card 
          v-for="course in filteredCourses" 
          :key="course.id"
          class="course-card"
          shadow="hover"
        >
          <div class="course-head">
            <div class="course-cover" :style="{ backgroundColor: course.color }">
              <el-icon class="course-icon"><component :is="course.icon" /></el-icon>
              <div class="course-labels">
                <el-tag size="small" effect="dark" :style="{ backgroundColor: course.color }">{{ course.category }}</el-tag>
                <el-tag size="small" class="level-tag" :type="getLevelType(course.level)">{{ course.level }}</el-tag>
              </div>
            </div>
            <div class="course-meta">
              <h3 class="course-title">{{ course.title }}</h3>
              <div class="instructor-info">
                <el-avatar :size="24" :src="course.instructorAvatar">{{ course.instructor.substring(0, 1) }}</el-avatar>
                <span class="instructor-name">{{ course.instructor }}</span>
              </div>
              <div class="course-stats">
                <span class="stat-item">
                  <el-icon><User /></el-icon>
                  {{ course.enrolledCount }} 人已选
                </span>
                <span class="stat-item">
                  <el-icon><Clock /></el-icon>
                  {{ course.duration }}
                </span>
                <span class="stat-item">
                  <el-rate v-model="course.rating" disabled text-color="#ff9900" :show-score="true" score-template="{value}" :colors="ratingColors" />
                </span>
              </div>
            </div>
          </div>
          
          <p class="course-description">{{ course.description }}</p>
          
          <div class="course-content">
            <h4 class="content-title">
              <el-icon><List /></el-icon>
              课程内容
            </h4>
            <div class="content-topics">
              <div class="topic-item" v-for="(topic, index) in course.topics.slice(0, 3)" :key="index">
                <el-icon><Check /></el-icon>
                <span>{{ topic }}</span>
              </div>
              <div class="topic-more" v-if="course.topics.length > 3">
                +{{ course.topics.length - 3 }} 个主题
              </div>
            </div>
          </div>
          
          <div class="course-footer">
            <div class="price-info">
              <template v-if="course.isFree">
                <span class="price free">免费</span>
              </template>
              <template v-else-if="course.discountPrice">
                <span class="price discounted">￥{{ course.discountPrice }}</span>
                <span class="original-price">￥{{ course.price }}</span>
              </template>
              <template v-else>
                <span class="price">￥{{ course.price }}</span>
              </template>
            </div>
            <div class="action-buttons">
              <el-button type="primary" @click="enrollCourse(course.id)">选课</el-button>
              <el-button plain @click="viewCourseDetail(course.id)">查看详情</el-button>
              <el-button circle @click="toggleFavorite(course)" :type="course.isFavorite ? 'danger' : ''">
                <el-icon><Star v-if="course.isFavorite" /><StarFilled v-else /></el-icon>
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[8, 16, 24, 32]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalCourses"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  User, 
  Clock, 
  Star,
  StarFilled,
  List,
  Check,
  Reading,
  DataAnalysis,
  Monitor,
  Setting,
  Coffee,
  Histogram,
  PieChart,
  Compass,
  Sunset,
  Ship
} from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(true)
const searchQuery = ref('')
const activeCategory = ref('all')
const currentPage = ref(1)
const pageSize = ref(8)
const totalCourses = ref(100)

// 课程分类
const categories = [
  { value: '计算机科学', label: '计算机科学' },
  { value: '数学', label: '数学' },
  { value: '人工智能', label: '人工智能' },
  { value: '工程学', label: '工程学' },
  { value: '商业管理', label: '商业管理' },
  { value: '艺术设计', label: '艺术设计' },
  { value: '语言学习', label: '语言学习' }
]

const featuredCategories = categories.slice(0, 5)

// 评分颜色
const ratingColors = ['#99A9BF', '#F7BA2A', '#FF9900']

// 过滤和排序
const filter = reactive({
  category: '',
  level: '',
  sort: 'popular'
})

// 观察分类标签页变化
watch(activeCategory, (newCategory) => {
  if (newCategory === 'all') {
    filter.category = ''
  } else {
    filter.category = newCategory
  }
  currentPage.value = 1
})

// 根据难度级别获取标签类型
const getLevelType = (level: string) => {
  switch (level) {
    case '入门':
      return 'success'
    case '中级':
      return 'warning'
    case '高级':
      return 'danger'
    default:
      return 'info'
  }
}

// 课程数据
const courses = ref([
  {
    id: 1,
    title: '计算机网络原理与应用',
    instructor: '张教授',
    instructorAvatar: '',
    rating: 4.8,
    enrolledCount: 2345,
    color: '#409EFF',
    description: '从网络协议到实际应用，全面掌握计算机网络知识，理解网络架构、TCP/IP协议栈以及网络安全等关键概念。',
    category: '计算机科学',
    level: '中级',
    duration: '16周',
    price: 299,
    discountPrice: 199,
    isFree: false,
    isFavorite: true,
    icon: 'Monitor',
    topics: ['网络协议', 'TCP/IP', '网络安全', '路由与交换', '网络架构', '网络编程']
  },
  {
    id: 2,
    title: '深度学习与神经网络',
    instructor: '李教授',
    instructorAvatar: '',
    rating: 4.9,
    enrolledCount: 1876,
    color: '#67C23A',
    description: '深入学习神经网络算法、模型训练与优化技术，掌握深度学习框架，解决复杂的人工智能问题。',
    category: '人工智能',
    level: '高级',
    duration: '12周',
    price: 499,
    discountPrice: 399,
    isFree: false,
    isFavorite: false,
    icon: 'DataAnalysis',
    topics: ['神经网络基础', '卷积神经网络', '循环神经网络', '模型优化', 'TensorFlow', 'PyTorch']
  },
  {
    id: 3,
    title: '高等微积分',
    instructor: '王教授',
    instructorAvatar: '',
    rating: 4.6,
    enrolledCount: 1203,
    color: '#E6A23C',
    description: '系统学习微积分理论与应用，掌握极限、微分、积分及微分方程等高等数学核心内容。',
    category: '数学',
    level: '中级',
    duration: '16周',
    price: 299,
    discountPrice: 0,
    isFree: false,
    isFavorite: false,
    icon: 'Reading',
    topics: ['极限与连续', '导数与微分', '积分理论', '微分方程', '多元微积分', '级数']
  },
  {
    id: 4,
    title: '软件工程与项目管理',
    instructor: '赵教授',
    instructorAvatar: '',
    rating: 4.7,
    enrolledCount: 1560,
    color: '#F56C6C',
    description: '学习软件开发全生命周期管理，掌握现代敏捷开发方法、团队协作与项目管理技巧。',
    category: '工程学',
    level: '中级',
    duration: '10周',
    price: 399,
    discountPrice: 299,
    isFree: false,
    isFavorite: true,
    icon: 'Setting',
    topics: ['软件开发流程', '需求分析', '设计模式', '敏捷开发', '测试与质量保证', '项目管理']
  },
  {
    id: 5,
    title: '市场营销策略',
    instructor: '刘教授',
    instructorAvatar: '',
    rating: 4.5,
    enrolledCount: 890,
    color: '#9B59B6',
    description: '掌握现代市场营销理念与实践，学习市场分析、品牌建设、数字营销等核心策略。',
    category: '商业管理',
    level: '入门',
    duration: '8周',
    price: 199,
    discountPrice: 0,
    isFree: true,
    isFavorite: false,
    icon: 'PieChart',
    topics: ['市场分析', '消费者行为', '品牌建设', '数字营销', '社交媒体策略', '营销计划制定']
  },
  {
    id: 6,
    title: '数据结构基础',
    instructor: '陈教授',
    instructorAvatar: '',
    rating: 4.8,
    enrolledCount: 2105,
    color: '#3498DB',
    description: '从零开始学习数据结构，掌握数组、链表、栈、队列、树、图等核心数据结构及其应用。',
    category: '计算机科学',
    level: '入门',
    duration: '12周',
    price: 199,
    discountPrice: 99,
    isFree: false,
    isFavorite: false,
    icon: 'Histogram',
    topics: ['数组与字符串', '链表', '栈与队列', '树与二叉树', '图论基础', '哈希表']
  },
  {
    id: 7,
    title: '前端开发进阶',
    instructor: '杨教授',
    instructorAvatar: '',
    rating: 4.7,
    enrolledCount: 1725,
    color: '#1ABC9C',
    description: '全面提升前端开发技能，深入学习现代JavaScript、框架开发、性能优化等高级前端技术。',
    category: '计算机科学',
    level: '中级',
    duration: '14周',
    price: 399,
    discountPrice: 0,
    isFree: false,
    isFavorite: false,
    icon: 'Monitor',
    topics: ['现代JavaScript', 'React生态', 'Vue开发', '状态管理', '性能优化', 'SSR与JAMStack']
  },
  {
    id: 8,
    title: '商业数据分析',
    instructor: '郭教授',
    instructorAvatar: '',
    rating: 4.6,
    enrolledCount: 1320,
    color: '#E74C3C',
    description: '学习商业数据分析方法与工具，掌握数据可视化、预测模型、商业智能等实用技能。',
    category: '商业管理',
    level: '中级',
    duration: '10周',
    price: 299,
    discountPrice: 0,
    isFree: false,
    isFavorite: true,
    icon: 'DataAnalysis',
    topics: ['数据收集与清洗', '统计分析', '数据可视化', '预测模型', '商业智能', '报告撰写']
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
  
  // 根据难度级别过滤
  if (filter.level) {
    let targetLevel: string
    switch (filter.level) {
      case 'beginner':
        targetLevel = '入门'
        break
      case 'intermediate':
        targetLevel = '中级'
        break
      case 'advanced':
        targetLevel = '高级'
        break
      default:
        targetLevel = ''
    }
    result = result.filter(course => course.level === targetLevel)
  }
  
  // 排序
  switch (filter.sort) {
    case 'newest':
      // 这里假设id越大越新
      result.sort((a, b) => b.id - a.id)
      break
    case 'rating':
      result.sort((a, b) => b.rating - a.rating)
      break
    case 'popular':
    default:
      result.sort((a, b) => b.enrolledCount - a.enrolledCount)
      break
  }
  
  // 更新总课程数量
  totalCourses.value = result.length
  
  // 分页
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return result.slice(startIndex, endIndex)
})

// 重置筛选条件
const resetFilters = () => {
  searchQuery.value = ''
  filter.category = ''
  filter.level = ''
  filter.sort = 'popular'
  activeCategory.value = 'all'
  currentPage.value = 1
}

// 选择课程
const enrollCourse = (courseId: number) => {
  // 模拟选课功能
  console.log(`选择课程 ${courseId}`)
  router.push(`/courses/enrolled`)
}

// 查看课程详情
const viewCourseDetail = (courseId: number) => {
  router.push(`/courses/${courseId}`)
}

// 收藏/取消收藏课程
const toggleFavorite = (course: any) => {
  course.isFavorite = !course.isFavorite
}

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

// 处理页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
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
.course-catalog {
  .catalog-header {
    margin-bottom: 24px;
    
    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 16px;
    }
    
    .catalog-filters {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
      
      .search-input {
        width: 240px;
      }
      
      .el-select {
        min-width: 140px;
      }
    }
  }
  
  .catalog-categories {
    margin-bottom: 24px;
    
    :deep(.el-tabs__nav) {
      border-radius: 8px;
    }
    
    :deep(.el-tabs__item) {
      padding: 0 20px;
      height: 40px;
      line-height: 40px;
      transition: all 0.3s;
      
      &.is-active {
        font-weight: 600;
      }
    }
  }
  
  .courses-loading {
    padding: 20px;
    background-color: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  }
  
  .empty-state {
    padding: 48px 0;
    background-color: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    text-align: center;
    
    p {
      color: #909399;
      margin-bottom: 16px;
    }
  }
  
  .course-list {
    display: grid;
    grid-template-columns: 1fr;
    gap: 24px;
    
    .course-card {
      border: none;
      overflow: hidden;
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
      }
      
      .course-head {
        display: flex;
        gap: 20px;
        margin-bottom: 16px;
        
        @media (max-width: 768px) {
          flex-direction: column;
          gap: 16px;
        }
        
        .course-cover {
          min-width: 200px;
          height: 120px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          position: relative;
          overflow: hidden;
          
          @media (max-width: 768px) {
            min-width: 100%;
            height: 160px;
          }
          
          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: radial-gradient(circle at center, rgba(255,255,255,0.2) 0%, rgba(0,0,0,0.1) 100%);
          }
          
          .course-icon {
            font-size: 60px;
            color: rgba(255, 255, 255, 0.9);
            filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.1));
            z-index: 1;
          }
          
          .course-labels {
            position: absolute;
            top: 8px;
            left: 8px;
            display: flex;
            gap: 6px;
            z-index: 2;
            
            .level-tag {
              font-weight: 500;
            }
          }
        }
        
        .course-meta {
          flex: 1;
          
          .course-title {
            font-size: 20px;
            font-weight: 600;
            margin-bottom: 12px;
            color: #303133;
            line-height: 1.4;
          }
          
          .instructor-info {
            display: flex;
            align-items: center;
            margin-bottom: 12px;
            
            .instructor-name {
              margin-left: 8px;
              font-size: 14px;
              color: #606266;
            }
          }
          
          .course-stats {
            display: flex;
            flex-wrap: wrap;
            gap: 16px;
            
            .stat-item {
              font-size: 14px;
              color: #606266;
              display: flex;
              align-items: center;
              
              .el-icon {
                margin-right: 4px;
                font-size: 16px;
              }
            }
          }
        }
      }
      
      .course-description {
        font-size: 14px;
        color: #606266;
        margin-bottom: 20px;
        line-height: 1.6;
      }
      
      .course-content {
        background-color: #f9f9f9;
        border-radius: 8px;
        padding: 16px;
        margin-bottom: 20px;
        
        .content-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 12px;
          display: flex;
          align-items: center;
          
          .el-icon {
            margin-right: 6px;
          }
        }
        
        .content-topics {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
          gap: 8px;
          
          .topic-item {
            display: flex;
            align-items: center;
            font-size: 14px;
            color: #606266;
            
            .el-icon {
              color: #67C23A;
              margin-right: 6px;
            }
          }
          
          .topic-more {
            font-size: 14px;
            color: #909399;
            margin-top: 8px;
            grid-column: 1 / -1;
          }
        }
      }
      
      .course-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        gap: 16px;
        
        .price-info {
          .price {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
            
            &.free {
              color: #67C23A;
            }
            
            &.discounted {
              color: #F56C6C;
            }
          }
          
          .original-price {
            margin-left: 8px;
            font-size: 16px;
            color: #909399;
            text-decoration: line-through;
          }
        }
        
        .action-buttons {
          display: flex;
          gap: 12px;
          
          @media (max-width: 576px) {
            width: 100%;
            justify-content: space-between;
          }
        }
      }
    }
  }
  
  .pagination-container {
    margin-top: 32px;
    display: flex;
    justify-content: center;
  }
}
</style> 