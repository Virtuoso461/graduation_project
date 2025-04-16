// 学生模块路由配置
export default [
  {
    path: '/student',
    component: () => import('@/layout/StudentLayout.vue'),
    meta: { requiresAuth: true, role: 'STUDENT' },
    children: [
      // 仪表盘
      {
        path: 'dashboard',
        name: 'StudentDashboard',
        component: () => import('@/views/student/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'fas fa-tachometer-alt' }
      },
      {
        path: 'overview',
        name: 'StudentOverview',
        component: () => import('@/views/student/dashboard/OverviewView.vue'),
        meta: { title: '学习概览', icon: 'fas fa-chart-line' }
      },
      
      // 个人中心
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/profile/ProfileView.vue'),
        meta: { title: '个人资料', icon: 'fas fa-user' }
      },
      {
        path: 'learning-records',
        name: 'StudentLearningRecords',
        component: () => import('@/views/student/profile/LearningRecordsView.vue'),
        meta: { title: '学习记录', icon: 'fas fa-history' }
      },
      {
        path: 'statistics',
        name: 'StudentStatistics',
        component: () => import('@/views/student/profile/StatisticsView.vue'),
        meta: { title: '学习统计', icon: 'fas fa-chart-bar' }
      },
      {
        path: 'trend',
        name: 'StudentTrend',
        component: () => import('@/views/student/profile/TrendView.vue'),
        meta: { title: '学习趋势', icon: 'fas fa-chart-line' }
      },
      
      // 作业模块
      {
        path: 'assignments',
        name: 'StudentAssignments',
        component: () => import('@/views/student/assignments/AssignmentsView.vue'),
        meta: { title: '作业列表', icon: 'fas fa-tasks' }
      },
      {
        path: 'assignments/:id',
        name: 'StudentAssignmentDetail',
        component: () => import('@/views/student/assignments/AssignmentDetailView.vue'),
        meta: { title: '作业详情', icon: 'fas fa-file-alt' }
      },
      {
        path: 'assignments/:id/submit',
        name: 'StudentSubmitAssignment',
        component: () => import('@/views/student/assignments/SubmitView.vue'),
        meta: { title: '提交作业', icon: 'fas fa-paper-plane' }
      },
      {
        path: 'assignments/history',
        name: 'StudentAssignmentHistory',
        component: () => import('@/views/student/assignments/HistoryView.vue'),
        meta: { title: '提交历史', icon: 'fas fa-history' }
      },
      {
        path: 'assignments/completed',
        name: 'StudentCompletedAssignments',
        component: () => import('@/views/student/assignments/CompletedView.vue'),
        meta: { title: '已完成作业', icon: 'fas fa-check-circle' }
      },
      
      // 考试模块
      {
        path: 'exams',
        name: 'StudentExams',
        component: () => import('@/views/student/exams/ExamsView.vue'),
        meta: { title: '考试列表', icon: 'fas fa-file-alt' }
      },
      {
        path: 'exams/:id',
        name: 'StudentExamDetail',
        component: () => import('@/views/student/exams/ExamDetailView.vue'),
        meta: { title: '考试详情', icon: 'fas fa-info-circle' }
      },
      {
        path: 'exams/:id/take',
        name: 'StudentTakeExam',
        component: () => import('@/views/student/exams/TakeExamView.vue'),
        meta: { title: '参加考试', icon: 'fas fa-edit' }
      },
      {
        path: 'exams/:id/result',
        name: 'StudentExamResult',
        component: () => import('@/views/student/exams/ResultView.vue'),
        meta: { title: '考试结果', icon: 'fas fa-poll' }
      },
      {
        path: 'exams/history',
        name: 'StudentExamHistory',
        component: () => import('@/views/student/exams/HistoryView.vue'),
        meta: { title: '考试历史', icon: 'fas fa-history' }
      },
      {
        path: 'exams/wrong-questions',
        name: 'StudentWrongQuestions',
        component: () => import('@/views/student/exams/WrongQuestionsView.vue'),
        meta: { title: '错题集', icon: 'fas fa-times-circle' }
      },
      {
        path: 'exams/knowledge-points',
        name: 'StudentKnowledgePoints',
        component: () => import('@/views/student/exams/KnowledgePointsView.vue'),
        meta: { title: '知识点掌握', icon: 'fas fa-brain' }
      },
      {
        path: 'exams/:id/ranking',
        name: 'StudentExamRanking',
        component: () => import('@/views/student/exams/RankingView.vue'),
        meta: { title: '考试排名', icon: 'fas fa-trophy' }
      },
      
      // 社区模块
      {
        path: 'community',
        name: 'StudentCommunity',
        component: () => import('@/views/student/community/CommunityView.vue'),
        meta: { title: '学习社区', icon: 'fas fa-comments' }
      },
      {
        path: 'community/posts',
        name: 'StudentPosts',
        component: () => import('@/views/student/community/PostsView.vue'),
        meta: { title: '帖子列表', icon: 'fas fa-list' }
      },
      {
        path: 'community/posts/:id',
        name: 'StudentPostDetail',
        component: () => import('@/views/student/community/PostDetailView.vue'),
        meta: { title: '帖子详情', icon: 'fas fa-file-alt' }
      },
      {
        path: 'community/create-post',
        name: 'StudentCreatePost',
        component: () => import('@/views/student/community/CreatePostView.vue'),
        meta: { title: '创建帖子', icon: 'fas fa-plus' }
      },
      {
        path: 'community/edit-post/:id',
        name: 'StudentEditPost',
        component: () => import('@/views/student/community/EditPostView.vue'),
        meta: { title: '编辑帖子', icon: 'fas fa-edit' }
      },
      {
        path: 'community/my-posts',
        name: 'StudentMyPosts',
        component: () => import('@/views/student/community/MyPostsView.vue'),
        meta: { title: '我的帖子', icon: 'fas fa-user-edit' }
      },
      {
        path: 'community/my-replies',
        name: 'StudentMyReplies',
        component: () => import('@/views/student/community/MyRepliesView.vue'),
        meta: { title: '我的回复', icon: 'fas fa-reply' }
      },
      
      // 资源模块
      {
        path: 'resources',
        name: 'StudentResources',
        component: () => import('@/views/student/resources/ResourcesView.vue'),
        meta: { title: '学习资源', icon: 'fas fa-folder' }
      },
      {
        path: 'resources/:id',
        name: 'StudentResourceDetail',
        component: () => import('@/views/student/resources/ResourceDetailView.vue'),
        meta: { title: '资源详情', icon: 'fas fa-file' }
      },
      {
        path: 'resources/collections',
        name: 'StudentResourceCollections',
        component: () => import('@/views/student/resources/CollectionsView.vue'),
        meta: { title: '我的收藏', icon: 'fas fa-bookmark' }
      },
      {
        path: 'resources/folders',
        name: 'StudentResourceFolders',
        component: () => import('@/views/student/resources/FoldersView.vue'),
        meta: { title: '收藏夹管理', icon: 'fas fa-folder-open' }
      },
      {
        path: 'resources/hot',
        name: 'StudentHotResources',
        component: () => import('@/views/student/resources/HotResourcesView.vue'),
        meta: { title: '热门资源', icon: 'fas fa-fire' }
      },
      {
        path: 'resources/latest',
        name: 'StudentLatestResources',
        component: () => import('@/views/student/resources/LatestResourcesView.vue'),
        meta: { title: '最新资源', icon: 'fas fa-clock' }
      },
      
      // 课程模块
      {
        path: 'courses',
        name: 'StudentCourses',
        component: () => import('@/views/student/courses/CoursesView.vue'),
        meta: { title: '课程列表', icon: 'fas fa-book' }
      },
      {
        path: 'courses/:id',
        name: 'StudentCourseDetail',
        component: () => import('@/views/student/courses/CourseDetailView.vue'),
        meta: { title: '课程详情', icon: 'fas fa-info-circle' }
      },
      {
        path: 'courses/enrolled',
        name: 'StudentEnrolledCourses',
        component: () => import('@/views/student/courses/EnrolledView.vue'),
        meta: { title: '已选课程', icon: 'fas fa-check' }
      },
      {
        path: 'courses/chapters/:id',
        name: 'StudentChapter',
        component: () => import('@/views/student/courses/ChapterView.vue'),
        meta: { title: '章节学习', icon: 'fas fa-book-open' }
      },
      {
        path: 'courses/:id/progress',
        name: 'StudentCourseProgress',
        component: () => import('@/views/student/courses/ProgressView.vue'),
        meta: { title: '学习进度', icon: 'fas fa-tasks' }
      },
      {
        path: 'courses/transcript',
        name: 'StudentTranscript',
        component: () => import('@/views/student/courses/TranscriptView.vue'),
        meta: { title: '成绩单', icon: 'fas fa-graduation-cap' }
      },
      
      // AI辅导模块
      {
        path: 'ai-guidance',
        name: 'StudentAIGuidance',
        component: () => import('@/views/student/ai-guidance/AskView.vue'),
        meta: { title: 'AI辅导', icon: 'fas fa-robot' }
      },
      {
        path: 'ai-guidance/history',
        name: 'StudentAIGuidanceHistory',
        component: () => import('@/views/student/ai-guidance/HistoryView.vue'),
        meta: { title: '历史记录', icon: 'fas fa-history' }
      }
    ]
  }
];
