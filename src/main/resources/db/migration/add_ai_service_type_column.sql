-- 为consume_statistic表添加aiServiceType字段
-- 用于区分不同AI服务类型的token消耗统计

-- 添加aiServiceType字段
ALTER TABLE consume_statistic 
ADD COLUMN aiServiceType VARCHAR(50) COMMENT 'AI服务类型 (IMAGE_ANALYSIS: 图像解析, TEXT_CHAT: 文字聊天)';

-- 为现有记录设置默认值
UPDATE consume_statistic 
SET aiServiceType = 'TEXT_CHAT' 
WHERE aiServiceType IS NULL;

-- 添加索引以提高查询性能
CREATE INDEX idx_consume_statistic_ai_service_type ON consume_statistic(aiServiceType);
CREATE INDEX idx_consume_statistic_chat_id_ai_service_type ON consume_statistic(chatId, aiServiceType);

-- 添加注释
ALTER TABLE consume_statistic MODIFY COLUMN aiServiceType VARCHAR(50) 
COMMENT 'AI服务类型: IMAGE_ANALYSIS-图像分析, TEXT_CHAT-文字聊天, 用于区分不同服务的token消耗';
