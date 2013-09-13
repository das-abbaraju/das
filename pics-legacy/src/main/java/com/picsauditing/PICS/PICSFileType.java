package com.picsauditing.PICS;

public enum PICSFileType {
	osha, data, brochure, logos, certs, note_attachment, audit, emp;
	
	public String filename(int id) { return this + "_" + id; }
}
