package hk.ccw.chinesecalendarwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.TimeZone;

public class UpdateWidgetReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
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

		context.startService(new Intent(context, UpdateWidgetService.class));
	}
}
