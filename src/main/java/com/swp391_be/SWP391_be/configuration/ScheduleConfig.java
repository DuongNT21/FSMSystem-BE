package com.swp391_be.SWP391_be.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Retryable
public class ScheduleConfig {
}