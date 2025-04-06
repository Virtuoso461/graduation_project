<template>
  <div class="profile-view">
    <div class="page-header">
      <h1>个人资料</h1>
      <div class="header-actions">
        <el-button v-if="!isEditing" type="primary" @click="startEditing">
          <el-icon><Edit /></el-icon>编辑资料
        </el-button>
        <template v-else>
          <el-button type="primary" @click="handleSave">
            <el-icon><Check /></el-icon>保存修改
          </el-button>
          <el-button @click="cancelEditing">
            <el-icon><Close /></el-icon>取消
          </el-button>
        </template>
      </div>
    </div>

    <div class="profile-container">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="profile-card">
            <div class="avatar-container">
              <el-avatar :size="120" :src="profileData.avatar || defaultAvatar" :key="profileData.avatar" />
              <el-upload
                v-if="isEditing"
                class="avatar-uploader"
                action="/api/upload/avatar"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                :data="{ email: userStore.getUserEmail() || profileData.email }"
                name="file"
              >
                <el-button type="primary" plain>
                  <el-icon><Upload /></el-icon>更换头像
                </el-button>
              </el-upload>
            </div>
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-value">{{ userInfo?.courseCount || 0 }}</div>
                <div class="stat-label">学习课程</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userInfo?.studyHours || 0 }}</div>
                <div class="stat-label">学习时长</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userInfo?.points || 0 }}</div>
                <div class="stat-label">积分</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="16">
          <el-card class="info-card">
            <template #header>
              <div class="card-header">
                <span>基本信息</span>
              </div>
            </template>
            <el-form
              ref="formRef"
              :model="profileData"
              :rules="rules"
              label-width="100px"
              class="profile-form"
              v-loading="loading"
            >
              <el-form-item label="用户名" prop="username">
                <template v-if="isEditing">
                  <el-input v-model="profileData.username" />
                </template>
                <template v-else>
                  <div class="info-text">{{ profileData.username || '未设置' }}</div>
                </template>
              </el-form-item>
              <el-form-item label="姓名" prop="name">
                <template v-if="isEditing">
                  <el-input v-model="profileData.name" />
                </template>
                <template v-else>
                  <div class="info-text">{{ profileData.name || '未设置' }}</div>
                </template>
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <div class="info-text">{{ profileData.email }}</div>
              </el-form-item>
              <el-form-item label="手机号" prop="phoneNumber">
                <template v-if="isEditing">
                  <el-input v-model="profileData.phoneNumber" />
                </template>
                <template v-else>
                  <div class="info-text">{{ profileData.phoneNumber || '未设置' }}</div>
                </template>
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <template v-if="isEditing">
                  <el-radio-group v-model="profileData.gender">
                    <el-radio value="male">男</el-radio>
                    <el-radio value="female">女</el-radio>
                  </el-radio-group>
                </template>
                <template v-else>
                  <div class="info-text">{{ profileData.gender === 'male' ? '男' : profileData.gender === 'female' ? '女' : '未设置' }}</div>
                </template>
              </el-form-item>
              <el-form-item label="生日" prop="birthday">
                <template v-if="isEditing">
                  <el-date-picker
                    v-model="profileData.birthday"
                    type="date"
                    placeholder="选择日期"
                  />
                </template>
                <template v-else>
                  <div class="info-text">{{ profileData.birthday || '未设置' }}</div>
                </template>
              </el-form-item>
              <el-form-item label="个人简介" prop="bio">
                <template v-if="isEditing">
                  <el-input
                    v-model="profileData.bio"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入个人简介"
                  />
                </template>
                <template v-else>
                  <div class="info-text">{{ profileData.bio || '未设置个人简介' }}</div>
                </template>
              </el-form-item>
            </el-form>
          </el-card>

          <el-card class="security-card">
            <template #header>
              <div class="card-header">
                <span>安全设置</span>
              </div>
            </template>
            <div class="security-list">
              <div class="security-item">
                <div class="item-info">
                  <h4>登录密码</h4>
                  <p>定期更换密码可以保护账号安全</p>
                </div>
                <el-button @click="handleChangePassword">修改密码</el-button>
              </div>
              <div class="security-item">
                <div class="item-info">
                  <h4>手机绑定</h4>
                  <p>已绑定手机：{{ profileData.phoneNumber || '未绑定' }}</p>
                </div>
                <el-button @click="handleChangePhone">更换手机</el-button>
              </div>
              <div class="security-item">
                <div class="item-info">
                  <h4>邮箱绑定</h4>
                  <p>已绑定邮箱：{{ profileData.email }}</p>
                </div>
                <el-button @click="handleChangeEmail">更换邮箱</el-button>
              </div>
              <div class="security-item">
                <div class="item-info">
                  <h4>账号注销</h4>
                  <p>注销后账号将无法恢复</p>
                </div>
                <el-button type="danger" @click="handleDeleteAccount">注销账号</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { Check, Close, Edit, Upload } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { profileApi } from '@/utils/http/api'
