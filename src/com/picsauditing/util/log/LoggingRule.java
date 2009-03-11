package com.picsauditing.util.log;

public class LoggingRule implements Comparable<LoggingRule> {
	protected String name = null;
	protected boolean logged = true;

	public LoggingRule() {
		
	}
	
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
	
	public void setName(String name) {
		this.name = name;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
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
	
	@Override
	public boolean equals(Object obj) {
		if( obj == null || ! (obj instanceof LoggingRule) ) {
			return false;
		}
		return this.compareTo((LoggingRule)obj) == 0;
	}
	
	@Override
	public String toString() {
		return "Rule name: " + getName() + " Rule value: " + isLogged();
	}
}
