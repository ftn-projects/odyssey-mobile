package com.example.odyssey.utils;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class TokenUtils {
    private static final String TOKEN_PROPERTY_KEY = "odyssey_token";

    public static void removeToken() {
        System.clearProperty(TOKEN_PROPERTY_KEY);
    }

    public static void saveToken(String token) {
        System.setProperty(TOKEN_PROPERTY_KEY, token);
    }

    public static String getToken() {
        return System.getProperty(TOKEN_PROPERTY_KEY);
    }

    public static Token decodeToken() {
        String token = getToken();
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

    public static String getRole() {
        return decodeToken().getRole();
    }

    public static String getEmail() {
        return decodeToken().getEmail();
    }

    public static Long getId() {
        return decodeToken().getId();
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
