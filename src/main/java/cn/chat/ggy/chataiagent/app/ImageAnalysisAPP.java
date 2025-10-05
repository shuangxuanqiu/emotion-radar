package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.model.ImageOcr.UserInfoList;
import cn.chat.ggy.chataiagent.advisor.MyLoggerAdvisor;
import cn.chat.ggy.chataiagent.advisor.ImageAnalysisObservabilityAdvisor;
import cn.chat.ggy.chataiagent.monitor.MonitorContextHolder;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class ImageAnalysisAPP {
    private final ChatClient chatClient;
    private final String systemPrompt;
    
    @Value("${spring.ai.dashscope.chat-vl.options.model}")
    private String imageAnalysisModel;

    /**
     * 图片解析的 chatclient
     * @param dashscopeChatModel AI聊天模型
     * @param promptImageAnalysis 图片分析提示词
     * @param imageAnalysisObservabilityAdvisor 图像分析可观测性顾问
     */
    public ImageAnalysisAPP(ChatModel dashscopeChatModel, 
                           @Qualifier("promptImageAnalysis") String promptImageAnalysis,
                           ImageAnalysisObservabilityAdvisor imageAnalysisObservabilityAdvisor) {
        this.systemPrompt = promptImageAnalysis;
        //初始化构建
        chatClient =  ChatClient.builder(dashscopeChatModel)
                //引入系统提示词
                .defaultSystem(systemPrompt)
                //顾问，拦截器
                .defaultAdvisors(
                        //自定义 advisor
                        new MyLoggerAdvisor()
                        //图像分析AI可观测性 advisor
                        ,imageAnalysisObservabilityAdvisor
                )
                .build();
    }

    /**
     * 图片识别核心方法 - 优化版本
     * @param prompt  提示词
     * @param file    图片
     * @param chatId  会话 id
     * @return
     */
   public UserInfoList ocrImage(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt, MultipartFile file, String chatId) {
       long startTime = System.currentTimeMillis();
       try {
           log.info("开始图片OCR处理 - chatId: {}, 原始文件大小: {} bytes", chatId, file.getSize());
           
           // 步骤1：直接处理图片压缩
           byte[] compressedImageData;
           try {
               compressedImageData = compressImageOptimized(file);
           } catch (IOException e) {
               log.error("图片压缩失败，使用原图 - chatId: {}", chatId, e);
               compressedImageData = file.getBytes();
           }
           
           long compressionTime = System.currentTimeMillis() - startTime;
           log.info("图片压缩完成 - chatId: {}, 压缩后大小: {} bytes, 压缩耗时: {} ms, 压缩率: {:.1f}%", 
                   chatId, compressedImageData.length, compressionTime,
                   (1.0 - (double) compressedImageData.length / file.getSize()) * 100);
           
           // 步骤2：创建优化的Media对象
           ByteArrayResource imageResource = new ByteArrayResource(compressedImageData) {
               @Override
               public String getFilename() {
                   return file.getOriginalFilename();
               }
           };
           
           Media media = new Media(MimeTypeUtils.parseMimeType(file.getContentType()), imageResource);
           
           // 步骤3：构建消息和提示词
           UserMessage message = UserMessage.builder()
                   .text(prompt)
                   .media(media)
                   .build();
           
           message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);
           
           // 步骤4：优化的模型配置 - 降低分辨率处理以提升速度
           Prompt chatPrompt = new Prompt(message,
                   DashScopeChatOptions.builder()
                           .withModel(imageAnalysisModel)
                           .withEnableThinking(true)
                           .withMultiModel(true)
                           .withVlHighResolutionImages(false)  // 关闭高分辨率以提升速度
                           .withTemperature(0.6)               // 降低随机性以提升响应速度
                           .build());
           
           // 步骤5：执行AI分析
           long analysisStart = System.currentTimeMillis();
           
           // 设置监控上下文
           MonitorContextHolder.setContext("chatId", chatId);
           MonitorContextHolder.setContext("requestUri", "/api/image/analysis");
           
           UserInfoList entity = chatClient.prompt(chatPrompt)
                   .toolContext(Map.of("chatId", chatId))
                   .call()
                   .entity(UserInfoList.class);
           
           long totalTime = System.currentTimeMillis() - startTime;
           long analysisTime = System.currentTimeMillis() - analysisStart;
           
           log.info("图片OCR完成 - chatId: {}, AI分析耗时: {} ms, 总耗时: {} ms", 
                   chatId, analysisTime, totalTime);
           
           return entity;
           
       } catch (Exception e) {
           long totalTime = System.currentTimeMillis() - startTime;
           log.error("图片OCR处理失败 - chatId: {}, 耗时: {} ms", chatId, totalTime, e);
           throw new RuntimeException("图片分析失败: " + e.getMessage(), e);
       }
   }
   
   /**
    * 智能图片压缩方法 - 根据图片大小和类型进行优化压缩
    * @param file 原始图片文件
    * @return 压缩后的图片字节数组
    * @throws IOException 处理异常
    */
   private byte[] compressImageOptimized(MultipartFile file) throws IOException {
       byte[] originalBytes = file.getBytes();
       
       // 如果文件已经很小（小于500KB），直接返回
       if (originalBytes.length <= 500 * 1024) {
           log.debug("图片文件较小（{}KB），跳过压缩", originalBytes.length / 1024);
           return originalBytes;
       }
       
       // 读取原始图片
       BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalBytes));
       if (originalImage == null) {
           log.warn("无法解析图片格式，使用原图");
           return originalBytes;
       }
       
       int originalWidth = originalImage.getWidth();
       int originalHeight = originalImage.getHeight();
       
       // 智能缩放策略
       int targetWidth, targetHeight;
       double scaleFactor;
       
       // 根据图片大小确定缩放策略
       if (originalWidth > 2048 || originalHeight > 2048) {
           // 超大图片：缩放到最大2048px
           scaleFactor = Math.min(2048.0 / originalWidth, 2048.0 / originalHeight);
       } else if (originalWidth > 1024 || originalHeight > 1024) {
           // 大图片：缩放到最大1024px
           scaleFactor = Math.min(1024.0 / originalWidth, 1024.0 / originalHeight);
       } else {
           // 中等图片：适度压缩到800px
           scaleFactor = Math.min(800.0 / originalWidth, 800.0 / originalHeight);
           scaleFactor = Math.min(scaleFactor, 1.0); // 不放大
       }
       
       targetWidth = (int) (originalWidth * scaleFactor);
       targetHeight = (int) (originalHeight * scaleFactor);
       
       // 执行高质量缩放
       BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
       Graphics2D g2d = resizedImage.createGraphics();
       
       // 设置高质量渲染参数
       g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
       g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
       g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
       g2d.dispose();
       
       // 输出压缩图片 - 使用JPEG格式以减小文件大小
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       
       // 根据压缩后的尺寸调整JPEG质量
       float quality = 0.85f; // 默认高质量
       if (targetWidth * targetHeight > 1024 * 1024) {
           quality = 0.75f; // 大图片使用中等质量
       } else if (targetWidth * targetHeight > 512 * 512) {
           quality = 0.80f; // 中图片使用中高质量
       }
       
       // 写入JPEG
       var writers = ImageIO.getImageWritersByFormatName("jpg");
       if (writers.hasNext()) {
           var writer = writers.next();
           var writeParam = writer.getDefaultWriteParam();
           writeParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
           writeParam.setCompressionQuality(quality);
           
           writer.setOutput(ImageIO.createImageOutputStream(baos));
           writer.write(null, new javax.imageio.IIOImage(resizedImage, null, null), writeParam);
           writer.dispose();
       } else {
           // 回退到普通JPEG写入
           ImageIO.write(resizedImage, "jpg", baos);
       }
       
       byte[] compressedBytes = baos.toByteArray();
       
       log.debug("图片压缩完成 - 原始尺寸: {}x{}, 压缩尺寸: {}x{}, 缩放比例: {:.2f}, JPEG质量: {:.2f}", 
               originalWidth, originalHeight, targetWidth, targetHeight, scaleFactor, quality);
       
       return compressedBytes;
   }

}
