package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {
    private WebDriverWait wait;

    @FindBy(css = ".order-btn")
    private WebElement orderRideButton;

    @FindBy(css = "app-map")
    private WebElement mapComponent;

    @FindBy(xpath = "//*[text()='menu']")
    public WebElement navigation;


    public HomePage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isHomePageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(orderRideButton)).isDisplayed()
                    && mapComponent.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickOrderRideButton() {
        wait.until(ExpectedConditions.elementToBeClickable(orderRideButton)).click();
    }

    public void waitForNavigationToOrderRide() {
        wait.until(ExpectedConditions.urlContains("/order-ride"));
    }

    public boolean isOrderRideButtonVisible() {
        try {
            return orderRideButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForMenuToBeClickable(){wait.until(ExpectedConditions.elementToBeClickable(navigation)); }

}