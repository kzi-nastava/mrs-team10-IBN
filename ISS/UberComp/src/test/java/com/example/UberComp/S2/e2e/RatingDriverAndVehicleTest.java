package com.example.UberComp.S2.e2e;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testng.annotations.Test;

@SpringBootTest
@ActiveProfiles("test")
public class RatingDriverAndVehicleTest extends TestBase{

    @Test
    void rating_driver_vehicle_test(){
        LoginPage loginPage = new LoginPage();
        loginPage.login("putnik@mail.com", "pass123");
    }
}
