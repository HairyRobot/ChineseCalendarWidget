/*
 * Copyright 2017 HairyRobot
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hk.ccw.chinesecalendarwidget;

import java.util.Calendar;
import java.util.Locale;

public class LunarCalendarUtil {

	/**
	 * 支持轉換的最小農歷年份
	 */
	public static final int MIN_YEAR = 1900;

	/**
	 * 支持轉換的最大農歷年份
	 */
	public static final int MAX_YEAR = 2099;

	// 天干
	private static final String[] STEM_NAMES = {
			"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"
	};

	// 地支
	private static final String[] BRANCH_NAMES = {
			"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"
	};

	// 生肖
	private static final String[][] ZODIAC_NAMES = {
			{"鼠", "牛", "虎", "兔", "龍", "蛇", "馬", "羊", "猴", "雞", "狗", "豬"},
			{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"}
	};

	// 月份 - "十一月", "冬月", "十二月", "臘月", "腊月"
	private static final String[][] MONTH_NAMES = {
			{"正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"},
			{"正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"}
	};

	// 日期
	private static final String DAY_NAMES[] = {
			"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
			"十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
			"廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
	};

	// 雜項名
	private static final String[][] MISC_NAMES = {
			{"肖", "閏", "大", "小", "年", "月", "日"},
			{"肖", "闰", "大", "小", "年", "月", "日"}
	};

	private static int sFontType = 0;

	public static void setFontType(int type) {
		if (type == 1) {
			sFontType = 1; // Simplified Chinese
		} else {
			sFontType = 0; // Traditional Chinese
		}
	}

	/**
	 * 傳回生肖的漢字
	 *
	 * @param index 索引 (0為鼠..11為豬)
	 * @return 生肖的漢字
	 */
	public static String getZodiacName(int index) {
		return ZODIAC_NAMES[sFontType][index];
	}

	/**
	 * 數字年份轉換為天干的相應索引
	 *
	 * @param year 要計算的年份
	 * @return year年的天干，0為甲..9為癸
	 */
	public static int getHeavenlyStem(int year) {
		return (year + 6) % 10;
	}

	/**
	 * 數字年份轉換為地支的相應索引
	 *
	 * @param year 要計算的年份
	 * @return year年的地支，0為子..11為亥
	 */
	public static int getEarthlyBranche(int year) {
		return (year + 8) % 12;
	}

	/**
	 * 數字年份轉換為生肖的相應索引
	 *
	 * @param year 要計算的年份
	 * @return year年的生肖，0為鼠..11為豬
	 */
	public static int getZodiac(int year) {
		return (year + 8) % 12;
	}

	/**
	 * 數字年份轉換為農歷年份的漢字
	 *
	 * @param year 年份
	 * @return 年份的漢字
	 */
	public static String numToChineseYear(int year) {
		return STEM_NAMES[getHeavenlyStem(year)]
				+ BRANCH_NAMES[getEarthlyBranche(year)]
				+ MISC_NAMES[sFontType][4];
	}

	/**
	 * 數字月份轉換為月份的漢字
	 *
	 * @param month 月份 (1..12)
	 * @return 月份的漢字
	 */
	public static String numToChineseMonth(int month) {
		return MONTH_NAMES[sFontType][month - 1];
	}

	/**
	 * 數字日期轉換為日期的漢字
	 *
	 * @param day 日期 (1..30)
	 * @return 日期的漢字
	 */
	public static String numToChineseDay(int day) {
		return DAY_NAMES[day - 1];
	}

	/**
	 * @param index 索引 (0为"肖", "閏", "大", "小", "年", "月", "日")
	 * @return 雜項名的漢字
	 */
	public static String getMiscName(int index) {
		return MISC_NAMES[sFontType][index];
	}

	/**
	 * 用來表示1900年到2099年間農歷年份的相關信息，共24位bit的16進制表示，其中：
	 * 1. 前4位表示該年閏哪個月﹔
	 * 2. 5-17位表示農歷年份13個月的大小月分布，0表示小，1表示大﹔
	 * 3. 最后7位表示農歷年首（正月初一）對應的公歷日期。
	 * <p>
	 * 以2014年的數據0x955ABF為例說明：
	 * 1001 0101 0101 1010 1011 1111
	 * 閏九月                農歷正月初一對應公歷1月31號
	 */
	private static final int LUNAR_MONTHS_INFO[] = {
			0x84B6BF, //1900
			0x04AE53, 0x0A5748, 0x5526BD, 0x0D2650, 0x0D9544, 0x46AAB9, 0x056A4D, 0x09AD42, 0x24AEB6, 0x04AE4A, //1901-1910
			0x6A4DBE, 0x0A4D52, 0x0D2546, 0x5D52BA, 0x0B544E, 0x0D6A43, 0x296D37, 0x095B4B, 0x749BC1, 0x049754, //1911-1920
			0x0A4B48, 0x5B25BC, 0x06A550, 0x06D445, 0x4ADAB8, 0x02B64D, 0x095742, 0x2497B7, 0x04974A, 0x664B3E, //1921-1930
			0x0D4A51, 0x0EA546, 0x56D4BA, 0x05AD4E, 0x02B644, 0x393738, 0x092E4B, 0x7C96BF, 0x0C9553, 0x0D4A48, //1931-1940
			0x6DA53B, 0x0B554F, 0x056A45, 0x4AADB9, 0x025D4D, 0x092D42, 0x2C95B6, 0x0A954A, 0x7B4ABD, 0x06CA51, //1941-1950
			0x0B5546, 0x555ABB, 0x04DA4E, 0x0A5B43, 0x352BB8, 0x052B4C, 0x8A953F, 0x0E9552, 0x06AA48, 0x6AD53C, //1951-1960
			0x0AB54F, 0x04B645, 0x4A5739, 0x0A574D, 0x052642, 0x3E9335, 0x0D9549, 0x75AABE, 0x056A51, 0x096D46, //1961-1970
			0x54AEBB, 0x04AD4F, 0x0A4D43, 0x4D26B7, 0x0D254B, 0x8D52BF, 0x0B5452, 0x0B6A47, 0x696D3C, 0x095B50, //1971-1980
			0x049B45, 0x4A4BB9, 0x0A4B4D, 0xAB25C2, 0x06A554, 0x06D449, 0x6ADA3D, 0x0AB651, 0x095746, 0x5497BB, //1981-1990
			0x04974F, 0x064B44, 0x36A537, 0x0EA54A, 0x86B2BF, 0x05AC53, 0x0AB647, 0x5936BC, 0x092E50, 0x0C9645, //1991-2000
			0x4D4AB8, 0x0D4A4C, 0x0DA541, 0x25AAB6, 0x056A49, 0x7AADBD, 0x025D52, 0x092D47, 0x5C95BA, 0x0A954E, //2001-2010
			0x0B4A43, 0x4B5537, 0x0AD54A, 0x955ABF, 0x04BA53, 0x0A5B48, 0x652BBC, 0x052B50, 0x0A9345, 0x474AB9, //2011-2020
			0x06AA4C, 0x0AD541, 0x24DAB6, 0x04B64A, 0x6a573D, 0x0A4E51, 0x0D2646, 0x5E933A, 0x0D534D, 0x05AA43, //2021-2030
			0x36B537, 0x096D4B, 0xB4AEBF, 0x04AD53, 0x0A4D48, 0x6D25BC, 0x0D254F, 0x0D5244, 0x5DAA38, 0x0B5A4C, //2031-2040
			0x056D41, 0x24ADB6, 0x049B4A, 0x7A4BBE, 0x0A4B51, 0x0AA546, 0x5B52BA, 0x06D24E, 0x0ADA42, 0x355B37, //2041-2050
			0x09374B, 0x8497C1, 0x049753, 0x064B48, 0x66A53C, 0x0EA54F, 0x06B244, 0x4AB638, 0x0AAE4C, 0x092E42, //2051-2060
			0x3C9735, 0x0C9649, 0x7D4ABD, 0x0D4A51, 0x0DA545, 0x55AABA, 0x056A4E, 0x0A6D43, 0x452EB7, 0x052D4B, //2061-2070
			0x8A95BF, 0x0A9553, 0x0B4A47, 0x6B553B, 0x0AD54F, 0x055A45, 0x4A5D38, 0x0A5B4C, 0x052B42, 0x3A93B6, //2071-2080
			0x069349, 0x7729BD, 0x06AA51, 0x0AD546, 0x54DABA, 0x04B64E, 0x0A5743, 0x452738, 0x0D264A, 0x8E933E, //2081-2090
			0x0D5252, 0x0DAA47, 0x65B53B, 0x056D4F, 0x04AE45, 0x4A4EB9, 0x0A4D4C, 0x0D1541, 0x2D92B5            //2091-2099
	};

	/**
	 * 將公歷日期轉換為農歷日期，且標識是否是閏月
	 *
	 * @param year     要計算的年份
	 * @param month    要計算的月份
	 * @param monthDay 要計算的日期
	 * @return 公歷日期對應的農歷日期，year0，month1，day2，leap3 (0為非閏月，1為閏月)
	 */
	public static int[] gregorianToLunar(int year, int month, int monthDay) {
		int[] lunarDate = new int[4];
		// baseYear 為上一個公歷年份
		int baseYear = year - 1;
		// baseDate 為 baseYear 的春節的公歷日期
		int[] lnyDate = getLunarNewYearDate(baseYear);
		Calendar baseDate = Calendar.getInstance();
		baseDate.set(Calendar.YEAR, lnyDate[0]);
		baseDate.set(Calendar.MONTH, lnyDate[1] - 1);
		baseDate.set(Calendar.DAY_OF_MONTH, lnyDate[2]);
		// objDate 為要計算的公歷日期
		Calendar objDate = Calendar.getInstance();
		objDate.set(Calendar.YEAR, year);
		objDate.set(Calendar.MONTH, month - 1);
		objDate.set(Calendar.DAY_OF_MONTH, monthDay);
		// offset = baseDate 與 objDate 之間的天數
		int offset = daysBetween(baseDate, objDate);

		// 用offset減去每農歷年的天數計算當天是農歷第幾天
		// lunarYear最終結果是農歷的年份，offset是當年的第幾天
		int lunarYear, daysInYear = 0;
		for (lunarYear = baseYear; lunarYear <= MAX_YEAR && offset > 0; lunarYear++) {
			daysInYear = daysInLunarYear(lunarYear);
			offset -= daysInYear;
		}
		if (offset < 0) {
			offset += daysInYear;
			lunarYear--;
		}

		// 農歷年份
		lunarDate[0] = lunarYear;

		int leapMonth = leapMonth(lunarYear); // 閏哪個月，1-12
		boolean isLeapMonth = false;
		// 用當年的天數offset，逐個減去每月（農歷）的天數，求出當天是本月的第幾天
		int lunarMonth, daysInMonth = 0;
		for (lunarMonth = 1; lunarMonth <= 13 && offset > 0; lunarMonth++) {
			daysInMonth = daysInLunarMonth(lunarYear, lunarMonth);
			offset -= daysInMonth;
		}
		if (offset < 0) {
			offset += daysInMonth;
			lunarMonth--;
		}
		// 當前月超過閏月，要校正
		if (leapMonth != 0 && lunarMonth > leapMonth) {
			lunarMonth--;
			if (lunarMonth == leapMonth) {
				isLeapMonth = true;
			}
		}

		lunarDate[1] = lunarMonth;
		lunarDate[2] = offset + 1;
		lunarDate[3] = isLeapMonth ? 1 : 0;

		return lunarDate;
	}

	/**
	 * 傳回農歷 year年的總天數
	 *
	 * @param year 要計算的年份
	 * @return 農歷 year年的總天數
	 */
	private static int daysInLunarYear(int year) {
		int sum = 348;
		if (leapMonth(year) != 0) {
			sum = 377;
		}
		int monthInfo = LUNAR_MONTHS_INFO[year - MIN_YEAR] & 0x0FFF80;
		for (int bitMask = 0x80000; bitMask > 0x7; bitMask >>= 1) {
			if ((monthInfo & bitMask) != 0) {
				sum += 1;
			}
		}
		return sum;
	}

	/**
	 * 傳回農歷 year年month月的總天數，總共有13個月包括閏月
	 *
	 * @param year  要計算的年份
	 * @param month 要計算的月份 (1..13)
	 * @return 農歷 year年month月的總天數
	 */
	private static int daysInLunarMonth(int year, int month) {
		if ((LUNAR_MONTHS_INFO[year - MIN_YEAR] & (0x100000 >> month)) == 0) {
			return 29;
		} else {
			return 30;
		}
	}

	/**
	 * 傳回農歷 year年month月的總天數
	 *
	 * @param year  要計算的年份
	 * @param month 要計算的月份 (1..12)
	 * @param leap  當月是否是閏月
	 * @return 天數，如果閏月是錯誤的，傳回 0
	 */
	public static int daysInLunarMonth(int year, int month, boolean leap) {
		int leapMonth = leapMonth(year);
		int offset = 0;

		// 如果本年有閏月且month大於閏月時，需要校正
		if (leapMonth != 0 && month > leapMonth) {
			offset = 1;
		}

		// 不考慮閏月
		if (!leap) {
			return daysInLunarMonth(year, month + offset);
		} else {
			// 傳入的閏月是正確的月份
			if (leapMonth != 0 && leapMonth == month) {
				return daysInLunarMonth(year, month + 1);
			}
		}

		return 0;
	}

	/**
	 * 傳回農歷 year年閏哪個月 1..12，沒閏傳回 0
	 *
	 * @param year 要計算的年份
	 * @return 農歷 year年閏哪個月 1..12，沒閏傳回 0
	 */
	public static int leapMonth(int year) {
		return ((LUNAR_MONTHS_INFO[year - MIN_YEAR] & 0xF00000)) >> 20;
	}

	/**
	 * 傳回農歷 year年春節的公歷日期
	 *
	 * @param year 要計算的年份
	 * @return 農歷 year年春節的公歷日期，year0，month1，day2
	 */
	private static int[] getLunarNewYearDate(int year) {
		int[] newYearDate = new int[3];
		newYearDate[0] = year;
		newYearDate[1] = ((LUNAR_MONTHS_INFO[year - MIN_YEAR] & 0x000060)) >> 5;
		newYearDate[2] = (LUNAR_MONTHS_INFO[year - MIN_YEAR] & 0x00001F);
		return newYearDate;
	}

	private static int daysBetween(Calendar day1, Calendar day2) {
		Calendar dayOne = (Calendar) day1.clone();
		Calendar dayTwo = (Calendar) day2.clone();

		if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
			return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
		} else {
			if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
				// swap them
				Calendar temp = dayOne;
				dayOne = dayTwo;
				dayTwo = temp;
			}
			int extraDays = 0;

			int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

			while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
				dayOne.add(Calendar.YEAR, -1);
				// getActualMaximum() important for leap years
				extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
			}

			return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
		}
	}

	public static String toString(int year, int month, int monthDay) {
		int[] lunarDate = gregorianToLunar(year, month, monthDay);
		StringBuilder buf = new StringBuilder();
		// 1901-02-19:辛丑年(肖牛)正月小初一
		buf.append(String.format(Locale.ENGLISH, "%04d-%02d-%02d:", year, month, monthDay));
		buf.append(numToChineseYear(lunarDate[0]));
		buf.append("(")
				.append(getMiscName(0)) // 肖
				.append(getZodiacName(getZodiac(lunarDate[0])))
				.append(")");
		if (lunarDate[3] == 1) {
			buf.append(getMiscName(1)); // 閏
		}
		buf.append(numToChineseMonth(lunarDate[1]));
		if (daysInLunarMonth(lunarDate[0], lunarDate[1], (lunarDate[3] == 1)) == 30) {
			buf.append(getMiscName(2)); // 大
		} else {
			buf.append(getMiscName(3)); // 小
		}
		buf.append(numToChineseDay(lunarDate[2]));
		return buf.toString();
	}
}
