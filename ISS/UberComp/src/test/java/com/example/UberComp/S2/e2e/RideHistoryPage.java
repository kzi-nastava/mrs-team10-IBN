package com.example.UberComp.S2.e2e;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

import static com.example.UberComp.S2.e2e.TestBase.driver;

public class RideHistoryPage {
    @FindBy(css =".ride-card")
    List<WebElement> cards;

    public RideHistoryPage(){
        PageFactory.initElements(driver, this);
    }

    void clickOncard(){

    }
}
