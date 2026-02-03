package com.example.UberComp.controller;

import com.example.UberComp.dto.PageDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.dto.ride.*;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.service.DriverService;
import com.example.UberComp.service.RideService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/rides")
public class RideController {
    @Autowired
    private final DriverService driverService;
    @Autowired
    private RideService rideService;

    @PreAuthorize("hasAnyAuthority('passenger','driver', 'administrator')")
    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageDTO<GetRideDTO>> getRideHistory(
            Authentication auth,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String startFrom,
            @RequestParam(required = false) String startTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Account account = (Account) auth.getPrincipal();
        LocalDateTime startFromFormatted = null;
        if(startFrom != null)
            startFromFormatted = LocalDateTime.ofInstant(Instant.parse(startFrom), ZoneId.systemDefault());
        LocalDateTime startToFormatted = null;
        if(startTo != null)
            startToFormatted = LocalDateTime.ofInstant(Instant.parse(startTo), ZoneId.systemDefault());

        Page<GetRideDTO> rides = null;
        switch(account.getAccountType()){
            case PASSENGER ->
                    rides = rideService.getRidesPassenger(
                            account.getUser().getId(),
                            sort,
                            startFromFormatted,
                            startToFormatted,
                            page,
                            size);
            case DRIVER ->
                    rides = rideService.getRidesDriver(
                            account.getUser().getId(),
                            sort,
                            startFromFormatted,
                            startToFormatted,
                            page,
                            size);
            case ADMINISTRATOR ->
                    rides = rideService.getRidesAdmin(
                            sort,
                            startFromFormatted,
                            startToFormatted,
                            page,
                            size);
        }
        return ResponseEntity.ok(new PageDTO<>(rides));
    }

