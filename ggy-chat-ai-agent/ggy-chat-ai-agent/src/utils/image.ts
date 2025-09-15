/**
 * 图片路径处理工具函数
 */

/**
 * 获取完整的图片显示路径
 * @param imagePath 原始图片路径
 * @returns 完整的图片显示路径
 */
export function getImageDisplayUrl(imagePath: string | undefined | null): string {
  if (!imagePath) return ''

  // 如果已经是完整的 URL，直接返回
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath
  }

  // 添加统一前缀
  const prefix = __IMAGE_DISPLAY_PREFIX__

  // 确保路径格式正确
  const cleanPath = imagePath.startsWith('/') ? imagePath.slice(1) : imagePath

  return `${prefix}/${cleanPath}`
}

/**
 * 从完整的图片显示路径中提取原始路径
 * @param fullUrl 完整的图片显示路径
 * @returns 原始图片路径
 */
export function extractImagePath(fullUrl: string | undefined | null): string {
  if (!fullUrl) return ''

  const prefix = __IMAGE_DISPLAY_PREFIX__

  // 如果不是以我们的前缀开头，直接返回原始值
  if (!fullUrl.startsWith(prefix)) {
    return fullUrl
  }

  // 移除前缀，返回原始路径
  return fullUrl.replace(`${prefix}/`, '')
}
