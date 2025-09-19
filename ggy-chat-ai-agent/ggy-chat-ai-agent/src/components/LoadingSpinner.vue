<template>
  <div class="loading-spinner-container" :class="{ 'fullscreen': fullscreen, [`theme-${theme}`]: true }">
    <div class="loading-content">
      <div class="spinner-wrapper">
        <!-- 现代化的旋转加载器 -->
        <div v-if="type === 'spin'" class="modern-spinner">
          <div class="spinner-ring">
            <div class="ring-segment"></div>
            <div class="ring-segment"></div>
            <div class="ring-segment"></div>
            <div class="ring-segment"></div>
          </div>
        </div>
        
        <!-- 脉冲点动画 -->
        <div v-else-if="type === 'pulse'" class="pulse-spinner">
          <div class="pulse-dot"></div>
          <div class="pulse-dot"></div>
          <div class="pulse-dot"></div>
        </div>
        
        <!-- 波浪动画 -->
        <div v-else-if="type === 'wave'" class="wave-spinner">
          <div class="wave-bar"></div>
          <div class="wave-bar"></div>
          <div class="wave-bar"></div>
          <div class="wave-bar"></div>
          <div class="wave-bar"></div>
        </div>
        
        <!-- 默认弹跳动画 -->
        <div v-else class="custom-spinner">
          <div class="spinner-circle"></div>
          <div class="spinner-circle"></div>
          <div class="spinner-circle"></div>
        </div>
      </div>
      <div class="loading-text" v-if="text">{{ text }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  text?: string
  fullscreen?: boolean
  type?: 'bounce' | 'spin' | 'pulse' | 'wave'
  theme?: 'primary' | 'gradient' | 'elegant'
}

withDefaults(defineProps<Props>(), {
  text: '加载中...',
  fullscreen: false,
  type: 'bounce',
  theme: 'gradient'
})
</script>

<style scoped>
.loading-spinner-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  min-height: 200px;
  transition: all 0.3s ease;
}

.loading-spinner-container.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  z-index: 9999;
  min-height: 100vh;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.spinner-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 原有弹跳动画 - 增强版 */
.custom-spinner {
  display: flex;
  gap: 8px;
  align-items: center;
}

.spinner-circle {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite both;
}

/* 主题色彩系统 */
.theme-primary .spinner-circle {
  background: #1890ff;
}

.theme-gradient .spinner-circle {
  background: linear-gradient(135deg, #1890ff, #52c41a);
}

.theme-elegant .spinner-circle {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.spinner-circle:nth-child(1) { animation-delay: -0.32s; }
.spinner-circle:nth-child(2) { animation-delay: -0.16s; }
.spinner-circle:nth-child(3) { animation-delay: 0s; }

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0.6) translateY(0);
    opacity: 0.4;
  }
  40% {
    transform: scale(1.2) translateY(-10px);
    opacity: 1;
  }
}

/* 现代旋转加载器 */
.modern-spinner {
  position: relative;
  width: 48px;
  height: 48px;
}

.spinner-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  animation: spin 1.2s linear infinite;
}

.ring-segment {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 3px solid transparent;
}

.theme-primary .ring-segment:nth-child(1) {
  border-top-color: #1890ff;
  animation: spin 1.2s linear infinite;
}

.theme-gradient .ring-segment:nth-child(1) {
  border-top-color: #1890ff;
  animation: spin 1.2s linear infinite;
}

.theme-gradient .ring-segment:nth-child(2) {
  border-right-color: #52c41a;
  animation: spin 1.2s linear infinite reverse;
  animation-delay: -0.3s;
}

.theme-elegant .ring-segment:nth-child(1) {
  border-top-color: #667eea;
  animation: spin 1.2s linear infinite;
}

.theme-elegant .ring-segment:nth-child(2) {
  border-right-color: #764ba2;
  animation: spin 1.2s linear infinite reverse;
  animation-delay: -0.3s;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 脉冲点动画 */
.pulse-spinner {
  display: flex;
  gap: 6px;
  align-items: center;
}

.pulse-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: pulseScale 1.5s ease-in-out infinite;
}

.theme-primary .pulse-dot {
  background: #1890ff;
}

