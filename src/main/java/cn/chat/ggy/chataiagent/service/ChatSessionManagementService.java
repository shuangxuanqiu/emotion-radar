package cn.chat.ggy.chataiagent.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.util.Map;
import java.util.Set;

/**
 * 聊天会话管理服务接口
 * 负责管理SSE连接、AI调用订阅等聊天相关资源
 * 
 * @author 来自小扬 (＾▽＾)／
 */
public interface ChatSessionManagementService {
    
    /**
     * 注册SSE连接
     * @param chatId 聊天ID
     * @param emitter SSE发射器
     */
    void registerSseEmitter(String chatId, SseEmitter emitter);
    
    /**
     * 注册AI调用订阅
     * @param chatId 聊天ID
     * @param subscription 响应式订阅
     */
    void registerAiSubscription(String chatId, Disposable subscription);
    
    /**
     * 获取SSE连接
     * @param chatId 聊天ID
     * @return SSE发射器，如果不存在返回null
     */
    SseEmitter getSseEmitter(String chatId);
    
    /**
     * 获取AI订阅
     * @param chatId 聊天ID
     * @return 响应式订阅，如果不存在返回null
     */
    Disposable getAiSubscription(String chatId);
    
    /**
     * 停止特定聊天会话
     * @param chatId 聊天ID
     * @return 停止结果详情
     */
    Map<String, Object> stopChatSession(String chatId);
    
    /**
     * 清理特定聊天会话的所有资源
     * @param chatId 聊天ID
     */
    void cleanupChatResources(String chatId);
    
    /**
     * 强制清理所有活跃连接
     * @return 清理结果详情
     */
    Map<String, Object> cleanupAllActiveSessions();
    
    /**
     * 获取活跃聊天统计信息
     * @return 统计信息
     */
    Map<String, Object> getActiveSessionStats();
    
    /**
     * 获取活跃的聊天ID集合
     * @return 聊天ID集合
     */
    Set<String> getActiveChatIds();
    
    /**
     * 检查聊天会话是否活跃
     * @param chatId 聊天ID
     * @return 是否活跃
     */
    boolean isSessionActive(String chatId);
    
    /**
     * 获取活跃连接数量
     * @return 连接数量
     */
    int getActiveConnectionCount();
    
    /**
     * 获取活跃AI调用数量
     * @return AI调用数量
     */
    int getActiveAiCallCount();
}
