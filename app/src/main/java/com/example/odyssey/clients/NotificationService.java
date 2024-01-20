package com.example.odyssey.clients;

import com.example.odyssey.model.notifications.Notification;

import java.lang.ref.Reference;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {
    @GET("notifications/user/{userId}")
    Call<List<Notification>> findByUserId(@Path("userId") Long userId, @Query("types") List<Notification.Type> types, @Query("read") Boolean read);

    @PUT("notifications/{id}/{read}")
    Call<ResponseBody> updateRead(@Path("id") Long id, @Path("read") Boolean read);
}
