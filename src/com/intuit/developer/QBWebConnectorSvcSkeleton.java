/**
 * QBWebConnectorSvcSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
package com.intuit.developer;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.util.id.GUID;

import com.intuit.developer.adaptors.QBXmlAdaptor;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.log.PicsLogger;

public class QBWebConnectorSvcSkeleton {

	protected QBSession currentSession = null;

	private static Map<String, QBSession> sessions = new HashMap<String, QBSession>();

	public AuthenticateResponse authenticate(Authenticate authenticate) {

		AuthenticateResponse response = new AuthenticateResponse();
		response.setAuthenticateResult(new ArrayOfString());

		PicsLogger.start("QBWebConnector");

		PicsLogger.log("Authenticating user: " + authenticate.getStrUserName());

		String sessionId = null;

		boolean finished = false;
		try {

			if (authenticate.getStrUserName() == null || authenticate.getStrUserName() == null) {
				return null;
			}

			AppPropertyDAO appPropsDao = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");

			String maxSessionsString = appPropsDao.find("PICSQBLOADER.maxSessions").getValue();
			String sessionTimeoutString = appPropsDao.find("PICSQBLOADER.sessionTimeout").getValue();
			String qbPassword = appPropsDao.find("PICSQBLOADER.password").getValue();

			int maxSessions = Integer.parseInt(maxSessionsString);
			long sessionTimeout = Long.parseLong(sessionTimeoutString);

			if (maxSessions != -1) {
				if (sessions.size() >= maxSessions) {
					for (Iterator<String> iterator = sessions.keySet().iterator(); iterator.hasNext();) {
						String key = iterator.next();
						QBSession session = sessions.get(key);

						if (sessionTimeout != -1 && !session.isProcessingSomething()
								&& System.currentTimeMillis() - session.getLastRequest().getTime() > sessionTimeout) {
							iterator.remove();
						}
					}
					if (sessions.size() >= maxSessions) {
						throw new Exception("too many sessions");
					}
				}
			}

			if (("PICSQBLOADER".equals(authenticate.getStrUserName()) || "PICSQBLOADERCAN".equals(authenticate.getStrUserName()))
					&& authenticate.getStrPassword().equals(qbPassword)) {

				QBSession session = new QBSession();
				session.setSessionId(GUID.asString());
				session.setLastRequest(new Date());
				// set country specific fields
				if("PICSQBLOADER".equals(authenticate.getStrUserName())){
					session.setCurrencyCode("USD");
					session.setQbID("qbListID");
				} else if("PICSQBLOADERCAN".equals(authenticate.getStrUserName())){
					session.setCurrencyCode("CAD");
					session.setQbID("qbListCAID");
				}
				sessions.put(session.getSessionId(), session);
				sessionId = session.getSessionId();
				PicsLogger.log("login valid for user: " + authenticate.getStrUserName() + ", sessionId: " + sessionId);

				session.setCurrentStep(QBIntegrationWorkFlow.values()[0]);
				currentSession = session;

				if (!shouldWeRunThisStep()) {
					moveToNextStep();
				}

				if (session.getCurrentStep().equals(QBIntegrationWorkFlow.Finished)) {
					finished = true;
				}
			}

			if (sessionId == null) {
				throw new Exception("unable to login");
			}

		} catch (Exception e) {
			response.getAuthenticateResult().addString("NOSESSION");
			PicsLogger.log("invalid credentials supplied for " + authenticate.getStrUserName());
			response.getAuthenticateResult().addString("nvu");
		}

		response.getAuthenticateResult().addString(sessionId);

		if (finished) {
			PicsLogger.log("login valid, but there is no work to do for " + authenticate.getStrUserName());
			response.getAuthenticateResult().addString("none");
		} else if (false) {
			String companyName = "theSpecificCompany";
			PicsLogger.log("using company: " + companyName);
			response.getAuthenticateResult().addString(companyName);
		} else if (true) {
			PicsLogger.log("using default company");
			response.getAuthenticateResult().addString("");
		}

		if (false) {
			String secondsToWaitForNextCall = "5";
			PicsLogger.log("due to high load, setting seconds to wait for next call to " + secondsToWaitForNextCall);
			response.getAuthenticateResult().addString(secondsToWaitForNextCall);
		}

		if (false) {
			String secondsToWaitForNextCall = "3600";
			PicsLogger.log("due to high load, setting seconds to wait for next feed to " + secondsToWaitForNextCall);
			response.getAuthenticateResult().addString(secondsToWaitForNextCall);
		}

		PicsLogger.stop();
		return response;
	}

	public GetLastErrorResponse getLastError(GetLastError getLastError) {
		GetLastErrorResponse response = new GetLastErrorResponse();

		if (start(getLastError.getTicket())) {

			if (currentSession.getLastError() == null) {
				PicsLogger.log(getLastError.getTicket() + ": instructing QBWC to do a noop");
				response.setGetLastErrorResult("Noop");
			} else { // we actually have an error message
				String error = currentSession.getLastError();
				PicsLogger.log(getLastError.getTicket() + ": error: " + error);
				response.setGetLastErrorResult(error);
				currentSession.setLastError(null);
			}
		}

		stop();
		return response;
	}

	public SendRequestXMLResponse sendRequestXML(SendRequestXML sendRequestXML) throws Exception {
		SendRequestXMLResponse response = new SendRequestXMLResponse();

		if (start(sendRequestXML.getTicket())) {

			PicsLogger.log(sendRequestXML.getTicket() + ": performing request side of step: "
					+ currentSession.getCurrentStep().name());

			// This contains important information about the client's QB
			// installation, and will only be present on the first call
			sendRequestXML.getStrHCPResponse();

			if (!true) { // validate the session

			} else {

				String qbXml = null;

				try {
					if (shouldWeRunThisStep()) {

						QBXmlAdaptor currentAdaptor = currentSession.getCurrentStep().getAdaptorInstance();

						qbXml = currentAdaptor.getQbXml(currentSession);

					} else {
						PicsLogger.log("Skipping " + currentSession.getCurrentStep().name());
					}
				} catch (Exception e) {
					PicsLogger.log("Error occurred while doing " + currentSession.getCurrentStep().name());
					PicsLogger.log("shouldWeRunThisStep() = " + shouldWeRunThisStep());
					PicsLogger.log("currentAdaptor = " + qbXml);
					e.printStackTrace();
					throw e;
				}

				PicsLogger.log(sendRequestXML.getTicket() + ": sendRequestXml() returning :\n" + qbXml);
				response.setSendRequestXMLResult(qbXml);
			}
		}

		stop();
		return response;
	}

	public ConnectionErrorResponse connectionError(ConnectionError connectionError) {

		ConnectionErrorResponse response = new ConnectionErrorResponse();

		if (start(connectionError.getTicket())) {

			PicsLogger.log(connectionError.getTicket() + ": QB Connection error: ");
			PicsLogger.log(connectionError.getTicket() + "\tMessage: " + connectionError.getMessage());
			PicsLogger.log(connectionError.getTicket() + "\tHresult: " + connectionError.getHresult());
			PicsLogger.log(connectionError.getTicket() + "\tHresult: " + connectionError.getHresult());

			// we probably want to send an email. this means that QBWC was not
			// able
			// to connect to the quickbooks installation

			if (true) {
				PicsLogger.log(connectionError.getTicket() + ": signalling to end feed");
				response.setConnectionErrorResult("done");
			} else if (false) {
				String companyName = null;
				PicsLogger.log(connectionError.getTicket() + ": signalling to switch companies");
				response.setConnectionErrorResult(companyName);
			}
		}

		stop();
		return response;
	}

	public CloseConnectionResponse closeConnection(CloseConnection closeConnection) {
		CloseConnectionResponse response = new CloseConnectionResponse();

		if (start(closeConnection.getTicket())) {

			if (currentSession.getErrors() != null && currentSession.getErrors().size() > 0) {

				StringBuffer body = new StringBuffer();

				body.append("QBWebConnector Errors:");
				body.append("\n\n");

				for (String error : currentSession.getErrors()) {
					body.append(error);
					body.append("\n");
				}

				try {
					EmailQueue email = new EmailQueue();
					email.setToAddresses("errors@picsauditing.com");
					email.setPriority(40);
					email.setSubject("QBWebConnector Errors");
					email.setBody(body.toString());
					email.setCreationDate(new Date());

					EmailSender.send(email);

				} catch (Exception notMuchWeCanDoButLogIt) {
					System.out.println("**********************************");
					System.out.println("Error Running QBWebConnector AND unable to send email");

					System.out.println(body.toString());

					System.out.println("**********************************");

					System.out.println(notMuchWeCanDoButLogIt);
					notMuchWeCanDoButLogIt.printStackTrace();
				}
			}

			sessions.remove(currentSession.getSessionId());

			String result = "Success";
			response.setCloseConnectionResult(result);
			PicsLogger.log(closeConnection.getTicket() + ": QB Connection closing with response:" + result);
		}

		currentSession = null;
		stop();
		return response;
	}

	public ReceiveResponseXMLResponse receiveResponseXML(ReceiveResponseXML receiveResponseXML) {

		ReceiveResponseXMLResponse response = new ReceiveResponseXMLResponse();

		if (start(receiveResponseXML.getTicket())) {

			PicsLogger.log(receiveResponseXML.getTicket() + ": received responseXML at step: "
					+ currentSession.getCurrentStep().name());
			PicsLogger.log(receiveResponseXML.getTicket() + ":\t response: " + receiveResponseXML.getResponse());
			PicsLogger.log(receiveResponseXML.getTicket() + ":\t message: " + receiveResponseXML.getMessage());
			PicsLogger.log(receiveResponseXML.getTicket() + ":\t hrResult: " + receiveResponseXML.getHresult());

			int percentDone = 0;

			QBXmlAdaptor currentAdaptor = currentSession.getCurrentStep().getAdaptorInstance();

			try {
				currentAdaptor.parseQbXml(currentSession, receiveResponseXML.getResponse());
			} catch (Exception e) {
				e.printStackTrace();
				currentAdaptor.setProceed(false);
			}

			if (!currentAdaptor.isRepeat()) {
				moveToNextStep();
			}

			if (currentAdaptor.isProceed()) {
				percentDone = (int) ((float) (100 * (currentSession.getCurrentStep().ordinal() + 1)) / ((float) (QBIntegrationWorkFlow
						.values().length)));
			} else {
				percentDone = 100;
			}

			response.setReceiveResponseXMLResult(percentDone);

			// PicsLogger.log( receiveResponseXML.getTicket() +
			// ": telling client there was an error" );
			// response.setReceiveResponseXMLResult(-1);
		}
		stop();
		return response;
	}

	private boolean start(String ticketId) {
		PicsLogger.start("QBWebConnector");

		currentSession = sessions.get(ticketId);

		if (currentSession != null) {
			currentSession.setProcessingSomething(true);
		}

		return true;
	}

	private void stop() {

		if (currentSession != null) {
			currentSession.setProcessingSomething(false);
			currentSession.setLastRequest(new Date());
		}

		PicsLogger.stop();
	}

	private boolean shouldWeRunThisStep() {
		AppPropertyDAO appPropsDao = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");

		AppProperty find = appPropsDao.find("PICSQBLOADER.doStep." + currentSession.getCurrentStep());

		if (find == null || find.getValue() == null)
			return false;

		return find.getValue().equals("Y");
	}

	private void moveToNextStep() {

		do {
			currentSession.setCurrentStep(currentSession.getCurrentStep().incrementStep());
		} while (!shouldWeRunThisStep() && currentSession.getCurrentStep() != QBIntegrationWorkFlow.Finished);

	}

}
