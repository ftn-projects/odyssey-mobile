package com.example.odyssey.utils;

import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Validation {

    private static void requestFocus(View view, Window window) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static boolean validatePassword(TextInputLayout pass, TextInputEditText password, Window window) {
        if (password.getText().toString().trim().isEmpty()) {
            pass.setError("Password is required");
            requestFocus(password, window);
            return false;
        } else if (password.getText().toString().length() < 4) {
            pass.setError("Password can't be less than 4 digits long");
            requestFocus(password, window);
            return false;
        }
        pass.setErrorEnabled(false);
        return true;
    }

    public static boolean validateNumber(TextInputLayout input, TextInputEditText edit, Window window) {
        if (edit.getText().toString().trim().isEmpty()) {
            input.setError("Input is required");
            requestFocus(edit, window);
            return false;
        } else if (Integer.parseInt(edit.getText().toString()) <= 0) {
            input.setError("Number must be non negative");
            requestFocus(edit, window);
            return false;
        }
        input.setErrorEnabled(false);
        return true;
    }

    public static boolean validateNumberCompare(TextInputLayout input, TextInputEditText edit, TextInputEditText min, Window window) {
        if (edit.getText().toString().trim().isEmpty()) {
            input.setError("Input is required");
            requestFocus(edit, window);
            return false;
        } else if (Integer.parseInt(edit.getText().toString()) <= 0) {
            input.setError("Number must be non negative");
            requestFocus(edit, window);
            return false;
        } else if (Integer.parseInt(edit.getText().toString()) <= Integer.parseInt(min.getText().toString())) {
            input.setError("Number must be bigger than " + min.getText().toString());
            requestFocus(edit, window);
            return false;
        }
        input.setErrorEnabled(false);
        return true;
    }

    public static boolean validateDouble(TextInputLayout input, TextInputEditText edit, Window window) {
        if (edit.getText().toString().trim().isEmpty()) {
            input.setError("Input is required");
            requestFocus(edit, window);
            return false;
        } else if (Double.parseDouble(edit.getText().toString()) <= 0) {
            input.setError("Number must be real");
            requestFocus(edit, window);
            return false;
        }
        input.setErrorEnabled(false);
        return true;
    }

    public static boolean validateEmpty(TextInputLayout input, TextInputEditText edit, Window window) {
        if (edit.getText().toString().trim().isEmpty()) {
            input.setError("Input is required");
            requestFocus(edit, window);
            return false;
        }
        input.setErrorEnabled(false);
        return true;
    }

    public static boolean validateConfirmedPassword(TextInputLayout pass, TextInputEditText password, TextInputEditText original, Window window) {
        if (password.getText().toString().trim().isEmpty()) {
            pass.setError("Password is required");
            requestFocus(password, window);
            return false;
        } else if (password.getText().toString().length() < 4) {
            pass.setError("Password can't be less than 4 digits long");
            requestFocus(password, window);
            return false;
        } else if (!password.getText().toString().equals(original.getText().toString())) {
            pass.setError("Passwords must match");
            requestFocus(password, window);
            return false;
        }
        pass.setErrorEnabled(false);
        return true;
    }

    public static boolean validateEmail(TextInputLayout email, TextInputEditText editText, Window window) {
        String emailId = editText.getText().toString();
        if (emailId.trim().isEmpty()) {
            editText.setError("Email is required");
            requestFocus(email, window);
            return false;
        } else {
            boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
            if (!isValid) {
                email.setError("Invalid Email address, ex: abc@example.com");
                requestFocus(editText, window);
                return false;
            } else {
                email.setErrorEnabled(false);
            }
        }
        return true;
    }

    public static boolean validateText(TextInputLayout inputText, TextInputEditText editText, Window window) {
        String text = editText.getText().toString();
        if (text.trim().isEmpty()) {
            editText.setError("Input is required");
            requestFocus(inputText, window);
            return false;
        } else {
            boolean isValid = text.matches("[a-zA-ZŠšĐđŽžĆćČč 123456789.-_]+");
            if (!isValid) {
                inputText.setError("Input must contain only alphanumeric characters");
                requestFocus(editText, window);
                return false;
            } else {
                inputText.setErrorEnabled(false);
            }
        }
        return true;
    }

    public static boolean validateLettersAndNumber(TextInputLayout inputText, TextInputEditText editText, Window window) {
        String text = editText.getText().toString();
        if (text.trim().isEmpty()) {
            editText.setError("Input is required");
            requestFocus(inputText, window);
            return false;
        } else {
            boolean isValid = text.matches("[A-Za-z0-9šŠčČćĆđĐžŽ ]+");
            if (!isValid) {
                inputText.setError("Input must contain only letters and numbers");
                requestFocus(editText, window);
                return false;
            } else {
                inputText.setErrorEnabled(false);
            }
        }
        return true;
    }

    public static boolean validatePhone(TextInputLayout inputText, TextInputEditText editText, Window window) {
        String phone = editText.getText().toString();
        if (phone.trim().isEmpty()) {
            editText.setError("Phone number is required");
            requestFocus(inputText, window);
            return false;
        } else {
            boolean isValid = Patterns.PHONE.matcher(phone).matches();
            if (!isValid) {
                inputText.setError("Invalid phone number");
                requestFocus(editText, window);
                return false;
            } else {
                inputText.setErrorEnabled(false);
            }
        }
        return true;
    }

    public static boolean validateSelection(AutoCompleteTextView selection, Window window) {
        if (selection.getText().toString().trim().isEmpty()) {
            selection.setError("Selection is required");
            requestFocus(selection, window);
            return false;
        }
        return true;
    }
}
