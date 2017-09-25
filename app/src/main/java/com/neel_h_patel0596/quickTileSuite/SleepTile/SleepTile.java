package com.neel_h_patel0596.quickTileSuite.SleepTile;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by O812 on 1/3/2017.
 */

public class SleepTile extends TileService {
    //Long press for dialog, quick press for cycle
    //TODO: Separate tiles to own apps b/c cant set qs_tile_preferences?
    //TODO: ICON SWITCHING
    private boolean canWrite;

    @Override
    public void onStartListening() {
        //get current setting and change icon
        int secondsValue = getCurrentSleepSetting()/1000;
        updateTile(secondsValue);
    }

    @Override
    public void onClick() {
        if (!canWrite) {
            checkWritePermission();
        } else {
            setTimeout();
        }
   }


    private void setTimeout(){
        int time;
        switch (getCurrentSleepSetting()) {
            case 15000:
                time = 30000;
                break;
            case 30000:
                time = 60000;
                break;
            case 60000:
                time = 180000;
                break;
            case 180000:
                time = 300000;
                break;
            case 300000:
                time = 600000;
                break;
            case 600000:
                time = 1800000;
                break;
            case 1800000:
                time = 15000;
                break;
            default:
                time = -1;
                break;
        }
        updateTile(time / 1000);
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
    }
    private int getCurrentSleepSetting() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void updateTile(int time){
        Tile tile = getQsTile();
        String currentSetting = (time < 60) ? time + " sec" : time/60 + " min";
        tile.setLabel(currentSetting);
        getQsTile().updateTile();
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
            Toast.makeText(this, "Need to allow modify system settings!", Toast.LENGTH_LONG).show();
        }
    }
}
