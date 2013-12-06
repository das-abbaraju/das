/**
 * SIWsOutputOfSIWsResultArrayOfCountry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.strikeiron.www.api;

public class SIWsOutputOfSIWsResultArrayOfCountry implements java.io.Serializable {
    private SIWsStatus serviceStatus;

    private SIWsResultArrayOfCountry serviceResult;

    public SIWsOutputOfSIWsResultArrayOfCountry() {
    }

    public SIWsOutputOfSIWsResultArrayOfCountry(
            SIWsStatus serviceStatus,
            SIWsResultArrayOfCountry serviceResult) {
        this.serviceStatus = serviceStatus;
        this.serviceResult = serviceResult;
    }


    /**
     * Gets the serviceStatus value for this SIWsOutputOfSIWsResultArrayOfCountry.
     *
     * @return serviceStatus
     */
    public SIWsStatus getServiceStatus() {
        return serviceStatus;
    }


    /**
     * Sets the serviceStatus value for this SIWsOutputOfSIWsResultArrayOfCountry.
     *
     * @param serviceStatus
     */
    public void setServiceStatus(SIWsStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }


    /**
     * Gets the serviceResult value for this SIWsOutputOfSIWsResultArrayOfCountry.
     *
     * @return serviceResult
     */
    public SIWsResultArrayOfCountry getServiceResult() {
        return serviceResult;
    }


    /**
     * Sets the serviceResult value for this SIWsOutputOfSIWsResultArrayOfCountry.
     *
     * @param serviceResult
     */
    public void setServiceResult(SIWsResultArrayOfCountry serviceResult) {
        this.serviceResult = serviceResult;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SIWsOutputOfSIWsResultArrayOfCountry)) return false;
        SIWsOutputOfSIWsResultArrayOfCountry other = (SIWsOutputOfSIWsResultArrayOfCountry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
                ((this.serviceStatus == null && other.getServiceStatus() == null) ||
                        (this.serviceStatus != null &&
                                this.serviceStatus.equals(other.getServiceStatus()))) &&
                ((this.serviceResult == null && other.getServiceResult() == null) ||
                        (this.serviceResult != null &&
                                this.serviceResult.equals(other.getServiceResult())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getServiceStatus() != null) {
            _hashCode += getServiceStatus().hashCode();
        }
        if (getServiceResult() != null) {
            _hashCode += getServiceResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(SIWsOutputOfSIWsResultArrayOfCountry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfCountry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "ServiceStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "ServiceResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsResultArrayOfCountry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.BeanSerializer(
                        _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.BeanDeserializer(
                        _javaType, _xmlType, typeDesc);
    }

}
