package com.automation.service;

import com.automation.model.StockAnalysisResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class GeminiAutomationService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiAutomationService.class);

    @Value("${gemini.url:https://gemini.google.com/app}")
    private String geminiUrl;

    @Value("${gemini.input.text:who are you}")
    private String inputText;

    @Value("${selenium.timeout:30}")
    private int timeoutSeconds;

    @Value("${selenium.headless:false}")
    private boolean headless;

    public void runAutomation() {
        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            
            // Check for Flatpak Brave (common on Pop OS)
            String userFlatpakBrave = System.getProperty("user.home") + "/.local/share/flatpak/exports/bin/com.brave.Browser";
            String systemFlatpakBrave = "/var/lib/flatpak/exports/bin/com.brave.Browser";
            
            if (new java.io.File(userFlatpakBrave).exists()) {
                logger.info("Using user Flatpak Brave from: {}", userFlatpakBrave);
                options.setBinary(userFlatpakBrave);
            } else if (new java.io.File(systemFlatpakBrave).exists()) {
                logger.info("Using system Flatpak Brave from: {}", systemFlatpakBrave);
                options.setBinary(systemFlatpakBrave);
            }
            
            // Let Selenium Manager (built into Selenium 4.x) handle ChromeDriver automatically
            logger.info("Using Selenium Manager to auto-detect ChromeDriver...");
            
            // Chrome/Brave options
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
            // Skip maximize - window already set to 1920x1080 with --window-size argument
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

            // Navigate to Gemini
            logger.info("Opening Gemini URL: {}", geminiUrl);
            driver.get(geminiUrl);

            // // Wait for the page to fully load
            // logger.info("Waiting for page to load...");
            // Thread.sleep(5000);

            // Wait for input field to be visible using aria-label
            logger.info("Waiting for text input field to be visible (using aria-label)...");
            WebElement inputField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[aria-label='Enter a prompt for Gemini']")
                )
            );
            
            logger.info("Input field is visible. Clicking on it to focus...");
            inputField.click();
            
            // Wait a moment after clicking
            Thread.sleep(500);
            
            logger.info("Typing text into input field...");
            inputField.sendKeys(inputText);

            // Wait for the text to be fully entered
            Thread.sleep(1000);
            logger.info("Text entered successfully. Waiting for send button...");

            // Wait for send button to be visible and clickable using aria-label
            logger.info("Waiting for send button to be visible and clickable...");
            WebElement sendButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[aria-label='Send message']")
                )
            );
            
            wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[aria-label='Send message']")
            ));
            
            // Click send button ONLY ONCE
            logger.info("Clicking send button (ONE TIME ONLY)...");
            sendButton.click();
            logger.info("Send button clicked. Message submitted successfully!");
            
            // Verify send button is no longer clickable/active (indicates message was sent)
            try {
                Thread.sleep(1000);
                logger.info("Verifying message was sent...");
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            // DO NOT click send again - just wait for response
            // Wait for response to appear - Gemini takes time to generate the response
            logger.info("Now waiting patiently for Gemini to generate response (this may take 30-90 seconds)...");
            logger.info("DO NOT interact with the browser - waiting for code element to appear...");
            
            // Wait specifically for the code element containing JSON to be visible
            logger.info("Looking for code element with data-test-id='code-content'...");
            WebDriverWait extendedWait = new WebDriverWait(driver, Duration.ofSeconds(90));
            WebElement codeElement = extendedWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("code[data-test-id='code-content'].code-container")
                )
            );
            
            logger.info("Code element found! Waiting for content to be fully loaded...");
            // Additional wait to ensure the complete JSON is rendered
            Thread.sleep(3000);
            
            // Extract the innerHTML which contains the JSON with HTML span tags
            String htmlContent = codeElement.getAttribute("innerHTML");
            logger.info("Raw HTML content length: {} characters", htmlContent.length());
            
            // Clean HTML tags and entities to get pure JSON
            String cleanedJson = cleanHtmlTags(htmlContent);
            
            logger.info("Cleaned JSON: \n{}", cleanedJson);
            
            // Parse JSON and map to Java object
            ObjectMapper objectMapper = new ObjectMapper();
            StockAnalysisResponse stockAnalysis = objectMapper.readValue(cleanedJson, StockAnalysisResponse.class);
            
            // Print the Java object
            logger.info("\n========== Stock Analysis Object ==========");
            logger.info("Market Status: {}", stockAnalysis.getMarketStatus());
            logger.info("\nSwing Trading Recommendations:");
            if (stockAnalysis.getSwingTradingRecommendations() != null) {
                stockAnalysis.getSwingTradingRecommendations().forEach(stock -> 
                    logger.info("  - {}", stock)
                );
            }
            logger.info("\nDisclaimer: {}", stockAnalysis.getDisclaimer());
            logger.info("==========================================\n");
            
            // Print the JSON message
            logger.info("\n========== JSON Response ==========");
            logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stockAnalysis));
            logger.info("===================================\n");

            logger.info("Automation task completed successfully!");

        } catch (Exception e) {
            logger.error("Error during automation: {}", e.getMessage(), e);
        } finally {
            if (driver != null) {
                logger.info("Closing browser...");
                driver.quit();
            }
        }
    }
    
    /**
     * Clean HTML tags from the JSON content
     * Removes span tags with hljs-* classes and decodes HTML entities
     * @param htmlContent HTML content with span tags
     * @return Cleaned JSON string
     */
    private String cleanHtmlTags(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            logger.warn("HTML content is null or empty");
            return "";
        }
        
        logger.debug("Cleaning HTML tags from content...");
        
        // Remove all HTML tags (span, div, etc.) and decode HTML entities
        String cleaned = htmlContent
            .replaceAll("<span[^>]*>", "")   // Remove opening span tags with any attributes
            .replaceAll("</span>", "")        // Remove closing span tags
            .replaceAll("<[^>]+>", "")        // Remove any other HTML tags
            .replace("&amp;", "&")            // Decode &amp; to &
            .replace("&lt;", "<")             // Decode &lt; to <
            .replace("&gt;", ">")             // Decode &gt; to >
            .replace("&quot;", "\"")          // Decode &quot; to "
            .replace("&apos;", "'")           // Decode &apos; to '
            .replace("&nbsp;", " ")           // Decode &nbsp; to space
            .replaceAll("\\s+\n", "\n")       // Clean up extra whitespace before newlines
            .trim();
        
        logger.debug("Cleaned content length: {} characters", cleaned.length());
        return cleaned;
    }
}
