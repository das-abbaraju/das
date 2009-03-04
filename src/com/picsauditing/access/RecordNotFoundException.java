package com.picsauditing.access;

public class RecordNotFoundException extends Exception {
	
	private static final long serialVersionUID = -1779465806139659363L;

	public RecordNotFoundException(String text){
		super(text + " not found");
	}

}
