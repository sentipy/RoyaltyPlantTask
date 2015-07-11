package com.sentilabs.royaltyplanttask.config;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by sentipy on 04/07/15.
 */

@Configuration
@EnableJpaRepositories(basePackages = "com.sentilabs.royaltyplanttask.repository")
@EntityScan("com.sentilabs.royaltyplanttask.entity")
@EnableScheduling
public class AppConfig {
}
