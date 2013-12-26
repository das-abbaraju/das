/**
 * MethodStatusRecord.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.strikeiron.www.api;

public class MethodStatusRecord extends SIWsResultArrayOfSIWsStatus implements java.io.Serializable {
    private String methodName;

    public MethodStatusRecord() {
    }

    public MethodStatusRecord(
            SIWsStatus[] results,
            String methodName) {
        super(
                results);
        this.methodName = methodName;
    }


    /**
     * Gets the methodName value for this MethodStatusRecord.
     *
     * @return methodName
     */
    public String getMethodName() {
        return methodName;
    }


    /**
     * Sets the methodName value for this MethodStatusRecord.
     *
     * @param methodName
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MethodStatusRecord)) return false;
        MethodStatusRecord other = (MethodStatusRecord) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) &&
                ((this.methodName == null && other.getMethodName() == null) ||
                        (this.methodName != null &&
                                this.methodName.equals(other.getMethodName())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getMethodName() != null) {
            _hashCode += getMethodName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(MethodStatusRecord.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "MethodStatusRecord"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("methodName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "MethodName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
