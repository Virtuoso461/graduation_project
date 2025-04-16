<template>
  <div class="todos-page">
    <div class="mb-6 flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">待办事项</h1>
        <p class="text-gray-600">管理您的教学相关待办任务</p>
      </div>
      
      <router-link 
        to="/teacher/todos/create" 
        class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark focus:outline-none"
      >
        <i class="fas fa-plus mr-2"></i>新建待办
      </router-link>
    </div>
    
    <!-- 筛选和搜索 -->
    <div class="bg-white rounded-lg shadow-md p-4 mb-6">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
        <div class="flex items-center space-x-4">
          <div>
            <label for="statusFilter" class="block text-sm font-medium text-gray-700 mb-1">
              状态
            </label>
            <select
              id="statusFilter"
              v-model="filters.status"
              class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
              @change="fetchTodos"
            >
              <option value="ALL">全部</option>
              <option value="PENDING">待完成</option>
              <option value="COMPLETED">已完成</option>
            </select>
          </div>
          
          <div>
            <label for="priorityFilter" class="block text-sm font-medium text-gray-700 mb-1">
              优先级
            </label>
            <select
              id="priorityFilter"
              v-model="filters.priority"
              class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
              @change="fetchTodos"
            >
              <option value="ALL">全部</option>
              <option value="HIGH">高</option>
              <option value="MEDIUM">中</option>
              <option value="LOW">低</option>
            </select>
          </div>
          
          <div>
            <label for="dateFilter" class="block text-sm font-medium text-gray-700 mb-1">
              日期范围
            </label>
            <select
              id="dateFilter"
              v-model="filters.dateRange"
              class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
              @change="fetchTodos"
            >
              <option value="ALL">全部</option>
              <option value="TODAY">今天</option>
              <option value="TOMORROW">明天</option>
              <option value="THIS_WEEK">本周</option>
              <option value="NEXT_WEEK">下周</option>
              <option value="THIS_MONTH">本月</option>
              <option value="OVERDUE">已逾期</option>
            </select>
          </div>
        </div>
        
        <div class="w-full md:w-64">
          <label for="searchInput" class="block text-sm font-medium text-gray-700 mb-1">
            搜索
          </label>
          <div class="relative">
            <input
              id="searchInput"
              v-model="filters.keyword"
              type="text"
              class="w-full px-3 py-2 pl-10 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
              placeholder="搜索待办事项..."
              @input="debouncedSearch"
            />
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-search text-gray-400"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 待办列表 -->
    <div class="bg-white rounded-lg shadow-md overflow-hidden">
      <div v-if="isLoading" class="p-6 text-center">
        <i class="fas fa-spinner fa-spin text-primary text-2xl"></i>
        <p class="mt-2 text-gray-600">加载中...</p>
      </div>
      
      <div v-else-if="todos.length === 0" class="p-6 text-center">
        <i class="fas fa-clipboard-list text-gray-400 text-4xl"></i>
        <p class="mt-2 text-gray-600">暂无待办事项</p>
        <router-link 
          to="/teacher/todos/create" 
          class="mt-4 inline-block px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark focus:outline-none"
        >
          创建新待办
        </router-link>
      </div>
      
      <div v-else>
        <ul class="divide-y divide-gray-200">
          <li v-for="todo in todos" :key="todo.id" class="p-4 hover:bg-gray-50">
            <div class="flex items-start">
              <!-- 完成状态 -->
              <div class="mr-3 pt-1">
                <button 
                  class="w-6 h-6 rounded-full border-2 flex items-center justify-center focus:outline-none"
                  :class="todo.completed ? 'bg-primary border-primary' : 'border-gray-300 hover:border-primary'"
                  @click="toggleTodoStatus(todo)"
                >
                  <i v-if="todo.completed" class="fas fa-check text-white text-xs"></i>
                </button>
              </div>
              
              <!-- 内容 -->
              <div class="flex-1">
                <div class="flex items-center mb-1">
                  <h3 
                    class="text-base font-medium mr-2"
                    :class="todo.completed ? 'text-gray-500 line-through' : 'text-gray-800'"
                  >
                    {{ todo.title }}
                  </h3>
                  
                  <!-- 优先级标签 -->
                  <span 
                    class="px-2 py-0.5 text-xs rounded-full"
                    :class="getPriorityClass(todo.priority)"
                  >
                    {{ getPriorityText(todo.priority) }}
                  </span>
                </div>
                
                <p 
                  class="text-sm mb-2"
                  :class="todo.completed ? 'text-gray-400' : 'text-gray-600'"
                >
                  {{ todo.description }}
                </p>
                
                <div class="flex items-center text-xs text-gray-500">
                  <div class="flex items-center mr-4">
                    <i class="far fa-calendar-alt mr-1"></i>
                    <span :class="isOverdue(todo.dueDate) && !todo.completed ? 'text-red-500 font-medium' : ''">
                      {{ formatDate(todo.dueDate) }}
                    </span>
                  </div>
                  
                  <div v-if="todo.relatedType" class="flex items-center">
                    <i 
                      :class="[getRelatedTypeIcon(todo.relatedType), 'mr-1']"
                    ></i>
                    <span>{{ getRelatedTypeText(todo.relatedType) }}: {{ todo.relatedTitle }}</span>
                  </div>
                </div>
              </div>
              
              <!-- 操作 -->
              <div class="ml-4 flex items-center">
                <button
                  class="text-gray-400 hover:text-primary focus:outline-none mr-3"
                  @click="editTodo(todo)"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button
                  class="text-gray-400 hover:text-red-500 focus:outline-none"
                  @click="showDeleteConfirm(todo)"
                >
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </div>
          </li>
        </ul>
        
        <!-- 分页 -->
        <div class="px-6 py-4 flex justify-between items-center border-t border-gray-200">
          <div class="text-sm text-gray-500">
            共 <span class="font-medium">{{ totalItems }}</span> 项
          </div>
          
          <div class="flex space-x-2">
            <button
              class="px-3 py-1 rounded border border-gray-300 text-sm"
              :class="currentPage === 1 ? 'text-gray-400 cursor-not-allowed' : 'text-gray-700 hover:bg-gray-50'"
              :disabled="currentPage === 1"
              @click="changePage(currentPage - 1)"
            >
              上一页
            </button>
            
            <button
              v-for="page in pageNumbers"
              :key="page"
              class="px-3 py-1 rounded border text-sm"
              :class="page === currentPage ? 'bg-primary text-white border-primary' : 'text-gray-700 border-gray-300 hover:bg-gray-50'"
              @click="changePage(page)"
            >
              {{ page }}
            </button>
            
            <button
              class="px-3 py-1 rounded border border-gray-300 text-sm"
              :class="currentPage === totalPages ? 'text-gray-400 cursor-not-allowed' : 'text-gray-700 hover:bg-gray-50'"
              :disabled="currentPage === totalPages"
              @click="changePage(currentPage + 1)"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 删除确认对话框 -->
    <div v-if="showDeleteDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl p-6 max-w-md w-full">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">确认删除</h3>
        <p class="text-gray-600 mb-6">
          您确定要删除待办事项 "{{ selectedTodo?.title }}" 吗？此操作不可撤销！
        </p>
        
        <div class="flex justify-end space-x-3">
          <button
            type="button"
            class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none"
            @click="showDeleteDialog = false"
          >
            取消
          </button>
          <button
            type="button"
            class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none"
            @click="deleteTodo"
            :disabled="isDeleting"
          >
            <span v-if="isDeleting">
              <i class="fas fa-spinner fa-spin mr-2"></i>删除中...
            </span>
            <span v-else>确认删除</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getTodos, updateTodoStatus, deleteTodo as deleteTodoApi } from '@/api/teacher/todos';
