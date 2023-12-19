package com.example.odyssey.utils;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Validation {

    private void requestFocus(View view, Window window) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    public boolean validatePassword(TextInputLayout pass, TextInputEditText password, Window window){
        if (password.getText().toString().trim().isEmpty()) {
            pass.setError("Password is required");
            requestFocus(password, window);
            return false;
        }else if(password.getText().toString().length() < 6){
            pass.setError("Password can't be less than 6 digits long");
            requestFocus(password, window);
            return false;
        }
        pass.setErrorEnabled(false);
        return true;
    }

    public boolean validateEmail(TextInputLayout email, TextInputEditText editText, Window window) {
        String emailId = editText.getText().toString();
        if (emailId.trim().isEmpty()) {
            editText.setError("Password is required");
            requestFocus(email, window);
            return false;
        } else {
            boolean  isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
            if (!isValid) {
                email.setError("Invalid Email address, ex: abc@example.com");
                requestFocus(editText,window);
                return false;
            } else { email.setErrorEnabled(false); }
        }
        return true;
    }

}
