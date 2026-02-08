package com.example.ubercorp.dto;

import java.util.Objects;

public class FavoriteRouteDTO {
    private Long Id;
    private AccountDTO userAccountDTO;
    private GetRouteDTO routeDTO;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public AccountDTO getUserAccountDTO() {
        return userAccountDTO;
    }

    public void setUserAccountDTO(AccountDTO userAccountDTO) {
        this.userAccountDTO = userAccountDTO;
    }

    public GetRouteDTO getRouteDTO() {
        return routeDTO;
    }

    public void setRouteDTO(GetRouteDTO routeDTO) {
        this.routeDTO = routeDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteRouteDTO that = (FavoriteRouteDTO) o;
        return Objects.equals(Id, that.Id) && Objects.equals(userAccountDTO, that.userAccountDTO) && Objects.equals(routeDTO, that.routeDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, userAccountDTO, routeDTO);
    }
}
