package tests.favorites;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.account.AccountPage;
import pages.account.LoginPage;
import pages.ridehistory.RideHistoryPage;

import java.time.Duration;

public class AddAndRemoveFavoritesTest {
    protected static WebDriver driver;
    protected static RideHistoryPage rideHistoryPage;
    protected static AccountPage accountPage;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);

        driver.get("http://localhost:4200/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(new By.ByCssSelector("button.form-button-one")));
        LoginPage login = new LoginPage(driver);
        login.enterEmail("passenger@mail.com");
        login.enterPassword("password");
        login.clickSignIn();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("notification")));

        driver.get("http://localhost:4200/ride-history");
        rideHistoryPage = new RideHistoryPage(driver);
    }

    @Test
    public void testAddAndRemoveFavoritesFlow() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        rideHistoryPage.openRideAndAddToFavorites(1);

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".favorite-btn"),
                "In Favorites"
        ));

        driver.get("http://localhost:4200/account");
        accountPage = new AccountPage(driver);

        accountPage.openAndRemoveFirstFavorite();
    }
}
