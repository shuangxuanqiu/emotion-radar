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
import cn.chat.ggy.chataiagent.entity.ConsumeStatistic;
import cn.chat.ggy.chataiagent.service.ConsumeStatisticService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * token统计表 控制层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@RestController
@RequestMapping("/consumeStatistic")
public class ConsumeStatisticController {

    @Autowired
    private ConsumeStatisticService consumeStatisticService;

    /**
     * 保存token统计表。
     *
     * @param consumeStatistic token统计表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody ConsumeStatistic consumeStatistic) {
        return consumeStatisticService.save(consumeStatistic);
    }

    /**
     * 根据主键删除token统计表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return consumeStatisticService.removeById(id);
    }

    /**
     * 根据主键更新token统计表。
     *
     * @param consumeStatistic token统计表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ConsumeStatistic consumeStatistic) {
        return consumeStatisticService.updateById(consumeStatistic);
    }

    /**
     * 查询所有token统计表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<ConsumeStatistic> list() {
        return consumeStatisticService.list();
    }

    /**
     * 根据主键获取token统计表。
     *
     * @param id token统计表主键
     * @return token统计表详情
     */
    @GetMapping("getInfo/{id}")
    public ConsumeStatistic getInfo(@PathVariable Long id) {
        return consumeStatisticService.getById(id);
    }

    /**
     * 分页查询token统计表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ConsumeStatistic> page(Page<ConsumeStatistic> page) {
        return consumeStatisticService.page(page);
    }

}
