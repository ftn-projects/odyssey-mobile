package com.example.odyssey.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.reports.UserReport;
import com.example.odyssey.model.reservations.AccreditReservation;
import com.example.odyssey.model.users.User;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportPopupDialog extends DialogFragment {

    private Long reported;
    String name;
    List<AccreditReservation> requests = new ArrayList<>();
    public ReportPopupDialog(Long reported){this.reported = reported;}

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_dialog, container, false);

        TextView reporter = view.findViewById(R.id.reporterName);
        getNameAndSurname(TokenUtils.getId(), reporter);

        TextView reporting = view.findViewById(R.id.reportingName);
        getNameAndSurname(reported, reporting);

        EditText description = view.findViewById(R.id.edit_text);

        Button report = view.findViewById(R.id.buttonReport);
        report.setOnClickListener(v -> {
            UserReport rep  = new UserReport(description.getText().toString(), TokenUtils.getId(), reported);
           makeReport(rep);
        });

        MaterialButton close = view.findViewById(R.id.report_close_button);
        close.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void getNameAndSurname(Long id, TextView text){
        Call<User> getUserCall = ClientUtils.userService.findById(id);
        getUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User found = response.body();
                    text.setText(found.getName() + " " +found.getSurname());
                } else {
                    name = "";
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    public void makeReport(UserReport rep){
        Long host = TokenUtils.getRole().equals("HOST") ? TokenUtils.getId() : reported;
        Long guest = TokenUtils.getRole().equals("GUEST") ? TokenUtils.getId() : reported;
        boolean isHost = host.equals(TokenUtils.getId());
        List<String> statuses = new ArrayList<>();
        statuses.add("ACCEPTED");

        if(isHost){
            Call<List<AccreditReservation>> call = ClientUtils.reservationService.getReservationsByHost(
                    host,
                    null,
                    statuses,
                    null,
                    null
            );

            call.enqueue(new Callback<List<AccreditReservation>>() {
                @Override
                public void onResponse(Call<List<AccreditReservation>> call, Response<List<AccreditReservation>> response) {
                    if(response.isSuccessful()){
                        requests = response.body();
                        boolean sent = false;
                        for(AccreditReservation request: requests){
                            if (request.getGuest().getId().equals(guest) && !request.getStart().isAfter(LocalDate.now())) {
                                sendReport(rep);
                                sent = true;
                                break;
                            }
                        }

                        if(!sent) Toast.makeText(requireActivity(),"You can't report this user.",Toast.LENGTH_LONG).show();
                    }else{
                        Log.d("REZ",response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<AccreditReservation>> call, Throwable t) {
                    Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
                }
            });
        }else{
            Call<List<AccreditReservation>> call = ClientUtils.reservationService.getReservationsByGuest(
                    guest,
                    null,
                    statuses,
                    null,
                    null
            );

            call.enqueue(new Callback<List<AccreditReservation>>() {
                @Override
                public void onResponse(Call<List<AccreditReservation>> call, Response<List<AccreditReservation>> response) {
                    if(response.isSuccessful()){
                        requests = response.body();
                        boolean sent = false;
                        for(AccreditReservation request: requests){
                            if (request.getAccommodation().getHost().getId().equals(host) && !request.getStart().isAfter(LocalDate.now())) {
                                sendReport(rep);
                                sent = true;
                                break;
                            }
                        }

                        if(!sent) Toast.makeText(requireActivity(),"You can't report this user.",Toast.LENGTH_LONG).show();
                    }else{
                        Log.d("REZ",response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<AccreditReservation>> call, Throwable t) {
                    Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
                }
            });
        }

    }

    private void sendReport(UserReport rep){
        Call<Void> call = ClientUtils.reportService.reportUser(rep);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("AAA",response.code() + "aaa");
                if(response.code() == 201){
                    dismiss();
                    Toast.makeText(requireActivity(),"Your report has been sent.",Toast.LENGTH_LONG).show();
                }else{
                    Log.d("REZ",response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("FAILED", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}
