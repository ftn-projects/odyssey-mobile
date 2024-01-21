package com.example.odyssey.fragments.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.Address;
import com.example.odyssey.model.users.PasswordUpdate;
import com.example.odyssey.model.users.User;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    public static final String ARG_USER = "user";
    User user = null;
    TextInputEditText nameInput, surnameInput, streetInput, cityInput, countryInput, phoneInput, emailInput, oldPasswordInput, newPasswordInput, confirmPasswordInput;
    Button updateImageBtn, updateDetailsBtn, updatePasswordBtn, deactivateBtn;

    public AccountFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_USER))
            user = (User) getArguments().getSerializable(ARG_USER);
        else throw new RuntimeException("User is null");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        setHasOptionsMenu(true);

        nameInput = v.findViewById(R.id.field_input_name);
        surnameInput = v.findViewById(R.id.field_input_surname);
        streetInput = v.findViewById(R.id.field_input_street);
        cityInput = v.findViewById(R.id.field_input_city);
        countryInput = v.findViewById(R.id.field_input_country);
        phoneInput = v.findViewById(R.id.field_input_phone);
        emailInput = v.findViewById(R.id.field_input_email);
        oldPasswordInput = v.findViewById(R.id.field_input_old_password);
        newPasswordInput = v.findViewById(R.id.field_input_new_password);
        confirmPasswordInput = v.findViewById(R.id.field_input_confirm_password);

        updateImageBtn = v.findViewById(R.id.btnUpdateImage);
        updateDetailsBtn = v.findViewById(R.id.btnUpdateDetails);
        updatePasswordBtn = v.findViewById(R.id.btnUpdatePassword);
        deactivateBtn = v.findViewById(R.id.btnDeactivate);

        updateImageBtn.setOnClickListener(v1 -> updateImage());
        updateDetailsBtn.setOnClickListener(v1 -> updateDetails());
        updatePasswordBtn.setOnClickListener(v1 -> updatePassword());
        deactivateBtn.setOnClickListener(v1 -> deactivateAccount());

        loadData();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.cancel_menu, menu);
        menu.findItem(R.id.nav_cancel_action).setOnMenuItemClickListener(item -> {
            closeFragment();
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void loadData() {
        nameInput.setText(user.getName());
        surnameInput.setText(user.getSurname());
        streetInput.setText(user.getAddress().getStreet());
        cityInput.setText(user.getAddress().getCity());
        countryInput.setText(user.getAddress().getCountry());
        phoneInput.setText(user.getPhone());
        emailInput.setText(user.getEmail());
    }

    private void closeFragment() {
        Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigateUp();
    }

    private void updateImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchImagePickerActivity.launch(i);
    }

    private void uploadImage(Uri uri) {
        String path = getImagePath(requireContext(), uri);
        if (path == null) return;

        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        ClientUtils.userService.uploadImage(user.getId(), imagePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("ACCOUNT", response.message());
                    return;
                }
                closeFragment();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("ACCOUNT", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    public static String getImagePath(Context context, Uri uri) {
        String path = null;
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (cursor.moveToFirst()) {
                    path = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private void updateDetails() {
        user.setName(Objects.requireNonNull(nameInput.getText()).toString());
        user.setSurname(nameInput.getText().toString());
        user.setAddress(new Address(
                Objects.requireNonNull(streetInput.getText()).toString(),
                Objects.requireNonNull(cityInput.getText()).toString(),
                Objects.requireNonNull(countryInput.getText()).toString()
        ));
        user.setPhone(Objects.requireNonNull(phoneInput.getText()).toString());
        user.setEmail(Objects.requireNonNull(emailInput.getText()).toString());
    }

    private void updatePassword() {
        String newPassword = Objects.requireNonNull(newPasswordInput.getText()).toString();
        String confirmPassword = Objects.requireNonNull(confirmPasswordInput.getText()).toString();

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(requireActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        PasswordUpdate passwordUpdate = new PasswordUpdate(user.getId(),
                Objects.requireNonNull(oldPasswordInput.getText()).toString(), newPassword);

        ClientUtils.userService.updatePassword(passwordUpdate).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("ACCOUNT", response.message());
                    return;
                }
                Toast.makeText(requireActivity(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                closeFragment();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("ACCOUNT", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void deactivateAccount() {
        ClientUtils.userService.deactivate(user.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("ACCOUNT", response.message());
                    return;
                }
                Toast.makeText(requireActivity(), "Account deactivated!", Toast.LENGTH_SHORT).show();
                TokenUtils.removeToken();
                Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigate(R.id.nav_home);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("ACCOUNT", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private final ActivityResultLauncher<Intent> launchImagePickerActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        uploadImage(data.getData());
                    }
                }
            });
    ;
}