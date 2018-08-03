package hk.ccw.chinesecalendarwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.TimeZone;

public class UpdateWidgetReceiver extends BroadcastReceiver {
	private static final String TAG = UpdateWidgetReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive");
		// https://stackoverflow.com/questions/16113459/timezone-changed-intent-being-received-every-few-seconds
		if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
			String oldTimezone = Prefs.getPrefs(context).getString(Prefs.PREF_TIMEZONE, null);
			String newTimezone = TimeZone.getDefault().getID();
			long now = System.currentTimeMillis();

			if (oldTimezone == null || TimeZone.getTimeZone(oldTimezone).getOffset(now)
					!= TimeZone.getTimeZone(newTimezone).getOffset(now)) {
				Prefs.getPrefs(context).edit().putString(Prefs.PREF_TIMEZONE, newTimezone).apply();
			} else {
				return;
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {    // API-26
			context.startForegroundService(new Intent(context, UpdateWidgetService.class));
		} else {
			context.startService(new Intent(context, UpdateWidgetService.class));
		}
	}
}
