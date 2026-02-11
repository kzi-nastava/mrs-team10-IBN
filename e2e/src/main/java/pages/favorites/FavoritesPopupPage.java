package pages.favorites;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FavoritesPopupPage {
    private WebDriverWait wait;

    @FindBy(css = ".popup")
    private WebElement popup;

    @FindBy(css = ".popup-header h2")
    private WebElement popupTitle;

    @FindBy(css = ".close-btn")
    private WebElement closeButton;

    @FindBy(css = ".routes-list")
    private WebElement routesList;

    @FindBy(css = ".route-card")
    private List<WebElement> routeCards;

    @FindBy(css = ".empty-state")
    private WebElement emptyState;

    public FavoritesPopupPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isPopupDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(popup)).isDisplayed();
    }

    public String getPopupTitle() {
        return wait.until(ExpectedConditions.visibilityOf(popupTitle)).getText();
    }

    public void closePopup() {
        wait.until(ExpectedConditions.elementToBeClickable(closeButton)).click();
        wait.until(ExpectedConditions.invisibilityOf(popup));
    }

    public boolean isEmptyStateDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(emptyState)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement getRouteCard(int index) {
        wait.until(ExpectedConditions.visibilityOfAllElements(routeCards));
        return routeCards.get(index);
    }

    public void selectRoute(int index) {
        WebElement card = getRouteCard(index);
        wait.until(ExpectedConditions.elementToBeClickable(card)).click();
        wait.until(ExpectedConditions.invisibilityOf(popup));
    }

    public List<String> getAllRouteLocations(int index) {
        WebElement card = getRouteCard(index);

        List<WebElement> routeTextElements = card.findElements(By.cssSelector(".route-path .route-text"));

        List<String> locations = new ArrayList<>();
        for (WebElement element : routeTextElements) {
            String text = element.getText().trim();
            if (!text.isEmpty() &&
                    !text.equalsIgnoreCase("Novi Sad") &&
                    !text.equalsIgnoreCase("Serbia")) {
                locations.add(text);
            }
        }

        return locations;
    }

    public String getRouteFullText(int index) {
        WebElement card = getRouteCard(index);
        WebElement routePath = card.findElement(By.cssSelector(".route-path"));
        return routePath.getText().trim();
    }
}