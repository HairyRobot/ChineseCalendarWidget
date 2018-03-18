package hk.ccw.chinesecalendarwidget;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SelectAppAdapter extends BaseAdapter {

	private static class ViewHolder {
		ImageView resIcon;
		TextView resAppName;
		TextView resPackageName;
		AppIconAsyncTask iconLoader;
	}

	private List<SelectAppEntry> mList;

	private Context mContext;
	private LayoutInflater mInflater;

	public SelectAppAdapter(Activity context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<SelectAppEntry> data) {
		mList = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.dialog_v_item, parent, false);
			holder = new ViewHolder();
			holder.resIcon = (ImageView) convertView.findViewById(R.id.icon);
			holder.resAppName = (TextView) convertView.findViewById(R.id.app_name);
			holder.resPackageName = (TextView) convertView.findViewById(R.id.package_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			if (holder.iconLoader != null) {
				holder.iconLoader.cancel();
			}
		}

		final SelectAppEntry currentEntry = (SelectAppEntry) getItem(position);

		try {
			holder.resAppName.setText(currentEntry.getAppName());
			holder.resPackageName.setText(currentEntry.getPackageName());
			holder.iconLoader = new AppIconAsyncTask(mContext, holder.resIcon, currentEntry.getPackageName());
			holder.iconLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		try {
			return mList == null ? null : mList.get(position);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
