<template>
  <app-layout>
    <div class="course-detail">
      <div class="course-header">
        <div class="course-info">
          <h1 class="course-title">{{ course.title }}</h1>
          <div class="course-meta">
            <span class="meta-item">
              <el-icon><User /></el-icon>
              {{ course.instructor }}
            </span>
            <span class="meta-item">
              <el-icon><Calendar /></el-icon>
              {{ course.schedule }}
            </span>
            <span class="meta-item">
              <el-icon><Timer /></el-icon>
              {{ course.duration }}
            </span>
            <span class="meta-item">
              <el-icon><Collection /></el-icon>
              {{ course.category }}
            </span>
          </div>
          <div class="course-progress">
            <el-progress 
              :percentage="course.progress" 
              :color="getProgressColor(course.progress)"
            />
            <span class="progress-text">完成进度：{{ course.progress }}%</span>
          </div>
        </div>
        <div class="course-actions">
          <el-button type="primary" size="large">继续学习</el-button>
          <el-button size="large">课程大纲</el-button>
        </div>
      </div>
      
      <div class="course-content">
        <el-tabs v-model="activeTab" class="content-tabs">
          <el-tab-pane label="课程介绍" name="intro">
            <div class="course-description">
              <h2>课程简介</h2>
              <p>{{ course.description }}</p>
              
              <h2>课程目标</h2>
              <ul>
                <li v-for="(goal, index) in course.objectives" :key="index">
                  {{ goal }}
                </li>
              </ul>
              
              <h2>课程大纲</h2>
              <div class="course-outline">
                <div 
                  v-for="(chapter, index) in course.outline" 
                  :key="index"
                  class="chapter-item"
                >
                  <div class="chapter-header">
                    <span class="chapter-number">第{{ index + 1 }}章</span>
                    <span class="chapter-title">{{ chapter.title }}</span>
                    <span class="chapter-duration">{{ chapter.duration }}</span>
                  </div>
                  <div class="chapter-content">
                    <div 
                      v-for="(section, sIndex) in chapter.sections" 
                      :key="sIndex"
                      class="section-item"
                    >
                      <span class="section-title">{{ section.title }}</span>
                      <span class="section-duration">{{ section.duration }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="学习记录" name="record">
            <div class="learning-record">
              <div class="record-stats">
                <div class="stat-item">
                  <div class="stat-value">{{ course.studyTime }}</div>
                  <div class="stat-label">学习时长</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ course.completedChapters }}/{{ course.totalChapters }}</div>
                  <div class="stat-label">完成章节</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ course.quizScore }}</div>
                  <div class="stat-label">测验得分</div>
                </div>
              </div>
              
              <div class="record-chart">
                <h3>学习进度趋势</h3>
                <!-- 这里可以添加学习进度图表 -->
              </div>
              
              <div class="record-list">
                <h3>最近学习记录</h3>
                <el-timeline>
                  <el-timeline-item
                    v-for="(record, index) in course.recentRecords"
                    :key="index"
                    :timestamp="record.time"
                    :type="record.type"
                  >
                    {{ record.content }}
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="课程资源" name="resources">
            <div class="course-resources">
              <div class="resource-categories">
                <el-radio-group v-model="resourceType" size="large">
                  <el-radio-button label="all">全部资源</el-radio-button>
                  <el-radio-button label="document">文档资料</el-radio-button>
                  <el-radio-button label="video">视频资源</el-radio-button>
                  <el-radio-button label="exercise">练习题</el-radio-button>
                </el-radio-group>
              </div>
              
              <div class="resource-list">
                <div 
                  v-for="(resource, index) in course.resources" 
                  :key="index"
                  class="resource-item"
                >
                  <div class="resource-icon">
                    <el-icon :class="getResourceIcon(resource.type)"></el-icon>
                  </div>
                  <div class="resource-info">
                    <h4 class="resource-title">{{ resource.title }}</h4>
                    <p class="resource-desc">{{ resource.description }}</p>
                    <div class="resource-meta">
                      <span>{{ resource.size }}</span>
                      <span>{{ resource.downloads }}次下载</span>
                    </div>
                  </div>
                  <div class="resource-actions">
                    <el-button type="primary" size="small">下载</el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </app-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import { 
  User,
  Calendar,
  Timer,
  Collection,
  Document,
  VideoPlay,
  Notebook
} from '@element-plus/icons-vue'

