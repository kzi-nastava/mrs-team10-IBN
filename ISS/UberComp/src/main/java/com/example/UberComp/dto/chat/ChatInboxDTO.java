package com.example.UberComp.dto.chat;

import com.example.UberComp.dto.account.UserDTO;
import com.example.UberComp.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatInboxDTO {
    private Long chatRoom;
    private UserDTO user;
}
