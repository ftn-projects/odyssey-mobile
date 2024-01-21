package com.example.odyssey.fragments.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.odyssey.R;
import com.example.odyssey.adapters.UserManagementAdapter;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.users.UserWithReports;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementFragment extends Fragment {
    UserManagementAdapter adapter;
    ListView usersView;
    List<UserWithReports> users = new ArrayList<>();
    String search;
    Boolean blocked = null, reported = false;

    public UserManagementFragment() {
    }

    public static UserManagementFragment newInstance() {
        return new UserManagementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_management, container, false);

        usersView = v.findViewById(R.id.users_list);

        adapter = new UserManagementAdapter(requireContext(), v, new ArrayList<>());
        usersView.setAdapter(adapter);

        MaterialButton allBtn = v.findViewById(R.id.requested_toggle);
        MaterialButton activeBtn = v.findViewById(R.id.active_toggle);
        MaterialButton blockedBtn = v.findViewById(R.id.blocked_toggle);
        MaterialButton reportedBtn = v.findViewById(R.id.reported_toggle);
        allBtn.setOnClickListener(btnView -> {
            blocked = null;
            filter();
        });
        allBtn.setChecked(blocked == null);
        activeBtn.setOnClickListener(v1 -> {
            blocked = false;
            filter();
        });
        blockedBtn.setOnClickListener(btnView -> {
            blocked = true;
            filter();
        });
        reportedBtn.setOnClickListener(btnView -> {
            reported = !reported;
            filter();
        });
        reportedBtn.setChecked(reported);
        TextInputEditText searchInput = v.findViewById(R.id.input_search);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search = s.toString().toLowerCase();
                filter();
            }
        });

        updateData();
        filter();
        return v;
    }

    private void filter() {
        List<UserWithReports> filtered = users.stream().filter(u -> (blocked == null ||
                !blocked && u.getStatus().equals(UserWithReports.AccountStatus.ACTIVE) ||
                blocked && u.getStatus().equals(UserWithReports.AccountStatus.BLOCKED)) &&
                (reported == (u.getReports().size() != 0)) &&
                (search == null || (u.getName() + " " + u.getSurname()).toLowerCase().contains(search))
        ).collect(Collectors.toList());

        adapter.clear();
        adapter.addAll(filtered);
    }

    private void updateData() {
        users.clear();
        ClientUtils.reportService.getAllUsers().enqueue(new Callback<List<UserWithReports>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserWithReports>> call, @NonNull Response<List<UserWithReports>> response) {
                if (response.isSuccessful()) {
                    List<UserWithReports> updated = response.body();
                    if (updated != null) {
                        updated.sort((n1, n2) -> n2.getId().compareTo(n1.getId()));
                        users.clear();
                        users.addAll(updated);
                        adapter.addAll(updated);
                    }
                } else {
                    String error = ClientUtils.getError(response, "Users could not be fetched");
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    Log.e("UserManagementFragment", "updateData(): " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserWithReports>> call, @NonNull Throwable t) {
                Log.e("UserManagementFragment", t.getMessage() == null ? "Failed getting unread notifications" : t.getMessage());
            }
        });
    }
}