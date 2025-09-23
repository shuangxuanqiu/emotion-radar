package cn.chat.ggy.chataiagent.controller;

import cn.chat.ggy.chataiagent.app.DeepSeekAPP;
import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;

import cn.chat.ggy.chataiagent.model.VO.FeedbackMessageVO;
import cn.chat.ggy.chataiagent.app.ChatBasicsApp;
import cn.chat.ggy.chataiagent.chatmemory.RedisChatMemory;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.chat.ggy.chataiagent.exception.ThrowUtils;
import cn.chat.ggy.chataiagent.monitor.MonitorContextHolder;
import cn.chat.ggy.chataiagent.service.*;
import cn.hutool.core.util.RandomUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/chat-ai")
@Tag(name = "情感雷达核心服务", description = "提供图片情感分析、聊天对话、系统监控和健康检查等核心功能接口")
@Slf4j
public class EmotionRadarController {
    @Resource
    private ChatAIAssistant chatAIAssistant;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private FeedbackMessageService feedbackMessageService;
    @Resource
    private ChatBasicsApp chatBasicsApp;
    @Resource
    private DeepSeekAPP deepSeekAPP;
    @Resource
    private CacheService cacheService;
    @Resource
    private ChatSessionManagementService chatSessionManagementService;
    @Resource
    private ChatContentService chatContentService;

    /**
     * 返回一个的内容是 json 格式
     * @param file
     * @param emotionalIndex
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/is-json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "图片情感分析(JSON格式)", description = "上传图片进行AI情感分析，返回JSON格式的分析结果")
    public ResultInfo resuleJson(
            @Parameter(description = "上传的图片文件", required = true) @RequestParam("file") MultipartFile file, 
            @Parameter(description = "情感指数参数", required = true, example = "5") @RequestParam("emotionalIndex") Long emotionalIndex) throws IOException {
        long startTime = System.currentTimeMillis();
        String chatId = RandomUtil.randomString(6);
        ResultInfo resultInfo = chatAIAssistant.chatHelpMe("请分析这张图片的内容", file, emotionalIndex,"", chatId);
        long endTime = System.currentTimeMillis();
        log.info("方法 resuleJson 执行耗时: {} ms, chatId: {}", endTime - startTime, chatId);
        return resultInfo;
    }

    /**
     * 存储来自用户的反馈信息
     * @param vo
     */
    @PostMapping("/chat/user/feedback")
    @Operation(summary = "用户反馈提交", description = "接收并存储用户对AI分析结果的反馈信息")
    public void userFeedback(
            @Parameter(description = "用户反馈信息对象", required = true) @Valid @RequestBody FeedbackMessageVO vo) {
        // 安全获取ResultStructure的详细信息
        String resultStructureInfo = "null";
        if (vo.getResultStructure() != null) {
            var result = vo.getResultStructure();
            resultStructureInfo = String.format("selectedText='%s', timestamp='%s', emotionalIndex=%s", 
                    result.getSelectedText(), result.getTimestamp(), result.getEmotionalIndex());
        }
        
        log.info("来自会话 Id: {}, 反馈类型: {}, 反馈内容: {}, 选择内容: [{}]",
                vo.getChatId(), vo.getMessageType(), vo.getFeedBackMessage(), resultStructureInfo);
        
        //存储来自用户的反馈信息
        feedbackMessageService.create(vo);
    }

    /**
     * 核心服务 emotion-radar
     * @param file
     * @param emotionalIndex
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/emotion-radar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "情感雷达核心服务", description = "上传图片进行AI情感分析，生成HTML格式的可视化分析报告")
    public String resultNoJson(
            @Parameter(description = "上传的图片文件", required = true) @RequestParam("file") MultipartFile file, 
            @Parameter(description = "情感指数参数", required = true, example = "5") @RequestParam("emotionalIndex") Long emotionalIndex,
            @Parameter(description = "聊天背景",required = true) @RequestParam("conversationScene") String conversationScene
            ) throws IOException {
        long startTime = System.currentTimeMillis();
        String chatId = RandomUtil.randomString(6);
        // 文件验证优化
        ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR, "上传的文件不能为空");
        // 文件大小限制 (10MB)
//        ThrowUtils.throwIf(file.getSize() > 10 * 1024 * 1024, ErrorCode.PARAMS_ERROR, "文件大小不能超过10MB");
        // 文件类型验证
        String contentType = file.getContentType();
        ThrowUtils.throwIf(contentType == null || !contentType.startsWith("image/"), ErrorCode.PARAMS_ERROR, "文件大小不能超过10MB");

        log.info("开始处理图片分析请求 - 文件: {}, 大小: {} bytes, chatId: {}", file.getOriginalFilename(), file.getSize(), chatId);

        try {
            //将会话 id 放到ThreadLocal 中
            MonitorContextHolder.setContext("chatId",chatId);
            //核心内容，ai 解析
            ResultInfo resultInfo = chatAIAssistant.chatHelpMe("请分析这张图片的内容", file, emotionalIndex, conversationScene,chatId);

            //制作 html 文件
            String resultUrl = chatAIAssistant.htmlStorage(resultInfo, chatId);
            // 异步保存聊天内容到数据库（不阻塞接口返回）
            chatContentService.saveChatContentAsync(resultInfo,chatId,resultUrl, null);
            long endTime = System.currentTimeMillis();
            log.info("方法 resultNoJson 执行成功 - 耗时: {} ms, chatId: {}, 结果URL: {}", endTime - startTime, chatId, resultUrl);
            return resultUrl;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("方法 resultNoJson 执行失败 - 耗时: {} ms, chatId: {}, 错误: {}", endTime - startTime, chatId, e.getMessage(), e);
            throw e;
        } finally {
            // 清理监控上下文，避免内存泄漏
            MonitorContextHolder.clearContext();
        }
    }

    /**
     * 真正停止AI厂商的回答
     * 不仅关闭前端连接，还会取消后端的AI调用
     *
     * @param chatId 聊天ID
     * @return 停止结果
     */
    @PostMapping(value = "/travel_guide/chat/stop")
    @Operation(summary = "停止AI对话", description = "强制停止指定会话的AI回答，释放相关资源")
    public ResponseEntity<Map<String, Object>> stopChat(
            @Parameter(description = "聊天会话ID", required = true, example = "abc123") @RequestParam String chatId) {
        try {
            Map<String, Object> result = chatSessionManagementService.stopChatSession(chatId);
            boolean success = (Boolean) result.get("success");

            if (success) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(404).body(result);
            }

        } catch (Exception e) {
            log.error("Controller层停止聊天异常 - chatId: {}, 错误: {}", chatId, e.getMessage(), e);

            Map<String, Object> errorResult = new ConcurrentHashMap<>();
            errorResult.put("success", false);
            errorResult.put("chatId", chatId);
            errorResult.put("message", "Controller层发生错误: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(errorResult);
        }
    }