import { getCurrentUserInfo, UserInfo, saveUserInfo } from '@/utils/indexedDB'
import { ApiResponse, ProfileData, ExtendedUserInfo } from '@/types/api'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 用户信息
const userStore = useUserStore()
const userInfo = ref<ExtendedUserInfo | null>(userStore.userInfo as ExtendedUserInfo | null)

const formRef = ref<FormInstance>()
const loading = ref(false)
const isEditing = ref(false)

// 编辑模式的临时数据
const tempProfileData = reactive<ProfileData>({
  email: '',
  username: '',
  name: '',
  phoneNumber: '',
  gender: '',
  birthday: undefined,
  bio: '',
  avatar: ''
})

// 表单验证规则
const rules = ref<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: false, message: '请输入姓名', trigger: 'blur' }
  ],
  phoneNumber: [
    { required: false, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
})

// 个人资料数据
const profileData = reactive<ProfileData>({
  email: '',
  username: '',
  name: '',
  phoneNumber: '',
  gender: '',
  birthday: undefined,
  bio: '',
  avatar: ''
})

// 开始编辑
const startEditing = () => {
  // 保存当前数据到临时对象
  Object.assign(tempProfileData, profileData)
  isEditing.value = true
}

// 取消编辑
const cancelEditing = () => {
  // 恢复原始数据
  Object.assign(profileData, tempProfileData)
  isEditing.value = false
}

// 获取个人资料
const fetchProfile = async () => {
  console.log('开始获取个人资料...')
  loading.value = true
  
  try {
    // 尝试从IndexedDB获取用户信息
    const userId = localStorage.getItem('currentUserId')
    console.log('从IndexedDB获取用户ID:', userId)
    
    if (userId) {
      try {
        const userInfo = await getCurrentUserInfo()
        console.log('从IndexedDB获取到的用户信息:', userInfo)
        
        if (userInfo) {
          // 从IndexedDB获取到用户信息，更新到表单数据
          profileData.name = userInfo.name || '';
          profileData.gender = userInfo.gender || '';
          profileData.phoneNumber = userInfo.phoneNumber || '';
          profileData.bio = userInfo.bio || '';
          profileData.birthday = userInfo.birthday || '';
          profileData.avatar = userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
          profileData.email = userInfo.email || '';
          profileData.username = userInfo.username || '';
          
          // 更新用户统计信息
          if (userInfo.value) {
            userInfo.value.courseCount = userInfo.courseCount || 0;
            userInfo.value.studyHours = userInfo.studyHours || 0;
            userInfo.value.points = userInfo.points || 0;
          }
          
          // 更新用户信息到store
          userStore.updateUserInfo({
            name: userInfo.name || '',
            avatar: userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
            role: userInfo.role || 'student'
          });
          
          console.log('已从IndexedDB更新个人资料数据:', profileData);
          return; // 如果从IndexedDB获取成功，直接返回，不再调用API
        }
      } catch (error) {
        console.error('从IndexedDB获取用户信息失败:', error);
        // 获取失败，继续尝试从API获取
      }
    }
    
    // 如果从IndexedDB获取失败或没有数据，尝试从API获取
    console.log('从API获取个人资料数据...');
    const response = await profileApi.getProfile(userId || '');
    
    if (response && typeof response === 'object' && 'data' in response && response.data) {
      const data = response.data as any;
      
      // 更新表单数据
      profileData.name = data.name || '';
      profileData.gender = data.gender || '';
      profileData.phoneNumber = data.phoneNumber || '';
      profileData.bio = data.bio || '';
      profileData.birthday = data.birthday || '';
      profileData.avatar = data.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
      
      if (userInfo.value) {
        userInfo.value.courseCount = data.courseCount || 0;
        userInfo.value.studyHours = data.studyHours || 0;
        userInfo.value.points = data.points || 0;
      }
      
      console.log('已从API更新个人资料数据:', profileData);
      
      // 如果从API获取成功，同时更新到IndexedDB
      if (userId) {
        const userInfoFromDB = await getCurrentUserInfo();
        if (userInfoFromDB) {
          // 更新现有数据
          const updatedUserInfo = {
            ...userInfoFromDB,
            name: profileData.name,
            gender: profileData.gender,
            phoneNumber: profileData.phoneNumber,
            bio: profileData.bio,
            birthday: profileData.birthday,
            avatar: profileData.avatar
          };
          await saveUserInfo(updatedUserInfo);
          console.log('已将API获取的个人资料同步到IndexedDB');
          
          // 更新用户信息到store
          userStore.updateUserInfo({
            name: updatedUserInfo.name || '',
            avatar: updatedUserInfo.avatar
          });
        } else {
          console.warn('未找到现有用户信息，无法更新IndexedDB');
        }
      }
    } else {
      console.warn('API返回的数据格式不正确或为空');
      // 使用默认值
      profileData.name = '';
      profileData.gender = '';
      profileData.phoneNumber = '';
      profileData.bio = '';
      profileData.birthday = '';
      profileData.avatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
    }
  } catch (error: any) {
    console.error('获取个人资料失败:', error);
    ElMessage.error(error?.message || '获取个人资料失败');
    
    // 发生错误时，使用默认值
    profileData.name = '';
    profileData.gender = '';
    profileData.phoneNumber = '';
    profileData.bio = '';
    profileData.birthday = '';
    profileData.avatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
  } finally {
    loading.value = false;
    console.log('获取个人资料完成');
  }
}

