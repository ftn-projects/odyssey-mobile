package com.example.odyssey.fragments;

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
import android.widget.Toast;

import com.example.odyssey.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.i("(¬‿¬)", "ProfileFragment onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        initViewMode(menu, inflater);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initViewMode(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.edit_menu, menu);
        menu.findItem(R.id.nav_edit_action).setOnMenuItemClickListener(item -> {
            Toast.makeText(requireActivity(), "EDITING", Toast.LENGTH_SHORT).show();
            initEditMode(menu, inflater);
            return true;
        });
        setEditingVisibility(View.GONE);
    }

    private void initEditMode(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.cancel_confirm_menu, menu);
        menu.findItem(R.id.nav_cancel_action).setOnMenuItemClickListener(item1 -> {
            Toast.makeText(requireActivity(), "RESET CHANGES", Toast.LENGTH_SHORT).show();

            // reset fields

            initViewMode(menu, inflater);
            return true;
        });
        menu.findItem(R.id.nav_commit_action).setOnMenuItemClickListener(item2 -> {
            Toast.makeText(requireActivity(), "SUBMIT CHANGES", Toast.LENGTH_SHORT).show();

            // collect data, send request...

            initViewMode(menu, inflater);
            return true;
        });
        setEditingVisibility(View.VISIBLE);
    }

    private void setEditingVisibility(int visibility) {
        requireView().findViewById(R.id.btnEditImage).setVisibility(visibility);
        requireView().findViewById(R.id.btnConfirmEmail).setVisibility(visibility);
        requireView().findViewById(R.id.confirm_password_section).setVisibility(visibility);
        requireView().findViewById(R.id.btnDeactivateOwn).setVisibility(visibility);
    }
}