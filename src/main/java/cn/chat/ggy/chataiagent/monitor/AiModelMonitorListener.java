package cn.chat.ggy.chataiagent.monitor;

import cn.chat.ggy.chataiagent.service.ConsumeStatisticService;
import cn.chat.ggy.chataiagent.service.impl.ConsumeStatisticServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiModelMonitorListener {

    @Resource
    private ConsumeStatisticService consumeStatisticService;
    @Async("databaseAsyncExecutor")
    public void onResponse(ChatClientResponse chatClientResponse) {
        try {
            // 获取完整的上下文信息
            String chatId = MonitorContextHolder.getContext("chatId");
            String traceId = MonitorContextHolder.getContext("traceId");
            String requestUri = MonitorContextHolder.getContext("requestUri");
            
            log.info("记录token消耗 - traceId: {}, chatId: {}, uri: {}", traceId, chatId, requestUri);
            
            // 拿到 token 信息
            Usage usage = chatClientResponse.chatResponse().getMetadata().getUsage();
            
            if (usage != null && chatId != null) {
                consumeStatisticService.create(chatId, usage);
                log.info("Token消耗记录成功 - traceId: {}, chatId: {}, totalTokens: {}, promptTokens: {}, completionTokens: {}", 
                        traceId, chatId, usage.getTotalTokens(), usage.getPromptTokens(), usage.getCompletionTokens());
            } else {
                log.warn("Token消耗记录跳过 - usage或chatId为空: usage={}, chatId={}, traceId={}", 
                        usage, chatId, traceId);
            }
        } catch (Exception e) {
            String traceId = MonitorContextHolder.getContext("traceId");
            log.error("记录token消耗失败 - traceId: {}", traceId, e);
        }
    }

    public void onRequest(ChatClientRequest request) {
    }
}
