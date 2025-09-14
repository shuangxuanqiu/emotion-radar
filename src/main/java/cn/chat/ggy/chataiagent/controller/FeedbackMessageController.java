package cn.chat.ggy.chataiagent.controller;


import cn.chat.ggy.chataiagent.entity.FeedbackMessage;
import cn.chat.ggy.chataiagent.service.FeedbackMessageService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生成内容反馈表 控制层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@RestController
@RequestMapping("/feedbackMessage")
public class FeedbackMessageController {

    @Autowired
    private FeedbackMessageService feedbackMessageService;

    /**
     * 保存生成内容反馈表。
     *
     * @param feedbackMessage 生成内容反馈表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody FeedbackMessage feedbackMessage) {
        return feedbackMessageService.save(feedbackMessage);
    }

    /**
     * 根据主键删除生成内容反馈表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return feedbackMessageService.removeById(id);
    }

    /**
     * 根据主键更新生成内容反馈表。
     *
     * @param feedbackMessage 生成内容反馈表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody FeedbackMessage feedbackMessage) {
        return feedbackMessageService.updateById(feedbackMessage);
    }

    /**
     * 查询所有生成内容反馈表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<FeedbackMessage> list() {
        return feedbackMessageService.list();
    }

    /**
     * 根据主键获取生成内容反馈表。
     *
     * @param id 生成内容反馈表主键
     * @return 生成内容反馈表详情
     */
    @GetMapping("getInfo/{id}")
    public FeedbackMessage getInfo(@PathVariable Long id) {
        return feedbackMessageService.getById(id);
    }

    /**
     * 分页查询生成内容反馈表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<FeedbackMessage> page(Page<FeedbackMessage> page) {
        return feedbackMessageService.page(page);
    }

}
