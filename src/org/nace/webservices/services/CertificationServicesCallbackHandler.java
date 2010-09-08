/**
 * CertificationServicesCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.nace.webservices.services;

/**
 * CertificationServicesCallbackHandler Callback class, Users can extend this
 * class and implement their own receiveResult and receiveError methods.
 */
public abstract class CertificationServicesCallbackHandler {

	protected Object clientData;

	/**
	 * User can pass in any object that needs to be accessed once the
	 * NonBlocking Web service call is finished and appropriate method of this
	 * CallBack is called.
	 * 
	 * @param clientData
	 *            Object mechanism by which the user can pass in user data that
	 *            will be avilable at the time this callback is called.
	 */
	public CertificationServicesCallbackHandler(Object clientData) {
		this.clientData = clientData;
	}

	/**
	 * Please use this constructor if you don't want to set any clientData
	 */
	public CertificationServicesCallbackHandler() {
		this.clientData = null;
	}

	/**
	 * Get the client data
	 */

	public Object getClientData() {
		return clientData;
	}

	/**
	 * auto generated Axis2 call back method for GetCertificationsBatch method
	 * override this method for handling normal response from
	 * GetCertificationsBatch operation
	 */
	public void receiveResultGetCertificationsBatch(
			org.nace.webservices.services.CertificationServicesStub.GetCertificationsBatchResponse result) {
	}

	/**
	 * auto generated Axis2 Error handler override this method for handling
	 * error response from GetCertificationsBatch operation
	 */
	public void receiveErrorGetCertificationsBatch(java.lang.Exception e) {
	}

	/**
	 * auto generated Axis2 call back method for GetCertificationsFullSet method
	 * override this method for handling normal response from
	 * GetCertificationsFullSet operation
	 */
	public void receiveResultGetCertificationsFullSet(
			org.nace.webservices.services.CertificationServicesStub.GetCertificationsFullSetResponse result) {
	}

	/**
	 * auto generated Axis2 Error handler override this method for handling
	 * error response from GetCertificationsFullSet operation
	 */
	public void receiveErrorGetCertificationsFullSet(java.lang.Exception e) {
	}

	/**
	 * auto generated Axis2 call back method for ResetBatch method override this
	 * method for handling normal response from ResetBatch operation
	 */
	public void receiveResultResetBatch(
			org.nace.webservices.services.CertificationServicesStub.ResetBatchResponse result) {
	}

	/**
	 * auto generated Axis2 Error handler override this method for handling
	 * error response from ResetBatch operation
	 */
	public void receiveErrorResetBatch(java.lang.Exception e) {
	}

	/**
	 * auto generated Axis2 call back method for GetCertificationsTestOnly
	 * method override this method for handling normal response from
	 * GetCertificationsTestOnly operation
	 */
	public void receiveResultGetCertificationsTestOnly(
			org.nace.webservices.services.CertificationServicesStub.GetCertificationsTestOnlyResponse result) {
	}

	/**
	 * auto generated Axis2 Error handler override this method for handling
	 * error response from GetCertificationsTestOnly operation
	 */
	public void receiveErrorGetCertificationsTestOnly(java.lang.Exception e) {
	}

}
