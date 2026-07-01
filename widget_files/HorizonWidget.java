package com.salah.horizon;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HorizonWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) updateWidget(context, mgr, id);
    }

    static void updateWidget(Context context, AppWidgetManager mgr, int id) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_horizon);
        try {
            // Try both SharedPreference store names Capacitor may use
            SharedPreferences prefs = context.getSharedPreferences("CAPStorage", Context.MODE_PRIVATE);
            String json = prefs.getString("featured_event", null);
            if (json == null) {
                prefs = context.getSharedPreferences("CapacitorStorage", Context.MODE_PRIVATE);
                json = prefs.getString("featured_event", null);
            }
            if (json != null) {
                JSONObject ev = new JSONObject(json);
                String name = ev.getString("name");
                long targetMs = ev.getLong("targetMs");
                long diff = targetMs - System.currentTimeMillis();
                views.setTextViewText(R.id.widget_event_name, name);
                if (diff > 0) {
                    long d = diff / 86400000L;
                    long h = (diff % 86400000L) / 3600000L;
                    long m = (diff % 3600000L) / 60000L;
                    views.setTextViewText(R.id.widget_countdown, d + "d  " + h + "h  " + m + "m");
                    String date = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                            .format(new Date(targetMs));
                    views.setTextViewText(R.id.widget_date, date);
                } else {
                    views.setTextViewText(R.id.widget_countdown, "Has passed");
                    views.setTextViewText(R.id.widget_date, "");
                }
            } else {
                views.setTextViewText(R.id.widget_event_name, "Open Horizon to load");
                views.setTextViewText(R.id.widget_countdown, "");
                views.setTextViewText(R.id.widget_date, "");
            }
        } catch (Exception e) {
            views.setTextViewText(R.id.widget_event_name, "Horizon");
            views.setTextViewText(R.id.widget_countdown, "Tap to open app");
            views.setTextViewText(R.id.widget_date, "");
        }
        mgr.updateAppWidget(id, views);
    }
}
