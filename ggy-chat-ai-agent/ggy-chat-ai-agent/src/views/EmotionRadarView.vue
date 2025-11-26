<template>
  <div class="emotion-radar-page">
    <!-- 有内容时显示的顶部栏 -->
    <div v-if="hasContent" class="page-header">
      <h2>🎯 情感雷达</h2>
      <a-space>
        <a-input v-model:value="chatIdInput" placeholder="请输入 chatId" style="width: 280px" size="middle" />
        <a-button @click="refreshChatId" :disabled="processing" size="middle" shape="round">
          <template #icon>
            <ReloadOutlined />
          </template>
          刷新 chatId
        </a-button>
      </a-space>
      <div class="header-actions">
        <a-tag :color="processing ? 'processing' : 'default'">{{ processing ? '识别中' : '待识别' }}</a-tag>
      </div>
    </div>

    <!-- 空状态页面 -->
    <div v-if="!hasContent" class="empty-hero">
      <a-card class="hero-card" :bordered="false">
        <div class="hero-content">
          <div class="hero-illustration">💬</div>
          <div class="hero-title">情感雷达</div>
          <div class="hero-subtitle">简洁、好用、回复快</div>
          <div class="hero-description">AI 识别总结随时助您获得最佳回复</div>

          <a-upload class="hero-uploader" :show-upload-list="false" :before-upload="beforeUpload" accept="image/*"
            :disabled="processing" @change="onUploadChange">
            <a-button type="primary" size="large" shape="round" class="upload-button">
              <template #icon>
                <UploadOutlined />
              </template>
              上传聊天截图试试吧
            </a-button>
          </a-upload>

          <div class="upload-hint">请放心上传，我们不会保存您的聊天截图</div>

          <div class="preset-panel">
            <div class="preset-title">初识</div>
            <div class="preset-tags">
              <a-tag class="preset-tag">推荐</a-tag>
              <a-tag class="preset-tag">高情商</a-tag>
              <a-tag class="preset-tag">哄女友</a-tag>
              <a-tag class="preset-tag">暖味拉扯</a-tag>
              <a-tag class="preset-tag">霸道总裁</a-tag>
              <a-tag class="preset-tag">摩女生</a-tag>
            </div>
          </div>
        </div>
      </a-card>
    </div>

    <!-- 主内容区域 -->
    <div v-else>
      <a-card class="glass-card" :bordered="false">
        <div class="section-title">👁️ 预览</div>
        <div class="image-stage">
          <canvas v-if="processing" ref="radarCanvas" class="scan-canvas"></canvas>
          <div class="image-container">
            <img v-if="imageUrl" :src="imageUrl" class="preview-img" alt="聊天截图预览" />
          </div>
        </div>
      </a-card>

      <a-card class="glass-card" :bordered="false" style="margin-top: 16px">
        <div class="section-title" style="display: flex; align-items: center; justify-content: space-between;">
          <span>✏️ 总提示词</span>
          <a-switch v-model:checked="isComplexMode" checked-children="复杂模式" un-checked-children="简单模式" />
        </div>

        <div v-if="!isComplexMode">
          <a-textarea v-model:value="totalPrompt" :rows="6" placeholder="请输入本次识别的总提示词..." allow-clear />
        </div>
        <div v-else>
          <a-collapse v-model:activeKey="activeKey" accordion ghost>
            <a-collapse-panel key="1" header="总提示词 (message)">
              <a-textarea v-model:value="totalPrompt" :rows="4" placeholder="请识别这张聊天界面截图..." allow-clear />
            </a-collapse-panel>
            <a-collapse-panel key="2" header="情绪背景 (conversationScene)">
              <a-input v-model:value="conversationScene" placeholder="例如：工作朋友" allow-clear />
            </a-collapse-panel>
            <a-collapse-panel key="3" header="情感指数参数 (emotionalIndex)">
              <a-input-number v-model:value="emotionalIndex" :min="0" :max="10" style="width: 100%"
                placeholder="例如：5" />
            </a-collapse-panel>
          </a-collapse>
        </div>
        <a-space style="margin-top: 12px">
          <a-button type="primary" :disabled="!canStart" @click="startStream" size="middle" shape="round">
            <template #icon>
              <PlayCircleOutlined />
            </template>
            开始识别
          </a-button>
          <a-button danger :disabled="!imageBase64Raw && items.length === 0" @click="resetAll" size="middle"
            shape="round">
            <template #icon>
              <DeleteOutlined />
            </template>
            清空
          </a-button>
        </a-space>
        <a-alert v-if="errorMsg" style="margin-top: 12px" type="error" :message="errorMsg" show-icon />
        <div v-if="processing" style="margin-top: 16px; text-align: center;">
          <div class="dynamic-loading-text fade-in-text">{{ currentLoadingText }}</div>
          <LoadingSpinner :text="''" type="wave" />
          <a-progress :percent="progressPercent" status="active"
            :stroke-color="{ '0%': '#108ee9', '100%': '#87d068' }" />
        </div>
      </a-card>
    </div>

    <!-- 对话策略卡片（始终显示，跨两列占满） -->
    <a-card class="glass-card strategy-card fade-in" :bordered="false" style="margin-top: 16px">
      <div class="section-title">🎯 对话策略</div>
      <div class="background-info">
        <div class="background-title">📊 聊天背景分析</div>
        <div class="background-grid">
          <div class="background-item" v-if="deepseekResult?.backgroundAnalysis?.relationshipType">
            <div class="background-label">关系类型</div>
            <div class="background-value">{{ deepseekResult?.backgroundAnalysis?.relationshipType }}</div>
          </div>
          <div class="background-item" v-if="deepseekResult?.backgroundAnalysis?.conversationScene">
            <div class="background-label">对话场景</div>
            <div class="background-value">{{ deepseekResult?.backgroundAnalysis?.conversationScene }}</div>
          </div>
          <div class="background-item" v-if="deepseekResult?.backgroundAnalysis?.topicNature">
            <div class="background-label">话题性质</div>
            <div class="background-value">{{ deepseekResult?.backgroundAnalysis?.topicNature }}</div>
          </div>
          <div class="background-item" v-if="deepseekResult?.backgroundAnalysis?.userToneCharacteristics">
            <div class="background-label">用户特征</div>
            <div class="background-value">{{ deepseekResult?.backgroundAnalysis?.userToneCharacteristics }}</div>
          </div>
          <div
            v-if="processing && !(deepseekResult?.backgroundAnalysis?.relationshipType || deepseekResult?.backgroundAnalysis?.conversationScene || deepseekResult?.backgroundAnalysis?.topicNature || deepseekResult?.backgroundAnalysis?.userToneCharacteristics)"
            style="grid-column: 1 / -1;">
            <a-skeleton active :paragraph="{ rows: 2 }" />
          </div>
        </div>
        <div class="emotional-summary"
          v-if="typeof deepseekResult?.overallEmotionalIndex === 'number' || deepseekResult?.emotionalReason">
          <div class="emotional-index-main" v-if="typeof deepseekResult?.overallEmotionalIndex === 'number'">整体情感指数:
            {{ deepseekResult?.overallEmotionalIndex }} 分</div>
          <div class="emotional-reason" v-if="deepseekResult?.emotionalReason">{{ deepseekResult?.emotionalReason }}
          </div>
        </div>
        <div v-else-if="processing" style="margin-top: 10px;">
          <a-skeleton active :title="false" :paragraph="{ rows: 1 }" />
        </div>
      </div>
      <div class="image-analysis-section" style="margin-top: 12px;">
        <div class="section-title" style="margin-bottom:8px;">🖼️ 截图解析</div>

        <div class="background-info" v-if="imageDetailsText">
          <div class="background-title">截图细节</div>
          <div class="background-value" style="margin-left:0">{{ imageDetailsText }}</div>
        </div>
        <div v-if="processing && !imageExtractText && !imageDetailsText">
          <a-skeleton active :paragraph="{ rows: 2 }" />
        </div>
      </div>
      <div class="message-list" style="margin-top: 12px;">
        <div class="emotion-tags-toolbar" style="margin-bottom: 12px;">
          <a-space wrap>
            <a-tag v-for="t in emotionTags" :key="t" class="clickable-tag" :disabled="continuationRunning"
              @click="handleEmotionTagClick(t)">{{ t
              }}</a-tag>
            <a-input v-if="addTagVisible" v-model:value="newTag" placeholder="输入标签" style="width: 160px" />
            <a-button v-if="addTagVisible" type="primary" size="small" @click="confirmAddTag">添加</a-button>
            <a-button type="dashed" size="small" @click="toggleAddTag">添加标签</a-button>
          </a-space>
        </div>
        <transition-group name="list-fade">
          <div class="message fade-in" v-for="(m, i) in deepseekResult?.messages || []"
            :key="(m.messageType || 0) + '-' + i" :class="{ 'celebrate': m.celebrate, 'loading-card': m.loading }">
            <div v-if="m.celebrate" class="celebrate-overlay">
              <span class="confetti c1"></span>
              <span class="confetti c2"></span>
              <span class="confetti c3"></span>
              <span class="confetti c4"></span>
              <span class="confetti c5"></span>
              <span class="confetti c6"></span>
              <span class="confetti c7"></span>
              <span class="confetti c8"></span>
              <span class="confetti c9"></span>
              <span class="confetti c10"></span>
              <span class="confetti c11"></span>
              <span class="confetti c12"></span>
            </div>
            <a-space wrap style="margin-bottom: 8px;">
              <a-tag color="geekblue" v-if="m.conversationScene">{{ m.conversationScene }}</a-tag>
              <a-tag color="orange" v-if="m.relationshipType">{{ m.relationshipType }}</a-tag>
              <a-tag color="purple" v-if="m.topicNature">{{ m.topicNature }}</a-tag>
              <a-tag color="cyan" v-if="m.userToneCharacteristics">{{ m.userToneCharacteristics }}</a-tag>
              <a-tag color="green" v-if="typeof m.emotionalIndex === 'number'">情感: {{ m.emotionalIndex }}</a-tag>
            </a-space>
            <div class="emotion-index" v-if="m.emotionalReason">{{ m.emotionalReason }}</div>
            <div v-if="m.loading" class="loading-container">
              <div class="loading-header">
                <span class="loading-text-plain">AI 正在续写</span>
                <span class="typing-dots"><i></i><i></i><i></i></span>
              </div>
              <div class="loading-progress">
                <div class="bar"></div>
              </div>
              <a-skeleton active :paragraph="{ rows: 2 }" :title="false" />
            </div>
            <div v-else class="message-content">{{ m.message }}</div>
            <button v-if="!m.loading" class="copy-btn" type="button"
              @click="copyMessage(m.message || '', m.emotionalIndex, $event)">复制</button>
          </div>
        </transition-group>
        <div v-if="processing && (!deepseekResult?.messages || deepseekResult?.messages.length === 0)"
          style="padding: 12px;">
          <a-skeleton active :paragraph="{ rows: 3 }" />
        </div>
      </div>
      <div class="message" v-if="imageExtractText">
        <div class="message-content">{{ imageExtractText }}</div>
      </div>
    </a-card>

    <!-- 实时返回内容卡片 -->
    <a-card class="glass-card" :bordered="false" style="margin-top: 16px">
      <template #title>
        <div class="section-title">📡 实时返回内容</div>
        <a-button size="small" type="link" @click="clearStreamLogs" style="float: right; padding: 0;">清空日志</a-button>
      </template>
      <div class="stream-plain">
        <pre class="stream-plain-text" aria-live="polite">{{ combinedText || '暂无返回内容' }}</pre>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
