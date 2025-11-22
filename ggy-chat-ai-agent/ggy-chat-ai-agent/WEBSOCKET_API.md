@PostMapping(value = "/travel_guide/chat/sse/emitter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@Operation(summary = "图片识别流式接口", description = "上传图片进行AI识别，返回 JSON 文本流（{G:\"文本\"}）")
public SseEmitter doChatWithLoveAppSseEmitter(
@Parameter(description = "上传的图片文件", required = true) @RequestParam("file") MultipartFile file,
@Parameter(description = "请求参数VO", required = true) @ModelAttribute EmotionRadarStreamRequestVO request) {

        // 验证文件
        if (file == null || file.isEmpty()) {
            SseEmitter errorEmitter = new SseEmitter(1000L);
            try {
                errorEmitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"type\":\"error\",\"message\":\"文件不能为空\"}"));
                errorEmitter.complete();
            } catch (Exception e) {
                log.error("发送错误信息失败", e);
            }
            return errorEmitter;
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            SseEmitter errorEmitter = new SseEmitter(1000L);
            try {
                errorEmitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"type\":\"error\",\"message\":\"文件类型必须是图片\"}"));
                errorEmitter.complete();
            } catch (Exception e) {
                log.error("发送错误信息失败", e);
            }
            return errorEmitter;
        }
        log.info("总提示词：{}", request.getMessage());
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        chatEmitters.put(request.getChatId(), emitter);

        // 获取 Flux 数据流并直接订阅
        imageAnalysisStreamAPP.doChatByStream(request.getMessage(), file, request.getChatId())
                .subscribe(
                        chunk -> {
                            try {
                                if (StringUtils.hasText(chunk)) {
                                    String json = objectMapper.writeValueAsString(Map.of("G", chunk));
                                    emitter.send(SseEmitter.event()
                                            .name("text")
                                            .data(json));
                                }
                            } catch (Exception e) {
                                log.error("发送数据失败", e);
                            }
                        },
                        // 处理错误
                        error -> {
                            log.error("流式处理错误", error);
                            try {
                                String json = objectMapper.writeValueAsString(Map.of("type", "error", "message", error.getMessage()));
                                emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data(json));
                                emitter.completeWithError(error);
                            } catch (Exception e) {
                                log.error("发送错误信息失败", e);
                                emitter.completeWithError(error);
                            }
                            chatEmitters.remove(request.getChatId());
                        },
                        // 处理完成
                        () -> {
                            try {
                                String json = objectMapper.writeValueAsString(Map.of("type", "complete", "message", "识别完成"));
                                emitter.send(SseEmitter.event()
                                        .name("complete")
                                        .data(json));
                                emitter.complete();
                            } catch (Exception e) {
                                log.error("完成流式处理失败", e);
                                emitter.completeWithError(e);
                            }
                            chatEmitters.remove(request.getChatId());
        }
                );
        return emitter;
    }

@Schema(description = "情感雷达流式识别请求参数")
@Data
public class EmotionRadarStreamRequestVO {
@Schema(description = "总提示词", example = "请识别这张聊天界面截图")
private String message;

    @Schema(description = "情绪背景", example = "工作朋友")
    private String conversationScene;

    @Schema(description = "情感指数参数", example = "5")
    private Long emotionalIndex;

    @Schema(description = "会话ID")
    private String chatId;

}
