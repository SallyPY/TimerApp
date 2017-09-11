package hu.ait.timerapp;

import android.app.DialogFragment;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import hu.ait.timerapp.apps.PickAppDialogFragment;

public class AppPreference extends Preference {
    private Context ctxt;

    public AppPreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        this.ctxt = ctxt;
    }
    protected void showDialog() {
        Settings activity = (Settings) ctxt;
        DialogFragment newFragment = PickAppDialogFragment.newInstance();
        newFragment.show(activity.getFragmentManager(), ctxt.getString(R.string.dialog));
    }
}

