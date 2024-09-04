package com.serversentry.server_sentry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oshi.SystemInfo;

@Configuration
public class OshiConfig {

    @Bean
    SystemInfo systemInfo() {
        return new SystemInfo();
    }
}