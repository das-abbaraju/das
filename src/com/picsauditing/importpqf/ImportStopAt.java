package com.picsauditing.importpqf;

public enum ImportStopAt {
	None, Text, SameLine, NextLine;
	
	public boolean isStopAtNone() {
		return this == None;
	}
	
	public boolean isStopAtText() {
		return this == Text;
	}
	
	public boolean isStopAtSameLine() {
		return this == SameLine;
	}
	
	public boolean isStopAtNextLine() {
		return this == NextLine;
	}
}
