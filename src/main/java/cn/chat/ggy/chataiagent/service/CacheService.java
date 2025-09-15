package cn.chat.ggy.chataiagent.service;

import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;

/**
 * 缓存服务接口
 * 提供图片分析结果和HTML内容的缓存功能
 * 
 * @author 来自小扬 (＾▽＾)／
 */
public interface CacheService {
    
    /**
     * 缓存图片分析结果
     * @param fileHash 文件哈希值
     * @param emotionalIndex 情绪指数
     * @param resultInfo 分析结果
     * @param expireSeconds 过期时间（秒）
     */
    void cacheImageAnalysisResult(String fileHash, Long emotionalIndex, ResultInfo resultInfo, long expireSeconds);
    
    /**
     * 获取缓存的图片分析结果
     * @param fileHash 文件哈希值
     * @param emotionalIndex 情绪指数
     * @return 缓存的分析结果，如果不存在返回null
     */
    ResultInfo getCachedImageAnalysisResult(String fileHash, Long emotionalIndex);
    
    /**
     * 缓存HTML内容
     * @param chatId 聊天ID
     * @param htmlContent HTML内容
     * @param expireSeconds 过期时间（秒）
     */
    void cacheHtmlContent(String chatId, String htmlContent, long expireSeconds);
    
    /**
     * 获取缓存的HTML内容
     * @param chatId 聊天ID
     * @return 缓存的HTML内容，如果不存在返回null
     */
    String getCachedHtmlContent(String chatId);
    
    /**
     * 计算文件哈希值
     * @param fileBytes 文件字节数组
     * @return 文件的MD5哈希值
     */
    String calculateFileHash(byte[] fileBytes);
    
    /**
     * 删除指定的缓存
     * @param key 缓存键
     */
    void evictCache(String key);
    
    /**
     * 清空所有缓存
     */
    void clearAllCache();
    
    /**
     * 获取缓存统计信息
     * @return 缓存统计信息
     */
    String getCacheStats();
}
