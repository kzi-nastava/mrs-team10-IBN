package pages.ridehistory;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RideHistoryPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    @FindBy(css = ".date-container mat-form-field:nth-of-type(1) input")
    private WebElement fromDateInput;

    @FindBy(css = ".date-container mat-form-field:nth-of-type(2) input")
    private WebElement toDateInput;

    @FindBy(css = "select")
    private WebElement sortDropdown;

    @FindBy(css = ".container")
    private WebElement scrollContainer;

    @FindBy(css = ".ride-card")
    private List<WebElement> rideCards;

    public RideHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public List<WebElement> getRideCards() {
        return driver.findElements(By.className("ride-card"));
    }

    public int getRideCount() {
        return getRideCards().size();
    }

    public void scrollToBottom() {
        int rideCount = getRideCount();
        while(true){
            js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", scrollContainer);
            if(rideCount == getRideCount()){
                break;
            }
            rideCount = getRideCount();
        }
    }

    public void sortBy(String value) {
        Select select = new Select(sortDropdown);
        select.selectByValue(value);
    }

    public void clearDateFields(){
        fromDateInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        toDateInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
    }

    private void setDateField(WebElement dateInput, LocalDate date) {
        wait.until(ExpectedConditions.elementToBeClickable(dateInput));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = date.format(formatter);

        dateInput.sendKeys(formattedDate);
        dateInput.sendKeys(Keys.ENTER);
    }

    public void setFromDate(LocalDate date) {
        setDateField(fromDateInput, date);
    }

    public void setToDate(LocalDate date) {
        setDateField(toDateInput, date);
    }

    public void setDateRange(LocalDate fromDate, LocalDate toDate) {
        setFromDate(fromDate);
        setToDate(toDate);
    }

    public List<Double> extractPrices() {
        List<Double> prices = new ArrayList<>();
        List<WebElement> cards = getRideCards();

        for (WebElement card : cards) {
            WebElement priceElement = card.findElement(By.className("price"));
            String priceText = priceElement.getText()
                    .replace(" RSD", "")
                    .replace(",", "")
                    .trim();
            prices.add(Double.parseDouble(priceText));
        }

        return prices;
    }

    public List<LocalDateTime> extractStartTimes() {
        List<LocalDateTime> startTimes = new ArrayList<>();
        List<WebElement> cards = getRideCards();

        for (WebElement card : cards) {
            List<WebElement> timeBoxes = card.findElements(By.className("time-box"));
            String dateText = timeBoxes.get(0).getText();
            String timeText = timeBoxes.get(1).getText();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(dateText + " " + timeText, formatter);
            startTimes.add(dateTime);
        }

        return startTimes;
    }

    public List<LocalDateTime> extractEndTimes() {
        List<LocalDateTime> endTimes = new ArrayList<>();
        List<WebElement> cards = getRideCards();

        for (WebElement card : cards) {
            List<WebElement> timeBoxes = card.findElements(By.className("time-box"));
            String dateText = timeBoxes.get(0).getText();
            String timeText = timeBoxes.get(2).getText();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(dateText + " " + timeText, formatter);
            endTimes.add(dateTime);
        }

        return endTimes;
    }
}
