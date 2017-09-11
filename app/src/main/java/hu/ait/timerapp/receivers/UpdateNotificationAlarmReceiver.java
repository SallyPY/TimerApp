package hu.ait.timerapp.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import hu.ait.timerapp.R;
import hu.ait.timerapp.TimerActivity;

public class UpdateNotificationAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctxt, Intent intent) {

        int time = intent.getIntExtra(ctxt.getString(R.string.time), 0);
        AlarmManager alarmMgr = (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);

        NotificationManager mNotificationManager =
                (NotificationManager) ctxt.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctxt)
                        .setSmallIcon(R.drawable.ic_query_builder)
                        .setContentTitle(ctxt.getString(R.string.timer))
                        .setContentText((time - 1) + ctxt.getString(R.string.minutes_remaining));
        Intent resultIntent = new Intent(ctxt, TimerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctxt);
        stackBuilder.addParentStack(TimerActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        int id = 0;

        intent.putExtra(ctxt.getString(R.string.time), time - 1);
        PendingIntent notificationIntent = PendingIntent.getBroadcast(ctxt, 1002, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(time > 1) {
            alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,  SystemClock.elapsedRealtime() + 60000, notificationIntent);
            Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            mNotificationManager.notify(id, notification);
        } else {
            alarmMgr.cancel(notificationIntent);
            mNotificationManager.cancel(0);
        }
    }
}
