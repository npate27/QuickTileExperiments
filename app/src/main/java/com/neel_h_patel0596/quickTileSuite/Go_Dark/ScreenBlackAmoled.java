package com.neel_h_patel0596.quickTileSuite.Go_Dark;


import android.app.Dialog;

import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by O812 on 12/31/2016.
 */

public class ScreenBlackAmoled extends TileService {

    //TODO: Setting to add time or something
    //TODO: make text centered, larger and grey
    //TODO: ICON CHANGE

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTileAdded() {
        updateTile();
    }

    @Override
    public void onClick() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen){

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                showSystemUI(this);
                this.dismiss();
                return super.onTouchEvent(event);
            }

            @Override
            protected void onCreate(Bundle savedInstanceState) {

                hideSystemUI(this);
                super.onCreate(savedInstanceState);
            }
        };
        dialog.getWindow().setWindowAnimations(android.R.style.Animation_Toast);
        this.showDialog(dialog);
    }



    private void updateTile() {
        Tile tile = super.getQsTile();
        tile.updateTile();
    }


    private void hideSystemUI(Dialog dialog) {
        View decorView = dialog.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }


    private void showSystemUI(Dialog dialog) {
        View decorView = dialog.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


}
