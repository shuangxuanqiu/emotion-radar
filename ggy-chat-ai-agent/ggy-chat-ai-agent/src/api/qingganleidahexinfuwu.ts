// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 清空缓存 清空系统中所有缓存数据 POST /chat-ai/cache/clear */
export async function clearCache(options?: { [key: string]: any }) {
  return request<string>('/chat-ai/cache/clear', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 获取缓存统计 获取系统缓存的统计信息，包括命中率、大小等 GET /chat-ai/cache/stats */
export async function getCacheStats(options?: { [key: string]: any }) {
  return request<string>('/chat-ai/cache/stats', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 获取活跃聊天统计 获取当前活跃聊天会话的统计信息 GET /chat-ai/chat/active/stats */
export async function getActiveChatStats(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/chat-ai/chat/active/stats', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 清理所有活跃连接 管理员功能：强制清理所有活跃的聊天连接和相关资源 POST /chat-ai/chat/cleanup/all */
export async function cleanupAllActiveChats(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/chat-ai/chat/cleanup/all', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 获取聊天记录 从Redis中获取指定会话的聊天记录 GET /chat-ai/chat/memory/redis */
export async function getRedisChatMemoryList(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getRedisChatMemoryListParams,
  options?: { [key: string]: any }
) {
  return request<API.Message[]>('/chat-ai/chat/memory/redis', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 用户反馈提交 接收并存储用户对AI分析结果的反馈信息 POST /chat-ai/chat/user/feedback */
export async function userFeedback(body: API.FeedbackMessageVO, options?: { [key: string]: any }) {
  return request<any>('/chat-ai/chat/user/feedback', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 情感雷达核心服务 上传图片进行AI情感分析，生成HTML格式的可视化分析报告 POST /chat-ai/emotion-radar */
export async function resultNoJson(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.resultNoJsonParams,
  body: {},
  file?: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()

  if (file) {
    formData.append('file', file)
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele]

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''))
        } else {
          formData.append(ele, new Blob([JSON.stringify(item)], { type: 'application/json' }))
        }
      } else {
        formData.append(ele, item)
      }
    }
  })

  return request<string>('/chat-ai/emotion-radar', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}

/** 情感雷达核心服务 上传图片进行AI情感分析，生成HTML格式的可视化分析报告 POST /chat-ai/emotion-radar-easy */
export async function easyReturn(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.easyReturnParams,
  body: {},
  file?: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()

  if (file) {
    formData.append('file', file)
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele]

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''))
        } else {
          formData.append(ele, new Blob([JSON.stringify(item)], { type: 'application/json' }))
        }
      } else {
        formData.append(ele, item)
      }
    }
  })

  return request<string>('/chat-ai/emotion-radar-easy', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}

/** 系统健康检查 获取系统运行状态信息，包括缓存状态、活跃连接数等 GET /chat-ai/health */
export async function healthCheck(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/chat-ai/health', {
    method: 'GET',
    ...(options || {}),
  })
}

/** SSE流式对话 建立SSE连接，实现AI流式对话功能 GET /chat-ai/travel_guide/chat/sse/emitter */
export async function doChatWithLoveAppSseEmitter1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.doChatWithLoveAppSseEmitter1Params,
  options?: { [key: string]: any }
) {
  return request<API.SseEmitter>('/chat-ai/travel_guide/chat/sse/emitter', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 停止AI对话 强制停止指定会话的AI回答，释放相关资源 POST /chat-ai/travel_guide/chat/stop */
export async function stopChat(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.stopChatParams,
  options?: { [key: string]: any }
) {
  return request<Record<string, any>>('/chat-ai/travel_guide/chat/stop', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
