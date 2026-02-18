package pages.account;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AccountPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public AccountPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//li[contains(.,'Favorites')]")
    private WebElement favoritesMenuItem;

    @FindBy(css = ".overlay")
    private WebElement favoritesOverlay;

    @FindBy(css = ".route-card")
    private List<WebElement> routeCards;

    @FindBy(css = ".remove-icon")
    private List<WebElement> removeIcons;

    @FindBy(css = ".close-btn")
    private WebElement closeButton;

    public void openFavoritesPopup() {
        wait.until(ExpectedConditions.elementToBeClickable(favoritesMenuItem));
        favoritesMenuItem.click();

        wait.until(ExpectedConditions.visibilityOf(favoritesOverlay));
    }

    public int getFavoriteRoutesCount() {
        return routeCards.size();
    }

    public void removeFirstFavoriteRoute() {
        wait.until(ExpectedConditions.visibilityOfAllElements(routeCards));

        int initialSize = routeCards.size();

        if (initialSize == 0) {
            throw new RuntimeException("No favorite routes to remove.");
        }

        removeIcons.get(0).click();

        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(
                By.cssSelector(".route-card"),
                initialSize
        ));
    }

    public void openAndRemoveFirstFavorite() {
        openFavoritesPopup();
        removeFirstFavoriteRoute();
    }
}
