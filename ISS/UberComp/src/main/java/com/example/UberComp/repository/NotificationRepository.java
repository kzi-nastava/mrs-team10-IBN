package com.example.UberComp.repository;

import com.example.UberComp.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByNotifiedUsersIdOrderByTime(Long id);
}
