package com.example.UberComp.controller;

import com.example.UberComp.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/notification")
public class NotificationController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Notification>> getNotifications() {
        ArrayList<Notification> notifs = new ArrayList<>();
        notifs.add(new Notification(1L, "Title 1", "Content 1"));
        notifs.add(new Notification(2L, "Title 2", "Content 2"));
        notifs.add(new Notification(3L, "Title 3", "Content 3"));
        return ResponseEntity.ok(notifs);
    }

    @MessageMapping("/panic") // activates on /ws/panic
    @SendTo("/notifications/admin") // send messages to topic /notification/admin
    public Notification broadcastPanicNotification(String rawNotif){
        ObjectMapper mapper = new ObjectMapper();
        try {
            Notification notification = mapper.readValue(rawNotif, Notification.class);
            return notification;
        } catch(Exception ex) {
            return null;
        }
    }
}
