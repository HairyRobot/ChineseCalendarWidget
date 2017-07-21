package hk.ccw.chinesecalendarwidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;

public class UpdateWidgetService extends IntentService {

	public UpdateWidgetService() {
		super("UpdateWidgetService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		WidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId);
	}
}
