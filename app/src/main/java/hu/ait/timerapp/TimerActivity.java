package hu.ait.timerapp;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.timerapp.apps.PickAppDialogFragment;
import hu.ait.timerapp.apps.PickAppHelper;
import hu.ait.timerapp.receivers.StopMusicAlarmReceiver;
import hu.ait.timerapp.receivers.UpdateNotificationAlarmReceiver;

import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;

public class TimerActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private PendingIntent notificationIntent;
    private NotificationManager notificationManager;

    @BindView(R.id.activity_main)
    RelativeLayout activityMain;

    @BindView(R.id.seekArcContainer)
    FrameLayout seekArcContainer;

    @BindView(R.id.seekArc)
    SeekArc seekArc;

    @BindView(R.id.seekArcProgress)
    TextView seekArcProgress;

    @BindView(R.id.tvMin)
    TextView tvMin;

    @BindView(R.id.btnStart)
    Button btnStart;

    @BindView(R.id.btnSun)
    ImageButton btnSun;

    @BindView(R.id.btnDark)
    ImageButton btnDark;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPref;

    private Handler handler;

    private Animation seekArcProgressAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setToolbar();
        setSeekArcUI();
        setTaskDescription();


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        seekArcProgressAnim = AnimationUtils.loadAnimation(this, R.anim.flash);
        seekArc.setProgress(getPreviousDuration());

        handler = new Handler();

        if(sharedPref.getBoolean(getString(R.string.alarm_active), false)) {
            setActiveAlarm();
        }
    }

    private void setActiveAlarm() {
        Long timeRemaining = calculateTimeRemaining();

        int minRemaining = (int) (timeRemaining/60000);
        Long delay = timeRemaining % 60000;

        startAnimation();
        seekArc.setEnabled(false);
        btnStart.setText(getString(R.string.reset));
        seekArc.setProgress(minRemaining + 1);
        handler.postDelayed(decrementTimerThread, delay);
    }

    @NonNull
    private Long calculateTimeRemaining() {
        Long currentTime = SystemClock.elapsedRealtime();
        Long alarmStartTime = sharedPref.getLong(getString(R.string.start_alarm_time), 0);

        Long timePassed  = currentTime - alarmStartTime;
        Long prevDuration = Long.valueOf(sharedPref.getInt(getString(R.string.prev_duration), 0) * 60 * 1000);

        return prevDuration - timePassed;
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setSeekArcUI() {
        seekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {}

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {}

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress,
                                          boolean fromUser) {
                seekArcProgress.setText(String.valueOf(progress));
            }
        });
    }

    private void setTaskDescription() {
        //min sdk 21, also ripple
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                getString(R.string.app_name),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon),
                new Color().rgb(38, 36, 36));
        this.setTaskDescription(taskDescription);
    }

    @OnClick(R.id.btnStart)
    public void onStartPressed(Button btn) {
        if (sharedPref.getString(getString(R.string.pref_app), "").equals("")) {
            chooseDefaultApp();
        } else {
            setUpButtonStates(btn);
        }
    }
    @OnClick(R.id.btnSun)
    public void onSunPressed(ImageButton btn) {
        btnSun.setVisibility(View.GONE);
        btnDark.setVisibility(View.VISIBLE);
        activityMain.setBackgroundColor(Color.WHITE);
        btnStart.setBackgroundColor(Color.WHITE);

    }
    @OnClick(R.id.btnDark)
    public void onDarkPressed(ImageButton btn) {
        btnDark.setVisibility(View.GONE);
        btnSun.setVisibility(View.VISIBLE);
        activityMain.setBackgroundColor(new Color().rgb(38,36,36));
        btnStart.setBackgroundColor(new Color().rgb(38,36,36));





    }
    private void chooseDefaultApp() {
        if (PickAppHelper.getMediaApps(this).size() == 0 && PickAppHelper.getAllApps(this).size() == 0) {
            Toast.makeText(TimerActivity.this, R.string.no_available_apps, Toast.LENGTH_SHORT).show();
        } else {
            DialogFragment newFragment = PickAppDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), getString(R.string.dialog));
        }
    }

    private void setUpButtonStates(Button btn) {
        if (btn.getText().toString().equals(getString(R.string.start))) {
            startTimer();
            btn.setText(R.string.reset);
        } else {
            stopTimer();
            btn.setText(getString(R.string.start));
        }
    }

    private void startTimer() {
        seekArc.setEnabled(false);
        int min = seekArc.getProgress();

        setPreviousDuration(min);
        launchMedia();
        startAnimation();
        startAlarm(min);
    }

    private void launchMedia() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(
                sharedPref.getString(getString(R.string.pref_app), ""));
        startActivity(launchIntent);
    }

    private void startAnimation() {
        seekArcProgress.startAnimation(seekArcProgressAnim);
        seekArc.startAnimation(seekArcProgressAnim);
        tvMin.startAnimation(seekArcProgressAnim);
    }

    private void startAlarm(int min) {
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StopMusicAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_ONE_SHOT);
        long startTime;
        alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,  (startTime = SystemClock.elapsedRealtime()) +
                min * 60000, alarmIntent);

        editor.putBoolean(getString(R.string.alarm_active), true);
        editor.putLong(getString(R.string.start_alarm_time), startTime);
        editor.commit();

        showNotification(startTime, 60000);
        startHandler();
    }

    private void showNotification(long startTime, long delay) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_query_builder)
                        .setContentTitle(getString(R.string.timer))
                        .setContentText(seekArc.getProgress() + getString(R.string.minutes_remaining));
        Intent resultIntent = new Intent(this, TimerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TimerActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        int id = 0;

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(id, notification);

        Intent nIntent = new Intent(this, UpdateNotificationAlarmReceiver.class);
        nIntent.putExtra(getString(R.string.time), seekArc.getProgress());

        notificationIntent = PendingIntent.getBroadcast(this, 1002, nIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,  startTime + delay, notificationIntent);
    }

    private void startHandler() {
        if(seekArc.getProgress() != 0) {
            handler.postDelayed(decrementTimerThread, 60000);
        } else {
            stopTimer();
        }
    }

    private Runnable decrementTimerThread = new Runnable() {
        public void run() {
        int currentDuration = Integer.parseInt(seekArcProgress.getText().toString());
        if( currentDuration > 0) {
            seekArcProgress.setText(String.valueOf(currentDuration - 1));
        }
        if (Integer.parseInt(seekArcProgress.getText().toString()) == 0) {
            stopTimer();
            finish();
        } else {
            handler.postDelayed(decrementTimerThread, 60000);
        }
        }
    };

    private void stopTimer() {
        cancelAlarm();
        seekArcProgressAnim.cancel();
        handler.removeCallbacks(decrementTimerThread);
        seekArc.setEnabled(true);
    }

    private void cancelAlarm() {
        notificationManager.cancel(0);

        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
            alarmMgr.cancel(notificationIntent);

        } else {
            alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, StopMusicAlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_ONE_SHOT);

            Intent nIntent = new Intent(this, UpdateNotificationAlarmReceiver.class);
            notificationIntent = PendingIntent.getBroadcast(this, 1002, nIntent, PendingIntent.FLAG_ONE_SHOT);

            alarmMgr.cancel(alarmIntent);
            alarmMgr.cancel(notificationIntent);
        }
        editor.putBoolean(getString(R.string.alarm_active), false);
        editor.commit();
    }

    private int getPreviousDuration() {
        return sharedPref.getInt(getString(R.string.prev_duration), 0);
    }

    private void setPreviousDuration(int min) {
        editor.putInt(getString(R.string.prev_duration), min);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, Settings.class);
            i.putExtra(getString(R.string.no_headers), true);
            i.putExtra(getString(R.string.show_fragment), Settings.FragmentSettingsBasic.class.getName());
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Integer.parseInt(sharedPref.getString(getString(R.string.key_max_time), getString(R.string.hour_in_min))) != (seekArc.getMax())) {
            seekArc.setMax(Integer.parseInt(sharedPref.getString(getString(R.string.key_max_time), getString(R.string.hour_in_min))));
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(decrementTimerThread);
        super.onDestroy();
    }

}


