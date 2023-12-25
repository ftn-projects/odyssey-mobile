package com.example.odyssey.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.Auth.AuthResponse;
import com.example.odyssey.model.Auth.Login;
import com.example.odyssey.utils.TokenUtils;
import com.example.odyssey.utils.Validation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout passwordInput, emailInput;
    TextInputEditText passwordEdit, emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.inputEmail);
        passwordInput = findViewById(R.id.inputPassword);
        emailEdit = findViewById(R.id.inputEditEmail);
        passwordEdit = findViewById(R.id.inputEditPassword);

        passwordEdit.addTextChangedListener(new ValidationTextWatcher(passwordEdit));
        emailEdit.addTextChangedListener(new ValidationTextWatcher(emailEdit));

    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        if (!Validation.validatePassword(passwordInput, passwordEdit, getWindow())) {
            return;
        }
        if (!Validation.validateEmail(emailInput, emailEdit, getWindow())) {
            return;
        }

        Login login = new Login(emailEdit.getText().toString(), passwordEdit.getText().toString());
        Call<AuthResponse> authResponse = ClientUtils.authService.login(login);
        Intent intent = new Intent(this, MainActivity.class);

        authResponse.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.code() == 200) {
                    if (response.body() == null) {
                        Log.d("REZ", "Bad");
                        return;
                    }

                    TokenUtils.saveToken(response.body().getToken());
                    Log.d("REZ", "Good");
                    startActivity(intent);
                } else {
                    Log.d("REZ", "Bad");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private class ValidationTextWatcher implements TextWatcher {
        private final View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.inputEditPassword)
                Validation.validatePassword(passwordInput, passwordEdit, getWindow());
            else if (view.getId() == R.id.inputEditEmail)
                Validation.validateEmail(emailInput, emailEdit, getWindow());
        }
    }

}