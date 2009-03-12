/**
 * QBWebConnectorSvcSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
package com.intuit.developer;

import org.jboss.util.id.GUID;

import com.picsauditing.util.log.PicsLogger;

public class QBWebConnectorSvcSkeleton {

	public AuthenticateResponse authenticate(Authenticate authenticate) {

		AuthenticateResponse response = new AuthenticateResponse();
		response.setAuthenticateResult(new ArrayOfString());

		PicsLogger.start("QBWebConnector" );		
		
		PicsLogger.log( "Authenticating user: " + authenticate.getStrUserName() );
		
		String sessionId = null;
		
		try {
			sessionId = QBWebConnectorWorker.authenticate(authenticate.getStrUserName(), authenticate.getStrPassword());
			response.getAuthenticateResult().addString(sessionId);
		}
		catch( Exception e ) {
			response.getAuthenticateResult().addString("NOSESSION");
		}
		
		

		if (false) { 
			PicsLogger.log( "invalid credentials supplied for " + authenticate.getStrUserName() );
			response.getAuthenticateResult().addString("nvu");
		} else if (false) { 
			PicsLogger.log( "login valid, but there is no work to do for " + authenticate.getStrUserName() );
			response.getAuthenticateResult().addString("none");
		} else if (false) { 
			String companyName = "theSpecificCompany";
			PicsLogger.log( "login valid for user: " + authenticate.getStrUserName() + ", sessionId: " + sessionId + ", using company: " + companyName );
			response.getAuthenticateResult().addString(companyName);
		} else if (true) { 
			PicsLogger.log( "login valid for user:"  + authenticate.getStrUserName() + ", sessionId: " + sessionId + ", using default company" );
			response.getAuthenticateResult().addString("");
		}

		if (false) { 
			String secondsToWaitForNextCall = "5";
			PicsLogger.log( "due to high load, setting seconds to wait for next call to " + secondsToWaitForNextCall );
			response.getAuthenticateResult().addString(secondsToWaitForNextCall);
		}

		if (false) { 
			String secondsToWaitForNextCall = "3600";
			PicsLogger.log( "due to high load, setting seconds to wait for next feed to " + secondsToWaitForNextCall );
			response.getAuthenticateResult().addString(secondsToWaitForNextCall);
		}

		
		PicsLogger.stop();
		return response;
	}

	public GetLastErrorResponse getLastError(GetLastError getLastError) {
		GetLastErrorResponse response = new GetLastErrorResponse();

		if( start( getLastError.getTicket() )) {

			if (true) { 
				PicsLogger.log( getLastError.getTicket() + ": instructing QBWC to do a noop");
				response.setGetLastErrorResult("Noop");
			} else if (false) { // we actually have an error message
				String error = "There was some error";
				PicsLogger.log( getLastError.getTicket() + ": error: " + error);
				response.setGetLastErrorResult(error);
			} else if (false) { // we want QBWC to enter interactive mode
				PicsLogger.log( getLastError.getTicket() + ": trying to enter interactive mode" );
				response.setGetLastErrorResult("Interactive mode");
			}
		}
		
		stop();
		return response;
	}

	public SendRequestXMLResponse sendRequestXML(SendRequestXML sendRequestXML) {
		SendRequestXMLResponse response = new SendRequestXMLResponse();

		if( start( sendRequestXML.getTicket() )) {
			
			// This contains important information about the client's QB
			// installation, and will only be present on the first call
			sendRequestXML.getStrHCPResponse();
	
			if (!true) { // validate the session
	
			} else {
				if (true) { // there isn't any work to do
					PicsLogger.log( sendRequestXML.getTicket() + ": sendRequestXml() has no more work to do");
					response.setSendRequestXMLResult("");
				} else { // there is work to do
					
					String qbXml = "someQbXmlString";
					PicsLogger.log( sendRequestXML.getTicket() + ": sendRequestXml() returning :\n" + qbXml);
					response.setSendRequestXMLResult(qbXml);
				}
			}
		}
		
		stop();
		return response;
	}

	public ConnectionErrorResponse connectionError(
			ConnectionError connectionError) {

		ConnectionErrorResponse response = new ConnectionErrorResponse();

		if( start( connectionError.getTicket() )) {
			
			PicsLogger.log( connectionError.getTicket() + ": QB Connection error: ");
			PicsLogger.log( connectionError.getTicket() + "\tMessage: " + connectionError.getMessage() );
			PicsLogger.log( connectionError.getTicket() + "\tHresult: " + connectionError.getHresult() );
			PicsLogger.log( connectionError.getTicket() + "\tHresult: " + connectionError.getHresult() );
	
			// we probably want to send an email. this means that QBWC was not able
			// to connect to the quickbooks installation

			
			if( true ) {
				PicsLogger.log( connectionError.getTicket() + ": signalling to end feed" );
				response.setConnectionErrorResult("done");
			}
			else if ( false ) {
				String companyName = null;
				PicsLogger.log( connectionError.getTicket() + ": signalling to switch companies" );
				response.setConnectionErrorResult(companyName);
			}
		}		
		
		stop();
		return response;
	}

	public CloseConnectionResponse closeConnection(
			CloseConnection closeConnection) {
		CloseConnectionResponse response = new CloseConnectionResponse();

		if( start( closeConnection.getTicket() )) {
		
			String result = "Success";
			
			PicsLogger.log( closeConnection.getTicket() + ": QB Connection closing with response:" + result);
			
			response.setCloseConnectionResult(result);
			
		}

		stop();
		return response;
	}

	public ReceiveResponseXMLResponse receiveResponseXML(
			ReceiveResponseXML receiveResponseXML) {

		ReceiveResponseXMLResponse response = new ReceiveResponseXMLResponse();

		if( start( receiveResponseXML.getTicket() )) {
		
			PicsLogger.log( receiveResponseXML.getTicket() + ": received responseXML" );
			PicsLogger.log( receiveResponseXML.getTicket() + ":\t response: " + receiveResponseXML.getResponse() );
			PicsLogger.log( receiveResponseXML.getTicket() + ":\t message: " + receiveResponseXML.getMessage() );
			PicsLogger.log( receiveResponseXML.getTicket() + ":\t hrResult: " + receiveResponseXML.getHresult() );
			
			
			if (true) {
				PicsLogger.log( receiveResponseXML.getTicket() + ": telling client that we're done." );
				response.setReceiveResponseXMLResult(100);
			} else if (false) {
				int percent = 10;
				PicsLogger.log( receiveResponseXML.getTicket() + ": telling client that we're " + percent + "% done." );
				response.setReceiveResponseXMLResult(percent);
			} else if (false) { // we have some error
				// set last error
				// setLastError()
	
				PicsLogger.log( receiveResponseXML.getTicket() + ": telling client there was an error" );
				response.setReceiveResponseXMLResult(-1);
			}
		}
		stop();
		return response;
	}

	
	private boolean start( String ticketId ) {
		PicsLogger.start("QBWebConnector" );
		return true;
	}
	
	private void stop() {
		PicsLogger.stop();
	}
	
}
