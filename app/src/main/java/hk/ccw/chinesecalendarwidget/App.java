package hk.ccw.chinesecalendarwidget;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

	public static final String NOTIFICATION_CHANNEL_ID = "FOREGROUND_SERVICE_STATUS";
	private static final String NOTIFICATION_CHANNEL_NAME = "Foreground service status";

	@Override
	public void onCreate() {
		super.onCreate();
		createNotificationChannel();
	}

	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {    // API-26
			NotificationChannel channel = new NotificationChannel(
					NOTIFICATION_CHANNEL_ID,
					NOTIFICATION_CHANNEL_NAME,
					NotificationManager.IMPORTANCE_LOW);
			channel.enableLights(false);
			channel.enableVibration(false);

			NotificationManager nm = getSystemService(NotificationManager.class);
			nm.createNotificationChannel(channel);
		}
	}
}
