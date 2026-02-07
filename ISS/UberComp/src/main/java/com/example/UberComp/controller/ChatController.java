package com.example.UberComp.controller;

import com.example.UberComp.dto.chat.ChatInboxDTO;
import com.example.UberComp.dto.chat.ChatMessageDTO;
import com.example.UberComp.dto.chat.ChatRoomDTO;
import com.example.UberComp.model.Account;
import com.example.UberComp.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.List;

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

    @PreAuthorize("hasAnyAuthority('passenger', 'driver')")
    @GetMapping(value = "/other",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatRoomDTO> getChatRoom(Authentication auth){
        Account acc = (Account) auth.getPrincipal();
        ChatRoomDTO chatRoom = chatService.getChatRoom(acc.getUser().getId());
        return ResponseEntity.ok(chatRoom);
    }

    @PreAuthorize("hasAnyAuthority('administrator')")
    @GetMapping(value="/room/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatRoomDTO> getChatRoom(Authentication auth, @PathVariable Long id){
        Account acc = (Account) auth.getPrincipal();
        ChatRoomDTO room = chatService.getChatRoomAdmin(id);
        room.setCurrentUserEmail(acc.getEmail());
        return ResponseEntity.ok(room);
    }

    @PreAuthorize("hasAnyAuthority('administrator')")
    @GetMapping(value="/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ChatInboxDTO>> getAllRooms(){
        List<ChatInboxDTO> inbox = chatService.getAllChatRooms();
        return ResponseEntity.ok(inbox);
    }
}
