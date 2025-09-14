package cn.chat.ggy.chataiagent.config;

import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步配置类
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {
    
    private ThreadPoolExecutor databaseExecutor;

    /**
     * 数据库异步操作线程池
     * 用于异步保存数据库操作，不阻塞主业务流程
     */
    @Bean("databaseAsyncExecutor")
    public Executor databaseAsyncExecutor() {
        // 核心线程数：同时处理数据库异步操作的线程数
        int corePoolSize = 3;
        
        // 最大线程数：高峰期最多的线程数
        int maxPoolSize = 8;
        
        // 空闲线程存活时间
        long keepAliveTime = 60L;
        
        // 队列容量：等待执行的任务队列大小
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(10);
        
        // 自定义线程工厂
        ThreadFactory threadFactory = new DatabaseAsyncThreadFactory();
        
        // 拒绝策略：当线程池和队列都满时，由调用者线程执行
        RejectedExecutionHandler rejectedHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        
        // 创建ThreadPoolExecutor
        this.databaseExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                rejectedHandler
        );
        
        // 允许核心线程超时
        this.databaseExecutor.allowCoreThreadTimeOut(true);
        
        log.info("数据库异步线程池初始化完成 - 核心线程数: {}, 最大线程数: {}, 队列容量: {}", 
                corePoolSize, maxPoolSize, workQueue.remainingCapacity());
        
        // 使用TTL装饰器包装线程池，支持TransmittableThreadLocal传递
        return TtlExecutors.getTtlExecutor(this.databaseExecutor);
    }
    
    /**
     * 自定义线程工厂
     * 为数据库异步操作线程设置有意义的名称，并支持TTL
     */
    private static class DatabaseAsyncThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix = "database-async-ttl-";
        
        @Override
        public Thread newThread(Runnable runnable) {
            // 使用TTL包装Runnable，确保线程局部变量能正确传递
            Runnable ttlRunnable = TtlRunnable.get(runnable);
            Thread thread = new Thread(ttlRunnable, namePrefix + threadNumber.getAndIncrement());
            // 设置为守护线程，JVM退出时不会阻塞
            thread.setDaemon(true);
            // 设置线程优先级
            thread.setPriority(Thread.NORM_PRIORITY);
            log.debug("创建TTL支持的异步线程: {}", thread.getName());
            return thread;
        }
    }
    
    /**
     * 应用关闭时优雅关闭线程池
     */
    @PreDestroy
    public void shutdown() {
        if (databaseExecutor != null) {
            log.info("开始关闭数据库异步线程池...");
            
            // 关闭线程池，不再接受新任务
            databaseExecutor.shutdown();
            
            try {
                // 等待已提交的任务完成，最多等待60秒
                if (!databaseExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.warn("等待60秒后仍有任务未完成，强制关闭线程池");
                    // 强制关闭
                    databaseExecutor.shutdownNow();
                    
                    // 再等待30秒
                    if (!databaseExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                        log.error("强制关闭后仍有任务未完成");
                    }
                }
                log.info("数据库异步线程池已优雅关闭");
            } catch (InterruptedException e) {
                log.warn("等待线程池关闭时被中断，强制关闭");
                databaseExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
