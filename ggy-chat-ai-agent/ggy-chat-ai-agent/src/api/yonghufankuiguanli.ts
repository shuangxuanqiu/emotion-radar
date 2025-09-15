// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 获取用户反馈详情 根据主键ID获取单个用户反馈的详细信息 GET /feedbackMessage/getInfo/${param0} */
export async function getInfo1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getInfo1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.FeedbackMessage>(`/feedbackMessage/getInfo/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 查询所有用户反馈 获取系统中所有用户反馈记录列表 GET /feedbackMessage/list */
export async function list1(options?: { [key: string]: any }) {
  return request<API.FeedbackMessage[]>('/feedbackMessage/list', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 分页 参数 查询用户反馈 根据分页参数查询用户反馈列表 POST /feedbackMessage/list/page/vo */
export async function listUserVoByPage(
  body: API.FeedbackQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageFeedbackMessage>('/feedbackMessage/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 分页查询用户反馈 根据分页参数查询用户反馈列表 GET /feedbackMessage/page */
export async function page1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.page1Params,
  options?: { [key: string]: any }
) {
  return request<API.PageFeedbackMessage>('/feedbackMessage/page', {
    method: 'GET',
    params: {
      ...params,
      page: undefined,
      ...params['page'],
    },
    ...(options || {}),
  })
}

/** 删除用户反馈 根据主键ID删除指定的用户反馈记录 DELETE /feedbackMessage/remove/${param0} */
export async function remove1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.remove1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<boolean>(`/feedbackMessage/remove/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 保存用户反馈 创建新的用户反馈记录，记录用户对生成内容的评价和建议 POST /feedbackMessage/save */
export async function save1(body: API.FeedbackMessage, options?: { [key: string]: any }) {
  return request<boolean>('/feedbackMessage/save', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 更新用户反馈 根据主键更新用户反馈信息 PUT /feedbackMessage/update */
export async function update1(body: API.FeedbackMessage, options?: { [key: string]: any }) {
  return request<boolean>('/feedbackMessage/update', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
