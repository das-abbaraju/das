package com.picsauditing.access;

/**
 * See also Anonymous and com.picsauditing.interceptors.SecurityInterceptor
 */
public interface SecurityAware {
	/**
	 * 
	 * @param requiresLogin -- e.g. methods that are annotated as Anonymous do not require login 
	 * 
	 */
	boolean isLoggedIn(boolean requiresLogin);

	void tryPermissions(OpPerms opPerm, OpType opType) throws NoRightsException;
	
	boolean isApiUser();

	boolean sessionCookieIsValidAndNotExpired();

	void updateClientSessionCookieExpiresTime();

	String logoutAndRedirectToLogin() throws Exception;
}
