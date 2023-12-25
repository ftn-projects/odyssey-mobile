package com.example.odyssey.clients;

import com.example.odyssey.model.Auth.AuthResponse;
import com.example.odyssey.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @GET("users")
    Call<List<User>> getAll();

    @GET("users/{id}")
    Call<User> findById(@Path("id") Long id);



    @PUT("users")
    Call<AuthResponse> update(@Body User user);
}
