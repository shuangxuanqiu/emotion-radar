package cn.chat.ggy.chataiagent.advisor;

import cn.chat.ggy.chataiagent.monitor.AiModelMonitorListener;
import cn.chat.ggy.chataiagent.monitor.MonitorContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 图像分析AI可观测性 Advisor
 * 专门用于监控图像分析AI服务的token消耗情况
 * 
 * 功能特点：
 * 1. 自动设置AI服务类型为IMAGE_ANALYSIS
 * 2. 生成专用的追踪ID用于日志跟踪
 * 3. 记录详细的token消耗统计信息
 * 4. 支持流式和非流式调用监控
 */
@Slf4j
@Component
public class ImageAnalysisObservabilityAdvisor implements CallAdvisor, StreamAdvisor {

    @Autowired
    private AiModelMonitorListener aiModelMonitorListener;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        // 设置较高的优先级，确保在其他advisor之后执行
        return 1000;
    }

    /**
     * 请求前的处理
     * 设置图像分析专用的监控上下文
     */
    private ChatClientRequest before(ChatClientRequest request) {
        try {
            // 设置AI服务类型为图像分析
            MonitorContextHolder.setContext("aiServiceType", "IMAGE_ANALYSIS");

            // 生成traceId用于追踪
            String traceId = generateTraceId("img");
            MonitorContextHolder.setContext("traceId", traceId);

            // 设置请求URI（如果没有设置的话）
            if (!MonitorContextHolder.hasKey("requestUri")) {
                MonitorContextHolder.setContext("requestUri", "/api/image/analysis");
            }
            
            // 获取当前chatId用于日志记录
            String chatId = MonitorContextHolder.getContext("chatId");

            log.info("🖼️ 图像分析AI请求监控开始 - traceId: {}, chatId: {}, AI服务类型: IMAGE_ANALYSIS", 
                    traceId, chatId);

        } catch (Exception e) {
            log.error("❌ 图像分析AI请求监控前置处理失败", e);
        }
        return request;
    }

    /**
     * 响应后的处理
     * 调用监控监听器记录token消耗
     */
    private void after(ChatClientResponse response) {
        try {
            String traceId = MonitorContextHolder.getContext("traceId");
            String chatId = MonitorContextHolder.getContext("chatId");

            log.info("🖼️ 图像分析AI响应监控开始 - traceId: {}, chatId: {}", traceId, chatId);

            // 获取token使用情况
            Usage usage = response.chatResponse().getMetadata().getUsage();
            if (usage != null) {
                log.info("=== 图像分析AI服务Token消耗统计 ===");
                log.info("TraceId: {}", traceId);
                log.info("ChatId: {}", chatId);
                log.info("AI服务类型: IMAGE_ANALYSIS");
                log.info("总Token消耗: {}", usage.getTotalTokens());
                log.info("提示Token: {}", usage.getPromptTokens());
                log.info("完成Token: {}", usage.getCompletionTokens());
                log.info("=====================================");
            } else {
                log.warn("⚠️ 图像分析AI响应中没有Token使用信息 - traceId: {}, chatId: {}", traceId, chatId);
            }

            // 调用监控监听器记录token消耗
            aiModelMonitorListener.onResponse(response,chatId,traceId,"IMAGE_ANALYSIS");
            log.info("✅ 图像分析Token消耗监控完成 - traceId: {}, chatId: {}", traceId, chatId);

        } catch (Exception e) {
            String traceId = MonitorContextHolder.getContext("traceId");
            log.error("❌ 图像分析AI响应监控处理失败 - traceId: {}", traceId, e);
        } finally {
            // 清理上下文，避免内存泄漏
            MonitorContextHolder.clearContext();
        }
    }

    /**
     * 非流式调用的advisor实现
     */
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        request = this.before(request);
        ChatClientResponse response = chain.nextCall(request);
        this.after(response);
        return response;
    }

    /**
     * 流式调用的advisor实现
     */
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        request = this.before(request);
        Flux<ChatClientResponse> responseFlux = chain.nextStream(request);
        
        return responseFlux.doOnComplete(() -> {
            log.debug("图像分析流式响应完成 - traceId: {}", MonitorContextHolder.getContext("traceId"));
        }).doOnError(error -> {
            String traceId = MonitorContextHolder.getContext("traceId");
            log.error("图像分析流式响应错误 - traceId: {}", traceId, error);
            MonitorContextHolder.clearContext();
        }).doFinally(signalType -> {
            // 确保在流完成后清理上下文
            MonitorContextHolder.clearContext();
        });
    }

    /**
     * 生成图像分析专用的追踪ID
     * @param prefix 前缀
     * @return 追踪ID
     */
    private String generateTraceId(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }
}
