package com.example.UberComp.S2.e2e;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static com.example.UberComp.S2.e2e.TestBase.driver;

public class LoginPage {
    @FindBy(css = "[formcontrolname='email']")
    WebElement username;

    @FindBy(css = "[formcontrolname='password']")
    WebElement password;

    @FindBy(xpath = "//*[text() = 'Sign In']")
    WebElement login;

    @FindBy(xpath = "//*[text()='menu']")
    WebElement navigation;

    @FindBy(xpath = "//*[text()='Ride history']")
    WebElement rideHistory;

    final String PAGE = "http://localhost:4200/login";

    public LoginPage(){
        driver.get(PAGE);
        PageFactory.initElements(driver, this);
    }

    void login(String userName, String pass){
        username.sendKeys(userName);
        password.sendKeys(pass);
        login.click();
        navigation.click();
        rideHistory.click();
    }
}
