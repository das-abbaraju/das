/**
 * SIWsResultArrayOfSIWsStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.strikeiron.www.api;

public class SIWsResultArrayOfSIWsStatus implements java.io.Serializable {
    private SIWsStatus[] results;

    public SIWsResultArrayOfSIWsStatus() {
    }

    public SIWsResultArrayOfSIWsStatus(
            SIWsStatus[] results) {
        this.results = results;
    }


    /**
     * Gets the results value for this SIWsResultArrayOfSIWsStatus.
     *
     * @return results
     */
    public SIWsStatus[] getResults() {
        return results;
    }


    /**
     * Sets the results value for this SIWsResultArrayOfSIWsStatus.
     *
     * @param results
     */
    public void setResults(SIWsStatus[] results) {
        this.results = results;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SIWsResultArrayOfSIWsStatus)) return false;
        SIWsResultArrayOfSIWsStatus other = (SIWsResultArrayOfSIWsStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
                ((this.results == null && other.getResults() == null) ||
                        (this.results != null &&
                                java.util.Arrays.equals(this.results, other.getResults())));
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
        if (getResults() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(getResults());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getResults(), i);
                if (obj != null &&
                        !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(SIWsResultArrayOfSIWsStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsResultArrayOfSIWsStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("results");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Results"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsStatus"));
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