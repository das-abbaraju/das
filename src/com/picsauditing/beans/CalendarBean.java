package com.picsauditing.beans;

import java.util.Date;

public class CalendarBean {
	
	
	private String locale = "en/US";		
	private boolean popup = true;
	private String pattern = "MM/dd/yyyy";
	private boolean showApply = true;
	private Date selectedDate = new Date();
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public boolean isPopup() {
		return popup;
	}
	public void setPopup(boolean popup) {
		this.popup = popup;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public boolean isShowApply() {
		return showApply;
	}
	public void setShowApply(boolean showApply) {
		this.showApply = showApply;
	}
	public Date getSelectedDate() {
		return selectedDate;
	}
	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
	}
	
	
	
	
}