// 当前激活的标签页
const activeTab = ref('intro')

// 资源类型
const resourceType = ref('all')

// 课程数据
const course = ref({
  id: 1,
  title: '数据结构与算法分析',
  instructor: '张教授',
  schedule: '每周二、四 14:00-16:00',
  duration: '12周',
  category: '计算机科学',
  progress: 65,
  description: '本课程介绍基本的数据结构和算法，包括数组、链表、栈、队列、树、图等数据结构，以及排序、搜索等算法。通过本课程的学习，学生将掌握常见数据结构的实现原理和基本操作，理解各种算法的设计思想和性能特点。',
  objectives: [
    '掌握基本数据结构的实现原理和操作方法',
    '理解常见算法的设计思想和性能分析',
    '培养算法思维和问题解决能力',
    '提高编程实践能力'
  ],
  outline: [
    {
      title: '绪论',
      duration: '2课时',
      sections: [
        { title: '课程介绍', duration: '30分钟' },
        { title: '数据结构概述', duration: '45分钟' },
        { title: '算法分析基础', duration: '45分钟' }
      ]
    },
    {
      title: '线性表',
      duration: '4课时',
      sections: [
        { title: '顺序表', duration: '45分钟' },
        { title: '链表', duration: '45分钟' },
        { title: '栈和队列', duration: '90分钟' }
      ]
    }
  ],
  studyTime: '24小时',
  completedChapters: 6,
  totalChapters: 12,
  quizScore: 92,
  recentRecords: [
    {
      content: '完成了第3章测验',
      time: '2024-03-18 15:30',
      type: 'success'
    },
    {
      content: '学习了第4章第2节',
      time: '2024-03-17 16:45',
      type: 'primary'
    },
    {
      content: '提交了第3章作业',
      time: '2024-03-16 14:20',
      type: 'warning'
    }
  ],
  resources: [
    {
      type: 'document',
      title: '课程讲义',
      description: '第一章至第三章的详细讲义',
      size: '2.5MB',
      downloads: 128
    },
    {
      type: 'video',
      title: '算法演示视频',
      description: '常见算法的可视化演示',
      size: '156MB',
      downloads: 89
    },
    {
      type: 'exercise',
      title: '练习题集',
      description: '每章配套练习题',
      size: '1.2MB',
      downloads: 256
    }
  ]
})

// 获取进度条颜色
const getProgressColor = (progress: number) => {
  if (progress < 30) return '#909399'
  if (progress < 70) return '#E6A23C'
  return '#67C23A'
}

// 获取资源类型对应的图标
const getResourceIcon = (type: string) => {
  switch(type) {
    case 'document': return 'Document'
    case 'video': return 'VideoPlay'
    case 'exercise': return 'Notebook'
    default: return 'Document'
  }
}
</script>

<style lang="scss" scoped>
// 导入响应式样式工具
@use '@/assets/styles/responsive' as responsive;

.course-detail {
  padding-bottom: 24px;
  min-height: 100vh;
  width: 100%;
  
  @include responsive.respond-to('lg') {
    gap: 20px;
  }
  
  @include responsive.respond-to('md') {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}

.course-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  
  @include responsive.respond-to('lg') {
    gap: 20px;
  }
  
  @include responsive.respond-to('md') {
    flex-direction: column;
    gap: 16px;
  }
  
  .course-info {
    flex: 1;
    
    .course-title {
      font-size: 1.75rem;
      font-weight: 600;
      margin: 0 0 16px;
      color: #303133;
      
      @include responsive.respond-to('lg') {
        font-size: 1.5rem;
        margin: 0 0 12px;
      }
      
      @include responsive.respond-to('md') {
        font-size: 1.35rem;
      }
      
      @include responsive.respond-to('sm') {
        font-size: 1.25rem;
      }
    }
    
    .course-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      margin-bottom: 16px;
      
      @include responsive.respond-to('md') {
        gap: 12px;
        margin-bottom: 12px;
      }
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        color: #606266;
        
        @include responsive.respond-to('md') {
          font-size: 0.95rem;
        }
        
        @include responsive.respond-to('sm') {
          font-size: 0.9rem;
        }
        
        .el-icon {
          font-size: 1.1rem;
        }
      }
    }
    
    .course-progress {
      display: flex;
      align-items: center;
      gap: 16px;
      
      @include responsive.respond-to('md') {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
      }
      
      :deep(.el-progress-bar) {
        width: 200px;
        
        @include responsive.respond-to('lg') {
          width: 100%;
        }
      }
      
      .progress-text {
        color: #606266;
        font-size: 0.9rem;
      }
    }
  }
  
  .course-actions {
    display: flex;
    gap: 12px;
    
    @include responsive.respond-to('md') {
      width: 100%;
      
      .el-button {
        flex: 1;
      }
    }
    
    @include responsive.respond-to('sm') {
      flex-direction: column;
      gap: 8px;
    }
  }
}

