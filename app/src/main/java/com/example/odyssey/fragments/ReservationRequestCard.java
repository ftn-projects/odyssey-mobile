package com.example.odyssey.fragments;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.reservations.AccreditReservation;

import java.time.format.DateTimeFormatter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationRequestCard extends LinearLayout {

    private AccreditReservation reservation;

    TextView title, status, guest, guestNumber, price, cancellations, startDate,endDate ;
    LinearLayout statusLayout, buttonSection;

    Button accept,decline;

    public ReservationRequestCard(Context context) {
        super(context);
        init();
    }

    public void setReservationRequest(AccreditReservation reservation){
        this.reservation = reservation;
        if(reservation!=null){
            if(reservation.getAccommodation().getTitle() !=null)
                title.setText(reservation.getAccommodation().getTitle());

            if(reservation.getStatus() != null){
                switch (reservation.getStatus()){
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
                }
            }

            if(reservation.getGuest() != null)
                guest.setText(reservation.getGuest().getName() + " " + reservation.getGuest().getSurname());

            if(reservation.getCancellationNumber() != null)
                cancellations.setText(reservation.getCancellationNumber().toString());

            if(reservation.getPrice()!=null)
                price.setText(reservation.getPrice().toString());

            if(reservation.getGuestNumber()!=null)
                guestNumber.setText(reservation.getGuestNumber().toString());

            if(reservation.getStart()!=null)
                startDate.setText(reservation.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            if(reservation.getEnd()!=null)
                endDate.setText(reservation.getEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_reservation_request_card, this, true);

        title = findViewById(R.id.accommodationTitleTextView);
        status = findViewById(R.id.status);
        statusLayout = findViewById((R.id.statusLayout));
        buttonSection = findViewById(R.id.buttonSection);
        guest = findViewById(R.id.guestName);
        cancellations = findViewById(R.id.cancellationNumber);
        price = findViewById(R.id.totalPrice);
        guestNumber = findViewById(R.id.guestNumber);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        accept = findViewById(R.id.buttonAccept);
        decline = findViewById(R.id.buttonDecline);

        accept.setOnClickListener(c -> {
            Call<ResponseBody> call = ClientUtils.reservationService.updateStatus(reservation.getId(),"ACCEPTED" );
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200){
                        Log.d("REQ","ACCEPTED");
                        buttonSection.setVisibility(View.GONE);
                    }
                    else{
                        Log.d("REQ","OOPSIE");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        });

        decline.setOnClickListener(c -> {
            Call<ResponseBody> call = ClientUtils.reservationService.updateStatus(reservation.getId(),"ACCEPTED" );
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200){
                        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.cancelled_30));
                        status.setTextColor(ContextCompat.getColor(getContext(), R.color.cancelled));
                        status.setText("DECLINED");
                        buttonSection.setVisibility(View.GONE);
                        Log.d("REQ","DECLINED");
                    }
                    else{
                        Log.d("REQ","OOPSIE");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        });
    }
}