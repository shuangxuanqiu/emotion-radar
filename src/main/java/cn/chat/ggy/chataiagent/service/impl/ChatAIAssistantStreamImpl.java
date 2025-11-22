package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.app.ImageAnalysisStreamAPP;
import cn.chat.ggy.chataiagent.app.chatScene.DeepSeekStreamAPP;
import cn.chat.ggy.chataiagent.model.VO.EmotionRadarStreamRequestVO;
import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultStructure;
import cn.chat.ggy.chataiagent.service.ChatAIAssistantStream;
import cn.chat.ggy.chataiagent.service.ChatContentService;
import cn.chat.ggy.chataiagent.service.ImageAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.springframework.ai.chat.prompt.PromptTemplate;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;

@Slf4j
@Service
public class ChatAIAssistantStreamImpl implements ChatAIAssistantStream {
    private final ObjectMapper objectMapper = new ObjectMapper();
    //默认的图片识别 AI
    @Resource
    private ImageAnalysisStreamAPP imageAnalysisStreamAPP;
    @Resource
    private ChatContentService chatContentService;
    @Resource
    private ImageAnalysisService imageAnalysisService;
    //默认的回复 AI
    @Resource
    private DeepSeekStreamAPP  deepSeekStreamAPP;
    @Override
    public SseEmitter chatHelpMe(MultipartFile file, EmotionRadarStreamRequestVO request, Map<String, SseEmitter> chatEmitters) throws IOException {
        // 初始化 SSE（不立刻结束，串联两段 AI 的流式输出）
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        chatEmitters.put(request.getChatId(), emitter);
        // 1) 保存图片文件（必须先完成，便于后续落库与回溯）
       String imagePath = imageAnalysisService.saveImageFile(file);
        log.info("图片保存成功，路径: {}", imagePath);
        // 2) 开始图片识别阶段；仅在阶段开始时发送一次来源标签
        safeSendMap(emitter, "text", Map.of("G", "<ggy>[image]\n"));
        // 线程安全的累积容器（图片识别文本）
        StringBuffer ocrBuffer = new StringBuffer();
        ocrBuffer.append("<ggy>[image]");//添加一个标头
        imageAnalysisStreamAPP.doChatByStream(request.getMessage(), file, request.getChatId())
                .subscribe(
                        chunk -> {
                            if (StringUtils.hasText(chunk)) {
                                // 增量输出不影响流传递，同时累积到缓冲区
                                ocrBuffer.append(chunk);
                                safeSendMap(emitter, "text", Map.of("G", chunk));
                            }
                        },
                        // 处理错误
                        error -> {
                            // 图片识别阶段错误
                            log.error("流式处理错误", error);
                            safeSendMap(emitter, "error", Map.of("type", "error", "message", error.getMessage()));
                            emitter.completeWithError(error);
                            chatEmitters.remove(request.getChatId());
                        },
                        // 处理完成
                        () -> {
                            // 3) 图片识别完成后先落库（异步，不阻塞 SSE）
                            imageAnalysisService.saveImageAnalysisAsync(
                                    imagePath, ocrBuffer.toString(), request.getChatId(), null);
                            // 4) 进入 DeepSeek 阶段，基于 PromptTemplate 渲染提示词（携带 JSON Schema）
                            String composedPrompt = buildDeepseekPrompt(request, ocrBuffer.toString());
                            // 阶段标签与格式规范提示（仅发送一次，便于前端区分与解析）
                            safeSendMap(emitter, "text", Map.of("G", "\n<ggy>[deepseek]\n"));
                            String schema = buildResultInfoJsonSchema();
                            safeSendMap(emitter, "format", Map.of("type", "format", "schema", schema));
                            // 线程安全的累积容器（DeepSeek 文本）
                            StringBuffer dsBuffer = new StringBuffer();
                            dsBuffer.append("<ggy>[deepseek]");//添加一个标头
                            deepSeekStreamAPP.doChatStream(composedPrompt, request.getChatId())
                                    .subscribe(
                                            dsChunk -> {
                                                if (StringUtils.hasText(dsChunk)) {
                                                    dsBuffer.append(dsChunk);
                                                    safeSendMap(emitter, "text", Map.of("G", dsChunk));
                                                }
                                            },
                                            dsError -> {
                                                // DeepSeek 阶段错误
                                                safeSendMap(emitter, "error", Map.of("type", "error", "message", dsError.getMessage()));
                                                emitter.completeWithError(dsError);
                                                chatEmitters.remove(request.getChatId());
                                            },
                                            () -> {
                                                // 5) 尝试将 DeepSeek 累积文本解析为 ResultInfo，供结构化渲染
                                                ResultInfo ri = tryParseResultInfo(dsBuffer.toString());
                                                if (ri != null) {
                                                    try {
                                                        String riJson = objectMapper.writeValueAsString(ri);
                                                        safeSendString(emitter, "result", riJson);
                                                    } catch (Exception ignore) {}
                                                }
                                                // 6) 落库 DeepSeek 文本（异步，不阻塞 SSE）
                                                chatContentService.saveRadarTextAsync(dsBuffer.toString(), request.getChatId(), null, null);
                                                // 7) 流式结束
                                                safeSendMap(emitter, "complete", Map.of("type", "complete", "message", "全部完成"));
                                                emitter.complete();
                                                chatEmitters.remove(request.getChatId());
                                            }
                                    );
                        }
                );

        return emitter;
    }
    /**
     * 续写接口
     * @param emotionalIndex  情感参数
     * @param emotionalLabels 情绪标签
     * @param chatId          会话 id
     * @return
     */
    @Override
    public SseEmitter continuationChatStream(Long emotionalIndex, String emotionalLabels, String chatId) {
        SseEmitter emitter = new SseEmitter(180000L);
        String schema = buildResultStructureJsonSchema();
        safeSendMap(emitter, "format", Map.of("type", "format", "schema", schema));
        String prompt = buildContinuationPrompt(emotionalIndex, emotionalLabels, schema);
        StringBuffer dsBuffer = new StringBuffer();
        deepSeekStreamAPP.continuationChatStream(prompt, chatId)
                .subscribe(
                        dsChunk -> {
                            if (StringUtils.hasText(dsChunk)) {
                                dsBuffer.append(dsChunk);
                                safeSendMap(emitter, "text", Map.of("G", dsChunk));
                            }
                        },
                        dsError -> {
                            safeSendMap(emitter, "error", Map.of("type", "error", "message", dsError.getMessage()));
                            emitter.completeWithError(dsError);
                        },
                        () -> {
                            List<ResultStructure> results = tryParseResultStructures(dsBuffer.toString());
                            if (results != null && !results.isEmpty()) {
                                try {
                                    String json = objectMapper.writeValueAsString(results);
                                    safeSendString(emitter, "result", json);
                                } catch (Exception ignore) {}
                            }
                            System.out.println("续写返回的结果："+dsBuffer.toString());
                            safeSendMap(emitter, "complete", Map.of("type", "complete", "message", "续写完成"));
                            emitter.complete();
                        }
                );

        return emitter;
    }



