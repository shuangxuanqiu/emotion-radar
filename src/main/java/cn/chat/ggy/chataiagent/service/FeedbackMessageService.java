package cn.chat.ggy.chataiagent.service;


import cn.chat.ggy.chataiagent.DTO.VO.FeedbackMessageVO;
import cn.chat.ggy.chataiagent.entity.FeedbackMessage;
import com.mybatisflex.core.service.IService;

/**
 * 生成内容反馈表 服务层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
public interface FeedbackMessageService extends IService<FeedbackMessage> {

    void create(FeedbackMessageVO vo);
}
