package pages.favorites;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class OrderRidePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(css = ".left-panel .header h1")
    private WebElement pageTitle;

    @FindBy(css = ".header button")
    private WebElement favoritesButton;

    @FindBy(css = ".location-text")
    private WebElement locationText;

    @FindBy(css = ".car-option")
    private List<WebElement> carOptions;

    @FindBy(css = ".car-option.selected")
    private WebElement selectedCarOption;

    @FindBy(css = ".total-price")
    private WebElement totalPrice;

    @FindBy(css = ".order-btn")
    private WebElement orderButton;

    private final By successMessageLocator = By.cssSelector(".success-message");
    private final By errorMessageLocator = By.cssSelector(".error-message");
    private final By noErrorMessageLocator = By.cssSelector(".no-error-message");
    private final By messageLocator = By.cssSelector(".message");

    public OrderRidePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void clickFavoritesButton() {
        wait.until(ExpectedConditions.elementToBeClickable(favoritesButton)).click();
    }

    public boolean isFavoritesPopupDisplayed() {
        try {
            WebElement popup = driver.findElement(By.cssSelector(".popup"));
            return wait.until(ExpectedConditions.visibilityOf(popup)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getAllLocations() {
        String fullText = wait.until(ExpectedConditions.visibilityOf(locationText)).getText();
        return Arrays.stream(fullText.split("→"))
                .map(String::trim)
                .filter(s -> !s.equalsIgnoreCase("Novi Sad") && !s.equalsIgnoreCase("Serbia"))
                .toList();
    }

    public void selectCar(String carType) {
        wait.until(ExpectedConditions.visibilityOfAllElements(carOptions));
        for (WebElement car : carOptions) {
            String name = car.findElement(By.cssSelector(".car-name")).getText();
            if (name.equalsIgnoreCase(carType)) {
                wait.until(ExpectedConditions.elementToBeClickable(car)).click();
                return;
            }
        }
        throw new RuntimeException("Car type not found: " + carType);
    }

    public boolean isOrderButtonEnabled() { try { return orderButton.isEnabled(); } catch (Exception e) { return false; } }

    public String getSelectedCar() {
        wait.until(ExpectedConditions.visibilityOf(selectedCarOption));
        return selectedCarOption.findElement(By.cssSelector(".car-name")).getText();
    }

    public boolean isTotalPriceDisplayed() {
        try {
            String price = totalPrice.getText();
            return !price.contains("-") && !price.trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForPriceCalculation() {
        wait.until(driver -> !totalPrice.getText().contains("-") && !totalPrice.getText().trim().isEmpty());
    }

    public boolean isOrderRidePageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(pageTitle)).isDisplayed() &&
                    wait.until(ExpectedConditions.visibilityOf(favoritesButton)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFavoritesButtonVisible() {
        try {
            return favoritesButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForRoutePopulation() {
        wait.until(driver -> {
            String text = locationText.getText();
            return !text.isEmpty() && !text.equals("Select locations");
        });
    }

    public boolean isOnOrderRidePage() {
        return driver.getCurrentUrl().contains("/order-ride");
    }

    public void waitForOrderButtonToBeEnabled() {
        wait.until(ExpectedConditions.elementToBeClickable(orderButton));
    }

    public void clickOrderButton() {
        wait.until(ExpectedConditions.elementToBeClickable(orderButton)).click();
    }

    public void waitForResponseMessage() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            longWait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(successMessageLocator),
                    ExpectedConditions.visibilityOfElementLocated(errorMessageLocator),
                    ExpectedConditions.visibilityOfElementLocated(noErrorMessageLocator),
                    ExpectedConditions.visibilityOfElementLocated(messageLocator)
            ));
        } catch (Exception ignored) {
        }
    }

    public MessageStatus checkMessageStatus() {
        if (!driver.findElements(successMessageLocator).isEmpty()) {
            return new MessageStatus(MessageType.SUCCESS, driver.findElement(successMessageLocator).getText());
        }
        if (!driver.findElements(noErrorMessageLocator).isEmpty()) {
            return new MessageStatus(MessageType.SUCCESS, driver.findElement(noErrorMessageLocator).getText());
        }
        if (!driver.findElements(errorMessageLocator).isEmpty()) {
            return new MessageStatus(MessageType.ERROR, driver.findElement(errorMessageLocator).getText());
        }
        if (!driver.findElements(messageLocator).isEmpty()) {
            return new MessageStatus(MessageType.GENERIC, driver.findElement(messageLocator).getText());
        }
        return new MessageStatus(MessageType.NONE, "");
    }

    public static class MessageStatus {
        private final MessageType type;
        private final String text;
        public MessageStatus(MessageType type, String text) {
            this.type = type;
            this.text = text;
        }
        public MessageType getType() { return type; }
        public String getText() { return text; }
    }

    public enum MessageType {
        SUCCESS, ERROR, GENERIC, NONE
    }
}
