// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 图片展示 根据图片路径参数展示图片资源 GET /imageAnalysis/display/&#42;&#42; */
export async function displayImage(options?: { [key: string]: any }) {
  return request<string>('/imageAnalysis/display/**', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 获取图片解析信息详情 根据主键ID获取单个图片解析记录的详细信息 GET /imageAnalysis/getInfo/${param0} */
export async function getInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getInfoParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.ImageAnalysis>(`/imageAnalysis/getInfo/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 查询所有图片解析信息 获取系统中所有图片解析记录列表 GET /imageAnalysis/list */
export async function list(options?: { [key: string]: any }) {
  return request<API.ImageAnalysis[]>('/imageAnalysis/list', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 分页 参数 查询图片解析信息 根据分页参数和chatId查询图片解析信息列表 POST /imageAnalysis/list/page/vo */
export async function listImageAnalysisByPage(
  body: API.ImageAnalysisQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageImageAnalysis>('/imageAnalysis/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 分页查询图片解析信息 根据分页参数查询图片解析信息列表 GET /imageAnalysis/page */
export async function page(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pageParams,
  options?: { [key: string]: any }
) {
  return request<API.PageImageAnalysis>('/imageAnalysis/page', {
    method: 'GET',
    params: {
      ...params,
      page: undefined,
      ...params['page'],
    },
    ...(options || {}),
  })
}

/** 删除图片解析信息 根据主键ID删除指定的图片解析记录 DELETE /imageAnalysis/remove/${param0} */
export async function remove(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.removeParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<boolean>(`/imageAnalysis/remove/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 保存图片解析信息 创建新的图片解析记录，存储AI对图片的分析结果 POST /imageAnalysis/save */
export async function save(body: API.ImageAnalysis, options?: { [key: string]: any }) {
  return request<boolean>('/imageAnalysis/save', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 更新图片解析信息 根据主键更新图片解析信息内容 PUT /imageAnalysis/update */
export async function update(body: API.ImageAnalysis, options?: { [key: string]: any }) {
  return request<boolean>('/imageAnalysis/update', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
