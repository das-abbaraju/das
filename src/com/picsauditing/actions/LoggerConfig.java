package com.picsauditing.actions;

import java.util.SortedSet;

import com.picsauditing.util.log.LoggingRule;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class LoggerConfig extends PicsActionSupport {

	protected LoggingRule rule = null;
	protected String watchName = null;

	@Override
	public String execute() throws Exception {

		PicsLogger.start("LoggerConfig.execute");
		if ("add".equals(button)) {
			if (rule != null && rule.getName() != null) {
				PicsLogger.getRules().add(rule);
				addActionMessage("Added rule:" + rule.getName());
			}
		} else if ("set".equals(button)) {
			if (rule != null && rule.getName() != null) {
				PicsLogger.getRules().remove(rule);
				PicsLogger.getRules().add(rule);
			}
		} else if ("delete".equals(button)) {
			if (rule != null && rule.getName() != null) {
				PicsLogger.getRules().remove(rule);
				addActionMessage("Removed rule:" + rule.getName());
			}
		} else if ("clear".equals(button)) {
			PicsLogger.getRules().clear();
			addActionMessage("Removed all rules");
		} else if ("test".equals(button)) {
			if (watchName != null) {
				PicsLogger.start(watchName);
				PicsLogger.log("this is a test message");
				PicsLogger.stop();
				addActionMessage("Tested " + watchName);
			}
		}

		PicsLogger.stop();

		return SUCCESS;
	}

	public LoggingRule getRule() {
		return rule;
	}

	public void setRule(LoggingRule rule) {
		this.rule = rule;
	}

	public String getWatchName() {
		return watchName;
	}

	public void setWatchName(String watchName) {
		this.watchName = watchName;
	}

	public SortedSet<LoggingRule> getRules() {
		return PicsLogger.getRules();
	}
}
