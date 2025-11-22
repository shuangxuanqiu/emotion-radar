package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.Constant.AppConstant;
import cn.chat.ggy.chataiagent.app.ImageAnalysisSimplifyAPP;
import cn.chat.ggy.chataiagent.app.chatScene.ChatJobAPP;
import cn.chat.ggy.chataiagent.app.chatScene.DefaultAllAPP;
import cn.chat.ggy.chataiagent.app.routingAPP;
import cn.chat.ggy.chataiagent.model.ImageOcr.UserInfoList;
import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.model.enums.ChatScene;
import cn.chat.ggy.chataiagent.model.saver.HtmlCodeResult;
import cn.chat.ggy.chataiagent.app.ChatBotApp;
import cn.chat.ggy.chataiagent.app.ImageAnalysisAPP;
import cn.chat.ggy.chataiagent.core.saver.HtmlCodeFileSaverTemplate;
import cn.chat.ggy.chataiagent.core.HtmlTemplateOptimizer;
import cn.chat.ggy.chataiagent.exception.BusinessException;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.chat.ggy.chataiagent.service.ChatAIAssistant;
import cn.chat.ggy.chataiagent.service.ImageAnalysisService;
import cn.chat.ggy.chataiagent.service.CacheService;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ChatAIAssistantImpl implements ChatAIAssistant {

    @Resource
    private ImageAnalysisSimplifyAPP imageAnalysisSimplifyAPP;
    @Resource
    private ChatBotApp chatBotApp;
    @Resource
    private DefaultAllAPP defaultAllAPP;
    @Resource
    private ChatJobAPP chatJobAPP;
    @Resource
    private routingAPP routingAPP;
    @Resource
    private ImageAnalysisAPP imageAnalysisAPP;
    @Resource
    private ImageAnalysisService imageAnalysisService;

    @Resource
    private HtmlTemplateOptimizer htmlTemplateOptimizer;
    @Resource
    private CacheService cacheService;
    private static final HtmlCodeFileSaverTemplate htmlCodeFileSaver = new HtmlCodeFileSaverTemplate();

    /**
     * 核心解析方法
     * @param message  用户的 message
     * @param file     上传的图片
     * @param emotionalIndex 情绪值
     * @param conversationScene 聊天背景
     * @param chatId   会话 id
     * @return
     * @throws IOException
     */
    @Override
    public ResultInfo chatHelpMe(String message, MultipartFile file, Long emotionalIndex,String conversationScene, String chatId) throws IOException {
        long startTime = System.currentTimeMillis();

            String imagePath = null;
            String imageAnalysisResult = null;
            String fileHash = null;
            ChatScene routedScene = ChatScene.DEFAULT_ALL; // 默认场景
            
            // 第一步：处理图片（如果有的话）- 使用CompletableFuture并行处理
                log.info("开始并行处理：图片分析 + 路由判断，文件名: {}, 大小: {} bytes", 
                        file.getOriginalFilename(), file.getSize());
                
                // 保存图片文件（必须先完成）
                imagePath = imageAnalysisService.saveImageFile(file);
                log.info("图片保存成功，路径: {}", imagePath);
                
                // 并行任务1：图片分析 - 使用 exceptionally 处理异常
                CompletableFuture<String> imageAnalysisFuture = CompletableFuture
                        .supplyAsync(() -> {
                            log.info("图片分析任务开始");
                            String result = imageAnalysisAPP.ocrImageText(
                                    "请分析这张聊天界面截图中的内容", file, chatId);
                            log.info("图片分析任务完成");
                            return result;
                        })
                        .exceptionally(ex -> {
                            log.error("图片分析任务失败: {}", ex.getMessage(), ex);
                            return null;  // 返回 null 作为失败的默认值
                        });
                
                // 并行任务2：路由判断 - 使用 exceptionally 处理异常
                CompletableFuture<ChatScene> routingFuture = CompletableFuture
                        .supplyAsync(() -> {
                            log.info("路由判断任务开始");
                            ChatScene scene = routingAPP.routeToScene(conversationScene, emotionalIndex);
                            log.info("路由判断任务完成: {}", scene.getValue());
                            return scene;
                        })
                        .exceptionally(ex -> {
                            log.error("路由判断任务失败: {}", ex.getMessage(), ex);
                            return ChatScene.DEFAULT_ALL;  // 返回默认场景
                        });
                
                // 使用 thenCombine 组合两个异步任务
                // 使用 handle 统一处理成功和失败情况，完全避免 try-catch
                final String finalImagePath = imagePath;
                AnalysisRoutingResult combinedResult = imageAnalysisFuture
                        .thenCombine(routingFuture, (analysisResult, scene) -> {
                            log.info("并行任务完成 - 图片分析: {}, 路由结果: {}", 
                                    analysisResult != null ? "成功" : "失败", 
                                    scene.getValue());
                            
                            // 异步保存图片分析信息到数据库（不阻塞主流程）
                            if (analysisResult != null) {
                                imageAnalysisService.saveImageAnalysisAsync(
                                        finalImagePath, analysisResult, chatId, null);
                            }
                            
                            // 返回组合结果
                            return new AnalysisRoutingResult(analysisResult, scene);
                        })
                        .orTimeout(30, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.error("并行任务或组合过程失败: {}", ex.getMessage(), ex);
                            // 返回默认值
                            return new AnalysisRoutingResult(null, ChatScene.DEFAULT_ALL);
                        })
                        .handle((result, throwable) -> {
                            // handle 方法会在成功或失败后都执行
                            // 用于最终的清理和结果处理
                            if (throwable != null) {
                                log.error("最终异常处理: {}", throwable.getMessage(), throwable);
                                return new AnalysisRoutingResult(null, ChatScene.DEFAULT_ALL);
                            }
                            return result;
                        })
                        .join();  // 等待完成
                
                // 从组合结果中提取数据
                imageAnalysisResult = combinedResult.getAnalysisResult();
                routedScene = combinedResult.getScene();

            
            // 第二步：准备聊天消息
            String chatMessage;
            if (imageAnalysisResult != null) {
                chatMessage = imageAnalysisResult;
            } else {
                chatMessage = message;
            }
            
            // 添加情绪值信息
            if (emotionalIndex != null && emotionalIndex >= 1 && emotionalIndex <= 10) {
                chatMessage += " \n 我要求的情绪值：" + emotionalIndex+" 分 \n";
            }
            //添加聊天背景
            chatMessage += " \n 我要求的聊天背景："+conversationScene+" \n";
            
            // 第三步：根据路由结果选择对应的APP进行AI聊天
            log.info("开始AI聊天处理，chatId: {}, 使用场景: {}, 消息长度: {}", 
                    chatId, routedScene.getValue(), chatMessage.length());
            
            ResultInfo result = selectAppAndChat(routedScene, chatMessage, chatId);
            
            log.info("AI聊天处理完成，chatId: {}, 使用APP: {}", chatId, routedScene.getValue());
            
            // 如果有图片且结果有效，缓存分析结果
            if (fileHash != null && result != null) {
                cacheService.cacheImageAnalysisResult(fileHash, emotionalIndex, result, 3600); // 缓存1小时
                log.debug("分析结果已缓存 - fileHash: {}, emotionalIndex: {}", fileHash, emotionalIndex);
            }

            long endTime = System.currentTimeMillis();
            log.info("接口总耗时: {}ms, chatId: {}, 场景: {}", 
                    (endTime - startTime), chatId, routedScene.getValue());
            return result;
    }

    /**
     * 根据路由场景选择对应的APP进行聊天
     * @param scene 场景枚举
     * @param message 聊天消息
     * @param chatId 会话ID
     * @return 聊天结果
     */
    private ResultInfo selectAppAndChat(ChatScene scene, String message, String chatId) {
        switch (scene) {
            case CHAT_JOB:
                log.info("选择ChatJobAPP处理 - 职场场景知识库");
                return chatJobAPP.doChat(message, chatId);
            
            case DEFAULT_ALL:
            default:
                log.info("选择DefaultAllAPP处理 - 默认全能知识库");
                return defaultAllAPP.doChat(message, chatId);
        }
    }
    

    

    


    @Override
    public String htmlStorage(ResultInfo resultInfo, String chatId) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 输入验证
            if (chatId == null || chatId.trim().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "chatId不能为空");
            }
            
            // 使用优化后的HTML模板生成器，增加内容验证
            String htmlCode = htmlTemplateOptimizer.generateOptimizedHtml(resultInfo, chatId);
            
            // 验证生成的HTML代码完整性
            if (htmlCode == null || htmlCode.length() < 1000) { // HTML模板至少应该有1000字符
                log.warn("生成的HTML代码可能不完整 - chatId: {}, 长度: {}", chatId, 
                        htmlCode != null ? htmlCode.length() : 0);
            }
            
            // 构建结果对象并保存
            HtmlCodeResult build = HtmlCodeResult.builder().htmlCode(htmlCode).build();
            htmlCodeFileSaver.saveCode(build, chatId);
            
            long endTime = System.currentTimeMillis();
            log.info("HTML生成完成 - 耗时: {} ms, chatId: {}, HTML大小: {} bytes", 
                    endTime - startTime, chatId, htmlCode.length());
            
            return String.format("%s/%s", AppConstant.CODE_DEPLOY_HOST, chatId);
            
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("HTML生成失败 - 耗时: {} ms, chatId: {}, 错误: {}", 
                    endTime - startTime, chatId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "HTML生成失败: " + e.getMessage());
        }
    }

    @Override
    public ResultInfo chatHelpMeSimplify(String prompt, MultipartFile file, Long emotionalIndex, String conversationScene, String chatId) {
        return  imageAnalysisSimplifyAPP.ocrImage(prompt, file, chatId);
    }

    /**
     * 内部类：用于存储图片分析和路由判断的组合结果
     */
    private static class AnalysisRoutingResult {
        private final String analysisResult;
        private final ChatScene scene;
        
        public AnalysisRoutingResult(String analysisResult, ChatScene scene) {
            this.analysisResult = analysisResult;
            this.scene = scene;
        }
        
        public String getAnalysisResult() {
            return analysisResult;
        }
        
        public ChatScene getScene() {
            return scene;
        }
    }
}
