/**
 * QBWebConnectorSvcLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.picsauditing.quickbooks;

public class QBWebConnectorSvcLocator extends org.apache.axis.client.Service implements com.picsauditing.quickbooks.QBWebConnectorSvc {

    public QBWebConnectorSvcLocator() {
    }


    public QBWebConnectorSvcLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public QBWebConnectorSvcLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for QBWebConnectorSvcSoap
    private java.lang.String QBWebConnectorSvcSoap_address = "https://idn.vogelfam.net/QBMSDonorSample/QBWebConnectorSvc.asmx";

    public java.lang.String getQBWebConnectorSvcSoapAddress() {
        return QBWebConnectorSvcSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String QBWebConnectorSvcSoapWSDDServiceName = "QBWebConnectorSvcSoap";

    public java.lang.String getQBWebConnectorSvcSoapWSDDServiceName() {
        return QBWebConnectorSvcSoapWSDDServiceName;
    }

    public void setQBWebConnectorSvcSoapWSDDServiceName(java.lang.String name) {
        QBWebConnectorSvcSoapWSDDServiceName = name;
    }

    public com.picsauditing.quickbooks.QBWebConnectorSvcSoap getQBWebConnectorSvcSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(QBWebConnectorSvcSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getQBWebConnectorSvcSoap(endpoint);
    }

    public com.picsauditing.quickbooks.QBWebConnectorSvcSoap getQBWebConnectorSvcSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.picsauditing.quickbooks.QBWebConnectorSvcSoapStub _stub = new com.picsauditing.quickbooks.QBWebConnectorSvcSoapStub(portAddress, this);
            _stub.setPortName(getQBWebConnectorSvcSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setQBWebConnectorSvcSoapEndpointAddress(java.lang.String address) {
        QBWebConnectorSvcSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.picsauditing.quickbooks.QBWebConnectorSvcSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.picsauditing.quickbooks.QBWebConnectorSvcSoapStub _stub = new com.picsauditing.quickbooks.QBWebConnectorSvcSoapStub(new java.net.URL(QBWebConnectorSvcSoap_address), this);
                _stub.setPortName(getQBWebConnectorSvcSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("QBWebConnectorSvcSoap".equals(inputPortName)) {
            return getQBWebConnectorSvcSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://developer.intuit.com/", "QBWebConnectorSvc");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://developer.intuit.com/", "QBWebConnectorSvcSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("QBWebConnectorSvcSoap".equals(portName)) {
            setQBWebConnectorSvcSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
