package com.picsauditing.util.log;

public class LoggingRule implements Comparable<LoggingRule> {
	protected String name = null;
	protected boolean logged = false;

	public LoggingRule(String name, boolean logged) {
		super();
		this.name = name;
		this.logged = logged;
	}
	public String getName() {
		return name;
	}
	public boolean isLogged() {
		return logged;
	}
	
	@Override
	public int compareTo(LoggingRule o) {
		if( o == null || o.getName() == null ) return 1;
		
		int cmp = new Integer(this.getName().length()).compareTo(o.getName().length());
		if( cmp == 0 ) {
			return this.getName().compareTo( o.getName() );
		}
		
		return -1 * cmp;
	}
}
