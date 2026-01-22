package com.example.UberComp.controller;

import com.example.UberComp.dto.PageDTO;
import com.example.UberComp.dto.driver.AvailableDriverDTO;
import com.example.UberComp.dto.driver.DriverDTO;
import com.example.UberComp.dto.driver.GetVehiclePositionDTO;
import com.example.UberComp.dto.driver.RouteDTO;
import com.example.UberComp.dto.ride.*;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.Account;
import com.example.UberComp.model.Driver;
import com.example.UberComp.model.PanicSignal;
import com.example.UberComp.model.Ride;
import com.example.UberComp.service.DriverService;
import com.example.UberComp.service.RideService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/rides")
public class RideController {

    private final DriverService driverService;
    private RideService rideService;

    //    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping(value = "/driver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<GetRideDTO>> getRidesDriver(Authentication auth, Pageable pageable) {

        Account account = (Account) auth.getPrincipal();
        Page<GetRideDTO> rides = rideService.getRidesDriver(account.getUser().getId(), pageable);

        return ResponseEntity.ok(rides);
    }


    @GetMapping(value= "/passenger", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageDTO<GetRideDTO>> getRidesPassenger(Authentication auth, Pageable pageable) {
        Account account = (Account) auth.getPrincipal();
        Page<GetRideDTO> rides = rideService.getRidesPassenger(account.getUser().getId(), pageable);
        return ResponseEntity.ok(new PageDTO<>(rides));
    }

    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetRideDetailsDTO>> getRideHistory(){
        ArrayList<GetRideDetailsDTO> allRides = new ArrayList<>();

        GetRideDetailsDTO finishedRide = new GetRideDetailsDTO();
        finishedRide.setId(1L);
        finishedRide.setStatus(RideStatus.Finished);

        GetRideDetailsDTO panickedRide = new GetRideDetailsDTO();
        panickedRide.setId(2L);
        panickedRide.setStatus(RideStatus.Panic);

        allRides.add(finishedRide);
        allRides.add(panickedRide);

        return new ResponseEntity<Collection<GetRideDetailsDTO>>(allRides, HttpStatus.OK);
    }

    @GetMapping(value = "/incoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IncomingRideDTO> getIncomingRide(){
        IncomingRideDTO ride = rideService.getIncomingRide();
        if(ride == null) return new ResponseEntity<IncomingRideDTO>(ride, HttpStatus.NO_CONTENT);
        return new ResponseEntity<IncomingRideDTO>(ride, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideDetailsDTO> getRide(@PathVariable("id") Long id)
    {
        GetRideDetailsDTO ride = rideService.getRide(id);
        return new ResponseEntity<GetRideDetailsDTO>(ride, HttpStatus.OK);
    }

    @GetMapping(value = "/activeRides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetVehiclePositionDTO>> activeRides(){
        Collection<GetVehiclePositionDTO> activeRides = rideService.getActiveRides();
        return ResponseEntity.ok(activeRides);
    }

    @GetMapping(value = "/trackingRidePassenger", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetVehiclePositionDTO> trackingRidePassenger(Authentication auth){
        Account acc = (Account) auth.getPrincipal();
        GetVehiclePositionDTO trackingRide = rideService.getTrackingRide(acc.getUser().getId());
        return ResponseEntity.ok(trackingRide);
    }

    @GetMapping(value="/scheduledRides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<GetRideDTO>> scheduledRides(Authentication auth, Pageable pageable){
        Account acc = (Account) auth.getPrincipal();
        Page<GetRideDTO> scheduledRides = rideService.getScheduledRidesForDriver(acc.getUser().getId(), pageable);
        return ResponseEntity.ok(scheduledRides);
    }

    @PutMapping(value = "/start/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StartedRideDTO> startRide(@RequestBody RideMomentDTO start, @PathVariable("id") Long id) {
        StartedRideDTO started = rideService.startRide(id, start);
        return new ResponseEntity<>(started, HttpStatus.OK);
    }

    @PutMapping(value = "/finish/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinishedRideDTO> finishRide(@RequestBody RideMomentDTO finish, @PathVariable("id") Long id) {
         FinishedRideDTO finished = rideService.endRide(id, finish);
         return new ResponseEntity<>(finished, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteRide(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

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

    @PostMapping(value = "/calculate-price", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceDTO> calculatePrice(@RequestBody CreateRideDTO dto) {
        PriceDTO priceDTO = rideService.calculatePrice(dto);
        return ResponseEntity.ok(priceDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideOrderResponseDTO> orderRide(@RequestBody CreateRideDTO dto, Authentication auth) {
        Account account = (Account) auth.getPrincipal();

        AvailableDriverDTO availableDriver = driverService.getAvailableDriver(dto);

        if (availableDriver == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        Ride ride = rideService.createRide(
                dto,
                account.getUser().getId(),
                availableDriver.getDriver().getCreateUserDTO().getId(),
                availableDriver.getEstimatedPickupMinutes()
        );

        RideOrderResponseDTO response = rideService.buildRideOrderResponse(ride.getId(), availableDriver);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteRouteDTO>> getFavorites(Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        List<FavoriteRouteDTO> favs = rideService.getFavoriteRoutes(account);
        return ResponseEntity.ok(favs);
    }

    @PutMapping("/history/{id}/add-to-favorites")
    public ResponseEntity<FavoriteRouteDTO> addToFavorites(
            @PathVariable Long id,
            Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        FavoriteRouteDTO favoriteRoute = rideService.addRouteToFavorites(id, account);
        return ResponseEntity.ok(favoriteRoute);
    }

    @DeleteMapping("/favorites/by-favorite-id/{favoriteId}")
    public ResponseEntity<Void> removeByFavoriteId(@PathVariable Long favoriteId, Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        rideService.removeRouteFromFavorites(favoriteId, account);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/history/by-route-id/{routeId}")
    public ResponseEntity<Void> removeByRouteId(@PathVariable Long routeId, Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        rideService.removeRouteFromFavoritesByRoute(routeId, account);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/start")
    public ResponseEntity<Void> startRide(@PathVariable Long id, @RequestBody RideMomentDTO start) {
        rideService.startRide(id, start);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ongoing")
    public ResponseEntity<Boolean> getOngoing(Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        boolean hasRide = rideService.hasOngoingRide(account.getUser().getId());
        return ResponseEntity.ok(hasRide);
      
    @PostMapping(value = "/panic", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinishedRideDTO> panic(@RequestBody StopRideDTO panic) {
        FinishedRideDTO panicked = rideService.stopRide(panic, true);
        return new ResponseEntity<>(panicked, HttpStatus.OK);
    }
}