package com.example.UberComp.service;

import com.example.UberComp.dto.NotificationDTO;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.Notification;
import com.example.UberComp.model.User;
import com.example.UberComp.repository.NotificationRepository;
import com.example.UberComp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    NotificationRepository notificationRepository;

    public Notification broadcastToAdmins(NotificationDTO rawNotif){
        Notification notification = new Notification(rawNotif.getTitle(), rawNotif.getContent(), LocalDateTime.now());
        List<User> admins = userRepository.findAllByRole(AccountType.ADMINISTRATOR);
        notification.setNotifiedUsers(admins);
        notificationRepository.save(notification);
        return notification;
    }

    public List<Notification> getNotifsForUser(User user) {
        List<Notification> notifs = notificationRepository.findByNotifiedUsersIdOrderByTime(user.getId());
        return notifs;
    }
}