import { formatDate } from '@/utils/date';

const router = useRouter();

// 状态
const todos = ref([]);
const isLoading = ref(false);
const isDeleting = ref(false);
const showDeleteDialog = ref(false);
const selectedTodo = ref(null);

// 分页
const currentPage = ref(1);
const pageSize = ref(10);
const totalItems = ref(0);
const totalPages = ref(1);

// 筛选
const filters = reactive({
  status: 'ALL',
  priority: 'ALL',
  dateRange: 'ALL',
  keyword: ''
});

// 计算属性
const pageNumbers = computed(() => {
  const pages = [];
  const maxVisiblePages = 5;
  
  if (totalPages.value <= maxVisiblePages) {
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
  } else {
    let startPage = Math.max(1, currentPage.value - Math.floor(maxVisiblePages / 2));
    let endPage = startPage + maxVisiblePages - 1;
    
    if (endPage > totalPages.value) {
      endPage = totalPages.value;
      startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
  }
  
  return pages;
});

// 获取待办列表
const fetchTodos = async () => {
  try {
    isLoading.value = true;
    
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: filters.keyword
    };
    
    if (filters.status !== 'ALL') {
      params.status = filters.status;
    }
    
    if (filters.priority !== 'ALL') {
      params.priority = filters.priority;
    }
    
    if (filters.dateRange !== 'ALL') {
      params.dateRange = filters.dateRange;
    }
    
    const response = await getTodos(params);
    
    todos.value = response.data.content || [];
    totalItems.value = response.data.totalElements || 0;
    totalPages.value = response.data.totalPages || 1;
  } catch (error) {
    console.error('获取待办列表失败:', error);
  } finally {
    isLoading.value = false;
  }
};

