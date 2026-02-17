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

import java.time.LocalDateTime;
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

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO),
                RideOrderResponseDTO.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void shouldReturnNoContentWhenNoAvailableDriver() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createRideDTOWithNoAvailableDriver();

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void shouldReturnNoDriverAvailableWhenAllAreToFarAway() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createRideDTOWithAllAddressesRequiringGeocodingTooFarAway();

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    public void shouldFailWhenStartAddressIsMissing() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        createRideDTO.setStartAddress(null);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldFailWhenDestinationAddressIsMissing() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        createRideDTO.setDestinationAddress(null);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldFailWhenNegativePrice() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        createRideDTO.setPrice(-100.0);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldFailWhenNegativeDistance() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        createRideDTO.setDistance(-5.0);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldSuccessfullyOrderLuxuryRide() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        createRideDTO.setVehicleType(
                vehicleTypeRepository.findVehicleTypeByName("LUXURY").getName()
        );
        createRideDTO.setPrice(250.0);

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.NO_CONTENT);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            RideOrderResponseDTO body = response.getBody();
            assertNotNull(body);
            assertNotNull(body.getRideId());
            assertTrue(body.getPrice() > 0);
        }
    }

    @Test
    public void shouldReturnNoContentWhenVanVehicleTypeNotAvailable() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createRideDTOWithNoAvailableDriver();

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void shouldSuccessfullyOrderScheduledRide() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        LocalDateTime scheduledTime = LocalDateTime.now().plusHours(2);
        createRideDTO.setScheduled(scheduledTime);

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.NO_CONTENT);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            RideOrderResponseDTO body = response.getBody();
            assertNotNull(body);
            assertNotNull(body.getRideId());
        }
    }

    @Test
    public void shouldFailToOrderScheduledRideForTomorrow() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        createRideDTO.setScheduled(tomorrow);

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldFailWhenScheduledTimeMoreThan5HoursInFuture() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        LocalDateTime tooFarInFuture = LocalDateTime.now().plusHours(6);
        createRideDTO.setScheduled(tooFarInFuture);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldSuccessfullyOrderRideWithMultipleStops() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createRideDTOWithMultipleStops(5);

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.NO_CONTENT);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            RideOrderResponseDTO body = response.getBody();
            assertNotNull(body);
            assertNotNull(body.getRideId());
            assertTrue(body.getPrice() > 0);
        }
    }

    @Test
    public void shouldSuccessfullyOrderRideWithThreeStops() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createRideDTOWithMultipleStops(3);

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldHandleRideWithManyStopsRequiringGeocoding() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createRideDTOWithMultipleStopsRequiringGeocoding(4);

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.NO_CONTENT);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            RideOrderResponseDTO body = response.getBody();
            assertNotNull(body);
            assertTrue(body.getEstimatedPickupMinutes() > 0);
        }
    }

    @Test
    public void shouldHandleEmptyStopsList() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        createRideDTO.setStops(new ArrayList<>());

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldGeocodeStartAddressWhenCoordinatesAreZero() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        GetCoordinateDTO start = new GetCoordinateDTO();
        start.setAddress("Bulevar Oslobodjenja 1, Novi Sad");
        start.setLat(0.0);
        start.setLon(0.0);
        createRideDTO.setStartAddress(start);

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldGeocodeDestinationAddressWhenCoordinatesAreZero() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createValidRideDTO();
        GetCoordinateDTO destination = new GetCoordinateDTO();
        destination.setAddress("Narodnih heroja 14, Novi Sad");
        destination.setLat(0.0);
        destination.setLon(0.0);
        createRideDTO.setDestinationAddress(destination);

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldGeocodeAllStopsWhenCoordinatesAreZero() {
        String token = getAuthToken();
        HttpHeaders headers = createHeaders(token);

        CreateRideDTO createRideDTO = createRideDTOWithMultipleStopsRequiringGeocoding(3);

        ResponseEntity<RideOrderResponseDTO> response = testRestTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(createRideDTO, headers),
                RideOrderResponseDTO.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    private String getAuthToken() {
        LogAccountDTO log = new LogAccountDTO("passenger@mail.com", "password");
        ResponseEntity<AuthTokenDTO> authResponse = testRestTemplate.postForEntity(
                "/auth/login",
                log,
                AuthTokenDTO.class
        );
        return authResponse.getBody().getAccessToken();
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private CreateRideDTO createRideDTOWithMultipleStops(int numberOfStops) {
        CreateRideDTO dto = new CreateRideDTO();

        GetCoordinateDTO start = new GetCoordinateDTO();
        start.setAddress("Bulevar Oslobodjenja 1, Novi Sad");
        start.setLat(45.2396);
        start.setLon(19.8227);
        dto.setStartAddress(start);

        List<GetCoordinateDTO> stops = new ArrayList<>();

        double[][] stopCoordinates = {
                {45.2500, 19.8300},
                {45.2520, 19.8320},
                {45.2540, 19.8340},
                {45.2530, 19.8360},
                {45.2545, 19.8380}
        };

        for (int i = 0; i < numberOfStops && i < stopCoordinates.length; i++) {
            GetCoordinateDTO stop = new GetCoordinateDTO();
            stop.setAddress("Stop " + (i + 1) + ", Novi Sad");
            stop.setLat(stopCoordinates[i][0]);
            stop.setLon(stopCoordinates[i][1]);
            stops.add(stop);
        }

        dto.setStops(stops);

        GetCoordinateDTO destination = new GetCoordinateDTO();
        destination.setAddress("Narodnih heroja 14, Novi Sad");
        destination.setLat(45.2551);
        destination.setLon(19.8451);
        dto.setDestinationAddress(destination);

        dto.setVehicleType(
                vehicleTypeRepository.findVehicleTypeByName("STANDARD").getName()
        );
        dto.setBabySeat(false);
        dto.setPetFriendly(false);
        dto.setScheduled(null);
        dto.setPrice(150.0 + (numberOfStops * 30.0));
        dto.setDistance(5.0 + (numberOfStops * 1.5));
        dto.setEstimatedDuration(15 + (numberOfStops * 5));

        return dto;
    }

    private CreateRideDTO createRideDTOWithMultipleStopsRequiringGeocoding(int numberOfStops) {
        CreateRideDTO dto = new CreateRideDTO();

        GetCoordinateDTO start = new GetCoordinateDTO();
        start.setAddress("Jevrejska 28, Novi Sad");
        start.setLat(0.0);
        start.setLon(0.0);
        dto.setStartAddress(start);

        List<GetCoordinateDTO> stops = new ArrayList<>();

        String[] addresses = {
                "Trg Slobode, Novi Sad",
                "Futoski put 100, Novi Sad",
                "Bulevar Cara Lazara 1, Novi Sad",
                "Zmaj Jovina 1, Novi Sad"
        };

        for (int i = 0; i < numberOfStops && i < addresses.length; i++) {
            GetCoordinateDTO stop = new GetCoordinateDTO();
            stop.setAddress(addresses[i]);
            stop.setLat(0.0);
            stop.setLon(0.0);
            stops.add(stop);
        }

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
        dto.setPrice(200.0 + (numberOfStops * 40.0));
        dto.setDistance(8.0 + (numberOfStops * 2.0));
        dto.setEstimatedDuration(25 + (numberOfStops * 7));

        return dto;
    }

    private CreateRideDTO createRideDTOWithAllAddressesRequiringGeocoding() {
        CreateRideDTO dto = new CreateRideDTO();

        GetCoordinateDTO start = new GetCoordinateDTO();
        start.setAddress("Jevrejska 28, Novi Sad");
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

    private CreateRideDTO createRideDTOWithAllAddressesRequiringGeocodingTooFarAway() {
        CreateRideDTO dto = new CreateRideDTO();

        GetCoordinateDTO start = new GetCoordinateDTO();
        start.setAddress("Bulevar oslobodjenja 17, Novi Sad");
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