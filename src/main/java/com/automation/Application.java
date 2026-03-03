package com.automation;

import com.automation.service.GeminiAutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner run(GeminiAutomationService automationService) {
        return args -> {
            logger.info("Starting Gemini Automation...");
            automationService.runAutomation();
            logger.info("Automation completed!");
        };
    }
}
