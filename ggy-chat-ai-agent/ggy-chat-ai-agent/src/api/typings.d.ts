declare namespace API {
  type BaseResponseListConsumeStatistic = {
    code?: number
    data?: ConsumeStatistic[]
    message?: string
  }

  type BaseResponseMapStringObject = {
    code?: number
    data?: Record<string, any>
    message?: string
  }

  type BaseResponsePageChatContent = {
    code?: number
    data?: PageChatContent
    message?: string
  }

  type BaseResponsePageFeedbackMessage = {
    code?: number
    data?: PageFeedbackMessage
    message?: string
  }

  type BaseResponsePageImageAnalysis = {
    code?: number
    data?: PageImageAnalysis
    message?: string
  }

  type ChatContent = {
    id?: number
    userId?: number
    chatId?: string
    resultUrl?: string
    tokenQuantity?: number
    resultContent?: string
    choiceContent?: string
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type ChatContentQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    chatId?: string
  }

  type ConsumeStatistic = {
    id?: number
    chatId?: string
    totalTokens?: number
    promptTokens?: number
    completionTokens?: number
    userId?: number
    editTime?: string
    createTime?: string
    updateTime?: string
    aiServiceType?: string
    isDelete?: number
  }

  type continuationChatStreamParams = {
    /** 情感指数参数 */
    emotionalIndex: number
    /** 情绪标签 */
    emotionalLabels: string
    /** 会话 id */
    chatId: string
  }

  type doChatWithLoveAppSseEmitter1Params = {
    /** 用户消息内容 */
    message: string
    /** 聊天会话ID */
    chatId: string
  }

  type doChatWithLoveAppSseEmitterParams = {
    /** 总提示词 */
    message?: string
    /** 情感指数参数 */
    emotionalIndex: number
    /** 会话ID */
    chatId: string
  }

  type easyReturnParams = {
    /** 情感指数参数 */
    emotionalIndex: number
    /** 聊天背景 */
    conversationScene: string
  }

  type FeedbackMessage = {
    id?: number
    chatId?: string
    messageType?: number
    feedBackMessage?: string
    resultStructure?: string
    userId?: number
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type FeedbackMessageVO = {
    /** 会话 id */
    chatId?: string
    /**  反馈内容 */
    feedBackMessage?: string
    resultStructure?: FeedbackResultStructure
    /** 用户选择类型，0正常复制，1不满意反馈 */
    messageType?: number
  }

  type FeedbackQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    chatId?: string
    messageType?: number
  }

  type FeedbackResultStructure = {
    /** 用户选择的文本内容 */
    selectedText?: string
    /** 选择时间戳 */
    timestamp?: string
    /** 用户选择的消息情感指数 */
    emotionalIndex?: number
    /** 额外的反馈信息 */
    additionalInfo?: string
  }

  type getInfo1Params = {
    /** 用户反馈主键ID */
    id: number
  }

  type getInfo2Params = {
    /** Token消费统计主键ID */
    id: number
  }

  type getInfo3Params = {
    /** 对话内容主键ID */
    id: number
  }

  type getInfoParams = {
    /** 图片解析信息主键ID */
    id: number
  }

  type getRedisChatMemoryListParams = {
    /** 聊天会话ID */
    chatId: string
  }

  type getTokenStatsByChatIdParams = {
    /** 聊天会话ID */
    chatId: string
  }

  type ImageAnalysis = {
    id?: number
    chatId?: string
    imageTxt?: string
    imagePath?: string
    tokenQuantity?: number
    userId?: number
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type ImageAnalysisQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    chatId?: string
  }

  type Message = {
    messageType?: 'USER' | 'ASSISTANT' | 'SYSTEM' | 'TOOL'
    text?: string
    metadata?: Record<string, any>
  }

  type page1Params = {
    /** 分页查询参数 */
    page: PageFeedbackMessage
  }

  type page2Params = {
    /** 分页查询参数 */
    page: PageConsumeStatistic
  }

  type page3Params = {
    /** 分页查询参数 */
    page: PageChatContent
  }

  type PageChatContent = {
    records?: ChatContent[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageConsumeStatistic = {
    records?: ConsumeStatistic[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageFeedbackMessage = {
    records?: FeedbackMessage[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageImageAnalysis = {
    records?: ImageAnalysis[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type pageParams = {
    /** 分页查询参数 */
    page: PageImageAnalysis
  }

  type remove1Params = {
    /** 用户反馈主键ID */
    id: number
  }

  type remove2Params = {
    /** Token消费统计主键ID */
    id: number
  }

  type remove3Params = {
    /** 对话内容主键ID */
    id: number
  }

  type removeParams = {
    /** 图片解析信息主键ID */
    id: number
  }

  type resultNoJsonParams = {
    /** 情感指数参数 */
    emotionalIndex: number
    /** 聊天背景 */
    conversationScene: string
  }

  type SseEmitter = {
    timeout?: number
  }

  type stopChatParams = {
    /** 聊天会话ID */
    chatId: string
  }
}
