package pages.rating;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class RatePage {
    private WebDriverWait wait;

    @FindBy(css =".label1")
    WebElement rateUsLabel;

    @FindBy(name="driver")
    List<WebElement> starsDriver;

    @FindBy(name="vehicle")
    List<WebElement> starsVehicle;

    @FindBy(css = "[placeholder = 'Leave a comment...']")
    WebElement comment;

    @FindBy(xpath = "//*[text()='OK']")
    WebElement ok;

    @FindBy(css =".cdk-overlay-container")
    WebElement closeDialog;
    @FindBy(css = ".button1")
    WebElement submit;

    public RatePage(WebDriver driver){
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void waitForRateUsLabel(){
        wait.until(ExpectedConditions.visibilityOf(rateUsLabel));
    }

    public void rateDriver(int rate){
        wait.until(ExpectedConditions.elementToBeClickable(starsDriver.get(rate))).click();
    }

    public void rateVehicle(int rate){
        wait.until(ExpectedConditions.elementToBeClickable(starsVehicle.get(rate))).click();
    }

    public void leaveComment(String comm){
        wait.until(ExpectedConditions.elementToBeClickable(comment)).click();
        comment.sendKeys(comm);
    }

    public void clickOnSubmit(){
        wait.until(ExpectedConditions.elementToBeClickable(submit)).click();
    }

    public void clickOnOk(){
        wait.until(ExpectedConditions.elementToBeClickable(ok)).click();
    }

    public void closeDialog(){
        closeDialog.click();
    }


}
