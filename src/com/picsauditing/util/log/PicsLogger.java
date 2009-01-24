package com.picsauditing.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

public class PicsLogger {

	protected static ThreadLocal<MyLogger> myLogger = new ThreadLocal<MyLogger>();
	protected static SortedSet<LoggingRule> rules = new TreeSet<LoggingRule>();
	protected static String dateFormat = "MMMdd HH:mm:ss.S";

	static public void log(String message) {

		MyLogger logger = getLogger();

		StopWatch watch = logger.top();

		String fqn = "|";

		if (watch != null) {
			fqn = watch.getFqn();
		}

		boolean shouldLog = true;

		for (LoggingRule rule : rules) {

			String ruleName = rule.getName();

			if (!ruleName.startsWith("|"))
				ruleName = "|" + ruleName;

			if (!ruleName.endsWith("|"))
				ruleName = ruleName + "|";

			if (fqn.contains(ruleName)) {
				shouldLog = rule.isLogged();
				break;
			}
		}
		if (shouldLog) {
			System.out.println(formatDate(new Date()) + ": " + message);
		}
	}

	static public void start(String stopWatchName) {
		start(stopWatchName, "");
	}

	static public void start(String stopWatchName, String message) {
		MyLogger logger = getLogger();

		StopWatch watch = null;

		if (logger.top() != null) {
			StopWatch parent = logger.top();
			watch = new StopWatch(parent, stopWatchName);
		} else {
			watch = new StopWatch(stopWatchName);
		}
		logger.push(watch);

		log("Starting: " + watch.getName() + " " + message);
	}

	static public void stop(String message) {

		MyLogger logger = getLogger();

		StopWatch watch = null;

		watch = logger.pop();

		if (watch != null) {
			Date now = new Date();
			log("Completed: " + watch.getName() + " (" + (now.getTime() - watch.getDate().getTime()) + "ms)");
			return;
		}
		throw new RuntimeException("pop called with nothing to pop");
	}

	static public void stop() {
		stop("");
	}

	static private MyLogger getLogger() {
		MyLogger logger = myLogger.get();

		if (logger == null) {
			logger = new MyLogger();
			myLogger.set(logger);
		}
		return logger;
	}

	static protected String formatDate(Date date) {
		return new SimpleDateFormat(dateFormat).format(date);
	}

}
