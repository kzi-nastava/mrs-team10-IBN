package com.example.UberComp.S2.controller;

import com.example.UberComp.dto.account.LogAccountDTO;
import com.example.UberComp.dto.auth.AuthTokenDTO;
import com.example.UberComp.dto.ride.FinishedRideDTO;
import com.example.UberComp.dto.ride.RideMomentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FinishRideControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void shouldFailAuthorization() {
        RideMomentDTO finish = new RideMomentDTO();
        finish.setIsotime("2026-10-10T12:15:00Z");

        ResponseEntity<FinishedRideDTO> response = testRestTemplate.exchange(
                "/api/rides/finish/1",
                HttpMethod.PUT,
                new HttpEntity<>(finish),
                FinishedRideDTO.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void shouldReturnNotFound() {
        LogAccountDTO log = new LogAccountDTO("driver@mail.com", "password");

        ResponseEntity<AuthTokenDTO> authResponse =
                testRestTemplate.postForEntity("/auth/login", log, AuthTokenDTO.class);

        String token = authResponse.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        RideMomentDTO finish = new RideMomentDTO();
        finish.setIsotime("2026-10-10T12:15:00Z");

        ResponseEntity<FinishedRideDTO> response = testRestTemplate.exchange(
                "/api/rides/finish/99999",
                HttpMethod.PUT,
                new HttpEntity<>(finish, headers),
                FinishedRideDTO.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnFinishedRide() {
        LogAccountDTO log = new LogAccountDTO("driver@mail.com", "password");

        ResponseEntity<AuthTokenDTO> authResponse =
                testRestTemplate.postForEntity("/auth/login", log, AuthTokenDTO.class);

        String token = authResponse.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        RideMomentDTO finish = new RideMomentDTO();
        finish.setIsotime("2026-10-10T12:15:00Z");

        ResponseEntity<FinishedRideDTO> response = testRestTemplate.exchange(
                "/api/rides/finish/1",
                HttpMethod.PUT,
                new HttpEntity<>(finish, headers),
                FinishedRideDTO.class
        );

        FinishedRideDTO finished = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, finished.getId());
        assertEquals(LocalDateTime.parse("2026-10-10T12:15:00"), finished.getFinishTime());
    }
}
