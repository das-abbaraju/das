/**
 * BasicAddress.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.strikeiron.www.api;

public class BasicAddress implements java.io.Serializable {
    private String streetAddressLines;

    private String countrySpecificLocalityLine;

    private String country;

    public BasicAddress() {
    }

    public BasicAddress(
            String streetAddressLines,
            String countrySpecificLocalityLine,
            String country) {
        this.streetAddressLines = streetAddressLines;
        this.countrySpecificLocalityLine = countrySpecificLocalityLine;
        this.country = country;
    }


    /**
     * Gets the streetAddressLines value for this BasicAddress.
     *
     * @return streetAddressLines
     */
    public String getStreetAddressLines() {
        return streetAddressLines;
    }


    /**
     * Sets the streetAddressLines value for this BasicAddress.
     *
     * @param streetAddressLines
     */
    public void setStreetAddressLines(String streetAddressLines) {
        this.streetAddressLines = streetAddressLines;
    }


    /**
     * Gets the countrySpecificLocalityLine value for this BasicAddress.
     *
     * @return countrySpecificLocalityLine
     */
    public String getCountrySpecificLocalityLine() {
        return countrySpecificLocalityLine;
    }


    /**
     * Sets the countrySpecificLocalityLine value for this BasicAddress.
     *
     * @param countrySpecificLocalityLine
     */
    public void setCountrySpecificLocalityLine(String countrySpecificLocalityLine) {
        this.countrySpecificLocalityLine = countrySpecificLocalityLine;
    }


    /**
     * Gets the country value for this BasicAddress.
     *
     * @return country
     */
    public String getCountry() {
        return country;
    }


    /**
     * Sets the country value for this BasicAddress.
     *
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof BasicAddress)) return false;
        BasicAddress other = (BasicAddress) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
                ((this.streetAddressLines == null && other.getStreetAddressLines() == null) ||
                        (this.streetAddressLines != null &&
                                this.streetAddressLines.equals(other.getStreetAddressLines()))) &&
                ((this.countrySpecificLocalityLine == null && other.getCountrySpecificLocalityLine() == null) ||
                        (this.countrySpecificLocalityLine != null &&
                                this.countrySpecificLocalityLine.equals(other.getCountrySpecificLocalityLine()))) &&
                ((this.country == null && other.getCountry() == null) ||
                        (this.country != null &&
                                this.country.equals(other.getCountry())));
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
        if (getStreetAddressLines() != null) {
            _hashCode += getStreetAddressLines().hashCode();
        }
        if (getCountrySpecificLocalityLine() != null) {
            _hashCode += getCountrySpecificLocalityLine().hashCode();
        }
        if (getCountry() != null) {
            _hashCode += getCountry().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(BasicAddress.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicAddress"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("streetAddressLines");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "StreetAddressLines"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countrySpecificLocalityLine");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "CountrySpecificLocalityLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("country");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Country"));
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
