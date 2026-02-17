package pages.rating;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class RideHistoryPage {
    private WebDriverWait wait;
    private WebDriver driver;

    @FindBy(css = ".container")
    WebElement container;

    @FindBy(css ="div.ride-card")
    List<WebElement> cards;

    @FindBy(css =".rate-btn")
    WebElement rateButton;

    @FindBy(xpath = "//*[text() = 'Ride history']")
    WebElement rideHistoryMenu;

    @FindBy(xpath = "//*[text()='View Reviews']")
    WebElement viewReviews;

    @FindBy(xpath = "//*[text() = 'User Reviews']")
    WebElement userReviewsLabel;

    @FindBy(css=".review")
    List<WebElement> listOfReviews;

    @FindBy(xpath = "//span[text() =' Driver rating: ']/mat-icon[@style='color: gold;']")
    List<WebElement> goldStarsDriver;

    @FindBy(xpath = "//span[text() =' Vehicle rating: ']/mat-icon[@style='color: gold;']")
    List<WebElement> goldStarsVehicle;

    public RideHistoryPage(WebDriver driver){
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickOnMenu(){
        wait.until(ExpectedConditions.elementToBeClickable(rideHistoryMenu));
        rideHistoryMenu.click();
    }

    public void waitForCards(){
        wait.until(ExpectedConditions.visibilityOf(container));
    }

    public void clickOnFirstCard(){
        wait.until(ExpectedConditions.elementToBeClickable(cards.get(0)));
        cards.get(0).click();
    }

    public void clickOnSecondCard(){
        wait.until(ExpectedConditions.elementToBeClickable(cards.get(2)));
        cards.get(2).click();
    }

    public void rateButtonClick(){
        wait.until(ExpectedConditions.elementToBeClickable(rateButton));
        rateButton.click();
    }

    public void viewReviewsClick(){
        wait.until(ExpectedConditions.elementToBeClickable(viewReviews)).click();
    }

    public void scrollReviewsToBottom() {
        WebElement container = driver.findElement(By.cssSelector(".container"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", container);
    }

    public boolean check(int driver, int vehicle, String comm){
        wait.until(ExpectedConditions.visibilityOf(userReviewsLabel));
        List<WebElement> goldDriverStarts = listOfReviews.get(0).findElements(By.xpath(".//span[text() =' Driver rating: ']/mat-icon[@style='color: gold;']"));
        List<WebElement> goldVehicleStars = listOfReviews.get(0).findElements(By.xpath( ".//span[text() =' Vehicle rating: ']/mat-icon[@style='color: gold;']"));
        WebElement comment = listOfReviews.get(0).findElement(By.cssSelector(".text"));

        if(goldDriverStarts.size() == driver+1 && goldVehicleStars.size() == vehicle+1 && comm.equals(comment.getText()))
            return true;
        else
            return  false;
    }

    public boolean canNotRateRide(){
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("p"), "You can't rate this drive because more than 3 days have passed."));
    }
}
