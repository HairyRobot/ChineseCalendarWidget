package hk.ccw.chinesecalendarwidget;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobUpdateWidgetService extends JobService {
	private static final String TAG = JobUpdateWidgetService.class.getSimpleName();

	@Override
	public boolean onStartJob(JobParameters params) {
		Log.i(TAG, "onStartJob");
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this,
				WidgetProvider.class));
		Intent broadcast = new Intent(this, WidgetProvider.class);
		broadcast.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		sendBroadcast(broadcast);
		return true;    // job done
	}

	@Override
	public boolean onStopJob(JobParameters params) {
		Log.i(TAG, "onStopJob");
		return false;
	}
}