/// <reference types="@/api/typings" />
import { ref, computed, onBeforeUnmount, onMounted, watch, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import { UploadOutlined, PlayCircleOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { useRoute } from 'vue-router'
import api from '@/api'
import { getImageDisplayUrl } from '@/utils/image'

// =================================================================================================
// ✨ 类型定义
// =================================================================================================

/**
 * @description OCR 识别结果的单项。
 * @property {string} text - 识别出的文本内容。
 * @property {number} [confidence] - 文本识别的置信度。
 * @property {string} [sender] - 消息发送者。
 * @property {number} [lineIndex] - 文本所在的行索引。
 * @property {number} [page] - 文本所在的页面编号。
 * @property {boolean | null} [isComplete] - 该项是否已处理完成。
 */
type OcrItem = {
  text: string
  confidence?: number
  sender?: string
  lineIndex?: number
  page?: number
  isComplete?: boolean | null
}

/**
 * @description DeepSeek 返回的聊天背景分析。
 * @property {string} [conversationScene] - 对话场景。
 * @property {string} [relationshipType] - 关系类型。
 * @property {string} [topicNature] - 话题性质。
 * @property {string} [userToneCharacteristics] - 用户语气特征。
 */
type DeepSeekBackground = {
  conversationScene?: string
  relationshipType?: string
  topicNature?: string
  userToneCharacteristics?: string
}

/**
 * @description DeepSeek 返回的单条消息分析或建议。
 * @property {string} [conversationScene] - 对话场景。
 * @property {number} [emotionalIndex] - 情感指数。
 * @property {string} [emotionalReason] - 情感分析的原因。
 * @property {string} [message] - 生成的回复建议。
 * @property {number} [overallEmotionalIndex] - 整体情感指数。
 * @property {string} [relationshipType] - 关系类型。
 * @property {string} [topicNature] - 话题性质。
 * @property {string} [userToneCharacteristics] - 用户语气特征。
 * @property {number} [messageType] - 消息类型（例如，1 代表续写）。
 * @property {boolean} [loading] - 是否处于加载状态（用于 UI 显示）。
 * @property {boolean} [celebrate] - 是否触发庆祝动画（用于 UI 显示）。
 */
type DeepSeekMessage = {
  conversationScene?: string
  emotionalIndex?: number
  emotionalReason?: string
  message?: string
  overallEmotionalIndex?: number
  relationshipType?: string
  topicNature?: string
  userToneCharacteristics?: string
  messageType?: number
  loading?: boolean
  celebrate?: boolean
}

/**
 * @description DeepSeek API 返回的完整结果结构。
 * @property {DeepSeekBackground} [backgroundAnalysis] - 聊天背景分析。
 * @property {string} [emotionalReason] - 整体情感分析的原因。
 * @property {DeepSeekMessage[]} [messages] - 消息分析和建议列表。
 * @property {number} [overallEmotionalIndex] - 整体情感指数。
 */
type DeepSeekResult = {
  backgroundAnalysis?: DeepSeekBackground
  emotionalReason?: string
  messages?: DeepSeekMessage[]
  overallEmotionalIndex?: number
}

// =================================================================================================
// ✨ 核心状态管理
// =================================================================================================

// --- 基本状态 ---
/** @description 是否正在进行 AI 识别或处理 */
const processing = ref(false)
/** @description 存储错误信息，用于在 UI 上显示 */
const errorMsg = ref('')

// --- 进度与识别结果 ---
/** @description 当前处理进度 */
const progressIndex = ref(0)
/** @description 总处理任务数 */
const progressTotal = ref(0)
/** @description 存储 OCR 识别出的文本项 */
const items = ref<OcrItem[]>([])
/** @description 实时组合的流式返回文本，用于日志显示 */
const combinedText = ref('')
/** @description 用户输入的总提示词 */
const totalPrompt = ref('')
/** @description 是否开启复杂模式（传递更多参数） */
const isComplexMode = ref(false)
/** @description 情绪背景参数 */
const conversationScene = ref('')
/** @description 情感指数参数 */
const emotionalIndex = ref<number | undefined>(undefined)
/** @description 折叠面板激活的 key */
const activeKey = ref(['1'])

/** @description DeepSeek API 的完整分析结果 */
const deepseekResult = ref<DeepSeekResult | null>(null)

// --- 聊天会话与 ID ---
/** @description 生成一个唯一的 chatId */
const generateChatId = (): string => `ocr_${Date.now()}_${Math.random().toString(36).slice(2, 9)}`
/** @description 当前聊天会话的 ID，用于关联请求 */
const chatIdInput = ref(generateChatId())
/** @description 刷新 chatId，开始新的会话 */
const refreshChatId = () => { chatIdInput.value = generateChatId() }

// --- 计算属性 ---
/** @description 判断是否可以开始识别（必须有图片、未在处理中、且有总提示词） */
const canStart = computed(() => !!imageBase64Raw.value && !processing.value && !!totalPrompt.value?.trim())
/** @description 判断当前页面是否处于有内容的状态（用于切换空状态和主内容） */
const hasContent = computed(() => !!imageBase64Raw.value || items.value.length > 0 || !!imageExtractText.value || !!imageDetailsText.value || !!deepseekResult.value)


// =================================================================================================
// ✨ UI 交互与事件处理
// =================================================================================================

// --- 情感标签管理 ---
/** @description 预设的情感标签列表 */
const emotionTags = ref<string[]>(['推荐', '高情商', '哄女友', '暧昧拉扯', '霸道总裁', '撩女生'])
/** @description 控制“添加标签”输入框的显示/隐藏 */
const addTagVisible = ref(false)
/** @description 绑定“添加标签”输入框的值 */
const newTag = ref('')

/** @description 切换“添加标签”输入框的可见性 */
const toggleAddTag = () => { addTagVisible.value = !addTagVisible.value }

/** @description 确认添加新标签 */
const confirmAddTag = () => {
  const v = newTag.value.trim()
  if (!v) { message.warning('请输入标签'); return }
  if (!emotionTags.value.includes(v)) { emotionTags.value.push(v) }
  newTag.value = ''
  addTagVisible.value = false
}

/**
 * @description 处理情感标签点击事件，触发 AI 续写。
 * @param {string} label - 被点击的标签文本。
 */
const continuationRunning = ref(false)
const handleEmotionTagClick = async (label: string) => {
  if (continuationRunning.value) { message.info('正在续写中，请稍候'); return }
  // 1. 获取当前情感指数，若不存在则默认为 6
  const idx = typeof deepseekResult.value?.overallEmotionalIndex === 'number' ? (deepseekResult.value as DeepSeekResult).overallEmotionalIndex as number : 6
  const chatId = (chatIdInput.value || generateChatId()).trim()
  const params = { emotionalIndex: idx, emotionalLabels: label, chatId }

  // 2. 准备 UI，插入一个加载占位符到消息列表顶部
  if (!deepseekResult.value) { deepseekResult.value = { messages: [] } }
  const existing = (deepseekResult.value!.messages as DeepSeekMessage[]) || []
  const placeholder: DeepSeekMessage = {
    conversationScene: '续写中',
    topicNature: '情绪标签',
    userToneCharacteristics: label,
    emotionalIndex: idx,
    message: '正在续写...',
    messageType: 1,
    loading: true
  }
  deepseekResult.value!.messages = [placeholder, ...existing]

  // 3. 启动续写流式请求
  try {
    await startContinuationStream(params)
  } catch (e: unknown) {
    // 即使续写失败，也要移除加载状态
    const messages = deepseekResult.value?.messages || []
    if (messages[0]?.loading) {
      messages.shift()
    }
    const msg = e instanceof Error ? e.message : String(e)
    errorMsg.value = `续写失败: ${msg}`
  }

  // 4. (调试用) 将请求参数和结果记录到日志中
  const existingJson = deepseekResult.value ? JSON.stringify(deepseekResult.value) : ''
  const requestJson = JSON.stringify(params)
  combinedText.value += (existingJson ? existingJson + '\n' : '') + requestJson + '\n'
}

// =================================================================================================
// ✨ SSE (Server-Sent Events) 通信
// =================================================================================================

/** @description 用于中断正在进行的续写请求 */
let continuationAbortController: AbortController | null = null

/**
 * @description 启动一个 SSE 连接，用于 AI 续写。
 * @param p - 包含情感指数、标签和 chatId 的参数对象。
 */
const startContinuationStream = async (p: { emotionalIndex: number; emotionalLabels: string; chatId: string }) => {
  // 如果存在旧的控制器，先中断它
  if (continuationAbortController) {
    continuationAbortController.abort()
    continuationAbortController = null
  }
  continuationAbortController = new AbortController()

  const qs = new URLSearchParams({ emotionalIndex: String(p.emotionalIndex), emotionalLabels: p.emotionalLabels, chatId: p.chatId }).toString()
  const url = `/api/stream-ai/travel_guide/chat/sse/continuation?${qs}`

  try {
    continuationRunning.value = true
    const response = await fetch(url, {
      method: 'POST',
      signal: continuationAbortController.signal,
      credentials: 'include'
    })

    if (!response.ok) { throw new Error(`HTTP error! status: ${response.status}`) }
    if (!response.body) { throw new Error('Response body is null') }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    let currentEvent: string | null = null
    let sessionText = ''

    // 持续读取流数据
    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        const trimmedLine = line.trim()
        if (!trimmedLine) continue

        if (trimmedLine.startsWith('event:')) {
          currentEvent = trimmedLine.slice(6).trim()
          continue
        }

        if (trimmedLine.startsWith('data:')) {
          const idx = line.indexOf('data:')
          const dataStr = idx >= 0 ? line.slice(idx + 5) : trimmedLine.slice(5)
          if (dataStr) {
            try {
              const data = JSON.parse(dataStr) as StreamLogData
              handleSSEMessage(currentEvent as StreamLog['type'] | null, data)
              const frag = extractTextFragment(data)
              if (frag) { sessionText += frag }
            } catch {
              handleSSEMessage('text', dataStr)
              sessionText += dataStr
            }
          }
          currentEvent = null
        }
      }
    }

    // 处理可能遗留的 buffer 数据
    if (buffer.trim()) {
      const trimmedBuffer = buffer.trim()
      if (trimmedBuffer.startsWith('event:')) {
        currentEvent = trimmedBuffer.slice(6).trim()
      } else if (trimmedBuffer.startsWith('data:')) {
        const dataStr = buffer.slice(buffer.indexOf('data:') + 5)
        if (dataStr) {
          try {
            const data = JSON.parse(dataStr) as StreamLogData
            handleSSEMessage(currentEvent as StreamLog['type'] | null, data)
            const frag = extractTextFragment(data)
            if (frag) { sessionText += frag }
          } catch {
            handleSSEMessage('text', dataStr)
            sessionText += dataStr
          }
        }
      }
    }

    // 解析并更新最终结果
    const msgs = parseContinuationMessages(sessionText)
    if (msgs.length) {
      if (!deepseekResult.value) { deepseekResult.value = { messages: [] } }
      const existing = (deepseekResult.value!.messages as DeepSeekMessage[]) || []
      // 移除加载占位符
      if (existing[0] && existing[0].loading) { existing.shift() }
      // 为新消息添加庆祝动画触发器
      msgs.forEach((m) => { m.celebrate = true })
      deepseekResult.value!.messages = [...msgs, ...existing]
      console.log('messages after continuation:', JSON.stringify(deepseekResult.value!.messages))
    }

  } catch (error: unknown) {
    const errorMessage = error instanceof Error ? error.message : '续写失败'
    console.log('continuation SSE error:', errorMessage)
    // 确保在出错时也能移除加载状态
    if (deepseekResult.value?.messages?.[0]?.loading) {
      deepseekResult.value.messages.shift()
    }
  } finally {
    continuationRunning.value = false
  }
}

