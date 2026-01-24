package com.example.ubercorp.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.ubercorp.activities.enums.RideStatus;
import com.example.ubercorp.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class RideDTO implements Parcelable{

        private Long id;

        private RouteDTO route;

        private ArrayList<User> passengers;

        private DriverDTO driver;

        private Boolean babies;

        private Boolean pets;

        private Double price;

        private String startTime;

        private String endTime;

        private LocalDateTime finish;

        private RideStatus status;

        private String cancellationReason;
        private String startLocation;
        private String endLocation;

        public static final Creator<RideDTO> CREATOR = new Creator<RideDTO>() {
            @Override
            public RideDTO createFromParcel(Parcel in) {
                return new RideDTO(in);
            }

            @Override
            public RideDTO[] newArray(int size) {
                return new RideDTO[size];
            }
        };

        public RideDTO(){}

        public RideDTO(Long id, String startLocation, String destination, String start, String end, Double price, ArrayList<User> passengers){
            this.id = id;
            this.startLocation = startLocation;
            this.endLocation = destination;
            this.startTime = start;
            this.endTime = end;
            this.price = price;
            this.passengers = passengers;
        }

        protected RideDTO(Parcel in){
            id = in.readLong();
            startLocation = in.readString();
            endLocation = in.readString();
            startTime = in.readString();
            endTime = in.readString();
            price = in.readDouble();
        }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RouteDTO getRoute() {
        return route;
    }

    public void setRoute(RouteDTO route) {
        this.route = route;
    }

    public ArrayList<User> getPassengers() {
        return passengers;
    }

    public void setPassengers(ArrayList<User> passengers) {
        this.passengers = passengers;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverDTO driver) {
        this.driver = driver;
    }

    public Boolean getBabies() {
        return babies;
    }

    public void setBabies(Boolean babies) {
        this.babies = babies;
    }

    public Boolean getPets() {
        return pets;
    }

    public void setPets(Boolean pets) {
        this.pets = pets;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public LocalDateTime getFinish() {
        return finish;
    }

    public void setFinish(LocalDateTime finish) {
        this.finish = finish;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getDestination() {
        return endLocation;
    }

    public void setDestination(String destination) {
        this.endLocation = destination;
    }
}
