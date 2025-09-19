package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.exception.BusinessException;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.model.dto.chatcontent.ChatContentQueryRequest;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.chat.ggy.chataiagent.model.entity.ChatContent;
import cn.chat.ggy.chataiagent.mapper.ChatContentMapper;
import cn.chat.ggy.chataiagent.service.ChatContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 对话内容表 服务层实现。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@Service
@Slf4j
public class ChatContentServiceImpl extends ServiceImpl<ChatContentMapper, ChatContent>  implements ChatContentService{


    /**
     * 异步保存聊天内容到数据库
     * @param resultInfo AI生成的结果信息
     * @param userId 用户ID
     */
    @Async("databaseAsyncExecutor")
    @Override
    public void saveChatContentAsync(ResultInfo resultInfo,String chatId,String resultUrl, Long userId) {
        try {
            // 将ResultInfo转换为JSON字符串存储
            String resultJson = JSONUtil.toJsonStr(resultInfo);

            ChatContent chatContent = ChatContent.builder()
                    .resultContent(resultJson)
                    .userId(userId)
                    .resultUrl(resultUrl) // 生成的 url 地址
                    .chatId(chatId)       // 房间 id
                    .tokenQuantity(0L) // 可以根据实际需要计算token数量
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .isDelete(0)
                    .build();

            boolean saved = save(chatContent);
            if (saved) {
                log.info("聊天内容保存成功");
            } else {
                log.error("聊天内容保存失败");
            }
        } catch (Exception e) {
            log.error("保存聊天内容到数据库失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取 QueryWrapper
     * @param chatContentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ChatContentQueryRequest chatContentQueryRequest) {
        if (chatContentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = chatContentQueryRequest.getId();
        String chatId = chatContentQueryRequest.getChatId();
        String sortField = chatContentQueryRequest.getSortField();
        String sortOrder = chatContentQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("chatId", chatId)
                .orderBy(sortField, "ASC".equals(sortOrder));
    }
}
