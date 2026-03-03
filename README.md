# Gemini Automation Project

A Spring Boot application that uses Selenium WebDriver to automate interactions with Google Gemini.

## Features

- Automated navigation to Gemini web interface
- Text input automation
- Button click automation
- Configurable XPath selectors
- Configurable timeout and browser options

## Prerequisites

Before you begin, ensure you have the following installed on your system:

- **Java 17 or higher** - [Download Java](https://adoptium.net/)
- **Maven 3.6 or higher** - [Download Maven](https://maven.apache.org/download.cgi)
- **Brave Browser** - [Download Brave](https://brave.com/download/)

### Verify Installation

Check if Java is installed:
```bash
java -version
```

Check if Maven is installed:
```bash
mvn -version
```

## Project Structure

```
automation/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── automation/
        │           ├── Application.java
        │           └── service/
        │               └── GeminiAutomationService.java
        └── resources/
            └── application.properties
```

## Setup Instructions

### 1. Clone or Navigate to the Project

```bash
cd /home/mayank/MyProjects/automation
```

### 2. Install Dependencies

Maven will automatically download all required dependencies including:
- Spring Boot
- Selenium WebDriver
- WebDriver Manager (automatically manages ChromeDriver)

Run:
```bash
mvn clean install
```

### 3. Configuration

The application can be configured via `src/main/resources/application.properties`:

```properties
# Gemini URL
gemini.url=https://gemini.google.com/app

# Text to input (input field is found by aria-label="Enter a prompt for Gemini")
gemini.input.text=who are you

# Send button is found by aria-label="Send message"

# Selenium Configuration
selenium.timeout=30
selenium.headless=false
```

#### Configuration Options:

- `gemini.url`: The URL of the Gemini application
- `gemini.input.text`: The text to type in the input field
- `selenium.timeout`: Wait timeout in seconds (default: 30)
- `selenium.headless`: Run browser in headless mode (default: false)

**Note**: Input field and send button are found using aria-label attributes, which are more stable than XPath.

## Running the Application

### Method 1: Using Maven

```bash
mvn spring-boot:run
```

### Method 2: Using Java JAR

First, build the JAR file:
```bash
mvn clean package
```

Then run it:
```bash
java -jar target/gemini-automation-1.0.0.jar
```

## What the Application Does

1. **Opens Brave Browser**: Launches Brave browser (Chromium-based)
2. **Navigates to Gemini**: Goes to https://gemini.google.com/app
3. **Waits for Text Field**: Waits for the input field to be visible (found by `aria-label="Enter a prompt for Gemini"`)
4. **Types Text**: Enters "who are you" into the text field
5. **Clicks Send Button**: Clicks the send button (found by `aria-label="Send message"`)
6. **Waits for Response**: Waits 5 seconds to see the response
7. **Closes Browser**: Automatically closes the browser

## Customization

### Change the Input Text

Edit `application.properties`:
```properties
gemini.input.text=Your custom question here
```

Or pass it as a command-line argument:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--gemini.input.text='What is AI?'"
```

### Run in Headless Mode

Edit `application.properties`:
```properties
selenium.headless=true
```

### Adjust Timeout

Edit `application.properties`:
```properties
selenium.timeout=60
```

## Troubleshooting

### Issue: ChromeDriver not found
**Solution**: WebDriverManager automatically downloads ChromeDriver (used for Brave). Ensure you have internet connection on first run.

### Issue: Element not found
**Solution**: 
- Gemini's UI might have changed. The input field is found by `aria-label="Enter a prompt for Gemini"` and send button by `aria-label="Send message"`
- Increase the timeout value in application.properties
- Check if you need to log in to Gemini first

### Issue: Timeout exceptions
**Solution**:
- Increase `selenium.timeout` value
- Check your internet connection
- Ensure Gemini website is accessible

### Issue: Brave browser not detected
**Solution**: 
- Ensure Brave is installed via Flatpak: `flatpak list | grep brave`
- Install if missing: `flatpak install flathub com.brave.Browser`
- Or install from: https://brave.com/download/

## Development

### Adding New Automation Steps

Edit `GeminiAutomationService.java` and add your automation logic in the `runAutomation()` method:

```java
// Example: Wait for response
WebElement response = wait.until(
    ExpectedConditions.presenceOfElementLocated(By.xpath("//your-xpath"))
);
System.out.println("Response: " + response.getText());
```

### Running in IDE

1. Import the project as a Maven project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Run the `Application.java` main class

## Technologies Used

- **Spring Boot 3.2.3**: Application framework
- **Selenium WebDriver 4.18.1**: Browser automation
- **WebDriverManager 5.7.0**: Automatic ChromeDriver management
- **Brave Browser**: Chromium-based browser (Flatpak version supported)
- **Maven**: Build and dependency management
- **Java 17**: Programming language

## Notes

- The application requires an active internet connection
- **Input field** is found using `aria-label="Enter a prompt for Gemini"`
- **Send button** is found using `aria-label="Send message"`
- Both selectors use aria-labels which are more reliable and accessible than XPath
- Brave browser (Flatpak) is automatically detected
- You may need to handle Google login if Gemini requires authentication

## License

This project is for educational and automation purposes.

## Support

For issues or questions, please check:
- Selenium documentation: https://www.selenium.dev/documentation/
- Spring Boot documentation: https://spring.io/projects/spring-boot
- WebDriverManager documentation: https://bonigarcia.dev/webdrivermanager/
