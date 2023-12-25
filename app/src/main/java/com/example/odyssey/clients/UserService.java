package com.example.odyssey.clients;

import com.example.odyssey.model.Auth.AuthResponse;
import com.example.odyssey.model.User;
import com.example.odyssey.model.users.PasswordUpdate;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {
    @GET("users")
    Call<List<User>> getAll();

    @GET("users/{id}")
    Call<User> findById(@Path("id") Long id);



    @PUT("users")
    Call<User> update(@Body User user);

    @PUT("users/password")
    Call<ResponseBody> updatePassword(@Body PasswordUpdate passwordUpdate);

    @DELETE("users/deactivate/{id}")
    Call<ResponseBody> deactivate(@Path("id") Long id);

    @Multipart
    @POST("users/image/{id}")
    Call<ResponseBody> uploadImage(@Path("id") Long id, @Part MultipartBody.Part image);
}
