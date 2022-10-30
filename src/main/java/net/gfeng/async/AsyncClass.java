package net.gfeng.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncClass {
    @Async("taskExecutor")
    public void doLog(String str){
        log.info(str);
    }
}
