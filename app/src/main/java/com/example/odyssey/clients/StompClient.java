package com.example.odyssey.clients;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.odyssey.utils.TokenUtils;

import ua.naiksoftware.stomp.Stomp;

public class StompClient {
    ua.naiksoftware.stomp.StompClient stompClient;

    public StompClient() {
        this(ClientUtils.WEB_SOCKET_PATH);
    }

    @SuppressLint("CheckResult")
    public StompClient(String url) {
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

    @SuppressLint("CheckResult")
    public void subscribe(String topic, Runnable runnable) {
        stompClient.topic(topic)
                .subscribe(stompMessage -> {
                    Log.d("(¬‿¬)", "Stomp message received: " + stompMessage.getPayload());
                    Long userId = Long.valueOf(stompMessage.getPayload());
                    if (userId.equals(TokenUtils.getId())) {
                        runnable.run();
                    }
                }, throwable -> Log.e("(¬‿¬)", "Stomp subscribe error", throwable));
    }

    public void disconnect() {
        stompClient.disconnect();
    }
}
