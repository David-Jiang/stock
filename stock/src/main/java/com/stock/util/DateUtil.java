package com.stock.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;

public class DateUtil {

	public String getWeekDay(final String westDay) {
		String[] weekDays = { "星期日", "星期ㄧ", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar c;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			c = Calendar.getInstance();
			c.setTime(format.parse(westDay));
		} catch (ParseException e) {
			return null;
		}

		int w = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0) {
			w = 0;
		}
		return weekDays[w];

	}

	public static String west2WestDateWithSlash(final String date) {

		if (StringUtils.isBlank(date)) {
			return "";
		}

		final int year = Integer.parseInt(date.substring(0, 4));
		final String month = date.substring(4, 6);
		final String day = date.substring(6);
		return year + "-" + month + "-" + day;
	}

	/**
	 * @param isHHmmDisplay:
	 *            display HH:mm:ss or not
	 * @return yyyMMdd || yyyMMdd HH:mm:ss
	 */
	public static String getTwToday(final boolean isHHmmDisplay) {
		if (isHHmmDisplay) {
			return west2TwTime(getToday(isHHmmDisplay));
		}
		return west2TwDate(getToday(isHHmmDisplay));
	}

	/**
	 * @param isHHmmDisplay:
	 *            display HH:mm:ss or not
	 * @return today's day in western format yyyyMMdd in string format
	 */
	public static String getToday(final boolean isHHmmDisplay) {
		final Calendar cal = Calendar.getInstance();
		final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		if (isHHmmDisplay) {
			return dateTimeFormat.format(cal.getTime());
		}
		return dateTimeFormat.format(cal.getTime()).substring(0, 8);
	}

	/**
	 * 將西元年的日期字串轉換為民國年的日期字串 (含時間) yyyy-MM-dd to yyyMMdd || yyyyMMdd to yyyMMdd
	 */
	public static String west2TwTime(final String date) {

		if (StringUtils.isBlank(date)) {
			return "";
		}

		final String date1 = date.replaceAll("-", "");
		final int year = Integer.parseInt(date1.substring(0, 4)) - 1911;
		final String monthDay = date1.substring(4);
		return String.format("%03d", year) + monthDay;
	}

	/**
	 * 將西元年的日期字串轉換為民國年的日期字串 yyyy-MM-dd to yyyMMdd || yyyyMMdd to yyyMMdd
	 */
	public static String west2TwDate(final String date) {

		if (StringUtils.isBlank(date)) {
			return "";
		}

		final String date1 = date.replaceAll("-", "");
		final int year = Integer.parseInt(date1.substring(0, 4)) - 1911;
		final String monthDay = date1.substring(4, 8);
		return String.format("%03d", year) + monthDay;
	}

	/**
	 * 將台灣的日期數字轉換為日期(時間)敘述: X 年 X 月 X 日( X 時 X 分)
	 * 
	 * @param date
	 *            台灣日期數字
	 * @param isHHmmDisplay
	 *            是否包含 X 時 X 分
	 */
	public static String twDateWithDescription(final String date, final boolean isHHmmDisplay) {

		if (StringUtils.isBlank(date)) {
			return "";
		}

		String twDate = "";

		if (isHHmmDisplay) {
			twDate = date.indexOf(" ") != 7 ? "0" + date : date;
		} else {
			twDate = date.length() != 7 ? "0" + date : date;
		}

		final String year = twDate.substring(0, 3);
		final String month = twDate.substring(3, 5);
		final String day = twDate.substring(5, 7);

		if (isHHmmDisplay) {
			final String hour = twDate.substring(8, 10);
			final String minute = twDate.substring(11, 13);
			return year + "年" + month + "月" + day + "日" + hour + "時" + minute + "分";
		} else {
			return year + "年" + month + "月" + day + "日";
		}
	}

	/**
	 * 將民國年的日期字串轉換為西元年的日期字串 yyyMMdd to yyyy-MM-dd
	 */
	public static String tw2WestDateWithDash(final String date) {

		if (StringUtils.isBlank(date)) {
			return "";
		}

		final int year = Integer.parseInt(date.substring(0, 3)) + 1911;
		final String month = date.substring(3, 5);
		final String day = date.substring(5);
		return year + "-" + month + "-" + day;
	}

	/**
	 * 民國年月日字串轉為Util.Date
	 */
	public static Date tw2UtilDate(final String str) {
		Date date = null;

		try {
			if (StringUtils.isBlank(str)) {
				return date;
			}
			final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			final String westDate = tw2WestDate(str);
			date = new Date(dateFormat.parse(westDate).getTime());
		} catch (final Exception e) {
			return date;
		}
		return date;
	}

	/**
	 * Converting taiwan date format yyymmdd into western date format
	 */
	public static String tw2WestDate(final String date) {
		if (StringUtils.isBlank(date) || date.length() != 7) {
			return null;
		}

		try {
			final int year = Integer.parseInt(date.substring(0, 3)) + 1911;
			final String monthDay = date.substring(3);
			return year + monthDay;
		} catch (final Exception ex) {
			Log.error(ex.getMessage(), ex);
			return null;
		}
	}

	public static Date getNow() {
		return GregorianCalendar.getInstance().getTime();
	}

	/**
	 * Converting taiwan slash date format yyy/mm/dd into util date
	 */
	public static Date twslash2UtilDate(String date) {
		if (StringUtils.isBlank(date)) {
			return null;
		}

		try {
			return tw2UtilDate(date.replaceAll("/", ""));
		} catch (final Exception ex) {
			Log.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * @return today's day in western format yyyy/MM/dd in string format
	 */
	public static String getTodayWithSlash() {
		final Calendar cal = Calendar.getInstance();
		final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		return dateTimeFormat.format(cal.getTime());
	}

	/**
	 * 計算特定月有幾天
	 * 
	 * @param month
	 *            特定月
	 */
	public static String calEndDayOfMonth(String month) {
		if (StringUtils.isBlank(month)) {
			return "";
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		return String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	}
	
	public static Timestamp tw2Timestamp(String twDate) {
		if (StringUtils.isBlank(twDate)) {
			return null;
		}
		Date date = tw2UtilDate(twDate);
		if (date == null) {
			return null;
		}
		return new Timestamp(date.getTime());
	}
}
