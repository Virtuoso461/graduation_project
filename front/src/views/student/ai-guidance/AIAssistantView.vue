<template>
  <div class="ai-assistant">
    <div class="page-header">
      <h2>AI 学习助手 <span class="model-badge">Deepseek-R1-7B</span></h2>
    </div>
    
    <div class="chat-container">
      <div class="chat-messages" ref="messagesContainer">
        <el-empty v-if="messages.length === 0" description="开始与 AI 助手对话吧" />
        
        <div v-for="(message, index) in messages" 
             :key="index" 
             :class="['message', message.type]">
          <div class="message-content" v-html="formatMessage(message.content)"></div>
        </div>
        
        <div v-if="isLoading" class="message assistant">
          <div class="message-content loading">
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
          </div>
        </div>
      </div>
      
      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入你的问题..."
          @keyup.enter.ctrl="sendMessage"
          :disabled="isLoading"
        />
        <div class="input-actions">
          <el-tooltip content="调整温度" placement="top">
            <div class="slider-container">
              <span class="slider-label">温度</span>
              <el-slider v-model="temperature" :min="0" :max="1" :step="0.1" :disabled="isLoading" />
            </div>
          </el-tooltip>
          <el-button type="primary" @click="sendMessage" :loading="isLoading">
            发送
          </el-button>
          <el-button type="danger" @click="clearChat" :disabled="isLoading || messages.length <= 1">
            清空对话
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { aiApi } from '@/utils/http/api'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  breaks: true,
})

interface Message {
  type: 'user' | 'assistant';
  content: string;
}

const messages = ref<Message[]>([])
const inputMessage = ref('')
const messagesContainer = ref<HTMLElement | null>(null)
const isLoading = ref(false)
const temperature = ref(0.7)
const maxTokens = ref(2048)

// 处理 markdown 格式
const formatMessage = (content: string) => {
  return md.render(content)
}

// 滚动到消息底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 清空聊天记录
const clearChat = () => {
  ElMessageBox.confirm('确定要清空聊天记录吗?', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 只保留第一条欢迎消息
    messages.value = messages.value.slice(0, 1)
    ElMessage.success('聊天记录已清空')
  }).catch(() => {})
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return
  
  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: inputMessage.value
  })
  
  // 保存用户输入并清空输入框
  const userInput = inputMessage.value
  inputMessage.value = ''
  
  // 滚动到底部
  scrollToBottom()
  
  // 设置加载状态
  isLoading.value = true
  
  try {
    // 收集之前的对话历史，只取最近5条消息，不包括欢迎消息
    // 这样可以确保每次对话都是相对独立的，减少历史记录影响
    const messagesWithoutWelcome = messages.value.length > 1 ? messages.value.slice(1) : []
    const recentHistory = messagesWithoutWelcome.slice(-10) // 只取最近10条消息
    
    // 准备请求参数
    const requestParams = {
      message: userInput,
      history: recentHistory,
      temperature: temperature.value,
      max_tokens: maxTokens.value
    }
    
    // 调用AI API接口
    const response = await aiApi.sendChatMessage(requestParams)
    
    // 添加AI回复
    messages.value.push({
      type: 'assistant',
      content: response.message
    })
    
    // 滚动到底部
    scrollToBottom()
    
  } catch (error: any) {
    console.error('获取AI回复失败:', error)
    // 显示错误消息
    const errorMsg = error.message || '抱歉，我遇到了一些问题。请稍后再试或联系管理员。'
    
    messages.value.push({
      type: 'assistant',
      content: `**错误**: ${errorMsg}`
    })
    
    ElMessage.error(errorMsg)
    
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  // 添加欢迎消息
  messages.value.push({
    type: 'assistant',
    content: '你好！我是你的 AI 学习助手，由 Deepseek 公司开发的 R1-7B 模型提供支持。有什么我可以帮你的吗？'
  })
})
</script>

