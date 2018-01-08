package com.neelhpatel.reverseportrait;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.Surface;
import android.widget.Toast;


public class ReversePortraitTileService extends TileService {
    private boolean canWrite;
    private boolean isManualMode;
    private boolean firstInstance = true;
    private Icon manualIcon;
    @Override
    public void onCreate() {
        super.onCreate();
        canWrite = (getQsTile() != null && getQsTile().getState() == Tile.STATE_ACTIVE);
        manualIcon = Icon.createWithResource(this, R.drawable.ic_manual_logo);
    }

    @Override
    public void onStartListening() {
        if(firstInstance){
            updateTile(Tile.STATE_INACTIVE);
        } else {
            firstInstance = false;
        }
        readPreferences();
        if(isManualMode){
            try {
                int rotation = Settings.System.getInt(getContentResolver(), Settings.System.USER_ROTATION);
                int state = (rotation == Surface.ROTATION_0) ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE;
                updateTile(state);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            updateTileManual();
        }
    }

    @Override
    public void onTileAdded() {
        updateTile();
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile();
        if (!canWrite) {
            checkWritePermission();
        } else if (isManualMode){
            switchOrientation();
        } else{
            Intent intent = new Intent(this, ChargingDetectService.class);
            switch(tile.getState()){
                case Tile.STATE_INACTIVE:
                    startService(intent);
                    break;
                case Tile.STATE_ACTIVE:
                    stopService(intent);
                    break;
                default:
                    break;
            }
        }
        updateTile();

    }

    private void updateTile(int state) {
        Tile tile = super.getQsTile();
        tile.setState(state);
        tile.updateTile();
    }

    private void updateTileManual() {
        Tile tile = super.getQsTile();
        tile.setIcon(manualIcon);
        tile.updateTile();
    }


    private void updateTile() {
        Tile tile = super.getQsTile();
        int newState = (tile.getState()==Tile.STATE_ACTIVE) ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE;
        if(!isManualMode) {
            int resId = (newState == Tile.STATE_INACTIVE) ? R.drawable.ic_charge_reverse_portrait : R.drawable.ic_charge_portrait;
            Icon icon = Icon.createWithResource(this, resId);
            tile.setIcon(icon);
        }
        tile.setState(newState);
        tile.updateTile();
    }

    private void checkWritePermission(){
        if(canWrite || Settings.System.canWrite(this)) {
            canWrite = true;
            onClick();
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

}
