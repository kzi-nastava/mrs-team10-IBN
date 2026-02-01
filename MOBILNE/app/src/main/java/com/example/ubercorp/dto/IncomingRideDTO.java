package com.example.ubercorp.dto;

public class IncomingRideDTO {
    private Long id;
    private RouteDTO route;

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
}
