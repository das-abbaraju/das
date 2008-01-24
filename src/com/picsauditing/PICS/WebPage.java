package com.picsauditing.PICS;

public class WebPage {
	private String title="";
	private boolean jsScriptaculous;
	private boolean jsPrototype;
	private boolean cached = true;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		if (title.length() == 0) this.title = "PICS";
		this.title = "PICS - "+title;
	}
	public boolean includeScriptaculous() {
		return jsScriptaculous;
	}
	public void includeScriptaculous(boolean value) {
		this.jsScriptaculous = value;
		if (value) this.jsPrototype = value;
	}
	public boolean includePrototype() {
		return jsPrototype;
	}
	public void includePrototype(boolean value) {
		this.jsPrototype = value;
	}
	public boolean isCached() {
		return cached;
	}
	public void setCached(boolean cached) {
		this.cached = cached;
	}
	
}
