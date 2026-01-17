package com.example.UberComp.controller;

import com.example.UberComp.dto.driver.GetVehiclePositionDTO;
import com.example.UberComp.dto.ride.*;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.Account;
import com.example.UberComp.service.RideService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.ArrayList;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/api/rides")
public class RideController {

    private RideService rideService;

//    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping(value= "/driver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetRideDTO>> getRidesDriver(
            Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        Collection<GetRideDTO> rides = rideService.getRidesDriver(account.getUser().getId());
        return ResponseEntity.ok(rides);
    }

    @GetMapping(value= "/passenger", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetRideDTO>> getRidesPassenger(
            Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        Collection<GetRideDTO> rides = rideService.getRidesPassenger(account.getUser().getId());
        return ResponseEntity.ok(rides);
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

    @PutMapping(value = "/stop/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinishedRideDTO> stopRide(@RequestBody StopRideDTO ride, @PathVariable("id") Long id){
        FinishedRideDTO finished = rideService.stopRide(id, ride);
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideDTO> orderRide(@RequestBody CreateRideDTO dto) {
        GetRideDTO rideDTO = new GetRideDTO();
        rideDTO.setStartLocation(dto.getStartAddress());
        rideDTO.setEndLocation(dto.getDestinationAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(rideDTO);
    }

    @PostMapping("/favorites/{id}/order")
    public ResponseEntity<GetRideDTO> orderFromFavorite(@PathVariable Long id) {
        GetRideDTO dto = new GetRideDTO();
        dto.setStartLocation("Dummy start address for favorite " + id);
        dto.setEndLocation("Dummy destination address");
        dto.setPrice(500.0);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Void> startRide(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}