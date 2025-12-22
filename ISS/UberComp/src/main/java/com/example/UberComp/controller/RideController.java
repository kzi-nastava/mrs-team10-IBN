package com.example.UberComp.controller;

import com.example.UberComp.dto.ride.*;
import com.example.UberComp.service.RideService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/api/rides")
public class RideController {

    private RideService rideService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetRideDTO>> getRides(
            @RequestParam(required = false) Long driverId) {

        Collection<GetRideDTO> rides = rideService.getRides(driverId);

        return ResponseEntity.ok(rides);
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideDetailsDTO> getRide(@PathVariable("id") Long id)
    {
        GetRideDetailsDTO ride = rideService.getRide(id);

        return new ResponseEntity<GetRideDetailsDTO>(ride, HttpStatus.OK);
    }

    @GetMapping(value = "/user/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetTrackingRideDTO> getTrackingRide(@PathVariable("id") Long id){
        GetTrackingRideDTO trackingRide = rideService.getTrackingRide(id);

        return new ResponseEntity<>(trackingRide, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteRide(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedStatusRideDTO> endOfRide(@RequestBody UpdatestatusRideDTO ride, @PathVariable Long id)
            throws Exception {
        UpdatedStatusRideDTO updatedRide = rideService.endOfRide(ride);

        return new ResponseEntity<UpdatedStatusRideDTO>(updatedRide, HttpStatus.OK);
    }
}
