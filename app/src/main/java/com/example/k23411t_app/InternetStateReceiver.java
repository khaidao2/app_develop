package com.example.k23411t_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class InternetStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
            
            if (isConnected) {
                boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                if (isWiFi) {
                    Toast.makeText(context, "Wifi is connected", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Internet is connected (Not Wifi)", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Internet is disconnected", Toast.LENGTH_LONG).show();
            }
        }
    }
}
