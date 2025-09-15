package cn.chat.ggy.chataiagent.controller;


import cn.chat.ggy.chataiagent.common.BaseResponse;
import cn.chat.ggy.chataiagent.common.ResultUtils;
import cn.chat.ggy.chataiagent.model.dto.feedback.FeedbackQueryRequest;
import cn.chat.ggy.chataiagent.model.entity.FeedbackMessage;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.chat.ggy.chataiagent.exception.ThrowUtils;
import cn.chat.ggy.chataiagent.service.FeedbackMessageService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 生成内容反馈表 控制层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@RestController
@RequestMapping("/feedbackMessage")
@Tag(name = "用户反馈管理", description = "用户对生成内容反馈信息的增删改查操作接口")
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
    @Operation(summary = "保存用户反馈", description = "创建新的用户反馈记录，记录用户对生成内容的评价和建议")
    public boolean save(@Parameter(description = "用户反馈实体对象", required = true) @RequestBody FeedbackMessage feedbackMessage) {
        return feedbackMessageService.save(feedbackMessage);
    }

    /**
     * 根据主键删除生成内容反馈表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(summary = "删除用户反馈", description = "根据主键ID删除指定的用户反馈记录")
    public boolean remove(@Parameter(description = "用户反馈主键ID", required = true, example = "1") @PathVariable Long id) {
        return feedbackMessageService.removeById(id);
    }

    /**
     * 根据主键更新生成内容反馈表。
     *
     * @param feedbackMessage 生成内容反馈表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(summary = "更新用户反馈", description = "根据主键更新用户反馈信息")
    public boolean update(@Parameter(description = "用户反馈实体对象", required = true) @RequestBody FeedbackMessage feedbackMessage) {
        return feedbackMessageService.updateById(feedbackMessage);
    }

    /**
     * 查询所有生成内容反馈表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(summary = "查询所有用户反馈", description = "获取系统中所有用户反馈记录列表")
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
    @Operation(summary = "获取用户反馈详情", description = "根据主键ID获取单个用户反馈的详细信息")
    public FeedbackMessage getInfo(@Parameter(description = "用户反馈主键ID", required = true, example = "1") @PathVariable Long id) {
        return feedbackMessageService.getById(id);
    }

    /**
     * 分页查询生成内容反馈表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(summary = "分页查询用户反馈", description = "根据分页参数查询用户反馈列表")
    public Page<FeedbackMessage> page(@Parameter(description = "分页查询参数", required = true) Page<FeedbackMessage> page) {
        return feedbackMessageService.page(page);

    }

    /**
     * 分页 参数 查询生成内容反馈表
     *
     * @param feedbackMessage 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页 参数 查询用户反馈", description = "根据分页参数查询用户反馈列表")
    public BaseResponse<Page<FeedbackMessage>> listUserVOByPage(@RequestBody FeedbackQueryRequest feedbackMessage) {
        ThrowUtils.throwIf(feedbackMessage == null, ErrorCode.PARAMS_ERROR);
        long pageNum = feedbackMessage.getPageNum();
        long pageSize = feedbackMessage.getPageSize();
        Page<FeedbackMessage> userPage = feedbackMessageService.page(Page.of(pageNum, pageSize),
                feedbackMessageService.getQueryWrapper(feedbackMessage));
        return ResultUtils.success(userPage);
    }

}
