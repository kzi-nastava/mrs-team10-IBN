package com.example.ubercorp.dto;

import java.util.List;

public class GetRideDTO {
    private List<RideDTO> content;
    private int totalPages;
    private long totalElements;
    private int number;

    public List<RideDTO> getContent() {
        return content;
    }

    public void setContent(List<RideDTO> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
