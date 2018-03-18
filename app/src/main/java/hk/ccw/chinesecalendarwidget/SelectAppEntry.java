package hk.ccw.chinesecalendarwidget;

import android.content.Context;
import android.content.pm.PackageManager;

public class SelectAppEntry {

	private String mPackageName;
	private String mAppName;

	public SelectAppEntry(Context context, String packageName) {
		mPackageName = packageName;
		mAppName = getAppLabel(context, packageName);
	}

	public String getPackageName() {
		return mPackageName;
	}

	public String getAppName() {
		return mAppName;
	}

	private static String getAppLabel(Context context, String packageName) {
		String appLabel;

		try {
			PackageManager pm = context.getPackageManager();
			CharSequence label = pm.getApplicationInfo(packageName, 0).loadLabel(pm);
			appLabel = label != null ? label.toString() : packageName;
		} catch (PackageManager.NameNotFoundException e) {
			appLabel = packageName;
			e.printStackTrace();
		}

		return appLabel;
	}
}
