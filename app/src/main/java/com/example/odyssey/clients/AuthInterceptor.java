package com.example.odyssey.clients;


import androidx.annotation.NonNull;

import com.example.odyssey.utils.TokenUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String accessToken = TokenUtils.getToken();
        Request request = chain.request();

        if (request.header("skip") == null && accessToken != null)
            request = request.newBuilder().header("Authorization", "Bearer " + accessToken).build();
        return chain.proceed(request);
    }
}

