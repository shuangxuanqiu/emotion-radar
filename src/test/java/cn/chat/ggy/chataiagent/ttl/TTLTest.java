package cn.chat.ggy.chataiagent.ttl;

import cn.chat.ggy.chataiagent.monitor.MonitorContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * TTL功能测试
 * 验证TransmittableThreadLocal是否能在异步执行中正确传递上下文
 */
@Slf4j
@SpringBootTest
public class TTLTest {

    @Test
    public void testTTLInAsyncExecution() throws Exception {
        // 在主线程中设置上下文
        MonitorContextHolder.setContext("chatId", "test-chat-123");
        MonitorContextHolder.setContext("traceId", "test-trace-456");
        MonitorContextHolder.setContext("userId", "test-user-789");
        
        log.info("主线程设置上下文完成");
        log.info("主线程 - chatId: {}", MonitorContextHolder.getContext("chatId"));
        log.info("主线程 - traceId: {}", MonitorContextHolder.getContext("traceId"));
        log.info("主线程 - userId: {}", MonitorContextHolder.getContext("userId"));
        
        // 测试异步执行
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            log.info("异步线程开始执行");
            log.info("异步线程 - chatId: {}", MonitorContextHolder.getContext("chatId"));
            log.info("异步线程 - traceId: {}", MonitorContextHolder.getContext("traceId"));
            log.info("异步线程 - userId: {}", MonitorContextHolder.getContext("userId"));
            
            // 验证上下文是否正确传递
            String chatId = MonitorContextHolder.getContext("chatId");
            String traceId = MonitorContextHolder.getContext("traceId");
            String userId = MonitorContextHolder.getContext("userId");
            
            if ("test-chat-123".equals(chatId) && "test-trace-456".equals(traceId) && "test-user-789".equals(userId)) {
                log.info("✅ TTL测试成功：上下文在异步线程中正确传递");
            } else {
                log.error("❌ TTL测试失败：上下文传递不正确");
                log.error("期望 chatId: test-chat-123, 实际: {}", chatId);
                log.error("期望 traceId: test-trace-456, 实际: {}", traceId);
                log.error("期望 userId: test-user-789, 实际: {}", userId);
            }
        });
        
        // 等待异步任务完成
        future.get(5, TimeUnit.SECONDS);
        
        // 清理上下文
        MonitorContextHolder.clearContext();
        log.info("主线程清理上下文完成");
    }

    @Test
    public void testTTLInNestedAsyncExecution() throws Exception {
        // 设置初始上下文
        MonitorContextHolder.setContext("level", "1");
        MonitorContextHolder.setContext("testId", "nested-test");
        
        log.info("Level 1 - 主线程: {}", MonitorContextHolder.getContext("level"));
        
        CompletableFuture<Void> level2Future = CompletableFuture.runAsync(() -> {
            log.info("Level 2 - 异步线程: {}", MonitorContextHolder.getContext("level"));
            log.info("Level 2 - testId: {}", MonitorContextHolder.getContext("testId"));
            
            // 在异步线程中修改上下文
            MonitorContextHolder.setContext("level", "2");
            
            CompletableFuture<Void> level3Future = CompletableFuture.runAsync(() -> {
                log.info("Level 3 - 嵌套异步线程: {}", MonitorContextHolder.getContext("level"));
                log.info("Level 3 - testId: {}", MonitorContextHolder.getContext("testId"));
                
                // 验证嵌套异步执行中的上下文传递
                String level = MonitorContextHolder.getContext("level");
                String testId = MonitorContextHolder.getContext("testId");
                
                if ("2".equals(level) && "nested-test".equals(testId)) {
                    log.info("✅ 嵌套TTL测试成功：上下文在多层异步线程中正确传递");
                } else {
                    log.error("❌ 嵌套TTL测试失败：上下文传递不正确");
                    log.error("期望 level: 2, 实际: {}", level);
                    log.error("期望 testId: nested-test, 实际: {}", testId);
                }
            });
            
            try {
                level3Future.get(3, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("Level 3 异步执行失败", e);
            }
        });
        
        level2Future.get(5, TimeUnit.SECONDS);
        MonitorContextHolder.clearContext();
    }
}

/**
 * 测试服务类
 */
@Slf4j
@Service
class TTLTestService {
    
    @Async("databaseAsyncExecutor")
    public CompletableFuture<String> asyncMethodWithTTL(String input) {
        log.info("异步方法开始执行 - 输入: {}", input);
        
        // 获取上下文
        String chatId = MonitorContextHolder.getContext("chatId");
        String traceId = MonitorContextHolder.getContext("traceId");
        
        log.info("异步方法中的上下文 - chatId: {}, traceId: {}", chatId, traceId);
        
        // 模拟一些处理时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String result = String.format("处理完成[%s] - chatId:%s, traceId:%s", input, chatId, traceId);
        log.info("异步方法执行完成 - 结果: {}", result);
        
        return CompletableFuture.completedFuture(result);
    }
}
