package cn.chat.ggy.chataiagent.chatmemory;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于Redis的聊天记忆存储实现类
 * 用于存储和管理AI聊天的历史消息
 */
@Slf4j
public class RedisChatMemory implements ChatMemoryRepository  {
    private final RedisTemplate<String,Object> redisTemplate;
    private final long defaultTtl;
    private static final String KEY_PREFIX = "chat_memory:";
    private final ObjectMapper objectMapper;

    /**
     * 构造函数，使用默认过期时间（7天）
     * @param stringRedisTemplate Redis模板
     */
    public RedisChatMemory(RedisTemplate<String,Object> stringRedisTemplate) {
        this(stringRedisTemplate, 2*24 * 60 * 60); // 2天默认过期时间
    }

    /**
     * 构造函数，指定过期时间
     * @param redisTemplate Redis模板
     * @param ttlInSeconds 过期时间（秒）
     */
    public RedisChatMemory(RedisTemplate<String,Object> redisTemplate, long ttlInSeconds) {
        this.objectMapper = new ObjectMapper();
        this.redisTemplate = redisTemplate;
        this.defaultTtl = ttlInSeconds;
    }


    /**
     * 从Redis获取数据工具方法
     * @param conversationId
     * @return
     */
    private List<Message> getFromRedis(String conversationId){
        String key = getRedisKey(conversationId);
        Object obj =  redisTemplate.opsForValue().get(key);
        List<Message> messageList  = new ArrayList<>();
        if(obj != null){
            List<String> list = Convert.convert(new TypeReference<List<String>>() {
            }, obj);

            for (String s : list) {
                Message message = MessageSerializer.deserialize(s);
                messageList.add(message);
            }
        }
        return messageList;
    }
    /**
     * 将数据存入Redis工具方法
     * @param conversationId
     * @param messages
     */
    private void setToRedis(String conversationId,List<Message> messages){
        List<String> stringList = new ArrayList<>();
        for (Message message : messages) {
            String serialize = MessageSerializer.serialize(message);
            stringList.add(serialize);
        }
        redisTemplate.opsForValue().set(conversationId,stringList);
    }
    
    /**
     * 获取拼接好前缀的key
     * @param conversationId 会话ID
     * @return 完整的Redis键名
     */
    private String getRedisKey(String conversationId) {
        return KEY_PREFIX + conversationId;
    }

    /**
     * 刷新key的过期时间
     * @param key Redis键名
     */
    private void refreshTtl(String key) {
        redisTemplate.expire(key, defaultTtl, TimeUnit.SECONDS);
        log.info("刷新会话缓存过期时间: {}, 设置为: {}秒", key, defaultTtl);
    }


    /**
     * 查找所有会话ID
     * @return 会话ID列表
     */
    @Override
    public List<String> findConversationIds() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            log.info("未找到任何会话记录");
            return Collections.emptyList();
        }
        List<String> conversationIds = keys.stream()
                .map(key -> key.substring(KEY_PREFIX.length())//去除统一前缀
                )
                .collect(Collectors.toList());
        log.info("成功获取到{}个会话ID", conversationIds.size());
        return conversationIds;
    }

    /**
     * 根据会话ID获取消息列表
     * @param conversationId 会话ID
     * @return 消息列表
     */
    @Override
    public List<Message> findByConversationId(String conversationId) {
        try {
            List<Message> value = getFromRedis(conversationId);
            refreshTtl(conversationId);

            log.info("成功获取会话ID为{}的消息记录，共{}条消息", conversationId, value.size());
            return value;
        } catch (Exception e) {
            log.error("反序列化会话{}的消息时出错: {}", conversationId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 保存会话消息
     * @param conversationId 会话ID
     * @param messages 消息列表
     */
    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            log.info("尝试保存空消息列表，已忽略操作，会话ID: {}", conversationId);
            return;
        }
        
        String key = getRedisKey(conversationId);
        try {
            setToRedis(key, messages);
            redisTemplate.expire(key, defaultTtl, TimeUnit.SECONDS);
            log.info("成功保存会话ID为{}的消息记录，共{}条消息，设置过期时间{}秒", 
                    conversationId, messages.size(), defaultTtl);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存会话{}的消息时出错: {}", conversationId, e.getMessage());
        }
    }

    /**
     * 删除会话记录
     * @param conversationId 会话ID
     */
    @Override
    public void deleteByConversationId(String conversationId) {
        String key = getRedisKey(conversationId);
        redisTemplate.delete(key);
        log.info("已删除会话ID为{}的所有消息记录", conversationId);
    }
}
