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
    Validation validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button submit = findViewById(R.id.btnLogin);

        emailInput = findViewById(R.id.inputEmail);
        passwordInput = findViewById(R.id.inputPassword);
        emailEdit = findViewById(R.id.inputEditEmail);
        passwordEdit = findViewById(R.id.inputEditPassword);

        passwordEdit.addTextChangedListener(new ValidationTextWatcher(passwordEdit));
        emailEdit.addTextChangedListener(new ValidationTextWatcher(emailEdit));

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!validation.validatePassword(passwordInput, passwordEdit, getWindow())) {
                    return;
                }
                if (!validation.validateEmail(emailInput,emailEdit, getWindow())) {
                    return;
                }
            }
        });
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void goToHome(View view) {
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
            if(view.getId() == R.id.inputEditPassword) validation.validatePassword(passwordInput, passwordEdit, getWindow());
            else if (view.getId() == R.id.inputEditEmail) validation.validateEmail(emailInput,emailEdit, getWindow());
        }
    }

}