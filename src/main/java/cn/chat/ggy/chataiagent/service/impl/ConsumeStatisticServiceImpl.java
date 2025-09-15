package cn.chat.ggy.chataiagent.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.chat.ggy.chataiagent.model.entity.ConsumeStatistic;
import cn.chat.ggy.chataiagent.mapper.ConsumeStatisticMapper;
import cn.chat.ggy.chataiagent.service.ConsumeStatisticService;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

/**
 * token统计表 服务层实现。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@Service
public class ConsumeStatisticServiceImpl extends ServiceImpl<ConsumeStatisticMapper, ConsumeStatistic>  implements ConsumeStatisticService{

    @Override
    public void create(String chatId, Usage usage) {
        //封装实体，并保存
        ConsumeStatistic build = ConsumeStatistic.builder().chatId(chatId)
                .completionTokens(Long.valueOf(usage.getCompletionTokens()))
                .promptTokens(Long.valueOf(usage.getPromptTokens())) // 修复：应该是getPromptTokens()
                .totalTokens(Long.valueOf(usage.getTotalTokens())).build();
        this.save(build);
    }
}
