import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/chat-content',
      name: 'chat-content',
      component: () => import('../views/ChatContentView.vue'),
      meta: {
        title: '对话内容管理',
      },
    },
    {
      path: '/user-feedback',
      name: 'user-feedback',
      component: () => import('../views/UserFeedbackView.vue'),
      meta: {
        title: '用户反馈管理',
      },
    },
    {
      path: '/token-stats',
      name: 'token-stats',
      component: () => import('../views/TokenStatsView.vue'),
      meta: {
        title: 'Token消费统计',
      },
    },
    {
      path: '/image-analysis',
      name: 'image-analysis',
      component: () => import('../views/ImageAnalysisView.vue'),
      meta: {
        title: '图片解析管理',
      },
    },
    {
      path: '/ocr',
      name: 'ocr',
      component: () => import('../views/OcrWebsocketView.vue'),
      meta: {
        title: 'OCR识别',
      },
    },
    {
      path: '/emotion-radar',
      name: 'emotion-radar',
      component: () => import('../views/EmotionRadarView.vue'),
      meta: {
        title: '情感雷达',
      },
    },
  ],
})

export default router
