// 教师模块路由配置
export default [
  {
    path: '/teacher',
    component: () => import('@/layout/TeacherLayout.vue'),
    meta: { requiresAuth: true, role: 'TEACHER' },
    children: [
      // 仪表盘
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: () => import('@/views/teacher/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'fas fa-tachometer-alt' }
      },
      
      // 个人中心
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: () => import('@/views/teacher/profile/ProfileView.vue'),
        meta: { title: '个人资料', icon: 'fas fa-user' }
      },
      
      // 课程管理
      {
        path: 'courses',
        name: 'TeacherCourses',
        component: () => import('@/views/teacher/courses/CoursesView.vue'),
        meta: { title: '课程列表', icon: 'fas fa-book' }
      },
      {
        path: 'courses/create',
        name: 'TeacherCreateCourse',
        component: () => import('@/views/teacher/courses/CreateCourseView.vue'),
        meta: { title: '创建课程', icon: 'fas fa-plus' }
      },
      {
        path: 'courses/:id',
        name: 'TeacherCourseDetail',
        component: () => import('@/views/teacher/courses/CourseDetailView.vue'),
        meta: { title: '课程详情', icon: 'fas fa-info-circle' }
      },
      {
        path: 'courses/:id/edit',
        name: 'TeacherEditCourse',
        component: () => import('@/views/teacher/courses/EditCourseView.vue'),
        meta: { title: '编辑课程', icon: 'fas fa-edit' }
      },
      {
        path: 'courses/:id/chapters',
        name: 'TeacherCourseChapters',
        component: () => import('@/views/teacher/courses/ChaptersView.vue'),
        meta: { title: '章节管理', icon: 'fas fa-list' }
      },
      {
        path: 'courses/:id/statistics',
        name: 'TeacherCourseStatistics',
        component: () => import('@/views/teacher/courses/StatisticsView.vue'),
        meta: { title: '课程统计', icon: 'fas fa-chart-bar' }
      },
      
      // 作业管理
      {
        path: 'assignments',
        name: 'TeacherAssignments',
        component: () => import('@/views/teacher/assignments/AssignmentsView.vue'),
        meta: { title: '作业列表', icon: 'fas fa-tasks' }
      },
      {
        path: 'assignments/create',
        name: 'TeacherCreateAssignment',
        component: () => import('@/views/teacher/assignments/CreateAssignmentView.vue'),
        meta: { title: '创建作业', icon: 'fas fa-plus' }
      },
      {
        path: 'assignments/:id/edit',
        name: 'TeacherEditAssignment',
        component: () => import('@/views/teacher/assignments/EditAssignmentView.vue'),
        meta: { title: '编辑作业', icon: 'fas fa-edit' }
      },
      {
        path: 'assignments/:id/submissions',
        name: 'TeacherAssignmentSubmissions',
        component: () => import('@/views/teacher/assignments/SubmissionsView.vue'),
        meta: { title: '提交列表', icon: 'fas fa-list' }
      },
      {
        path: 'assignments/submissions/:id/grade',
        name: 'TeacherGradeAssignment',
        component: () => import('@/views/teacher/assignments/GradeView.vue'),
        meta: { title: '批改作业', icon: 'fas fa-check' }
      },
      {
        path: 'assignments/statistics',
        name: 'TeacherAssignmentStatistics',
        component: () => import('@/views/teacher/assignments/StatisticsView.vue'),
        meta: { title: '作业统计', icon: 'fas fa-chart-bar' }
      },
      
      // 考试管理
      {
        path: 'exams',
        name: 'TeacherExams',
        component: () => import('@/views/teacher/exams/ExamsView.vue'),
        meta: { title: '考试列表', icon: 'fas fa-file-alt' }
      },
      {
        path: 'exams/create',
        name: 'TeacherCreateExam',
        component: () => import('@/views/teacher/exams/CreateExamView.vue'),
        meta: { title: '创建考试', icon: 'fas fa-plus' }
      },
      {
        path: 'exams/:id/edit',
        name: 'TeacherEditExam',
        component: () => import('@/views/teacher/exams/EditExamView.vue'),
        meta: { title: '编辑考试', icon: 'fas fa-edit' }
      },
      {
        path: 'exams/:id/submissions',
        name: 'TeacherExamSubmissions',
        component: () => import('@/views/teacher/exams/SubmissionsView.vue'),
        meta: { title: '提交列表', icon: 'fas fa-list' }
      },
      {
        path: 'exams/submissions/:id/grade',
        name: 'TeacherGradeExam',
        component: () => import('@/views/teacher/exams/GradeView.vue'),
        meta: { title: '批改考试', icon: 'fas fa-check' }
      },
      {
        path: 'exams/:id/statistics',
        name: 'TeacherExamStatistics',
        component: () => import('@/views/teacher/exams/StatisticsView.vue'),
        meta: { title: '考试统计', icon: 'fas fa-chart-bar' }
      },
      {
        path: 'exams/:id/ranking',
        name: 'TeacherExamRanking',
        component: () => import('@/views/teacher/exams/RankingView.vue'),
        meta: { title: '考试排名', icon: 'fas fa-trophy' }
      },
      
      // 学生管理
      {
        path: 'students',
        name: 'TeacherStudents',
        component: () => import('@/views/teacher/students/StudentsView.vue'),
        meta: { title: '学生列表', icon: 'fas fa-users' }
      },
      {
        path: 'students/:id',
        name: 'TeacherStudentDetail',
        component: () => import('@/views/teacher/students/StudentDetailView.vue'),
        meta: { title: '学生详情', icon: 'fas fa-user' }
      },
      {
        path: 'courses/:id/students',
        name: 'TeacherCourseStudents',
        component: () => import('@/views/teacher/students/CourseStudentsView.vue'),
        meta: { title: '课程学生', icon: 'fas fa-user-graduate' }
      },
      {
        path: 'students/groups',
        name: 'TeacherStudentGroups',
        component: () => import('@/views/teacher/students/GroupsView.vue'),
        meta: { title: '学生分组', icon: 'fas fa-users-cog' }
      },
      {
        path: 'students/groups/create',
        name: 'TeacherCreateStudentGroup',
        component: () => import('@/views/teacher/students/CreateGroupView.vue'),
        meta: { title: '创建分组', icon: 'fas fa-plus' }
      },
      {
        path: 'students/groups/:id/edit',
        name: 'TeacherEditStudentGroup',
        component: () => import('@/views/teacher/students/EditGroupView.vue'),
        meta: { title: '编辑分组', icon: 'fas fa-edit' }
      },
      
      // 资源管理
      {
        path: 'resources',
        name: 'TeacherResources',
        component: () => import('@/views/teacher/resources/ResourcesView.vue'),
        meta: { title: '资源列表', icon: 'fas fa-folder' }
      },
      {
        path: 'resources/create',
        name: 'TeacherCreateResource',
        component: () => import('@/views/teacher/resources/CreateResourceView.vue'),
        meta: { title: '创建资源', icon: 'fas fa-plus' }
      },
      {
        path: 'resources/:id/edit',
        name: 'TeacherEditResource',
        component: () => import('@/views/teacher/resources/EditResourceView.vue'),
        meta: { title: '编辑资源', icon: 'fas fa-edit' }
      },
      {
        path: 'resources/categories',
        name: 'TeacherResourceCategories',
        component: () => import('@/views/teacher/resources/CategoriesView.vue'),
        meta: { title: '分类管理', icon: 'fas fa-tags' }
      },
      {
        path: 'resources/statistics',
        name: 'TeacherResourceStatistics',
        component: () => import('@/views/teacher/resources/StatisticsView.vue'),
        meta: { title: '资源统计', icon: 'fas fa-chart-bar' }
      },
      
      // 待办事项
      {
        path: 'todos',
        name: 'TeacherTodos',
        component: () => import('@/views/teacher/todos/TodosView.vue'),
        meta: { title: '待办列表', icon: 'fas fa-check-square' }
      },
      {
        path: 'todos/create',
        name: 'TeacherCreateTodo',
        component: () => import('@/views/teacher/todos/CreateTodoView.vue'),
        meta: { title: '创建待办', icon: 'fas fa-plus' }
      }
    ]
  }
];
