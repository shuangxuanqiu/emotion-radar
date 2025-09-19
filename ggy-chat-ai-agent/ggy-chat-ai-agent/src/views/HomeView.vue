<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import api from '../api'

// 响应式数据
const loading = ref(false)
const testResult = ref<any>(null)

// 测试API接口
const testAPI = async () => {
  loading.value = true
  testResult.value = null
  
  try {
    // 调用对话内容分页接口进行测试
    const response = await api.duihuaneirongguanli.page3({
      page: {
        pageNumber: 1,
        pageSize: 10
      }
    })
    
    testResult.value = response.data
    message.success('API调用成功！')
    console.log('API测试结果:', response)
  } catch (error) {
    console.error('API调用失败:', error)
    message.error(`API调用失败: ${error}`)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="home-page home-theme">
    <div class="hero-section">
      <h1 class="hero-title">欢迎来到情感雷达</h1>
      <p class="hero-description">
        这是一个基于 Vue 3 + Ant Design Vue 构建的现代化 Web 应用
      </p>
      <div class="hero-buttons">
        <a-button type="primary" size="large" class="hero-button colorful-btn-primary">
          开始探索
        </a-button>
        <a-button 
          type="default" 
          size="large" 
          class="hero-button test-button colorful-btn-secondary" 
          :loading="loading"
          @click="testAPI"
        >
          测试API接口
        </a-button>
      </div>
    </div>

    <!-- API测试结果显示区域 -->
    <div v-if="testResult" class="test-result-section">
      <a-card title="API测试结果" class="result-card colorful-stats-card">
        <pre class="result-content">{{ JSON.stringify(testResult, null, 2) }}</pre>
      </a-card>
    </div>

    <div class="feature-section">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :sm="12" :md="8">
          <a-card title="现代化设计" class="feature-card colorful-stats-card">
            <p>采用 Ant Design Vue 组件库，提供优雅的用户界面</p>
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="8">
          <a-card title="响应式布局" class="feature-card colorful-stats-card">
            <p>完美适配各种设备，从手机到桌面端都有良好体验</p>
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="8">
          <a-card title="Vue 3 组合式 API" class="feature-card colorful-stats-card">
            <p>使用最新的 Vue 3 技术栈，代码更简洁高效</p>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  padding: 40px 0;
}

.hero-section {
  text-align: center;
  padding: 80px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  margin-bottom: 60px;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 20px;
  color: white;
}

.hero-description {
  font-size: 1.2rem;
  margin-bottom: 30px;
  opacity: 0.9;
}

.hero-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.hero-button {
  height: 48px;
  padding: 0 32px;
  font-size: 16px;
}

.test-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.test-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.feature-section {
  margin-top: 40px;
}

.feature-card {
  height: 100%;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.test-result-section {
  margin: 40px 0;
}

.result-card {
  max-width: 100%;
}

.result-content {
  background: #f6f8fa;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  padding: 16px;
  margin: 0;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.4;
  overflow-x: auto;
  max-height: 400px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }

  .hero-description {
    font-size: 1rem;
  }

  .home-page {
    padding: 20px 0;
  }

  .hero-section {
    padding: 60px 20px;
  }
}
</style>
