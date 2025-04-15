import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 页面组件
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ForgotPasswordView from '@/views/ForgotPasswordView.vue'
import DashboardView from '@/views/DashboardView.vue'
import SearchView from '@/views/SearchView.vue'
import CoursesView from '@/views/CoursesView.vue'
import CourseDetailView from '@/views/CourseDetailView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import EnrolledCoursesView from '@/views/EnrolledCoursesView.vue'
import CompletedCoursesView from '@/views/CompletedCoursesView.vue'

// 引入管理员布局组件
import AdminLayout from '@/components/AdminLayout.vue'

// 引入布局组件
import Layout from '@/layout/index.vue'

// 路由配置
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: {
      title: '注册',
      requiresAuth: false
    }
  },
  {
    path: '/forgot-password',
    name: 'forgot-password',
    component: ForgotPasswordView,
    meta: {
      title: '忘记密码',
      requiresAuth: false
    }
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: DashboardView,
        meta: {
          title: '学习中心',
          requiresAuth: true
        }
      },
      {
        path: 'search',
        name: 'search',
        component: SearchView,
        meta: {
          title: '资源搜索',
          requiresAuth: true
        }
      },
      {
        path: 'courses',
        component: CoursesView,
        children: [
          {
            path: '',
            name: 'enrolled-courses',
            redirect: '/courses/enrolled'
          },
          {
            path: 'enrolled',
            name: 'enrolled-courses',
            component: EnrolledCoursesView,
            meta: {
              requiresAuth: true,
              title: '已选课程'
            }
          },
          {
            path: 'completed',
            name: 'completed-courses',
            component: CompletedCoursesView,
            meta: {
              requiresAuth: true,
              title: '已完成课程'
            }
          }
        ],
        meta: {
          requiresAuth: true,
          title: '我的课程'
        }
      },
      {
        path: 'courses/:id',
        name: 'course-detail',
        component: CourseDetailView,
        meta: {
          title: '课程详情',
          requiresAuth: true
        }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/FavoritesView.vue'),
        meta: {
          title: '学习收藏',
          requiresAuth: true
        }
      },
      {
        path: 'ai-assistant',
        name: 'AIAssistant',
        component: () => import('@/views/AIAssistantView.vue'),
        meta: {
          title: 'AI学习助手',
          requiresAuth: true
        }
      },
      {
        path: 'resource-library',
        name: 'ResourceLibrary',
        component: () => import('@/views/ResourceLibraryView.vue'),
        meta: {
          title: '资源库',
          requiresAuth: true
        }
      },
      {
        path: 'assessments',
        name: 'Assessments',
        component: () => import('@/views/AssessmentsView.vue'),
        meta: {
          title: '测评中心',
          requiresAuth: true
        }
      },
      {
        path: 'exams',
        name: 'Exams',
        component: () => import('@/views/ExamsView.vue'),
        meta: {
          title: '在线测试',
          requiresAuth: true
        }
      },
      {
        path: 'exams/:id',
        name: 'ExamDetail',
        component: () => import('@/views/ExamDetailView.vue'),
        meta: {
          title: '测试详情',
          requiresAuth: true
        }
      },
      {
        path: 'exams/:id/take',
        name: 'TakeExam',
        component: () => import('@/views/TakeExamView.vue'),
        meta: {
          title: '参加测试',
          requiresAuth: true
        }
      },
      {
        path: 'exams/:id/result',
        name: 'ExamResult',
        component: () => import('@/views/ExamResultView.vue'),
        meta: {
          title: '测试结果',
          requiresAuth: true
        }
      },
      {
        path: 'assignments',
        name: 'Assignments',
        component: () => import('@/views/AssignmentsView.vue'),
        meta: {
          title: '在线作业',
          requiresAuth: true
        }
      },
      {
        path: 'assignments/:id',
        name: 'AssignmentDetail',
        component: () => import('@/views/AssignmentDetailView.vue'),
        meta: {
          title: '作业详情',
          requiresAuth: true
        }
      },
      {
        path: 'assignments/:id/submit',
        name: 'SubmitAssignment',
        component: () => import('@/views/SubmitAssignmentView.vue'),
        meta: {
          title: '提交作业',
          requiresAuth: true
        }
      },
      {
        path: 'assignments/:id/feedback',
        name: 'AssignmentFeedback',
        component: () => import('@/views/AssignmentFeedbackView.vue'),
        meta: {
          title: '作业反馈',
          requiresAuth: true
        }
      },
      {
        path: 'community',
        name: 'Community',
        component: () => import('@/views/CommunityView.vue'),
        meta: {
          title: '学习社区',
          requiresAuth: true
        }
      },
      {
        path: 'analytics',
        name: 'Analytics',
        component: () => import('@/views/AnalyticsView.vue'),
        meta: {
          title: '学习分析',
          requiresAuth: true
        }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/ProfileView.vue'),
        meta: {
          title: '个人中心',
          requiresAuth: true
        }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/SettingsView.vue'),
        meta: {
          title: '系统设置',
          requiresAuth: true
        }
      },
      {
        path: 'course-catalog',
        name: 'course-catalog',
        component: () => import('@/views/CourseCatalogView.vue'),
        meta: {
          requiresAuth: true,
          title: '课程目录'
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: {
      title: '页面未找到',
      requiresAuth: false
    }
  },
  // 管理员路由
  {
    path: '/admin',
    component: AdminLayout,
    meta: {
      requiresAuth: true,
      role: '管理员', // 需要管理员角色
      title: '管理后台'
    },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: () => import('@/views/admin/DashboardView.vue'),
        meta: {
          title: '控制面板',
          requiresAuth: true,
          role: '管理员'
        }
      },
      {
        path: 'users',
        name: 'admin-users',
        component: () => import('@/views/admin/UsersView.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true,
          role: '管理员'
        }
      },
      {
        path: 'users/add',
        name: 'admin-users-add',
        component: () => import('@/views/admin/AddUserView.vue'),
        meta: {
          title: '添加用户',
          requiresAuth: true,
          role: '管理员'
        }
      },
      {
        path: 'courses',
        name: 'admin-courses',
        component: () => import('@/views/admin/CoursesView.vue'),
        meta: {
          title: '课程管理',
          requiresAuth: true,
          role: '管理员'
        }
      },
      {
        path: 'courses/approval',
        name: 'admin-courses-approval',
        component: () => import('@/views/admin/CourseApprovalView.vue'),
        meta: {
          title: '课程审核',
          requiresAuth: true,
          role: '管理员'
        }
      },
      {
        path: 'settings',
        name: 'admin-settings',
        component: () => import('@/views/admin/SettingsView.vue'),
        meta: {
          title: '系统设置',
          requiresAuth: true,
          role: '管理员'
        }
      }
    ]
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  console.log(`路由跳转: 从 ${from.fullPath} 到 ${to.fullPath}`)

  const userStore = useUserStore()
  console.log('当前用户登录状态:', userStore.isLoggedIn)
  console.log('当前用户信息:', userStore.userInfo)

  // 设置页面标题
  document.title = `${to.meta.title} - 智能学习平台`

  // 检查是否需要认证 - 只检查requiresAuth
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    console.log('需要认证但用户未登录，重定向到登录页面')
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }

  // 检查角色权限
  if (to.meta.role && userStore.userInfo && userStore.userInfo.role !== to.meta.role) {
    console.log('用户角色不匹配，重定向到404页面')
    console.log('需要角色:', to.meta.role, '用户角色:', userStore.userInfo.role)
    next({ name: 'not-found' })
    return
  }

  console.log('路由检查通过，继续导航')
  next()
})

export default router
