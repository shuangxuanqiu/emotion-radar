package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.service.CacheService;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务实现类
 * 基于Redis实现缓存功能，提升接口响应速度
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 缓存键前缀
    private static final String IMAGE_ANALYSIS_PREFIX = "image_analysis:";
    private static final String HTML_CONTENT_PREFIX = "html_content:";
    
    // 默认过期时间
    private static final long DEFAULT_IMAGE_CACHE_EXPIRE = 3600; // 1小时
    private static final long DEFAULT_HTML_CACHE_EXPIRE = 1800;  // 30分钟

    @Override
    public void cacheImageAnalysisResult(String fileHash, Long emotionalIndex, ResultInfo resultInfo, long expireSeconds) {
        try {
            String cacheKey = buildImageAnalysisCacheKey(fileHash, emotionalIndex);
            String jsonValue = JSONUtil.toJsonStr(resultInfo);
            
            redisTemplate.opsForValue().set(cacheKey, jsonValue, expireSeconds, TimeUnit.SECONDS);
            log.debug("图片分析结果已缓存 - key: {}, 过期时间: {}s", cacheKey, expireSeconds);
            
        } catch (Exception e) {
            log.error("缓存图片分析结果失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public ResultInfo getCachedImageAnalysisResult(String fileHash, Long emotionalIndex) {
        try {
            String cacheKey = buildImageAnalysisCacheKey(fileHash, emotionalIndex);
            Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedValue != null) {
                log.debug("命中图片分析缓存 - key: {}", cacheKey);
                return JSONUtil.toBean(cachedValue.toString(), ResultInfo.class);
            }
            
            log.debug("图片分析缓存未命中 - key: {}", cacheKey);
            return null;
            
        } catch (Exception e) {
            log.error("获取图片分析缓存失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void cacheHtmlContent(String chatId, String htmlContent, long expireSeconds) {
        try {
            String cacheKey = buildHtmlContentCacheKey(chatId);
            redisTemplate.opsForValue().set(cacheKey, htmlContent, expireSeconds, TimeUnit.SECONDS);
            log.debug("HTML内容已缓存 - key: {}, 大小: {} bytes, 过期时间: {}s", 
                    cacheKey, htmlContent.length(), expireSeconds);
            
        } catch (Exception e) {
            log.error("缓存HTML内容失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getCachedHtmlContent(String chatId) {
        try {
            String cacheKey = buildHtmlContentCacheKey(chatId);
            Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedValue != null) {
                log.debug("命中HTML内容缓存 - key: {}", cacheKey);
                return cachedValue.toString();
            }
            
            log.debug("HTML内容缓存未命中 - key: {}", cacheKey);
            return null;
            
        } catch (Exception e) {
            log.error("获取HTML内容缓存失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String calculateFileHash(byte[] fileBytes) {
        try {
            // 使用MD5计算文件哈希值
            return DigestUtil.md5Hex(fileBytes);
        } catch (Exception e) {
            log.error("计算文件哈希值失败: {}", e.getMessage(), e);
            // 返回基于文件大小和当前时间的简单哈希
            return String.valueOf(fileBytes.length + System.currentTimeMillis() % 1000);
        }
    }

    @Override
    public void evictCache(String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("删除缓存 - key: {}, 结果: {}", key, deleted);
        } catch (Exception e) {
            log.error("删除缓存失败 - key: {}, 错误: {}", key, e.getMessage(), e);
        }
    }

    @Override
    public void clearAllCache() {
        try {
            // 删除所有图片分析缓存
            var imageKeys = redisTemplate.keys(IMAGE_ANALYSIS_PREFIX + "*");
            if (imageKeys != null && !imageKeys.isEmpty()) {
                redisTemplate.delete(imageKeys);
                log.info("清空图片分析缓存 - 删除数量: {}", imageKeys.size());
            }
            
            // 删除所有HTML内容缓存
            var htmlKeys = redisTemplate.keys(HTML_CONTENT_PREFIX + "*");
            if (htmlKeys != null && !htmlKeys.isEmpty()) {
                redisTemplate.delete(htmlKeys);
                log.info("清空HTML内容缓存 - 删除数量: {}", htmlKeys.size());
            }
            
        } catch (Exception e) {
            log.error("清空缓存失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getCacheStats() {
        try {
            var imageKeys = redisTemplate.keys(IMAGE_ANALYSIS_PREFIX + "*");
            var htmlKeys = redisTemplate.keys(HTML_CONTENT_PREFIX + "*");
            
            int imageCount = imageKeys != null ? imageKeys.size() : 0;
            int htmlCount = htmlKeys != null ? htmlKeys.size() : 0;
            
            return String.format("缓存统计 - 图片分析缓存: %d个, HTML内容缓存: %d个, 总计: %d个", 
                    imageCount, htmlCount, imageCount + htmlCount);
            
        } catch (Exception e) {
            log.error("获取缓存统计失败: {}", e.getMessage(), e);
            return "缓存统计获取失败";
        }
    }

    /**
     * 构建图片分析缓存键
     */
    private String buildImageAnalysisCacheKey(String fileHash, Long emotionalIndex) {
        return IMAGE_ANALYSIS_PREFIX + fileHash + ":" + (emotionalIndex != null ? emotionalIndex : "null");
    }

    /**
     * 构建HTML内容缓存键
     */
    private String buildHtmlContentCacheKey(String chatId) {
        return HTML_CONTENT_PREFIX + chatId;
    }
}
