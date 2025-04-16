<template>
  <div class="backup-page">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">备份与恢复</h1>
      <p class="text-gray-600">管理系统数据备份和恢复操作</p>
    </div>
    
    <!-- 备份操作 -->
    <div class="bg-white rounded-lg shadow-md overflow-hidden mb-6">
      <div class="p-6">
        <h2 class="text-lg font-semibold text-gray-800 mb-4">数据备份</h2>
        
        <div class="space-y-6">
          <!-- 手动备份 -->
          <div>
            <h3 class="text-md font-medium text-gray-700 mb-2">手动备份</h3>
            <p class="text-sm text-gray-600 mb-4">创建系统数据的完整备份，包括用户、课程、作业、考试等所有数据</p>
            
            <div class="flex items-center space-x-4">
              <button
                type="button"
                class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark focus:outline-none"
                @click="createBackup"
                :disabled="isCreatingBackup"
              >
                <span v-if="isCreatingBackup">
                  <i class="fas fa-spinner fa-spin mr-2"></i>备份中...
                </span>
                <span v-else>
                  <i class="fas fa-download mr-2"></i>创建备份
                </span>
              </button>
              
              <div v-if="backupProgress > 0 && backupProgress < 100" class="flex-1">
                <div class="w-full bg-gray-200 rounded-full h-2.5">
                  <div 
                    class="bg-primary h-2.5 rounded-full" 
                    :style="{ width: `${backupProgress}%` }"
                  ></div>
                </div>
                <div class="text-xs text-gray-500 mt-1">
                  备份进度: {{ backupProgress }}%
                </div>
              </div>
            </div>
          </div>
          
          <!-- 自动备份设置 -->
          <div>
            <h3 class="text-md font-medium text-gray-700 mb-2">自动备份设置</h3>
            
            <div class="space-y-4">
              <div class="flex items-center">
                <input
                  id="enableAutoBackup"
                  v-model="autoBackupSettings.enabled"
                  type="checkbox"
                  class="form-checkbox h-4 w-4 text-primary focus:ring-primary"
                />
                <label for="enableAutoBackup" class="ml-2 text-sm text-gray-700">
                  启用自动备份
                </label>
              </div>
              
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label for="backupFrequency" class="block text-sm font-medium text-gray-700 mb-1">
                    备份频率
                  </label>
                  <select
                    id="backupFrequency"
                    v-model="autoBackupSettings.frequency"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                    :disabled="!autoBackupSettings.enabled"
                  >
                    <option value="DAILY">每天</option>
                    <option value="WEEKLY">每周</option>
                    <option value="MONTHLY">每月</option>
                  </select>
                </div>
                
                <div>
                  <label for="backupTime" class="block text-sm font-medium text-gray-700 mb-1">
                    备份时间
                  </label>
                  <input
                    id="backupTime"
                    v-model="autoBackupSettings.time"
                    type="time"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                    :disabled="!autoBackupSettings.enabled"
                  />
                </div>
                
                <div>
                  <label for="backupRetention" class="block text-sm font-medium text-gray-700 mb-1">
                    保留备份数量
                  </label>
                  <input
                    id="backupRetention"
                    v-model.number="autoBackupSettings.retention"
                    type="number"
                    min="1"
                    max="30"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary"
                    :disabled="!autoBackupSettings.enabled"
                  />
                </div>
              </div>
              
              <div class="flex justify-end">
                <button
                  type="button"
                  class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark focus:outline-none"
                  @click="saveAutoBackupSettings"
                  :disabled="isSavingSettings"
                >
                  <span v-if="isSavingSettings">
                    <i class="fas fa-spinner fa-spin mr-2"></i>保存中...
                  </span>
                  <span v-else>保存设置</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 备份列表 -->
    <div class="bg-white rounded-lg shadow-md overflow-hidden mb-6">
      <div class="p-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-semibold text-gray-800">备份列表</h2>
          
          <button
            type="button"
            class="px-3 py-1 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 focus:outline-none text-sm"
            @click="refreshBackupList"
            :disabled="isLoadingBackups"
          >
            <i class="fas fa-sync-alt mr-1"></i>刷新
          </button>
        </div>
        
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  备份名称
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  创建时间
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  大小
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  类型
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  状态
                </th>
                <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  操作
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-if="isLoadingBackups">
                <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">
                  <i class="fas fa-spinner fa-spin mr-2"></i>加载中...
                </td>
              </tr>
              <tr v-else-if="backups.length === 0">
                <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">
                  暂无备份数据
                </td>
              </tr>
              <template v-else>
                <tr v-for="(backup, index) in backups" :key="index" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {{ backup.name }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ formatDate(backup.createdAt) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ formatFileSize(backup.size) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <span 
                      class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                      :class="backup.type === 'MANUAL' ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800'"
                    >
                      {{ backup.type === 'MANUAL' ? '手动' : '自动' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <span 
                      class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                      :class="getStatusClass(backup.status)"
                    >
                      {{ getStatusText(backup.status) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button
                      type="button"
                      class="text-primary hover:text-primary-dark mr-3"
                      @click="downloadBackup(backup.id)"
                      :disabled="backup.status !== 'COMPLETED'"
                    >
                      <i class="fas fa-download"></i>
                    </button>
                    <button
                      type="button"
                      class="text-primary hover:text-primary-dark mr-3"
                      @click="showRestoreConfirm(backup)"
                      :disabled="backup.status !== 'COMPLETED'"
                    >
                      <i class="fas fa-undo-alt"></i>
                    </button>
                    <button
                      type="button"
                      class="text-red-500 hover:text-red-700"
                      @click="showDeleteConfirm(backup.id)"
                    >
                      <i class="fas fa-trash-alt"></i>
                    </button>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    
    <!-- 恢复确认对话框 -->
    <div v-if="showRestoreDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl p-6 max-w-md w-full">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">确认恢复</h3>
        <p class="text-gray-600 mb-6">
          您确定要恢复到备份 <span class="font-medium">{{ selectedBackup?.name }}</span> 吗？
          <br>
          <span class="text-red-500 font-medium">警告：此操作将覆盖当前所有数据，且不可撤销！</span>
        </p>
        
        <div class="flex justify-end space-x-3">
          <button
            type="button"
            class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none"
            @click="showRestoreDialog = false"
          >
            取消
          </button>
          <button
            type="button"
            class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none"
            @click="restoreBackup"
            :disabled="isRestoring"
          >
            <span v-if="isRestoring">
              <i class="fas fa-spinner fa-spin mr-2"></i>恢复中...
            </span>
            <span v-else>确认恢复</span>
          </button>
        </div>
      </div>
    </div>
    
    <!-- 删除确认对话框 -->
    <div v-if="showDeleteDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl p-6 max-w-md w-full">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">确认删除</h3>
        <p class="text-gray-600 mb-6">
          您确定要删除此备份吗？此操作不可撤销！
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
            @click="deleteBackup"
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
import { ref, reactive, onMounted } from 'vue';
import { 
  getBackups, 
  createBackup, 
  downloadBackup as downloadBackupApi, 
  restoreBackup as restoreBackupApi, 
  deleteBackup as deleteBackupApi,
  getAutoBackupSettings,
  updateAutoBackupSettings
} from '@/api/admin/backup';
import { formatDate } from '@/utils/date';

// 状态
const backups = ref([]);
const isLoadingBackups = ref(false);
const isCreatingBackup = ref(false);
const backupProgress = ref(0);
const isSavingSettings = ref(false);
const isRestoring = ref(false);
const isDeleting = ref(false);

// 对话框状态
const showRestoreDialog = ref(false);
const showDeleteDialog = ref(false);
const selectedBackup = ref(null);
const selectedBackupId = ref(null);

// 自动备份设置
const autoBackupSettings = reactive({
  enabled: false,
  frequency: 'DAILY',
  time: '03:00',
  retention: 7
});

// 获取备份列表
const fetchBackups = async () => {
  try {
    isLoadingBackups.value = true;
    
    const response = await getBackups();
    backups.value = response.data || [];
  } catch (error) {
    console.error('获取备份列表失败:', error);
    alert(`获取备份列表失败: ${error.message || '未知错误'}`);
  } finally {
    isLoadingBackups.value = false;
  }
};

// 刷新备份列表
const refreshBackupList = () => {
  fetchBackups();
};

// 创建备份
const createBackup = async () => {
  try {
    isCreatingBackup.value = true;
    backupProgress.value = 0;
    
    // 模拟进度
    const progressInterval = setInterval(() => {
      if (backupProgress.value < 90) {
        backupProgress.value += 10;
      }
    }, 1000);
    
    await createBackupApi();
    
    clearInterval(progressInterval);
    backupProgress.value = 100;
    
    // 刷新备份列表
    fetchBackups();
    
    // 显示成功提示
    alert('备份创建成功');
    
    // 重置进度
    setTimeout(() => {
      backupProgress.value = 0;
    }, 2000);
  } catch (error) {
    console.error('创建备份失败:', error);
    alert(`创建备份失败: ${error.message || '未知错误'}`);
  } finally {
    isCreatingBackup.value = false;
  }
};

// 下载备份
const downloadBackup = async (backupId) => {
  try {
    await downloadBackupApi(backupId);
  } catch (error) {
    console.error('下载备份失败:', error);
    alert(`下载备份失败: ${error.message || '未知错误'}`);
  }
};

// 显示恢复确认对话框
const showRestoreConfirm = (backup) => {
  selectedBackup.value = backup;
  showRestoreDialog.value = true;
};

// 恢复备份
const restoreBackup = async () => {
  try {
    isRestoring.value = true;
    
    await restoreBackupApi(selectedBackup.value.id);
    
    // 关闭对话框
    showRestoreDialog.value = false;
    
    // 显示成功提示
    alert('备份恢复成功，系统将在5秒后自动刷新');
    
    // 5秒后刷新页面
    setTimeout(() => {
      window.location.reload();
    }, 5000);
  } catch (error) {
    console.error('恢复备份失败:', error);
    alert(`恢复备份失败: ${error.message || '未知错误'}`);
  } finally {
    isRestoring.value = false;
  }
};

// 显示删除确认对话框
const showDeleteConfirm = (backupId) => {
  selectedBackupId.value = backupId;
  showDeleteDialog.value = true;
};

// 删除备份
const deleteBackup = async () => {
  try {
    isDeleting.value = true;
    
    await deleteBackupApi(selectedBackupId.value);
    
    // 关闭对话框
    showDeleteDialog.value = false;
    
    // 刷新备份列表
    fetchBackups();
    
    // 显示成功提示
    alert('备份删除成功');
  } catch (error) {
    console.error('删除备份失败:', error);
    alert(`删除备份失败: ${error.message || '未知错误'}`);
  } finally {
    isDeleting.value = false;
  }
};

// 获取自动备份设置
const fetchAutoBackupSettings = async () => {
  try {
    const response = await getAutoBackupSettings();
    
    if (response.data) {
      autoBackupSettings.enabled = response.data.enabled;
      autoBackupSettings.frequency = response.data.frequency;
      autoBackupSettings.time = response.data.time;
      autoBackupSettings.retention = response.data.retention;
    }
  } catch (error) {
    console.error('获取自动备份设置失败:', error);
  }
};

// 保存自动备份设置
const saveAutoBackupSettings = async () => {
  try {
    isSavingSettings.value = true;
    
    await updateAutoBackupSettings(autoBackupSettings);
    
    // 显示成功提示
    alert('自动备份设置保存成功');
  } catch (error) {
    console.error('保存自动备份设置失败:', error);
    alert(`保存自动备份设置失败: ${error.message || '未知错误'}`);
  } finally {
    isSavingSettings.value = false;
  }
};

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B';
  
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 获取状态样式
const getStatusClass = (status) => {
  switch (status) {
    case 'COMPLETED':
      return 'bg-green-100 text-green-800';
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800';
    case 'FAILED':
      return 'bg-red-100 text-red-800';
    case 'IN_PROGRESS':
      return 'bg-blue-100 text-blue-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'COMPLETED':
      return '已完成';
    case 'PENDING':
      return '等待中';
    case 'FAILED':
      return '失败';
    case 'IN_PROGRESS':
      return '进行中';
    default:
      return '未知';
  }
};

// 初始化
onMounted(() => {
  fetchBackups();
  fetchAutoBackupSettings();
});
</script>
