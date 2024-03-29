package com.example.odyssey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.Address;
import com.example.odyssey.model.auth.Register;
import com.example.odyssey.model.users.User;
import com.example.odyssey.utils.Validation;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout passwordInput, emailInput, nameInput, surnameInput,
            addressInput, cityInput, countryInput, phoneNumberInput, confirmInput;
    TextInputEditText passwordEdit, emailEdit, nameEdit, surnameEdit,
            addressEdit, cityEdit, countryEdit, phoneNumberEdit, confirmEdit;
    SwitchMaterial roleSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.inputEmail);
        emailEdit = findViewById(R.id.inputEditEmail);
        emailEdit.addTextChangedListener(new ValidationTextWatcher(emailEdit));

        nameInput = findViewById(R.id.inputName);
        nameEdit = findViewById(R.id.inputEditName);
        nameEdit.addTextChangedListener(new ValidationTextWatcher(nameEdit));

        surnameInput = findViewById(R.id.inputSurname);
        surnameEdit = findViewById(R.id.inputEditSurname);
        surnameEdit.addTextChangedListener(new ValidationTextWatcher(surnameEdit));

        addressInput = findViewById(R.id.inputStreet);
        addressEdit = findViewById(R.id.inputEditStreet);
        addressEdit.addTextChangedListener(new ValidationTextWatcher(addressEdit));

        cityInput = findViewById(R.id.inputCity);
        cityEdit = findViewById(R.id.inputEditCity);
        cityEdit.addTextChangedListener(new ValidationTextWatcher(cityEdit));

        countryInput = findViewById(R.id.inputCountry);
        countryEdit = findViewById(R.id.inputEditCountry);
        countryEdit.addTextChangedListener(new ValidationTextWatcher(countryEdit));

        phoneNumberInput = findViewById(R.id.inputPhoneNumber);
        phoneNumberEdit = findViewById(R.id.inputEditPhoneNumber);
        phoneNumberEdit.addTextChangedListener(new ValidationTextWatcher(phoneNumberEdit));

        passwordInput = findViewById(R.id.inputPassword);
        passwordEdit = findViewById(R.id.inputEditPassword);
        passwordEdit.addTextChangedListener(new ValidationTextWatcher(passwordEdit));

        confirmInput = findViewById(R.id.inputConfirmPassword);
        confirmEdit = findViewById(R.id.inputEditConfirmPassword);
        confirmEdit.addTextChangedListener(new ValidationTextWatcher(confirmEdit));

        roleSwitch = findViewById(R.id.switchRole);

    }

    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    public void register(View view) {
        if (!Validation.validatePassword(passwordEdit) ||
                !Validation.validateEmail(emailEdit) ||
                !Validation.validatePhone(phoneNumberEdit) ||
                !Validation.validateText(nameEdit) ||
                !Validation.validateText(surnameEdit) ||
                !Validation.validateLettersAndNumber(addressEdit) ||
                !Validation.validateText(cityEdit) ||
                !Validation.validateText(countryEdit) ||
                !Validation.validateConfirmedPassword(confirmEdit, passwordEdit)) {
            return;
        }
        String role = roleSwitch.isChecked() ? "HOST" : "GUEST";

        Address address = new Address(addressEdit.getText().toString(), cityEdit.getText().toString(), countryEdit.getText().toString());
        Register register = new Register(-1L, emailEdit.getText().toString(), nameEdit.getText().toString(), surnameEdit.getText().toString(),
                phoneNumberEdit.getText().toString(), role, address, new User.Settings(), "", passwordEdit.getText().toString());
        Call<Register> registerResponse = ClientUtils.authService.register(register);

        registerResponse.enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Registration success", Toast.LENGTH_LONG).show();
                } else {
                    String message = ClientUtils.getError(response, "Registration failed");
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    Log.d("UWU", response.code() + " " + response.message());
                    Log.d("REZ", "Bad");
                }
            }

            @Override
            public void onFailure(Call<Register> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });

        Intent intent = new Intent(this, LoginActivity.class);
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
            if (view.getId() == R.id.inputEditPassword)
                Validation.validatePassword(passwordEdit);
            else if (view.getId() == R.id.inputEditEmail)
                Validation.validateEmail(emailEdit);
            else if (view.getId() == R.id.inputEditName)
                Validation.validateText(nameEdit);
            else if (view.getId() == R.id.inputEditSurname)
                Validation.validateText(surnameEdit);
            else if (view.getId() == R.id.inputEditStreet)
                Validation.validateLettersAndNumber(addressEdit);
            else if (view.getId() == R.id.inputEditCity)
                Validation.validateText(cityEdit);
            else if (view.getId() == R.id.inputEditCountry)
                Validation.validateText(countryEdit);
            else if (view.getId() == R.id.inputEditPhoneNumber)
                Validation.validatePhone(phoneNumberEdit);
            else if (view.getId() == R.id.inputEditConfirmPassword)
                Validation.validateConfirmedPassword(confirmEdit, passwordEdit);
        }
    }
}