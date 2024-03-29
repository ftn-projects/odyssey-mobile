package com.example.odyssey.fragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.reservations.AccreditReservation;
import com.example.odyssey.utils.ReservationsListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestsReservationsCard extends LinearLayout {

    private AccreditReservation reservation;
    TextView title, status, host, cancelBy, price, startDate, endDate, guestNumber;

    LinearLayout statusLayout, buttonSection;
    Button cancel;

    public GuestsReservationsCard(Context context, GuestsReservationsList parentFragment) {
        super(context);
        init(parentFragment);
    }

    public void setReservation(AccreditReservation reservation) {
        this.reservation = reservation;
        if (reservation != null) {
            if (reservation.getAccommodation().getTitle() != null)
                title.setText(reservation.getAccommodation().getTitle());

            if (reservation.getStatus() != null) {
                switch (reservation.getStatus()) {
                    case ACCEPTED:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.approved_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.approved));
                        status.setText("ACCEPTED");
                        break;
                    case REQUESTED:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.pending_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.pending));
                        status.setText("REQUESTED");
                        break;
                    case DECLINED:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.cancelled_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.cancelled));
                        status.setText("DECLINED");
                        buttonSection.setVisibility(View.GONE);
                        break;
                    case CANCELLED_REQUEST:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.zirko_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.zirko_siva));
                        status.setText("CANCELLED REQUEST");
                        buttonSection.setVisibility(View.GONE);
                        break;
                    case CANCELLED_RESERVATION:
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.zirko_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.zirko_siva));
                        status.setText("CANCELLED RESERVATION");
                        buttonSection.setVisibility(View.GONE);
                        break;
                }
            }

            if (reservation.getGuest() != null)
                host.setText(reservation.getAccommodation().getHost().getName() + " " + reservation.getAccommodation().getHost().getSurname());

            Log.d("DAYS", reservation.getAccommodation().getCancellationDue() + ".");
            if (reservation.getStart() != null && reservation.getAccommodation().getCancellationDue() != null) {
                LocalDate due = reservation.getStart().minusDays(reservation.getAccommodation().getCancellationDue());
                cancelBy.setText(due.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                if (!due.isAfter(LocalDate.now()))
                    cancel.setEnabled(false);
            }

            if (reservation.getPrice() != null)
                price.setText(reservation.getPrice().toString());

            if (reservation.getGuestNumber() != null)
                guestNumber.setText(reservation.getGuestNumber().toString());

            if (reservation.getStart() != null)
                startDate.setText(reservation.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            if (reservation.getEnd() != null)
                endDate.setText(reservation.getEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

    }

    private void init(GuestsReservationsList parentFragment) {
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_guests_reservations_card, this, true);

        title = findViewById(R.id.accommodationTitleTextView);
        status = findViewById(R.id.status);
        statusLayout = findViewById((R.id.statusLayout));
        buttonSection = findViewById(R.id.buttonSection);
        host = findViewById(R.id.hostName);
        cancelBy = findViewById(R.id.cancelBy);
        price = findViewById(R.id.totalPrice);
        guestNumber = findViewById(R.id.guestNumber);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        cancel = findViewById(R.id.buttonCancel);

        cancel.setOnClickListener(c -> {
            Log.d("REQ", "Uso");
            String newStatus = reservation.getStatus().equals(AccreditReservation.Status.REQUESTED) ? "CANCELLED_REQUEST" : "CANCELLED_RESERVATION";

            Call<ResponseBody> call = ClientUtils.reservationService.updateStatus(reservation.getId(), newStatus);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Reservation cancelled", Toast.LENGTH_LONG).show();
                        if (parentFragment != null && parentFragment instanceof ReservationsListener) {
                            ReservationsListener reservationsListener = (ReservationsListener) parentFragment;
                            reservationsListener.getReservations();
                        } else {
                            Log.e("GuestsReservationsCard", "Parent fragment does not implement ReservationsListener");
                        }
                        Log.d("GuestsReservationsCard", "Status changed");
                    } else {
                        String errorMessage = ClientUtils.getError(response, "Reservation could not be cancelled");
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.d("GuestsReservationsCard", response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("GuestsReservationsCard", "Status could not be changed: " + t.getMessage());
                }
            });
        });
    }
}