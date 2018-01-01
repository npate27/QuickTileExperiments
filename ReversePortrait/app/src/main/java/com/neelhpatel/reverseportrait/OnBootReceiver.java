package com.neelhpatel.reverseportrait;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String KEY_PREF_ON_BOOT = context.getResources().getString(R.string.on_boot_key);
        boolean onBootIsEnabled = prefs.getBoolean(KEY_PREF_ON_BOOT, false);
        if(onBootIsEnabled){
            Intent serviceIntent = new Intent(context, ChargingDetectService.class);
            context.startService(serviceIntent);
        }
    }
}
