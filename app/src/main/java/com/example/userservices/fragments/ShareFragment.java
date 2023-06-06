package com.example.userservices.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.userservices.R;

public class ShareFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        Button btnShare = view.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(v -> {
                Intent intentInvite = new Intent(Intent.ACTION_SEND);
                intentInvite.setType("text/plain");
                //String body = "https://1drv.ms/f/s!AteairfnzOPvp6Z2SPuQb8U9z6iJTw?e=0smDOL";
            String body="https://drive.google.com/drive/folders/1fNHhYVFrHvsXgzsieP6GrbWDOEmjIQQh";
                String subject = "Your Subject";
                intentInvite.putExtra(Intent.EXTRA_SUBJECT, subject);
                intentInvite.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(intentInvite, "Share using"));
        });
        return view;
    }
}