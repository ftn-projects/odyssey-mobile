package com.example.odyssey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.example.odyssey.R;
import com.example.odyssey.utils.Validation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

    public void goToHome(View view) {
        if (!Validation.validatePassword(passwordInput, passwordEdit, getWindow())) {
            return;
        }
        if (!Validation.validateEmail(emailInput,emailEdit, getWindow())) {
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
            if(view.getId() == R.id.inputEditPassword) Validation.validatePassword(passwordInput, passwordEdit, getWindow());
            else if (view.getId() == R.id.inputEditEmail) Validation.validateEmail(emailInput,emailEdit, getWindow());
        }
    }

}