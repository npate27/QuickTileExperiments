package com.neelhpatel.reverseportrait;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


@SuppressLint("ExportedPreferenceActivity")
public class TileSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new TilePreferenceFragment()).commit();
    }

    public static class TilePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        public String KEY_PREF_MANUAL_MODE;
        public String KEY_PREF_ON_BOOT;
        public String KEY_PREF_UNINSTALL;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            KEY_PREF_MANUAL_MODE = this.getResources().getString(R.string.manual_mode_key);
            KEY_PREF_ON_BOOT = this.getResources().getString(R.string.on_boot_key);
            KEY_PREF_UNINSTALL = getResources().getString(R.string.uninstall_key);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            SwitchPreference manualModePreference = (SwitchPreference) findPreference(KEY_PREF_MANUAL_MODE);
            SwitchPreference onBootPreference = (SwitchPreference) findPreference(KEY_PREF_ON_BOOT);
            Preference uninstallPreference = findPreference(KEY_PREF_UNINSTALL);

            if(manualModePreference.isChecked()){
                onBootPreference.setEnabled(false);
                onBootPreference.setChecked(false);
            }
            manualModePreference.setOnPreferenceChangeListener(this);
            onBootPreference.setOnPreferenceChangeListener(this);
            uninstallPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri packageURI = Uri.parse("package:com.neelhpatel.reverseportrait");
                    Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                    startActivity(uninstallIntent);
                    return true;
                }
            });
            return view;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(KEY_PREF_MANUAL_MODE)){
                SwitchPreference onBootPreference = (SwitchPreference) findPreference(KEY_PREF_ON_BOOT);

                if((boolean) newValue){
                    onBootPreference.setChecked(false);
                    onBootPreference.setEnabled(false);
                } else {
                    onBootPreference.setEnabled(true);
                }
            }
            return true;
        }
    }

}