package tests.favorites;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.account.LoginPage;
import pages.HomePage;
import pages.favorites.FavoritesPopupPage;
import pages.favorites.OrderRidePage;
import pages.favorites.OrderRidePage.MessageStatus;
import pages.favorites.OrderRidePage.MessageType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderRideFromFavoritesTest {

    private static WebDriver driver;
    private LoginPage loginPage;
    private HomePage homePage;
    private OrderRidePage orderRidePage;
    private FavoritesPopupPage favoritesPopupPage;

    private static final String TEST_EMAIL = "bojanapaunovic23@gmail.com";
    private static final String TEST_PASSWORD = "pass123";
    private static final String BASE_URL = "http://localhost:4200";

    @BeforeAll
    public static void setUpClass() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        driver = new ChromeDriver(options);
    }

    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        orderRidePage = new OrderRidePage(driver);
        favoritesPopupPage = new FavoritesPopupPage(driver);
    }

    private String normalizeAddress(String address) {
        return address
                .toLowerCase()
                .replaceAll(",?\\s*novi sad", "")
                .replaceAll(",?\\s*serbia", "")
                .replace(",", "")
                .trim();
    }

    @Test
    public void testCompleteOrderRideFromFavorites() {

        driver.get(BASE_URL + "/login");

        assertTrue(loginPage.isLoginPageLoaded());
        loginPage.login(TEST_EMAIL, TEST_PASSWORD);
        loginPage.waitForSuccessfulLogin();
        assertFalse(loginPage.isErrorMessageDisplayed());
        assertTrue(homePage.isHomePageLoaded());
        assertTrue(homePage.isOrderRideButtonVisible());

        homePage.clickOrderRideButton();
        homePage.waitForNavigationToOrderRide();

        assertTrue(orderRidePage.isOrderRidePageLoaded());
        assertTrue(orderRidePage.isOnOrderRidePage());
        assertTrue(orderRidePage.isFavoritesButtonVisible());

        orderRidePage.clickFavoritesButton();
        assertTrue(favoritesPopupPage.isPopupDisplayed());
        assertEquals("Favorite Routes", favoritesPopupPage.getPopupTitle());

        if (favoritesPopupPage.isEmptyStateDisplayed()) {
            favoritesPopupPage.closePopup();
            return;
        }

        List<String> favoriteRouteLocations = favoritesPopupPage.getAllRouteLocations(0);
        favoritesPopupPage.selectRoute(0);

        assertFalse(orderRidePage.isFavoritesPopupDisplayed());
        orderRidePage.waitForRoutePopulation();

        List<String> inputLocations = orderRidePage.getAllLocations();
        assertEquals(favoriteRouteLocations, inputLocations);

        String carType = "Standard";
        orderRidePage.selectCar(carType);
        assertEquals(carType, orderRidePage.getSelectedCar());

        orderRidePage.waitForPriceCalculation();
        assertTrue(orderRidePage.isTotalPriceDisplayed());

        orderRidePage.waitForOrderButtonToBeEnabled();
        assertTrue(orderRidePage.isOrderButtonEnabled());

        orderRidePage.clickOrderButton();
        orderRidePage.waitForResponseMessage();
        MessageStatus messageStatus = orderRidePage.checkMessageStatus();

        if (messageStatus.getType() == MessageType.SUCCESS) {
            assertFalse(messageStatus.getText().isEmpty());
        } else if (messageStatus.getType() == MessageType.ERROR) {
            fail("Order failed: " + messageStatus.getText());
        } else {
            fail("No response message received from backend");
        }
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) driver.quit();
    }
}