/**
 * @description 从 SSE 返回的文本中解析出结构化的消息对象。
 * @param {string} text - 包含一个或多个 JSON 对象的字符串。
 * @returns {DeepSeekMessage[]} - 解析后的消息数组。
 */
const parseContinuationMessages = (text: string): DeepSeekMessage[] => {
  const result: DeepSeekMessage[] = []
  if (!text) return result

  // 使用正则表达式匹配所有顶级的 JSON 对象
  const matches = text.match(/\{[\s\S]*?\}/g) || []
  for (const m of matches) {
    try {
      const obj = JSON.parse(m)
      const msg: DeepSeekMessage = {
        conversationScene: obj.conversationScene,
        emotionalIndex: obj.emotionalIndex,
        emotionalReason: obj.emotionalReason,
        message: obj.message,
        relationshipType: obj.relationshipType,
        topicNature: obj.topicNature,
        userToneCharacteristics: obj.userToneCharacteristics,
        messageType: 1, // 标记为续写消息
      }
      result.push(msg)
    } catch {
      // 忽略无法解析的片段
    }
  }
  return result
}

// =================================================================================================
// ✨ 图片与文件上传
// =================================================================================================

/** @description antd-vue 的 Upload 组件所需的文件列表 */
interface UploadFile { uid: string; name: string; status?: string; originFileObj?: File }
const fileList = ref<UploadFile[]>([])

