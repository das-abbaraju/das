package com.picsauditing.PICS;

/**
 * General BrainTree error handler. Subclass into specific error cases. See
 * 'Transaction Response Codes' in documentation.
 * 
 * @author Thomas Baker
 * @version 4.3, 05/03/2010
 */

public class BrainTreeServiceErrorResponseException extends Exception {
	private static final long serialVersionUID = 5827152846628113128L;

	public BrainTreeServiceErrorResponseException(String msg) {
		super(msg);
	}
}
