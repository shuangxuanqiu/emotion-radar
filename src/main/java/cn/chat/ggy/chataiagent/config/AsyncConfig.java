package cn.chat.ggy.chataiagent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 异步配置类 - 使用JDK21虚拟线程
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 数据库异步操作执行器 - 使用虚拟线程
     * 虚拟线程具有以下优势：
     * 1. 轻量级：可以创建数百万个虚拟线程
     * 2. 自动管理：JVM自动管理虚拟线程到平台线程的映射
     * 3. 天然支持ThreadLocal：无需TTL库即可正确传递线程局部变量
     * 4. 简化配置：无需复杂的线程池参数配置
     */
    @Bean("databaseAsyncExecutor")
    public Executor databaseAsyncExecutor() {
        // 创建虚拟线程执行器，每个任务都会在新的虚拟线程中执行
        Executor virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        
        log.info("数据库异步虚拟线程执行器初始化完成 - 使用JDK21虚拟线程技术");
        
        return virtualThreadExecutor;
    }
    
    /**
     * 注意：虚拟线程执行器无需手动关闭
     * 虚拟线程会在任务完成后自动回收，JVM关闭时会自动清理所有虚拟线程
     * 相比传统线程池，这大大简化了资源管理
     */
}
