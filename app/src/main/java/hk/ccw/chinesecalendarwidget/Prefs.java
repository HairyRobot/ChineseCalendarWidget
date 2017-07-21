package hk.ccw.chinesecalendarwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

class Prefs {

	private static final String PREF_PREFIX_KEY = "appWidget_";

	static final String DEFAULT_FONT_TYPE = "0";
	private static final String DEFAULT_WEEKDAY_FONT_COLOUR = "-16744448";	// 0x008000
	private static final String DEFAULT_SUNDAY_FONT_COLOUR = "-65536";		// 0xFF0000
	static final String DEFAULT_BACKGROUND_COLOUR = "3";
	private static final String DEFAULT_APP_TO_OPEN = "";

	static final String FONT_TYPE = "fontType";
	static final String WEEKDAY_FONT_COLOUR = "weekdayFontColour";
	static final String CUSTOM_SUNDAY_FONT_COLOUR_FLAG = "customSundayFontColourFlag";
	static final String SUNDAY_FONT_COLOUR = "sundayFontColour";
	static final String BACKGROUND_COLOUR = "backgroundColour";
	static final String APP_TO_OPEN = "appToOpen";

	private static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	static int getFontType(Context context, int widgetId) {
		String value = getValue(context, widgetId, FONT_TYPE, DEFAULT_FONT_TYPE);
		return Integer.valueOf(value);
	}

	static int getWeekdayFontColour(Context context, int widgetId) {
		String value = getValue(context, widgetId, WEEKDAY_FONT_COLOUR, DEFAULT_WEEKDAY_FONT_COLOUR);
		return Integer.valueOf(value);
	}

	static int getSundayFontColour(Context context, int widgetId) {
		String value = getValue(context, widgetId, SUNDAY_FONT_COLOUR, DEFAULT_SUNDAY_FONT_COLOUR);
		return Integer.valueOf(value);
	}

	static int getBackgroundColour(Context context, int widgetId) {
		String value = getValue(context, widgetId, BACKGROUND_COLOUR, DEFAULT_BACKGROUND_COLOUR);
		switch (Integer.valueOf(value)) {
			case 1:
				return context.getResources().getIdentifier("bg_white", "drawable", context.getPackageName());
			case 2:
				return context.getResources().getIdentifier("bg_white_percent_90", "drawable", context.getPackageName());
			case 3:
				return context.getResources().getIdentifier("bg_white_percent_80", "drawable", context.getPackageName());
			case 4:
				return context.getResources().getIdentifier("bg_white_percent_70", "drawable", context.getPackageName());
			case 5:
				return context.getResources().getIdentifier("bg_white_percent_60", "drawable", context.getPackageName());
			case 6:
				return context.getResources().getIdentifier("bg_white_percent_50", "drawable", context.getPackageName());
			case 7:
				return context.getResources().getIdentifier("bg_white_percent_40", "drawable", context.getPackageName());
			case 8:
				return context.getResources().getIdentifier("bg_white_percent_30", "drawable", context.getPackageName());
			case 9:
				return context.getResources().getIdentifier("bg_white_percent_20", "drawable", context.getPackageName());
			case 10:
				return context.getResources().getIdentifier("bg_white_percent_10", "drawable", context.getPackageName());
			case 11:
				return context.getResources().getIdentifier("bg_black", "drawable", context.getPackageName());
			case 12:
				return context.getResources().getIdentifier("bg_black_percent_90", "drawable", context.getPackageName());
			case 13:
				return context.getResources().getIdentifier("bg_black_percent_80", "drawable", context.getPackageName());
			case 14:
				return context.getResources().getIdentifier("bg_black_percent_70", "drawable", context.getPackageName());
			case 15:
				return context.getResources().getIdentifier("bg_black_percent_60", "drawable", context.getPackageName());
			case 16:
				return context.getResources().getIdentifier("bg_black_percent_50", "drawable", context.getPackageName());
			case 17:
				return context.getResources().getIdentifier("bg_black_percent_40", "drawable", context.getPackageName());
			case 18:
				return context.getResources().getIdentifier("bg_black_percent_30", "drawable", context.getPackageName());
			case 19:
				return context.getResources().getIdentifier("bg_black_percent_20", "drawable", context.getPackageName());
			case 20:
				return context.getResources().getIdentifier("bg_black_percent_10", "drawable", context.getPackageName());
			default:
				return context.getResources().getIdentifier("bg_transparent", "drawable", context.getPackageName());
		}
	}

	static String getAppToOpen(Context context, int widgetId) {
		return getValue(context, widgetId, APP_TO_OPEN, DEFAULT_APP_TO_OPEN);
	}

	private static String getValue(Context context, int widgetId, String key, String defValue) {
		String string = getPrefs(context).getString(PREF_PREFIX_KEY + widgetId, null);
		if (string == null) {
			return defValue;
		}
		JSONObject obj;
		try {
			obj = new JSONObject(string);
			return obj.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return defValue;
		}
	}

	static void setValues(Context context, int widgetId, String fontType,
						  int weekdayFontColour, int sundayFontColour, String backgroundColour,
						  String appToOpen) {
		JSONObject obj = new JSONObject();
		try {
			obj.put(FONT_TYPE, fontType);
			obj.put(WEEKDAY_FONT_COLOUR, "" + weekdayFontColour);
			obj.put(SUNDAY_FONT_COLOUR, "" + sundayFontColour);
			obj.put(BACKGROUND_COLOUR, backgroundColour);
			obj.put(APP_TO_OPEN, appToOpen);
			getPrefs(context).edit().putString(PREF_PREFIX_KEY + widgetId, obj.toString()).apply();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	static void delete(Context context, int widgetId) {
		getPrefs(context).edit().remove(PREF_PREFIX_KEY + widgetId).apply();
	}
}
