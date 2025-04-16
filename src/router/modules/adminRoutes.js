// 管理员模块路由配置
export default [
  {
    path: '/admin',
    component: () => import('@/layout/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      // 仪表盘
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'fas fa-tachometer-alt' }
      },
      
      // 用户管理
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/users/UsersView.vue'),
        meta: { title: '用户列表', icon: 'fas fa-users' }
      },
      {
        path: 'users/create',
        name: 'AdminCreateUser',
        component: () => import('@/views/admin/users/CreateUserView.vue'),
        meta: { title: '创建用户', icon: 'fas fa-user-plus' }
      },
      {
        path: 'users/:id',
        name: 'AdminUserDetail',
        component: () => import('@/views/admin/users/UserDetailView.vue'),
        meta: { title: '用户详情', icon: 'fas fa-user' }
      },
      {
        path: 'users/:id/edit',
        name: 'AdminEditUser',
        component: () => import('@/views/admin/users/EditUserView.vue'),
        meta: { title: '编辑用户', icon: 'fas fa-user-edit' }
      },
      {
        path: 'users/students',
        name: 'AdminStudents',
        component: () => import('@/views/admin/users/StudentsView.vue'),
        meta: { title: '学生管理', icon: 'fas fa-user-graduate' }
      },
      {
        path: 'users/teachers',
        name: 'AdminTeachers',
        component: () => import('@/views/admin/users/TeachersView.vue'),
        meta: { title: '教师管理', icon: 'fas fa-chalkboard-teacher' }
      },
      {
        path: 'users/admins',
        name: 'AdminAdmins',
        component: () => import('@/views/admin/users/AdminsView.vue'),
        meta: { title: '管理员管理', icon: 'fas fa-user-shield' }
      },
      
      // 课程管理
      {
        path: 'courses',
        name: 'AdminCourses',
        component: () => import('@/views/admin/courses/CoursesView.vue'),
        meta: { title: '课程列表', icon: 'fas fa-book' }
      },
      {
        path: 'courses/:id',
        name: 'AdminCourseDetail',
        component: () => import('@/views/admin/courses/CourseDetailView.vue'),
        meta: { title: '课程详情', icon: 'fas fa-info-circle' }
      },
      {
        path: 'courses/:id/approval',
        name: 'AdminCourseApproval',
        component: () => import('@/views/admin/courses/ApprovalView.vue'),
        meta: { title: '课程审核', icon: 'fas fa-check-circle' }
      },
      {
        path: 'courses/categories',
        name: 'AdminCourseCategories',
        component: () => import('@/views/admin/courses/CategoriesView.vue'),
        meta: { title: '分类管理', icon: 'fas fa-tags' }
      },
      {
        path: 'courses/statistics',
        name: 'AdminCourseStatistics',
        component: () => import('@/views/admin/courses/StatisticsView.vue'),
        meta: { title: '课程统计', icon: 'fas fa-chart-bar' }
      },
      
      // 系统设置
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('@/views/admin/settings/SettingsView.vue'),
        meta: { title: '系统设置', icon: 'fas fa-cog' }
      },
      {
        path: 'settings/backup',
        name: 'AdminBackup',
        component: () => import('@/views/admin/settings/BackupView.vue'),
        meta: { title: '备份恢复', icon: 'fas fa-database' }
      }
    ]
  }
];
