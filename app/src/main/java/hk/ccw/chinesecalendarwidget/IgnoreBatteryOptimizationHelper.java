package hk.ccw.chinesecalendarwidget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class IgnoreBatteryOptimizationHelper {

	private static final String PREF_IGNORE_BATTERY_OPTIMIZATION_DONT_SHOW_AGAIN = "ignoreBatteryOptimizationDontShowAgain";

	private static SharedPreferences pref;

	public IgnoreBatteryOptimizationHelper() {
	}

	public void showDialog(final Activity activity) {
		pref = PreferenceManager.getDefaultSharedPreferences(activity);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {    // API-23
			try {
				String packageName = activity.getPackageName();
				PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
				if (!pm.isIgnoringBatteryOptimizations(packageName)) {
					if (!pref.getBoolean(PREF_IGNORE_BATTERY_OPTIMIZATION_DONT_SHOW_AGAIN, false)) {
						showAlertDialog(activity);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showAlertDialog(final Activity activity) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		View dialogView = inflater.inflate(R.layout.dialog_message_dontshow, null);
		final TextView messageView = dialogView.findViewById(R.id.message);
		final CheckBox confirmFlagView = dialogView.findViewById(R.id.confirm_flag);

		messageView.setText(R.string.alert_msg_ignore_battery_optimization);

		confirmFlagView.setText(R.string.dont_show_this_again);
		confirmFlagView.setChecked(false);
		confirmFlagView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					SharedPreferences.Editor editor = pref.edit();
					editor.putBoolean(PREF_IGNORE_BATTERY_OPTIMIZATION_DONT_SHOW_AGAIN, true);
					editor.apply();
				} else {
					SharedPreferences.Editor editor = pref.edit();
					editor.putBoolean(PREF_IGNORE_BATTERY_OPTIMIZATION_DONT_SHOW_AGAIN, false);
					editor.apply();
				}
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(dialogView);
		builder.setTitle(R.string.alert_title_ignore_battery_optimization);
		builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				requestDisableBatteryOptimizations(activity);
			}
		});
		builder.setNegativeButton(R.string.btn_cancel, null);
		builder.setOnCancelListener(null);
		builder.create();
		builder.show();
	}

	@SuppressLint("BatteryLife")
	private void requestDisableBatteryOptimizations(final Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {    // API-23
			try {
				String packageName = activity.getPackageName();
				PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
				if (!pm.isIgnoringBatteryOptimizations(packageName)) {
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
					intent.setData(Uri.parse("package:" + packageName));
					activity.startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
