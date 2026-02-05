package com.example.UberComp.controller;

import com.example.UberComp.dto.user.FcmTokenDTO;
import com.example.UberComp.model.User;
import com.example.UberComp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final UserRepository userRepository;

    @PostMapping("/token")
    public ResponseEntity<Void> updateFcmToken(
            Authentication authentication,
            @RequestBody FcmTokenDTO fcmTokenDTO
    ) {
        String email = authentication.getName();
        User user = userRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFcmToken(fcmTokenDTO.getToken());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/token")
    public ResponseEntity<Void> deleteFcmToken(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFcmToken(null);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}