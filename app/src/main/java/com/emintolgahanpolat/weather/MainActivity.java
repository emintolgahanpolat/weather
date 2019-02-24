package com.emintolgahanpolat.weather;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.emintolgahanpolat.weather.Utils.ConnectivityReceiver;
import com.emintolgahanpolat.weather.Utils.LocationService;
import com.emintolgahanpolat.weather.Utils.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private boolean isConnected = false;
    private boolean locPer = false;

    String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    int PermissionCode = 67;

    private TextView txtState;


    private static final String TAG="Main Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        firebaseToken();//firebase kimliği oluşturma
        permissinsRequest();//izin isteği
        init();//gerekli tanımlamalar
        stateControl();//internet ve konum izni kontrolü




    }

    private void firebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }


                        String token = task.getResult().getToken();
                        String msg = getString(R.string.fcm_token, token);
                        Log.d(TAG, "Token "+msg);

                    }
                });
    }


    private void init() {
        txtState = (TextView) findViewById(R.id.txtMainState);
        txtState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locPer) {
                    stateControl();
                } else {

                    permissinsRequest();

                }
            }
        });

    }


    private void selectCityActivityStart() {
        String lattlong = locationString();
        if (lattlong != null) {
            Intent intent = new Intent(MainActivity.this, CityListActivity.class);
            intent.putExtra("lattlong", lattlong);
            startActivity(intent);

        }
    }


    private void permissinsRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                locPer = true;
                stateControl();
            } else {
                requestPermissions(permissions, PermissionCode);
            }

        } else {
            locPer = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 67: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locPer = true;
                }
                return;
            }
        }
        stateControl();
    }


    private void stateControl() {
        String message;
        if (!locPer) {
            message = "Konum izni ver";
        } else if (!ConnectivityReceiver.isConnected) {
            message = "İnternet Bağlantısını Kontrol edin";
        } else {
            message = "Ready";
            selectCityActivityStart();
        }
        txtState.setText(message);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
        stateControl();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        stateControl();
    }

    private String locationString() {

        LocationService gps = new LocationService(this);

        if (gps.canGetLocation()) {
            String lattlong = gps.getLatitude() + "," + gps.getLongitude();
            Log.e("lattlong : ", lattlong);
            gps.stopUsingGPS();

            return lattlong;

        } else {
            gps.showSettingsAlert();
            return null;
        }

    }


}
