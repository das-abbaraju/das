package com.picsauditing.actions;

import com.picsauditing.util.log.LoggingRule;
import com.picsauditing.util.log.PicsLogger;

public class LoggerConfig extends PicsActionSupport {

	protected LoggingRule rule = null;
	protected String watchName = null;
	
	@Override
	public String execute() throws Exception {
		
		PicsLogger.start("loggerConfig.execute");
		output = "";
		if( button == null || "list".equalsIgnoreCase(button) ) {
			for( LoggingRule rule : PicsLogger.getRules() ) {
				this.output += rule.toString() + "<br/>";
			}
		}
		else if( "add".equals(button)) {
			if( rule != null && rule.getName() != null) {
				PicsLogger.getRules().add(rule);
			}
		}
		else if( "set".equals(button)) {
			if( rule != null && rule.getName() != null) {
				PicsLogger.getRules().remove(rule);
				PicsLogger.getRules().add(rule);
			}
		}
		else if( "delete".equals(button)) {
			if( rule != null && rule.getName() != null) {
				PicsLogger.getRules().remove(rule);
			}
		}
		else if( "clear".equals(button)) {
			PicsLogger.getRules().clear();			
		}
		else if( "test".equals(button)) {
			if( watchName != null ) {
				PicsLogger.start(watchName);
				PicsLogger.log("this is a test message");
				PicsLogger.stop();
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
}
