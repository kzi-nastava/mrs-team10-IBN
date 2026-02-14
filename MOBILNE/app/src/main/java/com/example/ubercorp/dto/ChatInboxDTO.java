package com.example.ubercorp.dto;

public class ChatInboxDTO {
    private Long chatRoom;
    private UserDTO user;

    public Long getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(Long chatRoom) {
        this.chatRoom = chatRoom;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
