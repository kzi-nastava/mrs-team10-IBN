package com.example.UberComp.controller;

import com.example.UberComp.dto.NotificationDTO;
import com.example.UberComp.model.Account;
import com.example.UberComp.model.Notification;
import com.example.UberComp.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Notification>> getNotifications(Authentication auth) {
        Account account = (Account) auth.getPrincipal();
        List<Notification> notifs = notificationService.getNotifsForUser(account.getUser());
        return ResponseEntity.ok(notifs);
    }

    @MessageMapping("/panic") // activates on /ws/panic
    @SendTo("/notifications/admin") // send messages to topic /notification/admin
    public Notification broadcastPanicNotification(NotificationDTO rawNotif){
        Notification notification = notificationService.broadcastToAdmins(rawNotif);
        return notification;
    }
}
