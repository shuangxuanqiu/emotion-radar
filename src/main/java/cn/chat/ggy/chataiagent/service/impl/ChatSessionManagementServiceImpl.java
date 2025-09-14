package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.service.ChatSessionManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天会话管理服务实现类
 * 负责管理SSE连接、AI调用订阅等聊天相关资源
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Slf4j
@Service
public class ChatSessionManagementServiceImpl implements ChatSessionManagementService {

    // SSE连接管理
    private final Map<String, SseEmitter> chatEmitters = new ConcurrentHashMap<>();
    
    // AI调用订阅管理
    private final Map<String, Disposable> activeSubscriptions = new ConcurrentHashMap<>();

    @Override
    public void registerSseEmitter(String chatId, SseEmitter emitter) {
        if (chatId == null || emitter == null) {
            throw new IllegalArgumentException("chatId和emitter不能为空");
        }
        
        // 设置SSE连接的各种回调
        emitter.onTimeout(() -> {
            log.warn("SSE连接超时 - chatId: {}", chatId);
            cleanupChatResources(chatId);
        });
        
        emitter.onCompletion(() -> {
            log.info("SSE连接完成 - chatId: {}", chatId);
            cleanupChatResources(chatId);
        });
        
        emitter.onError((throwable) -> {
            log.error("SSE连接错误 - chatId: {}, 错误: {}", chatId, throwable.getMessage());
            cleanupChatResources(chatId);
        });
        
        chatEmitters.put(chatId, emitter);
        log.debug("注册SSE连接 - chatId: {}, 当前活跃连接数: {}", chatId, chatEmitters.size());
    }

    @Override
    public void registerAiSubscription(String chatId, Disposable subscription) {
        if (chatId == null || subscription == null) {
            throw new IllegalArgumentException("chatId和subscription不能为空");
        }
        
        activeSubscriptions.put(chatId, subscription);
        log.debug("注册AI订阅 - chatId: {}, 当前活跃AI调用数: {}", chatId, activeSubscriptions.size());
    }

    @Override
    public SseEmitter getSseEmitter(String chatId) {
        return chatEmitters.get(chatId);
    }

    @Override
    public Disposable getAiSubscription(String chatId) {
        return activeSubscriptions.get(chatId);
    }

    @Override
    public Map<String, Object> stopChatSession(String chatId) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new ConcurrentHashMap<>();
        boolean aiCallStopped = false;
        boolean sseConnectionClosed = false;
        
        try {
            // 1. 首先取消AI调用（这是关键！）
            Disposable subscription = activeSubscriptions.remove(chatId);
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose(); // 真正停止AI厂商的调用
                aiCallStopped = true;
                log.info("AI调用已停止 - chatId: {}", chatId);
            }
            
            // 2. 关闭SSE连接
            SseEmitter emitter = chatEmitters.remove(chatId);
            if (emitter != null) {
                try {
                    emitter.complete();
                    sseConnectionClosed = true;
                    log.info("SSE连接已关闭 - chatId: {}", chatId);
                } catch (Exception e) {
                    log.warn("关闭SSE连接时出现异常 - chatId: {}, 错误: {}", chatId, e.getMessage());
                    // 即使关闭时有异常，也认为是成功的
                    sseConnectionClosed = true;
                }
            }
            
            long endTime = System.currentTimeMillis();
            
            // 3. 构建返回结果
            result.put("success", aiCallStopped || sseConnectionClosed);
            result.put("chatId", chatId);
            result.put("aiCallStopped", aiCallStopped);
            result.put("sseConnectionClosed", sseConnectionClosed);
            result.put("duration", endTime - startTime);
            result.put("timestamp", System.currentTimeMillis());
            
            if (aiCallStopped || sseConnectionClosed) {
                result.put("message", "聊天已成功停止");
                log.info("聊天停止成功 - chatId: {}, AI调用停止: {}, SSE连接关闭: {}, 耗时: {}ms", 
                        chatId, aiCallStopped, sseConnectionClosed, endTime - startTime);
            } else {
                result.put("message", "未找到活跃的聊天会话");
                log.warn("未找到活跃的聊天会话 - chatId: {}", chatId);
            }
            
