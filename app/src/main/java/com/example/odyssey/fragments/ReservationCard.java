package com.example.odyssey.fragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.reservations.AccreditReservation;

import java.time.format.DateTimeFormatter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationCard extends LinearLayout {

    private AccreditReservation reservation;

    TextView title, status, guest, guestNumber, price, startDate, endDate;
    LinearLayout statusLayout, buttonSection;

    Button accept, decline, cancel, report;
    Boolean hostMode;

    public ReservationCard(Boolean hostMode, Context context) {
        super(context);
        this.hostMode = hostMode;
        init();
    }

    public void setReservation(AccreditReservation reservation) {
        this.reservation = reservation;
        if (reservation != null) {
            if (reservation.getAccommodation().getTitle() != null)
                title.setText(reservation.getAccommodation().getTitle());

            if (reservation.getStatus() != null) {
                switch (reservation.getStatus()) {
                    case REQUESTED:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.pending_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.pending));
                        status.setText("REQUESTED");
                        break;
                    case ACCEPTED:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.approved_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.approved));
                        status.setText("ACCEPTED");
                        break;
                    case DECLINED:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.cancelled_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.cancelled));
                        status.setText("DECLINED");
                        buttonSection.setVisibility(View.GONE);
                        break;
                    case CANCELLED_RESERVATION:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.zirko_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.zirko_siva));
                        status.setText("CANCELLED RESERVATION");
                        buttonSection.setVisibility(View.GONE);
                        break;
                    case CANCELLED_REQUEST:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.zirko_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.zirko_siva));
                        status.setText("CANCELLED REQUEST");
                        buttonSection.setVisibility(View.GONE);
                        break;
                }
            }

            if (reservation.getGuest() != null)
                guest.setText(reservation.getGuest().getName() + " " + reservation.getGuest().getSurname());

            if (reservation.getPrice() != null)
                price.setText(reservation.getPrice().toString());

            if (reservation.getGuestNumber() != null)
                guestNumber.setText(reservation.getGuestNumber().toString());

            if (reservation.getStart() != null)
                startDate.setText(reservation.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            if (reservation.getEnd() != null)
                endDate.setText(reservation.getEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        if (reservation.getStatus().equals(AccreditReservation.Status.REQUESTED))
            cancel.setVisibility(GONE);
        else {
            accept.setVisibility(GONE);
            decline.setVisibility(GONE);
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_reservation_request_card, this, true);

        title = findViewById(R.id.accommodationTitleTextView);
        status = findViewById(R.id.status);
        statusLayout = findViewById((R.id.statusLayout));
        buttonSection = findViewById(R.id.buttonSection);
        guest = findViewById(R.id.guestName);
        findViewById(R.id.cancellationNumber).setVisibility(GONE);
        findViewById(R.id.cancellationLabel).setVisibility(GONE);
        price = findViewById(R.id.totalPrice);
        guestNumber = findViewById(R.id.guestNumber);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        accept = findViewById(R.id.buttonAccept);
        decline = findViewById(R.id.buttonDecline);
        cancel = findViewById(R.id.buttonCancel);

        accept.setOnClickListener(c -> {
            Call<ResponseBody> call = ClientUtils.reservationService.updateStatus(reservation.getId(), "ACCEPTED");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Reservation accepted", Toast.LENGTH_LONG).show();
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.approved_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.approved));
                        status.setText("ACCEPTED");
                        buttonSection.setVisibility(View.GONE);
                        Log.d("ReservationCard", "ACCEPTED");
                    } else {
                        String message = ClientUtils.getError(response, "Reservation could not be cancelled");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        Log.d("ReservationCard", "not ACCEPTED");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("ReservationCard", "Reservation could not be accepted: " + t.getMessage());
                }
            });
        });

        decline.setOnClickListener(c -> {
            Call<ResponseBody> call = ClientUtils.reservationService.updateStatus(reservation.getId(), "ACCEPTED");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Reservation declined", Toast.LENGTH_LONG).show();
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.cancelled_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.cancelled));
                        status.setText("DECLINED");
                        buttonSection.setVisibility(View.GONE);
                        Log.d("ReservationCard", "DECLINED");
                    } else {
                        String message = ClientUtils.getError(response, "Reservation could not be cancelled");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        Log.d("ReservationCard", "not DECLINED");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e("ReservationCard", "Reservation could not be declined: " + t.getMessage());
                }
            });
        });

        cancel.setOnClickListener(c -> {
            String statusText = reservation.getStatus().equals(AccreditReservation.Status.ACCEPTED) ?
                    "CANCELLED_RESERVATION" : "CANCELLED_REQUEST";
            Call<ResponseBody> call = ClientUtils.reservationService.updateStatus(reservation.getId(), statusText);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Reservation cancelled", Toast.LENGTH_LONG).show();
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.zirko_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.zirko_siva));
                        status.setText(statusText);
                        buttonSection.setVisibility(View.GONE);
                    } else {
                        String message = ClientUtils.getError(response, "Reservation " +
                                (statusText.equals("CANCELLED_RESERVATION") ? "" : "request ")
                                + "could not be cancelled");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        Log.d("ReservationCard", "CANCELLED");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e("ReservationCard", "Reservation could not be cancelled: " + t.getMessage());
                }
            });
        });

        if (hostMode) cancel.setVisibility(GONE);
        else {
            accept.setVisibility(GONE);
            decline.setVisibility(GONE);
        }
    }
}