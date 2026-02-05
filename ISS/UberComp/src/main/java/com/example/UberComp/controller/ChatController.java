package com.example.UberComp.controller;

import com.example.UberComp.dto.ChatMessageDTO;
import com.example.UberComp.dto.ChatRoomDTO;
import com.example.UberComp.model.Account;
import com.example.UberComp.model.ChatMessage;
import com.example.UberComp.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {
    private ChatService chatService;

    @MessageMapping("/send-message")
    public void sendMessage(ChatMessageDTO dto, Authentication auth) {
        Account acc = (Account) auth.getPrincipal();
        chatService.saveAndSend(dto, acc);
    }

    @GetMapping(value = "/other",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatRoomDTO> getChatRoom(Authentication auth){
        Account acc = (Account) auth.getPrincipal();
        ChatRoomDTO chatRoom = chatService.getChatRoom(acc.getUser().getId());
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping(value="/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatRoomDTO> getChatRoomAdmin(Authentication auth){
        Account acc = (Account) auth.getPrincipal();
        ChatRoomDTO chatRoom = chatService.getChatRoom(3L);
        chatRoom.setUserId(1L);
        chatRoom.setCurrentUserEmail("ignatic@mail.com");
        return ResponseEntity.ok(chatRoom);
    }
}