            return result;
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("停止聊天时发生异常 - chatId: {}, 错误: {}", chatId, e.getMessage(), e);
            
            result.put("success", false);
            result.put("chatId", chatId);
            result.put("message", "停止聊天时发生错误: " + e.getMessage());
            result.put("duration", endTime - startTime);
            result.put("timestamp", System.currentTimeMillis());
            
            return result;
        }
    }

    @Override
    public void cleanupChatResources(String chatId) {
        if (chatId == null) {
            return;
        }
        
        try {
            // 清理SSE连接
            SseEmitter emitter = chatEmitters.remove(chatId);
            if (emitter != null) {
                log.debug("清理SSE连接 - chatId: {}", chatId);
            }
            
            // 清理AI调用订阅
            Disposable subscription = activeSubscriptions.remove(chatId);
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
                log.debug("清理AI调用订阅 - chatId: {}", chatId);
            }
        } catch (Exception e) {
            log.error("清理聊天资源时出现异常 - chatId: {}, 错误: {}", chatId, e.getMessage());
        }
    }

    @Override
    public Map<String, Object> cleanupAllActiveSessions() {
        long startTime = System.currentTimeMillis();
        int cleanedEmitters = 0;
        int cleanedSubscriptions = 0;
        
        try {
            // 清理所有SSE连接
            for (String chatId : chatEmitters.keySet()) {
                SseEmitter emitter = chatEmitters.remove(chatId);
                if (emitter != null) {
                    try {
                        emitter.complete();
                        cleanedEmitters++;
                    } catch (Exception e) {
                        log.warn("强制清理SSE连接时出现异常 - chatId: {}", chatId);
                        cleanedEmitters++; // 即使异常也算清理了
                    }
                }
            }
            
            // 清理所有AI调用
            for (String chatId : activeSubscriptions.keySet()) {
                Disposable subscription = activeSubscriptions.remove(chatId);
                if (subscription != null && !subscription.isDisposed()) {
                    subscription.dispose();
                    cleanedSubscriptions++;
                }
            }
            
            long endTime = System.currentTimeMillis();
            Map<String, Object> result = new ConcurrentHashMap<>();
            result.put("success", true);
            result.put("cleanedEmitters", cleanedEmitters);
            result.put("cleanedSubscriptions", cleanedSubscriptions);
            result.put("duration", endTime - startTime);
            result.put("timestamp", System.currentTimeMillis());
            result.put("message", "所有活跃连接已清理完成");
            
            log.info("强制清理完成 - SSE连接: {}, AI调用: {}, 耗时: {}ms", 
                    cleanedEmitters, cleanedSubscriptions, endTime - startTime);
            
            return result;
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            Map<String, Object> result = new ConcurrentHashMap<>();
            result.put("success", false);
            result.put("message", "清理过程中发生错误: " + e.getMessage());
            result.put("duration", endTime - startTime);
            result.put("timestamp", System.currentTimeMillis());
            
            log.error("强制清理时发生异常: {}", e.getMessage(), e);
            return result;
        }
    }

    @Override
    public Map<String, Object> getActiveSessionStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("totalActiveChats", chatEmitters.size());
        stats.put("totalActiveAiCalls", activeSubscriptions.size());
        stats.put("activeChatIds", chatEmitters.keySet());
        stats.put("timestamp", System.currentTimeMillis());
        return stats;
    }

    @Override
    public Set<String> getActiveChatIds() {
        return chatEmitters.keySet();
    }

    @Override
    public boolean isSessionActive(String chatId) {
        return chatEmitters.containsKey(chatId) || activeSubscriptions.containsKey(chatId);
    }

    @Override
    public int getActiveConnectionCount() {
        return chatEmitters.size();
    }

    @Override
    public int getActiveAiCallCount() {
        return activeSubscriptions.size();
    }
}
