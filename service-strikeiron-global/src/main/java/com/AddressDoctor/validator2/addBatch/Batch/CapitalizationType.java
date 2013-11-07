/**
 * CapitalizationType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.AddressDoctor.validator2.addBatch.Batch;

public class CapitalizationType implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected CapitalizationType(String value) {
        _value_ = value;
        _table_.put(_value_, this);
    }

    public static final String _NO_CHANGE = "NO_CHANGE";
    public static final String _UPPER_CASE = "UPPER_CASE";
    public static final String _LOWER_CASE = "LOWER_CASE";
    public static final String _MIXED_CASE = "MIXED_CASE";
    public static final CapitalizationType NO_CHANGE = new CapitalizationType(_NO_CHANGE);
    public static final CapitalizationType UPPER_CASE = new CapitalizationType(_UPPER_CASE);
    public static final CapitalizationType LOWER_CASE = new CapitalizationType(_LOWER_CASE);
    public static final CapitalizationType MIXED_CASE = new CapitalizationType(_MIXED_CASE);

    public String getValue() {
        return _value_;
    }

    public static CapitalizationType fromValue(String value)
            throws IllegalArgumentException {
        CapitalizationType enumeration = (CapitalizationType)
                _table_.get(value);
        if (enumeration == null) throw new IllegalArgumentException();
        return enumeration;
    }

    public static CapitalizationType fromString(String value)
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
            new org.apache.axis.description.TypeDesc(CapitalizationType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CapitalizationType"));
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
