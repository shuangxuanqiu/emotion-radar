package cn.chat.ggy.chataiagent.controller;

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
import cn.chat.ggy.chataiagent.model.entity.ConsumeStatistic;
import cn.chat.ggy.chataiagent.service.ConsumeStatisticService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * token统计表 控制层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@RestController
@RequestMapping("/consumeStatistic")
@Tag(name = "Token消费统计管理", description = "Token使用量统计数据的增删改查操作接口")
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
    @Operation(summary = "保存Token消费统计", description = "创建新的Token使用量统计记录")
    public boolean save(@Parameter(description = "Token消费统计实体对象", required = true) @RequestBody ConsumeStatistic consumeStatistic) {
        return consumeStatisticService.save(consumeStatistic);
    }

    /**
     * 根据主键删除token统计表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(summary = "删除Token消费统计", description = "根据主键ID删除指定的Token消费统计记录")
    public boolean remove(@Parameter(description = "Token消费统计主键ID", required = true, example = "1") @PathVariable Long id) {
        return consumeStatisticService.removeById(id);
    }

    /**
     * 根据主键更新token统计表。
     *
     * @param consumeStatistic token统计表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(summary = "更新Token消费统计", description = "根据主键更新Token消费统计信息")
    public boolean update(@Parameter(description = "Token消费统计实体对象", required = true) @RequestBody ConsumeStatistic consumeStatistic) {
        return consumeStatisticService.updateById(consumeStatistic);
    }

    /**
     * 查询所有token统计表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(summary = "查询所有Token消费统计", description = "获取系统中所有Token消费统计记录列表")
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
    @Operation(summary = "获取Token消费统计详情", description = "根据主键ID获取单个Token消费统计的详细信息")
    public ConsumeStatistic getInfo(@Parameter(description = "Token消费统计主键ID", required = true, example = "1") @PathVariable Long id) {
        return consumeStatisticService.getById(id);
    }

    /**
     * 分页查询token统计表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(summary = "分页查询Token消费统计", description = "根据分页参数查询Token消费统计列表")
    public Page<ConsumeStatistic> page(@Parameter(description = "分页查询参数", required = true) Page<ConsumeStatistic> page) {
        return consumeStatisticService.page(page, QueryWrapper.create().orderBy("createTime",false));
    }

}
