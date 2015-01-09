package com.srcoop.android.activity.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class DateUtil {

	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static String getDateFormarted() {
		return sdf.format(new Date(System.currentTimeMillis()));
	}

	public static long getDate(String dateString) throws ParseException {
		return sdf.parse(dateString).getTime();
	}
}
