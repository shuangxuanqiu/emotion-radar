package cn.chat.ggy.chataiagent.service;

import com.mybatisflex.core.service.IService;
import cn.chat.ggy.chataiagent.model.entity.ConsumeStatistic;
import org.springframework.ai.chat.metadata.Usage;

/**
 * token统计表 服务层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
public interface ConsumeStatisticService extends IService<ConsumeStatistic> {

    void create(String chatId, Usage usage);
    
    /**
     * 创建token消耗统计记录，支持AI服务类型区分
     * @param chatId 聊天会话ID
     * @param usage token使用情况
     * @param aiServiceType AI服务类型 (IMAGE_ANALYSIS: 图像解析, TEXT_CHAT: 文字聊天)
     */
    void create(String chatId, Usage usage, String aiServiceType);
}
