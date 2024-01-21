package com.example.odyssey.clients;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.odyssey.model.notifications.Notification;
import com.example.odyssey.utils.TokenUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import ua.naiksoftware.stomp.Stomp;

public class StompClient {
    ua.naiksoftware.stomp.StompClient stompClient;
    Context context;

    public StompClient(Context context) {
        this(context, ClientUtils.WEB_SOCKET_PATH);
    }

    @SuppressLint("CheckResult")
    public StompClient(Context context, String url) {
        this.context = context;
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);
        stompClient.connect();
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("(¬‿¬)", "Stomp connection opened");
                    break;
                case ERROR:
                    Log.e("(¬‿¬)", "Stomp connection error", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d("(¬‿¬)", "Stomp connection closed");
                    break;
            }
        });
    }

    public final String UPDATES_TOPIC = "/topic/notificationChange";
    public final String NOTIFICATION_TOPIC = "/topic/newNotification";

    @SuppressLint("CheckResult")
    public void subscribe(Runnable runnable) {
        stompClient.topic(UPDATES_TOPIC)
                .subscribe(stompMessage -> {
                    Log.d("(¬‿¬)", "Stomp message received: " + stompMessage.getPayload());
                    Long userId = Long.valueOf(stompMessage.getPayload());
                    if (userId.equals(TokenUtils.getId(context))) {
                        runnable.run();
                    }
                }, throwable -> Log.e("(¬‿¬)", "Stomp subscribe error", throwable));
    }

    @SuppressLint("CheckResult")
    public void subscribe(Consumer<Long> consumer) {
        stompClient.topic(NOTIFICATION_TOPIC)
                .subscribe(stompMessage -> {
                    Log.d("(¬‿¬)", "Stomp message received: " + stompMessage.getPayload());
                    Type type = new TypeToken<Map<String, String>>(){}.getType();
                    Map<String, String> map = new Gson().fromJson(stompMessage.getPayload(), type);

                    if (!map.containsKey("userId") || !map.containsKey("notificationId")) {
                        Log.e("(¬‿¬)", "Stomp message missing keys.");
                        return;
                    }

                    Long userId = Long.valueOf(Objects.requireNonNull(map.get("userId")));
                    Long notificationId = Long.valueOf(Objects.requireNonNull(map.get("notificationId")));

                    if (userId.equals(TokenUtils.getId(context))) {
                        consumer.accept(notificationId);
                    }
                }, throwable -> Log.e("(¬‿¬)", "Stomp subscribe error", throwable));
    }

    public void disconnect() {
        stompClient.disconnect();
    }
}
