// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 获取对话内容详情 根据主键ID获取单个对话内容的详细信息 GET /chatContent/getInfo/${param0} */
export async function getInfo3(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getInfo3Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.ChatContent>(`/chatContent/getInfo/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 查询所有对话内容 获取系统中所有对话内容记录列表 GET /chatContent/list */
export async function list3(options?: { [key: string]: any }) {
  return request<API.ChatContent[]>('/chatContent/list', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 分页 参数 查询对话内容 根据分页参数和chatId查询对话内容列表 POST /chatContent/list/page/vo */
export async function listChatContentByPage(
  body: API.ChatContentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageChatContent>('/chatContent/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 分页查询对话内容 根据分页参数查询对话内容列表 GET /chatContent/page */
export async function page3(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.page3Params,
  options?: { [key: string]: any }
) {
  return request<API.PageChatContent>('/chatContent/page', {
    method: 'GET',
    params: {
      ...params,
      page: undefined,
      ...params['page'],
    },
    ...(options || {}),
  })
}

/** 删除对话内容 根据主键ID删除指定的对话内容记录 DELETE /chatContent/remove/${param0} */
export async function remove3(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.remove3Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<boolean>(`/chatContent/remove/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 保存对话内容 创建新的对话内容记录 POST /chatContent/save */
export async function save3(body: API.ChatContent, options?: { [key: string]: any }) {
  return request<boolean>('/chatContent/save', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 更新对话内容 根据主键更新对话内容信息 PUT /chatContent/update */
export async function update3(body: API.ChatContent, options?: { [key: string]: any }) {
  return request<boolean>('/chatContent/update', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