// 切换待办状态
const toggleTodoStatus = async (todo) => {
  try {
    const updatedStatus = !todo.completed;
    
    await updateTodoStatus(todo.id, { completed: updatedStatus });
    
    // 更新本地状态
    todo.completed = updatedStatus;
  } catch (error) {
    console.error('更新待办状态失败:', error);
    alert(`更新待办状态失败: ${error.message || '未知错误'}`);
  }
};

// 编辑待办
const editTodo = (todo) => {
  router.push(`/teacher/todos/edit/${todo.id}`);
};

// 显示删除确认对话框
const showDeleteConfirm = (todo) => {
  selectedTodo.value = todo;
  showDeleteDialog.value = true;
};

// 删除待办
const deleteTodo = async () => {
  try {
    isDeleting.value = true;
    
    await deleteTodoApi(selectedTodo.value.id);
    
    // 关闭对话框
    showDeleteDialog.value = false;
    
    // 刷新列表
    fetchTodos();
    
    // 显示成功提示
    alert('待办删除成功');
  } catch (error) {
    console.error('删除待办失败:', error);
    alert(`删除待办失败: ${error.message || '未知错误'}`);
  } finally {
    isDeleting.value = false;
  }
};

// 切换页码
const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  
  currentPage.value = page;
  fetchTodos();
};

// 搜索防抖
let searchTimeout = null;
const debouncedSearch = () => {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => {
    currentPage.value = 1; // 重置页码
    fetchTodos();
  }, 500);
};

// 获取优先级样式
const getPriorityClass = (priority) => {
  switch (priority) {
    case 'HIGH':
      return 'bg-red-100 text-red-800';
    case 'MEDIUM':
      return 'bg-yellow-100 text-yellow-800';
    case 'LOW':
      return 'bg-green-100 text-green-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

// 获取优先级文本
const getPriorityText = (priority) => {
  switch (priority) {
    case 'HIGH':
      return '高';
    case 'MEDIUM':
      return '中';
    case 'LOW':
      return '低';
    default:
      return '未知';
  }
};

// 获取关联类型图标
const getRelatedTypeIcon = (type) => {
  switch (type) {
    case 'COURSE':
      return 'fas fa-book';
    case 'ASSIGNMENT':
      return 'fas fa-tasks';
    case 'EXAM':
      return 'fas fa-file-alt';
    case 'STUDENT':
      return 'fas fa-user-graduate';
    default:
      return 'fas fa-link';
  }
};

// 获取关联类型文本
const getRelatedTypeText = (type) => {
  switch (type) {
    case 'COURSE':
      return '课程';
    case 'ASSIGNMENT':
      return '作业';
    case 'EXAM':
      return '考试';
    case 'STUDENT':
      return '学生';
    default:
      return '关联项';
  }
};

// 检查是否逾期
const isOverdue = (dueDate) => {
  if (!dueDate) return false;
  
  const now = new Date();
  const due = new Date(dueDate);
  
  return due < now;
};

// 初始化
onMounted(() => {
  fetchTodos();
});
</script>
