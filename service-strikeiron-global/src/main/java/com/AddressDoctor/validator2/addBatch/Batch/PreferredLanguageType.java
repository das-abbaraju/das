/**
 * PreferredLanguageType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.AddressDoctor.validator2.addBatch.Batch;

public class PreferredLanguageType implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected PreferredLanguageType(String value) {
        _value_ = value;
        _table_.put(_value_, this);
    }

    public static final String _PFL_DATABASE = "PFL_DATABASE";
    public static final String _PFL_LATIN_SCRIPT = "PFL_LATIN_SCRIPT";
    public static final String _PFL_PREFERRED_BY_POSTAL_ADMIN = "PFL_PREFERRED_BY_POSTAL_ADMIN";
    public static final String _PFL_PRIMARY = "PFL_PRIMARY";
    public static final String _PFL_SECONDARY = "PFL_SECONDARY";
    public static final String _PFL_LANG_EN = "PFL_LANG_EN";
    public static final PreferredLanguageType PFL_DATABASE = new PreferredLanguageType(_PFL_DATABASE);
    public static final PreferredLanguageType PFL_LATIN_SCRIPT = new PreferredLanguageType(_PFL_LATIN_SCRIPT);
    public static final PreferredLanguageType PFL_PREFERRED_BY_POSTAL_ADMIN = new PreferredLanguageType(_PFL_PREFERRED_BY_POSTAL_ADMIN);
    public static final PreferredLanguageType PFL_PRIMARY = new PreferredLanguageType(_PFL_PRIMARY);
    public static final PreferredLanguageType PFL_SECONDARY = new PreferredLanguageType(_PFL_SECONDARY);
    public static final PreferredLanguageType PFL_LANG_EN = new PreferredLanguageType(_PFL_LANG_EN);

    public String getValue() {
        return _value_;
    }

    public static PreferredLanguageType fromValue(String value)
            throws IllegalArgumentException {
        PreferredLanguageType enumeration = (PreferredLanguageType)
                _table_.get(value);
        if (enumeration == null) throw new IllegalArgumentException();
        return enumeration;
    }

    public static PreferredLanguageType fromString(String value)
            throws IllegalArgumentException {
        return fromValue(value);
    }

    public boolean equals(Object obj) {
        return (obj == this);
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        return _value_;
    }

    public Object readResolve() throws java.io.ObjectStreamException {
        return fromValue(_value_);
    }

    public static org.apache.axis.encoding.Serializer getSerializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.EnumSerializer(
                        _javaType, _xmlType);
    }

    public static org.apache.axis.encoding.Deserializer getDeserializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.EnumDeserializer(
                        _javaType, _xmlType);
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(PreferredLanguageType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "PreferredLanguageType"));
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
