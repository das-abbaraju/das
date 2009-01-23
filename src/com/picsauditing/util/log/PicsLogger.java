package com.picsauditing.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PicsLogger {
	static private SimpleDateFormat formatter = new SimpleDateFormat("MMMdd HH:mm:ss.S");
	
	static boolean isLogging() {
		return true;
	}
	
	static public void log(String message) {
		if (!isLogging())
			return;
		out(now() + " " + message);
	}
	
	static public void start(String stopWatchName) {
		start(stopWatchName, "");
	}
	
	static public void start(String stopWatchName, String message) {
		if (!isLogging())
			return;
		out("Starting: " + stopWatchName + now() + " " + message);
	}
	
	static public void stop(String message) {
		log("Completed");
	}

	static public void stop() {
		stop("");
	}
	
	static private String now() {
		return formatter.format(new Date());
	}
	
	/**
	 * Send logging out to sysout, DB, screen, stdOut??
	 * @param message
	 */
	static private void out(String message) {
		System.out.println(message);
	}
}
