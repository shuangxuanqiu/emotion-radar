package cn.chat.ggy.chataiagent.service;

import cn.chat.ggy.chataiagent.DTO.ResultInfo;
import com.mybatisflex.core.service.IService;
import cn.chat.ggy.chataiagent.entity.ChatContent;
import org.springframework.scheduling.annotation.Async;

/**
 * 对话内容表 服务层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
public interface ChatContentService extends IService<ChatContent> {


    void saveChatContentAsync(ResultInfo resultInfo,String chatId,String resultUrl, Long userId);
}
