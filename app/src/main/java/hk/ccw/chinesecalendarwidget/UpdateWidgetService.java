package hk.ccw.chinesecalendarwidget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class UpdateWidgetService extends IntentService {

	private static final String NOTIFICATION_CHANNEL_ID = "channelId";
	private static final String NOTIFICATION_CHANNEL_NAME = "channelName";
	private static final int NOTIFICATION_ID = 3142;

	public UpdateWidgetService() {
		super("UpdateWidgetService");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {    // API-26
			startForeground(NOTIFICATION_ID, buildForegroundNotification());
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this,
				WidgetProvider.class));
		for (int appWidgetId : appWidgetIds) {
			WidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {    // API-26
			stopForeground(true);
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	private Notification buildForegroundNotification() {
		NotificationChannel notificationChannel = new NotificationChannel(
				NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
				NotificationManager.IMPORTANCE_LOW);
		notificationChannel.enableLights(false);
		notificationChannel.enableVibration(false);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.createNotificationChannel(notificationChannel);

		Notification.Builder builder = new Notification.Builder(getApplicationContext());
		builder.setContentTitle(getString(R.string.app_name));
		builder.setContentText(getString(R.string.widget_name));
		builder.setSmallIcon(android.R.drawable.stat_notify_chat);
		builder.setChannelId(NOTIFICATION_CHANNEL_ID);
		builder.setOngoing(true);
		builder.setAutoCancel(true);

		return (builder.build());
	}
}
