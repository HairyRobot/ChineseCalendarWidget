package hk.ccw.chinesecalendarwidget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

public class UpdateWidgetService extends IntentService {
	private static final String TAG = UpdateWidgetService.class.getSimpleName();

	private static final int NOTIFICATION_ID = 3142;

	private PowerManager.WakeLock wakeLock;

	public UpdateWidgetService() {
		super("UpdateWidgetService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"UpdateWidgetService:Wakelock");
		wakeLock.acquire(3000L);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {    // API-26
			startForeground(NOTIFICATION_ID, buildForegroundNotification());
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "onHandleIntent");
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this,
				WidgetProvider.class));
		Intent broadcast = new Intent(this, WidgetProvider.class);
		broadcast.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		sendBroadcast(broadcast);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		wakeLock.release();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {    // API-26
			stopForeground(true);
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	private Notification buildForegroundNotification() {
		Notification.Builder builder = new Notification.Builder(getApplicationContext());
		builder.setContentTitle(getString(R.string.app_name));
		builder.setContentText(getString(R.string.widget_name));
		builder.setSmallIcon(android.R.drawable.stat_notify_chat);
		builder.setChannelId(App.NOTIFICATION_CHANNEL_ID);
		builder.setOngoing(true);
		builder.setAutoCancel(true);
		return (builder.build());
	}
}
