package com.sentilabs.royaltyplanttask.config;

import com.sentilabs.royaltyplanttask.scheduler.DocumentProcessor;
import com.sentilabs.royaltyplanttask.scheduler.IDocumentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by sentipy on 04/07/15.
 */

@Configuration
@EnableJpaRepositories(basePackages = "com.sentilabs.royaltyplanttask.repository")
@EntityScan("com.sentilabs.royaltyplanttask.entity")
@EnableScheduling
public class AppConfig implements SchedulingConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(Executors.newScheduledThreadPool(5));
        for (int i = 0; i < 5; ++i) { //TODO: to take from properties
            IDocumentProcessor iDocumentProcessor = (IDocumentProcessor) applicationContext.getBean("iDocumentProcessor");
            IntervalTask intervalTask = new IntervalTask(() -> iDocumentProcessor.processDocuments()
                    , 10000);
            scheduledTaskRegistrar.addFixedDelayTask(intervalTask);
        }
    }

    @Bean
    @Scope("prototype")
    public IDocumentProcessor iDocumentProcessor() {
        return new DocumentProcessor();
    }
}
