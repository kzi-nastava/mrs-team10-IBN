package com.example.UberComp.controller;

import com.example.UberComp.dto.ride.*;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.service.RideService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideDetailsDTO> getRide(@PathVariable("id") Long id)
    {
        GetRideDetailsDTO ride = rideService.getRide(id);
        return new ResponseEntity<GetRideDetailsDTO>(ride, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetTrackingRideDTO> getTrackingRide(@PathVariable("id") Long id){
        GetTrackingRideDTO trackingRide = rideService.getTrackingRide(id);
        return new ResponseEntity<>(trackingRide, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteRide(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/stop/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StoppedRideDTO> stopRide(@RequestBody StopRideDTO ride, @PathVariable("id") Long id){
        StoppedRideDTO finished = new StoppedRideDTO(id, ride.getPassed(), ride.getFinishTime(), 0.0);
        return new ResponseEntity<>(finished, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedStatusRideDTO> updateRideStatus(@RequestBody UpdateStatusRideDTO rideStatus, @PathVariable Long id)
            throws Exception {
        UpdatedStatusRideDTO updatedRide = rideService.updateRideStatus(rideStatus);
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