    @GetMapping(value = "/travel_guide/chat/sse/emitter")
    @Operation(summary = "SSE流式对话", description = "建立SSE连接，实现AI流式对话功能")
    public SseEmitter doChatWithLoveAppSseEmitter(
            @Parameter(description = "用户消息内容", required = true, example = "你好") String message, 
            @Parameter(description = "聊天会话ID", required = true, example = "abc123") String chatId) {
        //创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L);//3分钟超时

        // 注册SSE连接到会话管理服务
        chatSessionManagementService.registerSseEmitter(chatId, emitter);

        //获取 Flux 数据流并直接订阅，保存订阅引用以便停止
        Disposable subscription = deepSeekAPP.doChatByStream(message, chatId)
                .subscribe(
                        //处理每条信息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                                log.debug("发送SSE数据 - chatId: {}, 数据长度: {}", chatId, chunk.length());
                            } catch (Exception e) {
                                log.error("发送SSE数据失败 - chatId: {}, 错误: {}", chatId, e.getMessage());
                                emitter.completeWithError(e);
                                chatSessionManagementService.cleanupChatResources(chatId);
                            }
                        },
                        //处理错误
                        error -> {
                            log.error("AI流式调用错误 - chatId: {}, 错误: {}", chatId, error.getMessage());
                            emitter.completeWithError(error);
                            chatSessionManagementService.cleanupChatResources(chatId);
                        },
                        //处理完成
                        () -> {
                            log.info("AI流式调用完成 - chatId: {}", chatId);
                            emitter.complete();
                            chatSessionManagementService.cleanupChatResources(chatId);
                        }
                );

        // 注册AI订阅到会话管理服务
        chatSessionManagementService.registerAiSubscription(chatId, subscription);
        log.info("开始AI流式调用 - chatId: {}, 消息长度: {}", chatId, message.length());

        return emitter;
    }

    @GetMapping(value = "/chat/memory/redis")
    @Operation(summary = "获取聊天记录", description = "从Redis中获取指定会话的聊天记录")
    public List<Message> getRedisChatMemoryList(
            @Parameter(description = "聊天会话ID", required = true, example = "abc123") String chatId) {
        return new RedisChatMemory(redisTemplate).findByConversationId(chatId);
    }

    /**
     * 获取缓存统计信息
     */
    @GetMapping(value = "/cache/stats")
    @Operation(summary = "获取缓存统计", description = "获取系统缓存的统计信息，包括命中率、大小等")
    public String getCacheStats() {
        return cacheService.getCacheStats();
    }

    /**
     * 清空所有缓存
     */
    @PostMapping(value = "/cache/clear")
    @Operation(summary = "清空缓存", description = "清空系统中所有缓存数据")
    public String clearCache() {
        try {
            cacheService.clearAllCache();
            return "缓存清空成功";
        } catch (Exception e) {
            log.error("清空缓存失败: {}", e.getMessage(), e);
            return "缓存清空失败: " + e.getMessage();
        }
    }

    /**
     * 系统健康检查
     */
    @GetMapping(value = "/health")
    @Operation(summary = "系统健康检查", description = "获取系统运行状态信息，包括缓存状态、活跃连接数等")
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new ConcurrentHashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("cacheStats", cacheService.getCacheStats());
        health.put("activeConnections", chatSessionManagementService.getActiveConnectionCount());
        health.put("activeAiCalls", chatSessionManagementService.getActiveAiCallCount());
        health.put("activeChatIds", chatSessionManagementService.getActiveChatIds());
        return health;
    }

    /**
     * 获取活跃聊天统计
     */
    @GetMapping(value = "/chat/active/stats")
    @Operation(summary = "获取活跃聊天统计", description = "获取当前活跃聊天会话的统计信息")
    public Map<String, Object> getActiveChatStats() {
        return chatSessionManagementService.getActiveSessionStats();
    }

    /**
     * 强制清理所有活跃连接（管理员功能）
     */
    @PostMapping(value = "/chat/cleanup/all")
    @Operation(summary = "清理所有活跃连接", description = "管理员功能：强制清理所有活跃的聊天连接和相关资源")
    public ResponseEntity<Map<String, Object>> cleanupAllActiveChats() {
        try {
            Map<String, Object> result = chatSessionManagementService.cleanupAllActiveSessions();
            boolean success = (Boolean) result.get("success");

            if (success) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(500).body(result);
            }

        } catch (Exception e) {
            log.error("Controller层清理所有连接异常: {}", e.getMessage(), e);

            Map<String, Object> errorResult = new ConcurrentHashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Controller层发生错误: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(errorResult);
        }
    }
}