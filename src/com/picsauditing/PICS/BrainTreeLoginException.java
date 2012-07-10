package com.picsauditing.PICS;

/**
 * Indicates that the BrainTree username or password has been left out before
 * attempting a BrainTree connection.
 * 
 * @author Thomas Baker
 * @version 4.3, 05/03/2010
 * 
 */

public class BrainTreeLoginException extends Exception {
	private static final long serialVersionUID = -6851686550854821977L;

	public BrainTreeLoginException(String msg) {
		super(msg);
	}
}