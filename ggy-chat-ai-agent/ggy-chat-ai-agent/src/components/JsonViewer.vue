<template>
    <div class="json-viewer">
        <!-- 如果是有效的JSON，显示格式化版本 -->
        <div v-if="isValidJson" class="json-content">
            <!-- 切换按钮 -->
            <div class="json-toolbar" v-if="showToolbar">
                <a-space>
                    <a-button size="small" :type="viewMode === 'formatted' ? 'primary' : 'default'"
                        @click="viewMode = 'formatted'">
                        格式化
                    </a-button>
                    <a-button size="small" :type="viewMode === 'raw' ? 'primary' : 'default'" @click="viewMode = 'raw'">
                        原始
                    </a-button>
                    <a-button size="small" :type="viewMode === 'text' ? 'primary' : 'default'"
                        @click="viewMode = 'text'">
                        纯文本
                    </a-button>
                    <a-button size="small" @click="copyToClipboard" :loading="copying">
                        <template #icon>
                            <CopyOutlined />
                        </template>
                        复制
                    </a-button>
                </a-space>
            </div>

            <!-- 格式化JSON显示 -->
            <div v-if="viewMode === 'formatted'" class="json-formatted">
                <pre><code>{{ formattedJson }}</code></pre>
            </div>

            <!-- 原始JSON显示 -->
            <div v-else-if="viewMode === 'raw'" class="json-raw">
                <pre><code>{{ content }}</code></pre>
            </div>

            <!-- 纯文本显示 -->
            <div v-else-if="viewMode === 'text'" class="json-text">
                <div class="text-content">{{ extractedText }}</div>
            </div>
        </div>

        <!-- 如果不是JSON，显示普通文本 -->
        <div v-else class="plain-text">
            <pre>{{ content }}</pre>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import { CopyOutlined } from '@ant-design/icons-vue'
import { isValidJSON, formatJSON, extractTextFromJSON } from '@/utils/json'

interface Props {
    content?: string | null
    showToolbar?: boolean
    defaultMode?: 'formatted' | 'raw' | 'text'
    maxHeight?: string
}

const props = withDefaults(defineProps<Props>(), {
    content: '',
    showToolbar: true,
    defaultMode: 'formatted',
    maxHeight: '300px'
})

const viewMode = ref(props.defaultMode)
const copying = ref(false)

// 检查是否为有效JSON
const isValidJson = computed(() => {
    return isValidJSON(props.content)
})

// 格式化后的JSON
const formattedJson = computed(() => {
    return formatJSON(props.content)
})

// 提取的纯文本
const extractedText = computed(() => {
    return extractTextFromJSON(props.content)
})

// 复制到剪贴板
const copyToClipboard = async () => {
    if (!props.content) return

    copying.value = true
    try {
        let textToCopy = props.content

        if (viewMode.value === 'formatted' && isValidJson.value) {
            textToCopy = formattedJson.value
        } else if (viewMode.value === 'text') {
            textToCopy = extractedText.value
        }

        await navigator.clipboard.writeText(textToCopy)
        message.success('已复制到剪贴板')
    } catch (error) {
        console.error('复制失败:', error)
        message.error('复制失败')
    } finally {
        copying.value = false
    }
}
</script>

<style scoped>
.json-viewer {
    width: 100%;
}

.json-toolbar {
    margin-bottom: 8px;
    padding: 8px;
    background: #f5f5f5;
    border-radius: 4px;
}

.json-content,
.plain-text {
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    background: #fafafa;
}

.json-formatted,
.json-raw,
.plain-text {
    max-height: v-bind(maxHeight);
    overflow-y: auto;
    padding: 12px;
}

.json-formatted pre,
.json-raw pre,
.plain-text pre {
    margin: 0;
    white-space: pre-wrap;
    word-wrap: break-word;
    font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
    font-size: 12px;
    line-height: 1.4;
}

.json-text {
    max-height: v-bind(maxHeight);
    overflow-y: auto;
    padding: 12px;
}

.text-content {
    white-space: pre-wrap;
    word-wrap: break-word;
    line-height: 1.6;
    color: #333;
}

/* JSON语法高亮 */
.json-formatted code {
    color: #333;
}

/* 滚动条样式 */
.json-formatted::-webkit-scrollbar,
.json-raw::-webkit-scrollbar,
.json-text::-webkit-scrollbar,
.plain-text::-webkit-scrollbar {
    width: 6px;
}

.json-formatted::-webkit-scrollbar-thumb,
.json-raw::-webkit-scrollbar-thumb,
.json-text::-webkit-scrollbar-thumb,
.plain-text::-webkit-scrollbar-thumb {
    background-color: #ccc;
    border-radius: 3px;
}

.json-formatted::-webkit-scrollbar-thumb:hover,
.json-raw::-webkit-scrollbar-thumb:hover,
.json-text::-webkit-scrollbar-thumb:hover,
.plain-text::-webkit-scrollbar-thumb:hover {
    background-color: #999;
}
</style>
