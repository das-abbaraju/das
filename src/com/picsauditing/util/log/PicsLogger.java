package com.picsauditing.util.log;

import java.util.Date;

public class PicsLogger {
	
	static boolean isLogging() {
		return true;
	}
	
	static public void log(String message) {
		if (!isLogging())
			return;
		Date now = new Date();
		System.out.println(now + " " + message);
	}
	
	static public void start(String stopWatchName) {
		start(stopWatchName, "");
	}
	
	static public void start(String stopWatchName, String message) {
		if (!isLogging())
			return;
		Date now = new Date();
		System.out.println("Starting: " + stopWatchName + now + " " + message);
	}
	
	static public void stop(String message) {
		log("Completed");
	}

	static public void stop() {
		stop("");
	}
}
