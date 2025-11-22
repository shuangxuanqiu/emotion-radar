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
          <div class="scan-overlay" v-if="processing"></div>
          <div class="image-container">
            <img v-if="imageUrl" :src="imageUrl" class="preview-img" />
          </div>
        </div>
      </a-card>

      <a-card class="glass-card" :bordered="false" style="margin-top: 16px">
        <div class="section-title">✏️ 总提示词</div>
        <a-textarea v-model:value="totalPrompt" :rows="6" placeholder="请输入本次识别的总提示词..." allow-clear />
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
        <div v-if="processing" style="margin-top: 12px">
          <LoadingSpinner text="识别中..." type="wave" />
          <a-progress :percent="progressPercent" status="active" />
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
        <div class="message" v-if="imageExtractText">
          <div class="message-content">{{ imageExtractText }}</div>
        </div>
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
            <a-tag v-for="t in emotionTags" :key="t" class="clickable-tag" @click="handleEmotionTagClick(t)">{{ t
              }}</a-tag>
            <a-input v-if="addTagVisible" v-model:value="newTag" placeholder="输入标签" style="width: 160px" />
            <a-button v-if="addTagVisible" type="primary" size="small" @click="confirmAddTag">添加</a-button>
            <a-button type="dashed" size="small" @click="toggleAddTag">添加标签</a-button>
          </a-space>
        </div>
        <div class="message fade-in" v-for="(m, i) in deepseekResult?.messages || []" :key="i">
          <a-space wrap style="margin-bottom: 8px;">
            <a-tag color="geekblue" v-if="m.conversationScene">{{ m.conversationScene }}</a-tag>
            <a-tag color="orange" v-if="m.relationshipType">{{ m.relationshipType }}</a-tag>
            <a-tag color="purple" v-if="m.topicNature">{{ m.topicNature }}</a-tag>
            <a-tag color="cyan" v-if="m.userToneCharacteristics">{{ m.userToneCharacteristics }}</a-tag>
            <a-tag color="green" v-if="typeof m.emotionalIndex === 'number'">情感: {{ m.emotionalIndex }}</a-tag>
          </a-space>
          <div class="emotion-index" v-if="m.emotionalReason">{{ m.emotionalReason }}</div>
          <div class="message-content">{{ m.message }}</div>
          <button class="copy-btn" type="button"
            @click="copyMessage(m.message || '', m.emotionalIndex, $event)">复制</button>
        </div>
        <div v-if="processing && (!deepseekResult?.messages || deepseekResult?.messages.length === 0)"
          style="padding: 12px;">
          <a-skeleton active :paragraph="{ rows: 3 }" />
        </div>
      </div>
    </a-card>

    <!-- 实时返回内容卡片 -->
    <a-card class="glass-card" :bordered="false" style="margin-top: 16px">
      <template #title>
        <div class="section-title">📡 实时返回内容</div>
        <a-button size="small" type="link" @click="clearStreamLogs" style="float: right; padding: 0;">清空日志</a-button>
      </template>
      <div class="stream-plain">
        <pre class="stream-plain-text">{{ combinedText || '暂无返回内容' }}</pre>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
