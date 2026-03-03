package com.automation.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class GeminiAutomationService {

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
                System.out.println("Using user Flatpak Brave from: " + userFlatpakBrave);
                options.setBinary(userFlatpakBrave);
            } else if (new java.io.File(systemFlatpakBrave).exists()) {
                System.out.println("Using system Flatpak Brave from: " + systemFlatpakBrave);
                options.setBinary(systemFlatpakBrave);
            }
            
            // Let Selenium Manager (built into Selenium 4.x) handle ChromeDriver automatically
            System.out.println("Using Selenium Manager to auto-detect ChromeDriver...");
            
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
            System.out.println("Opening Gemini URL: " + geminiUrl);
            driver.get(geminiUrl);

            // Wait for the page to fully load
            System.out.println("Waiting for page to load...");
            Thread.sleep(5000);

            // Wait for input field to be visible using aria-label
            System.out.println("Waiting for text input field to be visible (using aria-label)...");
            WebElement inputField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[aria-label='Enter a prompt for Gemini']")
                )
            );
            
            System.out.println("Input field is visible. Clicking on it...");
            wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[aria-label='Enter a prompt for Gemini']")
            )).click();
            
            // Wait a moment after clicking
            Thread.sleep(500);
            
            System.out.println("Typing text: " + inputText);
            inputField.sendKeys(inputText);

            // Wait for the text to be fully entered
            Thread.sleep(1000);

            // Wait for send button to be visible and clickable using aria-label
            System.out.println("Waiting for send button to be visible and clickable...");
            WebElement sendButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[aria-label='Send message']")
                )
            );
            
            wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[aria-label='Send message']")
            ));
            
            System.out.println("Clicking send button...");
            sendButton.click();

            // Wait to see the result
            System.out.println("Waiting for response...");
            Thread.sleep(5000);

            System.out.println("Automation task completed successfully!");

        } catch (Exception e) {
            System.err.println("Error during automation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                System.out.println("Closing browser...");
                driver.quit();
            }
        }
    }
}
