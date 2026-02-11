package tests.rating;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.HomePage;
import pages.account.LoginPage;
import pages.rating.RatePage;
import pages.rating.RideHistoryPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RatingDriverAndVehicleTest {

    private static WebDriver driver;
    private RideHistoryPage rideHistoryPage;
    private RatePage ratePage;

    private static final String BASE_URL = "http://localhost:4200";

    @BeforeAll
    public static void setUpClass() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
    }

    @BeforeEach
    public void setUp() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);
        rideHistoryPage = new RideHistoryPage(driver);
        ratePage = new RatePage(driver);
        driver.get(BASE_URL + "/login");
        loginPage.login("passenger@mail.com", "password");
        loginPage.waitForSuccessfulLogin();
        assertFalse(loginPage.isErrorMessageDisplayed());
        assertTrue(homePage.isHomePageLoaded());
        homePage.waitForMenuToBeClickable();
        homePage.navigation.click();
    }

    @Test
    void rate_driver_and_vehicle_and_leave_a_comment_successfully(){
        rideHistoryPage.clickOnMenu();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.rateButtonClick();
        ratePage.waitForRateUsLabel();
        ratePage.rateDriver(1);
        ratePage.rateVehicle(2);
        ratePage.leaveComment("comment");
        ratePage.clickOnSubmit();
        ratePage.clickOnOk();
        driver.navigate().refresh();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.viewReviewsClick();
        rideHistoryPage.scrollReviewsToBottom();
        assertTrue(rideHistoryPage.check(1,2,"comment"));
    }

    @Test
    void rate_driver_and_vehicle_and_do_not_leave_a_comment_successfully(){
        rideHistoryPage.clickOnMenu();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.rateButtonClick();
        ratePage.waitForRateUsLabel();
        ratePage.rateDriver(2);
        ratePage.rateVehicle(3);
        ratePage.clickOnSubmit();
        ratePage.clickOnOk();
        driver.navigate().refresh();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.viewReviewsClick();
        rideHistoryPage.scrollReviewsToBottom();
        assertTrue(rideHistoryPage.check(2,3,""));
    }

    @Test
    void rate_driver_and_leave_a_comment_successfully(){
        rideHistoryPage.clickOnMenu();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.rateButtonClick();
        ratePage.waitForRateUsLabel();
        ratePage.rateDriver(4);
        ratePage.leaveComment("comment 2");
        ratePage.clickOnSubmit();
        ratePage.clickOnOk();
        driver.navigate().refresh();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.viewReviewsClick();
        rideHistoryPage.scrollReviewsToBottom();
        assertTrue(rideHistoryPage.check(4,-1,"comment 2"));
    }

    @Test
    void rate_vehicle_and_leave_a_comment_successfully(){
        rideHistoryPage.clickOnMenu();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.rateButtonClick();
        ratePage.waitForRateUsLabel();
        ratePage.rateVehicle(0);
        ratePage.leaveComment("comment 3");
        ratePage.clickOnSubmit();
        ratePage.clickOnOk();
        driver.navigate().refresh();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.viewReviewsClick();
        rideHistoryPage.scrollReviewsToBottom();
        assertTrue(rideHistoryPage.check(-1,0,"comment 3"));
    }

    @Test
    void leave_a_comment_successfully(){
        rideHistoryPage.clickOnMenu();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.rateButtonClick();
        ratePage.waitForRateUsLabel();
        ratePage.leaveComment("comment 4");
        ratePage.clickOnSubmit();
        ratePage.clickOnOk();
        driver.navigate().refresh();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnFirstCard();
        rideHistoryPage.viewReviewsClick();
        rideHistoryPage.scrollReviewsToBottom();
        assertTrue(rideHistoryPage.check(-1,-1,"comment 4"));
    }

    @Test
    void rate_when_ride_happened_more_than_a_3_days_ago(){
        rideHistoryPage.clickOnMenu();
        rideHistoryPage.waitForCards();
        rideHistoryPage.clickOnSecondCard();
        rideHistoryPage.rateButtonClick();
        assertTrue(rideHistoryPage.canNotRateRide());
    }


}