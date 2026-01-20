package com.example.UberComp.repository;

import com.example.UberComp.model.Account;
import com.example.UberComp.model.FavoriteRoute;
import com.example.UberComp.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, Long> {
    List<FavoriteRoute> findFavoriteRouteByAccount(Account account);
    Optional<FavoriteRoute> findByAccountAndRoute(Account account, Route route);
    Optional<FavoriteRoute> findByRoute(Route route);
}
