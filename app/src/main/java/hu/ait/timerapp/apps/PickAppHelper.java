package hu.ait.timerapp.apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

public class PickAppHelper {

    public static List<ResolveInfo> getMediaApps(Context c) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_APP_MUSIC);
        return c.getPackageManager().queryIntentActivities(intent, 0);
    }

    public static List<ResolveInfo> getAllApps(Context c) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return c.getPackageManager().queryIntentActivities(intent, 0);
    }
}
