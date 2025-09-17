package cn.chat.ggy.chataiagent.interceptor;

import cn.chat.ggy.chataiagent.monitor.MonitorContextHolder;
import cn.hutool.core.util.RandomUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 上下文拦截器
 * 自动为每个请求设置traceId和其他上下文信息
 * 在JDK21虚拟线程环境下，ThreadLocal能够正确传递到异步线程
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Slf4j
@Component
public class ContextInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_KEY = "traceId";
    private static final String REQUEST_URI_KEY = "requestUri";
    private static final String REQUEST_METHOD_KEY = "requestMethod";
    private static final String USER_IP_KEY = "userIp";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 生成或获取traceId
            String traceId = request.getHeader(TRACE_ID_HEADER);
            if (traceId == null || traceId.trim().isEmpty()) {
                traceId = generateTraceId();
            }

            // 设置上下文信息
            MonitorContextHolder.setContext(TRACE_ID_KEY, traceId);
            MonitorContextHolder.setContext(REQUEST_URI_KEY, request.getRequestURI());
            MonitorContextHolder.setContext(REQUEST_METHOD_KEY, request.getMethod());
            MonitorContextHolder.setContext(USER_IP_KEY, getClientIpAddress(request));

            // 在响应头中返回traceId
            response.setHeader(TRACE_ID_HEADER, traceId);

            log.debug("上下文设置完成 - traceId: {}, uri: {}, method: {}, ip: {}", 
                     traceId, request.getRequestURI(), request.getMethod(), getClientIpAddress(request));

            return true;
        } catch (Exception e) {
            log.error("设置上下文失败", e);
            // 即使失败也继续处理请求
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        try {
            // 请求完成后清理上下文
            MonitorContextHolder.clearContext();
            log.debug("上下文已清理");
        } catch (Exception e) {
            log.error("清理上下文失败", e);
        }
    }

    /**
     * 生成traceId
     */
    private String generateTraceId() {
        return "trace-" + System.currentTimeMillis() + "-" + RandomUtil.randomString(6);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        
        // 处理多个IP的情况（X-Forwarded-For可能包含多个IP）
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        
        return clientIp;
    }
}
