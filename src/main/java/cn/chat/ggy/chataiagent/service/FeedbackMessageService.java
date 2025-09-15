package cn.chat.ggy.chataiagent.service;


import cn.chat.ggy.chataiagent.model.VO.FeedbackMessageVO;
import cn.chat.ggy.chataiagent.model.dto.feedback.FeedbackQueryRequest;
import cn.chat.ggy.chataiagent.model.entity.FeedbackMessage;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;

/**
 * 生成内容反馈表 服务层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
public interface FeedbackMessageService extends IService<FeedbackMessage> {

    void create(FeedbackMessageVO vo);

    QueryWrapper getQueryWrapper(FeedbackQueryRequest feedbackMessage);
}
