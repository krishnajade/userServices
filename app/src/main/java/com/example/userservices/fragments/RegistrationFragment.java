package com.example.userservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.userservices.LoginActivity;
import com.example.userservices.MyApplication;
import com.example.userservices.services.SignupApi;
import com.example.userservices.R;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationFragment extends Fragment implements LocationListener {
    TextView mmobile;
    EditText mfname;
    EditText mmname;
    EditText mlname;
    EditText maddress;
    EditText mpan_no;
    EditText maadhar_no;
    EditText moccupation;
    Button mSignupBtn;
    Spinner spinnerCity;
    private String FName,MName,LName,Address,City,state,
            PAN_NO,Aadhar_No,status,created_location,Occupation;
    private String mobile=MyApplication.mobile;
    LocationManager locationManager;
    private static final int REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private String phoneNumber;
    public RegistrationFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mmobile = view.findViewById(R.id.edit_text_userid);
        mfname = view.findViewById(R.id.edit_text_fname);
        mmname = view.findViewById(R.id.edit_text_mname);
        mlname=view.findViewById(R.id.edit_text_lname);
        maddress=view.findViewById(R.id.edit_text_address);
        spinnerCity = view.findViewById(R.id.spinner_city);
        mpan_no=view.findViewById(R.id.edit_text_pan_no);
        maadhar_no=view.findViewById(R.id.edit_text_aadhar_no);
        moccupation=view.findViewById(R.id.edit_text_occupation);

        mSignupBtn = view.findViewById(R.id.button_submit);

        //Get username from Login activity
        //username = getActivity().getIntent().getStringExtra("username");

        //Check location permissions
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        locationEnabled();
        getLocation();
        retrieveMobileNumber();

        // Set the text of the TextView to the phone number, if it's available
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            retrieveMobileNumber();
        }

        mmobile.setText(phoneNumber);
        
        mSignupBtn.setOnClickListener(v -> {
             mobile = mmobile.getText().toString();
             FName = mfname.getText().toString();
             MName = mmname.getText().toString();
             LName = mlname.getText().toString();
             Address = maddress.getText().toString();
             City = spinnerCity.getSelectedItem().toString();
             state="Maharashtra";
             PAN_NO = mpan_no.getText().toString();
             Aadhar_No = maadhar_no.getText().toString();
             status="Registered";
             Occupation=moccupation.getText().toString();
            // Validate the PAN and Aadhar numbers
            if (!isValidPAN(PAN_NO)) {
                mpan_no.setError("Invalid PAN number");
                return;
            }
            if (!isValidAadharNumber(Aadhar_No)) {
                maadhar_no.setError("Invalid Aadhar number");
                return;
            }
            //getMobileNumber(mobile);
            signup1(mobile, FName, MName, LName, Address,City,state,
                    PAN_NO,Aadhar_No,status, created_location,Occupation);
        });
        return view;
    }
    private boolean isValidPAN(String pan) {
        String regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        return pan.matches(regex);
    }
    private boolean isValidAadharNumber(String aadhar) {
        String regex = "^[2-9]{1}[0-9]{11}$";
        return aadhar.matches(regex);
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
    private void locationEnabled() {
        LocationManager lm = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", (paramDialogInterface, paramInt) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
    void getLocation() {
        try {
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            created_location = addresses.get(0).getLocality();
        } catch (Exception ignored) {
        }
    }
    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }


    private void signup1(String mobile, String FName, String MName, String LName,
                         String Address,String City,String state,String PAN_NO,String Aadhar_No,
                         String Status,String created_location,String Occupation) {
        final String BASE_URL = "https://services.trifrnd.in/api/usr/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SignupApi signupApi = retrofit.create(SignupApi.class);
        Call<String> call = signupApi.signup1(mobile,  FName,  MName,  LName,
                 Address, City, state, PAN_NO, Aadhar_No,
                 Status,created_location,Occupation);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String responseBody = response.body();
                if (response.isSuccessful()) {

                    assert responseBody != null;
                    if (responseBody.equals("OK")) {

                        //call the method to change status of user from unregistered to registered
                        //updateStatus(username);


                        Toast.makeText(getContext(), "User registered successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
//                    else if (responseBody.equals("User already registered")) {
//                        Toast.makeText(getContext(), "User already registered", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getActivity(), LoginActivity.class);
//                        startActivity(intent);
//                        requireActivity().finish();
//                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: Failed to register user", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}