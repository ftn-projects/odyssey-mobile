package com.example.odyssey.utils;

import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class Validation {

    private static void requestFocus(View view, Window window) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static boolean validatePassword(TextInputEditText password) {
        String text = Objects.requireNonNull(password.getText()).toString();
        if (text.trim().isEmpty()) {
            password.setError("Password is required");
            password.setCursorVisible(true);
            return false;
        } else if (text.length() < 4) {
            password.setError("Password can't be less than 4 digits long");
            password.setCursorVisible(true);
            return false;
        }
        return true;
    }

    public static boolean validateNumber(TextInputEditText edit) {
        String text = Objects.requireNonNull(edit.getText()).toString();
        if (text.trim().isEmpty()) {
            edit.setError("Input is required");
            return false;
        } else if (Integer.parseInt(text) <= 0) {
            edit.setError("Number must be non negative");
            return false;
        }
        return true;
    }

    public static boolean validateNumberCompare(TextInputEditText edit, TextInputEditText min) {
        String text = Objects.requireNonNull(edit.getText()).toString();
        String minText = Objects.requireNonNull(min.getText()).toString();
        if (text.trim().isEmpty()) {
            edit.setError("Input is required");
            return false;
        } else if (Integer.parseInt(text) <= 0) {
            edit.setError("Number must be non negative");
            return false;
        } else if (Integer.parseInt(text) <= Integer.parseInt(minText)) {
            edit.setError("Number must be bigger than " + minText);
            return false;
        }
        return true;
    }

    public static boolean validateDouble(TextInputEditText edit) {
        String text = Objects.requireNonNull(edit.getText()).toString();
        if (text.trim().isEmpty()) {
            edit.setError("Input is required");
            return false;
        } else if (Double.parseDouble(text) <= 0) {
            edit.setError("Number must be real");
            return false;
        }
        return true;
    }

    public static boolean validateEmpty(TextInputEditText edit) {
        if (Objects.requireNonNull(edit.getText()).toString().trim().isEmpty()) {
            edit.setError("Input is required");
            return false;
        }
        return true;
    }

    public static boolean validateConfirmedPassword(TextInputEditText password, TextInputEditText original) {
        String text = Objects.requireNonNull(password.getText()).toString();
        if (text.trim().isEmpty()) {
            password.setError("Password is required");
            return false;
        } else if (text.length() < 4) {
            password.setError("Password can't be less than 4 digits long");
            return false;
        } else if (!text.equals(Objects.requireNonNull(original.getText()).toString())) {
            password.setError("Passwords must match");
            return false;
        }
        return true;
    }

    public static boolean validateEmail(TextInputEditText editText) {
        String emailId = Objects.requireNonNull(editText.getText()).toString();
        if (emailId.trim().isEmpty()) {
            editText.setError("Email is required");
            return false;
        } else {
            boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
            if (!isValid) {
                editText.setError("Invalid Email address, ex: abc@example.com");
                return false;
            }
        }
        return true;
    }

    public static boolean validateText(TextInputEditText editText) {
        String text = Objects.requireNonNull(editText.getText()).toString();
        if (text.trim().isEmpty()) {
            editText.setError("Input is required");
            return false;
        } else {
            boolean isValid = text.matches("[a-zA-ZŠšĐđŽžĆćČč 123456789.-_]+");
            if (!isValid) {
                editText.setError("Input must contain only alphanumeric characters");
                return false;
            }
        }
        return true;
    }

    public static boolean validateLettersAndNumber(TextInputEditText editText) {
        String text = Objects.requireNonNull(editText.getText()).toString();
        if (text.trim().isEmpty()) {
            editText.setError("Input is required");
            return false;
        } else {
            boolean isValid = text.matches("[A-Za-z0-9šŠčČćĆđĐžŽ ]+");
            if (!isValid) {
                editText.setError("Input must contain only letters and numbers");
                return false;
            }
        }
        return true;
    }

    public static boolean validatePhone(TextInputEditText editText) {
        String phone = Objects.requireNonNull(editText.getText()).toString();
        if (phone.trim().isEmpty()) {
            editText.setError("Phone number is required");
            return false;
        } else {
            boolean isValid = Patterns.PHONE.matcher(phone).matches();
            if (!isValid) {
                editText.setError("Invalid phone number");
                return false;
            }
        }
        return true;
    }

    public static boolean validateSelection(AutoCompleteTextView selection) {
        if (selection.getText().toString().trim().isEmpty()) {
            selection.setError("Selection is required");
            return false;
        }
        return true;
    }
}
