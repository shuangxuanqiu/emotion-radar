/**
 * JSON 处理工具函数
 */

/**
 * 安全解析JSON字符串
 * @param jsonString JSON字符串
 * @returns 解析后的对象或原始字符串
 */
export function safeParseJSON(jsonString: string | undefined | null): any {
  if (!jsonString || typeof jsonString !== 'string') {
    return jsonString
  }

  try {
    // 尝试解析JSON
    return JSON.parse(jsonString)
  } catch (error) {
    // 如果解析失败，返回原始字符串
    return jsonString
  }
}

/**
 * 检查字符串是否为有效的JSON
 * @param str 待检查的字符串
 * @returns 是否为有效JSON
 */
export function isValidJSON(str: string | undefined | null): boolean {
  if (!str || typeof str !== 'string') {
    return false
  }

  try {
    JSON.parse(str)
    return true
  } catch (error) {
    return false
  }
}

/**
 * 格式化JSON字符串，用于显示
 * @param jsonString JSON字符串
 * @param indent 缩进空格数，默认为2
 * @returns 格式化后的JSON字符串或原始字符串
 */
export function formatJSON(jsonString: string | undefined | null, indent: number = 2): string {
  if (!jsonString || typeof jsonString !== 'string') {
    return jsonString || ''
  }

  try {
    const parsed = JSON.parse(jsonString)
    return JSON.stringify(parsed, null, indent)
  } catch (error) {
    return jsonString
  }
}

/**
 * 从JSON对象中提取纯文本内容（递归提取所有字符串值）
 * @param obj JSON对象或字符串
 * @returns 提取的文本内容
 */
export function extractTextFromJSON(obj: any): string {
  if (typeof obj === 'string') {
    // 如果是字符串，先尝试解析
    const parsed = safeParseJSON(obj)
    if (parsed !== obj) {
      return extractTextFromJSON(parsed)
    }
    return obj
  }

  if (typeof obj === 'number' || typeof obj === 'boolean') {
    return String(obj)
  }

  if (Array.isArray(obj)) {
    return obj.map((item) => extractTextFromJSON(item)).join(' ')
  }

  if (obj && typeof obj === 'object') {
    return Object.values(obj)
      .map((value) => extractTextFromJSON(value))
      .join(' ')
  }

  return ''
}

/**
 * 获取JSON对象的简要描述（用于表格显示）
 * @param jsonString JSON字符串
 * @param maxLength 最大长度，默认50
 * @returns 简要描述
 */
export function getJSONSummary(
  jsonString: string | undefined | null,
  maxLength: number = 50,
): string {
  if (!jsonString) return '-'

  // 直接显示原始内容，超过长度则截取并添加省略号
  const content = jsonString.trim()
  return content.length > maxLength ? content.substring(0, maxLength) + '...' : content
}
