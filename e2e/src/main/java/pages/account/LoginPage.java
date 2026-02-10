package pages.account;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.BasePage;

import java.time.Duration;

public class LoginPage extends BasePage {

    @FindBy(css = "#container img[alt='Login']")
    private WebElement loginImage;

    @FindBy(css = "input[formControlName='email']")
    private WebElement emailInput;

    @FindBy(css = "input[formControlName='password']")
    private WebElement passwordInput;

    @FindBy(css = "p.errormsg")
    private WebElement errorMessage;

    @FindBy(css = "button.form-button-one")
    private WebElement signInButton;

    @FindBy(css = "#container form")
    private WebElement loginForm;

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public boolean isLoginPageLoaded() {
        return wait.until(ExpectedConditions.visibilityOf(loginImage)).isDisplayed()
                && loginForm.isDisplayed();
    }

    public LoginPage enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(emailInput));
        emailInput.clear();
        emailInput.sendKeys(email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        return this;
    }

    public void clickSignIn() {
        wait.until(ExpectedConditions.elementToBeClickable(signInButton)).click();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement error = shortWait.until(ExpectedConditions.visibilityOf(errorMessage));
            String display = error.getCssValue("display");
            return error.isDisplayed() && !"none".equals(display);
        } catch (Exception e) {
            return false;
        }
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
    }

    public void waitForSuccessfulLogin() {
        wait.until(ExpectedConditions.invisibilityOf(loginForm));
    }
}