package com.example.UberComp.S3.controller;

import com.example.UberComp.dto.account.LogAccountDTO;
import com.example.UberComp.dto.auth.AuthTokenDTO;
import com.example.UberComp.dto.ride.FinishedRideDTO;
import com.example.UberComp.dto.ride.StopRideDTO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StopRideControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testWhenAuthorizationFails(){
        StopRideDTO stop = new StopRideDTO();
        stop.setId(1L);
        stop.setPassed(2);
        stop.setLat(15.0);
        stop.setLon(15.0);
        stop.setDistance(100.0);
        stop.setAddress("Stop Address");
        stop.setFinishTime("2026-10-10T12:15:00.000");

        ResponseEntity<FinishedRideDTO> responseEntity = testRestTemplate.exchange(
                "/api/rides/stop",
                HttpMethod.PUT,
                new HttpEntity<StopRideDTO>(stop),
                FinishedRideDTO.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    public void testWhenRideIsNotFound(){
        LogAccountDTO log = new LogAccountDTO("driver@mail.com", "password");
        ResponseEntity<AuthTokenDTO> authResponse = testRestTemplate.postForEntity(
                "/auth/login",
                log,
                AuthTokenDTO.class
        );

        String token = authResponse.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        StopRideDTO stop = new StopRideDTO();
        stop.setId(-1L);

        ResponseEntity<FinishedRideDTO> responseEntity = testRestTemplate.exchange(
                "/api/rides/stop",
                HttpMethod.PUT,
                new HttpEntity<StopRideDTO>(stop, headers),
                FinishedRideDTO.class
        );

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testWhenFinishedRideReturns(){
        LogAccountDTO log = new LogAccountDTO("driver@mail.com", "password");
        ResponseEntity<AuthTokenDTO> authResponse = testRestTemplate.postForEntity(
                "/auth/login",
                log,
                AuthTokenDTO.class
        );

        String token = authResponse.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        StopRideDTO stop = new StopRideDTO();
        stop.setId(1L);
        stop.setPassed(2);
        stop.setLat(15.0);
        stop.setLon(15.0);
        stop.setDistance(100.0);
        stop.setAddress("Stop Address");
        stop.setFinishTime("2026-10-10T12:15:00.000");

        ResponseEntity<FinishedRideDTO> responseEntity = testRestTemplate.exchange(
                "/api/rides/stop",
                HttpMethod.PUT,
                new HttpEntity<StopRideDTO>(stop, headers),
                FinishedRideDTO.class
        );

        FinishedRideDTO finished = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(finished.getId(), stop.getId());
        assertEquals(finished.getFinishTime(), LocalDateTime.parse(stop.getFinishTime()));
    }
}
