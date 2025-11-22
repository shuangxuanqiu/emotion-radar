package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.model.ImageOcr.UserInfoList;
import cn.hutool.core.io.FileUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ImageAnalysisAPPTest {
    @Resource
    private ImageAnalysisAPP imageAnalysisAPP;

    @Test
    void ocrImage() throws IOException {
        // ==================== 测试开始，记录总开始时间 ====================
        long totalStartTime = System.currentTimeMillis();
        
        // 图片目录路径
        String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir") + "/tmp/images";
        String path = CODE_OUTPUT_ROOT_DIR + File.separator + "aitest5.jpg";
        
        File imageFile = new File(path);
        long fileSize = imageFile.length();

        log.info("==================== 图片OCR测试开始 ====================");
        log.info("文件路径: {}", path);
        log.info("文件大小: {} bytes ({} KB)", fileSize, String.format("%.2f", fileSize / 1024.0));
        
        // 创建 MockMultipartFile
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            // ==================== 步骤1: 创建 MockMultipartFile ====================
            long step1StartTime = System.currentTimeMillis();
            
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file",                           // 参数名
                    imageFile.getName(),               // 原始文件名
                    "image/jpeg",                      // 内容类型
                    fis                                // 文件输入流
            );
            
            long step1Duration = System.currentTimeMillis() - step1StartTime;
            log.info("步骤1 - 创建MockMultipartFile完成，耗时: {} ms", step1Duration);
            
            // ==================== 步骤2: 执行图片OCR ====================
            long step2StartTime = System.currentTimeMillis();
            log.info("步骤2 - 开始执行图片OCR分析...");
            
            String result = imageAnalysisAPP.ocrImageText(
                    "请详细分析这张图片的内容", 
                    multipartFile,
                    "test-chat-id-121212"
            );
            
            long step2Duration = System.currentTimeMillis() - step2StartTime;
            log.info("步骤2 - 图片OCR分析完成，耗时: {} ms ({} 秒)", 
                    step2Duration, String.format("%.2f", step2Duration / 1000.0));
            
            // ==================== 步骤3: 验证结果 ====================
            long step3StartTime = System.currentTimeMillis();
            
            assertNotNull(result, "OCR结果不应为null");
            
            long step3Duration = System.currentTimeMillis() - step3StartTime;
            log.info("步骤3 - 结果验证完成，耗时: {} ms", step3Duration);
            
            // ==================== 测试完成，输出统计信息 ====================
            long totalDuration = System.currentTimeMillis() - totalStartTime;
            
            log.info("==================== 测试完成统计 ====================");
            log.info("测试结果: {}", result);
            log.info("==================== 耗时统计 ====================");
            log.info("步骤1 - 创建MockMultipartFile: {} ms", step1Duration);
            log.info("步骤2 - OCR分析处理: {} ms ({} 秒)", step2Duration, String.format("%.2f", step2Duration / 1000.0));
            log.info("步骤3 - 结果验证: {} ms", step3Duration);
            log.info("总耗时: {} ms ({} 秒)", totalDuration, String.format("%.2f", totalDuration / 1000.0));
            log.info("平均处理速度: {} KB/秒", String.format("%.2f", (fileSize / 1024.0) / (totalDuration / 1000.0)));
            log.info("====================================================");
            
        } catch (Exception e) {
            long totalDuration = System.currentTimeMillis() - totalStartTime;
            log.error("==================== 测试失败 ====================");
            log.error("失败耗时: {} ms ({} 秒)", totalDuration, String.format("%.2f", totalDuration / 1000.0));
            log.error("错误信息: ", e);
            fail("测试执行失败: " + e.getMessage());
        }
    }
    

}