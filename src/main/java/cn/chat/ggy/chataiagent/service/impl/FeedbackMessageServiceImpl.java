package cn.chat.ggy.chataiagent.service.impl;


import cn.chat.ggy.chataiagent.exception.BusinessException;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.chat.ggy.chataiagent.model.VO.FeedbackMessageVO;
import cn.chat.ggy.chataiagent.model.dto.feedback.FeedbackQueryRequest;
import cn.chat.ggy.chataiagent.model.entity.FeedbackMessage;
import cn.chat.ggy.chataiagent.mapper.FeedbackMessageMapper;

import cn.chat.ggy.chataiagent.service.FeedbackMessageService;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 生成内容反馈表 服务层实现。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@Service
public class FeedbackMessageServiceImpl extends ServiceImpl<FeedbackMessageMapper, FeedbackMessage>  implements FeedbackMessageService {

    @Override
    public void create(FeedbackMessageVO vo) {
        FeedbackMessage build = FeedbackMessage.builder().messageType(vo.getMessageType())
                .feedBackMessage(vo.getFeedBackMessage())
                .resultStructure(JSONUtil.toJsonStr(vo.getResultStructure()))
                .chatId(vo.getChatId())
                .build();
        this.save(build);
    }

    /**
     * 获取 QueryWrapper
     * @param feedbackMessage
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(FeedbackQueryRequest feedbackMessage) {
        if (feedbackMessage == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = feedbackMessage.getId();
        String chatId = feedbackMessage.getChatId();
        Integer messageType = feedbackMessage.getMessageType();
        String sortField = feedbackMessage.getSortField();
        String sortOrder = feedbackMessage.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("chatId", chatId)
                .eq("messageType", messageType).orderBy(sortField, "ascend".equals(sortOrder));
    }
}
