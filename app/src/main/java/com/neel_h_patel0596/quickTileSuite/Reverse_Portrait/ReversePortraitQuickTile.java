package com.neel_h_patel0596.quickTileSuite.Reverse_Portrait;


import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;


/**
 * Created by O812 on 12/27/2016.
 */
public class ReversePortraitQuickTile extends TileService {
    private boolean canWrite;

    //TODO: Uninstall stop?
    //TODO: If inactive and reverse portrait, toggle active
    //TODO: Toggle in main app to do manual vs on Charge
    //TODO: ICON CHANGE

    @Override
    public void onCreate() {
        super.onCreate();
        canWrite = (getQsTile() != null && getQsTile().getState() == Tile.STATE_ACTIVE);
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
        } else {
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
            updateTile();
        }
    }

    private void updateTile() {
        Tile tile = super.getQsTile();
        int newState = (tile.getState()==Tile.STATE_ACTIVE) ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE;
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
            Toast.makeText(this, "Need to allow modify system settings!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
