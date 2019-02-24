package com.emintolgahanpolat.weather.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    public static boolean isConnected = false;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        isNetworkAvailable(context); //receiver çalıştığı zaman çağırılacak method

    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE); //Sistem ağını dinliyor internet var mı yok mu

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        if(!isConnected){ //internet varsa
                            isConnected = true;
                            if (connectivityReceiverListener != null) {
                                connectivityReceiverListener.onNetworkConnectionChanged(true);
                            }
                            //Toast.makeText(context, "internete Bağlandınız!", Toast.LENGTH_LONG).show();
                        }
                        return true;

                    }
                }
            }
        }
        isConnected = false;
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(false);
        }
        //Toast.makeText(context, "İnternet Yok", Toast.LENGTH_LONG).show();

        return false;
    }
}