/// <reference types="@/api/typings" />
import { ref, computed, onBeforeUnmount, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { UploadOutlined, PlayCircleOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { useRoute } from 'vue-router'
import api from '@/api'
import { getImageDisplayUrl } from '@/utils/image'

type OcrItem = {
  text: string
  confidence?: number
  sender?: string
  lineIndex?: number
  page?: number
  isComplete?: boolean | null
}

type DeepSeekBackground = {
  conversationScene?: string
  relationshipType?: string
  topicNature?: string
  userToneCharacteristics?: string
}

type DeepSeekMessage = {
  conversationScene?: string
  emotionalIndex?: number
  emotionalReason?: string
  message?: string
  overallEmotionalIndex?: number
  relationshipType?: string
  topicNature?: string
  userToneCharacteristics?: string
}

type DeepSeekResult = {
  backgroundAnalysis?: DeepSeekBackground
  emotionalReason?: string
  messages?: DeepSeekMessage[]
  overallEmotionalIndex?: number
}

const processing = ref(false)
const errorMsg = ref('')
const progressIndex = ref(0)
const progressTotal = ref(0)
const items = ref<OcrItem[]>([])
const combinedText = ref('')
const totalPrompt = ref('')
const canStart = computed(() => !!imageBase64Raw.value && !processing.value && !!totalPrompt.value?.trim())
const hasContent = computed(() => !!imageBase64Raw.value || items.value.length > 0 || !!imageExtractText.value || !!imageDetailsText.value || !!deepseekResult.value)
const generateChatId = (): string => {
  return `ocr_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}
const chatIdInput = ref(generateChatId())
const refreshChatId = () => {
  chatIdInput.value = generateChatId()
}
const deepseekResult = ref<DeepSeekResult | null>(null)

const emotionTags = ref<string[]>(['推荐', '高情商', '哄女友', '暧昧拉扯', '霸道总裁', '撩女生'])
const addTagVisible = ref(false)
const newTag = ref('')
const toggleAddTag = () => { addTagVisible.value = !addTagVisible.value }
const confirmAddTag = () => {
  const v = newTag.value.trim()
  if (!v) { message.warning('请输入标签'); return }
  if (!emotionTags.value.includes(v)) { emotionTags.value.push(v) }
  newTag.value = ''
  addTagVisible.value = false
}
const handleEmotionTagClick = async (label: string) => {
  const idx = typeof deepseekResult.value?.overallEmotionalIndex === 'number' ? (deepseekResult.value as DeepSeekResult).overallEmotionalIndex as number : 6
  const chatId = (chatIdInput.value || generateChatId()).trim()
  const params = { emotionalIndex: idx, emotionalLabels: label, chatId }
  console.log('continuationChatStream params:', params)
  try {
    const resp = await api.emotionRadarStreamController.continuationChatStream(params)
    console.log('continuationChatStream resp:', resp)
  } catch (e) {
    console.log('continuationChatStream error:', e)
  }
  if (!deepseekResult.value) { deepseekResult.value = { messages: [] } }
  const msg: DeepSeekMessage = { conversationScene: '续写', topicNature: '情绪标签', userToneCharacteristics: label, emotionalIndex: idx, message: `[续写触发] ${label}` }
  deepseekResult.value!.messages = [msg, ...((deepseekResult.value!.messages as DeepSeekMessage[]) || [])]
  const existingJson = deepseekResult.value ? JSON.stringify(deepseekResult.value) : ''
  const requestJson = JSON.stringify(params)
  combinedText.value += (existingJson ? existingJson + '\n' : '') + requestJson + '\n'
}

interface UploadFile { uid: string; name: string; status?: string; originFileObj?: File }
const fileList = ref<UploadFile[]>([])
const imageBase64Raw = ref<string | null>(null)
const imageFileName = ref<string>('')
const dbImagePath = ref<string>('')
const imageUrl = computed(() => {
  if (imageBase64Raw.value) return `data:image/png;base64,${imageBase64Raw.value}`
  if (dbImagePath.value) return getImageDisplayUrl(dbImagePath.value)
  return ''
})

let abortController: AbortController | null = null

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

type StreamLog = { type: 'ocr_progress' | 'ocr_complete' | 'error' | 'complete' | 'text'; timestamp: number; data: StreamLogData | string }
const streamLogs = ref<StreamLog[]>([])
const MAX_LOG_COUNT = 200

const addStreamLog = (log: StreamLog) => {
  const dataSanitized = typeof log.data === 'object' && log.data !== null ? { ...(log.data as Record<string, unknown>) } : log.data
  if (typeof dataSanitized === 'object') { delete (dataSanitized as Record<string, unknown>).bbox }
  streamLogs.value.unshift({ ...log, data: dataSanitized })
  if (streamLogs.value.length > MAX_LOG_COUNT) { streamLogs.value = streamLogs.value.slice(0, MAX_LOG_COUNT) }
}
const clearStreamLogs = () => { streamLogs.value = []; combinedText.value = '' }

const extractTextFragment = (payload: StreamLogData | string): string => {
  if (typeof payload === 'string') return ''
  if (!payload) return ''
  const v = (payload as Record<string, unknown>)['G']
  return typeof v === 'string' ? v : ''
}

const imageExtractText = ref('')
const imageDetailsText = ref('')
const deepseekBuffer = ref('')
const tryParseJson = (s: string): DeepSeekResult | null => {
  try { return JSON.parse(s) as DeepSeekResult } catch { return null }
}
const mergeDeepseekResult = (obj: DeepSeekResult) => {
  if (!obj) return
  if (!deepseekResult.value) { deepseekResult.value = { backgroundAnalysis: {}, messages: [], overallEmotionalIndex: undefined, emotionalReason: undefined } }
  const cur = deepseekResult.value as DeepSeekResult
  if (obj.backgroundAnalysis) { cur.backgroundAnalysis = { ...(cur.backgroundAnalysis || {}), ...obj.backgroundAnalysis } }
  if (typeof obj.overallEmotionalIndex === 'number') { cur.overallEmotionalIndex = obj.overallEmotionalIndex }
  if (obj.emotionalReason) { cur.emotionalReason = obj.emotionalReason }
  if (Array.isArray(obj.messages) && obj.messages.length) { cur.messages = [...(cur.messages || []), ...obj.messages] }
  deepseekResult.value = { ...cur }
}
const parseTaggedContentIncremental = (incoming: string) => {
  if (!incoming) return
  const s = incoming
  const imgRegex = /<ggy>\[image]\s*(?:```(?:json|text)?\s*([\s\S]*?)```|([\s\S]*?)(?=<ggy>|$))/ig
  let imgMatch
  while ((imgMatch = imgRegex.exec(s)) !== null) {
    const content = (imgMatch[1] || imgMatch[2] || '').trim()
    if (content) { imageExtractText.value = imageExtractText.value ? `${imageExtractText.value}\n${content}` : content }
  }
  const detailsRegex = /---截图细节---\s*([\s\S]*?)(?=<ggy>|$)/g
  let detMatch
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

const getLogTagColor = (type: string): string => {
  switch (type) {
    case 'ocr_progress': return 'blue'
    case 'ocr_complete': return 'green'
    case 'error': return 'red'
    case 'complete': return 'cyan'
    default: return 'default'
  }
}
const getLogTypeLabel = (type: string): string => {
  switch (type) {
    case 'ocr_progress': return '进度'
    case 'ocr_complete': return '完成'
    case 'error': return '错误'
    case 'complete': return '完成'
    case 'text': return '文本'
    default: return type
  }
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
      combinedText.value += frag
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

const formatTimestamp = (timestamp: number): string => {
  const date = new Date(timestamp)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  const ms = String(date.getMilliseconds()).padStart(3, '0')
  return `${hours}:${minutes}:${seconds}.${ms}`
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

const progressPercent = computed(() => { if (!progressTotal.value) return 0; return Math.min(100, Math.round((progressIndex.value / progressTotal.value) * 100)) })

onBeforeUnmount(() => { if (abortController) { abortController.abort(); abortController = null } })

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
  -webkit-text-fill-color: transparent;
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

.scan-overlay {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0) 0%, rgba(102, 126, 234, 0.15) 50%, rgba(255, 255, 255, 0) 100%);
  animation: scanMove 1.8s linear infinite;
  z-index: 10;
  pointer-events: none;
}

@keyframes scanMove {
  0% {
    transform: translateY(-100%);
  }

  100% {
    transform: translateY(100%);
  }
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
