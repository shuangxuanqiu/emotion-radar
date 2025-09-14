package cn.chat.ggy.chataiagent.service;

import com.mybatisflex.core.service.IService;
import cn.chat.ggy.chataiagent.entity.ConsumeStatistic;
import org.springframework.ai.chat.metadata.Usage;

/**
 * token统计表 服务层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
public interface ConsumeStatisticService extends IService<ConsumeStatistic> {

    void create(String chatId, Usage usage);
}
