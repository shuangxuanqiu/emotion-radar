<template>
  <div class="ocr-recognition-management modern-gradient-bg">
    <div class="page-header colorful-page-header">
      <h2>OCR识别</h2>
      <a-space>
        <a-input v-model:value="chatIdInput" placeholder="请输入 chatId" style="width: 280px" size="middle" />
        <a-button @click="refreshChatId" :disabled="processing" size="middle" shape="round" ghost>
          <template #icon>
            <ReloadOutlined />
          </template>
          刷新 chatId
        </a-button>
      </a-space>
      <div class="header-actions">
        <a-space>
          <a-tag :color="processing ? 'processing' : 'default'">
            {{ processing ? '识别中' : '待识别' }}
          </a-tag>
        </a-space>
      </div>
    </div>

    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :lg="10">
        <a-card class="search-card colorful-search-card" :bordered="false">
          <div class="section-title">上传图片</div>
          <a-upload list-type="picture-card" :file-list="fileList" :max-count="1" :before-upload="beforeUpload"
            :disabled="processing || !!imageBase64Raw" @change="onUploadChange" @remove="onRemove">
            <div>
              <UploadOutlined />
              <div style="margin-top: 8px">选择图片</div>
            </div>
          </a-upload>
          <a-space style="margin-top: 12px">
            <a-button type="primary" :disabled="!canStart" @click="startOcr" size="middle" shape="round">
              <template #icon>
                <PlayCircleOutlined />
              </template>
              开始分析
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
            <LoadingSpinner text="分析中..." type="wave" />
            <a-progress :percent="progressPercent" status="active" />
          </div>
        </a-card>

        <a-card class="search-card colorful-search-card" :bordered="false" style="margin-top: 16px">
          <div class="section-title">总提示词</div>
          <a-textarea v-model:value="totalPrompt" :rows="6" placeholder="请输入本次识别的总提示词..." allow-clear />
          <div style="margin-top: 8px; color: #999; font-size: 12px;">开始识别将使用该提示词发送到后端</div>
        </a-card>

        <a-card class="table-card colorful-table-card" :bordered="false" style="margin-top: 16px">
          <div class="section-title">识别结果</div>
          <div class="result-list">
            <div class="result-item" v-for="(it, idx) in items" :key="idx">
              <a-space direction="vertical" size="small" style="width: 100%">
                <a-space>
                  <a-tag color="blue">{{ idx + 1 }}/{{ items.length }}</a-tag>
                  <a-tag v-if="it.lineIndex" color="purple">行{{ it.lineIndex }}</a-tag>
                  <a-tag v-if="it.sender" color="orange">{{ it.sender }}</a-tag>
                  <a-tag v-if="it.confidence" color="cyan">{{ (it.confidence * 100).toFixed(1) }}%</a-tag>
                </a-space>
                <span style="font-size: 14px; word-break: break-all; white-space: pre-wrap;">{{ it.text }}</span>
              </a-space>
            </div>
            <div v-if="items.length === 0" class="empty-result">
              <span>暂无识别结果</span>
            </div>
          </div>
        </a-card>

        <a-card v-if="deepseekResult" class="table-card colorful-table-card strategy-card fade-in" :bordered="false"
          style="margin-top: 16px">
          <div class="section-title">对话策略</div>
          <a-space direction="vertical" style="width: 100%">
            <a-space wrap>
              <a-tag color="gold" v-if="deepseekResult?.backgroundAnalysis?.conversationScene">场景：{{
                deepseekResult?.backgroundAnalysis?.conversationScene }}</a-tag>
              <a-tag color="blue" v-if="deepseekResult?.backgroundAnalysis?.relationshipType">关系：{{
                deepseekResult?.backgroundAnalysis?.relationshipType }}</a-tag>
              <a-tag color="purple" v-if="deepseekResult?.backgroundAnalysis?.topicNature">话题：{{
                deepseekResult?.backgroundAnalysis?.topicNature }}</a-tag>
              <a-tag color="cyan" v-if="deepseekResult?.backgroundAnalysis?.userToneCharacteristics">语气：{{
                deepseekResult?.backgroundAnalysis?.userToneCharacteristics }}</a-tag>
              <a-tag color="green" v-if="typeof deepseekResult?.overallEmotionalIndex === 'number'">总体情感：{{
                deepseekResult?.overallEmotionalIndex }}</a-tag>
            </a-space>
            <div v-if="deepseekResult?.emotionalReason" class="strategy-reason">{{ deepseekResult?.emotionalReason }}
            </div>
            <div class="message-list">
              <div class="message-item fade-in" v-for="(m, i) in deepseekResult?.messages || []" :key="i">
                <a-space wrap>
                  <a-tag color="geekblue" v-if="m.conversationScene">{{ m.conversationScene }}</a-tag>
                  <a-tag color="orange" v-if="m.relationshipType">{{ m.relationshipType }}</a-tag>
                  <a-tag color="purple" v-if="m.topicNature">{{ m.topicNature }}</a-tag>
                  <a-tag color="cyan" v-if="m.userToneCharacteristics">{{ m.userToneCharacteristics }}</a-tag>
                  <a-tag color="green" v-if="typeof m.emotionalIndex === 'number'">情感：{{ m.emotionalIndex }}</a-tag>
                </a-space>
                <div class="message-text">{{ m.message }}</div>
              </div>
            </div>
          </a-space>
        </a-card>

        <a-card v-if="imageExtractText || imageDetailsText" class="table-card colorful-table-card fade-in"
          :bordered="false" style="margin-top: 16px">
          <div class="section-title">截图解析</div>
          <a-space direction="vertical" style="width: 100%">
            <div v-if="imageExtractText">
              <a-tag color="blue">聊天内容提取</a-tag>
              <pre class="log-data" style="margin-top: 8px">{{ imageExtractText }}</pre>
            </div>
            <div v-if="imageDetailsText" style="margin-top: 12px">
              <a-tag color="purple">截图细节</a-tag>
              <pre class="log-data" style="margin-top: 8px">{{ imageDetailsText }}</pre>
            </div>
          </a-space>
        </a-card>


      </a-col>

      <a-col :xs="24" :lg="14">
        <a-card class="table-card colorful-table-card" :bordered="false">
          <div class="section-title">预览</div>
          <div class="image-stage">
            <div class="scan-overlay" v-if="processing"></div>
            <div class="image-container">
              <img v-if="imageUrl" :src="imageUrl" class="preview-img" />
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 实时返回内容展示区域 -->
    <a-card class="table-card colorful-table-card" :bordered="false" style="margin-top: 16px">
      <template #title>
        <div class="section-title">实时返回内容</div>
        <a-button size="small" type="link" @click="clearStreamLogs" style="float: right; padding: 0;">
          清空日志
        </a-button>
      </template>
      <div class="stream-logs-container">
        <div v-if="streamLogs.length === 0" class="empty-logs">
          <span>暂无返回内容</span>
        </div>
        <div v-else class="stream-logs-list">
          <div v-for="(log, idx) in streamLogs" :key="idx" class="stream-log-item" :class="log.type">
            <div class="log-header">
              <a-tag :color="getLogTagColor(log.type)" size="small">
                {{ getLogTypeLabel(log.type) }}
              </a-tag>
              <span class="log-time">{{ formatTimestamp(log.timestamp) }}</span>
            </div>
            <div class="log-content">
              <pre class="log-data">{{ JSON.stringify(log.data, null, 2) }}</pre>
            </div>
          </div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
