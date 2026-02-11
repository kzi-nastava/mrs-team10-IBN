package com.example.UberComp.S1.controller;

import com.example.UberComp.dto.account.LogAccountDTO;
import com.example.UberComp.dto.auth.AuthTokenDTO;
import com.example.UberComp.dto.driver.GetCoordinateDTO;
import com.example.UberComp.dto.ride.CreateRideDTO;
import com.example.UberComp.dto.ride.RideOrderResponseDTO;
import com.example.UberComp.repository.VehicleTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderRideControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Test
    public void shouldFailAuthorization() {
        CreateRideDTO createRideDTO = createValidRideDTO();

        ResponseEntity<RideOrderResponseDTO> response =
                testRestTemplate.exchange(
                        "/api/rides",
                        HttpMethod.POST,
                        new HttpEntity<>(createRideDTO),
                        RideOrderResponseDTO.class
                );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void shouldReturnNoContentWhenNoAvailableDriver() {
        LogAccountDTO log = new LogAccountDTO("passenger@mail.com", "password");

        ResponseEntity<AuthTokenDTO> authResponse =
                testRestTemplate.postForEntity(
                        "/auth/login",
                        log,
                        AuthTokenDTO.class
                );

        String token = authResponse.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateRideDTO createRideDTO = createRideDTOWithNoAvailableDriver();

        ResponseEntity<RideOrderResponseDTO> response =
                testRestTemplate.exchange(
                        "/api/rides",
                        HttpMethod.POST,
                        new HttpEntity<>(createRideDTO, headers),
                        RideOrderResponseDTO.class
                );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void shouldSuccessfullyOrderRide() {
        LogAccountDTO log = new LogAccountDTO("passenger@mail.com", "password");

        ResponseEntity<AuthTokenDTO> authResponse =
                testRestTemplate.postForEntity(
                        "/auth/login",
                        log,
                        AuthTokenDTO.class
                );

        String token = authResponse.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateRideDTO createRideDTO = createValidRideDTO();

        ResponseEntity<RideOrderResponseDTO> response =
                testRestTemplate.exchange(
                        "/api/rides",
                        HttpMethod.POST,
                        new HttpEntity<>(createRideDTO, headers),
                        RideOrderResponseDTO.class
                );

        RideOrderResponseDTO body = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getRideId());
        assertTrue(body.getRideId() > 0);
        assertNotNull(body.getDriverName());
        assertNotNull(body.getDriverPhone());
        assertTrue(body.getPrice() > 0);
        assertNotNull(body.getEstimatedPickupMinutes());
    }

    @Test
    public void shouldSuccessfullyOrderRideWithGeocodingForAllAddresses() {
        LogAccountDTO log = new LogAccountDTO("passenger@mail.com", "password");

        ResponseEntity<AuthTokenDTO> authResponse =
                testRestTemplate.postForEntity(
                        "/auth/login",
                        log,
                        AuthTokenDTO.class
                );

        String token = authResponse.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        CreateRideDTO createRideDTO = createRideDTOWithAllAddressesRequiringGeocoding();

        ResponseEntity<RideOrderResponseDTO> response =
                testRestTemplate.exchange(
                        "/api/rides",
                        HttpMethod.POST,
                        new HttpEntity<>(createRideDTO, headers),
                        RideOrderResponseDTO.class
                );

        RideOrderResponseDTO body = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getRideId());
        assertTrue(body.getRideId() > 0);
        assertNotNull(body.getDriverName());
        assertNotNull(body.getDriverPhone());
        assertTrue(body.getPrice() > 0);
        assertNotNull(body.getEstimatedPickupMinutes());
    }

    private CreateRideDTO createRideDTOWithAllAddressesRequiringGeocoding() {
        CreateRideDTO dto = new CreateRideDTO();

        GetCoordinateDTO start = new GetCoordinateDTO();
        start.setAddress("Bulevar Oslobodjenja 19, Novi Sad");
        start.setLat(0.0);
        start.setLon(0.0);
        dto.setStartAddress(start);

        List<GetCoordinateDTO> stops = new ArrayList<>();

        GetCoordinateDTO stop1 = new GetCoordinateDTO();
        stop1.setAddress("Trg Slobode, Novi Sad");
        stop1.setLat(0.0);
        stop1.setLon(0.0);
        stops.add(stop1);

        GetCoordinateDTO stop2 = new GetCoordinateDTO();
        stop2.setAddress("Futoski put 100, Novi Sad");
        stop2.setLat(0.0);
        stop2.setLon(0.0);
        stops.add(stop2);

        dto.setStops(stops);

        GetCoordinateDTO destination = new GetCoordinateDTO();
        destination.setAddress("Narodnih heroja 24, Novi Sad");
        destination.setLat(0.0);
        destination.setLon(0.0);
        dto.setDestinationAddress(destination);

        dto.setVehicleType(
                vehicleTypeRepository.findVehicleTypeByName("STANDARD").getName()
        );
        dto.setBabySeat(false);
        dto.setPetFriendly(false);
        dto.setScheduled(null);

        dto.setPrice(200.0);
        dto.setDistance(8.0);
        dto.setEstimatedDuration(25);

        return dto;
    }

    private CreateRideDTO createValidRideDTO() {
        CreateRideDTO dto = new CreateRideDTO();

        GetCoordinateDTO start = new GetCoordinateDTO();
        start.setAddress("Bulevar Oslobodjenja 1, Novi Sad");
        start.setLat(45.2396);
        start.setLon(19.8227);
        dto.setStartAddress(start);

        GetCoordinateDTO destination = new GetCoordinateDTO();
        destination.setAddress("Narodnih heroja 14, Novi Sad");
        destination.setLat(45.2551);
        destination.setLon(19.8451);
        dto.setDestinationAddress(destination);

        dto.setStops(new ArrayList<>());
        dto.setVehicleType(
                vehicleTypeRepository.findVehicleTypeByName("STANDARD").getName()
        );
        dto.setBabySeat(false);
        dto.setPetFriendly(false);
        dto.setScheduled(null);

        dto.setPrice(150.0);
        dto.setDistance(5.0);
        dto.setEstimatedDuration(15);

        return dto;
    }

    private CreateRideDTO createRideDTOWithNoAvailableDriver() {
        CreateRideDTO dto = new CreateRideDTO();

        GetCoordinateDTO start = new GetCoordinateDTO();
        start.setAddress("Remote Location");
        start.setLat(70.0);
        start.setLon(70.0);
        dto.setStartAddress(start);

        GetCoordinateDTO destination = new GetCoordinateDTO();
        destination.setAddress("Another Remote Location");
        destination.setLat(71.0);
        destination.setLon(71.0);
        dto.setDestinationAddress(destination);

        dto.setStops(new ArrayList<>());
        dto.setVehicleType(
                vehicleTypeRepository.findVehicleTypeByName("LUXURY").getName()
        );
        dto.setBabySeat(true);
        dto.setPetFriendly(true);
        dto.setScheduled(null);

        dto.setPrice(300.0);
        dto.setDistance(10.0);
        dto.setEstimatedDuration(30);

        return dto;
    }
}