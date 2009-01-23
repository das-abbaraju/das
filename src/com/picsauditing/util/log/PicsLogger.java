package com.picsauditing.util.log;

import java.util.Date;

public class PicsLogger {
	
	static public void log(String message) {
		Date now = new Date();
		System.out.println(now + " " + message);
	}
	
	static public void start(String stopWatchName) {
		
	}
	
	static public void stop(String message) {
		
	}

	static public void stop() {
		stop("");
	}
}
