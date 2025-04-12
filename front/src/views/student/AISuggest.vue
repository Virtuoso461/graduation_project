<template>
  <div class="ai-suggest-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="16" class="chat-container">
        <el-card class="chat-card">
          <template #header>
            <div class="chat-header">
              <div class="title">
                <i class="el-icon-s-promotion"></i>
                <span>学习助手</span>
              </div>
              <div class="subtitle">
                AI驱动的个性化学习指导，随时为您解答学习困惑
              </div>
            </div>
          </template>
          
          <div class="chat-messages" ref="messagesContainer">
            <div v-if="messages.length === 0" class="empty-state">
              <el-empty 
                description="开始向学习助手提问吧！"
                :image-size="200"
              >
                <el-button type="primary" @click="focusInput">开始聊天</el-button>
              </el-empty>
            </div>
            
            <div 
              v-for="(message, index) in messages" 
              :key="index" 
              :class="['message', message.sender === 'user' ? 'user-message' : 'ai-message']"
            >
              <div class="message-avatar">
                <el-avatar 
                  v-if="message.sender === 'user'"
                  :src="userAvatar || require('@/assets/img/avatar/default-avatar.png')" 
                  :size="40"
                ></el-avatar>
                <el-avatar 
                  v-else
                  icon="el-icon-s-promotion"
                  :size="40"
                  style="background-color: #409EFF;"
                ></el-avatar>
              </div>
              <div class="message-content">
                <div class="message-text" v-html="formatMessage(message.content)"></div>
                <div class="message-time">{{ formatTime(message.time) }}</div>
              </div>
            </div>
            
            <div v-if="loading" class="ai-message loading-message">
              <div class="message-avatar">
                <el-avatar 
                  icon="el-icon-s-promotion"
                  :size="40"
                  style="background-color: #409EFF;"
                ></el-avatar>
              </div>
              <div class="message-content">
                <div class="loading-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="chat-input">
            <el-input
              v-model="userInput"
              type="textarea"
              :rows="2"
              placeholder="输入您的问题..."
              resize="none"
              ref="inputField"
              @keydown.enter.prevent="sendMessage"
            ></el-input>
            <el-button 
              type="primary" 
              :icon="loading ? 'el-icon-loading' : 'el-icon-s-promotion'" 
              class="send-button"
              @click="sendMessage"
              :disabled="loading || !userInput.trim()"
            >
              {{ loading ? '思考中...' : '发送' }}
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8" class="suggestions-container">
        <el-card class="suggestions-card">
          <template #header>
            <div class="suggestions-header">
              <span>常见问题推荐</span>
            </div>
          </template>
          
          <div class="quick-suggestions">
            <el-button 
              v-for="(question, index) in commonQuestions" 
              :key="index"
              class="suggestion-item"
              @click="useQuestion(question)"
            >
              {{ question }}
            </el-button>
          </div>
        </el-card>
        
        <el-card class="learning-stats" v-if="learningStats">
          <template #header>
            <div class="stats-header">
              <span>我的学习统计</span>
            </div>
          </template>
          
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-title">总选课数</div>
              <div class="stat-value">{{ learningStats.totalCourses }} 门</div>
            </div>
            <div class="stat-item">
              <div class="stat-title">已完成课程</div>
              <div class="stat-value">{{ learningStats.completedCourses }} 门</div>
            </div>
            <div class="stat-item">
              <div class="stat-title">平均学习进度</div>
              <div class="stat-value">{{ learningStats.averageProgress }}%</div>
            </div>
            
            <div class="recent-courses" v-if="learningStats.recentCourses && learningStats.recentCourses.length > 0">
              <div class="recent-title">最近学习</div>
              <div 
                v-for="course in learningStats.recentCourses" 
                :key="course.id"
                class="recent-course"
              >
                <div class="course-title">{{ course.title }}</div>
                <div class="course-progress">
                  <el-progress 
                    :percentage="course.progress" 
                    :stroke-width="10" 
                    :show-text="false"
                  ></el-progress>
                  <span class="progress-text">{{ course.progress }}%</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue';
import { useUserStore } from '@/stores/user';
import axios from 'axios';
import marked from 'marked';
import DOMPurify from 'dompurify';

const userStore = useUserStore();
const userAvatar = computed(() => userStore.userInfo?.avatar);
const userEmail = computed(() => userStore.userInfo?.username);

const messagesContainer = ref(null);
const inputField = ref(null);
const userInput = ref('');
const messages = ref([]);
const loading = ref(false);
const commonQuestions = ref([]);
const learningStats = ref(null);

