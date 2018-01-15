package com.neelhpatel.reverseportrait;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.provider.Settings;
import android.view.Surface;

public class ChargingStatusReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionBroadcast = intent.getAction();
        this.context = context;
        if(actionBroadcast.equals(Intent.ACTION_POWER_CONNECTED)){
            changeOrientation(Surface.ROTATION_180);
        } else {
            changeOrientation(Surface.ROTATION_0);
        }
    }

    public void changeOrientation(int orientation){
        Settings.System.putInt(context.getContentResolver(), Settings.System.USER_ROTATION, orientation);
    }

    public boolean isCharging(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL);
    }

}
