package com.example.UberComp.dto;

import com.example.UberComp.model.ChatMessage;
import com.example.UberComp.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;
    private String currentUserEmail;
    private Long userId;
    private List<ChatMessageDTO> messages;
}
