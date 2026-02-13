package com.example.ubercorp.model;

import com.example.ubercorp.dto.ChatMessageDTO;

import java.util.List;

public class ChatRoom {
    private Long id;
    private String currentUserEmail;
    private Long receiver;
    private List<ChatMessageDTO> messages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public List<ChatMessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageDTO> messages) {
        this.messages = messages;
    }
}
