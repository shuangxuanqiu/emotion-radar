package cn.chat.ggy.chataiagent.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.chat.ggy.chataiagent.model.entity.ConsumeStatistic;
import cn.chat.ggy.chataiagent.mapper.ConsumeStatisticMapper;
import cn.chat.ggy.chataiagent.service.ConsumeStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * token统计表 服务层实现。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@Service
@Slf4j
public class ConsumeStatisticServiceImpl extends ServiceImpl<ConsumeStatisticMapper, ConsumeStatistic>  implements ConsumeStatisticService{

    @Override
    public void create(String chatId, Usage usage) {
        //封装实体，并保存
        ConsumeStatistic build = ConsumeStatistic.builder().chatId(chatId)
                .completionTokens(Long.valueOf(usage.getCompletionTokens()))
                .promptTokens(Long.valueOf(usage.getPromptTokens())) // 修复：应该是getPromptTokens()
                .totalTokens(Long.valueOf(usage.getTotalTokens()))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        this.save(build);
    }
    
    @Override
    public void create(String chatId, Usage usage, String aiServiceType) {
        try {
            //封装实体，并保存
            ConsumeStatistic build = ConsumeStatistic.builder()
                    .chatId(chatId)
                    .completionTokens(Long.valueOf(usage.getCompletionTokens()))
                    .promptTokens(Long.valueOf(usage.getPromptTokens()))
                    .totalTokens(Long.valueOf(usage.getTotalTokens()))
                    .aiServiceType(aiServiceType)  // 添加AI服务类型字段
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            
            boolean saved = this.save(build);
            if (saved) {
                // 记录详细的token消耗信息，包含AI服务类型
                log.info("=== TOKEN消耗统计保存成功 ===");
                log.info("chatId: {}", chatId);
                log.info("AI服务类型: {}", aiServiceType);
                log.info("总Token消耗: {}", usage.getTotalTokens());
                log.info("提示Token: {}", usage.getPromptTokens());
                log.info("完成Token: {}", usage.getCompletionTokens());
                log.info("记录ID: {}", build.getId());
                log.info("记录时间: {}", build.getCreateTime());
                log.info("==============================");
                
                // 如果是图像分析，记录特殊标记
                if ("IMAGE_ANALYSIS".equals(aiServiceType)) {
                    log.info("🖼️ 图像分析AI服务 - Token消耗已记录");
                } else if ("TEXT_CHAT".equals(aiServiceType)) {
                    log.info("💬 文字聊天AI服务 - Token消耗已记录");
                }
            } else {
                log.error("❌ Token消耗统计保存失败 - chatId: {}, AI服务类型: {}", chatId, aiServiceType);
            }
        } catch (Exception e) {
            log.error("❌ 保存Token消耗统计失败 - chatId: {}, AI服务类型: {}", chatId, aiServiceType, e);
        }
    }
}
