package com.picsauditing.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use sfl4j and perf4j directly
 */
@Deprecated
public class PicsLogger {

	protected static SortedSet<LoggingRule> rules = new TreeSet<LoggingRule>();
	protected static String dateFormat = "MMMdd HH:mm:ss.SSS";
	protected static boolean outputOn = false;
	protected static StringBuilder output = new StringBuilder();
	
	private final static Logger LOG = LoggerFactory.getLogger(PicsLogger.class);
	private final static org.perf4j.StopWatch STOPWATCH = new Slf4JStopWatch(LoggerFactory.getLogger("org.perf4j.TimingLogger"));

	/**
	 * Need to use Logger directly
	 */
	static public void log(String message) {
    	LOG.info(message);
		if (outputOn)
			output.append(message + "\n");
	}

	static public void start(String stopWatchName) {
		start(stopWatchName, "");
	}

	static public void start(String stopWatchName, boolean autostart) {
		start(stopWatchName, "");
	}

	static public void start(String stopWatchName, String message) {
		STOPWATCH.start(stopWatchName, message); 
	}

	static long stop(String message) {
		STOPWATCH.stop("PicsLogger", message);
		return STOPWATCH.getElapsedTime();
	}

	static public long stop() {
		return stop("PicsLogger");
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
