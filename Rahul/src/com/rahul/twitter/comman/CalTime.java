package com.rahul.twitter.comman;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Determines the timeline form given published date
 */
public class CalTime {

	@SuppressLint("SimpleDateFormat")
	public String getTime(String pubDate) throws ParseException {
		Date newsDate = new SimpleDateFormat("EE MMM dd kk:mm:ss zzzz yyyy")
				.parse(pubDate);
		Date systemDate = new Date();
		long pubTime = systemDate.getTime() - newsDate.getTime();
		long seconds = pubTime / 1000;
		long minute = seconds / 60;
		long hour = minute / 60;
		long day = hour / 24;
		String diffInTime;
		if (day > 0) {
			if (day == 1) {
				diffInTime = "Yesterday";
			} else {
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"E dd MMM yyyy");
				diffInTime = dateFormat.format(newsDate);
			}
		} else if (hour > 0) {
			if (hour == 1) {
				diffInTime = hour + " hour ago";
			} else {

				diffInTime = hour + " hours ago";
			}
		} else if (minute > 0) {
			if (minute == 1) {
				diffInTime = minute + " minute ago";
			} else {
				diffInTime = minute + " minutes ago";
			}
		} else {
			diffInTime = seconds + " seconds ago";
		}
		Log.d("Cal time", "time diff" + diffInTime);
		return diffInTime;
	}
}
