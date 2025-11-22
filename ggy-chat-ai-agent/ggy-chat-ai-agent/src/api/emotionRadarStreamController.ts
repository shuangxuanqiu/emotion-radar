// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 情感雷达流式续写接口 根据情感参数续写，返回 NDJSON（ResultStructure） POST /stream-ai/travel_guide/chat/sse/continuation */
export async function continuationChatStream(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.continuationChatStreamParams,
  options?: { [key: string]: any }
) {
  return request<API.SseEmitter>('/stream-ai/travel_guide/chat/sse/continuation', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 图片识别流式接口 上传图片进行AI识别，返回 JSON 文本流（{G:"文本"}） POST /stream-ai/travel_guide/chat/sse/emitter */
export async function doChatWithLoveAppSseEmitter(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.doChatWithLoveAppSseEmitterParams,
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

  return request<API.SseEmitter>('/stream-ai/travel_guide/chat/sse/emitter', {
    method: 'POST',
    params: {
      // message has a default value: 请识别这张聊天界面截图
      message: '请识别这张聊天界面截图',

      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}
