package com.example.UberComp.repository;

import com.example.UberComp.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findChatMessagesByChatRoom_Id(Long id);
}
