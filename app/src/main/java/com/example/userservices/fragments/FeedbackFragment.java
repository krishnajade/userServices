package com.example.userservices.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.userservices.R;
import com.example.userservices.services.FeedbackService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedbackFragment extends Fragment implements RatingBar.OnRatingBarChangeListener {

    private static final int REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private String phoneNumber;
    private RatingBar mRatingBar;
    private EditText mFeedbackEditText;
    private Button mSubmitButton;
    private String usrid;
    private float rating;
    private String UserFeed;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);

        retrieveMobileNumber();
         usrid = phoneNumber;
        // Set the text of the TextView to the phone number, if it's available
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }

        mRatingBar = rootView.findViewById(R.id.ratingBar);
        mFeedbackEditText = rootView.findViewById(R.id.feedbackEditText);
        mSubmitButton = rootView.findViewById(R.id.submitButton);

        mRatingBar.setOnRatingBarChangeListener(this);
        mSubmitButton.setOnClickListener(view -> submitFeedback());

        return rootView;
    }

private void submitFeedback() {
    // Get the rating and feedback text
     rating = mRatingBar.getRating();
    UserFeed = mFeedbackEditText.getText().toString();

    // Create a Retrofit instance
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.trifrnd.in/services/eng/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // Create a FeedbackService instance using the Retrofit instance
    FeedbackService feedbackService = retrofit.create(FeedbackService.class);

    // Send the feedback data to the endpoint using the FeedbackService
    Call<String> call = feedbackService.submitFeedback(usrid,rating,UserFeed);
    call.enqueue(new Callback<String>() {
        @Override
        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
            if (response.isSuccessful()) {
                // Handle successful response
                Toast.makeText(getContext(), "Feedback Received!", Toast.LENGTH_SHORT).show();
            } else {
                // Handle unsuccessful response
                Toast.makeText(getContext(), "Feedback Not Received!", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
        }
    });
}
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        // Update the UI based on the rating
        if (rating < 2.0) {
            mFeedbackEditText.setHint("What went wrong?");
        } else if (rating >= 4.0) {
            mFeedbackEditText.setHint("What did you like?");
        } else {
            mFeedbackEditText.setHint("How can we improve?");
        }
    }
    private void retrieveMobileNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        } else {
            String fullNumber = telephonyManager.getLine1Number();
            if (fullNumber != null && fullNumber.length() > 2) {
                phoneNumber = fullNumber.substring(fullNumber.length() - 10);
            } else {
                phoneNumber = "";
            }
        }
    }
}