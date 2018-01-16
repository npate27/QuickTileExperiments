package com.neelhpatel.reverseportrait;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.Surface;
import android.widget.Toast;


public class ReversePortraitTileService extends TileService {
    private boolean canWrite, isManualMode;
    private ChargingStatusReceiver mChargingStatusReceiver;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        canWrite = (getQsTile() != null && getQsTile().getState() == Tile.STATE_ACTIVE);
    }

    @Override
    public void onStartListening() {
        readPreferences();
        if(isManualMode){
            try {
                int rotation = Settings.System.getInt(getContentResolver(), Settings.System.USER_ROTATION);
                int state = (rotation == Surface.ROTATION_0) ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE;
                Tile tile = getQsTile();
                tile.setState(state);
                tile.setIcon(createIcon(R.drawable.ic_manual_logo));
                tile.updateTile();
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            updateTileChargeMode();
        }
    }

    @Override
    public void onTileAdded() {
        updateTileChargeMode();
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile();
        if (!canWrite) {
            checkWritePermission();
        } else if (isManualMode){
            switchOrientation();
        } else{
            switch(tile.getState()){
                case Tile.STATE_INACTIVE:
                    if(isCharging()){
                        setOrientation(Surface.ROTATION_180);
                    }
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        IntentFilter filter = new IntentFilter();
                        filter.addAction(Intent.ACTION_POWER_CONNECTED);
                        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
                        mChargingStatusReceiver = new ChargingStatusReceiver();
                        HandlerThread handlerThread = new HandlerThread("ChargingDetectThread");
                        handlerThread.start();
                        Looper looper = handlerThread.getLooper();
                        handler = new Handler(looper);
                        registerReceiver (mChargingStatusReceiver, filter, null, handler);
                    }
                    tile.setState(Tile.STATE_ACTIVE);
                    tile.setIcon(createIcon(R.drawable.ic_charge_reverse_portrait));
                    break;
                case Tile.STATE_ACTIVE:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        unregisterReceiver(mChargingStatusReceiver);
                    }
                    setOrientation(Surface.ROTATION_0);
                    tile.setState(Tile.STATE_INACTIVE);
                    tile.setIcon(createIcon(R.drawable.ic_charge_portrait));
                    break;
                default:
                    break;
            }
        }
        tile.updateTile();

    }

    private Icon createIcon(int resId){
        return Icon.createWithResource(this, resId);
    }

    private void updateTileChargeMode() {
        Tile tile = super.getQsTile();
        int resId = (tile.getState()==Tile.STATE_ACTIVE) ? R.drawable.ic_charge_reverse_portrait : R.drawable.ic_charge_portrait;
        tile.setIcon(createIcon(resId));
        tile.updateTile();
    }

    private void checkWritePermission(){
        if(canWrite || Settings.System.canWrite(this)) {
            canWrite = true;
        } else {
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            this.startActivity(intent);
            Toast.makeText(this, "Need to allow modify system settings", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void readPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String KEY_PREF_MANUAL_MODE = getResources().getString(R.string.manual_mode_key);
        isManualMode = prefs.getBoolean(KEY_PREF_MANUAL_MODE, false);
    }

    public void switchOrientation(){
        try {
            int rotation = Settings.System.getInt(getContentResolver(), Settings.System.USER_ROTATION);
            int reverseRotation = (rotation == Surface.ROTATION_0) ? Surface.ROTATION_180 : Surface.ROTATION_0;
            Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, reverseRotation);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setOrientation(int rotation){
        Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, rotation);
    }

    public boolean isCharging(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL);
    }

}