// 加载常见问题和学习统计
onMounted(async () => {
  try {
    // 获取常见问题
    const response = await axios.get('/api/ai/common-questions');
    commonQuestions.value = response.data.data.questions || [];
    
    // 加载统计数据
    await loadLearningStats();
    
    // 添加欢迎消息
    addMessage('assistant', '你好！我是你的学习助手，有任何学习相关的问题都可以问我。我能根据你的学习情况提供个性化的建议和指导。');
  } catch (error) {
    console.error('初始化AI助手失败:', error);
    commonQuestions.value = [
      "我如何提高学习效率？",
      "如何制定合理的学习计划？",
      "我应该如何复习所学内容？",
      "如何解决学习中的难点？"
    ];
  }
});

// 加载学习统计数据
const loadLearningStats = async () => {
  try {
    const response = await axios.get('/api/courses/learning-stats');
    learningStats.value = response.data;
  } catch (error) {
    console.error('获取学习统计失败:', error);
  }
};

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim() || loading.value) return;
  
  const question = userInput.value.trim();
  addMessage('user', question);
  userInput.value = '';
  loading.value = true;
  
  try {
    const response = await axios.post('/api/ai-assistant/guidance', {
      question: question
    });
    
    // 添加AI回复
    if (response.data && response.data.message) {
      addMessage('assistant', response.data.message);
    } else {
      addMessage('assistant', '抱歉，我暂时无法回答这个问题，请稍后再试。');
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    addMessage('assistant', '抱歉，服务暂时不可用，请稍后再试。');
  } finally {
    loading.value = false;
  }
};

// 添加消息到聊天记录
const addMessage = (sender, content) => {
  messages.value.push({
    sender,
    content,
    time: new Date()
  });
  
  // 滚动到底部
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

// 使用推荐问题
const useQuestion = (question) => {
  userInput.value = question;
  focusInput();
};

// 聚焦输入框
const focusInput = () => {
  nextTick(() => {
    inputField.value.focus();
  });
};

// 格式化消息内容（支持Markdown）
const formatMessage = (content) => {
  if (!content) return '';
  // 将Markdown转换为HTML并清理，防止XSS攻击
  return DOMPurify.sanitize(marked.parse(content));
};

// 格式化时间
const formatTime = (date) => {
  if (!date) return '';
  const d = new Date(date);
  const hours = d.getHours().toString().padStart(2, '0');
  const minutes = d.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}`;
};
</script>

<style scoped>
.ai-suggest-container {
  padding: 20px;
  height: calc(100vh - 150px);
}

.chat-container, .suggestions-container {
  margin-bottom: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  flex-direction: column;
}

.chat-header .title {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.chat-header .title i {
  margin-right: 8px;
  color: #409EFF;
}

.chat-header .subtitle {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  margin-bottom: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.empty-state {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.message {
  margin-bottom: 15px;
  display: flex;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  margin: 0 10px;
}

.message-content {
  max-width: 70%;
  background-color: #fff;
  padding: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.user-message .message-content {
  background-color: #ecf5ff;
}

.message-text {
  line-height: 1.5;
  word-break: break-word;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  text-align: right;
}

.loading-message .message-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.loading-dots {
  display: flex;
  align-items: center;
}

.loading-dots span {
  display: inline-block;
  width: 8px;
  height: 8px;
  margin: 0 3px;
  background-color: #409EFF;
  border-radius: 50%;
  animation: loading-dots 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes loading-dots {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-input {
  display: flex;
  align-items: flex-end;
  margin-top: auto;
}

.send-button {
  margin-left: 10px;
  height: 50px;
  width: 100px;
}

.suggestions-card,
.learning-stats {
  margin-bottom: 20px;
}

.suggestions-header,
.stats-header {
  font-weight: bold;
}

.quick-suggestions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.suggestion-item {
  text-align: left;
  padding: 10px;
  border-radius: 4px;
  transition: all 0.3s;
  height: auto;
  white-space: normal;
  word-break: break-word;
}

.suggestion-item:hover {
  background-color: #ecf5ff;
  color: #409EFF;
}

.stats-content {
  padding: 10px 0;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.stat-title {
  color: #606266;
}

.stat-value {
  font-weight: bold;
  color: #409EFF;
}

.recent-courses {
  margin-top: 20px;
}

.recent-title {
  font-weight: bold;
  margin-bottom: 10px;
  color: #606266;
}

.recent-course {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

.course-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.course-progress {
  display: flex;
  align-items: center;
}

.progress-text {
  margin-left: 10px;
  font-size: 12px;
  color: #606266;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .ai-suggest-container {
    height: auto;
  }
  
  .message-content {
    max-width: 85%;
  }
}

@media (max-width: 768px) {
  .message-content {
    max-width: 85%;
  }
}

</style>