/** @description 上传图片的原始 Base64 字符串（不含 data:image/... 前缀） */
const imageBase64Raw = ref<string | null>(null)
/** @description 上传图片的文件名 */
const imageFileName = ref<string>('')
/** @description 如果图片已存入数据库，则记录其路径 */
const dbImagePath = ref<string>('')

/**
 * @description 计算最终用于 `<img>` 标签的 src URL。
 * 优先使用 Base64 数据，其次是数据库路径。
 */
const imageUrl = computed(() => {
  if (imageBase64Raw.value) return `data:image/png;base64,${imageBase64Raw.value}`
  if (dbImagePath.value) return getImageDisplayUrl(dbImagePath.value)
  return ''
})

// =================================================================================================
// ✨ 主识别流程与流式日志
// =================================================================================================

/** @description 用于中断主识别流程（非续写） */
let abortController: AbortController | null = null

/**
 * @description SSE 流中单个 data 对象的结构。
 */
interface StreamLogData {
  type?: string
  text?: string
  confidence?: number
  detail?: string
  message?: string
  sender?: string
  lineIndex?: number
  page?: number
  isComplete?: boolean | null
  [key: string]: unknown
}

/**
 * @description 内部日志记录的结构。
 */
type StreamLog = {
  type: 'ocr_progress' | 'ocr_complete' | 'error' | 'complete' | 'text'
  timestamp: number
  data: StreamLogData | string
}

/** @description 存储所有流式日志，用于在“实时返回内容”卡片中显示 */
const streamLogs = ref<StreamLog[]>([])
const MAX_LOG_COUNT = 200 // 限制最大日志数量，防止内存溢出

/**
 * @description 向日志列表中添加一条新日志。
 * @param log - 要添加的日志对象。
 */
const addStreamLog = (log: StreamLog) => {
  // 移除敏感或不需要在 UI 上显示的字段，如 bbox
  const dataSanitized = typeof log.data === 'object' && log.data !== null ? { ...(log.data as Record<string, unknown>) } : log.data
  if (typeof dataSanitized === 'object') { delete (dataSanitized as Record<string, unknown>).bbox }

  streamLogs.value.unshift({ ...log, data: dataSanitized })
  if (streamLogs.value.length > MAX_LOG_COUNT) {
    streamLogs.value = streamLogs.value.slice(0, MAX_LOG_COUNT)
  }
}

/** @description 清空所有流式日志和组合文本 */
const clearStreamLogs = () => {
  streamLogs.value = []
  combinedText.value = ''
}

/**
 * @description 从 SSE 的 data 负载中提取特定的文本片段（例如，DeepSeek 的增量返回）。
 * @param payload - SSE 的 data 负载。
 * @returns {string} - 提取出的文本片段。
 */
const extractTextFragment = (payload: StreamLogData | string): string => {
  if (typeof payload === 'string') return ''
  if (!payload) return ''
  const v = (payload as Record<string, unknown>)['G']
  return typeof v === 'string' ? v : ''
}

// --- 流数据解析与状态 ---
/** @description 从流中解析出的“截图解析”文本 */
const imageExtractText = ref('')
/** @description 从流中解析出的“截图细节”文本 */
const imageDetailsText = ref('')
/** @description 用于拼接 DeepSeek 返回的 JSON 字符串的缓冲区 */
const deepseekBuffer = ref('')

/**
 * @description 尝试将字符串解析为 DeepSeekResult JSON 对象。
 * @param {string} s - 可能包含 JSON 的字符串。
 * @returns {DeepSeekResult | null} - 解析成功则返回对象，否则返回 null。
 */
const tryParseJson = (s: string): DeepSeekResult | null => {
  try { return JSON.parse(s) as DeepSeekResult } catch { return null }
}

/**
 * @description 将新的 DeepSeekResult 对象深度合并到现有的 `deepseekResult` ref 中。
 * @param {DeepSeekResult} obj - 新的、部分或完整的 DeepSeekResult 对象。
 */
