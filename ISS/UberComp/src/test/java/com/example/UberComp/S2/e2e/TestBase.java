package com.example.UberComp.S2.e2e;

import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.concurrent.TimeUnit;
import io.github.bonigarcia.wdm.WebDriverManager;


public class TestBase {
    public static ChromeDriver driver;

    @BeforeSuite
    public void initializeWebDriver() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterSuite
    public void quitDriver() {
        driver.quit();
    }
}
