<template>
    <a-layout-header class="global-header">
        <div class="header-content">
            <!-- 左侧 Logo 和标题 -->
            <div class="header-left">
                <div class="logo-wrapper">
                    <img src="@/assets/logo.svg" alt="Logo" class="logo" />
                    <span class="site-title">情感雷达</span>
                </div>
            </div>

            <!-- 中间菜单 -->
            <div class="header-center">
                <a-menu mode="horizontal" :selectedKeys="selectedKeys" :items="menuItems" @click="handleMenuClick"
                    class="header-menu" />
            </div>

            <!-- 右侧用户信息 -->
            <div class="header-right">
                <a-button type="primary" @click="handleLogin">
                    登录
                </a-button>
            </div>
        </div>
    </a-layout-header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'

const router = useRouter()
const route = useRoute()

// 菜单配置
const menuItems = ref<MenuProps['items']>([
    {
        key: '/',
        label: '首页',
    },
    {
        key: '/about',
        label: '关于',
    },
    {
        key: '/chat-content',
        label: '对话管理',
    },
    {
        key: '/user-feedback',
        label: '用户反馈',
    },
    {
        key: '/token-stats',
        label: 'Token统计',
    },
    {
        key: '/image-analysis',
        label: '图片解析',
    },
    {
        key: '/ocr',
        label: 'OCR识别',
    },
    {
        key: '/emotion-radar',
        label: '情感雷达',
    },
])

// 当前选中的菜单项
const selectedKeys = computed(() => [route.path])

// 菜单点击处理
const handleMenuClick = ({ key }: { key: string }) => {
    router.push(key)
}

// 登录按钮处理
const handleLogin = () => {
    console.log('登录功能待实现')
}
</script>

<style scoped>
.global-header {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow:
        0 8px 32px rgba(0, 0, 0, 0.1),
        0 1px 0 rgba(255, 255, 255, 0.5) inset;
    padding: 0;
    height: 72px;
    line-height: 72px;
    position: sticky;
    top: 0;
    z-index: 1000;
    transition: all 0.3s ease;
}

.header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
}

.header-left {
    display: flex;
    align-items: center;
}

.logo-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;
}

.logo {
    height: 32px;
    width: 32px;
}

.site-title {
    font-size: 20px;
    font-weight: 700;
    background: linear-gradient(135deg, #1890ff, #52c41a);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    white-space: nowrap;
    letter-spacing: 0.5px;
}

.header-center {
    flex: 1;
    display: flex;
    justify-content: center;
}

.header-menu {
    border-bottom: none;
    background: transparent;
    line-height: 72px;
}

:deep(.ant-menu-item) {
    border-radius: 8px;
    margin: 0 4px;
    transition: all 0.3s ease;
}

:deep(.ant-menu-item:hover) {
    background: rgba(24, 144, 255, 0.1);
    color: #1890ff;
}

:deep(.ant-menu-item-selected) {
    background: linear-gradient(135deg, rgba(24, 144, 255, 0.1), rgba(82, 196, 26, 0.1));
    color: #1890ff;
    font-weight: 600;
}

.header-right {
    display: flex;
    align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .header-content {
        padding: 0 16px;
    }

    .site-title {
        display: none;
    }

    .header-center {
        flex: none;
    }

    .header-menu {
        display: none;
    }
}

@media (max-width: 480px) {
    .header-content {
        padding: 0 12px;
    }

    .logo {
        height: 28px;
        width: 28px;
    }
}
</style>
