<template>
    <a-layout-footer class="global-footer">
        <div class="footer-content">
            <p class="copyright">
                来自小扬的原创项目，情感雷达
            </p>
        </div>
    </a-layout-footer>
</template>

<script setup lang="ts">
// 无需额外逻辑
</script>

<style scoped>
.global-footer {
    background: #f0f2f5;
    text-align: center;
    padding: 24px 0;
    border-top: 1px solid #e8e8e8;
    margin-top: auto;
}

.footer-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
}

.copyright {
    margin: 0;
    color: #666;
    font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .footer-content {
        padding: 0 16px;
    }

    .copyright {
        font-size: 13px;
    }
}
</style>
