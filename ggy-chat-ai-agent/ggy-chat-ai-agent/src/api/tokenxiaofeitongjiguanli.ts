// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 获取Token消费统计详情 根据主键ID获取单个Token消费统计的详细信息 GET /consumeStatistic/getInfo/${param0} */
export async function getInfo2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getInfo2Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.ConsumeStatistic>(`/consumeStatistic/getInfo/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 查询所有Token消费统计 获取系统中所有Token消费统计记录列表 GET /consumeStatistic/list */
export async function list2(options?: { [key: string]: any }) {
  return request<API.ConsumeStatistic[]>('/consumeStatistic/list', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 分页查询Token消费统计 根据分页参数查询Token消费统计列表 GET /consumeStatistic/page */
export async function page2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.page2Params,
  options?: { [key: string]: any }
) {
  return request<API.PageConsumeStatistic>('/consumeStatistic/page', {
    method: 'GET',
    params: {
      ...params,
      page: undefined,
      ...params['page'],
    },
    ...(options || {}),
  })
}

/** 删除Token消费统计 根据主键ID删除指定的Token消费统计记录 DELETE /consumeStatistic/remove/${param0} */
export async function remove2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.remove2Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<boolean>(`/consumeStatistic/remove/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 保存Token消费统计 创建新的Token使用量统计记录 POST /consumeStatistic/save */
export async function save2(body: API.ConsumeStatistic, options?: { [key: string]: any }) {
  return request<boolean>('/consumeStatistic/save', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 更新Token消费统计 根据主键更新Token消费统计信息 PUT /consumeStatistic/update */
export async function update2(body: API.ConsumeStatistic, options?: { [key: string]: any }) {
  return request<boolean>('/consumeStatistic/update', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
