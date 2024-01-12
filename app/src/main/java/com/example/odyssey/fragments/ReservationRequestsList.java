package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.AccreditReservation;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationRequestsList extends Fragment {

    View v;
    TextView status;
    boolean[] selectedLanguage;
    ArrayList<Integer> statusList = new ArrayList<>();
    String[] statusArray = {"Requested", "Declined", "Cancelled request"};
    String date1 = "", date2 = "";
    List<AccreditReservation> requests = new ArrayList<>();
    Button search;
    MaterialButton dateSelect;
    TextInputEditText title;

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
        title = v.findViewById(R.id.inputEditTitle);
        dateSelect = v.findViewById(R.id.selectDateButton);
        search = v.findViewById(R.id.searchBtn);
        selectedLanguage = new boolean[statusArray.length];

        dateSelect.setOnClickListener(c -> {
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
            )).build();
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                date2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.second));
                dateSelect.setText(date1 + " / " + date2);
            });

            materialDatePicker.show(getChildFragmentManager(), "tag");
        });

        search.setOnClickListener(c -> {
            getRequests();
            date1 = "";
            date2="";
            dateSelect.setText("SELECT DATE");
        });

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Long starting =  date1.equals("") ? null : LocalDate.parse(date1, formatter).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()*1000;
        Long ending = date1.equals("") ? null : LocalDate.parse(date2, formatter).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()*1000;

        if (starting!=null && ending!=null){
            Log.d("START", starting.toString());
            Log.d("END", ending.toString());
        }

        String titleText = title.getText().equals("") ? null : title.getText().toString().toUpperCase();

        List<String> statuses = new ArrayList<>();

        for (int j = 0; j < statusList.size(); j++) {
            switch(statusArray[statusList.get(j)]) {
                case "Requested":
                    statuses.add("REQUESTED");
                    break;
                case "Declined":
                    statuses.add("DECLINED");
                    break;
                case "Cancelled request":
                    statuses.add("CANCELLED_REQUEST");
                    break;
            }

        }

        if(statuses.isEmpty()){
            statuses.add("REQUESTED");
            statuses.add("DECLINED");
            statuses.add("CANCELLED_REQUEST");
        }

        Call<List<AccreditReservation>> call = ClientUtils.reservationService.getReservationsByHost(
                TokenUtils.getId(),
                titleText,
                statuses,
                starting,
                ending
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