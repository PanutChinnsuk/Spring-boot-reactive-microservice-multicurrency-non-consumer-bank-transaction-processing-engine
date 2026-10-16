package com.panut.centralbanking.appsecuritycore.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@AutoConfiguration
public class TaskSchedulerAutoConfiguration {


    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler tp = new ThreadPoolTaskScheduler();
        tp.setPoolSize(10);


        return tp;
    }
}
