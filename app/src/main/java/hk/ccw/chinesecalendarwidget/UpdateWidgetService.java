package hk.ccw.chinesecalendarwidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;

public class UpdateWidgetService extends IntentService {

	public UpdateWidgetService() {
		super("UpdateWidgetService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this,
				WidgetProvider.class));
		for (int appWidgetId : appWidgetIds) {
			WidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId);
		}
	}
}
