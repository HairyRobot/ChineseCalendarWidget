package hk.ccw.chinesecalendarwidget;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.util.Calendar;

public class WidgetProvider extends AppWidgetProvider {
	private static final String TAG = WidgetProvider.class.getSimpleName();

	private static final int JOB_ID_ALARM = 101;
	private static final double TEXT_HEIGHT = 0.7;

	private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		Log.i(TAG, "updateAppWidget: " + appWidgetId);
		Pair<Integer, Integer> availableSize = getTextAvailableSize(context, appWidgetId);
		if (availableSize == null) {
			return;
		}

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

		Calendar cal = Calendar.getInstance();
		int[] lunarDate = LunarCalendarUtil.gregorianToLunar(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

		LunarCalendarUtil.setFontType(Prefs.getFontType(context, appWidgetId));
		StringBuilder buf = new StringBuilder();
		// 辛丑
		buf.append(LunarCalendarUtil.numToChineseYear(lunarDate[0]));
		// 年
		buf.append(LunarCalendarUtil.getMiscName(4)); // 年
		// (肖牛)
		buf.append("(")
				.append(LunarCalendarUtil.getMiscName(0)) // 肖
				.append(LunarCalendarUtil.getZodiacName(LunarCalendarUtil.getZodiac(lunarDate[0])))
				.append(")");
		String widgetText1 = buf.toString();
		buf.setLength(0);
		// 正月
		if (lunarDate[3] == 1) {
			buf.append(LunarCalendarUtil.getMiscName(1)); // 閏
		}
		buf.append(LunarCalendarUtil.numToChineseMonth(lunarDate[1]));
		// 初一
		buf.append(LunarCalendarUtil.numToChineseDay(lunarDate[2]));
		// 日
		buf.append(LunarCalendarUtil.getMiscName(6)); // 日
		// 午
		if (Prefs.getShowHourFlag(context, appWidgetId)) {
			Calendar now = Calendar.getInstance();
			int hour = now.get(Calendar.HOUR_OF_DAY);
			buf.append(LunarCalendarUtil.numToChineseHour(hour));
			// 時
			buf.append(LunarCalendarUtil.getMiscName(7)); // 時
		}
		String widgetText2 = buf.toString();

		float widgetText1Size = TextSizer.getTextSize(context, widgetText1, availableSize);
		float widgetText2Size = TextSizer.getTextSize(context, widgetText2, availableSize);
		float textSize = (widgetText1Size > widgetText2Size) ? widgetText2Size : widgetText1Size;

		int backgroundColour = Prefs.getBackgroundColour(context, appWidgetId);
		remoteViews.setInt(R.id.widget_panel, "setBackgroundResource", backgroundColour);

		int fontColor;
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			fontColor = Prefs.getSundayFontColour(context, appWidgetId);
		} else {
			fontColor = Prefs.getWeekdayFontColour(context, appWidgetId);
		}

		remoteViews.setTextViewText(R.id.text1, widgetText1);
		remoteViews.setTextViewTextSize(R.id.text1, TypedValue.COMPLEX_UNIT_DIP, textSize);
		remoteViews.setTextColor(R.id.text1, fontColor);

		remoteViews.setTextViewText(R.id.text2, widgetText2);
		remoteViews.setTextViewTextSize(R.id.text2, TypedValue.COMPLEX_UNIT_DIP, textSize);
		remoteViews.setTextColor(R.id.text2, fontColor);

		String packageName = Prefs.getAppToOpen(context, appWidgetId);
		if (!TextUtils.isEmpty(packageName)) {
			PackageManager manager = context.getPackageManager();
			Intent i = manager.getLaunchIntentForPackage(packageName);
			if (i != null) {
				i.addCategory(Intent.CATEGORY_LAUNCHER);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pi = PendingIntent.getActivity(context, appWidgetId, i, 0);
				remoteViews.setOnClickPendingIntent(R.id.widget_panel, pi);
			}
		}
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(TAG, "onUpdate");
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
		if (appWidgetIds.length > 0) {
			scheduleAlarm(context);
		}
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
										  int appWidgetId, Bundle newOptions) {
		Log.i(TAG, "onAppWidgetOptionsChanged");
		updateAppWidget(context, appWidgetManager, appWidgetId);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.i(TAG, "onDeleted");
		for (int appWidgetId : appWidgetIds) {
			Prefs.delete(context, appWidgetId);
			Log.d(TAG, "deleted appWidgetId: " + appWidgetId);
		}
	}

	private static void scheduleAlarm(Context context) {
		Log.i(TAG, "scheduleAlarm");
		Intent intent = new Intent(context, UpdateWidgetReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar now = Calendar.getInstance();
		Calendar nextHour = Calendar.getInstance();
		nextHour.add(Calendar.HOUR, 1);
		nextHour.set(Calendar.MINUTE, 0);
		nextHour.set(Calendar.SECOND, 0);
		Log.d(TAG, "now: " + now.getTimeInMillis());
		Log.d(TAG, "nextHour: " + nextHour.getTimeInMillis());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // API-21
			long duration = nextHour.getTimeInMillis() - now.getTimeInMillis();
			Log.d(TAG, "duration: " + duration);
			scheduleJobAlarm(context, duration);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    // API-19
			am.setExact(AlarmManager.RTC, nextHour.getTimeInMillis(), pi);
		} else {
			am.set(AlarmManager.RTC, nextHour.getTimeInMillis(), pi);
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static void scheduleJobAlarm(Context context, long duration) {
		ComponentName componentName = new ComponentName(context, JobUpdateWidgetService.class);
		JobInfo.Builder builder = new JobInfo.Builder(JOB_ID_ALARM, componentName);
		if (duration < 0) {
			duration = 1000 * 60 * 60;    // 60 minutes
		}
		builder.setMinimumLatency(duration);
		builder.setOverrideDeadline(duration);
		builder.setRequiresDeviceIdle(false);
		JobInfo jobInfo = builder.build();
		JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
		jobScheduler.schedule(jobInfo);
	}

	private static Pair<Integer, Integer> getTextAvailableSize(Context context, int widgetId) {
		Pair<Integer, Integer> size = getWidgetSize(context, widgetId);
		if (size == null) {
			return null;
		}
		int width = size.first;
		int height = size.second;
		height *= TEXT_HEIGHT;
		return Pair.create((int) (width * .9), (int) (height * .85));
	}

	private static Pair<Integer, Integer> getWidgetSize(Context context, int widgetId) {
		boolean portrait = context.getResources().getConfiguration().orientation == 1;
		String w = portrait ? AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH : AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
		String h = portrait ? AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT : AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int width = appWidgetManager.getAppWidgetOptions(widgetId).getInt(w);
		int height = appWidgetManager.getAppWidgetOptions(widgetId).getInt(h);
		height = height / 2;
		return Pair.create(width, height);
	}
}
