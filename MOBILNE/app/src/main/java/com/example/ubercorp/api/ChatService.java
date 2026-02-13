package com.example.ubercorp.api;

import com.example.ubercorp.model.ChatInbox;
import com.example.ubercorp.model.ChatRoom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ChatService {
    @GET("api/chat/all")
    Call<List<ChatInbox>> getAllChats();

    @GET("api/chat/room/{id}")
    Call<ChatRoom> getChatRoom(@Header("Authorization") String token,
                               @Path("id") Long id);

    @GET("api/chat/other")
    Call<ChatRoom> getMyChat(@Header("Authorization") String token);
}
