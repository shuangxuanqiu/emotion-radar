<template>
  <div class="about-page">
    <div class="about-header">
      <h1 class="about-title">关于情感雷达</h1>
      <p class="about-subtitle">来自小扬的原创项目</p>
    </div>

    <div class="about-content">
      <a-row :gutter="[32, 32]">
        <a-col :xs="24" :lg="12">
          <a-card class="info-card">
            <template #title>
              <span class="card-title">项目介绍</span>
            </template>
            <p class="card-content">
              情感雷达是一个现代化的 Web 应用项目，采用 Vue 3 + TypeScript + Ant Design Vue 技术栈构建。
              项目注重用户体验和代码质量，提供了完整的前端解决方案。
            </p>
          </a-card>
        </a-col>

        <a-col :xs="24" :lg="12">
          <a-card class="info-card">
            <template #title>
              <span class="card-title">技术栈</span>
            </template>
            <div class="tech-stack">
              <a-tag color="blue">Vue 3</a-tag>
              <a-tag color="green">TypeScript</a-tag>
              <a-tag color="orange">Ant Design Vue</a-tag>
              <a-tag color="purple">Vite</a-tag>
              <a-tag color="red">Vue Router</a-tag>
              <a-tag color="cyan">Pinia</a-tag>
            </div>
          </a-card>
        </a-col>

        <a-col :xs="24">
          <a-card class="info-card">
            <template #title>
              <span class="card-title">特性</span>
            </template>
            <a-row :gutter="[16, 16]">
              <a-col :xs="24" :sm="12" :md="6">
                <div class="feature-item">
                  <div class="feature-icon">🎨</div>
                  <div class="feature-text">现代化设计</div>
                </div>
              </a-col>
              <a-col :xs="24" :sm="12" :md="6">
                <div class="feature-item">
                  <div class="feature-icon">📱</div>
                  <div class="feature-text">响应式布局</div>
                </div>
              </a-col>
              <a-col :xs="24" :sm="12" :md="6">
                <div class="feature-item">
                  <div class="feature-icon">⚡</div>
                  <div class="feature-text">高性能</div>
                </div>
              </a-col>
              <a-col :xs="24" :sm="12" :md="6">
                <div class="feature-item">
                  <div class="feature-icon">🛠️</div>
                  <div class="feature-text">易维护</div>
                </div>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<style scoped>
.about-page {
  padding: 40px 0;
}

.about-header {
  text-align: center;
  margin-bottom: 60px;
}

.about-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 16px;
}

.about-subtitle {
  font-size: 1.2rem;
  color: #666;
  margin: 0;
}

.info-card {
  height: 100%;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.info-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
}

.card-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  margin: 0;
}

.tech-stack {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.feature-item {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background: #f8f9fa;
  transition: background-color 0.3s ease;
}

.feature-item:hover {
  background: #e9ecef;
}

.feature-icon {
  font-size: 2rem;
  margin-bottom: 8px;
}

.feature-text {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .about-page {
    padding: 20px 0;
  }

  .about-title {
    font-size: 2rem;
  }

  .about-subtitle {
    font-size: 1rem;
  }

  .about-header {
    margin-bottom: 40px;
  }
}
</style>
