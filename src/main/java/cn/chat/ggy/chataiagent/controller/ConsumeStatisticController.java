package cn.chat.ggy.chataiagent.controller;

import cn.chat.ggy.chataiagent.common.BaseResponse;
import cn.chat.ggy.chataiagent.common.ResultUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import cn.chat.ggy.chataiagent.model.entity.ConsumeStatistic;
import cn.chat.ggy.chataiagent.service.ConsumeStatisticService;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
@Slf4j
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

    // ==================== 统计分析功能 ====================

    /**
     * 获取指定聊天会话的token统计
     */
    @GetMapping("/chat/{chatId}")
    @Operation(summary = "获取指定聊天会话的token统计", description = "根据chatId查询该会话的所有token消耗记录")
    public BaseResponse<List<ConsumeStatistic>> getTokenStatsByChatId(
            @Parameter(description = "聊天会话ID", required = true) @PathVariable String chatId) {
        
        log.info("查询chatId: {} 的token统计", chatId);
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(ConsumeStatistic::getChatId).eq(chatId)
                .orderBy(ConsumeStatistic::getCreateTime, false);
        
        List<ConsumeStatistic> stats = consumeStatisticService.list(queryWrapper);
        log.info("找到 {} 条token统计记录", stats.size());
        
        return ResultUtils.success(stats);
    }

    /**
     * 按AI服务类型统计token消耗
     */
    @GetMapping("/summary/by-service-type")
    @Operation(summary = "按AI服务类型统计token消耗", description = "统计不同AI服务类型的token消耗情况")
    public BaseResponse<Map<String, Object>> getTokenStatsByServiceType() {
        
        log.info("开始按AI服务类型统计token消耗");
        
        List<ConsumeStatistic> allStats = consumeStatisticService.list();
        
        // 按服务类型分组统计
        Map<String, List<ConsumeStatistic>> groupedByServiceType = allStats.stream()
                .collect(Collectors.groupingBy(
                    stat -> stat.getAiServiceType() != null ? stat.getAiServiceType() : "UNKNOWN"
                ));
        
        Map<String, Object> summary = groupedByServiceType.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        List<ConsumeStatistic> stats = entry.getValue();
                        long totalTokens = stats.stream()
                                .mapToLong(ConsumeStatistic::getTotalTokens)
                                .sum();
                        long totalPromptTokens = stats.stream()
                                .mapToLong(ConsumeStatistic::getPromptTokens)
                                .sum();
                        long totalCompletionTokens = stats.stream()
                                .mapToLong(ConsumeStatistic::getCompletionTokens)
                                .sum();
                        
                        return Map.of(
                            "count", stats.size(),
                            "totalTokens", totalTokens,
                            "promptTokens", totalPromptTokens,
                            "completionTokens", totalCompletionTokens,
                            "avgTokensPerRequest", stats.isEmpty() ? 0 : totalTokens / stats.size()
                        );
                    }
                ));
        
        log.info("AI服务类型统计完成，共 {} 种服务类型", summary.size());
        
        return ResultUtils.success(summary);
    }

    /**
     * 获取今日token统计总览
     */
    @GetMapping("/today/overview")
    @Operation(summary = "获取今日token统计总览", description = "获取今日所有AI服务的token消耗统计概览")
    public BaseResponse<Map<String, Object>> getTodayTokenStatsOverview() {
        
        log.info("查询今日token统计总览");
        
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(ConsumeStatistic::getCreateTime).ge(startOfDay);
        
        List<ConsumeStatistic> todayStats = consumeStatisticService.list(queryWrapper);
        
        // 总体统计
        long totalRequests = todayStats.size();
        long totalTokens = todayStats.stream().mapToLong(ConsumeStatistic::getTotalTokens).sum();
        long totalPromptTokens = todayStats.stream().mapToLong(ConsumeStatistic::getPromptTokens).sum();
        long totalCompletionTokens = todayStats.stream().mapToLong(ConsumeStatistic::getCompletionTokens).sum();
        
        // 按服务类型统计
        Map<String, Long> tokensByServiceType = todayStats.stream()
                .collect(Collectors.groupingBy(
                    stat -> stat.getAiServiceType() != null ? stat.getAiServiceType() : "UNKNOWN",
                    Collectors.summingLong(ConsumeStatistic::getTotalTokens)
                ));
        
        Map<String, Long> requestsByServiceType = todayStats.stream()
                .collect(Collectors.groupingBy(
                    stat -> stat.getAiServiceType() != null ? stat.getAiServiceType() : "UNKNOWN",
                    Collectors.counting()
                ));
        
        Map<String, Object> overview = Map.of(
            "date", startOfDay.toLocalDate().toString(),
            "totalRequests", totalRequests,
            "totalTokens", totalTokens,
            "totalPromptTokens", totalPromptTokens,
            "totalCompletionTokens", totalCompletionTokens,
            "avgTokensPerRequest", totalRequests > 0 ? totalTokens / totalRequests : 0,
            "tokensByServiceType", tokensByServiceType,
            "requestsByServiceType", requestsByServiceType
        );
        
        log.info("今日token统计：总请求 {} 次，总token {} 个", totalRequests, totalTokens);
        
        return ResultUtils.success(overview);
    }

}
