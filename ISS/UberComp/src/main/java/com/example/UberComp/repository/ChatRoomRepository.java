package com.example.UberComp.repository;

import com.example.UberComp.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findChatRoomByUser_Id(Long userId);
}
