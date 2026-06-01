package com.automation.service;

import com.automation.document.AnalysisRecord;
import com.automation.repository.AnalysisRecordRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;

@Service
public class GeminiAutomationService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiAutomationService.class);

    @Value("${gemini.url:https://gemini.google.com/app}")
    private String geminiUrl;

    @Value("${prompt.file:classpath:prompt.txt}")
    private Resource promptResource;

    @Value("${selenium.timeout:30}")
    private int timeoutSeconds;

    @Value("${selenium.headless:false}")
    private boolean headless;

    private final AnalysisRecordRepository repository;

    public GeminiAutomationService(AnalysisRecordRepository repository) {
        this.repository = repository;
    }

    public void runAutomation() {
        WebDriver driver = null;
        try {
            String inputText = readPromptFromFile();
            ChromeOptions options = new ChromeOptions();

            String userFlatpakBrave = System.getProperty("user.home") + "/.local/share/flatpak/exports/bin/com.brave.Browser";
            String systemFlatpakBrave = "/var/lib/flatpak/exports/bin/com.brave.Browser";

            if (new java.io.File(userFlatpakBrave).exists()) {
                logger.info("Using user Flatpak Brave from: {}", userFlatpakBrave);
                options.setBinary(userFlatpakBrave);
            } else if (new java.io.File(systemFlatpakBrave).exists()) {
                logger.info("Using system Flatpak Brave from: {}", systemFlatpakBrave);
                options.setBinary(systemFlatpakBrave);
            }

            logger.info("Using Selenium Manager to auto-detect ChromeDriver...");

            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-debugging-port=9222");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-gpu");

            if (headless) {
                options.addArguments("--headless=new");
            }

            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

            logger.info("Opening Gemini URL: {}", geminiUrl);
            driver.get(geminiUrl);
            Thread.sleep(5000);

            logger.info("Waiting for text input field to be visible...");
            WebElement inputField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[aria-label='Enter a prompt for Gemini']")
                )
            );

            inputField.click();
            Thread.sleep(5000);

            logger.info("Typing prompt...");
            inputField.sendKeys(inputText);
            Thread.sleep(5000);

            logger.info("Sending message...");
            inputField.sendKeys(Keys.ENTER);
            Thread.sleep(5000);

            logger.info("Message sent. Waiting for full response...");
            Thread.sleep(10000);
            String previousText = extractResponseText(driver);
            int stableCount = 0;
            int maxWaitSeconds = 180;
            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < maxWaitSeconds * 1000L) {
                Thread.sleep(3000);
                String currentText = extractResponseText(driver);
                if (currentText.length() > previousText.length() + 50) {
                    previousText = currentText;
                    stableCount = 0;
                    logger.debug("Response still growing... ({} chars)", currentText.length());
                } else {
                    stableCount++;
                    if (stableCount >= 3) {
                        logger.info("Response stable for 9 seconds. Full response received.");
                        break;
                    }
                }
            }

            logger.info("Response received. Extracting text...");

            String responseText = extractResponseText(driver);

            logger.info("Response text length: {} characters", responseText.length());

            saveToDatabase(inputText, responseText);

            logger.info("Automation completed successfully!");

        } catch (Exception e) {
            logger.error("Error during automation: {}", e.getMessage(), e);
        } finally {
            if (driver != null) {
                logger.info("Closing browser...");
                driver.quit();
            }
        }
    }

    private String extractResponseText(WebDriver driver) {
        try {
            String text = (String) ((JavascriptExecutor) driver).executeScript(
                "var log = document.querySelector('[role=\"log\"]');" +
                "return log ? log.innerText : document.body.innerText;"
            );
            return text != null ? text.trim() : "";
        } catch (Exception e) {
            logger.warn("JS extraction failed, falling back to body text: {}", e.getMessage());
            return driver.findElement(By.tagName("body")).getText().trim();
        }
    }

    private String readPromptFromFile() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(promptResource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            logger.warn("Failed to read prompt file, using default: {}", e.getMessage());
            return "who are you";
        }
    }

    private void saveToDatabase(String prompt, String responseText) {
        try {
            AnalysisRecord record = new AnalysisRecord(prompt, responseText);
            repository.save(record);
            logger.info("Saved analysis record to MongoDB (id: {}, response length: {})",
                record.getId(), responseText.length());
            logger.info("Total records in database: {}", repository.count());
        } catch (Exception e) {
            logger.error("Failed to save to MongoDB: {}", e.getMessage(), e);
        }
    }
}
