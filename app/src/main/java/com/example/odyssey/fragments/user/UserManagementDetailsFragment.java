package com.example.odyssey.fragments.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.adapters.UserReportAdapter;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.users.UserWithReports;
import com.example.odyssey.utils.TokenUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementDetailsFragment extends Fragment {
    public static final String ARG_USER_ID = "user";
    private TextView nameView, roleView, statusView, streetView, cityView, countryView, phoneView, emailView, bioView;
    ListView reportsList;
    Button activateBtn, deactivateBtn;
    private LinearLayout bioSection;
    private UserWithReports user;
    private Boolean isAdmin = false;

    public UserManagementDetailsFragment() {
    }

    public static UserManagementDetailsFragment newInstance() {
        return new UserManagementDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_USER_ID))
            user = (UserWithReports) getArguments().getSerializable(ARG_USER_ID);
        if (user == null)
            throw new RuntimeException("User is null");

        isAdmin = TokenUtils.getId(requireContext()).equals(user.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_management_details, container, false);

        nameView = v.findViewById(R.id.profile_name);
        roleView = v.findViewById(R.id.profile_role);
        statusView = v.findViewById(R.id.profile_status);
        streetView = v.findViewById(R.id.profile_street);
        cityView = v.findViewById(R.id.profile_city);
        countryView = v.findViewById(R.id.profile_country);
        phoneView = v.findViewById(R.id.profile_phone);
        emailView = v.findViewById(R.id.profile_email);
        bioView = v.findViewById(R.id.profile_bio);
        bioSection = v.findViewById(R.id.profile_bio_section);
        bioSection.setVisibility(user.getRole().equals("HOST") ? View.VISIBLE : View.GONE);
        reportsList = v.findViewById(R.id.reports_list);

        activateBtn = v.findViewById(R.id.activate_button);
        deactivateBtn = v.findViewById(R.id.deactivate_button);
        activateBtn.setOnClickListener(b -> updateStatus(UserWithReports.AccountStatus.ACTIVE));
        deactivateBtn.setOnClickListener(b -> updateStatus(UserWithReports.AccountStatus.BLOCKED));

        ImageView profileImage = v.findViewById(R.id.profile_image);
        String imagePath = ClientUtils.SERVICE_API_PATH + "users/image/" + user.getId();
        Glide.with(requireContext()).load(imagePath).into(profileImage);

        loadData();
        return v;
    }

    private void loadData() {
        String displayName = user.getName() + " " + user.getSurname();
        if (isAdmin) displayName += " (You)";
        nameView.setText(displayName);
        statusView.setText("Status: " + user.getStatus().toString());
        roleView.setText("Role: " + user.getRole());
        streetView.setText(user.getAddress().getStreet());
        cityView.setText(user.getAddress().getCity());
        countryView.setText(user.getAddress().getCountry());
        phoneView.setText(user.getPhone());
        emailView.setText(user.getEmail());
        bioView.setText(user.getBio());

        bioSection.setVisibility(user.getRole().equals("HOST") ? View.VISIBLE : View.GONE);
        reportsList.setAdapter(new UserReportAdapter(requireContext(), user.getReports()));

        activateBtn.setEnabled(!isAdmin && user.getStatus().equals(UserWithReports.AccountStatus.BLOCKED));
        deactivateBtn.setEnabled(!isAdmin && user.getStatus().equals(UserWithReports.AccountStatus.ACTIVE));
    }

    public void updateStatus(UserWithReports.AccountStatus status) {
        String actionText = status.equals(UserWithReports.AccountStatus.ACTIVE) ? "activated" : "blocked";
        Call<ResponseBody> call;

        if (status.equals(UserWithReports.AccountStatus.ACTIVE))
            call = ClientUtils.userService.activate(user.getId());
        else if (status.equals(UserWithReports.AccountStatus.BLOCKED))
            call = ClientUtils.userService.block(user.getId());
        else throw new RuntimeException("Invalid status update: " + status.toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String error = ClientUtils.getError(response, "User could not be " + actionText);
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    Log.e("UserManagementDetailsFragment", "User status could not be updated: " + response.code());
                    return;
                }
                Toast.makeText(getContext(), "User " + actionText, Toast.LENGTH_LONG).show();
                refreshUser();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("UserManagementDetailsFragment", "User status could not be updated: " + t.getMessage());
            }
        });
    }

    private void refreshUser() {
        ClientUtils.reportService.findUserWithReportById(user.getId()).enqueue(new Callback<UserWithReports>() {
            @Override
            public void onResponse(@NonNull Call<UserWithReports> call, @NonNull Response<UserWithReports> response) {
                if (!response.isSuccessful()) {
                    String error = ClientUtils.getError(response, "User could not be fetched");
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    Log.e("UserManagementDetailsFragment", "User could not be fetched: " + response.code());
                    return;
                }

                UserWithReports loaded = response.body();
                if (loaded == null) return;
                user = loaded;
                loadData();
            }

            @Override
            public void onFailure(@NonNull Call<UserWithReports> call, @NonNull Throwable t) {
                Log.e("ProfileFragment", "User could not be fetched: " + t.getMessage());
            }
        });
    }
}