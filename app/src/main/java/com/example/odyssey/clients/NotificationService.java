package com.example.odyssey.clients;

import com.example.odyssey.model.notifications.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {
    @GET("notifications/user/{userId}")
    Call<List<Notification>> findByUserId(@Path("userId") Long userId, @Query("types") List<Notification.Type> types, @Query("read") Boolean read);
}
