/**
 * QBWebConnectorSvcSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
package com.intuit.developer;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.intuit.developer.adaptors.QBXmlAdaptor;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;
import com.picsauditing.jpa.entities.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.*;

public class QBWebConnectorSvcSkeleton {

    public static final String PICSQBLOADER = "PICSQBLOADER";
    public static final String PICSQBLOADERCAN = "PICSQBLOADERCAN";
    public static final String PICSQBLOADERUK = "PICSQBLOADERUK";
    public static final String PICSQBLOADEREU = "PICSQBLOADEREU";
    public static final String PICSQBLOADERDKK = "PICSQBLOADERDKK";
    public static final String PICSQBLOADERSEK = "PICSQBLOADERSEK";
    public static final String PICSQBLOADERZAR = "PICSQBLOADERZAR";
    public static final String PICSQBLOADERNOK = "PICSQBLOADERNOK";
    public static final String PICSQBLOADERCHF = "PICSQBLOADERCHF";
    public static final String QB_LIST_ID = "qbListID";
    public static final String QB_LIST_CAID = "qbListCAID";
    public static final String QB_LIST_UKID = "qbListUKID";
    public static final String QB_LIST_EUID = "qbListEUID";
    public static final String QB_LIST_CHID = "qbListCHID";

    protected QBSession currentSession = null;

    private static Map<String, QBSession> sessions = new HashMap<String, QBSession>();

    private final Logger LOG = LoggerFactory.getLogger(QBWebConnectorSvcSkeleton.class);

    @SuppressWarnings({"unused", "deprecation"})
    public AuthenticateResponse authenticate(Authenticate authenticate) {

        AuthenticateResponse response = new AuthenticateResponse();
        response.setAuthenticateResult(new ArrayOfString());

        PicsLogger.start("QBWebConnector");

        PicsLogger.log("Authenticating user: " + authenticate.getStrUserName());

        String sessionId = null;

        boolean finished = false;
        try {
            if (authenticate.getStrUserName() == null) {
                return null;
            }

            AppPropertyDAO appPropsDao = SpringUtils.getBean("AppPropertyDAO");

            String maxSessionsString = appPropsDao.find("PICSQBLOADER.maxSessions").getValue();
            String sessionTimeoutString = appPropsDao.find("PICSQBLOADER.sessionTimeout")
                    .getValue();
            String qbPassword = appPropsDao.find("PICSQBLOADER.password").getValue();

            int maxSessions = Integer.parseInt(maxSessionsString);
            long sessionTimeout = Long.parseLong(sessionTimeoutString);

            if (maxSessions != -1) {
                if (sessions.size() >= maxSessions) {
                    for (Iterator<String> iterator = sessions.keySet()
                            .iterator(); iterator.hasNext(); ) {
                        String key = iterator.next();
                        QBSession session = sessions.get(key);

                        if (sessionTimeout != -1 && !session.isProcessingSomething()
                                && System.currentTimeMillis() - session.getLastRequest().getTime()
                                > sessionTimeout) {
                            iterator.remove();
                        }
                    }
                    if (sessions.size() >= maxSessions) {
                        throw new Exception("too many sessions");
                    }
                }
            }

            if (isKnownQBWCUsername(authenticate)
                    && authenticate.getStrPassword().equals(qbPassword)) {

                QBSession session = setUpSession(authenticate);

                sessions.put(session.getSessionId(), session);
                sessionId = session.getSessionId();
                PicsLogger.log(
                        "login valid for user: " + authenticate.getStrUserName() + ", sessionId: "
                                + sessionId);

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
            PicsLogger.log(
                    "login valid, but there is no work to do for " + authenticate.getStrUserName());
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
            PicsLogger.log("due to high load, setting seconds to wait for next call to "
                    + secondsToWaitForNextCall);
            response.getAuthenticateResult().addString(secondsToWaitForNextCall);
        }

        if (false) {
            String secondsToWaitForNextCall = "3600";
            PicsLogger.log("due to high load, setting seconds to wait for next feed to "
                    + secondsToWaitForNextCall);
            response.getAuthenticateResult().addString(secondsToWaitForNextCall);
        }

        PicsLogger.stop();
        return response;
    }

    private boolean isKnownQBWCUsername(Authenticate authenticate) {
        switch (authenticate.getStrUserName()) {
            case PICSQBLOADER:
            case PICSQBLOADERCAN:
            case PICSQBLOADERUK:
            case PICSQBLOADEREU:
            case PICSQBLOADERDKK:
            case PICSQBLOADERSEK:
            case PICSQBLOADERZAR:
            case PICSQBLOADERNOK:
            case PICSQBLOADERCHF:
                return true;

            default:
                return false;
        }
    }

    private QBSession setUpSession(Authenticate authenticate) {
        QBSession session = new QBSession();
        session.setSessionId(guid());
        session.setLastRequest(new Date());
        // set country specific fields

        switch (authenticate.getStrUserName()) {
            case PICSQBLOADER:
                session.setCurrencyCode(Currency.USD.name());
                session.setQbID(QB_LIST_ID);
                break;
            case PICSQBLOADERCAN:
                session.setCurrencyCode(Currency.CAD.name());
                session.setQbID(QB_LIST_CAID);
                break;
            case PICSQBLOADERUK:
                session.setCurrencyCode(Currency.GBP.name());
                session.setQbID(QB_LIST_UKID);
                break;
            case PICSQBLOADERCHF:
                session.setCurrencyCode(Currency.CHF.name());
                session.setQbID(QB_LIST_CHID);
                break;
            case PICSQBLOADEREU:
            case PICSQBLOADERDKK:
            case PICSQBLOADERNOK:
            case PICSQBLOADERZAR:
            case PICSQBLOADERSEK:
                session.setCurrencyCode(Currency.EUR.name());
                session.setQbID(QB_LIST_EUID);
                break;



        }
        session.setCurrentStep(QBIntegrationWorkFlow.values()[0]);
        return session;
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

    @SuppressWarnings("unused")
    public SendRequestXMLResponse sendRequestXML(SendRequestXML sendRequestXML) throws Exception {
        SendRequestXMLResponse response = new SendRequestXMLResponse();

        if ((sendRequestXML != null) && start(sendRequestXML.getTicket())) {

            StringBuilder myLogStatement = new StringBuilder();

            myLogStatement.append(sendRequestXML.getTicket());
            myLogStatement.append(": performing request side of step: ");
            logCurrentSessionStepName(myLogStatement);
            PicsLogger.log(myLogStatement.toString());

            // This contains important information about the client's QB
            // installation, and will only be present on the first call
            sendRequestXML.getStrHCPResponse();

            if (!true) { // validate the session

            } else {

                String qbXml = null;

                try {
                    if (shouldWeRunThisStep()) {
                        QBXmlAdaptor currentAdaptor = null;
                        if (currentSession != null && currentSession.getCurrentStep() != null) {
                            currentAdaptor = currentSession.getCurrentStep().getAdaptorInstance();

                            qbXml = currentAdaptor.getQbXml(currentSession);
                        }

                    } else {
                        if (currentSession != null && currentSession.getCurrentStep() != null) {
                            PicsLogger.log("Skipping " + currentSession.getCurrentStep().name());
                        }
                    }
                } catch (Exception e) {
                    myLogStatement = new StringBuilder();
                    myLogStatement.append("Error occurred while doing ");
                    logCurrentSessionStepName(myLogStatement);
                    myLogStatement.append(Strings.NEW_LINE);
                    myLogStatement.append("shouldWeRunThisStep() = ");
                    myLogStatement.append(shouldWeRunThisStep());
                    myLogStatement.append(Strings.NEW_LINE);
                    myLogStatement.append("currentAdaptor = ");
                    myLogStatement.append(qbXml);
                    PicsLogger.log(myLogStatement.toString());
                    e.printStackTrace();
                    throw e;
                }

                qbXml = fixCharactersForQuickBooksSpecification(qbXml);
                myLogStatement = new StringBuilder();
                myLogStatement.append(sendRequestXML.getTicket());
                myLogStatement.append(": sendRequestXml() returning :" + Strings.NEW_LINE);
                myLogStatement.append(qbXml);
                PicsLogger.log(myLogStatement.toString());
                response.setSendRequestXMLResult(qbXml);
            }
        }

        stop();
        return response;
    }

    private void logCurrentSessionStepName(StringBuilder myLogStatement) {
        if (currentSession != null && currentSession.getCurrentStep() != null) {
            myLogStatement.append(currentSession.getCurrentStep().name());
        }
    }

    private String fixCharactersForQuickBooksSpecification(String qbXml) {
        try {
            qbXml = replaceNonUSLatinCharactersWithUSEquivalents(qbXml);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return qbXml;
    }

    private String replaceNonUSLatinCharactersWithUSEquivalents(String qbXml) {
        return Normalizer.normalize(qbXml, Form.NFKD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @SuppressWarnings({"unused", "deprecation"})
    public ConnectionErrorResponse connectionError(ConnectionError connectionError) {

        ConnectionErrorResponse response = new ConnectionErrorResponse();

        if (start(connectionError.getTicket())) {

            PicsLogger.log(connectionError.getTicket() + ": QB Connection error: ");
            PicsLogger.log(
                    connectionError.getTicket() + "\tMessage: " + connectionError.getMessage());
            PicsLogger.log(
                    connectionError.getTicket() + "\tHresult: " + connectionError.getHresult());
            PicsLogger.log(
                    connectionError.getTicket() + "\tHresult: " + connectionError.getHresult());

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
                    email.setToAddresses(EmailAddressUtils.PICS_ERROR_EMAIL_ADDRESS);
                    email.setMediumPriority();
                    email.setSubject("QBWebConnector Errors");
                    email.setBody(body.toString());
                    email.setCreationDate(new Date());

                    EmailSender emailSender = SpringUtils.getBean(SpringUtils.EMAIL_SENDER);
                    emailSender.send(email);

                } catch (Exception notMuchWeCanDoButLogIt) {
                    LOG.error("**********************************");
                    LOG.error("Error Running QBWebConnector AND unable to send email");

                    LOG.error(body.toString());

                    LOG.error("**********************************");

                    LOG.error(notMuchWeCanDoButLogIt.toString());
                    notMuchWeCanDoButLogIt.printStackTrace();
                }
            }

            sessions.remove(currentSession.getSessionId());

            String result = "Success";
            response.setCloseConnectionResult(result);
            PicsLogger.log(closeConnection.getTicket() + ": QB Connection closing with response:"
                    + result);
        }

        currentSession = null;
        stop();
        return response;
    }

    public ReceiveResponseXMLResponse receiveResponseXML(ReceiveResponseXML receiveResponseXML) {

        ReceiveResponseXMLResponse response = new ReceiveResponseXMLResponse();

        if (receiveResponseXML != null && start(receiveResponseXML.getTicket())) {

            StringBuilder myLogStatement = new StringBuilder();

            myLogStatement.append(receiveResponseXML.getTicket());
            myLogStatement.append(": received responseXML at step: ");
            logCurrentSessionStepName(myLogStatement);
            myLogStatement.append(Strings.NEW_LINE);
            myLogStatement.append(receiveResponseXML.getTicket());
            myLogStatement.append(":\t response: ");
            myLogStatement.append(receiveResponseXML.getResponse());
            myLogStatement.append(Strings.NEW_LINE);
            myLogStatement.append(receiveResponseXML.getTicket());
            myLogStatement.append(":\t message: ");
            myLogStatement.append(receiveResponseXML.getMessage());
            myLogStatement.append(Strings.NEW_LINE);
            myLogStatement.append(receiveResponseXML.getTicket());
            myLogStatement.append(":\t hrResult: ");
            myLogStatement.append(receiveResponseXML.getHresult());
            PicsLogger.log(myLogStatement.toString());

            int percentDone = 0;

            QBXmlAdaptor currentAdaptor = currentSession.getCurrentStep().getAdaptorInstance();

            try {
                currentAdaptor.parseQbXml(currentSession, receiveResponseXML.getResponse());
            } catch (Exception e) {
                LOG.warn("An error occured while parsing QuickBooks XML {} ",
                        Strings.nullToBlank(receiveResponseXML.getResponse()), e);
                currentAdaptor.setProceed(false);
            }

            if (!currentAdaptor.isRepeat()) {
                moveToNextStep();
            }

            if (currentAdaptor.isProceed()) {
                percentDone = (int) ((float) (100 * (currentSession.getCurrentStep().ordinal() + 1))
                        / ((float) (QBIntegrationWorkFlow
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

        LOG.info("Stopped session.");
    }

    private boolean shouldWeRunThisStep() {
        AppPropertyDAO appPropsDao = SpringUtils.getBean(SpringUtils.APP_PROPERTY_DAO);

        QBIntegrationWorkFlow blah = null;
        if (currentSession != null && currentSession.getCurrentStep() != null) {
            blah = currentSession.getCurrentStep();
        }
        AppProperty find = appPropsDao.find("PICSQBLOADER.doStep." + blah);

        if (find == null || find.getValue() == null) {
            return false;
        }

        return find.getValue().equals("Y");
    }

    private void moveToNextStep() {

        do {
            if (currentSession != null
                    && currentSession.getCurrentStep() != null
                    ) {
                currentSession.setCurrentStep(currentSession.getCurrentStep().incrementStep());
            } else {
                return;
            }
        } while (!shouldWeRunThisStep()
                && currentSession.getCurrentStep() != QBIntegrationWorkFlow.Finished);

    }

    private static String guid() {
        EthernetAddress ethernetAddress = EthernetAddress.fromInterface();
        TimeBasedGenerator uuid_gen = Generators.timeBasedGenerator(ethernetAddress);
        UUID uuid = uuid_gen.generate();
        return uuid.toString();
    }

}
