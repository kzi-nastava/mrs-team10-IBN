package com.example.ubercorp.api;

import com.example.ubercorp.dto.ChatInboxDTO;
import com.example.ubercorp.dto.ChatRoomDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ChatService {
    @GET("api/chat/all")
    Call<List<ChatInboxDTO>> getAllChats(@Header("Authorization") String token);

    @GET("api/chat/room/{id}")
    Call<ChatRoomDTO> getChatRoom(@Header("Authorization") String token,
                                  @Path("id") Long id);

    @GET("api/chat/other")
    Call<ChatRoomDTO> getMyChat(@Header("Authorization") String token);
}
