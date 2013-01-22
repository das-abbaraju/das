package com.picsauditing.report;

public class RecordNotFoundException extends Exception {
	
	private static final long serialVersionUID = -1779465806139659363L;

	/**
	 * @param text The name of the missing object to be displayed in the error message
	 */
	public RecordNotFoundException(String missingObjectName){
		super(missingObjectName + " not found");
	}

}
