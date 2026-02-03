package com.example.ubercorp.dto;

public class RideMomentDTO {
    private String isotime;

    public RideMomentDTO(String isotime) {
        this.isotime = isotime;
    }

    public String getIsotime() {
        return isotime;
    }

    public void setIsotime(String isotime) {
        this.isotime = isotime;
    }
}