// 头像上传相关方法
const handleAvatarSuccess = (response: ApiResponse<{url: string}>) => {
  if (response.code === 200 && response.data) {
    // 更新表单数据中的头像地址
    profileData.avatar = response.data.url;
    
    // 同时更新到user store，确保界面立即刷新
    userStore.updateUserInfo({
      avatar: response.data.url
    });
    
    // 提示上传成功
    ElMessage.success('头像上传成功');
    
    // 异步更新到IndexedDB
    try {
      const userId = localStorage.getItem('currentUserId');
      if (userId) {
        getCurrentUserInfo().then(userInfoFromDB => {
          if (userInfoFromDB) {
            const updatedUserInfo = {
              ...userInfoFromDB,
              avatar: response.data.url
            };
            saveUserInfo(updatedUserInfo).then(() => {
              console.log('头像已保存到IndexedDB');
            });
          }
        });
      }
    } catch (error) {
      console.error('保存头像到IndexedDB失败:', error);
    }
  } else {
    ElMessage.error('头像上传失败');
  }
}

const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 保存个人资料
const handleSave = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    
    console.log('开始保存个人资料:', profileData);
    
    // 1. 保存到后端API
    try {
      const response = await profileApi.updateProfile(profileData);
      console.log('个人资料API保存结果:', response);
      
      if (!response || (typeof response === 'object' && 'code' in response && response.code !== 200)) {
        const errorMsg = response && typeof response === 'object' && 'message' in response ? response.message : '保存失败';
        throw new Error(errorMsg as string);
      }
    } catch (apiError: any) {
      console.error('保存到API失败:', apiError);
      ElMessage.error(apiError.message || '保存到服务器失败');
      // 即使API保存失败，也继续尝试保存到IndexedDB
    }
    
    // 2. 同步保存到IndexedDB
    try {
      const userId = localStorage.getItem('currentUserId');
      
      if (userId) {
        // 获取现有的用户信息
        const existingUserInfo = await getCurrentUserInfo();
        
        if (existingUserInfo) {
          // 更新现有数据
          const updatedUserInfo = {
            ...existingUserInfo,
            name: profileData.name,
            gender: profileData.gender,
            phoneNumber: profileData.phoneNumber,
            bio: profileData.bio,
            birthday: profileData.birthday,
            avatar: profileData.avatar
          };
          
          await saveUserInfo(updatedUserInfo);
          console.log('已保存更新后的个人资料到IndexedDB:', updatedUserInfo);
          
          // 更新用户信息到store
          userStore.updateUserInfo({
            name: updatedUserInfo.name || '',
            avatar: updatedUserInfo.avatar
          });
        } else {
          console.warn('未找到现有用户信息，无法更新IndexedDB');
        }
      } else {
        console.warn('未找到当前用户ID，无法更新IndexedDB');
      }
    } catch (dbError) {
      console.error('保存到IndexedDB失败:', dbError);
      ElMessage.warning('本地存储更新失败，下次访问可能需要重新加载数据');
    }
    
    ElMessage.success('个人资料保存成功');
    
    // 保存当前数据到临时对象（用于取消编辑时还原）
    Object.assign(tempProfileData, profileData);
    
    // 退出编辑模式
    isEditing.value = false;
  } catch (validationError: any) {
    console.error('表单验证失败:', validationError);
    ElMessage.error('请检查表单填写是否正确');
  } finally {
    loading.value = false;
  }
}

