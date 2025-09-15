<template>
  <div class="loading-spinner-container" :class="{ 'fullscreen': fullscreen }">
    <div class="loading-content">
      <div class="spinner-wrapper">
        <div class="custom-spinner">
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
}

withDefaults(defineProps<Props>(), {
  text: '加载中...',
  fullscreen: false
})
</script>

<style scoped>
.loading-spinner-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  min-height: 200px;
}

.loading-spinner-container.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
  z-index: 9999;
  min-height: 100vh;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.spinner-wrapper {
  position: relative;
}

.custom-spinner {
  display: flex;
  gap: 8px;
  align-items: center;
}

.spinner-circle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff, #52c41a);
  animation: bounce 1.4s ease-in-out infinite both;
}

.spinner-circle:nth-child(1) {
  animation-delay: -0.32s;
}

.spinner-circle:nth-child(2) {
  animation-delay: -0.16s;
}

.spinner-circle:nth-child(3) {
  animation-delay: 0s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1.2);
    opacity: 1;
  }
}

.loading-text {
  font-size: 14px;
  color: #666;
  font-weight: 500;
  letter-spacing: 0.5px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.7;
  }
  50% {
    opacity: 1;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .loading-spinner-container.fullscreen {
    background: rgba(0, 0, 0, 0.8);
  }
  
  .loading-text {
    color: #ccc;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .loading-spinner-container {
    padding: 30px 15px;
    min-height: 150px;
  }
  
  .spinner-circle {
    width: 10px;
    height: 10px;
  }
  
  .loading-text {
    font-size: 13px;
  }
}
</style>