const mergeDeepseekResult = (obj: DeepSeekResult) => {
  if (!obj) return
  if (!deepseekResult.value) {
    deepseekResult.value = { backgroundAnalysis: {}, messages: [], overallEmotionalIndex: undefined, emotionalReason: undefined }
  }
  const cur = deepseekResult.value as DeepSeekResult
  if (obj.backgroundAnalysis) { cur.backgroundAnalysis = { ...(cur.backgroundAnalysis || {}), ...obj.backgroundAnalysis } }
  if (typeof obj.overallEmotionalIndex === 'number') { cur.overallEmotionalIndex = obj.overallEmotionalIndex }
  if (obj.emotionalReason) { cur.emotionalReason = obj.emotionalReason }
  if (Array.isArray(obj.messages) && obj.messages.length) {
    const normalized = obj.messages.map((m) => ({ ...m, messageType: typeof m.messageType === 'number' ? m.messageType : 0 }))
    cur.messages = [...(cur.messages || []), ...normalized]
  }
  deepseekResult.value = { ...cur }
}
const parseTaggedContentIncremental = (incoming: string) => {
  if (!incoming) return
  const s = incoming
  const imgRegex = /<ggy>\[image]\s*(?:```(?:json|text)?\s*([\s\S]*?)```|([\s\S]*?)(?=<ggy>|$))/ig
  let imgMatch: RegExpExecArray | null
  while ((imgMatch = imgRegex.exec(s)) !== null) {
    const content = (imgMatch[1] || imgMatch[2] || '').trim()
    if (content) { imageExtractText.value = imageExtractText.value ? `${imageExtractText.value}\n${content}` : content }
  }
  const detailsRegex = /---截图细节---\s*([\s\S]*?)(?=<ggy>|$)/g
  let detMatch: RegExpExecArray | null
  while ((detMatch = detailsRegex.exec(s)) !== null) {
    const content = (detMatch[1] || '').trim()
    if (content) { imageDetailsText.value = imageDetailsText.value ? `${imageDetailsText.value}\n${content}` : content }
  }
  deepseekBuffer.value += s
  let buf = deepseekBuffer.value
  while (true) {
    const fencedJson = buf.match(/<ggy>\[deepseek]\s*```json\s*([\s\S]*?)```/i)
    if (fencedJson && fencedJson[1]) {
      const obj = tryParseJson(fencedJson[1])
      if (obj) mergeDeepseekResult(obj)
      buf = buf.replace(fencedJson[0], '')
      continue
    }
    const fencedAny = buf.match(/<ggy>\[deepseek]\s*```\s*([\s\S]*?)```/i)
    if (fencedAny && fencedAny[1]) {
      const obj = tryParseJson(fencedAny[1])
      if (obj) mergeDeepseekResult(obj)
      buf = buf.replace(fencedAny[0], '')
      continue
    }
    const tagIdx = buf.toLowerCase().indexOf('<ggy>[deepseek]')
    if (tagIdx >= 0) {
      const startBrace = buf.indexOf('{', tagIdx)
      if (startBrace >= 0) {
        let depth = 0
        let end = -1
        for (let i = startBrace; i < buf.length; i++) {
          const ch = buf[i]
          if (ch === '{') depth++
          else if (ch === '}') { depth--; if (depth === 0) { end = i; break } }
        }
        if (end > startBrace) {
          const jsonStr = buf.slice(startBrace, end + 1)
          const obj = tryParseJson(jsonStr)
          if (obj) mergeDeepseekResult(obj)
          buf = buf.slice(0, tagIdx) + buf.slice(end + 1)
          continue
        }
      }
    }
    break
  }
  deepseekBuffer.value = buf
}


const beforeUpload = () => false
interface UploadChangeInfo { fileList: UploadFile[]; file?: { originFileObj?: File } }
const onUploadChange = (info: UploadChangeInfo) => {
  if (processing.value || imageBase64Raw.value) return
  fileList.value = info.fileList
  const f = info.file?.originFileObj || info.fileList[0]?.originFileObj
  if (!f) return
  imageFileName.value = f.name || ''
  const reader = new FileReader()
  reader.onload = () => {
    const dataUrl = String(reader.result)
    const comma = dataUrl.indexOf(',')
    imageBase64Raw.value = dataUrl.slice(comma + 1)
    errorMsg.value = ''
  }
  reader.readAsDataURL(f)
}
const onRemove = () => {
  fileList.value = []
  imageBase64Raw.value = null
  imageFileName.value = ''
  items.value = []
  progressIndex.value = 0
  progressTotal.value = 0
  streamLogs.value = []
  combinedText.value = ''
  imageExtractText.value = ''
  imageDetailsText.value = ''
  deepseekResult.value = null
  deepseekBuffer.value = ''
}

const startStream = async () => {
  if (!imageBase64Raw.value) { message.warning('请先选择图片'); return }
  if (!totalPrompt.value || !totalPrompt.value.trim()) { message.warning('请输入总提示词'); return }
  if (abortController) { abortController.abort(); abortController = null }
  items.value = []
  progressIndex.value = 0
  progressTotal.value = 0
  processing.value = true
  errorMsg.value = ''
  streamLogs.value = []
  combinedText.value = ''
  imageExtractText.value = ''
  imageDetailsText.value = ''
  deepseekResult.value = null
  deepseekBuffer.value = ''

  const chatId = (chatIdInput.value || generateChatId()).trim()
  try {
    const file = await base64ToFile(imageBase64Raw.value, imageFileName.value || 'image.png')
    const formData = new FormData()
    formData.append('file', file)
    formData.append('message', totalPrompt.value.trim())
    formData.append('chatId', chatId)

    // 如果是复杂模式，传递额外的参数
    if (isComplexMode.value) {
      if (conversationScene.value && conversationScene.value.trim()) {
        formData.append('conversationScene', conversationScene.value.trim())
      }
      if (emotionalIndex.value !== undefined && emotionalIndex.value !== null) {
        formData.append('emotionalIndex', String(emotionalIndex.value))
      }
    }

    abortController = new AbortController()
    const response = await fetch('/api/stream-ai/travel_guide/chat/sse/emitter', { method: 'POST', body: formData, signal: abortController.signal, credentials: 'include' })
    if (!response.ok) { throw new Error(`HTTP error! status: ${response.status}`) }
    if (!response.body) { throw new Error('Response body is null') }
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    let currentEvent: string | null = null
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''
      for (const line of lines) {
        const trimmedLine = line.trim()
        if (!trimmedLine) continue
        if (trimmedLine.startsWith('event:')) { currentEvent = trimmedLine.slice(6).trim(); continue }
        if (trimmedLine.startsWith('data:')) {
          const idx = line.indexOf('data:')
          const dataStr = idx >= 0 ? line.slice(idx + 5) : trimmedLine.slice(5)
          if (dataStr) {
            try {
              const data = JSON.parse(dataStr) as StreamLogData
              handleSSEMessage(currentEvent as StreamLog['type'] | null, data)
            } catch {
              handleSSEMessage('text', dataStr)
            }
          }
          currentEvent = null
        }
      }
    }
    if (buffer.trim()) {
      const trimmedBuffer = buffer.trim()
      if (trimmedBuffer.startsWith('event:')) { currentEvent = trimmedBuffer.slice(6).trim() }
      else if (trimmedBuffer.startsWith('data:')) {
        const dataStr = buffer.slice(buffer.indexOf('data:') + 5)
        if (dataStr) {
          try { const data = JSON.parse(dataStr) as StreamLogData; handleSSEMessage(currentEvent as StreamLog['type'] | null, data) }
          catch { handleSSEMessage('text', dataStr) }
        }
      }
    }
    processing.value = false
    message.success('识别完成')
  } catch (error: unknown) {
    processing.value = false
    const errorMessage = error instanceof Error ? error.message : '识别失败'
    errorMsg.value = errorMessage
    message.error(`识别失败: ${errorMessage}`)
    addStreamLog({ type: 'error', timestamp: Date.now(), data: { message: errorMessage } })
  }
}

