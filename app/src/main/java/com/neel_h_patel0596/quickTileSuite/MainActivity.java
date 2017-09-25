package com.neel_h_patel0596.quickTileSuite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Switch reversePortraitSwitch, switch4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

    }

    private void initUI() {
        reversePortraitSwitch = (Switch) findViewById(R.id.reverse_portrait_switch);
        switch4 = (Switch) findViewById(R.id.switch4);
        reversePortraitSwitch.setOnCheckedChangeListener(new reversePortraitSwitchListener());
        switch4.setOnCheckedChangeListener(new reversePortraitSwitchListener());
    }

    class reversePortraitSwitchListener implements Switch.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                switch(buttonView.getId()){
                    case R.id.reverse_portrait_switch:
                        //TODO: remove from tile tray
                        //TODO: provide dialog/something for mode
                        //TODO: actually implement the modes...
                        break;
                    case R.id.switch4:
                        break;
                    default:
                        break;
                }
            } else {
                //unchecked, unregister quicktiles
            }
        }
    }
}
