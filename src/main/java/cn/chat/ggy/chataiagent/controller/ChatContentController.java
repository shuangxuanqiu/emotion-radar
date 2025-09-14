package cn.chat.ggy.chataiagent.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.chat.ggy.chataiagent.entity.ChatContent;
import cn.chat.ggy.chataiagent.service.ChatContentService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 对话内容表 控制层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@RestController
@RequestMapping("/chatContent")
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
    public boolean save(@RequestBody ChatContent chatContent) {
        return chatContentService.save(chatContent);
    }

    /**
     * 根据主键删除对话内容表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return chatContentService.removeById(id);
    }

    /**
     * 根据主键更新对话内容表。
     *
     * @param chatContent 对话内容表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ChatContent chatContent) {
        return chatContentService.updateById(chatContent);
    }

    /**
     * 查询所有对话内容表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
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
    public ChatContent getInfo(@PathVariable Long id) {
        return chatContentService.getById(id);
    }

    /**
     * 分页查询对话内容表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ChatContent> page(Page<ChatContent> page) {
        return chatContentService.page(page);
    }

}
