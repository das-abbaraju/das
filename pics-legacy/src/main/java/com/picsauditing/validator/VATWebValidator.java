package com.picsauditing.validator;

import com.netflix.hystrix.*;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.*;
import java.util.Map;

/*
    Please see https://github.com/Netflix/Hystrix/wiki
 */
public class VATWebValidator extends HystrixCommand<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(VATWebValidator.class);
    private static final String VALIDATION_URL = "http://isvat.appspot.com";
    private static Client client;
    private static WebResource webResource;
    private static final String HYSTRIX_COMMAND_GROUP = "VATWebValidator";
    private static final String VALIDATION_OK_STRING = "true";
    private static final String ERROR_STRING = "Trouble validating VAT code using http://isvat.appspot.com/. VAT code was: {}.";
    static final int THREAD_TIMEOUT_MS = 5000;
    private static final int THREAD_POOL_SIZE = 20;
    private static final int WEB_CONNECT_TIMEOUT_MS = 1000;
    private static final int WEB_READ_TIMEOUT_MS = 1000;
    private String vatCode;

    private static final String SOAP_NAME_SPACE="urn";
    private static final String SOAP_NAME_SPACE_ADDRESS = "urn:ec.europa.eu:taxud:vies:services:checkVat:types";
    private static final String SOAP_SERVICE_URI = "http://ec.europa.eu/taxation_customs/vies/services/checkVatService";

    /*
        The jersey client is threadsafe as long as you don't attempt to change the configuration after creation.
        Also, using getEntity (get(String)), it will close its own connections/resources/streams.
     */
    static {
        ClientConfig cc = new DefaultClientConfig();
        Map<String, Object> props = cc.getProperties();
        props.put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, WEB_CONNECT_TIMEOUT_MS);
        props.put(ClientConfig.PROPERTY_READ_TIMEOUT, WEB_READ_TIMEOUT_MS);
        client = Client.create(cc);
        webResource = client.resource(VALIDATION_URL);
    }

    public VATWebValidator(String vatCode) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(HYSTRIX_COMMAND_GROUP))
            .andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(THREAD_TIMEOUT_MS)
            ).andThreadPoolPropertiesDefaults(
                HystrixThreadPoolProperties.Setter().withCoreSize(THREAD_POOL_SIZE)
            )
        );
        this.vatCode = vatCode;
    }

    @Override
    protected Boolean run() throws Exception {
        return webValidate();
    }

    @Override
    protected Boolean getFallback() {
        logger.debug(ERROR_STRING, vatCode);
        return Boolean.TRUE;
    }

    public boolean webValidate() throws Exception {
        String countryPrefix;
        String numbers;
        try {
            countryPrefix = vatCode.substring(0, 2);
            numbers = vatCode.substring(2, vatCode.length());
        } catch (Exception e) {
            return false;
        }
        return runValidation(countryPrefix, numbers);
    }

    private boolean runValidation(String countryPrefix, String numbers) throws Exception {
        String valid="false";
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        try {
            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(countryPrefix, numbers), SOAP_SERVICE_URI);
            valid=soapResponse.getSOAPBody().getChildNodes().item(0).getChildNodes().item(3).getFirstChild().getTextContent();
            soapConnection.close();
        }
        catch (Exception ex)
        {
            return false;
        }
        if (VALIDATION_OK_STRING.equals(valid)) {
            return true;
        } else {
            return false;
        }
    }

    // for injecting test client for unit tests
    public static void registerWebClient(Client webclient) {
        client = webclient;
        webResource = client.resource(VALIDATION_URL);
    }

    private static SOAPMessage createSOAPRequest (String countryPrefix, String numbers) throws Exception{
         /*
        Constructed SOAP Request Message:
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:ec.europa.eu:taxud:vies:services:checkVat:types">
            <soapenv:Header/>
            <soapenv:Body>
                <urn:checkVat>
                    <urn:countryCode>?</urn:countryCode>
                    <urn:vatNumber>?</urn:vatNumber>
                </urn:checkVat>
            </soapenv:Body>
        </soapenv:Envelope>

        Soap Response Message
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
            <soap:Body>
                <checkVatResponse xmlns="urn:ec.europa.eu:taxud:vies:services:checkVat:types">
                    <countryCode>ES</countryCode>
                    <vatNumber>A79187423</vatNumber>
                    <requestDate>2014-09-18+02:00</requestDate>
                    <valid>true</valid>
                    <name>---</name>
                    <address>---</address>
                </checkVatResponse>
            </soap:Body>
        </soap:Envelope>
         */
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(SOAP_NAME_SPACE, SOAP_NAME_SPACE_ADDRESS);
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement checkVatElement = soapBody.addChildElement("checkVat",SOAP_NAME_SPACE);
        SOAPElement countryCodeElement = checkVatElement.addChildElement("countryCode",SOAP_NAME_SPACE);
        countryCodeElement.addTextNode(countryPrefix);
        SOAPElement vatNumberElement = checkVatElement.addChildElement("vatNumber",SOAP_NAME_SPACE);
        vatNumberElement.addTextNode(numbers);
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", SOAP_SERVICE_URI );
        soapMessage.saveChanges();
        return soapMessage;

    }
}
