package hk.ccw.chinesecalendarwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateWidgetReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		Intent i = new Intent(context, UpdateWidgetService.class);
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		context.startService(i);
	}
}
