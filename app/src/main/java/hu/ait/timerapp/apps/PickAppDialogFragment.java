package hu.ait.timerapp.apps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hu.ait.timerapp.R;

public class PickAppDialogFragment extends DialogFragment {

    public static PickAppDialogFragment newInstance() {
        PickAppDialogFragment frag = new PickAppDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context ctxt = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(ctxt, R.style.MyDialogTheme);
        builder.setTitle(R.string.available_apps);

        LayoutInflater inflater = LayoutInflater.from(ctxt);
        View dialogView = inflater.inflate(R.layout.app_list_main, null);

        final AlertDialog dialog;

        final List<ResolveInfo> mediaApps = PickAppHelper.getMediaApps(ctxt);
        ArrayList<ResolveInfo> mediaList = new ArrayList<>(mediaApps);

        ListView mediaListView = (ListView) dialogView.findViewById(R.id.listMedia);
        mediaListView.setAdapter(new AppListAdapter(ctxt, mediaList));

        List<ResolveInfo> allApps = PickAppHelper.getAllApps(ctxt);
        final ArrayList<ResolveInfo> allList = new ArrayList<>(allApps);

        Collections.sort(allList, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                return (o1.activityInfo.applicationInfo.loadLabel(ctxt.getPackageManager()).toString())
                        .compareTo(o2.activityInfo.applicationInfo.loadLabel(ctxt.getPackageManager()).toString());
            }
        });

        ListView allListView = (ListView) dialogView.findViewById(R.id.listAll);
        allListView.setAdapter(new AppListAdapter(ctxt, allList));

        builder.setView(dialogView);
        dialog = builder.create();

        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctxt).edit();

        mediaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                editor.putString(ctxt.getString(R.string.pref_app), mediaApps.get(position).activityInfo.packageName);
                editor.commit();
            }
        });

        allListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                editor.putString(ctxt.getString(R.string.pref_app), allList.get(position).activityInfo.packageName);
                editor.commit();
            }
        });
        return dialog;
    }


}