    @PreAuthorize("hasAuthority('driver')")
    @GetMapping(value = "/incoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IncomingRideDTO> getIncomingRide(Authentication auth){
        Account account = (Account) auth.getPrincipal();
        Driver driver = (Driver) account.getUser();
        IncomingRideDTO ride = rideService.getIncomingRide(driver);
        if(ride == null) return new ResponseEntity<IncomingRideDTO>(ride, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ride, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver', 'administrator')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideDetailsDTO> getRide(@PathVariable Long id)
    {
        GetRideDetailsDTO ride = rideService.getRide(id);
        return new ResponseEntity<>(ride, HttpStatus.OK);
    }

    @GetMapping(value = "/activeRides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetVehiclePositionDTO>> activeRides(){
        driverService.updateAllDriverLocation();
        Collection<GetVehiclePositionDTO> activeRides = rideService.getActiveRides();
        return ResponseEntity.ok(activeRides);
    }

    @PreAuthorize("hasAuthority('passenger')")
    @GetMapping(value = "/trackingRidePassenger", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetVehiclePositionDTO> trackingRidePassenger(Authentication auth){
        Account acc = (Account) auth.getPrincipal();
        GetVehiclePositionDTO trackingRide = rideService.getTrackingRide(acc.getUser().getId());
        return ResponseEntity.ok(trackingRide);
    }

    @PreAuthorize("hasAuthority('driver')")
    @GetMapping(value="/scheduledRides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<GetRideDTO>> scheduledRides(Authentication auth, Pageable pageable){
        Account acc = (Account) auth.getPrincipal();
        Page<GetRideDTO> scheduledRides = rideService.getScheduledRidesForDriver(acc.getUser().getId(), pageable);
        return ResponseEntity.ok(scheduledRides);
    }

    @PreAuthorize("hasAuthority('driver')")
    @PutMapping(value = "/start/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StartedRideDTO> startRide(@RequestBody RideMomentDTO start, @PathVariable Long id) {
        StartedRideDTO started = rideService.startRide(id, start);
        return new ResponseEntity<>(started, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('driver')")
    @PutMapping(value = "/finish/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinishedRideDTO> finishRide(@RequestBody RideMomentDTO finish, @PathVariable("id") Long id) {
         FinishedRideDTO finished = rideService.endRide(id, finish);
         return new ResponseEntity<>(finished, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('driver')")
    @PutMapping(value = "/stop", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinishedRideDTO> stopRide(@RequestBody StopRideDTO ride){
        FinishedRideDTO finished = rideService.stopRide(ride, false);
        return new ResponseEntity<>(finished, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedStatusRideDTO> updateRideStatus(@RequestBody UpdateStatusRideDTO rideStatus, @PathVariable Long id)
            throws Exception {
        UpdatedStatusRideDTO updatedRide = rideService.updateRideStatus(rideStatus);
        updatedRide.setId(id);
        updatedRide.setRideStatus(rideStatus.getRideStatus());
        return new ResponseEntity<UpdatedStatusRideDTO>(updatedRide, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('passenger')")
    @PostMapping(value = "/calculate-price", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceDTO> calculatePrice(@RequestBody CreateRideDTO dto) {
        PriceDTO priceDTO = rideService.calculatePrice(dto);
        return ResponseEntity.ok(priceDTO);
    }

    @PreAuthorize("hasAuthority('passenger')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideOrderResponseDTO> orderRide(@RequestBody CreateRideDTO dto, Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        if (account.getAccountStatus().equals(AccountStatus.BLOCKED))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new RideOrderResponseDTO(0L,0.0, account.getBlockingReason(), null, null, null, null, null, 0L, null));

        GetCoordinateDTO start = dto.getStartAddress();
        if (start.getLon() == 0 || start.getLat() == 0) {
            dto.setStartAddress(new GetCoordinateDTO(driverService.geocodeAddressWithCache(start.getAddress())));
        }

        for (int i = 0; i < dto.getStops().size(); ++i) {
            GetCoordinateDTO stop = dto.getStops().get(i);
            if (stop.getLon() == 0 || stop.getLat() == 0) {
                dto.getStops().set(i, new GetCoordinateDTO(driverService.geocodeAddressWithCache(stop.getAddress())));
            }
        }

        GetCoordinateDTO end = dto.getDestinationAddress();
        if (end.getLon() == 0 || end.getLat() == 0) {
            dto.setDestinationAddress(new GetCoordinateDTO(driverService.geocodeAddressWithCache(end.getAddress())));
        }

        AvailableDriverDTO availableDriver = driverService.getAvailableDriver(dto);

        if (availableDriver == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        Ride ride = rideService.createRide(
                dto,
                account.getUser().getId(),
                availableDriver.getDriver().getCreateUserDTO().getId(),
                availableDriver.getEstimatedPickupMinutes(),
                availableDriver.getDriverShouldLeaveAt()
        );

        RideOrderResponseDTO response = rideService.buildRideOrderResponse(ride.getId(), availableDriver);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('passenger')")
    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteRouteDTO>> getFavorites(Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        List<FavoriteRouteDTO> favorites = rideService.getFavoriteRoutes(account);
        return ResponseEntity.ok(favorites);
    }

    @PreAuthorize("hasAuthority('passenger')")
    @PutMapping("/history/{id}/add-to-favorites")
    public ResponseEntity<FavoriteRouteDTO> addToFavorites(
            @PathVariable Long id,
            Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        FavoriteRouteDTO favoriteRoute = rideService.addRouteToFavorites(id, account);
        return ResponseEntity.ok(favoriteRoute);
    }

    @PreAuthorize("hasAuthority('passenger')")
    @DeleteMapping("/favorites/by-favorite-id/{favoriteId}")
    public ResponseEntity<Void> removeByFavoriteId(@PathVariable Long favoriteId, Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        rideService.removeRouteFromFavorites(favoriteId, account);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('passenger')")
    @DeleteMapping("/history/by-route-id/{routeId}")
    public ResponseEntity<Void> removeByRouteId(@PathVariable Long routeId, Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        rideService.removeRouteFromFavoritesByRoute(routeId, account);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('driver')")
    @PutMapping("/{id}/start")
    public ResponseEntity<Void> startRide(@PathVariable Long id, @RequestBody RideMomentDTO start) {
        rideService.startRide(id, start);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver')")
    @GetMapping("/ongoing")
    public ResponseEntity<Boolean> getOngoing(Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        boolean hasRide = rideService.hasOngoingRide(account.getUser().getId());
        return ResponseEntity.ok(hasRide);
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver')")
    @PutMapping(value = "/panic", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinishedRideDTO> panic(@RequestBody StopRideDTO panic) {
        FinishedRideDTO panicked = rideService.stopRide(panic, true);
        return new ResponseEntity<>(panicked, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver')")
    @PutMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> cancelRide(@RequestBody CancelRideDTO cancelRideDTO){
        if(rideService.cancelRide(cancelRideDTO)){
            return ResponseEntity.ok(null);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('administrator')")
    @GetMapping(value="/adminView", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<GetCurrentRideDTO>> currentRides(
            Authentication auth,
            Pageable pageable,
            @RequestParam(required = false) String search){
        Page<GetCurrentRideDTO> currentRides = rideService.getCurrentRides(search, pageable);
        return ResponseEntity.ok(currentRides);
    }

}