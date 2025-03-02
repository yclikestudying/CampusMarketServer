package com.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 线程池配置类
 * 用于自定义和配置线程池，供Spring异步任务使用。
 */
@Configuration // 标记该类为配置类，Spring容器会加载该类中的Bean定义
public class ThreadPoolConfig {

    /**
     * 自定义线程池Bean
     * 通过@Bean注解将ThreadPoolTaskExecutor实例注册到Spring容器中。
     * 线程池的名称为"customTaskExecutor"，可以在@Async注解中指定使用该线程池。
     *
     * @return ThreadPoolTaskExecutor 自定义的线程池实例
     */
    @Bean(name = "customTaskExecutor")
    public ThreadPoolTaskExecutor customTaskExecutor() {
        // 创建ThreadPoolTaskExecutor实例
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 设置核心线程数：线程池中始终保持存活的线程数量
        // 即使线程处于空闲状态，也不会被销毁，除非设置了allowCoreThreadTimeOut
        executor.setCorePoolSize(10);

        // 设置最大线程数：线程池中允许的最大线程数量
        // 当队列满了之后，线程池会创建新线程，直到达到最大线程数
        executor.setMaxPoolSize(20);

        // 设置任务队列容量：用于存放等待执行的任务的队列大小
        // 当线程池中的线程都在忙碌时，新任务会进入队列等待
        executor.setQueueCapacity(50);

        // 设置线程空闲时间：当线程池中的线程数超过核心线程数时，多余的空闲线程的存活时间
        // 超过该时间后，空闲线程会被销毁，直到线程数等于核心线程数
        executor.setKeepAliveSeconds(60);

        // 设置线程名称前缀：方便在日志中识别线程
        // 例如，线程名称会显示为"custom-task-1"、"custom-task-2"等
        executor.setThreadNamePrefix("custom-task-");

        // 初始化线程池
        executor.initialize();

        // 返回配置好的线程池实例
        return executor;
    }
}