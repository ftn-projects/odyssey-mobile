package com.example.odyssey.clients;


import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.odyssey.utils.TokenUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    SharedPreferences preferences;

    public AuthInterceptor(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (preferences == null) return chain.proceed(chain.request());

        String accessToken = TokenUtils.getToken(preferences);
        Request request = chain.request();

        if (request.header("skip") == null && accessToken != null)
            request = request.newBuilder().header("Authorization", "Bearer " + accessToken).build();
        return chain.proceed(request);
    }
}

