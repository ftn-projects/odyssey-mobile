package com.example.odyssey.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.users.UserWithReports;
import com.example.odyssey.utils.TokenUtils;

import java.util.List;

public class UserManagementAdapter extends ArrayAdapter<UserWithReports> {
    Context context;
    View rootView;

    public UserManagementAdapter(Context context, View rootView, @NonNull List<UserWithReports> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.rootView = rootView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        UserWithReports user = getItem(position);
        if (user == null) {
            Log.e("NotificationSummaryAdapter", "Notification is null");
            return convertView;
        }

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_management_item, parent, false);

        TextView nameView = convertView.findViewById(R.id.profile_name);
        TextView roleView = convertView.findViewById(R.id.profile_role);
        TextView statusView = convertView.findViewById(R.id.account_status);
        ImageView profileImage = convertView.findViewById(R.id.profile_image);

        String displayName = user.getName() + " " + user.getSurname();
        if (user.getId().equals(TokenUtils.getId()))
            displayName += " (You)";
        nameView.setText(displayName);
        roleView.setText(user.getRole());
        statusView.setText(user.getStatus().toString());

        String imagePath = ClientUtils.SERVICE_API_PATH + "users/image/" + user.getId();
        Glide.with(context).load(imagePath).into(profileImage);

        convertView.setOnClickListener(view1 -> {
            Bundle args = new Bundle();
            args.putSerializable("user", user);
            Navigation.findNavController(rootView).navigate(R.id.nav_user_management_details, args);
        });

        setStatusDesign(convertView, user.getStatus());

        return convertView;
    }

    private void setStatusDesign(View v, UserWithReports.AccountStatus status) {
        int backColor = 0, frontColor = 0;
        String text = "";
        switch (status) {
            case PENDING:
                backColor = R.color.pending_30;
                frontColor = R.color.pending;
                text = "REQUESTED";
                break;
            case ACTIVE:
                backColor = R.color.approved_30;
                frontColor = R.color.approved;
                text = "ACTIVE";
                break;
            case BLOCKED:
                backColor = R.color.cancelled_30;
                frontColor = R.color.cancelled;
                text = "BLOCKED";
                break;
            case DEACTIVATED:
                backColor = R.color.zirko_30;
                frontColor = R.color.zirko_30;
                text = "DEACTIVATED";
                break;
        }
        LinearLayout statusLayout = v.findViewById(R.id.status_layout);
        TextView statusView = v.findViewById(R.id.account_status);

        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), backColor));
        statusView.setTextColor(ContextCompat.getColor(getContext(), frontColor));
        statusView.setText(text);
    }
}
