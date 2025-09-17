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
 * todo ⚠废弃
 * 文字聊天AI可观测性 Advisor
 * 专门用于监控文字聊天AI服务的token消耗情况
 */
@Slf4j
@Component
public class TextChatObservabilityAdvisor implements CallAdvisor, StreamAdvisor {

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
     * 设置文字聊天专用的监控上下文
     */
    private ChatClientRequest before(ChatClientRequest request) {
        try {
            // 设置AI服务类型为文字聊天
            MonitorContextHolder.setContext("aiServiceType", "TEXT_CHAT");

            // 生成traceId用于追踪
            String traceId = generateTraceId("txt");
            MonitorContextHolder.setContext("traceId", traceId);

            // 设置请求URI（如果没有设置的话）
            if (!MonitorContextHolder.hasKey("requestUri")) {
                MonitorContextHolder.setContext("requestUri", "/api/chat");
            }

            log.debug("文字聊天AI请求监控开始 - traceId: {}, chatId: {}", 
                    traceId, MonitorContextHolder.getContext("chatId"));

        } catch (Exception e) {
            log.error("文字聊天AI请求监控前置处理失败", e);
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

            log.debug("文字聊天AI响应监控开始 - traceId: {}, chatId: {}", traceId, chatId);

            // 获取token使用情况
            Usage usage = response.chatResponse().getMetadata().getUsage();
            if (usage != null) {
                log.info("文字聊天AI服务token消耗 - traceId: {}, chatId: {}, " +
                        "总Token: {}, 提示Token: {}, 完成Token: {}", 
                        traceId, chatId,
                        usage.getTotalTokens(), usage.getPromptTokens(), usage.getCompletionTokens());
            }

            // 调用监控监听器记录token消耗
            aiModelMonitorListener.onResponse(response);

        } catch (Exception e) {
            String traceId = MonitorContextHolder.getContext("traceId");
            log.error("文字聊天AI响应监控处理失败 - traceId: {}", traceId, e);
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
            log.debug("文字聊天流式响应完成 - traceId: {}", MonitorContextHolder.getContext("traceId"));
        }).doOnError(error -> {
            String traceId = MonitorContextHolder.getContext("traceId");
            log.error("文字聊天流式响应错误 - traceId: {}", traceId, error);
            MonitorContextHolder.clearContext();
        }).doFinally(signalType -> {
            // 确保在流完成后清理上下文
            MonitorContextHolder.clearContext();
        });
    }

    /**
     * 生成文字聊天专用的追踪ID
     * @param prefix 前缀
     * @return 追踪ID
     */
    private String generateTraceId(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }
}
