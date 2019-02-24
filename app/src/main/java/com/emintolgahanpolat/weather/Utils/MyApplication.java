package com.emintolgahanpolat.weather.Utils;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class MyApplication extends Application {


    private static MyApplication mInstance;

    ConnectivityReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
        registerReceiver(receiver, filter);

    }
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }


}
