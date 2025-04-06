export interface Assignment {
  id: number
  title: string
  description: string
  status: 'PENDING' | 'SUBMITTED' | 'GRADED'
  score?: number
  feedback?: string
  dueDate: string
  submissionDate?: string
  createdAt: string
  updatedAt: string
  email: string
  courseId?: string
  courseName?: string
  teacherName?: string
  difficulty?: number
  estimatedTime?: number
  questionCount?: number
  totalPoints?: number
  correctRate?: number
  ranking?: string
  hasFeedback?: boolean
  teacherComment?: string
} 