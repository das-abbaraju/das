package com.picsauditing.PICS;

/**
 * Subclassing Card declined BrainTree exceptions.
 * 
 * @author Thomas Baker
 * @version 4.3, 05/03/2010
 */

public class BrainTreeCardDeclinedException extends BrainTreeServiceErrorResponseException {
	private static final long serialVersionUID = 8427120616155034971L;

	public BrainTreeCardDeclinedException(String msg) {
		super(msg);
	}
}
