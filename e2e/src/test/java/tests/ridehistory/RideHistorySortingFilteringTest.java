package tests.ridehistory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.account.LoginPage;
import pages.ridehistory.RideHistoryPage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RideHistorySortingFilteringTest {

    protected static WebDriver driver;
    protected static RideHistoryPage rideHistoryPage;

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
        login.enterEmail("admin@mail.com");
        login.enterPassword("password");
        login.clickSignIn();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("notification")));

        driver.get("http://localhost:4200/ride-history");
        rideHistoryPage = new RideHistoryPage(driver);
    }

    @Test
    public void testSortByPriceAscending() {
        rideHistoryPage.sortBy("price-asc");
        rideHistoryPage.scrollToBottom();

        List<Double> prices = rideHistoryPage.extractPrices();

        for (int i = 0; i < prices.size() - 1; i++) {
            assertTrue(prices.get(i) <= prices.get(i + 1),
                    String.format("Price sorting failed: %.2f > %.2f at index %d",
                            prices.get(i), prices.get(i + 1), i));
        }
    }

    @Test
    public void testSortByPriceDescending() {
        rideHistoryPage.sortBy("price-desc");
        rideHistoryPage.scrollToBottom();

        List<Double> prices = rideHistoryPage.extractPrices();

        for (int i = 0; i < prices.size() - 1; i++) {
            assertTrue(prices.get(i) >= prices.get(i + 1),
                    String.format("Price sorting failed: %.2f < %.2f at index %d",
                            prices.get(i), prices.get(i + 1), i));
        }
    }

    @Test
    public void testSortByStartTimeAscending() {
        rideHistoryPage.sortBy("start-asc");
        rideHistoryPage.scrollToBottom();

        List<LocalDateTime> startTimes = rideHistoryPage.extractStartTimes();

        for (int i = 0; i < startTimes.size() - 1; i++) {
            assertTrue(
                    startTimes.get(i).isBefore(startTimes.get(i + 1)) ||
                            startTimes.get(i).isEqual(startTimes.get(i + 1)),
                    String.format("Start time sorting failed at index %d", i)
            );
        }
    }

    @Test
    public void testSortByStartTimeDescending() {
        rideHistoryPage.sortBy("start-desc");
        rideHistoryPage.scrollToBottom();

        List<LocalDateTime> startTimes = rideHistoryPage.extractStartTimes();

        for (int i = 0; i < startTimes.size() - 1; i++) {
            assertTrue(
                    startTimes.get(i).isAfter(startTimes.get(i + 1)) ||
                            startTimes.get(i).isEqual(startTimes.get(i + 1)),
                    String.format("Start time sorting failed at index %d", i)
            );
        }
    }

    @Test
    public void testSortByEndTimeAscending() {
        rideHistoryPage.sortBy("end-asc");
        rideHistoryPage.scrollToBottom();

        List<LocalDateTime> endTimes = rideHistoryPage.extractEndTimes();

        for (int i = 0; i < endTimes.size() - 1; i++) {
            assertTrue(
                    endTimes.get(i).isBefore(endTimes.get(i + 1)) ||
                            endTimes.get(i).isEqual(endTimes.get(i + 1)),
                    String.format("End time sorting failed at index %d", i)
            );
        }
    }

    @Test
    public void testSortByEndTimeDescending()  {
        rideHistoryPage.sortBy("end-desc");
        rideHistoryPage.scrollToBottom();

        List<LocalDateTime> endTimes = rideHistoryPage.extractEndTimes();

        for (int i = 0; i < endTimes.size() - 1; i++) {
            assertTrue(
                    endTimes.get(i).isAfter(endTimes.get(i + 1)) ||
                            endTimes.get(i).isEqual(endTimes.get(i + 1)),
                    String.format("End time sorting failed at index %d", i)
            );
        }
    }

    @Test
    public void testFilterByFromDate() {
        rideHistoryPage.clearDateFields();
        LocalDate filterDate = getTestDate();
        rideHistoryPage.setFromDate(filterDate);
        rideHistoryPage.scrollToBottom();

        List<LocalDateTime> startTimes = rideHistoryPage.extractStartTimes();

        for (LocalDateTime startTime : startTimes) {
            assertTrue(
                    !startTime.toLocalDate().isBefore(filterDate),
                    String.format("Ride with start time %s should not appear (filter date: %s)",
                            startTime, filterDate)
            );
        }
    }

    @Test
    public void testFilterByToDate() {
        rideHistoryPage.clearDateFields();
        LocalDate filterDate = getTestDate().plusDays(5);
        rideHistoryPage.setToDate(filterDate);
        rideHistoryPage.scrollToBottom();

        List<LocalDateTime> startTimes = rideHistoryPage.extractStartTimes();

        for (LocalDateTime startTime : startTimes) {
            assertTrue(
                    !startTime.toLocalDate().isAfter(filterDate),
                    String.format("Ride with start time %s should not appear (filter date: %s)",
                            startTime, filterDate)
            );
        }
    }

    @Test
    public void testFilterByDateRange() {
        rideHistoryPage.clearDateFields();
        LocalDate fromDate = getTestDate().minusDays(5);
        LocalDate toDate = getTestDate().plusDays(5);
        rideHistoryPage.setDateRange(fromDate, toDate);
        rideHistoryPage.scrollToBottom();

        List<LocalDateTime> startTimes = rideHistoryPage.extractStartTimes();

        for (LocalDateTime startTime : startTimes) {
            LocalDate startDate = startTime.toLocalDate();
            assertTrue(
                    !startDate.isBefore(fromDate) && !startDate.isAfter(toDate),
                    String.format("Ride with start time %s is outside date range %s to %s",
                            startTime, fromDate, toDate)
            );
        }
    }

    @Test
    public void testFilterAndSortCombined() {
        rideHistoryPage.clearDateFields();
        LocalDate fromDate = getTestDate();
        rideHistoryPage.setFromDate(fromDate);
        rideHistoryPage.sortBy("price-desc");
        rideHistoryPage.scrollToBottom();

        List<LocalDateTime> startTimes = rideHistoryPage.extractStartTimes();
        List<Double> prices = rideHistoryPage.extractPrices();

        for (LocalDateTime startTime : startTimes) {
            assertTrue(
                    !startTime.toLocalDate().isBefore(fromDate),
                    "Date filter not applied correctly"
            );
        }

        for (int i = 0; i < prices.size() - 1; i++) {
            assertTrue(prices.get(i) >= prices.get(i + 1),
                    "Price sorting not maintained with filter");
        }
    }

    private LocalDate getTestDate(){
        return LocalDate.of(2026, Month.FEBRUARY, 1);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }
}