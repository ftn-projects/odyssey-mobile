package com.example.odyssey.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class TokenUtils {
    public static final String TOKEN_PROPERTY_KEY = "odyssey_token";
    public static final String APPLICATION_PREFERENCES_KEY = "odyssey_preferences";

    public static void removeToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APPLICATION_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(TOKEN_PROPERTY_KEY).apply();
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences preferences = context.getSharedPreferences(APPLICATION_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_PROPERTY_KEY, token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APPLICATION_PREFERENCES_KEY, Context.MODE_PRIVATE);
        return preferences.getString(TOKEN_PROPERTY_KEY, null);
    }

    public static String getToken(SharedPreferences preferences) {
        return preferences.getString(TOKEN_PROPERTY_KEY, null);
    }

    public static Token decodeToken(Context context) {
        String token = getToken(context);
        if (token == null) return new Token();

        try {
            String[] split = token.split("\\.");
            JSONObject json = new JSONObject(getJson(split[1]));
            JSONArray roles = new JSONArray(json.getString("role"));
            return new Token(
                    roles.getJSONObject(0).getString("name"),
                    json.getString("sub"),
                    json.getLong("subId")
            );
        } catch (UnsupportedEncodingException | JSONException ignored) {
            Log.e("TOKEN", "Bad token: " + token);
        }
        return new Token();
    }

    private static String getJson(String encoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(encoded, Base64.URL_SAFE);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static String getRole(Context context) {
        return decodeToken(context).getRole();
    }

    public static Long getId(Context context) {
        return decodeToken(context).getId();
    }

    public static class Token {
        private String role;
        private String email;
        private Long id;

        public Token() {
        }

        public Token(String role, String email, Long id) {
            this.role = role;
            this.email = email;
            this.id = id;
        }

        public String getRole() {
            return role;
        }

        public String getEmail() {
            return email;
        }

        public Long getId() {
            return id;
        }
    }
}