.course-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 24px;
  
  @include responsive.respond-to('md') {
    padding: 16px;
  }
  
  .content-tabs {
    :deep(.el-tabs__nav) {
      border-radius: 4px;
      overflow: hidden;
    }
    
    @include responsive.respond-to('sm') {
      :deep(.el-tabs__header) {
        margin-bottom: 16px;
      }
      
      :deep(.el-tabs__nav) {
        width: 100%;
        display: flex;
        
        .el-tabs__item {
          flex: 1;
          text-align: center;
          padding: 0 5px;
          font-size: 14px;
        }
      }
    }
  }
}

.course-description {
  h2 {
    font-size: 1.25rem;
    font-weight: 600;
    margin: 24px 0 16px;
    color: #303133;
    
    @include responsive.respond-to('md') {
      font-size: 1.15rem;
      margin: 20px 0 12px;
    }
    
    &:first-child {
      margin-top: 0;
    }
  }
  
  p {
    font-size: 0.95rem;
    line-height: 1.6;
    color: #606266;
    margin: 0 0 16px;
    
    @include responsive.respond-to('md') {
      font-size: 0.9rem;
      margin: 0 0 12px;
    }
  }
  
  ul {
    margin: 0 0 16px;
    padding-left: 20px;
    
    @include responsive.respond-to('md') {
      margin: 0 0 12px;
    }
    
    li {
      font-size: 0.95rem;
      line-height: 1.6;
      color: #606266;
      margin-bottom: 8px;
      
      @include responsive.respond-to('md') {
        font-size: 0.9rem;
        margin-bottom: 6px;
      }
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

.course-outline {
  .chapter-item {
    margin-bottom: 24px;
    
    @include responsive.respond-to('md') {
      margin-bottom: 20px;
    }
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .chapter-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;
      
      @include responsive.respond-to('md') {
        gap: 8px;
        margin-bottom: 8px;
      }
      
      @include responsive.respond-to('sm') {
        flex-wrap: wrap;
      }
      
      .chapter-number {
        font-weight: 600;
        color: #409EFF;
      }
      
      .chapter-title {
        font-weight: 500;
        color: #303133;
        flex: 1;
      }
      
      .chapter-duration {
        color: #909399;
        font-size: 0.9rem;
        white-space: nowrap;
        
        @include responsive.respond-to('sm') {
          margin-top: 4px;
          width: 100%;
        }
      }
    }
    
    .chapter-content {
      padding-left: 24px;
      
      @include responsive.respond-to('md') {
        padding-left: 16px;
      }
      
      .section-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 0;
        
        @include responsive.respond-to('sm') {
          flex-direction: column;
          align-items: flex-start;
          gap: 4px;
          padding: 10px 0;
        }
        
        .section-title {
          color: #606266;
          
          @include responsive.respond-to('md') {
            font-size: 0.95rem;
          }
        }
        
        .section-duration {
          color: #909399;
          font-size: 0.9rem;
          
          @include responsive.respond-to('md') {
            font-size: 0.85rem;
          }
        }
      }
    }
  }
}

.learning-record {
  .record-stats {
    display: flex;
    gap: 24px;
    margin-bottom: 32px;
    
    @include responsive.respond-to('lg') {
      gap: 16px;
    }
    
    @include responsive.respond-to('md') {
      margin-bottom: 24px;
    }
    
    @include responsive.respond-to('sm') {
      flex-direction: column;
      gap: 16px;
      margin-bottom: 24px;
    }
    
    .stat-item {
      text-align: center;
      
      @include responsive.respond-to('sm') {
        display: flex;
        align-items: center;
        justify-content: space-between;
        text-align: left;
        border-bottom: 1px solid #ebeef5;
        padding-bottom: 16px;
      }
      
      .stat-value {
        font-size: 1.5rem;
        font-weight: 600;
        color: #303133;
        margin-bottom: 4px;
        
        @include responsive.respond-to('md') {
          font-size: 1.35rem;
        }
        
        @include responsive.respond-to('sm') {
          font-size: 1.25rem;
          margin-bottom: 0;
        }
      }
      
      .stat-label {
        font-size: 0.9rem;
        color: #909399;
        
        @include responsive.respond-to('sm') {
          font-size: 0.95rem;
        }
      }
    }
  }
  
  .record-chart {
    margin-bottom: 32px;
    
    @include responsive.respond-to('md') {
      margin-bottom: 24px;
    }
    
    h3 {
      font-size: 1.1rem;
      font-weight: 600;
      margin: 0 0 16px;
      color: #303133;
      
      @include responsive.respond-to('md') {
        font-size: 1rem;
        margin: 0 0 12px;
      }
    }
  }
  
  .record-list {
    h3 {
      font-size: 1.1rem;
      font-weight: 600;
      margin: 0 0 16px;
      color: #303133;
      
      @include responsive.respond-to('md') {
        font-size: 1rem;
        margin: 0 0 12px;
      }
    }
    
    :deep(.el-timeline-item__content) {
      @include responsive.respond-to('md') {
        font-size: 0.95rem;
      }
      
      @include responsive.respond-to('sm') {
        font-size: 0.9rem;
      }
    }
    
    :deep(.el-timeline-item__timestamp) {
      @include responsive.respond-to('sm') {
        font-size: 0.8rem;
      }
    }
  }
}

.course-resources {
  .resource-categories {
    margin-bottom: 24px;
    
    @include responsive.respond-to('md') {
      margin-bottom: 20px;
    }
    
    @include responsive.respond-to('sm') {
      :deep(.el-radio-group) {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        
        .el-radio-button {
          margin-bottom: 8px;
          
          &:first-child .el-radio-button__inner {
            border-radius: 4px 0 0 4px;
          }
          
          &:last-child .el-radio-button__inner {
            border-radius: 0 4px 4px 0;
          }
          
          .el-radio-button__inner {
            padding: 8px 12px;
          }
        }
      }
    }
  }
  
  .resource-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    
    @include responsive.respond-to('md') {
      gap: 12px;
    }
    
    .resource-item {
      display: flex;
      gap: 16px;
      padding: 16px;
      background-color: #f5f7fa;
      border-radius: 8px;
      
      @include responsive.respond-to('md') {
        padding: 12px;
        gap: 12px;
      }
      
      @include responsive.respond-to('sm') {
        flex-wrap: wrap;
      }
      
      .resource-icon {
        font-size: 24px;
        color: #409EFF;
        display: flex;
        align-items: center;
        justify-content: center;
        
        @include responsive.respond-to('md') {
          font-size: 20px;
        }
      }
      
      .resource-info {
        flex: 1;
        min-width: 0; // 防止文本溢出
        
        .resource-title {
          font-size: 1rem;
          font-weight: 500;
          margin: 0 0 8px;
          color: #303133;
          
          @include responsive.respond-to('md') {
            font-size: 0.95rem;
          }
        }
        
        .resource-desc {
          font-size: 0.9rem;
          color: #606266;
          margin: 0 0 8px;
          
          @include responsive.respond-to('md') {
            font-size: 0.85rem;
          }
          
          @include responsive.respond-to('sm') {
            display: none; // 在小屏幕上隐藏描述文本
          }
        }
        
        .resource-meta {
          display: flex;
          gap: 16px;
          font-size: 0.85rem;
          color: #909399;
          
          @include responsive.respond-to('md') {
            gap: 12px;
            font-size: 0.8rem;
          }
        }
      }
      
      .resource-actions {
        display: flex;
        align-items: center;
        
        @include responsive.respond-to('sm') {
          margin-top: 8px;
          width: 100%;
          justify-content: flex-end;
        }
      }
    }
  }
}
</style> 