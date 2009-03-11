/**
 * QBWebConnectorSvcSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
package com.intuit.developer;

public class QBWebConnectorSvcSkeleton {


	public AuthenticateResponse authenticate(Authenticate authenticate) {

		AuthenticateResponse response = new AuthenticateResponse( );
		response.setAuthenticateResult(new ArrayOfString());

		System.out.println("authenticating: " + authenticate.getStrUserName() + "/" + authenticate.getStrPassword() );
		
		
		response.getAuthenticateResult().addString("thisIsTheSessionId");
		
		if ( false ) { //were the credentials good? 
			//invalid credentials supplied
			response.getAuthenticateResult().addString("nvu");
		}
		else if ( false ) { //is there no work to do?
			//good login, but there is no work to do
			response.getAuthenticateResult().addString("none");
		}
		else if ( false ) { // is there a specific quickbooks company file we want to use? 
			//set the company to use
			response.getAuthenticateResult().addString("theSpecificCompany");
		}
		else if ( true ) { // we want to use the default company
			//set the company to use			
			response.getAuthenticateResult().addString("");
		}
		

		if( false ) { //do we want QBWC to wait before making the calls 
			//we want them to wait 5 seconds before making the next call
			response.getAuthenticateResult().addString("5");
		}
		
		
		if( false ) { //do we want to limit, on the server-side, the frequency that the feed runs 
			//makes the feed only happen every hour
			response.getAuthenticateResult().addString("3600");
		}
		
		return response;
	}

	public GetLastErrorResponse getLastError(GetLastError getLastError) {
		GetLastErrorResponse response = new GetLastErrorResponse();
		
		if( true ) { //this causes QBWC to stop the current request cycle
			response.setGetLastErrorResult("Noop");
		}
		else if( false ){  //we actually have an error message
			response.setGetLastErrorResult("There was some error");
		}
		else if( false ){  //we want QBWC to enter interactive mode
			response.setGetLastErrorResult("Interactive mode");
		}
		
		return response;
	}

	
	
	public SendRequestXMLResponse sendRequestXML(SendRequestXML sendRequestXML) {
		SendRequestXMLResponse response = new SendRequestXMLResponse();

		//This contains important information about the client's QB installation, and will only be present on the first call
		sendRequestXML.getStrHCPResponse();   
		
		
		if( ! true ) { //validate the session
		
		}
		else {
			if( true ) { //there isn't any work to do
				response.setSendRequestXMLResult("");
			}
			else {  //there is work to do
				response.setSendRequestXMLResult("someQbXmlString");
			}
		}
		
		return response;
	}

	public ConnectionErrorResponse connectionError(
			ConnectionError connectionError) {
		//we probably want to send an email.  this means that QBWC was not able to connect to the quickbooks installation
		ConnectionErrorResponse response = new ConnectionErrorResponse();
		return response;
	}

	public CloseConnectionResponse closeConnection(
			CloseConnection closeConnection) {
		CloseConnectionResponse response = new CloseConnectionResponse();
		return response;
	}


	public ReceiveResponseXMLResponse receiveResponseXML(
			ReceiveResponseXML receiveResponseXML) {

		ReceiveResponseXMLResponse response = new ReceiveResponseXMLResponse();
		return response;
	}
	
}
