package com.example.UberComp.dto.ride;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.RouteDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRouteDTO {
    private Long Id;
    private AccountDTO userAccountDTO;
    private RouteDTO routeDTO;
}