.theme-gradient .pulse-dot:nth-child(1) { background: #1890ff; }
.theme-gradient .pulse-dot:nth-child(2) { background: #13c2c2; }
.theme-gradient .pulse-dot:nth-child(3) { background: #52c41a; }

.theme-elegant .pulse-dot:nth-child(1) { background: #667eea; }
.theme-elegant .pulse-dot:nth-child(2) { background: #6a7fdb; }
.theme-elegant .pulse-dot:nth-child(3) { background: #764ba2; }

.pulse-dot:nth-child(1) { animation-delay: 0s; }
.pulse-dot:nth-child(2) { animation-delay: 0.2s; }
.pulse-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes pulseScale {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1.3);
    opacity: 1;
  }
}

/* 波浪动画 */
.wave-spinner {
  display: flex;
  gap: 4px;
  align-items: flex-end;
  height: 32px;
}

.wave-bar {
  width: 4px;
  height: 8px;
  border-radius: 2px;
  animation: waveHeight 1.2s ease-in-out infinite;
}

.theme-primary .wave-bar {
  background: #1890ff;
}

.theme-gradient .wave-bar:nth-child(1) { background: linear-gradient(to top, #1890ff, #40a9ff); }
.theme-gradient .wave-bar:nth-child(2) { background: linear-gradient(to top, #13c2c2, #36cfc9); }
.theme-gradient .wave-bar:nth-child(3) { background: linear-gradient(to top, #52c41a, #73d13d); }
.theme-gradient .wave-bar:nth-child(4) { background: linear-gradient(to top, #faad14, #ffc53d); }
.theme-gradient .wave-bar:nth-child(5) { background: linear-gradient(to top, #f759ab, #ff85c0); }

.theme-elegant .wave-bar:nth-child(1) { background: linear-gradient(to top, #667eea, #7b8cec); }
.theme-elegant .wave-bar:nth-child(2) { background: linear-gradient(to top, #6a7fdb, #7f94dd); }
.theme-elegant .wave-bar:nth-child(3) { background: linear-gradient(to top, #6e8bdc, #839cde); }
.theme-elegant .wave-bar:nth-child(4) { background: linear-gradient(to top, #7297dd, #87a4df); }
.theme-elegant .wave-bar:nth-child(5) { background: linear-gradient(to top, #764ba2, #8a5fb5); }

.wave-bar:nth-child(1) { animation-delay: 0s; }
.wave-bar:nth-child(2) { animation-delay: 0.1s; }
.wave-bar:nth-child(3) { animation-delay: 0.2s; }
.wave-bar:nth-child(4) { animation-delay: 0.3s; }
.wave-bar:nth-child(5) { animation-delay: 0.4s; }

@keyframes waveHeight {
  0%, 100% {
    height: 8px;
    transform: scaleY(1);
  }
  50% {
    height: 24px;
    transform: scaleY(1.5);
  }
}

/* 加载文本样式 */
.loading-text {
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 0.5px;
  animation: textPulse 2s ease-in-out infinite;
  background: linear-gradient(135deg, #1890ff, #52c41a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-align: center;
}

.theme-primary .loading-text {
  color: #1890ff;
  background: none;
  -webkit-text-fill-color: #1890ff;
}

.theme-elegant .loading-text {
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes textPulse {
  0%, 100% {
    opacity: 0.8;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.02);
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .loading-spinner-container.fullscreen {
    background: rgba(0, 0, 0, 0.85);
  }
  
  .loading-text {
    filter: brightness(1.2);
  }
  
  .theme-primary .loading-text {
    color: #40a9ff;
    -webkit-text-fill-color: #40a9ff;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .loading-spinner-container {
    padding: 30px 15px;
    min-height: 150px;
  }
  
  .modern-spinner {
    width: 36px;
    height: 36px;
  }
  
  .spinner-circle {
    width: 12px;
    height: 12px;
  }
  
  .pulse-dot {
    width: 10px;
    height: 10px;
  }
  
  .wave-spinner {
    height: 24px;
  }
  
  .wave-bar {
    width: 3px;
  }
  
  .loading-text {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .loading-content {
    gap: 16px;
  }
  
  .modern-spinner {
    width: 32px;
    height: 32px;
  }
  
  .loading-text {
    font-size: 13px;
  }
}

/* 高性能动画优化 */
.spinner-wrapper * {
  will-change: transform, opacity;
}

/* 减少动画的性能影响 */
@media (prefers-reduced-motion: reduce) {
  .spinner-circle,
  .spinner-ring,
  .pulse-dot,
  .wave-bar,
  .loading-text,
  .loading-content {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
</style>
