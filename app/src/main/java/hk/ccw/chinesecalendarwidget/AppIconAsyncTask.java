package hk.ccw.chinesecalendarwidget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class AppIconAsyncTask extends AsyncTask<Void, Integer, Drawable> {

	private Context mContext;
	private ImageView mImageView;
	private String mPackageName;
	private boolean mCancel = false;

	public AppIconAsyncTask(Context context, ImageView imageView, String packageName) {
		mContext = context;
		mImageView = imageView;
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
		if (!mCancel) return getAppIcon(mContext, mPackageName);
		return null;
	}

	@Override
	protected void onPostExecute(Drawable drawable) {
		super.onPostExecute(drawable);
		if (drawable != null && !mCancel) {
			mImageView.setImageDrawable(drawable);
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
