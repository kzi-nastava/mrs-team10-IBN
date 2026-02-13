package com.example.ubercorp.dto;

public class ChatMessageDTO {
    private String content;
    private Long chatRoom;
    private String email;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(Long chatRoom) {
        this.chatRoom = chatRoom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
