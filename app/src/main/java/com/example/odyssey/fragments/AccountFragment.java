package com.example.odyssey.fragments;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.Address;
import com.example.odyssey.model.users.User;
import com.example.odyssey.model.users.PasswordUpdate;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    User user = new User();
    ActivityResultLauncher<Intent> launchImagePickerActivity;
    TextInputEditText nameInput, surnameInput, streetInput, cityInput, countryInput, phoneInput, emailInput, oldPasswordInput, newPasswordInput, confirmPasswordInput;
    Button updateImageButton, updateDetailsButton, updatePasswordButton, deactivateAccountButton;

    public AccountFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("(¬‿¬)", "ProfileFragment onCreate()");

        launchImagePickerActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    uploadImage(data.getData());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        updateImageButton = v.findViewById(R.id.btnUpdateImage);
        updateDetailsButton = v.findViewById(R.id.btnUpdateDetails);
        updatePasswordButton = v.findViewById(R.id.btnUpdatePassword);
        deactivateAccountButton = v.findViewById(R.id.btnDeactivate);

        getCurrentUser();

        updateImageButton.setOnClickListener(v1 -> updateImage());
        updateDetailsButton.setOnClickListener(v1 -> updateDetails());
        updatePasswordButton.setOnClickListener(v1 -> updatePassword());
        deactivateAccountButton.setOnClickListener(v1 -> deactivateAccount());
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        initViewMode(menu, inflater);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initViewMode(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.edit_menu, menu);
        menu.findItem(R.id.nav_edit_action).setOnMenuItemClickListener(item -> {
            Toast.makeText(requireActivity(), "EDITING", Toast.LENGTH_SHORT).show();
            initEditMode(menu, inflater);
            return true;
        });

        setEditingVisibility(View.GONE);
    }

    private void initEditMode(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.cancel_menu, menu);
        menu.findItem(R.id.nav_cancel_action).setOnMenuItemClickListener(item1 -> {
            Toast.makeText(requireActivity(), "RESET CHANGES", Toast.LENGTH_SHORT).show();
            resetFields();
            onCreateOptionsMenu(menu, inflater);
            return true;
        });

        setEditingVisibility(View.VISIBLE);
    }

    private void setEditingVisibility(int visibility) {
//        updateImageButton.setVisibility(visibility);
        updateDetailsButton.setVisibility(visibility);
        updatePasswordButton.setVisibility(visibility);
        deactivateAccountButton.setVisibility(visibility);

        boolean editable = visibility == View.VISIBLE;
        if (!editable) oldPasswordInput.setText("***********");
        else oldPasswordInput.setText("");

        nameInput.setFocusableInTouchMode(editable);
        surnameInput.setFocusableInTouchMode(editable);
        streetInput.setFocusableInTouchMode(editable);
        cityInput.setFocusableInTouchMode(editable);
        countryInput.setFocusableInTouchMode(editable);
        phoneInput.setFocusableInTouchMode(editable);
        emailInput.setFocusableInTouchMode(editable);
        oldPasswordInput.setFocusableInTouchMode(editable);
        newPasswordInput.setFocusableInTouchMode(editable);
        confirmPasswordInput.setFocusableInTouchMode(editable);

        newPasswordInput.setVisibility(visibility);
        confirmPasswordInput.setVisibility(visibility);
    }

    private void getCurrentUser() {
        ClientUtils.userService.findById(TokenUtils.getId()).enqueue(userRetrieval);
    }

    private void reloadFragment() {
        NavController controller = Navigation.findNavController(requireActivity(), R.id.fragment_container_main);
        int id = controller.getCurrentDestination().getId();
        controller.popBackStack(id, true);
        controller.navigate(id);
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
                    Log.e("ACCOUNT", response.message() != null ? response.message() : "error");
                    return;
                }
                reloadFragment();
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
        user.setName(nameInput.getText().toString());
        user.setSurname(nameInput.getText().toString());
        user.setAddress(new Address(
                streetInput.getText().toString(),
                cityInput.getText().toString(),
                countryInput.getText().toString()
        ));
        user.setPhone(phoneInput.getText().toString());
        user.setEmail(emailInput.getText().toString());
        ClientUtils.userService.update(user).enqueue(userRetrieval);
        reloadFragment();
    }

    private void updatePassword() {
        if (!newPasswordInput.getText().toString().equals(confirmPasswordInput.getText().toString())) {
            Toast.makeText(requireActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        PasswordUpdate passwordUpdate = new PasswordUpdate(
                user.getId(),
                oldPasswordInput.getText().toString(),
                newPasswordInput.getText().toString()
        );
        ClientUtils.userService.updatePassword(passwordUpdate).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("ACCOUNT", response.message() != null ? response.message() : "error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("ACCOUNT", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
        reloadFragment();
    }

    private void deactivateAccount() {
        ClientUtils.userService.deactivate(user.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("ACCOUNT", response.message() != null ? response.message() : "error");
                    return;
                }

                TokenUtils.removeToken();
                Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigate(R.id.nav_home);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("ACCOUNT", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
        reloadFragment();
    }

    private void resetFields() {
        nameInput.setText(user.getName());
        surnameInput.setText(user.getSurname());
        streetInput.setText(user.getAddress().getStreet());
        cityInput.setText(user.getAddress().getCity());
        countryInput.setText(user.getAddress().getCountry());
        phoneInput.setText(user.getPhone());
        emailInput.setText(user.getEmail());
    }

    Callback<User> userRetrieval = new Callback<User>() {
        @Override
        public void onResponse(@NonNull Call<User> call, Response<User> response) {
            if (!response.isSuccessful()) {
                Log.e("ACCOUNT", response.message() != null ? response.message() : "error");
                return;
            }
            user = response.body();
            resetFields();
        }

        @Override
        public void onFailure(@NonNull Call<User> call, Throwable t) {
            Log.e("ACCOUNT", t.getMessage() != null ? t.getMessage() : "error");
        }
    };
}