/// <reference types="@/api/typings" />
import { ref, computed, onBeforeUnmount } from 'vue'
import { message } from 'ant-design-vue'
import { UploadOutlined, PlayCircleOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
// 导入 API 类型定义（虽然不直接使用 api 对象，但需要类型定义）

import api from '@/api'

// 格式化时间戳为可读时间
const formatTimestamp = (timestamp: number): string => {
  const date = new Date(timestamp)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  const ms = String(date.getMilliseconds()).padStart(3, '0')
  return `${hours}:${minutes}:${seconds}.${ms}`
}

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
const generateChatId = (): string => {
  return `ocr_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}
const chatIdInput = ref(generateChatId())
const refreshChatId = () => {
  chatIdInput.value = generateChatId()
}
const deepseekResult = ref<DeepSeekResult | null>(null)
const eventCounters = ref({ text: 0, ocr_progress: 0, ocr_complete: 0, error: 0, complete: 0 })
const summaryLogged = ref(false)

interface UploadFile {
  uid: string
  name: string
  status?: string
  originFileObj?: File
}

const fileList = ref<UploadFile[]>([])
const imageBase64Raw = ref<string | null>(null)
const imageFileName = ref<string>('')
const imageUrl = computed(() => (imageBase64Raw.value ? `data:image/png;base64,${imageBase64Raw.value}` : ''))



// SSE 相关
let eventSource: EventSource | null = null
let abortController: AbortController | null = null

// 流式日志
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

type StreamLog = {
  type: 'ocr_progress' | 'ocr_complete' | 'error' | 'complete' | 'text'
  timestamp: number
  data: StreamLogData | string
}

const streamLogs = ref<StreamLog[]>([])
const MAX_LOG_COUNT = 200 // 最多保留 200 条日志

// 添加流式日志
const addStreamLog = (log: StreamLog) => {
  const dataSanitized =
    typeof log.data === 'object' && log.data !== null
      ? { ...(log.data as Record<string, unknown>) }
      : log.data
  if (typeof dataSanitized === 'object') {
    delete (dataSanitized as Record<string, unknown>).bbox
  }
  streamLogs.value.unshift({ ...log, data: dataSanitized })
  // 限制日志数量
  if (streamLogs.value.length > MAX_LOG_COUNT) {
    streamLogs.value = streamLogs.value.slice(0, MAX_LOG_COUNT)
  }
}

// 清空流式日志
const clearStreamLogs = () => {
  streamLogs.value = []
}

const extractTextFragment = (payload: StreamLogData | string): string => {
  if (typeof payload === 'string') return payload
  if (!payload) return ''
  const candidateKeys = ['G', 'text', 'content']
  for (const k of candidateKeys) {
    const v = (payload as Record<string, unknown>)[k]
    if (typeof v === 'string') return v
  }
  return ''
}

const imageExtractText = ref('')
const imageDetailsText = ref('')
const parseTaggedContent = (s: string) => {
  if (!s) return
  const imgBlockMatch = s.match(/<ggy>\[image]\s*```([\s\S]*?)```/)
  const detailsMatch = s.match(/---截图细节---\s*([\s\S]*?)(?=<ggy>|$)/)
  const deepseekMatch = s.match(/<ggy>\[deepseek]\s*```json\s*([\s\S]*?)```/)
  if (imgBlockMatch && imgBlockMatch[1]) {
    imageExtractText.value = imgBlockMatch[1].trim()
  }
  if (detailsMatch && detailsMatch[1]) {
    imageDetailsText.value = detailsMatch[1].trim()
  }
  if (deepseekMatch && deepseekMatch[1]) {
    try {
      const obj = JSON.parse(deepseekMatch[1])
      if (obj && (obj.backgroundAnalysis || obj.messages)) {
        deepseekResult.value = obj as DeepSeekResult
      }
    } catch { }
  }
}

// 获取日志标签颜色
const getLogTagColor = (type: string): string => {
  switch (type) {
    case 'ocr_progress':
      return 'blue'
    case 'ocr_complete':
      return 'green'
    case 'error':
      return 'red'
    case 'complete':
      return 'cyan'
    default:
      return 'default'
  }
}

// 获取日志类型标签
const getLogTypeLabel = (type: string): string => {
  switch (type) {
    case 'ocr_progress':
      return '进度'
    case 'ocr_complete':
      return '完成'
    case 'error':
      return '错误'
    case 'complete':
      return '完成'
    case 'text':
      return '文本'
    default:
      return type
  }
}

const beforeUpload = () => false

interface UploadChangeInfo {
  fileList: UploadFile[]
  file?: {
    originFileObj?: File
  }
}

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
}



const startOcr = async () => {
  if (!imageBase64Raw.value) {
    message.warning('请先选择图片')
    return
  }

  // 关闭之前的连接
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
  if (abortController) {
    abortController.abort()
    abortController = null
  }

  items.value = []
  progressIndex.value = 0
  progressTotal.value = 0
  processing.value = true
  errorMsg.value = ''
  streamLogs.value = []
  combinedText.value = ''
  deepseekResult.value = null
  eventCounters.value = { text: 0, ocr_progress: 0, ocr_complete: 0, error: 0, complete: 0 }
  summaryLogged.value = false

  const chatId = (chatIdInput.value || generateChatId()).trim()

  try {
    const file = await base64ToFile(imageBase64Raw.value, imageFileName.value || 'image.png')

    if (!totalPrompt.value || !totalPrompt.value.trim()) {
      message.warning('请输入总提示词')
      processing.value = false
      return
    }
    const formData = new FormData()
    formData.append('file', file)
    formData.append('message', totalPrompt.value.trim())
    formData.append('chatId', chatId)

    abortController = new AbortController()
    const response = await fetch('/api/stream-ai/travel_guide/chat/sse/emitter', {
      method: 'POST',
      body: formData,
      signal: abortController.signal,
      credentials: 'include',
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    if (!response.body) {
      throw new Error('Response body is null')
    }

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
      if (trimmedBuffer.startsWith('event:')) {
        currentEvent = trimmedBuffer.slice(6).trim()
      } else if (trimmedBuffer.startsWith('data:')) {
        const dataStr = buffer.slice(buffer.indexOf('data:') + 5)
        if (dataStr) {
          try {
            const data = JSON.parse(dataStr) as StreamLogData
            handleSSEMessage(currentEvent as StreamLog['type'] | null, data)
          } catch {
            handleSSEMessage('text', dataStr)
          }
        }
      }
    }

    processing.value = false
    message.success('识别完成')
    logSseSummary()
  } catch (error: unknown) {
    processing.value = false
    if (error instanceof Error && error.name === 'AbortError') {
      errorMsg.value = '请求已取消'
      message.warning('请求已取消')
    } else {
      const errorMessage = error instanceof Error ? error.message : '识别失败'
      errorMsg.value = errorMessage
      message.error(`识别失败: ${errorMessage}`)
      addStreamLog({
        type: 'error',
        timestamp: Date.now(),
        data: { message: errorMessage },
      })
    }
  }
}

// 处理 SSE 消息
const handleSSEMessage = (eventType: StreamLog['type'] | null, payload: StreamLogData | string) => {
  const timestamp = Date.now()

  const logType: StreamLog['type'] =
    eventType === 'ocr_progress' || eventType === 'ocr_complete' || eventType === 'error' || eventType === 'complete'
      ? eventType
      : 'text'

  addStreamLog({
    type: logType,
    timestamp,
    data: payload,
  })
  eventCounters.value[logType] = (eventCounters.value[logType] || 0) + 1

  if (logType === 'text') {
    const frag = extractTextFragment(payload)
    if (frag) {
      combinedText.value += frag
      if (items.value.length === 0) {
        items.value.push({ text: combinedText.value })
      } else {
        items.value[0].text = combinedText.value
      }
    }
    return
  }

  if (logType === 'ocr_progress' && typeof payload === 'object' && payload) {
    const msg = payload as StreamLogData
    const item: OcrItem = {
      text: msg.text || '',
      confidence: typeof msg.confidence === 'number' ? msg.confidence : undefined,
      sender: typeof msg.sender === 'string' ? msg.sender : undefined,
      lineIndex: typeof msg.lineIndex === 'number' ? msg.lineIndex : undefined,
      page: typeof msg.page === 'number' ? msg.page : undefined,
      isComplete: msg.isComplete !== undefined ? msg.isComplete : undefined,
    }
    items.value.push(item)
    if (typeof msg.lineIndex === 'number' && msg.lineIndex > 0) {
      progressIndex.value = msg.lineIndex
      progressTotal.value = Math.max(progressTotal.value, msg.lineIndex)
    } else {
      progressIndex.value = items.value.length
      progressTotal.value = Math.max(progressTotal.value, items.value.length)
    }
    if (msg.isComplete === true) {
      processing.value = false
      message.success('识别完成')
    }
  } else if (logType === 'ocr_complete' && typeof payload === 'object' && payload) {
    const msg = payload as StreamLogData
    processing.value = false
    if (msg.text) {
      const item: OcrItem = {
        text: msg.text || '',
        confidence: typeof msg.confidence === 'number' ? msg.confidence : undefined,
        sender: typeof msg.sender === 'string' ? msg.sender : undefined,
        lineIndex: typeof msg.lineIndex === 'number' ? msg.lineIndex : undefined,
        page: typeof msg.page === 'number' ? msg.page : undefined,
        isComplete: msg.isComplete !== undefined ? msg.isComplete : undefined,
      }
      items.value.push(item)
    }
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
    parseTaggedContent(combinedText.value)
    message.success('识别完成')
    logSseSummary()
  }
}

const logSseSummary = () => {
  if (summaryLogged.value) return
  const counters = eventCounters.value
  console.log('[SSE] summary:', {
    counters,
    combinedText: combinedText.value,
    itemsCount: items.value.length,
    error: errorMsg.value || null,
  })
  summaryLogged.value = true
}

// 将 base64 转换为 File 对象
const base64ToFile = (base64: string, filename: string): Promise<File> => {
  return new Promise((resolve) => {
    // 将 base64 转换为 Blob
    const byteCharacters = atob(base64)
    const byteNumbers = new Array(byteCharacters.length)
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i)
    }
    const byteArray = new Uint8Array(byteNumbers)
    const blob = new Blob([byteArray], { type: 'image/png' })
    const file = new File([blob], filename, { type: 'image/png' })
    resolve(file)
  })
}

const resetAll = () => {
  // 停止当前请求
  if (abortController) {
    abortController.abort()
    abortController = null
  }
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
  onRemove()
  errorMsg.value = ''
  processing.value = false
}



const progressPercent = computed(() => {
  if (!progressTotal.value) return 0
  return Math.min(100, Math.round((progressIndex.value / progressTotal.value) * 100))
})



onBeforeUnmount(() => {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
})
</script>

<style scoped>
.ocr-recognition-management {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 16px 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #1890ff, #52c41a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-title {
  font-weight: 600;
  margin-bottom: 12px;
  padding-left: 4px;
}

.image-stage {
  position: relative;
  width: 100%;
  min-height: 200px;
  max-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  overflow: hidden;
  background: #f8f9fa;
  border: 1px solid #e8e8e8;
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
  max-width: 100%;
  max-height: 500px;
  height: auto;
  display: block;
  object-fit: contain;
}



.scan-overlay {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0) 0%, rgba(64, 169, 255, 0.12) 50%, rgba(255, 255, 255, 0) 100%);
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

:deep(.ant-upload-select) {
  width: 120px;
  height: 120px;
}

:deep(.ant-upload-list-picture-card .ant-upload-list-item) {
  width: 120px;
  height: 120px;
}

.result-list {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px 0;
}

.result-item {
  padding: 8px 12px;
  margin-bottom: 8px;
  border-radius: 6px;
  background: #fafafa;
  border: 1px solid #e8e8e8;
  transition: all 0.2s;
}

.result-item:hover {
  background: #f0f0f0;
  border-color: #d9d9d9;
}

.empty-result {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.stream-logs-container {
  max-height: 500px;
  overflow-y: auto;
  padding: 8px 0;
}

.empty-logs {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.stream-logs-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stream-log-item {
  padding: 12px;
  border-radius: 8px;
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

.fade-in {
  animation: fadeInUp 0.28s ease-out both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translate3d(0, 6px, 0);
  }

  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}

.strategy-card .message-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.strategy-card .message-item .message-text {
  margin-top: 6px;
  padding: 10px 12px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
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
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
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
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  color: #333;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .ocr-recognition-management {
    padding: 16px;
  }

  .page-header h2 {
    font-size: 22px;
  }

  .image-stage {
    max-height: 300px;
  }

  .preview-img {
    max-height: 300px;
  }
}
</style>