// 安全设置相关方法
const handleChangePassword = () => {
  ElMessageBox.prompt('请输入新密码', '修改密码', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputType: 'password',
    inputValidator: (value) => {
      return value.length >= 6 || '密码长度不能小于6位'
    }
  }).then(({ value }) => {
    ElMessage.success('密码修改成功')
  }).catch(() => {
    // 取消操作
  })
}

const handleChangePhone = () => {
  ElMessageBox.prompt('请输入新手机号', '更换手机', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputPattern: /^1[3-9]\d{9}$/,
    inputErrorMessage: '请输入正确的手机号格式'
  }).then(({ value }) => {
    profileData.phoneNumber = value
    ElMessage.success('手机号更新成功')
  }).catch(() => {
    // 取消操作
  })
}

const handleChangeEmail = () => {
  ElMessageBox.prompt('请输入新邮箱', '更换邮箱', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputPattern: /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/,
    inputErrorMessage: '请输入正确的邮箱格式'
  }).then(({ value }) => {
    ElMessage.warning('由于邮箱是账号的唯一标识，更换邮箱需要联系管理员处理')
  }).catch(() => {
    // 取消操作
  })
}

const handleDeleteAccount = () => {
  ElMessageBox.confirm(
    '账号注销后将无法恢复，确认要注销账号吗？',
    '注销账号',
    {
      confirmButtonText: '确认注销',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.warning('注销账号需要联系管理员处理')
  }).catch(() => {
    // 取消操作
  })
}

// 页面加载时获取个人资料
onMounted(() => {
  fetchProfile()
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/responsive' as responsive;

.profile-view {
  padding: 20px;
  min-height: calc(100vh - 60px); // 减去顶部导航栏的高度
  position: relative;
  background-color: var(--el-bg-color);
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    position: sticky;
    top: 0;
    z-index: 1;
    background-color: var(--el-bg-color);

    h1 {
      margin: 0;
      font-size: 24px;
      color: var(--el-text-color-primary);
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  .profile-container {
    position: relative;
    z-index: 0;
    
    .profile-card {
      .avatar-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 20px;

        .el-avatar {
          margin-bottom: 16px;
        }
      }

      .user-stats {
        display: flex;
        justify-content: space-around;
        margin-top: 20px;

        .stat-item {
          text-align: center;

          .stat-value {
            font-size: 24px;
            font-weight: bold;
            color: var(--el-color-primary);
          }

          .stat-label {
            font-size: 14px;
            color: var(--el-text-color-secondary);
          }
        }
      }
    }

    .info-card {
      margin-bottom: 20px;

      .info-text {
        line-height: 32px;
        color: var(--el-text-color-regular);
      }
    }

    .security-card {
      .security-list {
        .security-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 16px 0;
          border-bottom: 1px solid var(--el-border-color-lighter);

          &:last-child {
            border-bottom: none;
          }

          .item-info {
            h4 {
              margin: 0 0 8px;
              font-size: 16px;
              color: var(--el-text-color-primary);
            }

            p {
              margin: 0;
              font-size: 14px;
              color: var(--el-text-color-secondary);
            }
          }
        }
      }
    }
  }
}

@include responsive.respond-to('md') {
  .profile-view {
    .profile-container {
      .el-col {
        width: 100%;
        margin-bottom: 20px;
      }
    }
  }
}
</style>