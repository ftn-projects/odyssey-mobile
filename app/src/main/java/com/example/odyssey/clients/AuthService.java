package com.example.odyssey.clients;

import com.example.odyssey.model.auth.AuthResponse;
import com.example.odyssey.model.auth.Login;
import com.example.odyssey.model.auth.Register;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("users/login")
    Call<AuthResponse> login(@Body Login login);

    @POST("users/register")
    Call<Register> register(@Body Register register);
}
