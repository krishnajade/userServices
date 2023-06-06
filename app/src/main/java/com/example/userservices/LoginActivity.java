package com.example.userservices;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import retrofit2.Call;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.userservices.fragments.RegistrationFragment;
import com.example.userservices.services.ApiService;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_PHONE_STATE = 1;
    private EditText etPassword;
    private String phoneNumber;
    private TextView etUsername;
    private ApiService apiService;
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Check if the required permissions are granted
        if (!checkPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                || !checkPermission(Manifest.permission.INTERNET)
                || !checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                || !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                || !checkPermission(Manifest.permission.READ_SMS)
                || !checkPermission(Manifest.permission.READ_PHONE_STATE)
                || !checkPermission(Manifest.permission.READ_PHONE_NUMBERS)) {
            // Request the required permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_PHONE_NUMBERS
            }, REQUEST_CODE_PERMISSIONS);
        } else {
            // The required permissions are already granted
            retrieveMobileNumber();
            //fetchData(String.valueOf(etUsername));
            setupActivity();
        }
    }
    private void retrieveMobileNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_READ_PHONE_STATE);
        } else {
            String fullNumber = telephonyManager.getLine1Number();

            if (fullNumber != null && fullNumber.length() > 10) {
                phoneNumber = fullNumber.substring(fullNumber.length() - 10);
            } else {
                phoneNumber = "N.A.";
            }
        }
        MyApplication.mobile = phoneNumber;
        fetchData(phoneNumber);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                retrieveMobileNumber();
                //fetchData(String.valueOf(etUsername));
                fetchData(phoneNumber);
                setupActivity();
            } else {
                // permission denied, close the app
                Toast.makeText(this, "Permissions are required to use this app", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    private void setupActivity() {
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);

        etUsername.setText(phoneNumber);

        Button btnLogin = findViewById(R.id.login_button);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://services.trifrnd.in/api/usr/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            login(username, password);
        });
        TextView signup = findViewById(R.id.dont_have_account);
        signup.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void fetchData(String mobileNumber) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://services.trifrnd.in/api/usr/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.getUser1(mobileNumber);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if ("No".equals(response.body())) {
                    Intent intentr = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intentr);
                    finish();
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
            }
        });
    }
    private void login(String username, String password) {
        apiService.login(username, password).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /*Unregistered -> InactiveUserProfileActivity
                Registered -> RegisteredUserProfileActivity
                */

//                Intent intent = new Intent(LoginActivity.this, UploadDocumentsFragment.class);
//                intent.putExtra("username", username);

                Intent intent2 = new Intent(LoginActivity.this, RegistrationFragment.class);
                intent2.putExtra("username", username);

                switch (response.body()) {
                    case "Unregistered": {
                        Intent intentu = new Intent(LoginActivity.this, InactiveUserProfileActivity.class);
                        intentu.putExtra("username", username);
                        startActivity(intentu);
                        finish();
                        break;
                    }
                    case "Registered": {
                        Intent intentr = new Intent(LoginActivity.this, UserProfileActivity.class);
                        intentr.putExtra("username", username);
                        startActivity(intentr);
                        finish();
                        break;
                    }
                    default:
                        Toast.makeText(LoginActivity.this, "Error: Invalid username or password", Toast.LENGTH_LONG).show();
                        break;
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: Failed to connect to server" , Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}