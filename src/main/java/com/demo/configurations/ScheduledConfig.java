package com.demo.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);  // Số lượng luồng
        scheduler.setThreadNamePrefix("ScheduledTask-");
        scheduler.initialize();
        return scheduler;
    }

    // initialDelay thời gian deley cho lần đầu, fixedRate khoảng cách giữa các lần chạy method, fixedDelay tương tự nhưng phải đợi hoàn thành xong mới đc tiếp tục
//    @Scheduled(initialDelay = 8000,fixedDelay= 10000)
    public void CleanBlackList() {
        //check when token invalid if false remove the token from table blacklist
        System.out.println("task running");
    }

//    @Scheduled(cron = "0 0 12 * * ?") // Thực hiện tác vụ vào lúc 12h trưa hàng ngày
//    public void task2() {
//        // Thực hiện các tác vụ nền 2
//    }
}
