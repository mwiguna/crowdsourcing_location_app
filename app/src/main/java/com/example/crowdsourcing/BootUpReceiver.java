package com.example.crowdsourcing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;


public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        /***** For start Service  ****/
        Intent serviceIntent = new Intent(context, ForegroundService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }

}
