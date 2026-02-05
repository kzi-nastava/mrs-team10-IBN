package com.example.UberComp.model;

import com.example.UberComp.dto.ChatMessageDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String content;
    @Column
    private LocalDateTime time;
    @ManyToOne
    private ChatRoom chatRoom;
    @ManyToOne
    private User sender;
}
