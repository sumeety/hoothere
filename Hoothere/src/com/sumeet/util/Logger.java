package com.sumeet.util;

import android.util.Log;

public class Logger {
	
	final static private String Tag = "Hoothere";
	
	static public void writeLog(String msg) {
		Log.w(Tag, msg);
	}
}
