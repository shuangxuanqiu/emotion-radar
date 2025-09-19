package cn.chat.ggy.chataiagent.controller;

import cn.chat.ggy.chataiagent.common.BaseResponse;
import cn.chat.ggy.chataiagent.common.ResultUtils;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.chat.ggy.chataiagent.exception.ThrowUtils;
import cn.chat.ggy.chataiagent.model.dto.chatcontent.ChatContentQueryRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.chat.ggy.chataiagent.model.entity.ChatContent;
import cn.chat.ggy.chataiagent.service.ChatContentService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 对话内容表 控制层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@RestController
@RequestMapping("/chatContent")
@Tag(name = "对话内容管理", description = "对话内容表的增删改查操作接口")
public class ChatContentController {

    @Autowired
    private ChatContentService chatContentService;

    /**
     * 保存对话内容表。
     *
     * @param chatContent 对话内容表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(summary = "保存对话内容", description = "创建新的对话内容记录")
    public boolean save(@Parameter(description = "对话内容实体对象", required = true) @RequestBody ChatContent chatContent) {
        return chatContentService.save(chatContent);
    }

    /**
     * 根据主键删除对话内容表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(summary = "删除对话内容", description = "根据主键ID删除指定的对话内容记录")
    public boolean remove(@Parameter(description = "对话内容主键ID", required = true, example = "1") @PathVariable Long id) {
        return chatContentService.removeById(id);
    }

    /**
     * 根据主键更新对话内容表。
     *
     * @param chatContent 对话内容表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(summary = "更新对话内容", description = "根据主键更新对话内容信息")
    public boolean update(@Parameter(description = "对话内容实体对象", required = true) @RequestBody ChatContent chatContent) {
        return chatContentService.updateById(chatContent);
    }

    /**
     * 查询所有对话内容表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(summary = "查询所有对话内容", description = "获取系统中所有对话内容记录列表")
    public List<ChatContent> list() {
        return chatContentService.list();
    }

    /**
     * 根据主键获取对话内容表。
     *
     * @param id 对话内容表主键
     * @return 对话内容表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(summary = "获取对话内容详情", description = "根据主键ID获取单个对话内容的详细信息")
    public ChatContent getInfo(@Parameter(description = "对话内容主键ID", required = true, example = "1") @PathVariable Long id) {
        return chatContentService.getById(id);
    }

    /**
     * 分页查询对话内容表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(summary = "分页查询对话内容", description = "根据分页参数查询对话内容列表")
    public Page<ChatContent> page(@Parameter(description = "分页查询参数", required = true) Page<ChatContent> page) {
        return chatContentService.page(page,QueryWrapper.create().orderBy("createTime",false));
    }

    /**
     * 分页 参数 查询对话内容表
     *
     * @param chatContentQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页 参数 查询对话内容", description = "根据分页参数和chatId查询对话内容列表")
    public BaseResponse<Page<ChatContent>> listChatContentByPage(@RequestBody ChatContentQueryRequest chatContentQueryRequest) {
        ThrowUtils.throwIf(chatContentQueryRequest == null, ErrorCode.PARAMS_ERROR);
        //排序
        chatContentQueryRequest.setSortField("id");
        chatContentQueryRequest.setSortOrder("ASC");

        long pageNum = chatContentQueryRequest.getPageNum();
        long pageSize = chatContentQueryRequest.getPageSize();
        Page<ChatContent> chatContentPage = chatContentService.page(Page.of(pageNum, pageSize),
                chatContentService.getQueryWrapper(chatContentQueryRequest));
        return ResultUtils.success(chatContentPage);
    }

}
