package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.AccreditReservation;
import com.example.odyssey.utils.TokenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationRequestsList extends Fragment {

    View v;
    TextView status;
    boolean[] selectedLanguage;
    ArrayList<Integer> statusList = new ArrayList<>();
    String[] statusArray = {"Requested", "Declined", "Cancelled request"};
    List<AccreditReservation> requests = new ArrayList<>();
    public ReservationRequestsList() {
    }

    public static ReservationRequestsList newInstance() {
        return new ReservationRequestsList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_reservation_requests_list, container, false);

        status = v.findViewById(R.id.statusDropDown);
        selectedLanguage = new boolean[statusArray.length];

        status.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            builder.setTitle("Select statuses");

            builder.setCancelable(false);

            builder.setMultiChoiceItems(statusArray, selectedLanguage, (dialogInterface, i, b) -> {
                if (b) {
                    statusList.add(i);
                    Collections.sort(statusList);
                } else {
                    statusList.remove(Integer.valueOf(i));
                }
            });

            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < statusList.size(); j++) {
                    stringBuilder.append(statusArray[statusList.get(j)]);
                    if (j != statusList.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                for (int j = 0; j < selectedLanguage.length; j++) {
                    selectedLanguage[j] = false;
                    statusList.clear();
                }
            });
            builder.show();
        });

        getRequests();

        return v;
    }

    public void getRequests() {
        List<String> statuses = new ArrayList<>();
        statuses.add("REQUESTED");
        statuses.add("DECLINED");
        statuses.add("CANCELLED_REQUEST");

        Call<List<AccreditReservation>> call = ClientUtils.reservationService.getReservationsByHost(
                TokenUtils.getId(),
                null,
                statuses,
                null,
                null
        );

        call.enqueue(new Callback<List<AccreditReservation>>() {
            @Override
            public void onResponse(Call<List<AccreditReservation>> call, Response<List<AccreditReservation>> response) {
                if(response.code() == 200){
                    LinearLayout cardHolder = v.findViewById(R.id.holder);
                    cardHolder.removeAllViews();
                    requests = response.body();
                    for(AccreditReservation request: requests){
                        ReservationRequestCard card = new ReservationRequestCard(requireContext());
                        card.setReservationRequest(request);
                        cardHolder.addView(card);
                    }
                }else{
                    Log.d("FUCK",response.message());
                }
            }

            @Override
            public void onFailure(Call<List<AccreditReservation>> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}