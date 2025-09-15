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
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 0;
    height: 64px;
    line-height: 64px;
    position: sticky;
    top: 0;
    z-index: 100;
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
    font-size: 18px;
    font-weight: 600;
    color: #1890ff;
    white-space: nowrap;
}

.header-center {
    flex: 1;
    display: flex;
    justify-content: center;
}

.header-menu {
    border-bottom: none;
    background: transparent;
    line-height: 64px;
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
