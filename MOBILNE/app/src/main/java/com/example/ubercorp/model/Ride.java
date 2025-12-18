package com.example.ubercorp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

public class Ride implements Parcelable {

    private Long id;
    private String startLocation;
    private String destination;
    private Date start;
    private Date end;
    private Double price;

    private ArrayList<User> passengers;

    public Ride(){}

    public Ride(Long id, String startLocation, String destination, Date start, Date end, Double price, ArrayList<User> passengers){
        this.id = id;
        this.startLocation = startLocation;
        this.destination = destination;
        this.start = start;
        this.end = end;
        this.price = price;
        this.passengers = passengers;
    }

    protected Ride(Parcel in){
        id = in.readLong();
        startLocation = in.readString();
        destination = in.readString();
        start = new Date(in.readLong());
        end = new Date(in.readLong());
        price = in.readDouble();
    }

    public Long getId(){return id;}
    public void setId(Long id) {this.id = id;}
    public String getStartLocation(){return startLocation;}
    public void setStartLocation(String startLocation) {this.startLocation = startLocation;}
    public String getDestination(){return this.destination;}
    public void setDestination(String destination){this.destination = destination;}
    public Date getStart(){return this.start;}
    public void setStart(Date start){this.start = start;}
    public Date getEnd(){return this.end;}
    public void setEnd(Date end){this.end = end;}

    public Double getPrice(){return this.price;}

    public void setPrice(Double price) {this.price = price;}

    public ArrayList<User> getPassengers() {
        return passengers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(startLocation);
        dest.writeString(destination);
        dest.writeLong(start.getTime());
        dest.writeLong(end.getTime());
        dest.writeDouble(price);
    }

    public static final Creator<Ride> CREATOR = new Creator<Ride>() {
        @Override
        public Ride createFromParcel(Parcel in) {
            return new Ride(in);
        }

        @Override
        public Ride[] newArray(int size) {
            return new Ride[size];
        }
    };
}
