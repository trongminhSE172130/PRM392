package com.fpt.project.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fpt.project.R;
import com.fpt.project.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private MaterialButton btnLogout;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        setupData();
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        
        if (getContext() != null) {
            sharedPreferences = getContext().getSharedPreferences("app_prefs", MODE_PRIVATE);
        }
    }

    private void setupData() {
        if (sharedPreferences != null) {
            String fullName = sharedPreferences.getString("user_full_name", "User Name");
            String email = sharedPreferences.getString("user_email", "user@example.com");
            
            tvUserName.setText(fullName);
            tvUserEmail.setText(email);
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void performLogout() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
        
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