<style lang="scss" scoped>
.ai-assistant {
  height: 100%;
  display: flex;
  flex-direction: column;
  
  .page-header {
    padding: 20px;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    align-items: center;
    
    h2 {
      margin: 0;
      font-size: 24px;
      color: #303133;
    }
    
    .model-badge {
      margin-left: 10px;
      font-size: 14px;
      background-color: #67c23a;
      color: white;
      padding: 2px 8px;
      border-radius: 12px;
      font-weight: normal;
    }
  }
  
  .chat-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    gap: 20px;
    overflow: hidden;
    
    .chat-messages {
      flex: 1;
      overflow-y: auto;
      padding: 20px;
      background-color: #f5f7fa;
      border-radius: 8px;
      
      .message {
        margin-bottom: 20px;
        max-width: 85%;
        
        &.user {
          margin-left: auto;
          
          .message-content {
            background-color: #409eff;
            color: white;
            border-radius: 8px 8px 0 8px;
            
            :deep(a) {
              color: #ecf5ff;
              text-decoration: underline;
            }
            
            :deep(pre) {
              background: rgba(0, 0, 0, 0.1);
              padding: 10px;
              border-radius: 4px;
              overflow-x: auto;
            }
            
            :deep(code) {
              font-family: monospace;
              padding: 2px 4px;
              background: rgba(0, 0, 0, 0.1);
              border-radius: 2px;
            }
          }
        }
        
        &.assistant {
          margin-right: auto;
          
          .message-content {
            background-color: white;
            color: #303133;
            border-radius: 8px 8px 8px 0;
            
            :deep(a) {
              color: #409eff;
            }
            
            :deep(pre) {
              background: #f6f8fa;
              padding: 10px;
              border-radius: 4px;
              overflow-x: auto;
            }
            
            :deep(code) {
              font-family: monospace;
              padding: 2px 4px;
              background: #f6f8fa;
              border-radius: 2px;
            }
            
            :deep(table) {
              border-collapse: collapse;
              margin: 10px 0;
              
              th, td {
                border: 1px solid #dfe2e5;
                padding: 6px 13px;
              }
              
              th {
                background: #f6f8fa;
              }
            }
            
            &.loading {
              display: flex;
              align-items: center;
              justify-content: center;
              min-height: 24px;
              min-width: 60px;
              
              .loading-dot {
                display: inline-block;
                width: 8px;
                height: 8px;
                margin: 0 3px;
                border-radius: 50%;
                background-color: #909399;
                animation: dot-flashing 1s infinite alternate;
                
                &:nth-child(2) {
                  animation-delay: 0.2s;
                }
                
                &:nth-child(3) {
                  animation-delay: 0.4s;
                }
              }
            }
          }
        }
        
        .message-content {
          padding: 12px 16px;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
          word-break: break-word;
        }
      }
    }
    
    .chat-input {
      display: flex;
      flex-direction: column;
      gap: 12px;
      
      .el-input {
        width: 100%;
      }
      
      .input-actions {
        display: flex;
        align-items: center;
        
        .slider-container {
          flex: 1;
          display: flex;
          align-items: center;
          margin-right: 20px;
          
          .slider-label {
            margin-right: 10px;
            white-space: nowrap;
            font-size: 14px;
            color: #606266;
          }
          
          .el-slider {
            margin-right: 20px;
            flex: 1;
          }
        }
      }
    }
  }
}

@keyframes dot-flashing {
  0% {
    opacity: 0.3;
  }
  100% {
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .chat-container {
    .chat-messages {
      .message {
        max-width: 90%;
      }
    }
    
    .chat-input {
      .input-actions {
        flex-direction: column;
        align-items: stretch;
        
        .slider-container {
          margin-bottom: 10px;
          margin-right: 0;
        }
        
        .el-button {
          margin-bottom: 5px;
        }
      }
    }
  }
}
</style> 