    private String buildDeepseekPrompt(EmotionRadarStreamRequestVO request, String ocrText) {
        String schema = buildResultInfoJsonSchema();
        // 使用 Spring AI 的 PromptTemplate 组合上下文与 Schema，避免硬编码串接
        String templateText = """
        请基于以下信息生成面向中文用户的回复建议，并严格按照提供的 JSON Schema 输出：
        
        用户提示:
        {message}
        
        聊天背景:
        {conversationScene}
        
        情感指数:
        {emotionalIndex}
        
        以下是图像OCR还原的聊天记录（逐行换行，禁止合并）：
        {ocrText}
        仅输出符合此 JSON Schema 的内容：
        {jsonSchema}
        """;
        PromptTemplate pt = new PromptTemplate(templateText);
        Map<String, Object> vars = new HashMap<>();
        vars.put("message", StringUtils.hasText(request.getMessage()) ? request.getMessage() : "");
        vars.put("conversationScene", StringUtils.hasText(request.getConversationScene()) ? request.getConversationScene() : "");
        vars.put("emotionalIndex", request.getEmotionalIndex() != null ? String.valueOf(request.getEmotionalIndex()) : "");
        vars.put("ocrText", StringUtils.hasText(ocrText) ? ocrText : "");
        vars.put("jsonSchema", schema);
        return pt.render(vars);
    }

