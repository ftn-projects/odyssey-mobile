package com.example.odyssey.clients;

import com.example.odyssey.model.Auth.AuthResponse;
import com.example.odyssey.model.Auth.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("users/login")
    Call<AuthResponse> login(@Body Login login);

}
