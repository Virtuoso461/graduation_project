<template>
  <div class="assignment-submission-view">
    <div class="page-header">
      <h1>作业提交详情</h1>
      <div class="header-buttons">
        <el-button @click="goBack" plain>返回</el-button>
      </div>
    </div>

    <div class="submission-container" v-loading="isLoading">
      <el-card v-if="submission">
        <template #header>
          <div class="submission-header">
            <div class="left">
              <h2>{{ submission.assignmentTitle }}</h2>
              <div class="submission-meta">
                <div class="meta-item">
                  <el-icon><Document /></el-icon>
                  <span>{{ submission.courseName }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  <span>提交时间：{{ formatDate(submission.submitTime) }}</span>
                </div>
                <div class="meta-item">
                  <el-icon v-if="submission.status === 'SUBMITTED'" color="#E6A23C"><InfoFilled /></el-icon>
                  <el-icon v-if="submission.status === 'GRADED'" color="#67C23A"><SuccessFilled /></el-icon>
                  <span>状态：{{ getStatusText(submission.status) }}</span>
                </div>
              </div>
            </div>
            <div class="right" v-if="submission.status === 'GRADED'">
              <div class="score-display">
                {{ submission.score || 0 }}/{{ submission.totalPoints || 100 }}
              </div>
            </div>
          </div>
        </template>

        <!-- 提交答案内容 -->
        <div class="submission-content">
          <!-- 文本答案 -->
          <div v-if="submission.answers && Object.keys(submission.answers).length > 0" class="section">
            <h3>文本答案</h3>
            <div class="text-answers">
              <div class="answer-item" v-for="(answer, questionId) in submission.answers" :key="questionId">
                <div class="question-title">{{ getQuestionTitle(questionId) }}</div>
                <div class="answer-content">{{ answer }}</div>
                <div v-if="submission.feedback && submission.feedback[questionId]" class="feedback">
                  <div class="feedback-title">教师反馈：</div>
                  <div class="feedback-content">{{ submission.feedback[questionId] }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 附件列表 -->
          <div v-if="submission.attachments && submission.attachments.length > 0" class="section">
            <h3>提交的附件</h3>
            <div class="attachments-list">
              <div class="attachment-item" v-for="(file, index) in submission.attachments" :key="index">
                <el-icon class="file-icon"><Document /></el-icon>
                <div class="file-info">
                  <div class="file-name">{{ file.fileName }}</div>
                  <div class="file-meta">{{ getFileTypeText(file.fileType) }}</div>
                </div>
                <div class="file-actions">
                  <el-button size="small" type="primary" @click="downloadFile(file)" plain>
                    <el-icon><Download /></el-icon>下载
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 提交备注 -->
          <div v-if="submission.comments" class="section">
            <h3>提交备注</h3>
            <div class="submission-comments">{{ submission.comments }}</div>
          </div>

          <!-- 整体评分和评语 -->
          <div v-if="submission.status === 'GRADED'" class="section">
            <h3>评分详情</h3>
            <div class="grading-details">
              <div class="overall-score">
                <div class="score-title">总分</div>
                <div class="score-value">{{ submission.score || 0 }}/{{ submission.totalPoints || 100 }}</div>
              </div>
              <div v-if="submission.teacherComment" class="teacher-comment">
                <div class="comment-title">教师评语</div>
                <div class="comment-content">{{ submission.teacherComment }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { assignmentApi } from '@/utils/http/api'
import { 
  Calendar, Document, Download, InfoFilled, 
  SuccessFilled, Warning
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const assignmentId = ref(route.params.assignmentId as string)
const isLoading = ref(false)
const submission = ref<any>(null)

// 获取提交详情
const fetchSubmissionDetail = async () => {
  try {
    isLoading.value = true
    const response = await assignmentApi.getSubmissionDetail(assignmentId.value)
    
    if (response && response.code === 200 && response.data) {
      submission.value = response.data
      console.log('获取提交详情成功:', submission.value)
    } else {
      ElMessage.error('获取提交详情失败')
      console.error('获取提交详情失败:', response)
    }
  } catch (error) {
    ElMessage.error(`获取提交详情失败: ${error instanceof Error ? error.message : '未知错误'}`)
    console.error('获取提交详情出错:', error)
  } finally {
    isLoading.value = false
  }
}

// 下载文件
const downloadFile = (file) => {
  if (file.fileUrl) {
    window.open(file.fileUrl, '_blank')
  } else {
    ElMessage.warning('文件链接不可用')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'SUBMITTED':
      return '已提交（待批改）'
    case 'GRADED':
      return '已批改'
    default:
      return status
  }
}

// 获取文件类型文本
const getFileTypeText = (fileType) => {
  if (!fileType) return '未知类型'
  if (fileType.includes('pdf')) {
    return 'PDF文档'
  } else if (fileType.includes('word') || fileType.includes('doc')) {
    return 'Word文档'
  } else if (fileType.includes('image') || fileType.includes('png') || fileType.includes('jpg')) {
    return '图片'
  } else if (fileType.includes('zip') || fileType.includes('rar') || fileType.includes('7z')) {
    return '压缩文件'
  } else if (fileType.includes('text')) {
    return '文本文件'
  } else {
    return fileType
  }
}

// 获取问题标题
const getQuestionTitle = (questionId) => {
  // 如果有问题标题映射可以在这里实现
  return `问题 ${questionId}`
}

// 返回
const goBack = () => {
  router.push(`/assignments/${assignmentId.value}`)
}

// 页面加载时获取提交详情
onMounted(() => {
  fetchSubmissionDetail()
})
</script>

<style lang="scss" scoped>
.assignment-submission-view {
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

  .submission-container {
    .submission-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;

      .left {
        h2 {
          margin: 0 0 10px 0;
          font-size: 18px;
        }

        .submission-meta {
          display: flex;
          flex-wrap: wrap;
          gap: 15px;
          margin-bottom: 10px;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 5px;
            color: var(--el-text-color-secondary);
            font-size: 14px;
          }
        }
      }

      .right {
        .score-display {
          background-color: #67c23a;
          color: white;
          padding: 10px 15px;
          border-radius: 4px;
          font-size: 18px;
          font-weight: bold;
        }
      }
    }

    .submission-content {
      .section {
        margin-bottom: 30px;

        h3 {
          font-size: 16px;
          margin-bottom: 15px;
          padding-bottom: 10px;
          border-bottom: 1px solid var(--el-border-color-light);
        }

        .text-answers {
          .answer-item {
            margin-bottom: 20px;
            padding-bottom: 20px;
            border-bottom: 1px dashed var(--el-border-color-light);

            &:last-child {
              border-bottom: none;
            }

            .question-title {
              font-weight: bold;
              margin-bottom: 10px;
            }

            .answer-content {
              background: var(--el-fill-color-light);
              padding: 15px;
              border-radius: 4px;
              white-space: pre-wrap;
              margin-bottom: 10px;
            }

            .feedback {
              margin-top: 10px;
              padding: 10px;
              background: #f8f8f8;
              border-left: 3px solid #67c23a;

              .feedback-title {
                font-weight: bold;
                margin-bottom: 5px;
                color: #67c23a;
              }
            }
          }
        }

        .attachments-list {
          display: flex;
          flex-direction: column;
          gap: 10px;

          .attachment-item {
            display: flex;
            align-items: center;
            padding: 10px;
            background: var(--el-fill-color-light);
            border-radius: 4px;

            .file-icon {
              font-size: 24px;
              margin-right: 10px;
              color: var(--el-color-primary);
            }

            .file-info {
              flex: 1;

              .file-name {
                font-weight: bold;
              }

              .file-meta {
                font-size: 12px;
                color: var(--el-text-color-secondary);
              }
            }
          }
        }

        .submission-comments {
          background: var(--el-fill-color-light);
          padding: 15px;
          border-radius: 4px;
          white-space: pre-wrap;
        }

        .grading-details {
          .overall-score {
            display: flex;
            align-items: center;
            margin-bottom: 15px;

            .score-title {
              font-weight: bold;
              margin-right: 10px;
            }

            .score-value {
              font-size: 18px;
              color: #67c23a;
              font-weight: bold;
            }
          }

          .teacher-comment {
            padding: 15px;
            background: #f8f8f8;
            border-radius: 4px;

            .comment-title {
              font-weight: bold;
              margin-bottom: 10px;
            }

            .comment-content {
              white-space: pre-wrap;
            }
          }
        }
      }
    }
  }
}
</style> 