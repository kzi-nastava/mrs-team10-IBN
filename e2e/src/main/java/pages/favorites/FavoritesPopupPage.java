package pages.favorites;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
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
        List<WebElement> elements = card.findElements(By.cssSelector(".route-text"));
        return elements.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(s -> !s.equalsIgnoreCase("Novi Sad") && !s.equalsIgnoreCase("Serbia"))
                .collect(Collectors.toList());
    }
}
