/**
 * 简单的时间格式化工具
 */

/**
 * 格式化时间为 "年-月-日 时:分:秒" 格式
 * @param timeStr 时间字符串
 * @returns 格式化后的时间字符串
 */
export function formatTime(timeStr: string | undefined | null): string {
  if (!timeStr) {
    return '-'
  }

  try {
    const date = new Date(timeStr)

    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      return timeStr
    }

    // 格式化为 "年-月-日 时:分:秒"
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch (error) {
    console.warn('时间格式化失败:', error)
    return timeStr
  }
}