const handleSSEMessage = (eventType: StreamLog['type'] | null, payload: StreamLogData | string) => {
  const timestamp = Date.now()
  const logType: StreamLog['type'] = eventType === 'ocr_progress' || eventType === 'ocr_complete' || eventType === 'error' || eventType === 'complete' ? eventType : 'text'
  addStreamLog({ type: logType, timestamp, data: payload })
  if (logType === 'text') {
    const frag = extractTextFragment(payload)
    if (frag) {
      combinedText.value = combinedText.value + frag
      if (combinedText.value.length > 4000) { combinedText.value = combinedText.value.slice(-4000) }
      parseTaggedContentIncremental(frag)
      if (items.value.length === 0) { items.value.push({ text: combinedText.value }) }
      else { items.value[0].text = combinedText.value }
    }
    return
  }
  if (logType === 'ocr_progress' && typeof payload === 'object' && payload) {
    const msg = payload as StreamLogData
    const item: OcrItem = { text: msg.text || '', confidence: typeof msg.confidence === 'number' ? msg.confidence : undefined, sender: typeof msg.sender === 'string' ? msg.sender : undefined, lineIndex: typeof msg.lineIndex === 'number' ? msg.lineIndex : undefined, page: typeof msg.page === 'number' ? msg.page : undefined, isComplete: msg.isComplete !== undefined ? msg.isComplete : undefined }
    items.value.push(item)
    if (typeof msg.lineIndex === 'number' && msg.lineIndex > 0) { progressIndex.value = msg.lineIndex; progressTotal.value = Math.max(progressTotal.value, msg.lineIndex) }
    else { progressIndex.value = items.value.length; progressTotal.value = Math.max(progressTotal.value, items.value.length) }
    if (msg.isComplete === true) { processing.value = false; message.success('识别完成') }
  } else if (logType === 'ocr_complete' && typeof payload === 'object' && payload) {
    const msg = payload as StreamLogData
    processing.value = false
    if (msg.text) { const item: OcrItem = { text: msg.text || '', confidence: typeof msg.confidence === 'number' ? msg.confidence : undefined, sender: typeof msg.sender === 'string' ? msg.sender : undefined, lineIndex: typeof msg.lineIndex === 'number' ? msg.lineIndex : undefined, page: typeof msg.page === 'number' ? msg.page : undefined, isComplete: msg.isComplete !== undefined ? msg.isComplete : undefined }; items.value.push(item) }
    progressIndex.value = items.value.length
    progressTotal.value = items.value.length
    message.success('识别完成')
  } else if (logType === 'error') {
    processing.value = false
    const msg = payload as StreamLogData
    const detailMsg = (msg && (msg.detail || msg.message)) || '识别失败'
    errorMsg.value = detailMsg
    message.error(`识别失败: ${detailMsg}`)
  } else if (logType === 'complete') {
    processing.value = false
    message.success('识别完成')
  }
}


const base64ToFile = (base64: string, filename: string): Promise<File> => {
  return new Promise((resolve) => {
    const byteCharacters = atob(base64)
    const byteNumbers = new Array(byteCharacters.length)
    for (let i = 0; i < byteCharacters.length; i++) { byteNumbers[i] = byteCharacters.charCodeAt(i) }
    const byteArray = new Uint8Array(byteNumbers)
    const blob = new Blob([byteArray], { type: 'image/png' })
    const file = new File([blob], filename, { type: 'image/png' })
    resolve(file)
  })
}

const resetAll = () => {
  if (abortController) { abortController.abort(); abortController = null }
  onRemove()
  errorMsg.value = ''
  processing.value = false
}

const copyMessage = async (text: string, emotionalIndex?: number | null, e?: Event) => {
  const btn = e && (e.target as HTMLButtonElement)
  try {
    await navigator.clipboard.writeText(text)
    message.success('已复制')
    if (btn) {
      btn.classList.add('copy-animation')
      btn.classList.add('copy-success')
      const original = btn.textContent || '复制'
      btn.textContent = '拿走不谢 (。◕‿◕。)'
      setTimeout(() => {
        btn.textContent = original
        btn.classList.remove('copy-success')
        btn.classList.remove('copy-animation')
      }, 2000)
    }
  } catch {
    message.error('复制失败')
  }
}

// =================================================================================================
// ✨ Canvas 动画与加载文案
// =================================================================================================

const loadingTexts = [
  '正在连接情感雷达...',
  '正在分析对话场景...',
  '正在解读潜台词...',
  '稍等一下，马上就好...',
  '欢迎来到小扬情感雷达...',
  'AI 正在疯狂思考中...',
  '正在构建情感分析报告...',
  '识别中，请耐心等待...'
]
const currentLoadingText = ref(loadingTexts[0])
let loadingTextInterval: any = null

const startLoadingTextAnimation = () => {
  let index = 0
  currentLoadingText.value = loadingTexts[0]
  if (loadingTextInterval) clearInterval(loadingTextInterval)
  loadingTextInterval = setInterval(() => {
    index = (index + 1) % loadingTexts.length
    currentLoadingText.value = loadingTexts[index]
  }, 2500)
}

const stopLoadingTextAnimation = () => {
  if (loadingTextInterval) clearInterval(loadingTextInterval)
  loadingTextInterval = null
}

const radarCanvas = ref<HTMLCanvasElement | null>(null)
let canvasCtx: CanvasRenderingContext2D | null = null
let animationFrameId: number | null = null

const initCanvas = () => {
  if (!radarCanvas.value) return
  const canvas = radarCanvas.value
  const parent = canvas.parentElement
  if (parent) {
    canvas.width = parent.clientWidth
    canvas.height = parent.clientHeight
  }
  canvasCtx = canvas.getContext('2d')
  drawScan()
}

const drawScan = () => {
  if (!canvasCtx || !radarCanvas.value) return
  const ctx = canvasCtx
  const { width, height } = radarCanvas.value

  let scanY = 0
  const speed = 3 // 扫描速度

  const animate = () => {
    if (!processing.value) return

    ctx.clearRect(0, 0, width, height)

    // 1. 绘制扫描线（高亮线条）
    ctx.beginPath()
    ctx.moveTo(0, scanY)
    ctx.lineTo(width, scanY)
    ctx.lineWidth = 2
    ctx.strokeStyle = '#764ba2' // 紫色调
    ctx.shadowBlur = 10
    ctx.shadowColor = '#667eea'
    ctx.stroke()
    ctx.shadowBlur = 0 // 重置阴影

    // 2. 绘制扫描拖尾（渐变）
    const gradient = ctx.createLinearGradient(0, scanY, 0, scanY - 150) // 向上拖尾
    gradient.addColorStop(0, 'rgba(118, 75, 162, 0.4)')
    gradient.addColorStop(1, 'rgba(118, 75, 162, 0)')

    ctx.fillStyle = gradient
    ctx.fillRect(0, scanY - 150, width, 150)

    // 3. 随机生成一些“目标点”闪烁（可选，增加科技感）
    if (Math.random() > 0.92) {
      const x = Math.random() * width
      // 只在扫描线附近生成点
      const y = scanY - Math.random() * 50
      if (y > 0) {
        ctx.fillStyle = 'rgba(255, 255, 255, 0.8)'
        ctx.beginPath()
        ctx.arc(x, y, 2, 0, Math.PI * 2)
        ctx.fill()
      }
    }

    // 更新位置
    scanY += speed
    if (scanY > height) {
      scanY = 0
    }

    animationFrameId = requestAnimationFrame(animate)
  }
  animate()
}

watch(processing, (newVal) => {
  if (newVal) {
    startLoadingTextAnimation()
    nextTick(() => initCanvas())
  } else {
    stopLoadingTextAnimation()
    if (animationFrameId) {
      cancelAnimationFrame(animationFrameId)
      animationFrameId = null
    }
  }
})

const progressPercent = computed(() => { if (!progressTotal.value) return 0; return Math.min(100, Math.round((progressIndex.value / progressTotal.value) * 100)) })

onBeforeUnmount(() => {
  stopLoadingTextAnimation()
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
    animationFrameId = null
  }
  if (abortController) { abortController.abort(); abortController = null }
  if (continuationAbortController) { continuationAbortController.abort(); continuationAbortController = null }
})

