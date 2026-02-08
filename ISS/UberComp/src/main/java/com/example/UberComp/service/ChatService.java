package com.example.UberComp.service;

import com.example.UberComp.dto.account.UserDTO;
import com.example.UberComp.dto.chat.ChatInboxDTO;
import com.example.UberComp.dto.chat.ChatMessageDTO;
import com.example.UberComp.dto.chat.ChatRoomDTO;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.ChatRepository;
import com.example.UberComp.repository.ChatRoomRepository;
import com.example.UberComp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {
    public ChatRoomRepository roomRepository;
    public ChatRepository chatRepository;
    public UserRepository userRepository;
    public SimpMessagingTemplate messagingTemplate;

    public ChatRoomDTO getChatRoom(Long id){
        ChatRoom room = roomRepository.findChatRoomByUser_Id(id);
        if (room == null) {
            room = new ChatRoom();
            room.setUser(userRepository.findById(id).get());
            roomRepository.save(room);
        }

        ChatRoom finalRoom = room;
        List<ChatMessageDTO> messages = chatRepository.findChatMessagesByChatRoom_Id(room.getId())
                .stream()
                .map(m -> new ChatMessageDTO(
                        m.getContent(),
                        finalRoom.getId(),
                        m.getSender().getAccount().getEmail(),
                        m.getSender().getId()
                ))
                .toList();

        return new ChatRoomDTO(room.getId(), room.getUser().getAccount().getEmail(), room.getUser().getId(), messages);
    }

    public ChatRoomDTO getChatRoomAdmin(Long id){
        ChatRoom room = roomRepository.findChatRoomById(id);
        List<ChatMessageDTO> messages = chatRepository.findChatMessagesByChatRoom_Id(room.getId())
                .stream()
                .map(m -> new ChatMessageDTO(
                        m.getContent(),
                        room.getId(),
                        m.getSender().getAccount().getEmail(),
                        m.getSender().getId()
                ))
                .toList();

        return new ChatRoomDTO(room.getId(), room.getUser().getAccount().getEmail(), room.getUser().getId(), messages);
    }

    public List<ChatInboxDTO> getAllChatRooms(){
        List<ChatRoom> rooms = roomRepository.findAll();
        List<ChatInboxDTO> inbox = new ArrayList<>();
        for (ChatRoom room: rooms)
            inbox.add(new ChatInboxDTO(room.getId(), new UserDTO(room.getUser())));
        return inbox;
    }

    public void saveAndSend(ChatMessageDTO messageDTO, Account account) {
        ChatMessage chatMessage = new ChatMessage();
        User sender = userRepository.findById(account.getUser().getId()).orElseThrow();
        ChatRoom room = roomRepository.findById(messageDTO.getChatRoom()).get();

        chatMessage.setContent(messageDTO.getContent());
        chatMessage.setSender(sender);
        chatMessage.setChatRoom(room);
        chatRepository.save(chatMessage);

        ChatMessageDTO wsMessage = new ChatMessageDTO(
                chatMessage.getContent(),
                room.getId(),
                sender.getAccount().getEmail(),
                sender.getId()
        );

        if (account.getAccountType() == AccountType.ADMINISTRATOR) {
            sendToUser(room.getUser().getAccount().getEmail(), wsMessage);
            messagingTemplate.convertAndSend("/topic/chat/"+room.getUser().getAccount().getEmail(), wsMessage);

        } else {
            messagingTemplate.convertAndSend("/topic/chat/admin", wsMessage);
        }
    }


    public void sendToUser(String userEmail, ChatMessageDTO message) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/topic/chat/",
                    message
            );

        } catch (Exception e) {
            System.err.println("Failed to send message to " + userEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
