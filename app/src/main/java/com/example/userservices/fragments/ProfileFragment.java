package com.example.userservices.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.userservices.R;
import com.example.userservices.models.User;
import com.example.userservices.services.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private String phoneNumber;
    private TextView idTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView genderTextView;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveMobileNumber();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get references to the TextViews where you want to display the response data

        usernameTextView = view.findViewById(R.id.username_textview);
        emailTextView = view.findViewById(R.id.email_textview);
        TextView phoneNumberTextView = view.findViewById(R.id.phone_number_textview);
        genderTextView = view.findViewById(R.id.gender_textview);

        // Set the text of the TextView to the phone number, if it's available
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            retrieveMobileNumber();
        }

        fetchData(phoneNumber);

        phoneNumberTextView.setText(phoneNumber);
        return view;
    }
    private void retrieveMobileNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        } else {
            String fullNumber = telephonyManager.getLine1Number();
            if (fullNumber != null && fullNumber.length() > 10) {
                phoneNumber = fullNumber.substring(fullNumber.length() - 10);
            }
         else {
                phoneNumber = "";
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                retrieveMobileNumber();
            } else {
                Toast.makeText(getActivity(), "Permission to read phone state denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void fetchData(String mobileNumber) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.trifrnd.in/services/eng/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<User> call = apiService.getUser(mobileNumber);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        // Update the UI with the user data

                        TextView usernameTextView = getView().findViewById(R.id.username_textview);
                        usernameTextView.setText(user.getUsername());

                        TextView emailTextView = getView().findViewById(R.id.email_textview);
                        emailTextView.setText(user.getEmail());

                        TextView genderTextView = getView().findViewById(R.id.gender_textview);
                        genderTextView.setText(user.getGender());
                    }
                }
                // Handle the unsuccessful response case
                else{
                    Toast.makeText(getContext(), "User data not received", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // Handle the failure case
                Toast.makeText(getContext(), "Error: Failed to fetch user data", Toast.LENGTH_LONG).show();
            }
        });
    }
}