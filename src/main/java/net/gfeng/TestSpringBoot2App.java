package net.gfeng;

import lombok.extern.slf4j.Slf4j;
import net.gfeng.async.AsyncClass;
import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@EnableAsync
@Slf4j
public class TestSpringBoot2App {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(TestSpringBoot2App.class, args);
        Assert.isTrue(AsyncLoggerContextSelector.isSelected(), "log4j2 的异步 disruptor启动失败");
        AsyncClass ac =ctx.getBean(AsyncClass.class);
        int i=0;
        while(true) {
//            log.info("start success!");
            try{
                ac.doLog("in doLog log success!");
            }catch(RejectedExecutionException e){
                System.out.println("catched RejectedExecutionException:"+i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            i++;
        }
    }


    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(5);
        // 设置最大线程数
        executor.setMaxPoolSize(20);
        // 设置队列容量
        executor.setQueueCapacity(100);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(10);
        // 设置默认线程名称
        executor.setThreadNamePrefix("test");
        // 设置拒绝策略
        //(1) 默认的ThreadPoolExecutor.AbortPolicy   处理程序遭到拒绝将抛出运行时RejectedExecutionException;
        //(2) ThreadPoolExecutor.CallerRunsPolicy 线程调用运行该任务的 execute 本身。此策略提供简单的反馈控制机制，能够减缓新任务的提交速度
        //(3) ThreadPoolExecutor.DiscardPolicy  不能执行的任务将被删除;
        //(4) ThreadPoolExecutor.DiscardOldestPolicy  如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }


}
