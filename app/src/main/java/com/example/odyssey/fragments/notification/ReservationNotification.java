package com.example.odyssey.fragments.notification;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.odyssey.R;
import com.example.odyssey.model.notifications.Notification;
import com.example.odyssey.model.reservations.AccreditReservation;
import com.example.odyssey.utils.TokenUtils;

import java.time.format.DateTimeFormatter;

public class ReservationNotification extends Fragment {

    TextView title, date, description, status, accommodationTitle, guesthost, price, guestNumber, startDate, endDate;
    LinearLayout statusLayout;
    private Notification notification;
    private AccreditReservation reservation;
    public final String ARG_NOTIFICATION = "notification";

    public ReservationNotification() {}

    public static ReservationNotification newInstance() {
        return new ReservationNotification();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null && getArguments().containsKey(ARG_NOTIFICATION))
            notification = (Notification) getArguments().getSerializable(ARG_NOTIFICATION);

        if(notification == null) throw new RuntimeException("Notification is null");
        else reservation = notification.getReservation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reservation_notification, container, false);
        title = v.findViewById(R.id.notificationTitle);
        date = v.findViewById(R.id.notificationDate);
        description = v.findViewById(R.id.notificationDescription);

        title.setText(notification.getTitle());
        date.setText(notification.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        description.setText(notification.getText());

        status = v.findViewById(R.id.status);
        statusLayout = v.findViewById(R.id.statusLayout);

        accommodationTitle = v.findViewById(R.id.accommodationTitleTextView);
        guesthost = v.findViewById(R.id.guestName);
        price = v.findViewById(R.id.totalPrice);
        guestNumber = v.findViewById(R.id.guestNumber);
        startDate = v.findViewById(R.id.startDate);
        endDate = v.findViewById(R.id.endDate);

        if(reservation.getStatus() != null){
            switch (reservation.getStatus()){
                case ACCEPTED:
                    statusLayout.getBackground().setTint(ContextCompat.getColor(requireContext(), R.color.approved_30));
                    status.setTextColor(ContextCompat.getColor(requireContext(), R.color.approved));
                    status.setText("ACCEPTED");
                    break;
                case REQUESTED:
                    statusLayout.getBackground().setTint(ContextCompat.getColor(requireContext(), R.color.pending_30));
                    status.setTextColor(ContextCompat.getColor(requireContext(), R.color.pending));
                    status.setText("REQUESTED");
                    break;
                case DECLINED:
                    statusLayout.getBackground().setTint(ContextCompat.getColor(requireContext(), R.color.cancelled_30));
                    status.setTextColor(ContextCompat.getColor(requireContext(), R.color.cancelled));
                    status.setText("DECLINED");
                    break;
                case CANCELLED_REQUEST:
                    statusLayout.getBackground().setTint(ContextCompat.getColor(requireContext(), R.color.zirko_30));
                    status.setTextColor(ContextCompat.getColor(requireContext(), R.color.zirko_siva));
                    status.setText("CANCELLED REQUEST");
                    break;
                case CANCELLED_RESERVATION:
                    statusLayout.getBackground().setTint(ContextCompat.getColor(requireContext(), R.color.zirko_30));
                    status.setTextColor(ContextCompat.getColor(requireContext(), R.color.zirko_siva));
                    status.setText("CANCELLED RESERVATION");
                    break;
            }
        }

        if(reservation.getAccommodation().getTitle() != null) accommodationTitle.setText(reservation.getAccommodation().getTitle());
        if(reservation.getPrice()!=null) price.setText(reservation.getPrice().toString());
        if(reservation.getGuestNumber()!=null) guestNumber.setText(reservation.getGuestNumber().toString());
        if(reservation.getStart()!=null) startDate.setText(reservation.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if(reservation.getStart()!=null) endDate.setText(reservation.getEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if(TokenUtils.getRole(requireContext()).equals("GUEST")) guesthost.setText(reservation.getAccommodation().getHost().getName() + " " + reservation.getAccommodation().getHost().getSurname());
        else guesthost.setText(reservation.getGuest().getName() + " " + reservation.getGuest().getSurname());
        return v;
    }
}