    private String buildResultInfoJsonSchema() {
        try {
            // 优先尝试 Spring AI 的 BeanOutputConverter 生成 JSON Schema
            Class<?> clazz = Class.forName("org.springframework.ai.converter.BeanOutputConverter");
            Object converter = clazz.getConstructor(Class.class).newInstance(ResultInfo.class);
            return (String) clazz.getMethod("getJsonSchema").invoke(converter);
        } catch (Throwable t) {
            // 回退到 victools 生成 Schema，以保障 Schema 始终可用
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
            configBuilder.with(Option.FLATTENED_ENUMS);
            SchemaGeneratorConfig config = configBuilder.build();
            SchemaGenerator generator = new SchemaGenerator(config);
            return generator.generateSchema(ResultInfo.class).toString();
        }
    }

    private ResultInfo tryParseResultInfo(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        try {
            return objectMapper.readValue(text, ResultInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String buildResultStructureJsonSchema() {
        try {
            Class<?> clazz = Class.forName("org.springframework.ai.converter.BeanOutputConverter");
            Object converter = clazz.getConstructor(Class.class).newInstance(ResultStructure.class);
            return (String) clazz.getMethod("getJsonSchema").invoke(converter);
        } catch (Throwable t) {
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
            configBuilder.with(Option.FLATTENED_ENUMS);
            SchemaGeneratorConfig config = configBuilder.build();
            SchemaGenerator generator = new SchemaGenerator(config);
            return generator.generateSchema(ResultStructure.class).toString();
        }
    }

    private String buildContinuationPrompt(Long emotionalIndex, String emotionalLabels, String jsonSchema) {
        String templateText = """
        基于当前会话上下文进行续写，生成 1-2 条中文回复建议，严格遵循以下 JSON Schema，每条建议是一个对象：
        
        情感指数: {emotionalIndex}
        情绪标签: {emotionalLabels}
       
        仅以 NDJSON 输出（每行一个 JSON 对象），不得包含多余说明：
        {jsonSchema}
        """;
        PromptTemplate pt = new PromptTemplate(templateText);
        Map<String, Object> vars = new HashMap<>();
        vars.put("emotionalIndex", emotionalIndex == null ? "" : String.valueOf(emotionalIndex));
        vars.put("emotionalLabels", StringUtils.hasText(emotionalLabels) ? emotionalLabels : "");
        vars.put("jsonSchema", jsonSchema);
        return pt.render(vars);
    }

    private List<ResultStructure> tryParseResultStructures(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        List<ResultStructure> list = new ArrayList<>();
        String[] lines = text.split("\r?\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!StringUtils.hasText(trimmed)) continue;
            try {
                ResultStructure item = objectMapper.readValue(trimmed, ResultStructure.class);
                list.add(item);
            } catch (Exception ignored) {}
            if (list.size() >= 2) break;
        }
        if (!list.isEmpty()) return list;
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructCollectionType(List.class, ResultStructure.class));
        } catch (Exception e) {
            return List.of();
        }
    }

    // 统一的 SSE 发送封装（Map 负载）
    private void safeSendMap(SseEmitter emitter, String name, Map<String, Object> payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            emitter.send(SseEmitter.event().name(name).data(json));
        } catch (Exception e) {
            log.error("发送数据失败", e);
        }
    }

    // 统一的 SSE 发送封装（字符串负载）
    private void safeSendString(SseEmitter emitter, String name, String payload) {
        try {
            emitter.send(SseEmitter.event().name(name).data(payload));
        } catch (Exception e) {
            log.error("发送数据失败", e);
        }
    }
}