const tryLoadByChatId = async (chatId: string) => {
  try {
    const chatResp = await api.duihuaneirongguanli.listChatContentByPage({ pageNum: 1, pageSize: 1, chatId })
    console.log('chatContentByPage resp:', chatResp)
    const chatRecord = chatResp?.data?.data?.records?.[0] || chatResp?.data?.records?.[0]
    if (chatRecord) {
      const text = chatRecord.resultContent || chatRecord.choiceContent || ''
      if (text) {
        combinedText.value = text
        parseTaggedContentIncremental(text)
      }
    }
  } catch (e) {
    console.log('chatContentByPage error:', e)
  }
  try {
    const imgResp = await api.tupianjiexiguanli.listImageAnalysisByPage({ pageNum: 1, pageSize: 1, chatId })
    console.log('imageAnalysisByPage resp:', imgResp)
    const imgRecord = imgResp?.data?.data?.records?.[0] || imgResp?.data?.records?.[0]
    if (imgRecord) {
      dbImagePath.value = imgRecord.imagePath || ''
      if (imgRecord.imageTxt) {
        imageExtractText.value = imgRecord.imageTxt
      }
    }
  } catch (e) {
    console.log('imageAnalysisByPage error:', e)
  }
}

onMounted(() => {
  const route = useRoute()
  const qId = (route.query?.chatId as string) || new URLSearchParams(location.search).get('chatId') || ''
  if (qId) {
    chatIdInput.value = qId
    tryLoadByChatId(qId)
  }
})
</script>

<style scoped>
.emotion-radar-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  flex-wrap: wrap;
  gap: 12px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
}

/* 空状态样式 */
.empty-hero {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.hero-card {
  width: 100%;
  max-width: 600px;
  border-radius: 24px !important;
  background: rgba(255, 255, 255, 0.95) !important;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15) !important;
}

.hero-content {
  text-align: center;
  padding: 20px;
}

.hero-illustration {
  font-size: 80px;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
  will-change: transform;
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-10px);
  }
}

.hero-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.hero-subtitle {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.hero-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 30px;
  line-height: 1.6;
}

.upload-button {
  margin-bottom: 16px;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3) !important;
  font-size: 16px !important;
  height: auto !important;
  padding: 12px 32px !important;
}

.upload-hint {
  font-size: 12px;
  color: #999;
  margin-bottom: 30px;
}

.preset-panel {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 20px;
  margin-top: 20px;
}

.preset-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  font-weight: 500;
}

.preset-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.preset-tag {
  border-radius: 16px !important;
  background: white !important;
  border: 1px solid #e8e8e8 !important;
  padding: 6px 16px !important;
  cursor: pointer;
  transition: all 0.3s;
}

.preset-tag:hover {
  border-color: #667eea !important;
  color: #667eea !important;
  transform: translateY(-2px);
}

/* 玻璃态卡片 */
.glass-card {
  background: rgba(255, 255, 255, 0.95) !important;
  border-radius: 20px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 图片预览区域 */
.image-stage {
  position: relative;
  width: 90%;
  max-width: 900px;
  margin: 0 auto;
  min-height: 260px;
  max-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  overflow: hidden;
  background: #fafafa;
  border: 2px solid #f0f0f0;
}

.image-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: auto;
}

.preview-img {
  width: 100%;
  height: auto;
  max-height: 70vh;
  display: block;
  object-fit: contain;
}

.scan-canvas {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 10;
  pointer-events: none;
}

.dynamic-loading-text {
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 12px;
  min-height: 24px;
  transition: all 0.5s ease;
}

.fade-in-text {
  animation: textFadeIn 0.5s ease-out;
}

