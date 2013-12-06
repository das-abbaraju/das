/**
 * GlobalAddressVerificationLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.strikeiron.www.api;

public class GlobalAddressVerificationLocator extends org.apache.axis.client.Service implements GlobalAddressVerification {

    public GlobalAddressVerificationLocator() {
    }


    public GlobalAddressVerificationLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GlobalAddressVerificationLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GlobalAddressVerificationSoap
    private String GlobalAddressVerificationSoap_address = "http://wsparam.strikeiron.com/StrikeIron/GlobalAddressVerification5/GlobalAddressVerification";

    public String getGlobalAddressVerificationSoapAddress() {
        return GlobalAddressVerificationSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private String GlobalAddressVerificationSoapWSDDServiceName = "GlobalAddressVerificationSoap";

    public String getGlobalAddressVerificationSoapWSDDServiceName() {
        return GlobalAddressVerificationSoapWSDDServiceName;
    }

    public void setGlobalAddressVerificationSoapWSDDServiceName(String name) {
        GlobalAddressVerificationSoapWSDDServiceName = name;
    }

    public GlobalAddressVerificationSoap getGlobalAddressVerificationSoap() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GlobalAddressVerificationSoap_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGlobalAddressVerificationSoap(endpoint);
    }

    public GlobalAddressVerificationSoap getGlobalAddressVerificationSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            GlobalAddressVerificationSoapStub _stub = new GlobalAddressVerificationSoapStub(portAddress, this);
            _stub.setPortName(getGlobalAddressVerificationSoapWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGlobalAddressVerificationSoapEndpointAddress(String address) {
        GlobalAddressVerificationSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (GlobalAddressVerificationSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                GlobalAddressVerificationSoapStub _stub = new GlobalAddressVerificationSoapStub(new java.net.URL(GlobalAddressVerificationSoap_address), this);
                _stub.setPortName(getGlobalAddressVerificationSoapWSDDServiceName());
                return _stub;
            }
        } catch (Throwable t) {
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
        String inputPortName = portName.getLocalPart();
        if ("GlobalAddressVerificationSoap".equals(inputPortName)) {
            return getGlobalAddressVerificationSoap();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.strikeiron.com", "GlobalAddressVerification");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.strikeiron.com", "GlobalAddressVerificationSoap"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

        if ("GlobalAddressVerificationSoap".equals(portName)) {
            setGlobalAddressVerificationSoapEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
