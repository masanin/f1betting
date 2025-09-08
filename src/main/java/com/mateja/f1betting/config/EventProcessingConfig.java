package com.mateja.f1betting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
@EnableAsync
public class EventProcessingConfig {

    @Bean
    public Scheduler eventProcessingScheduler() {
        return Schedulers.newBoundedElastic(
                10,
                10000,
                "event-processor"
        );
    }
}
