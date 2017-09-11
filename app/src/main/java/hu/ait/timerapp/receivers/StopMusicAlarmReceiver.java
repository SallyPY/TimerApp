package hu.ait.timerapp.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import hu.ait.timerapp.R;

public class StopMusicAlarmReceiver extends BroadcastReceiver {
//    public static final String SERVICECMD = "com.android.music.musicservicecommand";
//    public static final String CMDNAME = "command";
//    public static final String CMDSTOP = "stop";
//    public static final String ACTION_ALARM_RECEIVER = "ACTION_ALARM_RECEIVER";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    public void onReceive(Context ctxt, Intent intent) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(ctxt);
        AudioManager mAudioManager = (AudioManager) ctxt.getSystemService(Context.AUDIO_SERVICE);
        editor = sharedPref.edit();
        editor.putBoolean(ctxt.getString(R.string.alarm_active), false);
        editor.commit();

        stopMusic(mAudioManager);
        handlePreferences(ctxt);
    }

    private void stopMusic(AudioManager mAudioManager) {
        AudioManager.OnAudioFocusChangeListener af;
        af = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
            }
        };
        mAudioManager.requestAudioFocus(af,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        //        if (mAudioManager.isMusicActive()) {
//            Intent i = new Intent(SERVICECMD);
//            i.putExtra(CMDNAME, CMDSTOP);
//            context.sendBroadcast(i);
//            mAudioManager.abandonAudioFocus(af);
//        }
        //handler in here?????????, firebaseeeee
    }

    private void handlePreferences(Context ctxt) {
        if(sharedPref.getBoolean(ctxt.getString(R.string.key_bluetooth), false)) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
        if(sharedPref.getBoolean(ctxt.getString(R.string.key_wifi), false)) {
            WifiManager wifiManager = (WifiManager) ctxt.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }
}