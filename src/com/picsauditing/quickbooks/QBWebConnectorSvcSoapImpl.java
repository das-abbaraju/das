/**
 * QBWebConnectorSvcSoapImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.picsauditing.quickbooks;

public class QBWebConnectorSvcSoapImpl implements com.picsauditing.quickbooks.QBWebConnectorSvcSoap{
    public java.lang.String[] authenticate(java.lang.String strUserName, java.lang.String strPassword) throws java.rmi.RemoteException {
        System.out.println("authenticating");
    	return new String[] {"this", "is", "a", "test"};
    }

    public java.lang.String sendRequestXML(java.lang.String ticket, java.lang.String strHCPResponse, java.lang.String strCompanyFileName, java.lang.String qbXMLCountry, int qbXMLMajorVers, int qbXMLMinorVers) throws java.rmi.RemoteException {
        return null;
    }

    public int receiveResponseXML(java.lang.String ticket, java.lang.String response, java.lang.String hresult, java.lang.String message) throws java.rmi.RemoteException {
        return -3;
    }

    public java.lang.String connectionError(java.lang.String ticket, java.lang.String hresult, java.lang.String message) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String getLastError(java.lang.String ticket) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String closeConnection(java.lang.String ticket) throws java.rmi.RemoteException {
        return null;
    }

}
