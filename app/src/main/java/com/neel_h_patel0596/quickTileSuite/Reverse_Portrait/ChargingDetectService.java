package com.neel_h_patel0596.quickTileSuite.Reverse_Portrait;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

/**
 * Created by O812 on 12/30/2016.
 */

public class ChargingDetectService extends Service {

    boolean isTurned;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isTurned = false;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        if(isCharging()){
            Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_180);
            isTurned = true;
        }
        registerReceiver(receiver, filter);
        Log.d("TAG1", "OnStartCommand, receiver registered");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(isCharging()){
            Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_0);
            isTurned = false;
        }
        unregisterReceiver(receiver);
        Log.d("TAG1", "OnDestory, receiver unregistered");
        Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_0);
        super.onDestroy();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isTurned){
                Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_180);
            } else {
                Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_0);
            }
            isTurned = !isTurned;
        }
    };

    public boolean isCharging(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL);
    }
}
