package cn.chat.ggy.chataiagent.service.impl;


import cn.chat.ggy.chataiagent.DTO.VO.FeedbackMessageVO;
import cn.chat.ggy.chataiagent.entity.FeedbackMessage;
import cn.chat.ggy.chataiagent.mapper.FeedbackMessageMapper;

import cn.chat.ggy.chataiagent.service.FeedbackMessageService;
import cn.hutool.json.JSONUtil;
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
}
