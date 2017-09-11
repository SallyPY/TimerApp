package hu.ait.timerapp.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.ait.timerapp.R;

public class AppListAdapter extends BaseAdapter
 {
     private List<ResolveInfo> appList;
     private Context ctxt;

     public AppListAdapter(Context ctxt, ArrayList<ResolveInfo> list) {
         appList = list;
         this.ctxt = ctxt;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         View row;
         LayoutInflater inflater = (LayoutInflater) ctxt
                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         if (convertView == null) {
             row = inflater.inflate(R.layout.app_row, parent,
                     false);
         } else {
             row = convertView;
         }
         ResolveInfo app = appList.get(position);
         TextView name = (TextView) row.findViewById(R.id.tvName);
         name.setText(app.activityInfo.applicationInfo.loadLabel(ctxt.getPackageManager()).toString());

         ImageView imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
         try {
             Drawable icon = ctxt.getPackageManager().getApplicationIcon(app.activityInfo.applicationInfo.packageName);
             imgIcon.setImageDrawable(icon);
         } catch (PackageManager.NameNotFoundException e) {
             e.printStackTrace();
         };

         return row;
     }

     @Override
     public int getCount() {
         return appList.size();
     }

     @Override
     public Object getItem(int position) {
         return appList.get(position);
     }

     @Override
     public long getItemId(int position) {
         return position;
     }
}
