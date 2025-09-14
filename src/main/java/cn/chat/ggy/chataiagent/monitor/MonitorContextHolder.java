package cn.chat.ggy.chataiagent.monitor;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MonitorContextHolder {

    /**
     * 使用TransmittableThreadLocal替代ThreadLocal
     * 解决异步执行时上下文传递问题
     */
    private static final TransmittableThreadLocal<Map<String, String>> CONTEXT_HOLDER = new TransmittableThreadLocal<>();

    /**
     * 设置监控上下文（支持多个键值对）
     */
    public static void setContext(String key, String value) {
        Map<String, String> context = CONTEXT_HOLDER.get();
        if (context == null) {
            context = new HashMap<>();
            CONTEXT_HOLDER.set(context);
        }
        context.put(key, value);
        log.debug("设置上下文: {}={}", key, value);
    }

    /**
     * 批量设置监控上下文
     */
    public static void setContext(Map<String, String> contextMap) {
        if (contextMap != null && !contextMap.isEmpty()) {
            Map<String, String> context = CONTEXT_HOLDER.get();
            if (context == null) {
                context = new HashMap<>();
                CONTEXT_HOLDER.set(context);
            }
            context.putAll(contextMap);
            log.debug("批量设置上下文: {}", contextMap);
        }
    }

    /**
     * 获取当前监控上下文
     */
    public static String getContext(String key) {
        Map<String, String> context = CONTEXT_HOLDER.get();
        if (context == null) {
            log.debug("上下文为空，无法获取key: {}", key);
            return null;
        }
        String value = context.get(key);
        log.debug("获取上下文: {}={}", key, value);
        return value;
    }

    /**
     * 获取所有上下文
     */
    public static Map<String, String> getAllContext() {
        Map<String, String> context = CONTEXT_HOLDER.get();
        return context != null ? new HashMap<>(context) : new HashMap<>();
    }

    /**
     * 移除特定的上下文键
     */
    public static void removeContext(String key) {
        Map<String, String> context = CONTEXT_HOLDER.get();
        if (context != null) {
            context.remove(key);
            log.debug("移除上下文key: {}", key);
        }
    }

    /**
     * 清除监控上下文
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
        log.debug("清除所有上下文");
    }

    /**
     * 检查上下文是否存在
     */
    public static boolean hasContext() {
        Map<String, String> context = CONTEXT_HOLDER.get();
        return context != null && !context.isEmpty();
    }

    /**
     * 检查特定键是否存在
     */
    public static boolean hasKey(String key) {
        Map<String, String> context = CONTEXT_HOLDER.get();
        return context != null && context.containsKey(key);
    }
}