@keyframes textFadeIn {
  from {
    opacity: 0;
    transform: translateY(5px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 优化卡片样式细节 */
.glass-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.glass-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12) !important;
}

/* 优化按钮样式 */
.ant-btn-round {
  border-radius: 50px;
}

/* 上传组件样式 */
:deep(.ant-upload-select) {
  width: 120px;
  height: 120px;
}

:deep(.ant-upload-list-picture-card .ant-upload-list-item) {
  width: 120px;
  height: 120px;
}

/* 结果列表 */
.result-list {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px 0;
}

.result-item {
  padding: 12px;
  margin-bottom: 12px;
  border-radius: 12px;
  background: #fafafa;
  border: 1px solid #e8e8e8;
  transition: all 0.2s;
}

.result-item:hover {
  background: #f0f0f0;
  border-color: #d9d9d9;
  transform: translateX(4px);
}

.empty-result {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty-logs {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

/* 日志容器 */
.stream-logs-container {
  max-height: 500px;
  overflow-y: auto;
  padding: 8px 0;
}

.stream-logs-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stream-log-item {
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
  background: #fafafa;
  transition: all 0.2s;
}

.stream-log-item:hover {
  background: #f0f0f0;
  border-color: #d9d9d9;
}

.stream-log-item.ocr_progress {
  border-left: 3px solid #1890ff;
}

.stream-log-item.ocr_complete {
  border-left: 3px solid #52c41a;
}

.stream-log-item.error {
  border-left: 3px solid #ff4d4f;
}

.stream-log-item.complete {
  border-left: 3px solid #13c2c2;
}

.log-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.log-time {
  font-size: 12px;
  color: #666;
  font-family: 'Monaco', 'Menlo', monospace;
}

.log-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.log-data {
  margin: 0;
  padding: 8px;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
  font-size: 12px;
  font-family: 'Monaco', 'Menlo', monospace;
  color: #333;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
}

.stream-plain {
  padding: 8px 0;
}

.stream-plain-text {
  margin: 0;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  font-size: 13px;
  line-height: 1.7;
  font-family: 'Monaco', 'Menlo', monospace;
  color: #333;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 消息卡片样式 */
.message {
  margin: 20px 0;
  padding: 20px;
  border-left: 5px solid #667eea;
  background: linear-gradient(145deg, #f8f9fa, #e9ecef);
  border-radius: 12px;
  position: relative;
  transition: transform 0.2s ease;
  will-change: transform, opacity;
}

.message:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.emotion-index {
  font-weight: bold;
  color: #667eea;
  margin-bottom: 12px;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.message-content {
  line-height: 1.8;
  margin-right: 70px;
  color: #333;
  white-space: pre-wrap;
  word-wrap: break-word;
  word-break: break-word;
}

.loading-text {
  background: linear-gradient(90deg, #dddddd 0%, #eeeeee 50%, #dddddd 100%);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  animation: shimmer 1.5s linear infinite;
}

@keyframes shimmer {
  0% {
    background-position: 0% 0;
  }

  100% {
    background-position: 200% 0;
  }
}



.celebrate {
  animation: celebratePop 2s ease-out;
  will-change: transform, opacity;
}

@keyframes celebratePop {
  0% {
    transform: scale(0.96);
  }

  70% {
    transform: scale(1.06);
  }

  100% {
    transform: scale(1);
  }
}

.celebrate::after {
  content: '🎉';
  position: absolute;
  top: -8px;
  right: -8px;
  font-size: 24px;
  animation: floatUp 1s ease-out;
  opacity: 0;
}

@keyframes floatUp {
  0% {
    transform: translateY(12px);
    opacity: 0;
  }

  40% {
    opacity: 1;
  }

  100% {
    transform: translateY(-16px);
    opacity: 0;
  }
}

.celebrate-overlay {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.confetti {
  position: absolute;
  bottom: 8px;
  width: 8px;
  height: 18px;
  border-radius: 2px;
  transform: translate(0, 0) rotate(0);
  opacity: 0;
  animation: confettiFly 1s ease-out forwards;
  will-change: transform, opacity;
}

.c1 {
  left: 5%;
  background: #ff7f50;
  --dx: 60px;
}

.c2 {
  left: 15%;
  background: #ffd700;
  --dx: 80px;
}

.c3 {
  left: 28%;
  background: #7fffd4;
  --dx: 100px;
}

.c4 {
  left: 42%;
  background: #adff2f;
  --dx: 120px;
}

.c5 {
  left: 58%;
  background: #87cefa;
  --dx: 140px;
}

.c6 {
  left: 70%;
  background: #ff69b4;
  --dx: 160px;
}

.c7 {
  left: 78%;
  background: #ffa500;
  --dx: 180px;
}

.c8 {
  left: 85%;
  background: #00fa9a;
  --dx: 200px;
}

.c9 {
  left: 90%;
  background: #ba55d3;
  --dx: 220px;
}

.c10 {
  left: 35%;
  background: #87cefa;
  --dx: 110px;
}

.c11 {
  left: 50%;
  background: #ff7f50;
  --dx: 150px;
}

.c12 {
  left: 95%;
  background: #ffd700;
  --dx: 240px;
}

@keyframes confettiFly {
  0% {
    transform: translate(0, 0) rotate(0deg);
    opacity: 0;
  }

  20% {
    opacity: 1;
  }

  100% {
    transform: translate(var(--dx), -160%) rotate(360deg);
    opacity: 0;
  }
}

.emoji {
  position: absolute;
  bottom: 0;
  font-size: 20px;
  transform: translate(0, 0) scale(0.9);
  opacity: 0;
  animation: emojiRise 1s ease-out forwards;
  will-change: transform, opacity;
}

.e1 {
  left: 12%;
}

.e2 {
  left: 50%;
}

.e3 {
  left: 88%;
}

@keyframes emojiRise {
  0% {
    transform: translate(0, 8px) scale(0.9);
    opacity: 0;
  }

  30% {
    opacity: 1;
  }

  100% {
    transform: translate(160px, -80px) scale(1);
    opacity: 0;
  }
}

.copy-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: linear-gradient(145deg, #667eea, #764ba2);
  color: white;
  border: none;
  padding: 12px 18px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  min-width: 70px;
  text-align: center;
}

.copy-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.copy-btn:active {
  transform: translateY(0);
}

.copy-success {
  background: linear-gradient(145deg, #52c41a, #389e0d) !important;
}

.copy-animation {
  animation: copyPulse 0.6s ease-out;
}

@keyframes copyPulse {
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.05);
    box-shadow: 0 0 20px rgba(102, 126, 234, 0.6);
  }

  100% {
    transform: scale(1);
  }
}

.clickable-tag {
  cursor: pointer;
  user-select: none;
}

.loading-card {
  border: 2px solid transparent;
  background: linear-gradient(#ffffff, #ffffff) padding-box,
    linear-gradient(135deg, #667eea, #764ba2) border-box;
  background-size: 100% 100%, 200% 100%;
  animation: borderPulse 2.4s ease-in-out infinite;
}

@keyframes borderPulse {
  0% {
    background-position: 0 0, 0% 0;
    box-shadow: 0 0 0 rgba(118, 75, 162, 0.0);
  }

  50% {
    background-position: 0 0, 100% 0;
    box-shadow: 0 6px 18px rgba(118, 75, 162, 0.18);
  }

  100% {
    background-position: 0 0, 0% 0;
    box-shadow: 0 0 0 rgba(118, 75, 162, 0.0);
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.loading-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #764ba2;
}

.loading-text-plain {
  letter-spacing: 0.5px;
}

.typing-dots i {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #764ba2;
  margin-right: 3px;
  animation: dotPulse 1.2s ease-in-out infinite;
}

.typing-dots i:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dots i:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes dotPulse {

  0%,
  100% {
    transform: scale(0.8);
    opacity: 0.5;
  }

  50% {
    transform: scale(1.2);
    opacity: 1;
  }
}

.loading-progress {
  width: 100%;
  height: 6px;
  border-radius: 6px;
  background: #eee;
  overflow: hidden;
}

.loading-progress .bar {
  width: 40%;
  height: 100%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 6px;
  animation: progressSlide 1.6s ease-in-out infinite;
}

@keyframes progressSlide {
  0% {
    transform: translateX(-120%);
  }

  50% {
    transform: translateX(60%);
  }

  100% {
    transform: translateX(220%);
  }
}

/* 背景分析卡片 */
.background-info {
  background: linear-gradient(145deg, #e8f4fd, #d1ecf1);
  margin: 20px 0;
  padding: 25px;
  border-radius: 16px;
  border-left: 5px solid #17a2b8;
  box-shadow: 0 4px 15px rgba(23, 162, 184, 0.1);
}

.background-title {
  font-size: 16px;
  font-weight: 600;
  color: #17a2b8;
  margin-bottom: 20px;
  text-align: center;
}

.background-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 15px;
}

.background-item {
  background: rgba(255, 255, 255, 0.8);
  padding: 15px;
  border-radius: 12px;
  border: 1px solid rgba(23, 162, 184, 0.2);
  transition: all 0.3s ease;
}

.background-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(23, 162, 184, 0.2);
  background: rgba(255, 255, 255, 0.95);
}

.background-label {
  font-weight: 600;
  color: #666;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

.background-value {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
  font-weight: 500;
}

.emotional-summary {
  background: linear-gradient(145deg, #fff3cd, #ffeaa7);
  border: 2px solid #ffc107;
  border-radius: 12px;
  padding: 15px;
  margin-top: 20px;
  text-align: center;
}

.emotional-index-main {
  font-size: 20px;
  font-weight: bold;
  color: #856404;
  margin-bottom: 8px;
}

.emotional-reason {
  color: #856404;
  font-size: 13px;
  font-style: italic;
}

/* 淡入动画 */
.fade-in {
  animation: fadeInUp 0.4s ease-out;
  will-change: transform, opacity;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .emotion-radar-page {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
    padding: 12px 16px;
  }

  .page-header h2 {
    font-size: 20px;
    margin-bottom: 12px;
  }

  .hero-illustration {
    font-size: 60px;
  }

  .hero-title {
    font-size: 24px;
  }

  .image-stage {
    width: 100%;
    max-height: 75vh;
    margin: 0 auto;
  }

  .preview-img {
    width: 100%;
    height: auto;
    max-height: 80vh;
  }

  .background-grid {
    grid-template-columns: 1fr;
  }

  .message-content {
    margin-right: 80px;
  }

  .copy-btn {
    min-width: 65px;
    padding: 10px 16px;
  }
}

/* 优先照顾低动效偏好用户，避免高频动画造成不适 */
@media (prefers-reduced-motion: reduce) {

  .hero-illustration,
  .scan-overlay,
  .loading-text,
  .typing-dots i,
  .loading-progress .bar,
  .celebrate,
  .confetti,
  .emoji,
  .fade-in {
    animation: none !important;
    transition: none !important;
  }
}

/* 列表过渡效果（TransitionGroup） */
.list-fade-enter-active,
.list-fade-leave-active {
  transition: all .25s ease;
}

.list-fade-enter-from,
.list-fade-leave-to {
  opacity: 0;
  transform: translateY(6px);
}

@media (min-width: 768px) and (max-width: 1200px) {
  .image-stage {
    width: 85%;
    max-width: 800px;
    max-height: 55vh;
    margin: 0 auto;
  }

  .preview-img {
    max-height: 60vh;
  }
}
</style>
