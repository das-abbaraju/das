package com.picsauditing.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

public class PicsLogger {

	protected static ThreadLocal<MyLogger> myLogger = new ThreadLocal<MyLogger>();
	protected static SortedSet<LoggingRule> rules = new TreeSet<LoggingRule>();
	protected static String dateFormat = "MMMdd HH:mm:ss.SSS";
	protected static boolean outputOn = false;
	protected static StringBuilder output = new StringBuilder();

	static public void log(String message) {

		MyLogger logger = getLogger();

		StopWatch watch = logger.top();

		String fqn = "|";

		if (watch != null) {
			fqn = watch.getFqn();
		}

		if (fqn.length() != 1) {
			fqn = fqn + "|";
		}

		boolean shouldLog = false;

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
		if (outputOn)
			output.append(message + "\n");
	}

	static public void start(String stopWatchName) {
		start(stopWatchName, "");
	}

	static public void start(String stopWatchName, boolean autostart) {
		if (autostart)
			addRuntimeRule(stopWatchName);

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

	static long stop(String message) {

		MyLogger logger = getLogger();

		StopWatch watch = null;

		watch = logger.top();

		if (watch != null) {

			Date now = new Date();

			long millis = now.getTime() - watch.getDate().getTime();
			log("Completed: " + watch.getName() + " (" + millis + "ms)");
			logger.pop();
			return millis;
		}
		throw new RuntimeException("pop called with nothing to pop");
	}

	static public long stop() {
		return stop("");
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

	public static SortedSet<LoggingRule> getRules() {
		return rules;
	}

	public static void setRules(SortedSet<LoggingRule> rules) {
		PicsLogger.rules = rules;
	}

	/**
	 * Get the current output (if any) and clear it for the next time
	 * 
	 * @return
	 */
	public static String getOutput() {
		if (!outputOn)
			return "Turn on output first";
		String outputString = output.toString();
		output = new StringBuilder();
		return outputString;
	}

	public static void setOutputOn(boolean outputOn) {
		PicsLogger.outputOn = outputOn;
		getOutput(); // Clear the log
	}

	public static void addRuntimeRule(String rule) {
		LoggingRule ruleObject = new LoggingRule(rule, true);
		PicsLogger.getRules().add(ruleObject);
	}

}
