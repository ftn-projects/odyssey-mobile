package com.example.odyssey.fragments.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.fragments.AccommodationReviewCard;
import com.example.odyssey.fragments.review.ReviewSectionFragment;
import com.example.odyssey.model.accommodations.AvailabilitySlot;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.HostReview;
import com.example.odyssey.model.users.User;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    public static final String ARG_USER_ID = "user_id";
    private TextView nameView, roleView, streetView, cityView, countryView, phoneView, emailView, bioView;
    private TextInputEditText reviewInput;
    private LinearLayout bioSection;
    private LinearLayout reviewList;
    private RatingBar ratingBar;
    private User user, loggedIn;
    private boolean isLogged = true;
    private Long userId;
    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long loggedInId = TokenUtils.getId(requireContext()), id = loggedInId;
        if (getArguments() != null && getArguments().containsKey(ARG_USER_ID))
            id = getArguments().getLong(ARG_USER_ID);

        userId = id;
        isLogged = loggedInId != null && loggedInId.equals(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        nameView = v.findViewById(R.id.profile_name);
        roleView = v.findViewById(R.id.profile_role);
        streetView = v.findViewById(R.id.profile_street);
        cityView = v.findViewById(R.id.profile_city);
        countryView = v.findViewById(R.id.profile_country);
        phoneView = v.findViewById(R.id.profile_phone);
        emailView = v.findViewById(R.id.profile_email);
        bioView = v.findViewById(R.id.profile_bio);
        bioSection = v.findViewById(R.id.profile_bio_section);

        Button reportButton = v.findViewById(R.id.report_button);
        reportButton.setVisibility(isLogged || TokenUtils.getId(requireContext()) == null ? View.GONE : View.VISIBLE);
        reportButton.setOnClickListener(v1 -> submitReport());
        Button editButton = v.findViewById(R.id.edit_button);
        editButton.setVisibility(isLogged ? View.VISIBLE : View.GONE);
        editButton.setOnClickListener(c -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigate(R.id.nav_account, bundle);
        });

        String imagePath = ClientUtils.SERVICE_API_PATH + "users/image/" + userId;
        ImageView profileImage = v.findViewById(R.id.profile_image);
        Glide.with(requireContext()).load(imagePath).into(profileImage);
        v.findViewById(R.id.profile_address_section).setVisibility(isLogged ? View.VISIBLE : View.GONE);

        loadData(userId);
        getLoggedIn();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addReviewSection();
    }

    private void addReviewSection(){
        LinearLayout reviewSectionContainer = getView().findViewById(R.id.profile_fragment_reviews_section_container);
        ReviewSectionFragment reviewSectionFragment = new ReviewSectionFragment();
        Bundle args = new Bundle();
        args.putSerializable("id", userId);
        args.putSerializable("type", "HOST");
        reviewSectionFragment.setArguments(args);
        getChildFragmentManager().beginTransaction()
                .add(reviewSectionContainer.getId(), reviewSectionFragment)
                .commit();
    }

    private void loadData(User user) {
        String displayName = user.getName() + " " + user.getSurname();
        nameView.setText(displayName);
        roleView.setText(user.getRole());
        streetView.setText(user.getAddress().getStreet());
        cityView.setText(user.getAddress().getCity());
        countryView.setText(user.getAddress().getCountry());
        phoneView.setText(user.getPhone());
        emailView.setText(user.getEmail());
        bioView.setText(user.getBio());

        bioSection.setVisibility(user.getRole().equals("HOST") ? View.VISIBLE : View.GONE);
    }



    private void submitReport() {
        Bundle args = new Bundle();
        args.putLong("reported", userId);
        Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigate(R.id.nav_report, args);
    }

    private void loadData(Long id) {
        ClientUtils.userService.findById(id).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "User could not be fetched.", Toast.LENGTH_LONG).show();
                    Log.e("ProfileFragment", "User could not be fetched: " + response.code());
                    return;
                }

                User loaded = response.body();
                if (loaded == null) return;
                user = loaded;
                loadData(loaded);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("ProfileFragment", "User could not be fetched: " + t.getMessage());
            }
        });
    }

    private void getLoggedIn() {
        if (TokenUtils.getId(requireContext()) == null) return;

        ClientUtils.userService.findById(TokenUtils.getId(requireContext())).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "User could not be fetched.", Toast.LENGTH_LONG).show();
                    Log.e("ProfileFragment", "User could not be fetched: " + response.code());
                    return;
                }

                User loaded = response.body();
                if (loaded == null) return;
                loggedIn = loaded;
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("ProfileFragment", "User could not be fetched: " + t.getMessage());
            }
        });
    }
}