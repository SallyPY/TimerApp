package hu.ait.timerapp;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.PreferenceScreen);
        getWindow().getDecorView().setBackgroundColor(new Color().rgb(38, 36, 36));
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return (fragmentName.equals(
                getString(R.string.settings_fragment_name)));
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_headers, target);
    }

    public static class FragmentSettingsBasic extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            final SharedPreferences sharedPreferences = getPreferenceScreen()
                    .getSharedPreferences();

            EditTextPreference maxTimePref = (EditTextPreference) findPreference(getString(R.string.key_max_time));
            maxTimePref.setSummary(sharedPreferences.getString(getString(R.string.key_max_time), getString(R.string.hour_in_min)));

            final AppPreference appPreference = (AppPreference) findPreference(getString(R.string.pref_app));
            getAppLabel(sharedPreferences, appPreference);
            appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AppPreference apps = (AppPreference) preference;
                    apps.showDialog();
                    return false;
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            setUpView(view);
            return view;
        }

        private void setUpView(View view) {
            int padding_in_dp = 20;
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
            view.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
            view.setBackgroundColor(new Color().rgb(38, 36, 36));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.key_max_time))) {
                Preference maxTimePref = findPreference(key);
                maxTimePref.setSummary(sharedPreferences.getString(key, ""));
            } else if (key.equals(getString(R.string.pref_app))) {
                Preference appPref = findPreference(key);
                getAppLabel(sharedPreferences, (AppPreference) appPref);
            }
        }

        private void getAppLabel(SharedPreferences sharedPreferences, AppPreference appPreference) {
            PackageManager pm = getPreferenceScreen().getContext().getApplicationContext().getPackageManager();
            try {
                ApplicationInfo ai = pm.getApplicationInfo(sharedPreferences.getString(getString(R.string.pref_app), ""), 0);
                appPreference.setSummary(pm.getApplicationLabel(ai));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
