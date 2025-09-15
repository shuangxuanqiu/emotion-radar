package cn.chat.ggy.chataiagent.service;

import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.model.dto.chatcontent.ChatContentQueryRequest;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.core.query.QueryWrapper;
import cn.chat.ggy.chataiagent.model.entity.ChatContent;

/**
 * 对话内容表 服务层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
public interface ChatContentService extends IService<ChatContent> {


    void saveChatContentAsync(ResultInfo resultInfo,String chatId,String resultUrl, Long userId);

    QueryWrapper getQueryWrapper(ChatContentQueryRequest chatContentQueryRequest);
}
