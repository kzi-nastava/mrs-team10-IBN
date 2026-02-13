package com.example.ubercorp.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.ubercorp.dto.AppNotificationDTO;
import com.example.ubercorp.dto.IncomingRideDTO;
import com.example.ubercorp.dto.ChatMessageDTO;
import com.example.ubercorp.utils.AppForegroundChecker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class MyNotificationManager {
    private static final String TAG = "NotificationManager";
    private static MyNotificationManager instance;
    private final Context context;
    private StompClient stompClient;
    private Gson gson;
    private CompositeDisposable disposables;

    private NotificationListener listener;

    public final PublishSubject<AppNotificationDTO> newNotification = PublishSubject.create();
    public final PublishSubject<IncomingRideDTO> incomingRide = PublishSubject.create();
    public final PublishSubject<Boolean> connectionStatus = PublishSubject.create();

    private final Set<Long> receivedNotificationIds = new HashSet<>();

    public interface NotificationListener {
        void onNotificationReceived(AppNotificationDTO notification);
        void onRideReceived(IncomingRideDTO ride);
        void onConnectionStatusChanged(boolean isConnected);
        void onError(String error);
    }

    private MyNotificationManager(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson();
        this.disposables = new CompositeDisposable();
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyNotificationManager(context);
        }
        return instance;
    }

    public void setListener(NotificationListener listener) {
        this.listener = listener;
        setupListeners();
    }

    private void setupListeners() {
        disposables.clear();

        disposables.add(
                newNotification
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                notification -> {
                                    if (listener != null) {
                                        new Handler(Looper.getMainLooper()).post(() ->
                                                listener.onNotificationReceived(notification));
                                    }
                                },
                                error -> Log.e(TAG, "Error in notification stream", error)
                        )
        );

        disposables.add(
                incomingRide
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ride -> {
                                    if (listener != null) {
                                        new Handler(Looper.getMainLooper()).post(() ->
                                                listener.onRideReceived(ride));
                                    }
                                },
                                error -> Log.e(TAG, "Error in ride stream", error)
                        )
        );

        disposables.add(
                connectionStatus
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isConnected -> {
                                    if (listener != null) {
                                        new Handler(Looper.getMainLooper()).post(() ->
                                                listener.onConnectionStatusChanged(isConnected));
                                    }
                                },
                                error -> Log.e(TAG, "Error in connection status", error)
                        )
        );
    }

    @SuppressLint("CheckResult")
    public void connect(String serverUrl, String authToken, String userEmail) {
        if (stompClient != null && stompClient.isConnected()) {
            Log.d(TAG, "Already connected");
            return;
        }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, serverUrl);
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + authToken));

        stompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "WebSocket connected");
                            connectionStatus.onNext(true);
                            subscribeToTopics(userEmail);
                            break;
                        case CLOSED:
                            Log.d(TAG, "WebSocket closed");
                            connectionStatus.onNext(false);
                            break;
                        case ERROR:
                            Log.e(TAG, "WebSocket error", lifecycleEvent.getException());
                            connectionStatus.onNext(false);
                            break;
                    }
                });

        stompClient.connect(headers);
    }

    private void subscribeToTopics(String userEmail) {
        stompClient.topic("/user/queue/notifications")
                .subscribe(topicMessage -> {
                    try {
                        String messageBody = topicMessage.getPayload();
                        AppNotificationDTO notification = gson.fromJson(messageBody, AppNotificationDTO.class);

                        if (notification != null && receivedNotificationIds.contains(notification.getId())) {
                            Log.d(TAG, "Duplicate notification ignored: " + notification.getTitle());
                            return;
                        }
                        if (notification != null) {
                            receivedNotificationIds.add(notification.getId());
                        }

                        if (AppForegroundChecker.isAppInForeground(context)) {
                            newNotification.onNext(notification);
                            Log.d(TAG, "WS notification emitted: " + notification.getTitle());
                        } else {
                            Log.d(TAG, "App in background, ignoring WS notification, FCM will handle it");
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing notification", e);
                    }
                }, throwable -> Log.e(TAG, "Error subscribing to notifications", throwable));

        stompClient.topic("/topic/ride/" + userEmail)
                .subscribe(topicMessage -> {
                    try {
                        String messageBody = topicMessage.getPayload();
                        IncomingRideDTO ride = gson.fromJson(messageBody, IncomingRideDTO.class);
                        incomingRide.onNext(ride);
                        Log.d(TAG, "Received ride update");
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing ride message", e);
                    }
                }, throwable -> Log.e(TAG, "Error subscribing to ride updates", throwable));
    }

    public void subscribeToChat(Long chatRoomId, Consumer<ChatMessageDTO> callback) {
        Log.d(TAG, "Subscribing to /topic/chat/" + chatRoomId);

        stompClient.topic("/topic/chat/" + chatRoomId)
                .subscribe(
                        stompMessage -> {
                            Log.d(TAG, "✅ Message received on /topic/chat/" + chatRoomId);
                            Log.d(TAG, "Payload: " + stompMessage.getPayload());

                            try {
                                ChatMessageDTO msg = new Gson().fromJson(stompMessage.getPayload(), ChatMessageDTO.class);
                                Log.d(TAG, "Parsed message: " + msg.getContent());
                                callback.accept(msg);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing message", e);
                            }
                        },
                        error -> Log.e(TAG, "Error subscribing to chat", error)
                );
    }

    public void sendMessage(ChatMessageDTO message) {

        if (stompClient == null || !stompClient.isConnected()) {
            Log.e(TAG, "StompClient not connected!");
            return;
        }

        String jsonMessage = new Gson().toJson(message);

        stompClient.send("/ws/send-message", jsonMessage)
                .subscribe(
                        () -> Log.d(TAG, "Message sent successfully"),
                        error -> {
                            Log.e(TAG, "Error sending message: " + error.getMessage(), error);
                            error.printStackTrace();
                        }
                );
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
            connectionStatus.onNext(false);
        }
        disposables.clear();
    }

    public boolean isConnected() {
        return stompClient != null && stompClient.isConnected();
    }
}
