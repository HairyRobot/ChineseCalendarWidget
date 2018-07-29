package hk.ccw.chinesecalendarwidget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class AppIconAsyncTask extends AsyncTask<Void, Integer, Drawable> {

	private WeakReference<Context> mContextRef;
	private WeakReference<ImageView> mImageViewRef;
	private String mPackageName;
	private boolean mCancel = false;

	public AppIconAsyncTask(Context context, ImageView imageView, String packageName) {
		mContextRef = new WeakReference<>(context);
		mImageViewRef = new WeakReference<>(imageView);
		mPackageName = packageName;
	}

	public void cancel() {
		mCancel = true;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Drawable doInBackground(Void... voids) {
		Context context = mContextRef.get();
		if (context == null) return null;
		if (!mCancel) return getAppIcon(context, mPackageName);
		return null;
	}

	@Override
	protected void onPostExecute(Drawable drawable) {
		super.onPostExecute(drawable);
		if (drawable != null && !mCancel) {
			ImageView imageView = mImageViewRef.get();
			if (imageView != null) imageView.setImageDrawable(drawable);
		}
	}

	private static Drawable getAppIcon(Context context, String packageName) {
		Drawable icon = null;

		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
			icon = appInfo.loadIcon(pm);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return icon;
	}
}
