package com.picsauditing.PICS;

/**
 * Signals that no response has been received from BrainTree
 * after submitting a transaction. Impossible to determine
 * success of transaction through current API without
 * manual verification.
 *
 * @author  Thomas Baker
 * @version 4.3, 05/03/2010
 */

public class NoBrainTreeServiceResponseException extends Exception {
	private static final long serialVersionUID = -4486104374529012978L;

	public NoBrainTreeServiceResponseException() {
		super("No response from BrainTree service recieved. Unable to verify procedure success.");
	}

	public NoBrainTreeServiceResponseException(String msg) {
		super("No response from BrainTree service recieved. Unable to verify procedure success." + msg);
	}
}
