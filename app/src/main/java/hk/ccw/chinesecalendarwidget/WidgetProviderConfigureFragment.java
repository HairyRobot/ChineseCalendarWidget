package hk.ccw.chinesecalendarwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class WidgetProviderConfigureFragment extends PreferenceFragment {

	private static List<ApplicationInfo> sAappInfo;

	private ListPreference mFontTypeView;
	private ListPreference mBackgroundColourView;

	private int mAppWidgetId;
	private int mWeekdayFontColour;
	private int mSundayFontColour;
	private boolean mCustomSundayFontColourFlag;
	private String mAppToOpen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sAappInfo = null;
		mAppToOpen = "";

		Bundle extras = getActivity().getIntent().getExtras();
		mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		addPreferencesFromResource(R.xml.preferences);

		mFontTypeView = (ListPreference) findPreference(Prefs.FONT_TYPE);
		setFontTypeSummary(Prefs.DEFAULT_FONT_TYPE);
		mFontTypeView.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				setFontTypeSummary(newValue.toString());
				return true;
			}
		});

		ColorPickerPreference weekdayFontColourView = (ColorPickerPreference) findPreference(Prefs.WEEKDAY_FONT_COLOUR);
		weekdayFontColourView.setAlphaSliderEnabled(false);
		weekdayFontColourView.setTitle(R.string.pref_t_weekday_font_colour);
		weekdayFontColourView.setSummary("#ff008000");
		mWeekdayFontColour = getResources().getInteger(R.integer.COLOR_WEEKDAY);
		weekdayFontColourView.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mWeekdayFontColour = Integer.valueOf(String.valueOf(newValue));
				preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
				return true;
			}
		});

		SwitchPreference customSundayFontColourFlagView = (SwitchPreference) findPreference(Prefs.CUSTOM_SUNDAY_FONT_COLOUR_FLAG);
		mCustomSundayFontColourFlag = true;
		customSundayFontColourFlagView.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (newValue instanceof Boolean) {
					mCustomSundayFontColourFlag = (Boolean) newValue;
				}
				return true;
			}
		});

		ColorPickerPreference sundayFontColourView = (ColorPickerPreference) findPreference(Prefs.SUNDAY_FONT_COLOUR);
		sundayFontColourView.setAlphaSliderEnabled(false);
		sundayFontColourView.setTitle(R.string.pref_t_sunday_font_colour);
		sundayFontColourView.setSummary("#ffff0000");
		mSundayFontColour = getResources().getInteger(R.integer.COLOR_SUNDAY);
		sundayFontColourView.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mSundayFontColour = Integer.valueOf(String.valueOf(newValue));
				preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
				return true;
			}
		});

		mBackgroundColourView = (ListPreference) findPreference(Prefs.BACKGROUND_COLOUR);
		setBackgroundColourSummary(Prefs.DEFAULT_BACKGROUND_COLOUR);
		mBackgroundColourView.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				setBackgroundColourSummary(newValue.toString());
				return true;
			}
		});

		PreferenceScreen appToOpenView = (PreferenceScreen) findPreference(Prefs.APP_TO_OPEN);
		appToOpenView.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (sAappInfo != null) {
					showSelectAppDialog(getActivity());
				}
				return true;
			}
		});

		PreferenceScreen addWidgetView = (PreferenceScreen) findPreference("addWidget");
		addWidgetView.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				saveWidgetSettings();
				return true;
			}
		});

		PreferenceScreen versionView = (PreferenceScreen) findPreference("version");
		versionView.setTitle(getString(R.string.about_t_version, getAppVersionName(getActivity())));

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				sAappInfo = getLaunchable(getActivity());
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void setFontTypeSummary(String value) {
		String summaryText;
		String[] itemNames = getResources().getStringArray(R.array.font_type_name);
		String[] itemCodes = getResources().getStringArray(R.array.font_type_code);
		int idx;
		for (idx = 0; idx < itemCodes.length; idx++) {
			if (itemCodes[idx].equals(value)) {
				break;
			}
		}
		summaryText = itemNames[idx];
		mFontTypeView.setSummary(summaryText);
	}

	private void setBackgroundColourSummary(String value) {
		String summaryText;
		String[] itemNames = getResources().getStringArray(R.array.background_colour_name);
		String[] itemCodes = getResources().getStringArray(R.array.background_colour_code);
		int idx;
		for (idx = 0; idx < itemCodes.length; idx++) {
			if (itemCodes[idx].equals(value)) {
				break;
			}
		}
		summaryText = itemNames[idx];
		mBackgroundColourView.setSummary(summaryText);
	}

	private void saveWidgetSettings() {
		Intent broadcast = new Intent(getActivity(), WidgetProvider.class);
		broadcast.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{mAppWidgetId});
		getActivity().sendBroadcast(broadcast);
		if (!mCustomSundayFontColourFlag) {
			mSundayFontColour = mWeekdayFontColour;
		}
		Prefs.setValues(getActivity(), mAppWidgetId, mFontTypeView.getValue(),
				mWeekdayFontColour, mSundayFontColour, mBackgroundColourView.getValue(),
				mAppToOpen);
		Intent result = new Intent();
		result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		getActivity().setResult(Activity.RESULT_OK, result);
		getActivity().finish();
	}

	private static String getAppVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static List<ApplicationInfo> getLaunchable(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resInfos = packageManager.queryIntentActivities(intent, 0);
		HashSet<String> packageNames = new HashSet<>(0);
		List<ApplicationInfo> appInfos = new ArrayList<>(0);

		for (ResolveInfo resolveInfo : resInfos) {
			packageNames.add(resolveInfo.activityInfo.packageName);
		}

		for (String packageName : packageNames) {
			try {
				appInfos.add(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
			} catch (PackageManager.NameNotFoundException ignored) {
			}
		}

		Collections.sort(appInfos, new ApplicationInfo.DisplayNameComparator(packageManager));
		return appInfos;
	}

	private void showSelectAppDialog(final Context context) {
		final PackageManager pm = context.getPackageManager();
		final CharSequence[] items;
		items = new CharSequence[sAappInfo.size()];

		for (int i = 0; i < sAappInfo.size(); i++) {
			items[i] = String.valueOf(pm.getApplicationLabel(sAappInfo.get(i)) + "\n  "
					+ sAappInfo.get(i).packageName
			);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				mAppToOpen = sAappInfo.get(item).packageName;
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				mAppToOpen = "";
			}
		});
		builder.create();
		builder.show();
	}
}
