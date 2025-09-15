<template>
    <a-layout class="basic-layout">
        <!-- 头部导航栏 -->
        <GlobalHeader />

        <!-- 主要内容区域 -->
        <a-layout-content class="layout-content">
            <div class="content-wrapper">
                <RouterView />
            </div>
        </a-layout-content>

        <!-- 底部版权信息 -->
        <GlobalFooter />
    </a-layout>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import GlobalHeader from '@/components/global/GlobalHeader.vue'
import GlobalFooter from '@/components/global/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background: var(--color-background-gray);
}

.layout-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: var(--color-background-gray);
    position: relative;
}

.content-wrapper {
    flex: 1;
    max-width: 1600px;
    margin: 0 auto;
    padding: 0;
    width: 100%;
    box-sizing: border-box;
    background: transparent;
    animation: fadeIn 0.3s ease-out;
}

/* 页面淡入动画 */
@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 页面内容区域样式 */
:deep(.ant-layout-content) {
    background: var(--color-background-gray);
}

/* 优化页面卡片容器 */
:deep(.page-container) {
    background: var(--color-background);
    border-radius: 8px;
    box-shadow: var(--shadow-sm);
    margin: 24px;
    overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 1200px) {
    .content-wrapper {
        max-width: 100%;
    }
    
    :deep(.page-container) {
        margin: 16px;
        border-radius: 6px;
    }
}

@media (max-width: 768px) {
    :deep(.page-container) {
        margin: 12px;
        border-radius: 4px;
        box-shadow: var(--shadow-sm);
    }
}

@media (max-width: 480px) {
    :deep(.page-container) {
        margin: 8px;
        border-radius: 4px;
    }
}

/* 优化加载状态样式 */
:deep(.ant-spin-container) {
    transition: opacity 0.3s ease;
}

:deep(.ant-spin-blur) {
    opacity: 0.5;
    user-select: none;
    pointer-events: none;
}

/* 优化表格容器样式 */
:deep(.ant-table-wrapper) {
    background: var(--color-background);
    border-radius: 8px;
    box-shadow: var(--shadow-sm);
    overflow: hidden;
}

/* 优化搜索卡片样式 */
:deep(.search-card) {
    background: var(--color-background);
    border-radius: 8px;
    box-shadow: var(--shadow-sm);
    border: 1px solid var(--color-border-light);
}

/* 优化统计卡片样式 */
:deep(.ant-statistic) {
    .ant-statistic-title {
        color: var(--color-text-secondary);
        font-weight: 500;
    }
    
    .ant-statistic-content {
        font-weight: 600;
    }
}
</style>
